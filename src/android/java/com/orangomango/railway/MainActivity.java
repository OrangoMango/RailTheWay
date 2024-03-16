package com.orangomango.railway;

import javafxports.android.FXActivity;
import android.os.Bundle;

public class MainActivity extends FXActivity{
    private Runnable r;
    private static MainActivity instance;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        instance = this;
    }

    @Override
    public void onBackPressed(){
        if (this.r != null){
            this.r.run();
        }
    }

    public static MainActivity getInstance(){
        return instance;
    }

    public void setOnBackPressed(Runnable r){
        this.r = r;
    }
}