package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;

public class Track extends Tile{
	private World world;
	private byte connection; // 0 0 0 0 -> N E S W
	private int connectionAmount;
	private byte direction;
	private byte baseDirection;

	public Track(World world, int x, int y){
		super(x, y);
		this.world = world;
	}

	public void addConnection(byte n){
		this.connection |= n;
		if (n != this.baseDirection) this.direction = n;
		this.connectionAmount++;
	}

	// Only for double tracks
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
		gc.setFill(Color.GRAY);
		gc.fillRect(this.x*WIDTH, this.y*HEIGHT, WIDTH, HEIGHT);
		gc.setFill(Color.RED);
		gc.fillText(""+this.direction, this.x*WIDTH, this.y*HEIGHT+HEIGHT);
	}
}