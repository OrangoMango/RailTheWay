package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;

public class Train{
	private List<Carriage> train = new ArrayList<>();
	private Color color;

	public Train(World world, int n, double x, double y, int offX, int offY, byte direction, Color color){
		for (int i = 0; i < n; i++){
			Carriage c = new Carriage(world, x+(Tile.WIDTH+5)*offX*i, y+(Tile.HEIGHT+5)*offY*i, direction);
			this.train.add(c);
		}
		this.color = color;
	}

	public void update(){
		for (Carriage c : this.train){
			c.update();
		}
	}

	public void render(GraphicsContext gc){
		for (Carriage c : this.train){
			c.render(gc, this.color);
		}
	}
}