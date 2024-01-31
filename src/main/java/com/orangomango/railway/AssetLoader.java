package com.orangomango.railway;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.AudioClip;

import java.util.HashMap;

public class AssetLoader{
	private HashMap<String, Image> images = new HashMap<>();
	private HashMap<String, AudioClip> audios = new HashMap<>();
	private HashMap<String, Media> musics = new HashMap<>();

	private static AssetLoader instance;

	static {
		AssetLoader loader = new AssetLoader(); // Initialize the asset loader
	}

	public AssetLoader(){
		instance = this;
		loadImages("background.png", "button_credits.png", "button_play.png", "button_quit.png", "car.png", "cargo.png", "carriage.png", "crossingGate.png", "diff_difficult.png", "diff_easy.png", "grass.png", "icon.png", "rail.png", "road.png", "slider.png", "station.png", "stoplight.png", "title.png", "warning.png");
		loadAudios("gameover.wav", "rail_change.wav", "select.wav", "station.wav", "station_missed.wav", "stoplight.wav", "warning.wav");
		loadMusics("background.mp3");
	}

	public Image getImage(String name){
		return this.images.getOrDefault(name, null);
	}

	public AudioClip getAudio(String name){
		return this.audios.getOrDefault(name, null);
	}

	public Media getMedia(String name){
		return this.musics.getOrDefault(name, null);
	}

	public static AssetLoader getInstance(){
		return instance;
	}

	private void loadImages(String... names){
		for (String name : names){
			this.images.put(name, new Image(getClass().getResourceAsStream("/images/"+name)));
		}
	}

	private void loadAudios(String... names){
		for (String name : names){
			this.audios.put(name, new AudioClip(getClass().getResource("/audio/"+name).toExternalForm()));
		}
	}

	private void loadMusics(String... names){
		for (String name : names){
			this.musics.put(name, new Media(getClass().getResource("/audio/"+name).toExternalForm()));
		}
	}
}