package com.orangomango.railway;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import com.orangomango.railway.ui.HomeScreen;

public class MainApplication extends Application{
	private static final int FPS = 40;
	private static final Media BACKGROUND_MUSIC = AssetLoader.getInstance().getMedia("background.mp3");

	public static Stage stage;

	@Override
	public void start(Stage stage){
		MainApplication.stage = stage;
		stage.setTitle("RAIL-the-WAY v2.0");

		MediaPlayer music = new MediaPlayer(BACKGROUND_MUSIC);
		music.setCycleCount(MediaPlayer.INDEFINITE);
		music.play();
		
		HomeScreen gs = new HomeScreen(FPS);

		stage.setScene(gs.getScene());
		stage.setResizable(false);
		stage.getIcons().add(AssetLoader.getInstance().getImage("icon.png"));
		stage.show();
	}
	
	public static void main(String[] args){
		launch(args);
	}
}
