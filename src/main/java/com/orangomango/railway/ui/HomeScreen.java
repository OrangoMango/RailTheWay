package com.orangomango.railway.ui;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.animation.*;
import javafx.util.Duration;

import com.orangomango.railway.MainApplication;

public class HomeScreen{
	private int width, height, fps;

	public HomeScreen(int w, int h, int fps){
		this.width = w;
		this.height = h;
		this.fps = fps;
	}

	public Scene getScene(){
		StackPane pane = new StackPane();
		Canvas canvas = new Canvas(this.width, this.height);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		pane.getChildren().add(canvas);

		Timeline loop = new Timeline(new KeyFrame(Duration.millis(1000.0/this.fps), e -> update(gc)));
		loop.setCycleCount(Animation.INDEFINITE);
		loop.play();

		canvas.setOnMousePressed(e -> {
			GameScreen gs = new GameScreen(this.width, this.height, this.fps);
			loop.stop();
			MainApplication.stage.setScene(gs.getScene());
		});

		Scene scene = new Scene(pane, this.width, this.height);
		return scene;
	}

	private void update(GraphicsContext gc){

	}
}