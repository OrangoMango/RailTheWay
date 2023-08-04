package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;

import java.util.*;

import dev.webfx.platform.resource.Resource;

public class Stoplight extends Tile{
	private boolean go = true;
	private byte target;
	private World world;
	private static final Image IMAGE = new Image(Resource.toUrl("/images/stoplight.png", Stoplight.class));

	private static final AudioClip STOPLIGHT = new AudioClip(Resource.toUrl("/audio/stoplight.wav", Stoplight.class));

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
		Tile trg = null;
		if ((this.target & 8) == 8){
			trg = world.getTileAt(this.x, this.y-1);
		} else if ((this.target & 4) == 4){
			trg = world.getTileAt(this.x+1, this.y);
		} else if ((this.target & 2) == 2){
			trg = world.getTileAt(this.x, this.y+1);
		} else if ((this.target & 1) == 1){
			trg = world.getTileAt(this.x-1, this.y);
		}
		if (trg != null){
			for (Train train : trains){
				Carriage c = train.getTrain().get(0);
				int worldX = (int)(c.getX()/WIDTH);
				int worldY = (int)(c.getY()/HEIGHT);
				if (worldX == trg.getX() && worldY == trg.getY()){
					c.setMoving(true);
				}
			}
		}
		STOPLIGHT.play();
	}

	public void render(GraphicsContext gc){
		gc.drawImage(IMAGE, 1+34*(this.go ? 1 : 0), 1, 32, 32, this.x*WIDTH, this.y*HEIGHT, WIDTH, HEIGHT);
	}
}