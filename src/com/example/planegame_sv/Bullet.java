package com.example.planegame_sv;

import java.text.BreakIterator;

import android.R.bool;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class Bullet {
	// 子弹图片资源
	public Bitmap bmpBullet;
	// 子弹的坐标
	public int bulletX, bulletY;
	// 子弹的速度
	public int speed;
	// 子弹的种类及数量
	public int bulletType;
	// 主角的子弹
	public static final int BULLET_PLAYER = -1;
	// 鸭子的子弹
	public static final int BULLET_DUCK = 1;
	// 苍蝇的子弹
	public static final int BULLET_FLY = 2;
	// Boss的子弹
	public static final int BULLET_BOSS = 3;
	// 子弹是否超屏，优化处理
	public boolean isDead;
	// Boss疯狂状态下子弹相关成员变量
	private int dir; // 当前Boss子弹方向
	// 8方向常量
	public static final int DIR_UP = -1;
	public static final int DIR_DOWN = 2;
	public static final int DIR_LEFT = 3;
	public static final int DIR_RIGHT = 4;
	public static final int DIR_UP_LEFT = 5;
	public static final int DIR_UP_RIGHT = 6;
	public static final int DIR_DOWN_LEFT = 7;
	public static final int DIR_DOWN_RIGHT= 8;
	
	// 子弹构造函数
	public Bullet(Bitmap bmpBullet, int bulletX, int bulletY, int bulletType) {
		this.bmpBullet = bmpBullet;
		this.bulletX = bulletX;
		this.bulletY = bulletY;
		this.bulletType = bulletType;
		// 不同的子弹类型速度不一
		switch (bulletType) {
		case BULLET_PLAYER:
			speed = MySurfaceView.screenH / 80;
			break;
		case BULLET_DUCK:
			speed = MySurfaceView.screenH / 98;
			break;
		case BULLET_FLY:
			speed = MySurfaceView.screenH / 80;
			break;
		case BULLET_BOSS:
			speed = MySurfaceView.screenH / 64;
			break;			
		default:
			break;
		}
	}
	public Bullet(Bitmap bmpBullet, int bulletX, int bulletY, int bulletType, 
			int dir) {
		this.bmpBullet = bmpBullet;
		this.bulletX = bulletX;
		this.bulletY = bulletY;
		this.bulletType = bulletType;
		speed = MySurfaceView.screenH / 64;
		this.dir = dir;
	}
	// 子弹的绘制
	public void draw(Canvas canvas, Paint paint) {
		canvas.drawBitmap(bmpBullet, bulletX, bulletY, paint);		
	}
	// 子弹的逻辑
	public void logic() {
		// 不同的子弹类型逻辑不一
		// 主角的子弹垂直向上运动
		switch (bulletType) {
		case BULLET_PLAYER:
			bulletY -= speed;
			if (bulletY < -50) {
				isDead = true;
			}
			break;
		// 鸭子和苍蝇的子弹都是垂直下落
		case BULLET_DUCK:			
		case BULLET_FLY:
			bulletY += speed;
			if (bulletY > MySurfaceView.screenH) {
				isDead = true;
			}
			break;
		//　Boss疯狂状态下的8方向子弹逻辑
		case BULLET_BOSS:
			switch (dir) {
			case DIR_UP:
				bulletY -= speed;
				break;
			case DIR_DOWN:
				bulletY += speed;
				break;
			case DIR_LEFT:
				bulletX -= speed;
				break;
			case DIR_RIGHT:
				bulletX += speed;
					break;
			case DIR_UP_LEFT:
				bulletX -= speed;
				bulletY -= speed;
				break;
			case DIR_UP_RIGHT:
				bulletX += speed;
				bulletY -= speed;
				break;
			case DIR_DOWN_LEFT:
				bulletX -= speed;
				bulletY += speed;
				break;
			case DIR_DOWN_RIGHT:
				bulletX += speed;
				bulletY += speed;
				break;
			default:
				break;
			}
			// 边界处理
			if (bulletY > MySurfaceView.screenH || bulletY <= -50 ||
					bulletX > MySurfaceView.screenW || bulletX < -50) {
				isDead = true;
			}
			break;		
		default:
			break;
		}
	}
	
	
	
}
