package riot;

public class Riot {
	
	public static void debug(String message) {
		System.out.println(message);
	}
	
	public static void main(String[] args) {
		SpriteManager manager = new SpriteManager("sheets");
		/*ClientWindow window = new ClientWindow();
		ClientNetwork network = new ClientNetwork();
		
		ConnectionWindow view = new ConnectionWindow(window);
		
		view.gameLoop();
		
		window.dispose();
		network.suspend();
		System.exit(0);*/
	}
}
