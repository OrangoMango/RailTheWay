package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Tile{
	protected int x, y;
	private int index;
	public static double WIDTH, HEIGHT;
	private static final Image IMAGE = new Image(Track.class.getResourceAsStream("/images/grass.png"));

	public Tile(int x, int y){
		this.x = x;
		this.y = y;
		double rnd = Math.random();
		if (rnd < 0.5){
			this.index = 3; // grass
		} else if (rnd < 0.65){
			this.index = Math.random() > 0.5 ? 1 : 2; // trees
		} else {
			this.index = 0; // flowers
		}
	}

	public int getX(){
		return this.x;
	}

	public int getY(){
		return this.y;
	}

	public void render(GraphicsContext gc){
		gc.drawImage(IMAGE, 1+34*this.index, 1, 32, 32, this.x*WIDTH, this.y*HEIGHT, WIDTH, HEIGHT);
	}
}