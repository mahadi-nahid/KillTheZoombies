package com.sust.game;

import com.sust.game.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FirstActivity extends Activity {
    private SystemUiHider mSystemUiHider;
    private Thread thread;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_first);
	final View contentView = findViewById(R.id.fullscreen_content);
	mSystemUiHider = SystemUiHider.getInstance(this, contentView,
		SystemUiHider.FLAG_HIDE_NAVIGATION);
	mSystemUiHider.setup();
	mSystemUiHider.hide();

	new Thread(new Runnable() {
	    @Override
	    public void run() {// for sound
		try {
		    mp = MediaPlayer.create(FirstActivity.this, R.raw.first);
		    mp.start();
		    Thread.sleep(2000);

		    mp.stop();
		    mp.release();
		} catch (Exception e) {
		    // TODO: handle exception
		}
	    }
	}).start();

	thread = new Thread() {
	    @Override
	    public void run() {
		try {
		    synchronized (this) {
			// Wait given period of time or exit on touch
			wait(3000);
		    }
		} catch (InterruptedException ex) {
		    ex.printStackTrace();
		}

		// Run next activity
		Intent intent = new Intent();
		//intent.setClass(FirstActivity.this, MenuActivity.class);

		intent.setClass(FirstActivity.this, MenuActivity.class);
		startActivity(intent);
		finish();
		interrupt();
	    }
	};

	thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt) {
	if (evt.getAction() == MotionEvent.ACTION_DOWN) {
	    synchronized (thread) {
		thread.notifyAll();
	    }
	}
	return true;
    }
}
