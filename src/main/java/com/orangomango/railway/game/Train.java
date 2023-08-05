package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;

import java.util.*;

public class Train{
	private List<Carriage> train = new ArrayList<>();
	private TrainType type;
	private boolean exists = true;

	public Train(World world, int n, double x, double y, int offX, int offY, byte direction, TrainType type){
		boolean cargo = false;
		Random random = new Random();
		for (int i = 0; i < n; i++){
			Carriage c = new Carriage(world, type, x+(Tile.WIDTH+5)*offX*i, y+(Tile.HEIGHT+5)*offY*i, direction, i == 0 ? null : this.train.get(i-1));
			if (i == 0){
				cargo = c.isCargo();
			} else if (cargo){
				c.setCargoIndex(random.nextInt(4)+1);
			}
			c.setCargo(cargo); // Be sure that the train is either all cargo or all the trainType
			this.train.add(c);
		}
		this.type = type;
	}

	public boolean exists(){
		return this.exists;
	}

	public List<Carriage> getTrain(){
		return this.train;
	}

	public void update(){
		if (this.train.get(0).isMoving() && this.exists){
			boolean allOutside = true;;
			for (Carriage c : this.train){
				c.update();
				if (c.isInside()){
					allOutside = false;
				}
			}
			if (allOutside){
				this.exists = false;
			}
		}
	}

	public void render(GraphicsContext gc){
		for (Carriage c : this.train){
			c.render(gc);
		}
	}
}