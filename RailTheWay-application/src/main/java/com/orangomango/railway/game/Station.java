package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import dev.webfx.platform.scheduler.Scheduler;
import dev.webfx.platform.resource.Resource;

public class Station extends Tile{
	private TrainType trainType;
	private boolean using;
	private int index;
	private static final Image IMAGE = new Image(Resource.toUrl("/images/station.png", Station.class));
	private static final Image TIMER_IMAGE = new Image(Resource.toUrl("/images/station_timer.png", Station.class));

	public Station(int x, int y, TrainType type){
		super(x, y);
		this.trainType = type;
	}

	public boolean use(int time){
		if (this.using) return false;
		this.using = true;

		int[] index = new int[]{0};
		Scheduler.schedulePeriodic(time/9, scheduled -> {
			if (index[0] < 9){
				this.index = index[0];
				index[0]++;
			} else {
				this.using = false;
				this.index = 0;
				scheduled.cancel();
			}
		});

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
