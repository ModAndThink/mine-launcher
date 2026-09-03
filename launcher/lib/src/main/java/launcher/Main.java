package launcher;

import java.text.DecimalFormat;
import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.Step;

public class Main {

	public static void main(String[] args) {
		System.out.println("[Mouskill launcher] application launch (coucou)");
		
		App.main(args);
		
		IProgressCallback callback = new IProgressCallback() {
			private final DecimalFormat decimalFormat = new DecimalFormat("#.#");
			private String currentStep = "";
			
			@Override
            public void step(Step step) {
				currentStep = step.name();
            }
			
			@Override
			public void update(DownloadList.DownloadInfo info) {
				System.out.println(decimalFormat.format(info.getDownloadedBytes() * 100.d / info.getTotalToDownloadBytes()) + "%");
				System.out.println(currentStep);
			}
			
		};
		
		GameLauncher launcher = GameLauncher.getInstance();
		launcher.updateGame(callback);
	}
}
