package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import com.orangomango.railway.AssetLoader;

public class Station extends Tile{
	private TrainType trainType;
	private boolean using;
	private int index;
	private static final Image IMAGE = AssetLoader.getInstance().getImage("station.png");
	private static final Image TIMER_IMAGE = AssetLoader.getInstance().getImage("station_timer.png");

	public Station(int x, int y, TrainType type){
		super(x, y);
		this.trainType = type;
	}

	public boolean use(int time){
		if (this.using) return false;
		this.using = true;
		new Thread(() -> {
			for (int i = 0; i < 9; i++){
				try {
					this.index = i;
					Thread.sleep(time/9);
				} catch (InterruptedException ex){
					ex.printStackTrace();
				}
			}
			this.using = false;
		}).start();
		return true;
	}

	public TrainType getType(){
		return this.trainType;
	}

	@Override
	public void render(GraphicsContext gc){
		int idx = this.trainType.ordinal();
		gc.drawImage(IMAGE, 1+34*idx, 1, 32, 32, this.x*WIDTH, this.y*HEIGHT, WIDTH, HEIGHT);
		if (this.using) gc.drawImage(TIMER_IMAGE, 1+18*this.index, 1, 16, 16, this.x*WIDTH+8, this.y*HEIGHT+8, 16, 16);
	}
}