package org.racenet.framework;

import org.racenet.framework.interfaces.Audio;
import org.racenet.framework.interfaces.FileIO;
import org.racenet.framework.interfaces.Game;
import org.racenet.framework.interfaces.Graphics;
import org.racenet.framework.interfaces.Input;
import org.racenet.framework.interfaces.Screen;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;

public abstract class AndroidGame extends Activity implements Game {
	
	private int screenWidth;
	private int screenHeight;
	
    private AndroidFastRenderView renderView;
    private Graphics graphics;
    private Audio audio;
    private Input input;
    private FileIO fileIO;
    private Screen screen;
    private WakeLock wakeLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	screenWidth = getWindowManager().getDefaultDisplay().getWidth();
    	screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        int frameBufferWidth = isLandscape ? screenWidth : screenHeight;
        int frameBufferHeight = isLandscape ? screenHeight : screenWidth;
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Config.RGB_565);
        float scaleX = (float) frameBufferWidth / getWindowManager().getDefaultDisplay().getWidth();
        float scaleY = (float) frameBufferHeight / getWindowManager().getDefaultDisplay().getHeight();

        renderView = new AndroidFastRenderView(this, frameBuffer);
        graphics = new AndroidGraphics(getAssets(), frameBuffer);
        fileIO = new AndroidFileIO(getAssets());
        audio = new AndroidAudio(this);
        input = new AndroidInput(this, renderView, scaleX, scaleY);
        screen = getStartScreen();
        setContentView(renderView);
        
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");
    }

    @Override
    public void onResume() {
    	
        super.onResume();
        wakeLock.acquire();
        screen.resume();
        renderView.resume();
    }

    @Override
    public void onPause() {
    	
        super.onPause();
        wakeLock.release();
        renderView.pause();
        screen.pause();

        if (isFinishing()) {
        	
        	screen.dispose();
        }
    }

    public Input getInput() {
    	
        return input;
    }

    public FileIO getFileIO() {
    	
        return fileIO;
    }

    public Graphics getGraphics() {
    	
        return graphics;
    }

    public Audio getAudio() {
    	
        return audio;
    }

    public void setScreen(Screen screen) {
    	
        if (screen == null) {
        	
        	throw new IllegalArgumentException("Screen must not be null");
        }

        this.screen.pause();
        this.screen.dispose();
        screen.resume();
        screen.update(0);
        this.screen = screen;
    }
    
    public Screen getCurrentScreen() {
    	
        return screen;
    }
    
    public int getScreenWidth() {
    	
    	return screenWidth;
    }
    
    public int getScreenHeight() {
    	
    	return screenHeight;
    }
}