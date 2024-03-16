package com.orangomango.railway.ui;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.*;
import javafx.scene.image.Image;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

import java.util.*;

import com.orangomango.railway.MainApplication;
import com.orangomango.railway.Util;
import com.orangomango.railway.AssetLoader;

public class HomeScreen{
	private int fps;
	private List<UiButton> buttons = new ArrayList<>();
	private Image background = AssetLoader.getInstance().getImage("background.png");
	private Image rails = AssetLoader.getInstance().getImage("rails.png");
	private Image title = AssetLoader.getInstance().getImage("title.png");
	private static final Font FONT = Font.loadFont(HomeScreen.class.getResourceAsStream("/fonts/font.ttf"), 25);

	public HomeScreen(int fps){
		this.fps = fps;
	}

	public StackPane getScene(){
		StackPane pane = new StackPane();
		Canvas canvas = new Canvas(Util.GAME_WIDTH, Util.GAME_HEIGHT);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		pane.getChildren().add(canvas);

		Timeline loop = new Timeline(new KeyFrame(Duration.millis(1000.0/this.fps), e -> update(gc)));
		loop.setCycleCount(Animation.INDEFINITE);
		loop.play();

		UiButton playButton = new UiButton(gc, 300, 400, 128, 128, AssetLoader.getInstance().getImage("button_play.png"), () -> {
			Random random = new Random();
			GameScreen gs = new GameScreen("world"+(random.nextInt(9)+1)+".wld", this.fps);
			loop.stop();
			MainApplication.stage.getScene().setRoot(gs.getScene());
		});
		UiButton creditsButton = new UiButton(gc, 500, 400, 128, 128, AssetLoader.getInstance().getImage("button_credits.png"), () -> {
			WorldsScreen ws = new WorldsScreen(this.fps);
			loop.stop();
			MainApplication.stage.getScene().setRoot(ws.getScene());
		});
		UiButton quitButton = new UiButton(gc, 700, 400, 128, 128, AssetLoader.getInstance().getImage("button_quit.png"), () -> {
			// Quit game
			System.exit(0);
		});

		this.buttons.add(playButton);
		this.buttons.add(creditsButton);
		this.buttons.add(quitButton);

		canvas.setOnMousePressed(e -> {
			for (UiButton ub : this.buttons){
				ub.click(e.getX()/Util.SCALE, e.getY()/Util.SCALE);
			}
		});
		
		return pane;
	}

	private void update(GraphicsContext gc){
		gc.clearRect(0, 0, Util.GAME_WIDTH, Util.GAME_HEIGHT);
		gc.drawImage(this.background, 0, 0, Util.GAME_WIDTH, Util.GAME_HEIGHT);

		gc.save();
		gc.scale(Util.SCALE, Util.SCALE);

		gc.drawImage(this.title, 190, 130);
		gc.setFont(FONT);
		gc.setTextAlign(TextAlignment.CENTER);

		String[] labels = new String[]{"Random", "Select", "Quit"};
		for (int i = 0; i < this.buttons.size(); i++){
			UiButton ub = this.buttons.get(i);
			ub.render();
			gc.setFill(Color.BLUE);
			gc.fillText(labels[i], ub.getX()+64, ub.getY()-15);
		}

		gc.drawImage(this.rails, 300, 525);

		gc.setFill(Color.BLACK);
		gc.fillText("RAIL-the-WAY by OrangoMango, v2.0, Indie Dev Game Jam 2023 (Post-jam). Music from freesound.org", 1150/2, 750-50);

		gc.restore();
	}
}
