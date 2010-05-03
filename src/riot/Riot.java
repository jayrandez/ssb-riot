package riot;

public class Riot {
	public static void main(String[] args) {
		ClientWindow window = new ClientWindow();
		ClientNetwork network = new ClientNetwork();
		GameEngine engine = new GameEngine(window, network);
		
		engine.gameLoop();
		
		window.dispose();
		network.suspend();
		System.exit(0);
	}
}
