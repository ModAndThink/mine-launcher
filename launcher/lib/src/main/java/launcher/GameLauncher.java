package launcher;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.json.Mod;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.flowupdater.versions.neoforge.NeoForgeVersion;
import fr.flowarg.flowupdater.versions.neoforge.NeoForgeVersionBuilder;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import javafx.application.Platform;

public class GameLauncher {
	// SETTING
	private static String gameVersion = "1.21.1";
	private static String modLoaderVersion = "21.1.249";
	private static Path launcherDir = GameDirGenerator.createGameDir("Mouskill", true);
	
	private static GameLauncher singleInstance;
	
	// We put the constructor in private
	private GameLauncher() {}
	
	// Use it to get instance
	public static GameLauncher getInstance() {
		if (singleInstance == null) singleInstance = new GameLauncher();
		
		return singleInstance;
	}
	
	public void updateGame(IProgressCallback callback) {
		VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                .withName(gameVersion)
                .build();
		
		// We get mods
		List<Mod> mods = Mod.getModsFromJson("http://modsdownload.mouskill.playit.plus/mods/index.json");
		
		NeoForgeVersion neoForge = new NeoForgeVersionBuilder()
				.withNeoForgeVersion(modLoaderVersion)
				.withMods(mods)
				.build();
		
        FlowUpdater flowUpdater = new FlowUpdater.FlowUpdaterBuilder()
                .withVanillaVersion(vanillaVersion)
                .withModLoaderVersion(neoForge)
                .withProgressCallback(callback)
                .build();
        
        try {
        	System.out.println("[Mouskill launcher] minecraft launcher updating");
			flowUpdater.update(launcherDir);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void launchGame(String playername) {
		AuthInfos authInfos = new AuthInfos(playername, UUID.randomUUID().toString(), UUID.randomUUID().toString());
		
		try {
			// Initialize launcher
			NoFramework noFramework = new NoFramework(
					launcherDir,
					authInfos,
					GameFolder.FLOW_UPDATER
			);
			
			System.out.println("[Mouskill launcher] starting game");
			// Run it
			noFramework.launch(gameVersion,modLoaderVersion, NoFramework.ModLoader.NEO_FORGE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void launchGame(String playername, int ramAmout) {
		AuthInfos authInfos = new AuthInfos(playername, UUID.randomUUID().toString(), UUID.randomUUID().toString());
		
		try {
			// Initialize launcher
			NoFramework noFramework = new NoFramework(
					launcherDir,
					authInfos,
					GameFolder.FLOW_UPDATER
			);
			
			noFramework.getAdditionalVmArgs().add("-Xmx"+ramAmout+"M");
			noFramework.getAdditionalArgs().add("--quickPlayMultiplayer");
			noFramework.getAdditionalArgs().add("mouskill.playit.plus");
			
			System.out.println("[Mouskill launcher] starting game");
			// Run it
			Process p = noFramework.launch(gameVersion,modLoaderVersion, NoFramework.ModLoader.NEO_FORGE);
			
			Platform.runLater(() -> {
				try {
					p.waitFor();
					Platform.exit();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Path getLaunchDir() {
		return launcherDir;
	}
}
