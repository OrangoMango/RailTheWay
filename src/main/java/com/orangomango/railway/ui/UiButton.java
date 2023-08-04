package com.orangomango.railway.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Rectangle2D;
import javafx.scene.media.AudioClip;

public class UiButton{
	private double x, y, w, h;
	private Image image;
	private Runnable onClick;
	private GraphicsContext gc;

	private static final AudioClip SELECT_SOUND = new AudioClip(UiButton.class.getResource("/audio/select.wav").toExternalForm());

	public UiButton(GraphicsContext gc, double x, double y, double w, double h, Image image, Runnable onClick){
		this.gc = gc;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.image = image;
		this.onClick = onClick;
	}

	public void click(double x, double y){
		Rectangle2D rect = new Rectangle2D(this.x, this.y, this.w, this.h);
		if (rect.contains(x, y)){
			this.onClick.run();
			SELECT_SOUND.play();
		}
	}

	public void render(){
		gc.drawImage(this.image, this.x, this.y, this.w, this.h);
	}
}