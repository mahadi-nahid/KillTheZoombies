package com.sust.game;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.os.Bundle;
import android.renderscript.Font;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HighScore extends Activity implements OnClickListener {

    TextView[] tv;
    Button backButton;
    String sql;
    Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_high_score);
	tv = new TextView[5];


	tv[0] = (TextView) findViewById(R.id.textView1);
	tv[1] = (TextView) findViewById(R.id.textView2);
	tv[2] = (TextView) findViewById(R.id.textView3);
	tv[3] = (TextView) findViewById(R.id.textView4);
	tv[4] = (TextView) findViewById(R.id.textView5);

	

	backButton = (Button) findViewById(R.id.button1);
	backButton.setOnClickListener(this);

	init();
    }

    public void init() {
	DB db = new DB(this);
	ArrayList<Data> list = db.readData();
	for (int i = 0; i < 5; i++) {
	    try {
		Data data = list.get(i);
		tv[i].setText((i + 1) + "." + data.getName() + " "
			+ data.getScore());
		Log.e("A", data.getName() + ":" + data.getScore());
	    } catch (Exception e) {
		break;
	    }
	}
	db.closeDataBase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.activity_high_score, menu);
	return true;
    }

    @Override
    public void onClick(View v) {
	if (v.getId() == R.id.button1) {
	    Intent intent = new Intent(HighScore.this, MenuActivity.class);
	    startActivity(intent);
	    finish();
	}
    }

}
