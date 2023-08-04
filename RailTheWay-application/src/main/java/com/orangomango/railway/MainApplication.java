package com.orangomango.railway;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import com.orangomango.railway.ui.HomeScreen;

import dev.webfx.platform.resource.Resource;

public class MainApplication extends Application{
	private static final int WIDTH = 1150;
	private static final int HEIGHT = 750;
	private static final int FPS = 40;
	public static Stage stage;
	private static final Media BACKGROUND_MUSIC = new Media(Resource.toUrl("/audio/background.mp3", MainApplication.class));
	
	@Override
	public void start(Stage stage){
		MainApplication.stage = stage;
		stage.setTitle("RAIL-the-WAY v1.0");

		MediaPlayer music = new MediaPlayer(BACKGROUND_MUSIC);
		music.setCycleCount(MediaPlayer.INDEFINITE);
		music.play();
		
		HomeScreen gs = new HomeScreen(WIDTH, HEIGHT, FPS);

		stage.setScene(gs.getScene());
		stage.setResizable(false);
		stage.show();
	}
	
	public static void main(String[] args){
		launch(args);
	}
}
