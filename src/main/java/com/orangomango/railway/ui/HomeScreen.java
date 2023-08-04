package com.orangomango.railway.ui;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

import java.util.*;

import com.orangomango.railway.MainApplication;

public class HomeScreen{
	private int width, height, fps;
	private List<UiButton> buttons = new ArrayList<>();
	private Image background = new Image(getClass().getResourceAsStream("/images/background.png"));
	private Image rails = new Image(getClass().getResourceAsStream("/images/rails.png"));
	private Image title = new Image(getClass().getResourceAsStream("/images/title.png"));
	private static final Font FONT = Font.loadFont(HomeScreen.class.getResourceAsStream("/fonts/font.ttf"), 25);

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

		UiButton playButton = new UiButton(gc, 300, 400, 128, 128, new Image(getClass().getResourceAsStream("/images/button_play.png")), () -> {
			GameScreen gs = new GameScreen("world1.wld", this.width, this.height, this.fps);
			loop.stop();
			MainApplication.stage.setScene(gs.getScene());
		});
		UiButton creditsButton = new UiButton(gc, 500, 400, 128, 128, new Image(getClass().getResourceAsStream("/images/button_credits.png")), () -> {
			WorldsScreen ws = new WorldsScreen(this.width, this.height, this.fps);
			loop.stop();
			MainApplication.stage.setScene(ws.getScene());
		});
		UiButton quitButton = new UiButton(gc, 700, 400, 128, 128, new Image(getClass().getResourceAsStream("/images/button_quit.png")), () -> {
			// Quit game
			System.exit(0);
		});

		this.buttons.add(playButton);
		this.buttons.add(creditsButton);
		this.buttons.add(quitButton);

		canvas.setOnMousePressed(e -> {
			if (e.getButton() == MouseButton.PRIMARY){
				for (UiButton ub : this.buttons){
					ub.click(e.getX(), e.getY());
				}
			}
		});

		Scene scene = new Scene(pane, this.width, this.height);
		return scene;
	}

	private void update(GraphicsContext gc){
		gc.clearRect(0, 0, this.width, this.height);
		gc.drawImage(this.background, 0, 0, this.width, this.height);

		gc.drawImage(this.title, 190, 130);

		for (UiButton ub : this.buttons){
			ub.render();
		}

		gc.drawImage(this.rails, 300, 525);

		gc.setFill(Color.BLACK);
		gc.setFont(FONT);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.fillText("RAIL-the-WAY by OrangoMango, v1.0, Indie Dev Game Jam 2023 (Made in 72h). Music from freesound.org", this.width/2, this.height-50);
	}
}