package Rechnungsschreiber;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

 
public class Main extends Application {

	private Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		mainWindow();
	}
	
	
	public void mainWindow() {
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("MainWindow.fxml"));
			AnchorPane pane = loader.load();
			
			primaryStage.setMinHeight(400.00);
			primaryStage.setMinWidth(500.00);
			primaryStage.setTitle("Rechnungsschreiber");
			
			MainWindowController controller = loader.getController();
			controller.setMain(this);
			
			Scene scene = new Scene(pane);
			
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		launch(args);
	}

}
