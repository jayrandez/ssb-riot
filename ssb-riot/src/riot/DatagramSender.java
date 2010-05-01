package riot;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

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
	private String hostname;
	private int port;
	private IoSession session;
	
	public DatagramSender(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		
		this.connector = new NioDatagramConnector();
		connector.setHandler(this);
		ConnectFuture connFuture = connector.connect(new InetSocketAddress(hostname, port));
		
		connFuture.awaitUninterruptibly();
		
		connFuture.addListener(new IoFutureListener() {
			public void operationComplete(IoFuture future) {
				ConnectFuture connFuture = (ConnectFuture)future;
				if(connFuture.isConnected()) {
					session = future.getSession();
					for(int i = 0; i <= 10000; i++) {
						for(int j = 0; j < 20; j++) {
							IoBuffer buffer = IoBuffer.allocate(8);
				            buffer.putLong(i);
				            buffer.flip();
				            session.write(buffer);
						}
					}
				}
				else {
					
				}
			}
		});
	}

	public void messageSent(IoSession session, Object message) throws Exception {
		SocketAddress remoteAddress = session.getRemoteAddress();
		//System.out.println("Sender: Sent message.");
	}
	
	public void exceptionCaught(IoSession arg0, Throwable arg1) throws Exception {}
	public void messageReceived(IoSession arg0, Object arg1) throws Exception {}
	public void sessionClosed(IoSession arg0) throws Exception {}
	public void sessionCreated(IoSession arg0) throws Exception {}
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {}
	public void sessionOpened(IoSession arg0) throws Exception {}
}
