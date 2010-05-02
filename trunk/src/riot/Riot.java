package riot;

public class Riot {
	public static void main(String[] args) {
		GameWindow window = new GameWindow();
		GameNetwork network = new GameNetwork();
		GameEngine engine = new GameEngine(window, network);
		
		engine.gameLoop();
		
		window.dispose();
		network.suspend();
		System.exit(0);
	}
}
