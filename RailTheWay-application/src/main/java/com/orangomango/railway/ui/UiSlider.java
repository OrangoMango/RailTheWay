package com.orangomango.railway.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

import dev.webfx.platform.resource.Resource;

public class UiSlider{
	private GraphicsContext gc;
	private double x, y, w, h;
	private double value = 0.5;
	private Image sImage, eImage, slider;
	private Consumer<Double> onDrag;

	public UiSlider(GraphicsContext gc, double x, double y, double w, double h, Image sImage, Image eImage, Consumer<Double> onDrag){
		this.gc = gc;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.sImage = sImage;
		this.eImage = eImage;
		this.slider = new Image(Resource.toUrl("/images/slider.png", UiSlider.class));
		this.onDrag = onDrag;
	}

	public void drag(double x, double y){
		double v = (x-this.x)/this.w;
		v = Math.min(0.9, Math.max(v, 0.1));
		this.value = v;
		this.onDrag.accept(this.value);
	}

	public void render(){
		gc.setFill(Color.BLACK);
		gc.fillRect(this.x, this.y+(this.h-this.h*0.3)/2, this.w, this.h*0.3);
		gc.drawImage(this.slider, this.x+this.w*this.value-32, this.y+(this.h-this.h*0.3)/2-15, 64, 64);
		gc.drawImage(this.sImage, this.x, this.y, this.h, this.h);
		gc.drawImage(this.eImage, this.x+this.w-this.h, this.y, this.h, this.h);
	}
}
