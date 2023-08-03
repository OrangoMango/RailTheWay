package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import com.orangomango.railway.Util;

public class Carriage{
	private static final double WIDTH = 50;
	private static final double HEIGHT = 30;
	private static final double SPEED = 3;

	private double x, y;
	private World world;
	private byte direction;

	public Carriage(World world, double x, double y, byte direction){
		this.x = x;
		this.y = y;
		this.world = world;
		this.direction = direction;
	}

	public void update(){
		int worldX = (int)(this.x/Tile.WIDTH);
		int worldY = (int)(this.y/Tile.HEIGHT);
		Tile tile = this.world.getTileAt(worldX, worldY);
		if (tile instanceof Track && Math.abs(this.x-(worldX*Tile.WIDTH+Tile.WIDTH/2)) < SPEED && Math.abs(this.y-(worldY*Tile.HEIGHT+Tile.HEIGHT/2)) < SPEED){
			// Fix the position
			this.x = worldX*Tile.WIDTH+Tile.WIDTH/2;
			this.y = worldY*Tile.HEIGHT+Tile.HEIGHT/2;
			
			Track track = (Track)tile;
			int connAmount = track.getConnectionAmount();
			byte connections = track.getConnections();
			if ((connections & Util.invertDirection(this.direction)) == Util.invertDirection(this.direction)){
				connAmount--;
			}
			if (connAmount == 1){
				byte finalDir = (byte)(connections & Util.invertBits(Util.invertDirection(this.direction))); // 1001 0100 -> 1001 0001 -> 1001 1110 -> 1000
				this.direction = finalDir;
			} else if (connAmount == 2){
				byte trackDir = track.getDirection(); // 1010 1000 -> 1000
				if (trackDir == Util.invertDirection(this.direction)){
					this.direction = track.getBaseDirection();
				} else {
					this.direction = trackDir;
				}
			}
		}

		if ((this.direction & 8) == 8){
			this.y -= SPEED;
		} else if ((this.direction & 4) == 4){
			this.x += SPEED;
		} else if ((this.direction & 2) == 2){
			this.y += SPEED;
		} else if ((this.direction & 1) == 1){
			this.x -= SPEED;
		}
	}

	public void render(GraphicsContext gc, Color color){
		gc.setFill(color);
		if ((this.direction & 5) != 0){
			gc.fillRect(this.x-WIDTH/2, this.y-HEIGHT/2, WIDTH, HEIGHT);
		} else if ((this.direction & 10) != 0){
			gc.fillRect(this.x-HEIGHT/2, this.y-WIDTH/2, HEIGHT, WIDTH);
		}
	}
}