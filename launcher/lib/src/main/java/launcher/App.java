package launcher;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application {

	@Override
    public void start(Stage primaryStage) {
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
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BOTTOM_CENTER);
        grid.setHgap(10);
        grid.setVgap(20);
        grid.setPadding(new Insets(10,10,10,10));
        grid.getColumnConstraints().addAll(colLabel, colInput, colBtn);
        
        Scene scene = new Scene(grid, 800, 450);
        scene.setFill(Color.CYAN);
        primaryStage.setScene(scene);
        
        // User name selection
        Label userTextLabel = new Label();
        userTextLabel.setText("Nom : ");
        grid.add(userTextLabel, 0, 1);
        
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);
        
        // Ram selection
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
        ramLabel.setText("RAM allouée :");
        grid.add(ramLabel, 0, 0);
        
        Slider ramSlider = new Slider(1048,4000,1048);
        ramSlider.setShowTickLabels(true);
        ramSlider.setShowTickMarks(true);
        ramSlider.setMajorTickUnit(512);
        ramSlider.setBlockIncrement(128);
        ramGrid.add(ramSlider, 0, 0);
        
        TextField ramTextField = new TextField();
        ramTextField.setText("1048");
        ramGrid.add(ramTextField, 1, 0);
        
        grid.add(ramGrid, 1, 0);
        
        // Play button
        Button playButton = new Button();
        playButton.setText("LANCER");
        playButton.setMaxSize(Double.MAX_VALUE, 80);
        
        StackPane buttonPane = new StackPane(playButton);
        buttonPane.setAlignment(Pos.CENTER);

        grid.add(buttonPane, 2, 0, 1, 3);
    }
	
	public static void main(String[] args) {
        launch(args);
    }

}
