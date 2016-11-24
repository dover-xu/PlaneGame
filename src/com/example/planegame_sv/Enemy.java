package com.example.planegame_sv;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class Enemy {
	// �л��������ʶ
	public int type;
	// ��Ӭ
	public static final int TYPE_FLY = 1;
	// ��Ӭ����
	public boolean isDown = true;
	// Ѽ�ӣ����������˶���
	public static final int TYPE_DUCKL = 2;
	// Ѽ�ӣ����������˶���
	public static final int TYPE_DUCKR = 3;
	// �л�ͼƬ��Դ
	public Bitmap bmpEnemy;
	// �л�����
	public int x, y;
	// �л�ÿ֡�Ŀ��
	int frameW, frameH;
	// �л���ǰ֡�±�
	private int frameIndex;
	// �л����ƶ��ٶ�
	private int speed;
	// �жϵл��Ƿ��Ѿ�����
	public boolean isDead;
	// �л��Ĺ��캯��
	public Enemy(Bitmap bmpEnemy, int enemyType, int x, int y) {
		this.bmpEnemy = bmpEnemy;
		frameW = bmpEnemy.getWidth() / 10;
		frameH = bmpEnemy.getHeight();
		this.type = enemyType;
		this.x = x;
		this.y = y;
		// ��ͬ����ĵл��ٶȲ�ͬ
		switch (type) {
		// ��Ӭ
		case TYPE_FLY:
			speed = MySurfaceView.screenH / 40;		
			break;
		// Ѽ��
		case TYPE_DUCKL:
			speed = MySurfaceView.screenH / 213;
			break;
		case TYPE_DUCKR:
			speed = MySurfaceView.screenH / 213;
			break;
		default:
			break;
		}
	}
	// �л���ͼ����
	public void draw(Canvas canvas, Paint paint) {
		canvas.save();
		canvas.clipRect(x, y, x + frameW, y + frameH);
		canvas.drawBitmap(bmpEnemy, x - frameIndex * frameW, y, paint);
		canvas.restore();
	}
	// �л��߼�AI
	public void logic() {
		// ����ѭ������֡�γɶ���
		frameIndex++;
		if (frameIndex >= 10) {
			frameIndex = 0;
		}
		// ��ͬ����ĵл�ӵ�в�ͬ��AI�߼�
		switch (type) {
		case TYPE_FLY:
			if (isDead == false) {
				// ���ٳ��֣����ٷ���
				if (isDown) {
					speed -= MySurfaceView.screenH/800; //����
					if (speed < MySurfaceView.screenH/100)
						speed = MySurfaceView.screenH/100;
					y += speed;
					if (y >= MySurfaceView.screenH*90/100)
						isDown = false;
				} else {					
					speed += MySurfaceView.screenH/800; //����
					if (speed > MySurfaceView.screenH / 30)
						speed = MySurfaceView.screenH / 30;
					y -= speed;
				}
				
				if (y <= -frameH) {
					isDead = true;
				}
			}
			break;
		case TYPE_DUCKL:
			if (isDead == false) {
				// б���½��˶�
				x += speed / 2;
				y += speed;
				if (x > MySurfaceView.screenW || y > MySurfaceView.screenH) {
					isDead = true;
				}
			}
			break;
		case TYPE_DUCKR:
			if (isDead == false) {
				// б���½��˶�
				 x -= speed / 2;
				 y += speed;
				 if (x < 0 || y > MySurfaceView.screenH) {
					 isDead = true;
				 }
			}
			break;
		default:
			break;
		}
	}
	// �жϵл��������ӵ���ײ
	public boolean isCollisionWith(Bullet bullet) {
		int x2 = bullet.bulletX;
		int y2 = bullet.bulletY;
		int w2 = bullet.bmpBullet.getWidth();
		int h2 = bullet.bmpBullet.getHeight();
		if (x >= x2 && x >= x2 + w2) {
			return false;
		} else if (x <= x2 && x + frameW <= x2) {
			return false;			
		} else if (y >= y2 && y >= y2 + h2) {
			return false;
		} else if (y <= y2 && y + frameH <= y2) {
			return false;
		}
		// ������ײ����������
		isDead = true;
		return true;
	}
}
