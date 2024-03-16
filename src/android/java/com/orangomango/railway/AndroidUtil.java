package com.orangomango.railway;

import javafxports.android.FXActivity;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.util.DisplayMetrics;
import android.media.MediaPlayer;

import java.lang.reflect.Method;
import java.nio.file.*;
import java.io.*;
import java.util.HashMap;

/**
 * Android utility class.
 *
 * @author OrangoMango
 * @version 1.0
 */
public class AndroidUtil{
	private static HashMap<String, MediaPlayer> mediaPlayers = new HashMap<>();

	public static void prepareApp(){
		try {
			if (Build.VERSION.SDK_INT >= 29){
				Method forName = Class.class.getDeclaredMethod("forName", String.class);
				Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
				Class vmRuntimeClass = (Class)forName.invoke(null, "dalvik.system.VMRuntime");
				Method getRuntime = (Method)getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);
				Method setHiddenApiExemptions = (Method)getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
				Object vmRuntime = getRuntime.invoke(null);
				setHiddenApiExemptions.invoke(vmRuntime, (Object[])new String[][]{new String[]{"L"}});
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static double[] getScreenSize(){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		FXActivity.getInstance().getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
		int height = displayMetrics.heightPixels;
		int width = displayMetrics.widthPixels;
		float density = displayMetrics.density;
		return new double[]{width/density, height/density};
	}
	
	public static void launchFullscreen(){
		FXActivity.getInstance().runOnUiThread(() -> {
			// Setup fullscreen
			FXActivity.getInstance().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
			FXActivity.getInstance().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			WindowManager.LayoutParams lp = FXActivity.getInstance().getWindow().getAttributes();
			lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
			FXActivity.getInstance().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

			// Clear useless temp files in cache of previous sessions
			for (File f : FXActivity.getInstance().getCacheDir().listFiles()){
				f.delete();
			}
		});
	}
	
	public static void copyAudioFiles(String... audioNames){
		try {
			File audioDir = new File(FXActivity.getInstance().getFilesDir().getAbsolutePath(), "audio");
			if (!audioDir.exists()) audioDir.mkdir();
			for (String name : audioNames){
				File file = new File(FXActivity.getInstance().getFilesDir().getAbsolutePath(), "/audio/"+name.split("/")[2]);
				if (!file.exists()){
					Files.copy(AndroidUtil.class.getResourceAsStream(name), file.toPath());
				}
			}
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	public static void playSound(String name, boolean loop){
		try {
			MediaPlayer mp = new MediaPlayer();
			mediaPlayers.put(name, mp);
			mp.setDataSource(FXActivity.getInstance().getFilesDir().getAbsolutePath()+"/audio/"+name);
			mp.setLooping(loop);
			mp.prepare();
			mp.setOnCompletionListener(player -> {
				player.release();
				mediaPlayers.remove(name);
			});
			mp.start();
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}
}