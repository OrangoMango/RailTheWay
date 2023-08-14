package com.orangomango.railway.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.animation.*;
import javafx.util.Duration;

import java.util.List;

import com.orangomango.railway.Util;

public class CrossingGate extends Rail{
	private boolean vertical;
	private int imageIndex = 8;
	private volatile boolean open = true;
	private boolean animating;
	private static final Image IMAGE = new Image(Rail.class.getResourceAsStream("/images/crossingGate.png"));

	public CrossingGate(World world, int x, int y, boolean vertical){
		super(world, x, y);
		this.vertical = vertical;
		if (this.vertical){
			addConnection((byte)8);
			addConnection((byte)2);
		} else {
			addConnection((byte)4);
			addConnection((byte)1);
		}
	}

	public boolean isOpen(){
		return this.open;
	}

	public void toggle(List<Car> cars){
		setOn(cars, !this.open);
	}

	private void setOn(List<Car> cars, boolean value){
		if (this.animating) return;
		this.animating = true;
		this.open = value;

		// Auto-open
		if (!this.open){
			Util.schedule(() -> {
				if (!this.open){
					setOn(cars, true);
				}
			}, 4000);
		}

		// Start the animation
		this.imageIndex = this.open ? 0 : 8;
		Timeline animation = new Timeline(new KeyFrame(Duration.millis(50), e -> this.imageIndex += this.open ? 1 : -1));
		animation.setCycleCount(8);
		animation.setOnFinished(e -> this.animating = false);
		animation.play();

		Tile trg1 = null;
		Tile trg2 = null;
		if (this.vertical){
			trg1 = this.world.getTileAt(this.x-1, this.y);
			trg2 = this.world.getTileAt(this.x+1, this.y);
		} else {
			trg1 = this.world.getTileAt(this.x, this.y-1);
			trg2 = this.world.getTileAt(this.x, this.y+1);
		}
		for (Car car : cars){
			if (car.getCurrentTile() == trg1 || car.getCurrentTile() == trg2){
				car.setMoving(true);
			}
		}
	}

	@Override
	public void render(GraphicsContext gc){
		gc.save();
		gc.translate(this.x*WIDTH+WIDTH/2, this.y*HEIGHT+HEIGHT/2);
		if (!this.vertical) gc.rotate(90);
		gc.drawImage(IMAGE, 1+34*this.imageIndex, 1, 32, 32, -WIDTH/2, -HEIGHT/2, WIDTH, HEIGHT);
		gc.restore();
	}
}