package com.sust.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class GameOverActivity extends Activity implements OnClickListener {

	Button		saveButton;
	Button		skipButton;
	EditText	nameEditText;
	Intent		intent;
	String		name;
	int			score;
	// int level;
	Boolean		isStarted;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_over);
		saveButton = (Button) findViewById(R.id.game_over_save_button);
		skipButton = (Button) findViewById(R.id.game_over_skip_button);
		nameEditText = (EditText) findViewById(R.id.game_over_name_et);

		saveButton.setOnClickListener(this);
		skipButton.setOnClickListener(this);
		// level = getIntent().getIntExtra("level", 0);
		score = getIntent().getIntExtra("score", 0);

		isStarted = true;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_game_over, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == R.id.game_over_save_button) {
			DB db = new DB(this);
			Data data = new Data(nameEditText.getText().toString(), score);
			db.saveData(data);
			db.closeDataBase();
			if (isStarted) {
				isStarted = false;
				intent = new Intent(GameOverActivity.this, MenuActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}

		} else if (v.getId() == R.id.game_over_skip_button) {

			if (isStarted) {
				isStarted = false;
				intent = new Intent(GameOverActivity.this, MenuActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		intent = new Intent(GameOverActivity.this, MenuActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();

	}

}
