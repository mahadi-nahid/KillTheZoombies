package com.sust.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("WrongCall")
public class Game extends Activity implements SensorEventListener {

    private static final int[] DIRECTION_TO_ANIMATION_MAP = { 3, 1, 0, 2 };

    // ---------------------
    float ballX;
    float ballY;
    float sensorX;
    float sensorY;
    float centerX;
    float centerY;
    // float psx = 0;
    // float psy = 0;
    // --------------------
    Bitmap ball;

    SensorManager sensorManager;

    GameView gameView;

    // ------------------------
    public GameThread gameLoopThread;

    private final int xSpeed = 0;
    private final int ySpeed = 0;

    private int numOfGoods;
    private int numOfZombies;
    private int level;
    private int score;
    private int life;
    private int killedZombies = 0;
    private MediaPlayer mp;

    private int[] zombiesID = { R.drawable.zombie1, R.drawable.zombie2,
	    R.drawable.zombie3, R.drawable.zombie4, R.drawable.zombie5,
	    R.drawable.zombie6 };
    private int[] goodsID = { R.drawable.good1, R.drawable.good2,
	    R.drawable.good3, R.drawable.good4, R.drawable.good5,
	    R.drawable.good6 };

    // -------------------------

    // -----------------------------------
    public class GameCharecter {

	private static final int BMP_ROWS = 4;
	private static final int BMP_COLUMNS = 3;

	// direction = 0 up, 1 left, 2 down, 3 right,
	// animation = 3 back, 1 left, 0 front, 2 right
	//

	private final GameView gameView;
	private final Bitmap bmp;
	private int charecterX;
	private int charecterY;
	private int xSpeed;
	private int ySpeed;
	private static final int MAX_SPEED = 5;

	private final int width;
	private final int height;
	private int currentFrame = 0;

	public GameCharecter(GameView gameView, Bitmap bmp) {
	    this.gameView = gameView;
	    this.bmp = bmp;

	    this.width = bmp.getWidth() / BMP_COLUMNS;
	    this.height = bmp.getHeight() / BMP_ROWS;

	    Random rnd = new Random();
	    charecterX = rnd.nextInt(gameView.getWidth() - width);
	    charecterY = rnd.nextInt(gameView.getHeight() - height);

	    xSpeed = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;
	    ySpeed = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;
	}

	private void update() {
	    if (charecterX > gameView.getWidth() - width - xSpeed
		    || charecterX + xSpeed < 0) {
		xSpeed = -xSpeed; // change the direction
	    }
	    charecterX = charecterX + xSpeed;
	    if (charecterY > gameView.getHeight() - height - ySpeed
		    || charecterY + ySpeed < 0) {
		ySpeed = -ySpeed; // change the direction...
	    }
	    charecterY = charecterY + ySpeed;

	    currentFrame = ++currentFrame % BMP_COLUMNS; // update the image
	    // frame
	    // ..
	}

	public void onDraw(Canvas canvas) {
	    update(); // update the coordinate x and y , current frame ..

	    int srcX = currentFrame * width;
	    int srcY = getAnimationRow() * height;

	    // two rectangle source and destination....
	    Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
	    Rect dst = new Rect(charecterX, charecterY, charecterX + width,
		    charecterY + height);

	    canvas.drawBitmap(bmp, src, dst, null); // draw the bmp image....
	}

	// direction = 0 up, 1 left, 2 down, 3 right,
	// animation = 3 back, 1 left, 0 front, 2 right
	private int getAnimationRow() {
	    double dirDouble = (Math.atan2(xSpeed, ySpeed) / (Math.PI / 2) + 2);
	    int direction = (int) Math.round(dirDouble) % BMP_ROWS;
	    return DIRECTION_TO_ANIMATION_MAP[direction];
	}

	public boolean isCollition(float x2, float y2) {
	    return x2 + ball.getWidth() > charecterX && x2 < charecterX + width
		    && y2 + ball.getHeight() > charecterY
		    && y2 < charecterY + height;
	}

    }

    // ---------------------------------------------
    public class GameView extends SurfaceView {

	private SurfaceHolder holder;

	private List<GameCharecter> zombies;

	private List<GameCharecter> goods;

	private List<Blood> bloods;

	private long lastClick;

	private Bitmap bloodImage;

	public GameView(Context context) {
	    super(context);
	    bloods = new ArrayList<Blood>();
	    zombies = new ArrayList<GameCharecter>();
	    goods = new ArrayList<GameCharecter>();
	    gameLoopThread = new GameThread(GameView.this);

	    holder = getHolder();
	    holder.addCallback(new SurfaceHolder.Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		    boolean retry = true;
		    gameLoopThread.setRunning(false);
		    while (retry) {
			try {
			    gameLoopThread.join();
			    retry = false;
			} catch (InterruptedException e) {
			}
		    }

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		    createAllZombies(numOfGoods); // creating all zombies and
						  // other
		    // images

		    gameLoopThread.setRunning(true);
		    gameLoopThread.start();

		    // sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		    // explosion = sp.load(this, R.raw.a,// 1);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format,
			int width, int height) {
		}
	    });

	    bloodImage = BitmapFactory.decodeResource(getResources(),
		    R.drawable.blood1);

	}

	private void createAllZombies(int n) {
	    // zombies is an ArrayList....add is a build in method ...

	    numOfGoods = n;
	    numOfZombies = 3 * numOfGoods + 1;

	    // goods
	    for (int i = 1; i <= numOfGoods; i++) {
		goods.add(createZombie(goodsID[i % 6]));
	    }
	    // zombies
	    for (int i = 1; i <= numOfZombies; i++) {
		zombies.add(createZombie(zombiesID[i % 6]));
	    }

	}

	private GameCharecter createZombie(int resouce) {
	    Bitmap bmp = BitmapFactory.decodeResource(getResources(), resouce);
	    return new GameCharecter(this, bmp);
	}

	private void newLevel(int level) {

	    for (GameCharecter zombie : zombies) {
		zombies.remove(zombie);
	    }
	    for (GameCharecter good : goods) {
		goods.remove(good);
	    }

	    createAllZombies(level + 1);

	}

	@Override
	protected void onDraw(Canvas canvas) {
	    try {
		canvas.drawColor(Color.WHITE);
		handler.post(new Runnable() {
		    @Override
		    public void run() {
			if (menu != null) {
			    MenuItem mnuPageIndex = menu.findItem(R.id.score);
			    if (mnuPageIndex != null) {
				mnuPageIndex.setTitle(getScore());
			    }
			}

		    }
		});
	    } catch (Exception e) {

	    }

	    // ==========================================================
	    try {
		for (Blood blood : bloods) {
		    blood.onDraw(canvas);
		}
	    } catch (Exception e) {

	    }
	    try {

		isStarted = true;

		if (zombies.isEmpty()) {

		    if (isStarted) {
			isStarted = false;

			newLevel(level);
			level = level + 1;

			// finish();
		    }
		    gameLoopThread.interrupt();

		    // ---------------------------------------------------------------------
		}

		if (life == 0) {
		    if (isStarted) {
			isStarted = false;
			intent = new Intent(Game.this, GameOverActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("score", score);
			// intent.putExtra("level", level);
			startActivity(intent);

		    }

		    gameLoopThread.interrupt();
		    finish();
		}

		// --------------------------------------------

		ballX = (float) (ballX + sensorX * 1.5);
		ballY = (float) (ballY + sensorY * 1.5);

		if (ballX < 0)
		    ballX = 0;
		if (ballY < 0)
		    ballY = 0;
		if (ballX > canvas.getWidth() - ball.getWidth())
		    ballX = canvas.getWidth() - ball.getWidth();
		if (ballY > canvas.getHeight() - ball.getHeight())
		    ballY = canvas.getHeight() - ball.getHeight();

		canvas.drawBitmap(ball, ballX, ballY, null);

		for (GameCharecter zombie : zombies) {
		    zombie.onDraw(canvas);
		}
		for (GameCharecter good : goods) {
		    good.onDraw(canvas);
		}

		for (GameCharecter zombie : zombies) {

		    if (zombie.isCollition(ballX, ballY)) {
			new Thread(new Runnable() {
			    @Override
			    public void run() {
				try {
				    mp = MediaPlayer.create(Game.this,
					    R.raw.collision);
				    mp.start();
				    Thread.sleep(2000);
				    mp.stop();
				    mp.release();
				} catch (Exception e) {
				    // TODO: handle exception
				}
			    }
			}).start();
			score += 10;
			zombies.remove(zombie);
			killedZombies = killedZombies + 1;

			if (killedZombies == 5) {
			    life++;
			    killedZombies = 0;
			}

			bloods.add(new Blood(bloods, gameView,
				zombie.charecterX, zombie.charecterY,
				bloodImage));
			break;
		    }
		}

		for (GameCharecter good : goods) {

		    if (good.isCollition(ballX, ballY)) {
			new Thread(new Runnable() {
			    @Override
			    public void run() {
				try {
				    mp = MediaPlayer.create(Game.this,
					    R.raw.collision);

				    mp.start();
				    Thread.sleep(2000);
				    mp.stop();
				    mp.release();
				} catch (Exception e) {
				    // TODO: handle exception
				}
			    }
			}).start();
			score -= 10;
			life--;
			goods.remove(good);

			bloods.add(new Blood(bloods, gameView, good.charecterX,
				good.charecterY, bloodImage));
			break;
		    }
		}

	    } catch (Exception e) {

	    }
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

	    if (System.currentTimeMillis() - lastClick > 500) {
		lastClick = System.currentTimeMillis();
		float x = event.getX();
		float y = event.getY();
		synchronized (getHolder()) {
		    for (int i = zombies.size() - 1; i >= 0; i--) {
			GameCharecter zombie = zombies.get(i);
			if (zombie.isCollition(x, y)) {
			    score += 10;
			    zombies.remove(zombie);
			    bloods.add(new Blood(bloods, this, x, y, bloodImage));
			    killedZombies = killedZombies + 1;

			    if (killedZombies == 7) {
				life++;
				killedZombies = 0;
			    }

			    break;
			}

		    }

		    // ----------------------

		    for (int i = goods.size() - 1; i >= 0; i--) {
			GameCharecter good = goods.get(i);
			if (good.isCollition(event.getX(), event.getY())) {

			    life--;
			    goods.remove(good);

			    break;
			}

		    }
		}

		// ------------------
	    }

	    return true;
	}

    }

    // -------------------------------------

    // ------------OnActivityResults------------------------

    // -------------------------SensorEventListener----------------
    private Handler handler;
    private Intent intent;
    boolean isStarted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	isStarted = true;
	super.onCreate(savedInstanceState);
	handler = new Handler();
	// ------------------------
	sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
	    Sensor sensor = sensorManager.getSensorList(
		    Sensor.TYPE_ACCELEROMETER).get(0);
	    sensorManager.registerListener(this, sensor,
		    SensorManager.SENSOR_DELAY_NORMAL);
	}

	ball = BitmapFactory.decodeResource(getResources(),
		R.drawable.ic_launcher);

	killedZombies = 0;
	level = 1;
	score = 0;
	numOfGoods = getIntent().getIntExtra("n", 0);
	level = getIntent().getIntExtra("level", 0);
	score = getIntent().getIntExtra("score", 0);
	life = getIntent().getIntExtra("life", 0);
	killedZombies = getIntent().getIntExtra("killedZombies", 0);

	ballX = ballY = sensorX = sensorY = 0;

	// ------------------------
	gameView = new GameView(this);
	setContentView(gameView);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @Override
    public void onSensorChanged(SensorEvent e) {

	try {
	    Thread.sleep(16);
	} catch (InterruptedException e1) {
	    e1.printStackTrace();
	}
	sensorX = e.values[1];
	sensorY = e.values[0];
    }

    private Menu menu;

    // ------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.option_menu, menu);
	this.menu = menu;
	return true;
    }

    public String getScore() {
	return "Score :" + score + "  Life :" + life + " Level :" + level;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

	if (item.getItemId() == R.id.about) {
	    Intent intent = new Intent(Game.this, AboutActivity.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	    finish();
	} else if (item.getItemId() == R.id.highScore) {
	    Intent intent = new Intent(Game.this, HighScore.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	    finish();
	} else if (item.getItemId() == R.id.exit) {
	    Log.d("Option", "Exit is clicked..");

	    AlertDialog.Builder builder = new AlertDialog.Builder(Game.this);
	    builder.setMessage("Are You Sure You Want To Exit?");

	    builder.setCancelable(false);
	    builder.setPositiveButton("Yes",
		    new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			    Intent intent = new Intent(Game.this,
				    MenuActivity.class);
			    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    startActivity(intent);
			    finish();
			}
		    });

	    builder.setNegativeButton("No",
		    new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			    dialog.cancel();
			}
		    });

	    AlertDialog alert = builder.create();
	    alert.show();
	}
	return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
	// TODO Auto-generated method stub
	super.onBackPressed();
	if (isStarted) {
	    isStarted = false;
	    intent = new Intent(Game.this, MenuActivity.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	    finish();
	}
    }
}
