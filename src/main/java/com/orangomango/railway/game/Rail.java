package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;

import java.util.*;

import com.orangomango.railway.Util;

public class Rail extends Tile{
	protected World world;
	private byte connection; // 0 0 0 0 -> N E S W
	private int connectionAmount;
	private byte direction;
	private byte baseDirection;
	private boolean isInput;
	private static final Image IMAGE = new Image(Rail.class.getResourceAsStream("/images/rail.png"));

	private static final AudioClip TRACK_SOUND = new AudioClip(Rail.class.getResource("/audio/rail_change.wav").toExternalForm());

	public Rail(World world, int x, int y){
		super(x, y);
		this.world = world;
	}

	public void setInput(boolean value){
		this.isInput = value;
	}

	public boolean isInput(){
		return this.isInput;
	}

	public void addConnection(byte n){
		this.connection |= n;
		if (n != this.baseDirection) this.direction = n;
		this.connectionAmount++;
	}

	// Only for double rails
	public void setBaseDirection(byte dir){
		this.baseDirection = dir;
	}

	public void changeDirection(){
		// 0111 0001 0100 -> 0100, 0010
		if (this.connectionAmount != 3){
			throw new IllegalStateException("Connections amount invalid: "+this.connectionAmount);
		}
		List<Byte> options = new ArrayList<>();
		if ((this.connection & 8) == 8 && ((this.connection & 8) & this.baseDirection) == 0){
			options.add((byte)8);
		}
		if ((this.connection & 4) == 4 && ((this.connection & 4) & this.baseDirection) == 0){
			options.add((byte)4);
		}
		if ((this.connection & 2) == 2 && ((this.connection & 2) & this.baseDirection) == 0){
			options.add((byte)2);
		}
		if ((this.connection & 1) == 1 && ((this.connection & 1) & this.baseDirection) == 0){
			options.add((byte)1);
		}
		int index = options.indexOf(this.direction);
		index++;
		index %= options.size();
		this.direction = options.get(index);
		TRACK_SOUND.play();
	}

	public byte getDirection(){
		return this.direction;
	}

	public byte getConnections(){
		return this.connection;
	}

	public int getConnectionAmount(){
		return this.connectionAmount;
	}

	public byte getBaseDirection(){
		return this.baseDirection;
	}

	@Override
	public void render(GraphicsContext gc){
		int index = 0;
		int rotate = 0;
		boolean flip = false;
		if (this.connectionAmount == 4){
			index = 2;
		} else if (this.connectionAmount == 3){
			index = this.direction == Util.invertDirection(this.baseDirection) ? 3 : 4;

			// Flip the double rail when needed
			if ((this.baseDirection & 8) == 8 && (this.connection & 14) == 14){
				flip = true;
			}
			if ((this.baseDirection & 4) == 4 && (this.connection & 7) == 7){
				flip = true;
			}
			if ((this.baseDirection & 2) == 2 && (this.connection & 11) == 11){
				flip = true;
			}
			if ((this.baseDirection & 1) == 1 && (this.connection & 13) == 13){
				flip = true;
			}

			if ((this.connection & 14) == 14){ // 1110
				rotate = 0;
			} else if ((this.connection & 7) == 7){ // 0111
				rotate = 90;
			} else if ((this.connection & 11) == 11){ // 1011
				rotate = 180;
			} else if ((this.connection & 13) == 13){ // 1101
				rotate = 270;
			}
		} else if (this.connectionAmount == 2){
			// 1100 0011 -> 1   1010 0101 -> 0
			if ((this.connection & 10) == 10 || (this.connection & 5) == 5){
				index = 0;
				if ((this.connection & 10) == 10){
					rotate = 0;
				} else if ((this.connection & 5) == 5){
					rotate = 90;
				}
			} else {
				index = 1;
				if ((this.connection & 10) == 10){ // 0110
					rotate = 0;
				} else if ((this.connection & 3) == 3){ // 0011
					rotate = 90;
				} else if ((this.connection & 9) == 9){ // 1001
					rotate = 180;
				} else if ((this.connection & 12) == 12){ // 1100
					rotate = 270;
				}
			}
		} else if (this.connectionAmount == 1){
			index = 0;
			if ((this.connection & 10) != 0){ // N S
				rotate = 0;
			} else if ((this.connection & 5) != 0){ // E W
				rotate = 90;
			}
		}

		gc.save();
		gc.translate(this.x*WIDTH+WIDTH/2, this.y*HEIGHT+HEIGHT/2);
		gc.rotate(rotate);
		gc.drawImage(IMAGE, 1+34*index, 1, 32, 32, -WIDTH/2, -HEIGHT/2+(flip ? HEIGHT : 0), WIDTH, HEIGHT*(flip ? -1 : 1));
		gc.restore();
	}
}