package riot;

import java.io.*;
import java.net.*;
import java.util.*;

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
	
	public void addOutgoing(String hostname) {
		try {
			Socket outgoingSocket = new Socket(hostname, Riot.Port);
			outgoingSocket.setTcpNoDelay(true);
			outgoingSocket.setReceiveBufferSize(100);
			outgoingSocket.setSendBufferSize(100);
			System.out.println(outgoingSocket.getReceiveBufferSize());
			sockets.add(outgoingSocket);
		}
		catch(IOException ex) {
			System.out.println("Failed to add outgoing connection.");
		}
	}
	
	public void acceptIncoming() {
		try {
			serverSocket = new ServerSocket(Riot.Port);
			new IncomingAcceptor().start();
		}
		catch(IOException ex) {
			System.out.println("Failed to start accepting incoming connections.");
		}
	}
	
	public void sendData(byte[] data) {
		for(int i = 0; i < sockets.size(); i++) {
			Socket socket = sockets.get(i);
			try {
				DataOutputStream stream = new DataOutputStream(socket.getOutputStream());
				stream.writeInt(data.length);
				stream.write(data);
			}
			catch(IOException ex) {
				System.out.println("Failed to write data to a socket." + socket.getLocalAddress().toString());
				sockets.remove(socket);
				i--;
			}
		}
	}
	
	public Message receiveData() {
		while(sockets.isEmpty());
		if(readIndex >= sockets.size())
			readIndex = 0;
		Socket socket = sockets.get(readIndex++);
		
		try {
			DataInputStream stream = new DataInputStream(socket.getInputStream());
			if(stream.available() == 0 && sendKeepAlives == true) {
				byte[] data = {Riot.KeepAlive};
				return new Message(socket, data);
			}
			byte[] data = new byte[stream.readInt()];
			stream.read(data);
			return new Message(socket, data);
		}
		catch(IOException ex) {
			System.out.println("Failed to read data from socket.");
			sockets.remove(socket);
			byte[] data = {Riot.Disconnect};
			return new Message(socket, data);
		}
	}
	
	private class IncomingAcceptor extends Thread {
		public void run() {
			while(true) {
				try {
					Socket incoming = serverSocket.accept();
					incoming.setTcpNoDelay(true);
					incoming.setReceiveBufferSize(100);
					incoming.setSendBufferSize(100);
					System.out.println("Incoming connection : " + incoming.getLocalAddress().toString());
					sockets.add(incoming);
				}
				catch (IOException e) {
				    System.out.println("Failed to accept an incoming socket.");
				}
			}
		}
	}
}
