package riot;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class Communicator implements IoHandler {

	ArrayList<IoSession> receivers;
	HashMap<IoSession, byte[]> senders;
	IoAcceptor acceptor;
	Receiver callback;
	 
	public Communicator() {
		receivers = new ArrayList<IoSession>();
		senders = new HashMap<IoSession, byte[]>();
		
		acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("logger", new LoggingFilter() );
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));
        acceptor.setHandler(this);
        
        try {acceptor.bind(new InetSocketAddress(48123));}
        catch(IOException e) {}
	}
	
	public void setCallback(Receiver callback) {
		this.callback = callback;
	}
	
	public void addDestination(String address) {
		IoConnector connector = new NioSocketConnector();
		connector.setHandler(this);
        ConnectFuture connFuture = connector.connect(new InetSocketAddress(address, 48123));
        connFuture.addListener(new IoFutureListener() {
			public void operationComplete(IoFuture future) {
				ConnectFuture connFuture = (ConnectFuture)future;
		        if(connFuture.isConnected() ){
		        	synchronized(receivers) {
		        		receivers.add(future.getSession());
		        	}
		        }
			}
        });
	}
	
	public void sendData(byte[] dataStream) {
		synchronized(receivers) {
			for(IoSession session: receivers) {
				IoBuffer buffer = IoBuffer.allocate(dataStream.length);
				buffer.put(dataStream);
				session.write(buffer);
			}
		}
	}
	
	public void sessionCreated(IoSession session) throws Exception {
		synchronized(senders) {
			senders.put(session, "");
		}
	}
	
	public void messageReceived(IoSession session, Object message) throws Exception {
		synchronized(senders) {
			IoBuffer buffer = (IoBuffer)message;
			byte[] currentData = senders.get(session);
			byte[] addedData = buffer.array();
			byte[] combinedData = new byte[currentData.length + addedData.length];
			System.arraycopy(currentData, 0, combinedData, 0, currentData.length);
			System.arraycopy(addedData, 0, combinedData, currentData.length, addedData.length);
			currentData = combinedData;
			
			while(currentData.length - 1 >= currentData[0]) {
				int messageLength = (int)currentData[0];
				byte[] callbackMessage = new byte[messageLength];
				System.arraycopy(currentData, 1, callbackMessage, 0, messageLength);
				byte[] newData = new byte[currentData.length - (messageLength + 1)];
				System.arraycopy()
			}
			
			if(callbackMessage != null) {
				if(callback != null) {
					callback.receiveMessage(session, callbackMessage);
				}
			}
			
			senders.put(session, currentData);
		}
	}
	
	public void exceptionCaught(IoSession arg0, Throwable arg1) throws Exception {}
	public void messageSent(IoSession arg0, Object arg1) throws Exception {}
	public void sessionClosed(IoSession arg0) throws Exception {}
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {}
	public void sessionOpened(IoSession arg0) throws Exception {}
}
