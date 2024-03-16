package com.orangomango.railway;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import com.orangomango.railway.ui.HomeScreen;

public class MainApplication extends Application{
	private static final int FPS = 40;
	public static Stage stage;

	@Override
	public void start(Stage stage) throws Exception{
		// Load the AssetLoader
		Class.forName("com.orangomango.railway.AssetLoader");

		AndroidUtil.prepareApp();
		MainApplication.stage = stage;
		stage.setTitle("RAIL-the-WAY v2.0");
		AndroidUtil.launchFullscreen();
		AndroidUtil.playSound("background.mp3", true);
		
		HomeScreen gs = new HomeScreen(FPS);
		Scene scene = new Scene(gs.getScene(), Util.WINDOW_WIDTH, Util.WINDOW_HEIGHT);
		scene.setFill(Color.BLACK);

		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}
	
	public static void main(String[] args){
		launch(args);
	}
}
