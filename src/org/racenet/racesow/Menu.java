package org.racenet.racesow;

import java.util.ArrayList;
import java.util.List;

import org.racenet.framework.GLGame;
import org.racenet.framework.TexturedBlock;

import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class Menu implements GestureDetector.OnGestureListener {

	List<TexturedBlock> items = new ArrayList<TexturedBlock>();
	List<Callback> callbacks = new ArrayList<Callback>();
	GLGame game;
	float viewWidth;
	float viewHeight;
	float spaceWidth = 5;
	float velocity = 0;
	
	public class Callback {
		
		public void handle() {}
	}
	
	public Menu(GLGame game, float viewWidth, float viewHeight) {
		
		if (!Racesow.LOOPER_PREPARED) {
			
			Racesow.LOOPER_PREPARED = true;
			Looper.prepare();
		}
		
		this.game = game;
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
	}
	
	public void addItem(String texture, Callback callback) {
		
		/*
		TexturedBlock item = new TexturedBlock(this.game, 0, 0, this.viewWidth / 3, -1, texture, TexturedBlock.FUNC_NONE, -1, -1);
		item.position.y = this.viewHeight / 2 - item.bounds.height / 2 - this.viewHeight / 5;
		
		float posX = 0;
		int length = this.items.size();
		for (int i = 0; i < length; i++) {
			
			posX += this.items.get(i).bounds.width + this.spaceWidth;
		}
		
		item.position.x = posX;
		
		this.items.add(item);
		this.callbacks.add(callback);
		*/
	}
	
	public boolean onDown(MotionEvent event) {
		
		this.velocity = 0;
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				
		if (!this.allowMoveMenu(velocityX)) {
			
			return false;
		} 
		
		this.velocity = velocityX;
		
		return true;
	}

	public void onLongPress(MotionEvent arg0) {
		
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		
		return this.moveMenu(distanceX);
	}

	public void onShowPress(MotionEvent arg0) {
		
	}

	public boolean onSingleTapUp(MotionEvent event) {

		this.buttonPress(event.getX(), this.viewHeight - event.getY());
		return false;
	}
	
	public void buttonPress(float x, float y) {
		
		int length = this.items.size();
		for (int i = 0; i < length; i++) {
			
			TexturedBlock item = this.items.get(i);
			if (x > item.position.x && x < item.position.x + item.bounds.getWidth() &&
				y > item.position.y && y < item.position.y + item.bounds.getHeight()) {
				
				this.callbacks.get(i).handle();
				break;
			}
		}
	}
	
	public boolean allowMoveMenu(float distance) {
		
		TexturedBlock first = this.items.get(0);
		TexturedBlock last = this.items.get(this.items.size() - 1);
		
		if ((first.position.x > this.viewWidth / 2 - first.bounds.getWidth() / 2 && distance < 0) ||
			(last.position.x < this.viewWidth / 2 - last.bounds.getWidth() / 2 && distance > 0)) {
			
			return false;
		
		} else {
		
			return true;
		}
	}
	
	public boolean moveMenu(float distance) {
		
		if (!this.allowMoveMenu(distance)) {
			
			return false;
		}
		
		int length = this.items.size();
		for (int i = 0; i < length; i++) {
			
			this.items.get(i).position.x -= distance;
		}
		
		return true;
	}
	
	public void update(float deltaTime) {
		
		if (this.velocity != 0) {
			
			this.moveMenu(-this.velocity  * deltaTime);
			this.velocity = this.velocity / 1.025f;
		}
	}
	
	public void draw() {
		
		int length = this.items.size();
		for (int i = 0; i < length; i++) {
			
			this.items.get(i).draw();
		}
	}
	
	public void reloadTextures() {
		
		int length = this.items.size();
		for (int i = 0; i < length; i++) {
			
			this.items.get(i).reloadTexture();
		}
	}
	
	public void dispose() {
		
		int length = this.items.size();
		for (int i = 0; i < length; i++) {
			
			this.items.get(i).dispose();
		}
	}
}
