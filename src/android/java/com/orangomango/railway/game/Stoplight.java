package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

import com.orangomango.railway.Util;
import com.orangomango.railway.AndroidUtil;
import com.orangomango.railway.AssetLoader;

public class Stoplight extends Tile{
	private boolean go;
	private byte target;
	private World world;

	private static final Image IMAGE = AssetLoader.getInstance().getImage("stoplight.png");
	private static final String STOPLIGHT_SOUND = "stoplight.wav";

	public Stoplight(World world, int x, int y, byte target){
		super(x, y);
		this.target = target;
		this.world = world;
	}

	public boolean canGo(){
		return this.go;
	}

	public void toggle(List<Train> trains){
		this.go = !this.go;
		Tile trg = getTargetTile();
		if (trg != null){
			for (Train train : trains){
				Carriage c = train.getTrain().get(0);
				if (c.getCurrentTile() == trg){
					c.setMoving(true);
				}
			}
		}
		AndroidUtil.playSound(STOPLIGHT_SOUND, false);
	}

	public Tile getTargetTile(){
		if ((this.target & 8) == 8){
			return this.world.getTileAt(this.x, this.y-1);
		} else if ((this.target & 4) == 4){
			return this.world.getTileAt(this.x+1, this.y);
		} else if ((this.target & 2) == 2){
			return this.world.getTileAt(this.x, this.y+1);
		} else if ((this.target & 1) == 1){
			return this.world.getTileAt(this.x-1, this.y);
		} else {
			return null;
		}
	}

	@Override
	public void render(GraphicsContext gc){
		gc.drawImage(IMAGE, 1+34*(this.go ? 1 : 0), 1, 32, 32, this.x*WIDTH, this.y*HEIGHT, WIDTH, HEIGHT);
	}
}