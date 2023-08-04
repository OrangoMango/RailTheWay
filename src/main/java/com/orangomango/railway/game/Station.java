package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;

public class Station extends Tile{
	private TrainType trainType;

	public Station(int x, int y, TrainType type){
		super(x, y);
		this.trainType = type;
	}

	public TrainType getType(){
		return this.trainType;
	}

	public void render(GraphicsContext gc){
		gc.setFill(this.trainType.getColor());
		gc.fillRect(this.x*WIDTH, this.y*HEIGHT, WIDTH, HEIGHT);
	}
}