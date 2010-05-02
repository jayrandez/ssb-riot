package riot;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;

public class DatagramSender implements IoHandler {

	private NioDatagramConnector connector;
	private int port;
	private ArrayList<IoSession> sessions;
	private ArrayList<Datagram> queue;
	
	public DatagramSender(int port) {
		this.port = port;
		
		sessions = new ArrayList<IoSession>();
		queue = new ArrayList<Datagram>();
		connector = new NioDatagramConnector();
		
		connector.setHandler(this);
		
		new Thread() {
			public void run() {
				sendDatagrams();
			}
		}.start();
	}
	
	public void newSession(String hostname) {
		ConnectFuture connFuture = connector.connect(new InetSocketAddress(hostname, port));
		connFuture.awaitUninterruptibly();
		connFuture.addListener(new IoFutureListener() {
			public void operationComplete(IoFuture future) {
				ConnectFuture connFuture = (ConnectFuture)future;
				if(connFuture.isConnected()) {
					IoSession session = future.getSession();
					synchronized(sessions) {
						sessions.add(session);
					}
				}
			}
		});
	}
	
	public void endSession(String hostname) {
		synchronized(sessions) {
			for(int i = 0; i < sessions.size(); i++) {
				if(sessions.get(i).getRemoteAddress().equals(hostname)) {
					sessions.remove(i);
					break;
				}
			}
		}
	}
	
	public void sessionClosed(IoSession session) throws Exception {
		// if session is still needed, reopen it
	}	
	
	public void pushDatagram(Datagram datagram) {
		synchronized(queue) {
			queue.add(datagram);
		}
	}
	
	public void sendDatagrams() {
		while(true) {
			Datagram current;
			synchronized(queue) {
				if(queue.size() > 0) {
					current = queue.get(0);
					queue.remove(0);
				}
				else
					current = null;
			}
			if(current == null)
				current = new Datagram(Messages.KeepAlive);
			synchronized(sessions) {
				for(IoSession session: sessions) {
					IoBuffer buffer = IoBuffer.allocate(8);
		            buffer.putLong(current.getValue());
		            buffer.flip();
		            session.write(buffer);
				}
			}
			try {
				Thread.sleep(20);
			}
			catch (InterruptedException e) {}
		}
	}

	public void messageSent(IoSession session, Object message) throws Exception {}
	public void exceptionCaught(IoSession arg0, Throwable arg1) throws Exception {}
	public void messageReceived(IoSession arg0, Object arg1) throws Exception {}
	public void sessionCreated(IoSession arg0) throws Exception {}
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {}
	public void sessionOpened(IoSession arg0) throws Exception {}
}
