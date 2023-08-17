package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;

import com.orangomango.railway.Util;
import com.orangomango.railway.ui.GameScreen;

public class Carriage{
	private static final double SPEED = 3;
	private static final Image IMAGE = new Image(Carriage.class.getResourceAsStream("/images/carriage.png"));
	private static final Image CARGO_IMAGE = new Image(Carriage.class.getResourceAsStream("/images/cargo.png"));

	private static final AudioClip STATION_SOUND = new AudioClip(Carriage.class.getResource("/audio/station.wav").toExternalForm());
	private static final AudioClip STATION_MISSED = new AudioClip(Carriage.class.getResource("/audio/station_missed.wav").toExternalForm());

	private double x, y;
	private World world;
	private byte direction;
	private boolean moving = true;
	private TrainType trainType;
	private Carriage parent;
	private Tile currentTile;
	private boolean stationPassed;
	private boolean cargo;
	private int cargoIndex;
	private boolean missed, jolly;
	private int multiplier = 1;

	public Carriage(World world, TrainType trainType, double x, double y, byte direction, Carriage parent){
		this.x = x;
		this.y = y;
		this.cargo = Math.random() > 0.8;
		this.trainType = trainType;
		this.world = world;
		this.direction = direction;
		this.parent = parent;
	}

	public boolean isCargo(){
		return this.cargo;
	}

	public void setCargo(boolean value){
		this.cargo = value;
	}

	public void setCargoIndex(int i){
		this.cargoIndex = i;
	}

	public void update(){
		if (!this.moving) return;

		int worldX = (int)(this.x/Tile.WIDTH);
		int worldY = (int)(this.y/Tile.HEIGHT);
		Tile tile = this.world.getTileAt(worldX, worldY);
		if (tile instanceof Rail && Math.abs(this.x-(worldX*Tile.WIDTH+Tile.WIDTH/2)) < SPEED && Math.abs(this.y-(worldY*Tile.HEIGHT+Tile.HEIGHT/2)) < SPEED){
			// Fix the position
			this.x = worldX*Tile.WIDTH+Tile.WIDTH/2;
			this.y = worldY*Tile.HEIGHT+Tile.HEIGHT/2;
			
			Rail rail = (Rail)tile;
			int connAmount = rail.getConnectionAmount();
			byte connections = rail.getConnections();
			if ((connections & Util.invertDirection(this.direction)) == Util.invertDirection(this.direction)){
				connAmount--;
			}
			if (connAmount == 1){
				byte finalDir = (byte)(connections & Util.invertBits(Util.invertDirection(this.direction))); // 1001 0100 -> 1001 0001 -> 1001 1110 -> 1000
				this.direction = finalDir;
			} else if (connAmount == 2){
				byte railDir = rail.getDirection(); // 1010 1000 -> 1000
				if (rail.getBaseDirection() == Util.invertDirection(this.direction)){
					this.direction = railDir;
				} else if (rail.getBaseDirection() == this.direction || Util.invertDirection(this.direction) == railDir){
					this.direction = rail.getBaseDirection();
				} else {
					rail.changeDirection();
					this.direction = rail.getBaseDirection();
				}
			}

			if (this.parent == null){
				// Station
				if ((!this.stationPassed || this.jolly) && !this.cargo){
					Util.getNeighbors(this.world, tile).stream().filter(t -> t instanceof Station && (this.jolly || ((Station)t).getType() == this.trainType)).findAny().ifPresent(t -> {
						final int useTime = 1500;
						boolean available = ((Station)t).use(useTime);
						if (available){
							this.moving = false;
							STATION_SOUND.play();
							GameScreen.score += 100*(this.multiplier++);
							GameScreen.arrivals++;
							this.stationPassed = true;
							Util.schedule(() -> {
								this.moving = true;
								this.jolly = true;
							}, useTime);
						}
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
		}

		this.currentTile = tile;

		if (!isInside()){
			if (!this.missed && this.parent == null && !this.stationPassed && !this.cargo){
				GameScreen.score -= 75;
				GameScreen.misses++;
				STATION_MISSED.play();
				this.missed = true;
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

	public boolean isJolly(){
		return this.jolly;
	}

	public void setJolly(boolean value){
		this.jolly = value;
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

	public void render(GraphicsContext gc){
		if (!isInside()) return;
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

		if (this.cargo){
			gc.drawImage(CARGO_IMAGE, 1+34*this.cargoIndex, 1, 32, 32, -Tile.WIDTH/2, -Tile.HEIGHT/2, Tile.WIDTH, Tile.HEIGHT);
		} else {
			int index = this.jolly ? 4 : this.trainType.ordinal();
			gc.drawImage(IMAGE, 1+34*index, 1, 32, 32, -Tile.WIDTH/2, -Tile.HEIGHT/2, Tile.WIDTH, Tile.HEIGHT);
		}
		gc.restore();
	}
}