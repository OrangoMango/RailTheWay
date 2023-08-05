package com.orangomango.railway;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import com.orangomango.railway.ui.HomeScreen;

public class MainApplication extends Application{
	private static final int WIDTH = 1150;
	private static final int HEIGHT = 750;
	private static final double SCALE = 1;
	private static final int FPS = 40;
	public static Stage stage;
	private static final Media BACKGROUND_MUSIC = new Media(MainApplication.class.getResource("/audio/background.mp3").toExternalForm());
	
	@Override
	public void start(Stage stage){
		MainApplication.stage = stage;
		stage.setTitle("RAIL-the-WAY v1.0");

		MediaPlayer music = new MediaPlayer(BACKGROUND_MUSIC);
		music.setCycleCount(MediaPlayer.INDEFINITE);
		music.play();
		
		HomeScreen gs = new HomeScreen(WIDTH, HEIGHT, FPS, SCALE);

		stage.setScene(gs.getScene());
		stage.setResizable(false);
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
		stage.show();
	}
	
	public static void main(String[] args){
		launch(args);
	}
}
