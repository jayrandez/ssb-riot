package riot;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The class responsible for every facet of networking in SSB
 * It handles both input and output. If the server feature is enabled it automatically
 * adds all connections to its senders list. Destinations can also be added manually
 * which generally the client will do to connect to a server.
 */
public class Communicator {
	ArrayList<Socket> sockets;
	ServerSocket serverSocket;
	int readIndex;
	boolean sendKeepAlives;
	
	public Communicator(boolean sendKeepAlives) {
		sockets = new ArrayList<Socket>();
		readIndex = 0;
		this.sendKeepAlives = sendKeepAlives;
	}
	
	/**
	 * Creates a socket and add it to the destinations list
	 */
	public void addOutgoing(String hostname) {
		try {
			Socket outgoingSocket = new Socket(hostname, Riot.Port);
			outgoingSocket.setTcpNoDelay(true);
			outgoingSocket.setReceiveBufferSize(300);
			outgoingSocket.setSendBufferSize(300);
			sockets.add(outgoingSocket);
		}
		catch(IOException ex) {
			System.out.println("Failed to add outgoing connection.");
		}
	}
	
	/**
	 * Allow this communicator to function as a server and add connections to destination list
	 */
	public void acceptIncoming() {
		try {
			serverSocket = new ServerSocket(Riot.Port);
			new IncomingAcceptor().start();
		}
		catch(IOException ex) {
			System.out.println("Failed to start accepting incoming connections.");
		}
	}
	
	/**
	 * Sends the data in a byte array to all destinations
	 */
	public void sendData(byte[] data) {
		for(int i = 0; i < sockets.size(); i++) {
			Socket socket = sockets.get(i);
			try {
				DataOutputStream stream = new DataOutputStream(socket.getOutputStream());
				stream.writeInt(data.length);
				stream.write(data);
			}
			catch(IOException ex) {
				System.out.println("Failed to write data to a socket: " + socket.getLocalAddress().toString());
				try{socket.close();}
				catch(IOException e) {}
			}
		}
	}
	
	/**
	 * Attempts to receive data from a socket in the destinations list
	 * It will try the next socket upon each call.
	 */
	public Message receiveData() {
		/* Wait for a socket to become available and increment it so a new one will
		 * be read on the next call.
		 */
		while(sockets.isEmpty());
		if(readIndex >= sockets.size())
			readIndex = 0;
		Socket socket = sockets.get(readIndex++);
		
		try {
			DataInputStream stream = new DataInputStream(socket.getInputStream());
			/* If this stream isn't ready to write return a palceholder message. */
			if(stream.available() == 0 && sendKeepAlives == true) {
				byte[] data = {Riot.KeepAlive};
				return new Message(socket, data);
			}
			/* Get the size of the message to follow */
			byte[] data = new byte[stream.readInt()];
			/* Fill the data buffer with the sent message. */
			stream.read(data);
			return new Message(socket, data);
		}
		catch(IOException ex) {
			System.out.println("Failed to read data from socket: " + socket.getLocalAddress().toString());
			sockets.remove(socket);
			byte[] data = {Riot.Disconnect};
			return new Message(socket, data);
		}
	}
	
	/**
	 * The thread which accepts incoming connections and adds them to the destination list
	 */
	private class IncomingAcceptor extends Thread {
		public void run() {
			while(true) {
				try {
					Socket incoming = serverSocket.accept();
					incoming.setTcpNoDelay(true);
					incoming.setReceiveBufferSize(300);
					incoming.setSendBufferSize(300);
					System.out.println("Incoming connection: " + incoming.getLocalAddress().toString());
					sockets.add(incoming);
				}
				catch (IOException e) {
				    System.out.println("Failed to accept an incoming socket.");
				}
			}
		}
	}
}
