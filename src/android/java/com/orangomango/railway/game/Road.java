package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import com.orangomango.railway.AssetLoader;

public class Road extends Tile{
	private byte connection; // 0 0 0 0 -> N E S W
	private int connectionAmount;
	private byte disconnection;
	private static final Image IMAGE = AssetLoader.getInstance().getImage("road.png");

	public Road(int x, int y){
		super(x, y);
	}
	
	public void setDisconnection(byte value){
		this.disconnection = value;
	}

	public void addConnection(byte n){
		if ((this.disconnection & n) != 0){
			return;
		}
		this.connection |= n;
		this.connectionAmount++;
	}

	public byte getConnections(){
		return this.connection;
	}

	public int getConnectionAmount(){
		return this.connectionAmount;
	}

	@Override
	public void render(GraphicsContext gc){
		int index = 0;
		int rotate = 0;
		if (this.connectionAmount == 4){
			index = 2;
		} else if (this.connectionAmount == 3){
			index = 3;
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
		gc.drawImage(IMAGE, 1+34*index, 1, 32, 32, -WIDTH/2, -HEIGHT/2, WIDTH, HEIGHT);
		gc.restore();
	}
}