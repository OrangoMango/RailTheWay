package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

import dev.webfx.platform.resource.Resource;

public class World{
	private int width, height;
	private Tile[][] world;

	public World(String name){
		Random random = new Random();

		String[] worldData = Resource.getText(Resource.toUrl(name, World.class)).split("\n");
		int lineCounter = 0;
		String[] header = worldData[lineCounter++].split(" ");
		this.width = Integer.parseInt(header[0].split("x")[0]);
		this.height = Integer.parseInt(header[0].split("x")[1]);
		Tile.WIDTH = Integer.parseInt(header[1].split("x")[0]);
		Tile.HEIGHT = Integer.parseInt(header[1].split("x")[1]);
		this.world = new Tile[this.width][this.height];
		for (int y = 0; y < this.height; y++){
			String line = worldData[lineCounter++];
			for (int x = 0; x < this.width; x++){
				String[] data = line.split("\t")[x].split(";");
				int type = Integer.parseInt(data[0]);
				Tile tile = null;
				if (type == 0){
					tile = new Tile(x, y);
				} else if (type == 1){
					tile = new Track(this, x, y);
					if (data.length == 2){
						((Track)tile).setBaseDirection(Byte.parseByte(data[1]));
					}
				} else if (type == 2){
					tile = new Station(x, y, TrainType.values()[Integer.parseInt(data[1])]);
				} else if (type == 3){
					tile = new Stoplight(this, x, y, Byte.parseByte(data[1]));
				} else if (type == 4){
					tile = new Track(this, x, y);
					((Track)tile).setInput(true);
				}
				this.world[x][y] = tile;
			}
		}

		updateConnections();
	}

	public int getWidth(){
		return this.width;
	}

	public int getHeight(){
		return this.height;
	}

	private void updateConnections(){
		for (int x = 0; x < this.width; x++){
			for (int y = 0; y < this.height; y++){
				Tile tile = this.world[x][y];
				if (tile instanceof Track){
					Track track = (Track)tile;
					Tile n = getTileAt(x, y-1);
					Tile e = getTileAt(x+1, y);
					Tile s = getTileAt(x, y+1);
					Tile w = getTileAt(x-1, y);
					if (n != null && n instanceof Track) track.addConnection((byte)8);
					if (e != null && e instanceof Track) track.addConnection((byte)4);
					if (s != null && s instanceof Track) track.addConnection((byte)2);
					if (w != null && w instanceof Track) track.addConnection((byte)1);
				}
			}
		}
	}

	public Tile getTileAt(int x, int y){
		if (x < 0 || y < 0 || x >= this.width || y >= this.height){
			return null;
		} else {
			return this.world[x][y];
		}
	}

	public void render(GraphicsContext gc){
		for (int x = 0; x < this.width; x++){
			for (int y = 0; y < this.height; y++){
				this.world[x][y].render(gc);
			}
		}
	}
}