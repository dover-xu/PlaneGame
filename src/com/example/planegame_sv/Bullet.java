package com.example.planegame_sv;

import java.text.BreakIterator;

import android.R.bool;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class Bullet {
	// �ӵ�ͼƬ��Դ
	public Bitmap bmpBullet;
	// �ӵ�������
	public int bulletX, bulletY;
	// �ӵ����ٶ�
	public int speed;
	// �ӵ������༰����
	public int bulletType;
	// ���ǵ��ӵ�
	public static final int BULLET_PLAYER = -1;
	// Ѽ�ӵ��ӵ�
	public static final int BULLET_DUCK = 1;
	// ��Ӭ���ӵ�
	public static final int BULLET_FLY = 2;
	// Boss���ӵ�
	public static final int BULLET_BOSS = 3;
	// �ӵ��Ƿ������Ż�����
	public boolean isDead;
	// Boss���״̬���ӵ���س�Ա����
	private int dir; // ��ǰBoss�ӵ�����
	// 8������
	public static final int DIR_UP = -1;
	public static final int DIR_DOWN = 2;
	public static final int DIR_LEFT = 3;
	public static final int DIR_RIGHT = 4;
	public static final int DIR_UP_LEFT = 5;
	public static final int DIR_UP_RIGHT = 6;
	public static final int DIR_DOWN_LEFT = 7;
	public static final int DIR_DOWN_RIGHT= 8;
	
	// �ӵ����캯��
	public Bullet(Bitmap bmpBullet, int bulletX, int bulletY, int bulletType) {
		this.bmpBullet = bmpBullet;
		this.bulletX = bulletX;
		this.bulletY = bulletY;
		this.bulletType = bulletType;
		// ��ͬ���ӵ������ٶȲ�һ
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
	// �ӵ��Ļ���
	public void draw(Canvas canvas, Paint paint) {
		canvas.drawBitmap(bmpBullet, bulletX, bulletY, paint);		
	}
	// �ӵ����߼�
	public void logic() {
		// ��ͬ���ӵ������߼���һ
		// ���ǵ��ӵ���ֱ�����˶�
		switch (bulletType) {
		case BULLET_PLAYER:
			bulletY -= speed;
			if (bulletY < -50) {
				isDead = true;
			}
			break;
		// Ѽ�ӺͲ�Ӭ���ӵ����Ǵ�ֱ����
		case BULLET_DUCK:			
		case BULLET_FLY:
			bulletY += speed;
			if (bulletY > MySurfaceView.screenH) {
				isDead = true;
			}
			break;
		//��Boss���״̬�µ�8�����ӵ��߼�
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
			// �߽紦��
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
