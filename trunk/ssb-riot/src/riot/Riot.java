package riot;

import jgame.platform.JGEngine;

public class Riot extends JGEngine {
	private static final long serialVersionUID = -6417663999978098545L;

	public static void main(String[] args) {
		DatagramReceiver recv = new DatagramReceiver(48123);
		recv.bindReceiver();
		DatagramSender send = new DatagramSender("localhost", 48123);
	}

	public void initCanvas() {
		
	}
	
	public void initGame() {
		
	}
}
