package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Tile{
	protected int x, y;
	public static final double WIDTH = 50, HEIGHT = 50;

	public Tile(int x, int y){
		this.x = x;
		this.y = y;
	}

	public void render(GraphicsContext gc){
		gc.setFill(Color.GREEN);
		gc.fillRect(this.x*WIDTH, this.y*HEIGHT, WIDTH, HEIGHT);
	}
}