package com.orangomango.railway;

import javafx.application.Application;
import javafx.stage.Stage;

import com.orangomango.railway.ui.HomeScreen;

public class MainApplication extends Application{
	private static final int WIDTH = 1150;
	private static final int HEIGHT = 750;
	private static final int FPS = 40;
	public static Stage stage;
	
	@Override
	public void start(Stage stage){
		MainApplication.stage = stage;
		stage.setTitle("Indie Dev Game Jam 1");
		
		HomeScreen gs = new HomeScreen(WIDTH, HEIGHT, FPS);

		stage.setScene(gs.getScene());
		stage.setResizable(false);
		stage.show();
	}
	
	public static void main(String[] args){
		launch(args);
	}
}
