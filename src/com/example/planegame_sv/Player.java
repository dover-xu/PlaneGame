package com.example.planegame_sv;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class Player {
	// 主角的血量与血量位图
	// 默认3血
	private int playerHp = 3;
	private Bitmap bmpPlayerHp;
	// 主角的坐标以及位图
	public int x, y;
	public Bitmap bmpPlayer;
	// 主角移动速度
	private int speed = 15;
	// 主角移动标识
	private boolean isUp, isDown, isLeft, isRight;
	// 碰撞后处于无敌时间
	// 计时器
	private int noCollisionCount = 0;
	// 无敌时间
	private int noCollisionTime = 60;
	// 是否碰撞的标识位
	private boolean isCollision;
	
	// 主角的构造函数
	public Player(Bitmap bmpPlayer, Bitmap bmpPlayerHp) {
		this.bmpPlayer = bmpPlayer;
		this.bmpPlayerHp = bmpPlayerHp;
		x = MySurfaceView.screenW / 2 - bmpPlayer.getWidth() / 2;
		y = MySurfaceView.screenH - bmpPlayer.getHeight();
	}
	// 主角的绘图函数
	public void draw(Canvas canvas, Paint paint) {
		// 绘制主角
		//当主角处于无敌时间，让主角闪烁
		if (isCollision) {
			// 每2次游戏循环，绘制一次主角
			if (noCollisionCount % 2 == 0) {
				canvas.drawBitmap(bmpPlayer, x, y, paint);
			}
		} else {
			canvas.drawBitmap(bmpPlayer, x, y, paint);
		}
		// 绘制主角血量
		for (int i=0; i<playerHp; i++) {
			canvas.drawBitmap(bmpPlayerHp, i * bmpPlayerHp.getWidth(), 
					MySurfaceView.screenH - bmpPlayerHp.getHeight(), paint);
		}
	}
	// 按键按下
	public void buttonDown(View v) {
		switch (v.getId()) {
		case R.id.btnUp:
			isUp = true;
			break;
		case R.id.btnDown:
			isDown = true;
			break;
		case R.id.btnLeft:
			isLeft = true;
			break;
		case R.id.btnRight:
			isRight = true;
			break;
		default:
			break;
		}
	}		
	// 按键弹起
	public void buttonUp(View v) {
		switch (v.getId()) {
		case R.id.btnUp:
			isUp = false;
			break;
		case R.id.btnDown:
			isDown = false;
			break;
		case R.id.btnLeft:
			isLeft = false;
			break;
		case R.id.btnRight:
			isRight = false;
			break;
		default:
			break;
		}	
	}
	// 主角的逻辑
	public void logic() {
		// 处理主角移动
		if (isLeft) {
			x -= speed;
		}
		if (isRight) {
			x += speed;
		}
		if (isUp) {
			y -= speed;			
		}
		if (isDown) {
			y += speed;
		}
		// 判断屏幕X边界
		if (x + bmpPlayer.getWidth() >= MySurfaceView.screenW) {
			x = MySurfaceView.screenW - bmpPlayer.getWidth();
		} else if (x < 0) {
			x = 0;
		}
		// 判断屏幕Y边界
		if (y + bmpPlayer.getHeight() >= MySurfaceView.screenH) {
			y = MySurfaceView.screenH - bmpPlayer.getHeight();
		} else if (y < 0) {
			y = 0;
		}		
		//处于无敌状态
		if (isCollision) {
			// 计时器开始计时
			noCollisionCount++;
			if (noCollisionCount >= noCollisionTime) {
				// 无敌时间过后，结束无敌状态，初始化计数器
				isCollision = false;
				noCollisionCount = 0;
			}
		}
	}
	// 设置主角血量
	public void setPlayerHp(int hp) {
		this.playerHp = hp;
	}
	// 获取主角血量
	public int getPlayerHp() {
		return playerHp;
	}
	// 判断碰撞（主角与敌机）
	public boolean isCollisionWith(Enemy en) {
		if (!isCollision) {
			int x2 = en.x;
			int y2 = en.y;
			int w2 = en.frameW;
			int h2 = en.frameH;
			if (x >= x2 && x >= x2 +w2) {
				return false;
			} else if (x <= x2 && x + bmpPlayer.getWidth() <= x2) {
				return false;
			} else if (y > y2 && y > y2 + h2) {
				return false;
			} else if (y < y2 && y + bmpPlayer.getHeight() <= y2) {
				return false;
			}
			// 碰撞即处于无敌状态			
			isCollision = true;
			return true;		
		} else {
			return false;
		}
	}
	// 判断碰撞（主角与敌机子弹）
	public boolean isCollisionWith(Bullet bullet) {
		if (!isCollision) {
			int x2 = bullet.bulletX;
			int y2 = bullet.bulletY;
			int w2 = bullet.bmpBullet.getWidth();
			int h2 = bullet.bmpBullet.getHeight();
			if (x >= x2 && x >= x2 +w2) {
				return false;
			} else if (x <= x2 && x + bmpPlayer.getWidth() <= x2) {
				return false;
			} else if (y > y2 && y > y2 + h2) {
				return false;
			} else if (y < y2 && y + bmpPlayer.getHeight() <= y2) {
				return false;
			}
			// 碰撞即处于无敌状态			
			isCollision = true;
			return true;		
		} else {
			return false;
		}
	}	
	
	
}
