package com.orangomango.railway.ui;

import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.input.MouseButton;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.WritableImage;
import javafx.scene.media.AudioClip;

import java.util.*;

import com.orangomango.railway.Util;
import com.orangomango.railway.AssetLoader;
import com.orangomango.railway.MainApplication;
import com.orangomango.railway.game.*;

public class GameScreen{
	private int fps;
	private double translateX, translateY;
	private World world;
	private List<Train> trains = new ArrayList<>();
	private List<Car> cars = new ArrayList<>();
	private Timeline loop;
	private WritableImage canvasImage;
	private long startTime, playedTime;
	private List<Tile> warningTiles = new ArrayList<>();
	private volatile boolean warningBlink;
	private volatile boolean gameRunning = true;
	private String worldName;
	private boolean gameoverSkip = false;
	private Map<KeyCode, Boolean> keys = new HashMap<>();

	public static int score, arrivals, misses;
	public static InformationText infoText;
	public static int TRAIN_COOLDOWN = 8200;
	public static final Font FONT = Font.loadFont(GameScreen.class.getResourceAsStream("/fonts/font.ttf"), 25);
	private static final Font FONT_45 = Font.loadFont(GameScreen.class.getResourceAsStream("/fonts/font.ttf"), 45);
	private static final Image WARNING_IMAGE = AssetLoader.getInstance().getImage("warning.png");
	private static final AudioClip GAME_OVER_SOUND = AssetLoader.getInstance().getAudio("gameover.wav");
	private static final AudioClip WARNING_SOUND = AssetLoader.getInstance().getAudio("warning.wav");

	public GameScreen(String worldName, int fps){
		this.worldName = worldName;
		this.fps = fps;
		this.startTime = System.currentTimeMillis();
		score = 0;
		arrivals = 0;
		misses = 0;
		infoText = null;
	}

	public Scene getScene(){
		StackPane pane = new StackPane();
		Canvas canvas = new Canvas(Util.GAME_WIDTH, Util.GAME_HEIGHT);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		pane.getChildren().add(canvas);

		this.world = new World(getClass().getResourceAsStream("/worlds/"+this.worldName), this.cars);
		this.translateX = (1150-250-this.world.getWidth()*Tile.WIDTH)/2;
		this.translateY = (750-this.world.getHeight()*Tile.HEIGHT)/2;

		Thread creator = new Thread(() -> {
			while (this.gameRunning){
				try {
					int n = Math.random() < 0.6 ? 1 : (Math.random() < 0.8 ? 2 : 3);
					if (score < 1500) n = 1;
					WARNING_SOUND.play();
					Tile[] warningTile = Util.getRandomStart(this.world, n);
					n = warningTile.length; // fix
					for (int i = 0; i < n; i++){
						this.warningTiles.add(warningTile[i]);
					}
					Thread.sleep(1000);
					WARNING_SOUND.play();
					int cooldown = TRAIN_COOLDOWN-50*(int)Math.round(score/225.0);
					Thread.sleep(Math.max(cooldown, 1000));
					for (int i = 0; i < n; i++){
						createRandomTrain(warningTile[i]);
						this.warningTiles.remove(warningTile[i]);
					}
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

		Thread jollyColors = new Thread(() -> {
			Random random = new Random();
			while (this.gameRunning){
				try {
					// Set jolly color
					for (Train t : this.trains){
						if (t.getTrain().get(0).isJolly()){
							int color = random.nextInt(TrainType.values().length);
							for (Carriage c : t.getTrain()){
								c.setJollyColor(color);
							}
						}
					}
					Thread.sleep(300);
				} catch (InterruptedException ex){
					ex.printStackTrace();
				}
			}
		});
		jollyColors.setDaemon(true);
		jollyColors.start();

		canvas.setOnMousePressed(e -> {
			final double ex = e.getX()/Util.SCALE-this.translateX;
			final double ey = e.getY()/Util.SCALE-this.translateY;
			if (e.getButton() == MouseButton.PRIMARY){
				if (this.gameRunning){
					Tile tile = this.world.getTileAt((int)(ex/Tile.WIDTH), (int)(ey/Tile.HEIGHT));
					if (tile instanceof Rail && ((Rail)tile).getConnectionAmount() == 3){
						((Rail)tile).changeDirection();
					} else if (tile instanceof Stoplight){
						((Stoplight)tile).toggle(this.trains);
					} else if (tile instanceof CrossingGate){
						((CrossingGate)tile).toggle(this.cars);
					}
				} else if (this.gameoverSkip){
					this.loop.stop();
					HomeScreen hs = new HomeScreen(this.fps);
					MainApplication.stage.setScene(hs.getScene());
				}
			}
		});

		canvas.setFocusTraversable(true);
		canvas.setOnKeyPressed(e -> this.keys.put(e.getCode(), true));
		canvas.setOnKeyReleased(e -> this.keys.put(e.getCode(), false));

		this.loop = new Timeline(new KeyFrame(Duration.millis(1000.0/this.fps), e -> update(gc)));
		this.loop.setCycleCount(Animation.INDEFINITE);
		this.loop.play();

		Scene scene = new Scene(pane, Util.WINDOW_WIDTH, Util.WINDOW_HEIGHT);
		scene.setFill(Color.BLACK);
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
		Train train = new Train(this.world, random.nextInt(5)+2, tile.getX()*Tile.WIDTH+Tile.WIDTH/2, tile.getY()*Tile.HEIGHT+Tile.HEIGHT/2, offX, offY, dir, this.world.getRandomTrains().get(random.nextInt(this.world.getRandomTrains().size())));
		this.trains.add(train);
	}

	private void update(GraphicsContext gc){
		gc.clearRect(0, 0, Util.GAME_WIDTH, Util.GAME_HEIGHT);
		gc.setFill(Color.web("#616161"));
		gc.fillRect(0, 0, Util.GAME_WIDTH, Util.GAME_HEIGHT);

		long diff = System.currentTimeMillis()-this.startTime;

		if (!this.gameRunning){
			gc.drawImage(this.canvasImage, 0, 0, Util.GAME_WIDTH, Util.GAME_HEIGHT);
			gc.save();
			gc.setGlobalAlpha(0.6);
			gc.setFill(Color.BLACK);
			gc.fillRect(0, 0, Util.GAME_WIDTH, Util.GAME_HEIGHT);
			gc.restore();
			gc.save();
			gc.scale(Util.SCALE, Util.SCALE);
			gc.setFill(Color.WHITE);
			gc.setFont(FONT_45);
			gc.setTextAlign(TextAlignment.CENTER);
			String formatTime = formatTime((int)this.playedTime);
			gc.fillText("GAME OVER\nYou scored "+score+",\n"+arrivals+" trains passed and\n"+misses+" trains missed the station.\nYou were able to control your\ntrains for just "+formatTime+" :(\n\nClick on the screen to exit", 1150/2-100, 750/2-175);
			gc.restore();
			return;
		}

		gc.save();
		gc.scale(Util.SCALE, Util.SCALE);
		gc.translate(this.translateX, this.translateY);

		this.world.render(gc);
		for (int i = 0; i < this.trains.size(); i++){
			Train train = this.trains.get(i);
			train.update();
			train.render(gc);
		}

		for (Car car : this.cars){
			car.update(this.cars);
			car.render(gc);
		}

		if (this.warningBlink){
			for (int i = 0; i < this.warningTiles.size(); i++){
				Tile wt = this.warningTiles.get(i);
				gc.drawImage(WARNING_IMAGE, wt.getX()*Tile.WIDTH, wt.getY()*Tile.HEIGHT, 32, 32);
			}
		}

		gc.translate(-this.translateX, -this.translateY);

		if (this.score < 0){
			this.score = 0; // Score min is 0
		}
		gc.setFill(Color.WHITE);
		gc.setFont(FONT);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.fillText("JOLLY: "+this.world.isJollyAvailable()+"\n\nScore: "+score+"\nArrivals: "+arrivals+"\nMisses: "+misses+"\nSurvived: "+formatTime((int)diff), 1150-125, 750/2-100);

		if (this.infoText != null){
			this.infoText.render(gc);
			if (!this.infoText.exists()){
				this.infoText = null;
			}
		}

		gc.restore();

		try {
			List<Tile> tiles = new ArrayList<Tile>(this.trains.stream().flatMap(train -> train.getTrain().stream()).map(c -> c.getCurrentTile()).filter(c -> c != null).toList());
			tiles.addAll(this.cars.stream().map(c -> c.getCurrentTile()).filter(c -> c != null).distinct().toList());
			Map<Tile, Integer> occurences = new HashMap<>();
			for (Tile t : tiles){
				occurences.put(t, occurences.getOrDefault(t, 0)+1);
			}
			for (Map.Entry<Tile, Integer> entry : occurences.entrySet()){
				if (entry.getValue() > 1){
					// GAME OVER
					GAME_OVER_SOUND.play();
					this.playedTime = diff;
					this.canvasImage = gc.getCanvas().snapshot(null, new WritableImage((int)Math.ceil(Util.GAME_WIDTH), (int)Math.ceil(Util.GAME_HEIGHT)));
					this.gameRunning = false;
					Util.schedule(() -> this.gameoverSkip = true, 1500);
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

		// Quit the game
		if (this.keys.getOrDefault(KeyCode.ESCAPE, false)){
			this.loop.stop();
			this.gameRunning = false;
			HomeScreen hs = new HomeScreen(this.fps);
			MainApplication.stage.setScene(hs.getScene());
			this.keys.put(KeyCode.ESCAPE, false);
		}
	}

	private String formatTime(int time){
		int seconds = time/1000 % 60;
		int minutes = time/60000;
		return minutes+":"+(seconds < 10 ? "0"+seconds : seconds);
	}
}