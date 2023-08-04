package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import com.orangomango.railway.Util;
import com.orangomango.railway.ui.GameScreen;

public class Carriage{
	private static final double SPEED = 3;
	private static final Image IMAGE = new Image(Track.class.getResourceAsStream("/images/carriage.png"));

	private double x, y;
	private World world;
	private byte direction;
	private boolean moving = true;
	private TrainType trainType;
	private Carriage parent;
	private Tile currentTile;
	private boolean stationPassed;

	public Carriage(World world, TrainType trainType, double x, double y, byte direction, Carriage parent){
		this.x = x;
		this.y = y;
		this.trainType = trainType;
		this.world = world;
		this.direction = direction;
		this.parent = parent;
	}

	public void update(){
		if (!this.moving) return;

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
				if (track.getBaseDirection() == Util.invertDirection(this.direction)){
					this.direction = trackDir;
				} else if (track.getBaseDirection() == this.direction || Util.invertDirection(this.direction) == trackDir){
					this.direction = track.getBaseDirection();
				} else {
					track.changeDirection();
					this.direction = track.getBaseDirection();
				}
			}

			// Station
			if (this.parent == null){
				if (!this.stationPassed){
					Util.getNeighbors(this.world, tile).stream().filter(t -> t instanceof Station && ((Station)t).getType() == this.trainType).findAny().ifPresent(t -> {
						this.moving = false;
						GameScreen.score += 100;
						this.stationPassed = true;
						Util.schedule(() -> this.moving = true, 1500);
					});
				}

				// Stoplight
				Util.getNeighbors(this.world, tile).stream().filter(t -> t instanceof Stoplight && !((Stoplight)t).canGo()).findAny().ifPresent(t -> {
					this.moving = false;
				});
			} else {
				Point2D thisPoint = new Point2D(this.x, this.y);
				Point2D otherPoint = new Point2D(this.parent.x, this.parent.y);
				if (thisPoint.distance(otherPoint) > Math.sqrt(Tile.WIDTH*Tile.WIDTH+Tile.HEIGHT*Tile.HEIGHT)){
					this.moving = false;
				}
			}

			this.currentTile = tile;
		}

		if (!isInside()){
			if (this.currentTile != null && this.parent == null && !this.stationPassed){
				GameScreen.score -= 75;
			}
			this.currentTile = null;
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

	public Tile getCurrentTile(){
		return this.currentTile;
	}

	public void setMoving(boolean value){
		this.moving = value;
	}

	public boolean isMoving(){
		return this.moving;
	}

	public boolean isInside(){
		return this.x >= 0 && this.y >= 0 && this.x <= this.world.getWidth()*Tile.WIDTH && this.y <= this.world.getHeight()*Tile.HEIGHT;
	}

	public double getX(){
		return this.x;
	}

	public double getY(){
		return this.y;
	}

	public void render(GraphicsContext gc){
		if (!isInside()) return;
		int index = this.trainType.ordinal();
		gc.save();
		gc.translate(this.x, this.y);
		if ((this.direction & 8) == 8){
			gc.rotate(270);
		} else if ((this.direction & 4) == 4){
			gc.rotate(0);
		} else if ((this.direction & 2) == 2){
			gc.rotate(90);
		} else if ((this.direction & 1) == 1){
			gc.rotate(180);
		}
		gc.drawImage(IMAGE, 1+34*index, 1, 32, 32, -Tile.WIDTH/2, -Tile.HEIGHT/2, Tile.WIDTH, Tile.HEIGHT);
		gc.restore();
	}
}