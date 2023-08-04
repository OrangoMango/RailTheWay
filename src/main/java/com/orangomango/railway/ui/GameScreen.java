package com.orangomango.railway.ui;

import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.*;

import com.orangomango.railway.Util;
import com.orangomango.railway.game.*;

public class GameScreen{
	private int width, height, fps;
	private double translateX, translateY;
	private World world;
	private List<Train> trains = new ArrayList<>();
	private Timeline loop;
	private volatile Tile warningTile;
	private volatile boolean warningBlink;
	private volatile boolean gameRunning = true;

	public static int score;
	private static final Font FONT = Font.loadFont(GameScreen.class.getResourceAsStream("/fonts/font.ttf"), 25);
	private static final Image WARNING_IMAGE = new Image(GameScreen.class.getResourceAsStream("/images/warning.png"));

	public GameScreen(int w, int h, int fps){
		this.width = w;
		this.height = h;
		this.fps = fps;
		score = 0;
	}

	public Scene getScene(){
		StackPane pane = new StackPane();
		Canvas canvas = new Canvas(this.width, this.height);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		pane.getChildren().add(canvas);

		this.world = new World(getClass().getResourceAsStream("/world1.wld"));
		this.translateX = (this.width-250-this.world.getWidth()*Tile.WIDTH)/2;
		this.translateY = (this.height-this.world.getHeight()*Tile.HEIGHT)/2;

		Thread creator = new Thread(() -> {
			while (this.gameRunning){
				try {
					this.warningTile = Util.getRandomStart(this.world);
					Thread.sleep(5000);
					createRandomTrain(this.warningTile);
				} catch (InterruptedException ex){
					ex.printStackTrace();
				}
			}
		});
		creator.setDaemon(true);
		creator.start();

		Thread warning = new Thread(() -> {
			while (this.gameRunning){
				try {
					this.warningBlink = !this.warningBlink;
					Thread.sleep(500);
				} catch (InterruptedException ex){
					ex.printStackTrace();
				}
			}
		});
		warning.setDaemon(true);
		warning.start();

		canvas.setOnMousePressed(e -> {
			final double ex = e.getX()-this.translateX;
			final double ey = e.getY()-this.translateY;
			if (e.getButton() == MouseButton.PRIMARY){
				Tile tile = this.world.getTileAt((int)(ex/Tile.WIDTH), (int)(ey/Tile.HEIGHT));
				if (tile instanceof Track && ((Track)tile).getConnectionAmount() == 3){
					((Track)tile).changeDirection();
				} else if (tile instanceof Stoplight){
					((Stoplight)tile).toggle(this.trains);
				}
			}
		});

		this.loop = new Timeline(new KeyFrame(Duration.millis(1000.0/this.fps), e -> update(gc)));
		this.loop.setCycleCount(Animation.INDEFINITE);
		this.loop.play();

		Scene scene = new Scene(pane, this.width, this.height);
		return scene;
	}

	private void createRandomTrain(Tile tile){
		Random random = new Random();
		byte dir = 0;
		if (tile.getX() == 0) dir = (byte)4;
		else if (tile.getY() == 0) dir = (byte)2;
		else if (tile.getX() == this.world.getWidth()-1) dir = (byte)1;
		else if (tile.getY() == this.world.getHeight()-1) dir = (byte)8;
		int offX = 0, offY = 0;
		if (dir == 8){
			offX = 0;
			offY = 1;
		} else if (dir == 4){
			offX = -1;
			offY = 0;
		} else if (dir == 2){
			offX = 0;
			offY = -1;
		} else if (dir == 1){
			offX = 1;
			offY = 0;
		}
		Train train = new Train(this.world, random.nextInt(5)+2, tile.getX()*Tile.WIDTH+Tile.WIDTH/2, tile.getY()*Tile.HEIGHT+Tile.HEIGHT/2, offX, offY, dir, TrainType.values()[random.nextInt(TrainType.values().length)]);
		this.trains.add(train);
	}

	private void update(GraphicsContext gc){
		gc.clearRect(0, 0, this.width, this.height);
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, this.width, this.height);

		gc.save();
		gc.translate(this.translateX, this.translateY);
		this.world.render(gc);
		for (int i = 0; i < this.trains.size(); i++){
			Train train = this.trains.get(i);
			train.update();
			train.render(gc);
		}

		if (this.warningBlink){
			gc.drawImage(WARNING_IMAGE, this.warningTile.getX()*Tile.WIDTH, this.warningTile.getY()*Tile.HEIGHT, 32, 32);
		}

		gc.restore();

		if (this.score < 0){
			this.score = 0; // Score min is 0
		}
		gc.setFill(Color.WHITE);
		gc.setFont(FONT);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.fillText("Score: "+this.score, this.width-150, 150);

		try {
			List<Tile> tiles = this.trains.stream().flatMap(train -> train.getTrain().stream()).map(c -> c.getCurrentTile()).filter(c -> c != null).toList();
			Map<Tile, Integer> occurences = new HashMap<>();
			for (Tile t : tiles){
				occurences.put(t, occurences.getOrDefault(t, 0)+1);
			}
			for (Map.Entry<Tile, Integer> entry : occurences.entrySet()){
				if (entry.getValue() > 1){
					// GAME OVER
					this.loop.stop();
					this.gameRunning = false;
					return;
				}
			}
		} catch (ConcurrentModificationException ex){
			// Ignore if a train is added in the same time
			System.out.println("Skipping collision check");
		}

		// Remove the trains outside the screen
		for (int i = 0; i < this.trains.size(); i++){
			Train t = this.trains.get(i);
			if (!t.exists()){
				this.trains.remove(i);
				i--;
			}
		}
	}
}