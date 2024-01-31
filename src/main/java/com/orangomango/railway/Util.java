package com.orangomango.railway;

import java.util.*;

import com.orangomango.railway.game.*;

public class Util{
	public static final double WINDOW_WIDTH = 1150;
	public static final double WINDOW_HEIGHT = 750;
	public static final double SCALE = WINDOW_HEIGHT/750;
	public static final double GAME_WIDTH = 1150*Util.SCALE;
	public static final double GAME_HEIGHT = 750*Util.SCALE;

	public static byte invertDirection(byte dir){
		if ((dir & 8) == 8){
			return (byte)2;
		} else if ((dir & 4) == 4){
			return (byte)1;
		} else if ((dir & 2) == 2){
			return (byte)8;
		} else if ((dir & 1) == 1){
			return (byte)4;
		} else {
			throw new IllegalStateException("Not a valid direction: "+dir);
		}
	}

	public static Tile[] getRandomStart(World world, int n){
		List<Tile> tiles = new ArrayList<>();
		for (int y = 0; y < world.getHeight(); y++){
			for (int x = 0; x < world.getWidth(); x++){
				if (x == 0 || y == 0 || x == world.getWidth()-1 || y == world.getHeight()-1){
					Tile tile = world.getTileAt(x, y);
					if (tile instanceof Rail && ((Rail)tile).isInput()){
						tiles.add(tile);
					}
				}
			}
		}
		Random r = new Random();
		n = Math.min(tiles.size(), n); // Setup N
		Tile[] output = new Tile[n];
		for (int i = 0; i < n; i++){
			output[i] = tiles.remove(r.nextInt(tiles.size()));
		}
		return output;
	}

	public static void schedule(Runnable r, int delay){
		new Thread(() -> {
			try {
				Thread.sleep(delay);
				r.run();
			} catch (InterruptedException ex){
				ex.printStackTrace();
			}
		}).start();
	}

	public static byte invertBits(byte dir){
		return (byte)(dir ^ 15);
	}

	public static List<Tile> getNeighbors(World world, Tile tile){
		List<Tile> list = new ArrayList<>();
		Tile n = world.getTileAt(tile.getX(), tile.getY()-1);
		Tile e = world.getTileAt(tile.getX()+1, tile.getY());
		Tile s = world.getTileAt(tile.getX(), tile.getY()+1);
		Tile w = world.getTileAt(tile.getX()-1, tile.getY());
		if (n != null) list.add(n);
		if (e != null) list.add(e);
		if (s != null) list.add(s);
		if (w != null) list.add(w);
		return list;
	}
}