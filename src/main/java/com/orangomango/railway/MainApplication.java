package com.orangomango.railway;

import javafx.application.Application;
import javafx.stage.Stage;

import com.orangomango.railway.ui.GameScreen;

public class MainApplication extends Application{
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 800;
	private static final int FPS = 40;
	
	@Override
	public void start(Stage stage){
		stage.setTitle("Indie Dev Game Jam 1");
		
		GameScreen gs = new GameScreen(WIDTH, HEIGHT, FPS);

		stage.setScene(gs.getScene());
		stage.show();
	}
	
	public static void main(String[] args){
		launch(args);
	}
}
