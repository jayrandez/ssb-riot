package riot;

import java.net.*;

public class Message {
	public Socket sender;
	public byte[] data;
	
	public Message(Socket sender, byte[] data) {
		this.sender = sender;
		this.data = data;
	}
}
