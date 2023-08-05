package com.orangomango.railway.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Rectangle2D;
import javafx.scene.media.AudioClip;

import dev.webfx.platform.resource.Resource;

public class UiButton{
	private double x, y, w, h;
	private Image image;
	private Runnable onClick;
	private GraphicsContext gc;
	private volatile boolean hovering;

	private static final AudioClip SELECT_SOUND = new AudioClip(Resource.toUrl("/audio/select.wav", UiButton.class));

	public UiButton(GraphicsContext gc, double x, double y, double w, double h, Image image, Runnable onClick){
		this.gc = gc;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.image = image;
		this.onClick = onClick;
	}

	public double getX(){
		return this.x;
	}

	public double getY(){
		return this.y;
	}

	public void click(double x, double y){
		Rectangle2D rect = new Rectangle2D(this.x, this.y, this.w, this.h);
		if (rect.contains(x, y)){
			this.onClick.run();
			SELECT_SOUND.play();
		}
	}

	public void hover(double x, double y){
		Rectangle2D rect = new Rectangle2D(this.x, this.y, this.w, this.h);
		this.hovering = rect.contains(x, y);
	}

	public void render(){
		if (this.hovering){
			gc.drawImage(this.image, this.x-12, this.y-12, this.w+25, this.h+25);
		} else {
			gc.drawImage(this.image, this.x, this.y, this.w, this.h);		
		}
	}
}