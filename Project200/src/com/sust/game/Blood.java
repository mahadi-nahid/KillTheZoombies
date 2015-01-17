package com.sust.game;

import java.util.List;

import com.sust.game.Game.GameView;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Blood {
    
    private float x;
    private float y;
    private static int life = 5;
    private Bitmap bmp;
    private List<Blood> bloods;
    
    
    public Blood(List<Blood> bloods, GameView gameView, 
	    float x, float y, Bitmap bmp){
	
	this.x = Math.min(Math.max(x-bmp.getWidth()/2, 0),
		gameView.getWidth()-bmp.getWidth());
	this.y = Math.min(Math.max(y-bmp.getHeight()/2,0),
		gameView.getHeight() - bmp.getHeight());
	
	this.bmp = bmp;
	this.bloods = bloods;
    }
    
    
    public void onDraw(Canvas canvas){
	update();
	canvas.drawBitmap(bmp, x, y, null);
    }
    
    private void update(){
	if(--life < 1){
	    bloods.remove(this);
	}
    }

}
