package riot;

import java.net.*;

/**
 * A message transmitted over the TCP connection
 */
public class Message {
	public Socket sender;
	public byte[] data;
	
	public Message(Socket sender, byte[] data) {
		this.sender = sender;
		this.data = data;
	}
}
