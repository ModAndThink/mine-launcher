package launcher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Path;

import com.sun.management.OperatingSystemMXBean;

import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.Step;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application {
	private static OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
		     java.lang.management.ManagementFactory.getOperatingSystemMXBean();
	private static int minRamAmout = 2048;
	private static int maxRamAmout = (int) (os.getTotalMemorySize()/(1024 * 1024));
	private static boolean isLaunching = false;
	private static Saver saver;
	
	Slider ramSlider = new Slider(minRamAmout,maxRamAmout,minRamAmout);
	TextField ramTextField = new TextField();
	TextField userTextField = new TextField();
	GridPane downloadGrid = new GridPane();
	ProgressBar downloadBar = new ProgressBar();
	Label downloadLabel = new Label();
	

	@Override
    public void start(Stage primaryStage) {
		if (!isServerUp("mouskill.playit.plus",25565,100)) {
			Alert alert = new Alert(AlertType.ERROR,"Serveur inactif, contactez-moi en cas de problème.");
			alert.show();
			return;
		}
		
		// We setup the main windows
        primaryStage.setTitle("Mouskill Launcher");
        primaryStage.setResizable(false);
        primaryStage.setHeight(450);
        primaryStage.setWidth(800);
        primaryStage.show();
        
        // Here we create the scene and grid where every element while be on
        ColumnConstraints colLabel = new ColumnConstraints();
        colLabel.setHgrow(Priority.NEVER);
        ColumnConstraints colInput = new ColumnConstraints();
        colInput.setPercentWidth(60);
        ColumnConstraints colBtn = new ColumnConstraints();
        colBtn.setHgrow(Priority.ALWAYS);
        
        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.NEVER);
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.NEVER);
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BOTTOM_CENTER);
        grid.setHgap(10);
        grid.setVgap(20);
        grid.setPadding(new Insets(10,10,10,10));
        grid.getColumnConstraints().addAll(colLabel, colInput, colBtn);
        grid.getRowConstraints().addAll(row1, row2);
        
        Scene scene = new Scene(grid, 800, 450);
        scene.setFill(Color.CYAN);
        primaryStage.setScene(scene);
        
        // User name selection
        Label userTextLabel = new Label();
        userTextLabel.setText("Nom : ");
        grid.add(userTextLabel, 0, 1);
        
        if (saver.get("player_name") != null) userTextField.setText(saver.get("player_name"));
        grid.add(userTextField, 1, 1);
        
        // Ram selection
        int savedRamAmout = minRamAmout;
        if (saver.get("selected_ram") != null) {
        	try {
        		savedRamAmout = Integer.parseInt(saver.get("selected_ram"));
    			
    			if (savedRamAmout < minRamAmout) savedRamAmout = minRamAmout;
    			else if (savedRamAmout > maxRamAmout) savedRamAmout = maxRamAmout;
    		}
    		catch (NumberFormatException e) {
    			
    		}
        }
        
        GridPane ramGrid = new GridPane();
        ramGrid.setAlignment(Pos.CENTER);
        ramGrid.setHgap(10);
        ramGrid.setVgap(20);
        ramGrid.setPadding(new Insets(25,25,25,25));
        
        ColumnConstraints colRamSlider = new ColumnConstraints();
        colRamSlider.setPercentWidth(75);
        ColumnConstraints colRamText = new ColumnConstraints();
        colRamText.setPercentWidth(25);
        
        ramGrid.getColumnConstraints().addAll(colRamSlider, colRamText);
        
        Label ramLabel = new Label();
        ramLabel.setText("RAM allouée (mo) :");
        grid.add(ramLabel, 0, 0);
        
        ramSlider.setShowTickLabels(true);
        ramSlider.setShowTickMarks(true);
        ramSlider.setMajorTickUnit(512);
        ramSlider.setBlockIncrement(128);
        ramSlider.setValue(savedRamAmout);
        ramSlider.setOnMouseReleased((MouseEvent event) -> {
        	ramTextField.setText(Integer.toString((int)ramSlider.getValue()));
        });
        ramGrid.add(ramSlider, 0, 0);
        
        ramTextField.setText(Integer.toString(savedRamAmout));
        ramTextField.setOnKeyReleased((KeyEvent event) -> {
        	if (event.getCode() == KeyCode.ENTER) {
        		try {
        			int ramAmout = Integer.parseInt(ramTextField.getText());
        			
        			if (ramAmout < minRamAmout) ramAmout = minRamAmout;
        			else if (ramAmout > maxRamAmout) ramAmout = maxRamAmout;
        			
        			ramSlider.setValue(ramAmout);
        			ramTextField.setText(Integer.toString(ramAmout));
        		}
        		catch (NumberFormatException e) {
        			
        		}
        	}
        });
        
        ramGrid.add(ramTextField, 1, 0);
        
        grid.add(ramGrid, 1, 0);
        
        // Play button
        Button playButton = new Button();
        playButton.setText("LANCER");
        playButton.setMaxSize(Double.MAX_VALUE, 80);
        playButton.setDefaultButton(true);
        playButton.setOnMouseClicked((MouseEvent event) -> {
        	if (isLaunching) return;
        	new Thread(this::startGame).start();
        });
        
        StackPane buttonPane = new StackPane(playButton);
        buttonPane.setAlignment(Pos.CENTER);

        grid.add(buttonPane, 2, 0, 1, 3);
        
        // Progress bar
        GridPane.setHgrow(downloadGrid, Priority.ALWAYS);
        GridPane.setFillWidth(downloadGrid, true);
        
        ColumnConstraints downloadCol1 = new ColumnConstraints();
        downloadCol1.setPercentWidth(75);
        ColumnConstraints downloadCol2 = new ColumnConstraints();
        downloadCol2.setPercentWidth(25);
        downloadGrid.getColumnConstraints().addAll(downloadCol1, downloadCol2);
        
        downloadBar.setMaxWidth(Double.MAX_VALUE);
        downloadGrid.add(downloadBar, 0, 0);
        
        downloadLabel.setText("");
        downloadGrid.add(downloadLabel, 1, 0);
        
        grid.add(downloadGrid, 0, 2, 4, 1);
        downloadGrid.setVisible(false);
        downloadGrid.setManaged(false);
    }
	
	public void startGame() {
		if (userTextField.getText().length() == 0) return;
		isLaunching = true;
		
		saver.set("selected_ram", Integer.toString((int)ramSlider.getValue()));
		saver.set("player_name", userTextField.getText());
		
		downloadGrid.setVisible(true);
		downloadGrid.setManaged(true);
		
		GameLauncher launcher = GameLauncher.getInstance();
		launcher.updateGame(callback);
		
		Platform.runLater(() -> {
			((Stage) (userTextField.getScene().getWindow())).close();
		});
		
		launcher.launchGame(userTextField.getText(), (int)ramSlider.getValue());
	}
	
	IProgressCallback callback = new IProgressCallback() {
		private String currentStep = "";
		
		@Override
        public void step(Step step) {
			currentStep = step.name();
        }
		
		@Override
		public void update(DownloadList.DownloadInfo info) {
			
			Platform.runLater(() -> {
				downloadBar.setProgress((double)info.getDownloadedBytes() / (double)info.getTotalToDownloadBytes());
				downloadLabel.setText(currentStep);
			});
		}
		
		@Override
		public void onFileDownloaded(Path path) {
			System.out.println("Downloading file : "+path.getFileName());
		}
		
	};
	
	public static boolean isServerUp(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
	
	public static void main(String[] args) {
		GameLauncher launcher = GameLauncher.getInstance();
		saver = new Saver(launcher.getLaunchDir().resolve("config.properties"));
		saver.load();
		
		launch(args);
        
    }

}
