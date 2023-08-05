package com.orangomango.railway.ui;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.KeyCode;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

import java.util.*;

import com.orangomango.railway.MainApplication;

public class WorldsScreen{
	private int width, height, fps;
	private List<UiButton> buttons = new ArrayList<>();
	private Image background = new Image(getClass().getResourceAsStream("/images/background.png"));
	private static final Font FONT = Font.loadFont(HomeScreen.class.getResourceAsStream("/fonts/font.ttf"), 25);

	public WorldsScreen(int w, int h, int fps){
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

		canvas.setFocusTraversable(true);
		canvas.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ESCAPE){
				HomeScreen hs = new HomeScreen(this.width, this.height, this.fps);
				MainApplication.stage.setScene(hs.getScene());
			}
		});

		canvas.setOnMousePressed(e -> {
			if (e.getButton() == MouseButton.PRIMARY){
				for (UiButton ub : this.buttons){
					ub.click(e.getX(), e.getY());
				}
			}
		});

		canvas.setOnMouseMoved(e -> {
			for (UiButton ub : this.buttons){
				ub.hover(e.getX(), e.getY());
			}
		});

		UiButton map1 = new UiButton(gc, 310, 200, 128, 128, new Image(getClass().getResourceAsStream("/images/button_play.png")), () -> play(1, loop));
		UiButton map2 = new UiButton(gc, 510, 200, 128, 128, new Image(getClass().getResourceAsStream("/images/button_play.png")), () -> play(2, loop));
		UiButton map3 = new UiButton(gc, 710, 200, 128, 128, new Image(getClass().getResourceAsStream("/images/button_play.png")), () -> play(3, loop));
		UiButton map4 = new UiButton(gc, 410, 400, 128, 128, new Image(getClass().getResourceAsStream("/images/button_play.png")), () -> play(4, loop));
		UiButton map5 = new UiButton(gc, 610, 400, 128, 128, new Image(getClass().getResourceAsStream("/images/button_play.png")), () -> play(5, loop));

		this.buttons.add(map1);
		this.buttons.add(map2);
		this.buttons.add(map3);
		this.buttons.add(map4);
		this.buttons.add(map5);

		Scene scene = new Scene(pane, this.width, this.height);
		scene.setFill(Color.BLACK);
		return scene;
	}

	private void play(int n, Timeline loop){
		GameScreen gs = new GameScreen("world"+n+".wld", this.width, this.height, this.fps);
		loop.stop();
		MainApplication.stage.setScene(gs.getScene());
	}

	private void update(GraphicsContext gc){
		gc.clearRect(0, 0, this.width, this.height);
		gc.drawImage(this.background, 0, 0, this.width, this.height);

		for (UiButton ub : this.buttons){
			ub.render();
		}

		gc.setFill(Color.BLACK);
		gc.setFont(FONT);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.fillText("Press ESCAPE to go back", this.width/2, this.height-50);
	}
}