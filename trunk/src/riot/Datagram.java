package riot;

public class Datagram {

	public static byte protocol = 48;
	
	String source;
	long value;
	
	public Datagram(long value, String source) {
		this.value = value;
		this.source = source;
	}
	
	public Datagram(byte command) {
		long value = 0;
		value |= ((long)protocol) << 7*8;
		value |= ((long)command) << 6*8;
		this.value = value;
	}
	
	public Datagram(byte command, byte[] data) {
		long value = 0;
		value |= ((long)protocol) << 7*8;
		value |= ((long)command) << 6*8;
		for(int i = 0; i < data.length && i <= 5; i++) {
			value |= (long)data[i] << (5-i)*8;
		}
		this.value = value;
	}
	
	public boolean isValid() {
		long temp = value;
		temp &= ((long)0xFF000000) << 4*8;
		if((byte)(temp >> 7*8) == protocol) {
			return true;
		}
		return false;
	}
	
	public long getValue() {
		return value;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getSource() {
		return source;
	}
	
	public byte getCommand() {
		long temp = value;
		temp &= ((long)0x00FF0000) << 4*8;
		return (byte)(temp >> 6*8);
	}
	
	public byte[] getData() {
		byte[] bytes = new byte[6];
		for(int i = 5; i >= 0; i--) {
			long temp = value;
			temp = temp >> i*8;
			temp &= 0x000000FF;
			bytes[5-i] = (byte) temp;
		}
		return bytes;
	}
	
	public static void main(String[] args) {
		System.out.println("Testing Datagram: ");
		
		// CHECK WITH THIS DATA
		byte command = 127;
		byte[] bytes = {12, 16, 25, 7, 4, 3};
		
		Datagram data = new Datagram(command, bytes);
		data.setSource("192.168.1.1");
		
		String sender = data.getSource();
		long value = data.getValue();
		byte auto = data.getCommand();
		byte[] sent = data.getData();
		
		System.out.println(sender + " sent command: " + auto);
		for(int i = 0; i < sent.length; i++) {
			System.out.print("." + sent[i]);
		}
		System.out.println(".");
		System.out.println("Overall value: " + value);
		if(data.isValid()) {
			System.out.println("Valid sender.");
		}
		else {
			System.out.println("Invalid sender!");
		}
	}
}
