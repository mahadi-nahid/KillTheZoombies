package com.sust.game;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MenuActivity extends Activity implements OnClickListener {

	ImageButton startButton;
	ImageButton hsButton;
	ImageButton insButton;
	ImageButton abtButton;
	ImageButton exitButton;
	MediaPlayer mp;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_m);

		startButton = (ImageButton) findViewById(R.id.imageButton1);
		hsButton = (ImageButton) findViewById(R.id.imageButton2);
		insButton = (ImageButton) findViewById(R.id.imageButton3);
		abtButton = (ImageButton) findViewById(R.id.imageButton4);
		exitButton = (ImageButton) findViewById(R.id.imageButton5);

		startButton.setOnClickListener(this);
		hsButton.setOnClickListener(this);
		insButton.setOnClickListener(this);
		abtButton.setOnClickListener(this);
		exitButton.setOnClickListener(this);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mp = MediaPlayer.create(MenuActivity.this, R.raw.start);
					mp.start();
					Thread.sleep(2000);

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_m, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.imageButton1) {

			intent = new Intent(MenuActivity.this, Game.class);
			intent.putExtra("n", 1);
			intent.putExtra("life", 3);
			intent.putExtra("score", 0);
			intent.putExtra("level", 1);
			intent.putExtra("killedZombies", 0);
			startActivity(intent);
			try {
				mp.stop();
				mp.release();
			} catch (Exception e) {
				// TODO: handle exception
			}
			finish();
		} else if (v.getId() == R.id.imageButton2) {

			Intent intent = new Intent(MenuActivity.this, HighScore.class);
			startActivity(intent);
			try {
				mp.stop();
				mp.release();
			} catch (Exception e) {
				// TODO: handle exception
			}
			finish();

		} else if (v.getId() == R.id.imageButton3) {

			Intent intent = new Intent(MenuActivity.this, GameInstruction.class);
			startActivity(intent);
			try {
				mp.stop();
				mp.release();
			} catch (Exception e) {
				// TODO: handle exception
			}
			finish();
		} else if (v.getId() == R.id.imageButton4) {

			Intent intent = new Intent(MenuActivity.this, AboutActivity.class);
			startActivity(intent);
			try {
				mp.stop();
				mp.release();
			} catch (Exception e) {
				// TODO: handle exception
			}
			finish();
		} else if (v.getId() == R.id.imageButton5) {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		try {
			mp.stop();
			mp.release();
		} catch (Exception e) {
			// TODO: handle exception
		}
		finish();
	}

	// --------------------------------------------
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			mp.stop();
			mp.release();
		} catch (Exception e) {
			// TODO: handle exception
		}
		finish();
	}
}
