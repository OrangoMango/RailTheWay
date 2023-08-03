package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;

import java.io.*;

public class World{
	private int width, height;
	private Tile[][] world;

	public World(InputStream inputStream){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String header = reader.readLine();
			this.width = Integer.parseInt(header.split("x")[0]);
			this.height = Integer.parseInt(header.split("x")[1]);
			this.world = new Tile[this.width][this.height];
			for (int y = 0; y < this.height; y++){
				String line = reader.readLine();
				for (int x = 0; x < this.width; x++){
					String[] data = line.split(" ")[x].split(";");
					int type = Integer.parseInt(data[0]);
					Tile tile = null;
					if (type == 0){
						tile = new Tile(x, y);
					} else if (type == 1){
						tile = new Track(this, x, y);
						if (data.length == 2){
							((Track)tile).setBaseDirection(Byte.parseByte(data[1]));
						}
					}
					this.world[x][y] = tile;
				}
			}
			reader.close();
		} catch (IOException ex){
			ex.printStackTrace();
		}

		updateConnections();
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