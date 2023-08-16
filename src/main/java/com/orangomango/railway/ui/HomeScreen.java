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
	private double scale;
	private List<UiButton> buttons = new ArrayList<>();
	private Image background = new Image(getClass().getResourceAsStream("/images/background.png"));
	private Image rails = new Image(getClass().getResourceAsStream("/images/rails.png"));
	private Image title = new Image(getClass().getResourceAsStream("/images/title.png"));
	private static final Font FONT = Font.loadFont(HomeScreen.class.getResourceAsStream("/fonts/font.ttf"), 25);

	public HomeScreen(int w, int h, int fps, double scale){
		this.width = w;
		this.height = h;
		this.fps = fps;
		this.scale = scale;
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
			Random random = new Random(); // "world"+(random.nextInt(3)+1)+".wld"
			GameScreen gs = new GameScreen("tempworld.wld", this.width, this.height, this.fps, this.scale);
			loop.stop();
			MainApplication.stage.setScene(gs.getScene());
		});
		UiButton creditsButton = new UiButton(gc, 500, 400, 128, 128, new Image(getClass().getResourceAsStream("/images/button_credits.png")), () -> {
			WorldsScreen ws = new WorldsScreen(this.width, this.height, this.fps, this.scale);
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
					ub.click(e.getX()/this.scale, e.getY()/this.scale);
				}
			}
		});

		Scene scene = new Scene(pane, this.width, this.height);
		return scene;
	}

	private void update(GraphicsContext gc){
		gc.clearRect(0, 0, this.width, this.height);
		gc.drawImage(this.background, 0, 0, this.width, this.height);

		gc.save();
		gc.scale(this.scale, this.scale);

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