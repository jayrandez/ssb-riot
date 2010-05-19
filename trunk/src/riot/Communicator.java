package riot;

import java.io.*;
import java.net.*;
import java.util.*;

public class Communicator {
	ArrayList<Socket> sockets;
	ServerSocket serverSocket;
	int readIndex;
	
	public Communicator() {
		sockets = new ArrayList<Socket>();
		readIndex = 0;
	}
	
	public boolean addOutgoing(String hostname) {
		try {
			Socket outgoingSocket = new Socket(hostname, Riot.Port);
			synchronized(sockets) {
				sockets.add(outgoingSocket);
			}
			return true;
		}
		catch(IOException ex) {
			System.out.println("Failed to add outgoing connection.");
			return false;
		}
	}
	
	public boolean acceptIncoming() {
		try {
			serverSocket = new ServerSocket(Riot.Port);
			new IncomingAcceptor().start();
			return true;
		}
		catch(IOException ex) {
			System.out.println("Failed to start accepting incoming connections.");
			return false;
		}
	}
	
	public boolean sendData(byte[] data) {
		boolean result = true;
		synchronized(sockets) {
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
					result = false;
				}
			}
		}
		return result;
	}
	
	public Message receiveData() {
		while(sockets.isEmpty());
		if(readIndex >= sockets.size()) {
			readIndex = 0;
		}
		Socket socket = sockets.get(readIndex);
		
		try {
			DataInputStream stream = new DataInputStream(socket.getInputStream());
			int size = stream.readInt();
			byte[] data = new byte[size];
			stream.read(data);
			readIndex++;
			return new Message(socket, data);
		}
		catch(IOException ex) {
			System.out.println("Failed to read data from socket.");
			synchronized(sockets) {
				sockets.remove(socket);
			}
			return receiveData();
		}
	}
	
	private class IncomingAcceptor extends Thread {
		public void run() {
			while(true) {
				try {
					Socket incoming = serverSocket.accept();
					synchronized(sockets) {
						System.out.println("Incoming connection : " + incoming.getLocalAddress().toString());
						sockets.add(incoming);
					}
				}
				catch (IOException e) {
				    System.out.println("Failed to accept an incoming socket.");
				}
			}
		}
	}
}
