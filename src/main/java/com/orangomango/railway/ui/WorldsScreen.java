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
import java.io.*;

import com.orangomango.railway.MainApplication;

public class WorldsScreen{
	private int width, height, fps;
	private double scale;
	private List<UiButton> buttons = new ArrayList<>();
	private UiSlider slider;
	private double scrollY;
	private Map<Integer, String> titles = new HashMap<>();
	private Image background = new Image(getClass().getResourceAsStream("/images/background.png"));
	private static final Font FONT = Font.loadFont(HomeScreen.class.getResourceAsStream("/fonts/font.ttf"), 25);

	public WorldsScreen(int w, int h, int fps, double scale){
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

		this.slider = new UiSlider(gc, 100, 75, 950, 96, new Image(getClass().getResourceAsStream("/images/diff_easy.png")), new Image(getClass().getResourceAsStream("/images/diff_difficult.png")), v -> GameScreen.TRAIN_COOLDOWN = (int)(16400*(1-v)));
		GameScreen.TRAIN_COOLDOWN = 8200;

		Timeline loop = new Timeline(new KeyFrame(Duration.millis(1000.0/this.fps), e -> update(gc)));
		loop.setCycleCount(Animation.INDEFINITE);
		loop.play();

		canvas.setFocusTraversable(true);
		canvas.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ESCAPE){
				HomeScreen hs = new HomeScreen(this.width, this.height, this.fps, this.scale);
				MainApplication.stage.setScene(hs.getScene());
			}
		});

		canvas.setOnMousePressed(e -> {
			if (e.getButton() == MouseButton.PRIMARY){
				for (UiButton ub : this.buttons){
					ub.click(e.getX()/this.scale, (e.getY()-this.scrollY)/this.scale);
				}
			}
		});

		canvas.setOnMouseDragged(e -> {
			if (e.getButton() == MouseButton.PRIMARY){
				this.slider.drag(e.getX()/this.scale, (e.getY()-this.scrollY)/this.scale);
			}
		});

		canvas.setOnMouseMoved(e -> {
			for (UiButton ub : this.buttons){
				ub.hover(e.getX()/this.scale, (e.getY()-this.scrollY)/this.scale);
			}
		});

		final int levels = 4;
		canvas.setOnScroll(e -> {
			if (e.getDeltaY() > 0){
				if (this.scrollY > 0) return;
				this.scrollY += 35;
			} else if (e.getDeltaY() < 0){
				if (this.scrollY < -(levels/5*380-750+75)) return;
				this.scrollY -= 35;
			} 
		});

		for (int i = 0; i < levels; i++){
			final int levelNumber = i+1;
			UiButton mapButton = new UiButton(gc, 110+(i%5)*200, 300+250*(i/5), 128, 128, new Image(getClass().getResourceAsStream("/images/button_play.png")), () -> play(levelNumber, loop));
			this.buttons.add(mapButton);
			titles.put(levelNumber, getTitle(levelNumber));
		}

		Scene scene = new Scene(pane, this.width, this.height);
		scene.setFill(Color.BLACK);
		return scene;
	}

	private String getTitle(int n){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/worlds/world"+n+".wld")));
			String title = reader.readLine();
			reader.close();
			return title;
		} catch (IOException ex){
			ex.printStackTrace();
			return null;
		}
	}

	private void play(int n, Timeline loop){
		GameScreen gs = new GameScreen("world"+n+".wld", this.width, this.height, this.fps, this.scale);
		loop.stop();
		MainApplication.stage.setScene(gs.getScene());
	}

	private void update(GraphicsContext gc){
		gc.clearRect(0, 0, this.width, this.height);

		gc.save();
		gc.translate(0, this.scrollY);
		gc.scale(this.scale, this.scale);
		gc.drawImage(this.background, 0, -this.scrollY, 1150, 750); // Background is fixed

		gc.setFont(FONT);
		gc.setFill(Color.BLUE);
		gc.setTextAlign(TextAlignment.CENTER);
		for (int i = 0; i < this.buttons.size(); i++){
			UiButton ub = this.buttons.get(i);
			ub.render();
			gc.fillText(this.titles.get(i+1), ub.getX()+64, ub.getY()+165);
		}

		this.slider.render();

		gc.setFill(Color.BLACK);
		gc.setFont(FONT);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.fillText("Press ESCAPE to go back", 1150/2, 50);
		gc.restore();
	}
}