package com.orangomango.railway.ui;

import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.input.MouseButton;

import java.util.*;

import com.orangomango.railway.game.*;

public class GameScreen{
	private int width, height, fps;
	private World world;
	private List<Train> trains = new ArrayList<>();

	public GameScreen(int w, int h, int fps){
		this.width = w;
		this.height = h;
		this.fps = fps;
	}

	public Scene getScene(){
		StackPane pane = new StackPane();
		Canvas canvas = new Canvas(this.width, this.height);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		pane.getChildren().add(canvas);

		this.world = new World(getClass().getResourceAsStream("/world1.wld"));
		this.trains.add(new Train(world, 6, 2*Tile.WIDTH+Tile.WIDTH/2, 8*Tile.WIDTH+Tile.HEIGHT/2, -1, 0, (byte)4, Color.LIME));
		this.trains.add(new Train(world, 4, 11*Tile.WIDTH+Tile.WIDTH/2, 0*Tile.WIDTH+Tile.HEIGHT/2, 0, -1, (byte)2, Color.YELLOW));

		canvas.setOnMousePressed(e -> {
			if (e.getButton() == MouseButton.PRIMARY){
				Tile tile = this.world.getTileAt((int)(e.getX()/Tile.WIDTH), (int)(e.getY()/Tile.HEIGHT));
				if (tile instanceof Track && ((Track)tile).getConnectionAmount() == 3){
					((Track)tile).changeDirection();
				}
			}
		});

		Timeline loop = new Timeline(new KeyFrame(Duration.millis(1000.0/this.fps), e -> update(gc)));
		loop.setCycleCount(Animation.INDEFINITE);
		loop.play();

		Scene scene = new Scene(pane, this.width, this.height);
		return scene;
	}

	private void update(GraphicsContext gc){
		gc.clearRect(0, 0, this.width, this.height);
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, this.width, this.height);

		this.world.render(gc);
		
		for (Train train : this.trains){
			train.update();
			train.render(gc);
		}
	}
}