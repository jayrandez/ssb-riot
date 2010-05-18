package riot;

import java.net.Socket;

public class Message {
	public Socket sender;
	public byte[] data;
	
	public Message(Socket sender, byte[] data) {
		this.sender = sender;
		this.data = data;
	}
}
