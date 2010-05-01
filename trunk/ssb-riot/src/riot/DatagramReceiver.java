package riot;
//48123
import java.io.*;
import java.net.*;
import org.apache.mina.core.filterchain.*;
import org.apache.mina.core.service.*;
import org.apache.mina.core.session.*;
import org.apache.mina.core.buffer.*;
import org.apache.mina.filter.logging.*;
import org.apache.mina.transport.socket.*;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

public class DatagramReceiver implements IoHandler {
	
	private NioDatagramAcceptor acceptor;
	private int port;
	private long last;
	
	public DatagramReceiver(int port) {
		this.port = port;
		this.acceptor = new NioDatagramAcceptor();
		this.last = 0;
		
		acceptor.setHandler(this);
		//DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
		//chain.addLast("logger", new LoggingFilter());
		DatagramSessionConfig dcfg = acceptor.getSessionConfig();
		dcfg.setReuseAddress(true);
	}
	
	public boolean bindReceiver() {
		try {
			acceptor.bind(new InetSocketAddress(port));
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void messageReceived(IoSession session, Object message) throws Exception {
		if(message instanceof IoBuffer) {
			IoBuffer buffer = (IoBuffer)message;
			long current = buffer.getLong();
			if(current > last + 1) {
				System.out.println("Failure to receive packet #" + (last+1));
			}
			last = current;
			if(last == 10000) {
				System.out.println("Done.");
				System.exit(0);
			}
		}
	}

	public void sessionClosed(IoSession session) throws Exception {
		SocketAddress remoteAddress = session.getRemoteAddress();
		System.out.println("Receiver: Session closed.");
	}

	public void sessionCreated(IoSession session) throws Exception {
		SocketAddress remoteAddress = session.getRemoteAddress();
		System.out.println("Receiver: Session created.");
	}
	
	public void exceptionCaught(IoSession arg0, Throwable arg1) throws Exception {}
	public void messageSent(IoSession arg0, Object arg1) throws Exception {}
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {}
	public void sessionOpened(IoSession arg0) throws Exception {}
}
