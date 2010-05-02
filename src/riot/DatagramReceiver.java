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

public abstract class DatagramReceiver implements IoHandler {
	
	private NioDatagramAcceptor acceptor;
	private int port;
	
	public DatagramReceiver(int port) {
		this.port = port;
		this.acceptor = new NioDatagramAcceptor();
		
		acceptor.setHandler(this);
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
			dispatch(new Datagram(current, session.getRemoteAddress().toString()));
		}
	}

	// THIS FUNCTION MUST BE **FAST** (Faster execution than 3 Millis)
	public abstract void dispatch(Datagram datagram);
	
	public void sessionClosed(IoSession session) throws Exception {}
	public void sessionCreated(IoSession session) throws Exception {}
	public void exceptionCaught(IoSession arg0, Throwable arg1) throws Exception {}
	public void messageSent(IoSession arg0, Object arg1) throws Exception {}
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {}
	public void sessionOpened(IoSession arg0) throws Exception {}
}
