package launcher;

public class Main {

	public static void main(String[] args) {
		System.out.println("[Mouskill launcher] application launch (coucou)");
		
		GameLauncher launcher = GameLauncher.getInstance();
		launcher.updateGame();
		launcher.launchGame();
	}
}
