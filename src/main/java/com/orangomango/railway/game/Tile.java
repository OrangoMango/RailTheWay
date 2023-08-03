package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Tile{
	protected int x, y;
	public static double WIDTH, HEIGHT;

	public Tile(int x, int y){
		this.x = x;
		this.y = y;
	}

	public int getX(){
		return this.x;
	}

	public int getY(){
		return this.y;
	}

	public void render(GraphicsContext gc){
		gc.setFill(Color.GREEN);
		gc.fillRect(this.x*WIDTH, this.y*HEIGHT, WIDTH, HEIGHT);
	}
}