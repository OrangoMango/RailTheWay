package com.orangomango.railway.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.animation.*;
import javafx.util.Duration;

import com.orangomango.railway.Util;

public class InformationText{
	private double x, y;
	private Color color;
	private String text;
	private boolean exists = true;

	public InformationText(Color color, String text, double x, double y){
		this.color = color;
		this.text = text;
		this.x = x;
		this.y = y;
		Timeline animation = new Timeline(new KeyFrame(Duration.millis(60), e -> this.x -= 15));
		animation.setCycleCount(10);
		animation.setOnFinished(e -> Util.schedule(() -> this.exists = false, 1000));
		animation.play();
	}

	public boolean exists(){
		return this.exists;
	}

	public void render(GraphicsContext gc){
		gc.setFill(this.color);
		gc.setFont(GameScreen.FONT);
		gc.fillText(this.text, this.x, this.y);
	}
}