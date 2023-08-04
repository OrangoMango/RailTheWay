package com.orangomango.railway.game;

import javafx.scene.paint.Color;

public enum TrainType{
	YELLOW(Color.YELLOW),
	RED(Color.RED),
	BLUE(Color.BLUE),
	LIME(Color.LIME);

	private Color color;

	private TrainType(Color color){
		this.color = color;
	}

	public Color getColor(){
		return this.color;
	}
}