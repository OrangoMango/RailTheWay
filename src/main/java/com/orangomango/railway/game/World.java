package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.*;

public class World{
	private int width, height;
	private Tile[][] world;
	private List<TrainType> randomTrains = new ArrayList<>();

	public World(InputStream inputStream, List<Car> cars){
		Random random = new Random();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String[] header = reader.readLine().split(" ");
			this.width = Integer.parseInt(header[0].split("x")[0]);
			this.height = Integer.parseInt(header[0].split("x")[1]);
			Tile.WIDTH = Integer.parseInt(header[1].split("x")[0]);
			Tile.HEIGHT = Integer.parseInt(header[1].split("x")[1]);
			for (String rt : header[2].split(";")){
				randomTrains.add(TrainType.values()[Integer.parseInt(rt)]);
			}
			this.world = new Tile[this.width][this.height];
			for (int y = 0; y < this.height; y++){
				String line = reader.readLine();
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
					} else if (type == 5){
						tile = new Road(x, y);
					} else if (type == 6){
						tile = new CrossingGate(this, x, y, Integer.parseInt(data[1]) == 1);
					}
					this.world[x][y] = tile;
				}
			}
			int carsN = Integer.parseInt(reader.readLine().split(" ")[1]);
			for (int i = 0; i < carsN; i++){
				String line = reader.readLine();
				int xp = Integer.parseInt(line.split(" ")[1]);
				int yp = Integer.parseInt(line.split(" ")[2]);
				byte dir = Byte.parseByte(line.split(" ")[3]);
				cars.add(new Car(this, xp*Tile.WIDTH+Tile.WIDTH/2, yp*Tile.HEIGHT+Tile.HEIGHT/2, dir));
			}
			reader.close();
		} catch (IOException ex){
			ex.printStackTrace();
		}

		updateConnections();
	}

	public int getWidth(){
		return this.width;
	}

	public int getHeight(){
		return this.height;
	}

	public List<TrainType> getRandomTrains(){
		return this.randomTrains;
	}

	private void updateConnections(){
		for (int x = 0; x < this.width; x++){
			for (int y = 0; y < this.height; y++){
				Tile tile = this.world[x][y];
				Tile n = getTileAt(x, y-1);
				Tile e = getTileAt(x+1, y);
				Tile s = getTileAt(x, y+1);
				Tile w = getTileAt(x-1, y);
				if (tile instanceof Track){
					Track track = (Track)tile;
					if (n != null && n instanceof Track) track.addConnection((byte)8);
					if (e != null && e instanceof Track) track.addConnection((byte)4);
					if (s != null && s instanceof Track) track.addConnection((byte)2);
					if (w != null && w instanceof Track) track.addConnection((byte)1);
				} else if (tile instanceof Road){
					Road road = (Road)tile;
					if (n != null && (n instanceof Road || n instanceof CrossingGate)) road.addConnection((byte)8);
					if (e != null && (e instanceof Road || e instanceof CrossingGate)) road.addConnection((byte)4);
					if (s != null && (s instanceof Road || s instanceof CrossingGate)) road.addConnection((byte)2);
					if (w != null && (w instanceof Road || w instanceof CrossingGate)) road.addConnection((byte)1);
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