package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Point2D;

import java.util.*;

import com.orangomango.railway.Util;

import dev.webfx.platform.resource.Resource;

public class Car{
	private static final double SPEED = 1;
	private static final Image IMAGE = new Image(Resource.toUrl("/images/car.png", Car.class));

	private double x, y;
	private World world;
	private byte direction;
	private boolean moving = true;
	private Tile currentTile;
	private int carIndex;

	public Car(World world, double x, double y, byte direction){
		this.world = world;
		this.x = x;
		this.y = y;
		this.direction = direction;
		Random random = new Random();
		this.carIndex = random.nextInt(4);
	}

	public void update(List<Car> cars){
		if (!this.moving) return;

		final int worldX = (int)(this.x/Tile.WIDTH);
		final int worldY = (int)(this.y/Tile.HEIGHT);
		Tile tile = this.world.getTileAt(worldX, worldY);
		if (Math.abs(this.x-(worldX*Tile.WIDTH+Tile.WIDTH/2)) < SPEED && Math.abs(this.y-(worldY*Tile.HEIGHT+Tile.HEIGHT/2)) < SPEED){
			// Fix the position
			this.x = worldX*Tile.WIDTH+Tile.WIDTH/2;
			this.y = worldY*Tile.HEIGHT+Tile.HEIGHT/2;

			if (tile instanceof Road){
				Road road = (Road)tile;
				int connAmount = road.getConnectionAmount();
				byte connections = road.getConnections();
				if ((connections & Util.invertDirection(this.direction)) == Util.invertDirection(this.direction)){
					connAmount--;
				}
				if (connAmount == 1){
					byte finalDir = (byte)(connections & Util.invertBits(Util.invertDirection(this.direction))); // 1001 0100 -> 1001 0001 -> 1001 1110 -> 1000
					this.direction = finalDir;
				} else if (connAmount == 2){
					byte finalDir = (byte)(connections & Util.invertBits(Util.invertDirection(this.direction))); // 1001 0100 -> 1001 0001 -> 1001 1110 -> 1000
					List<Byte> options = new ArrayList<>();
					if ((finalDir & 8) == 8) options.add((byte)8);
					if ((finalDir & 4) == 4) options.add((byte)4);
					if ((finalDir & 2) == 2) options.add((byte)2);
					if ((finalDir & 1) == 1) options.add((byte)1);
					Random random = new Random();
					byte out = options.get(random.nextInt(options.size()));
					this.direction = out;
				}

				// Railway gates
				Util.getNeighbors(this.world, tile).stream().filter(t -> {
					if (t instanceof CrossingGate && !((CrossingGate)t).isOpen()){
						if ((this.direction & 8) == 8 && t.getY() < worldY){
							return true;
						} else if ((this.direction & 4) == 4 && t.getX() > worldX){
							return true;
						} else if ((this.direction & 2) == 2 && t.getY() > worldY){
							return true;
						} else if ((this.direction & 1) == 1 && t.getX() < worldX){
							return true;
						} else return false;
					} else return false;
				}).findAny().ifPresent(t -> {
					this.moving = false;
				});
			}
		}

		this.currentTile = tile;

		if (cars.stream().filter(c -> {
			if (c != this && c.direction == this.direction){
				Point2D tp = new Point2D(this.x, this.y);
				Point2D op = new Point2D(c.x, c.y);
				if ((this.direction & 8) == 8 && op.getY() < tp.getY()){
					return tp.distance(op) < Math.sqrt(Tile.WIDTH*Tile.WIDTH+Tile.HEIGHT*Tile.HEIGHT)*0.6;
				} else if ((this.direction & 4) == 4 && op.getX() > tp.getX()){
					return tp.distance(op) < Math.sqrt(Tile.WIDTH*Tile.WIDTH+Tile.HEIGHT*Tile.HEIGHT)*0.6;
				} else if ((this.direction & 2) == 2 && op.getY() > tp.getY()){
					return tp.distance(op) < Math.sqrt(Tile.WIDTH*Tile.WIDTH+Tile.HEIGHT*Tile.HEIGHT)*0.6;
				} else if ((this.direction & 1) == 1 && op.getX() < tp.getX()){
					return tp.distance(op) < Math.sqrt(Tile.WIDTH*Tile.WIDTH+Tile.HEIGHT*Tile.HEIGHT)*0.6;
				} else return false;
			} else return false;
		}).findAny().isEmpty()){
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
	}

	public Tile getCurrentTile(){
		return this.currentTile;
	}

	public void setMoving(boolean value){
		this.moving = value;
	}

	public void render(GraphicsContext gc){
		gc.save();
		gc.translate(this.x, this.y);
		if ((this.direction & 8) == 8){
			gc.rotate(0);
		} else if ((this.direction & 4) == 4){
			gc.rotate(90);
		} else if ((this.direction & 2) == 2){
			gc.rotate(180);
		} else if ((this.direction & 1) == 1){
			gc.rotate(270);
		}
		gc.drawImage(IMAGE, 1+34*this.carIndex, 1, 32, 32, -Tile.WIDTH/2, -Tile.HEIGHT/2, Tile.WIDTH, Tile.HEIGHT);
		gc.restore();
	}
}
