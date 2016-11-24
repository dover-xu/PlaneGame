package com.example.planegame_sv;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class Boss {	
	// Boss��Ѫ��
	public int hp = 20;
	// Boss��ͼƬ��Դ
	private Bitmap bmpBoss;
	// Boss����
	public int x, y;
	// Bossÿ֡�Ŀ��
	public int frameW, frameH;
	// Boss��ǰ֡�±�
	private int frameIndex;
	// Boss�˶����ٶ�
	private int speed = MySurfaceView.screenH / 64;
	// Boss���˶��켣
	// һ��ʱ���������Ļ�·��˶������ҷ����Χ�ӵ�
	// ����״̬�£��ӵ���ֱ�����˶�
	private boolean isCrazy;
	// ������״̬��ʱ����
	private int crazyTime = MySurfaceView.screenW / 8;
	// ������
	private int count;
	// �����������
	Random rand = new Random();
	 
	public Boss(Bitmap bmpBoss) {
		this.bmpBoss = bmpBoss;
		frameW = bmpBoss.getWidth() / 10;
		frameH = bmpBoss.getHeight();
		// Boss��X�������
		x = MySurfaceView.screenW / 2 - frameW / 2;
		y = frameH;
	}
	// Boss�Ļ���
	public void draw(Canvas canvas, Paint paint) {
		canvas.save();
		canvas.clipRect(x, y, x + frameW, y + frameH);
		canvas.drawBitmap(bmpBoss, x - frameIndex * frameW, y, paint);
		canvas.restore();
	}
	// Boss���߼�
	public void logic() {
		// ����ѭ������֡�γɶ���
		frameIndex++;
		if (frameIndex >= 10) {
			frameIndex = 0;
		}
		// û�з���״̬
		if (isCrazy == false) {
			x += speed;
			if (x + frameW >= MySurfaceView.screenW) {
				speed = -speed;
			} else if (x <= -frameW / 2) {
				speed = -speed;
			}
			count++;
									
			if (count % crazyTime == 0) {	
				count = 0;
				isCrazy = true;
				speed = MySurfaceView.screenH / 16;
			}
		// ����״̬			
		} else {
			speed -= MySurfaceView.screenH / 400;
			// ��Boss����ʱ���������ӵ�
			if (speed >= 0 && speed < MySurfaceView.screenH / 400) {
				// ���8�����ӵ�
				MySurfaceView.vcBulletBoss.add(new Bullet(MySurfaceView.bmpBossBullet, 
						x + frameW / 2, y + frameH / 2, Bullet.BULLET_BOSS, Bullet.DIR_UP));
				MySurfaceView.vcBulletBoss.add(new Bullet(MySurfaceView.bmpBossBullet, 
						x + frameW / 2, y + frameH / 2, Bullet.BULLET_BOSS, Bullet.DIR_DOWN));
				MySurfaceView.vcBulletBoss.add(new Bullet(MySurfaceView.bmpBossBullet, 
						x + frameW / 2, y + frameH / 2, Bullet.BULLET_BOSS, Bullet.DIR_LEFT));
				MySurfaceView.vcBulletBoss.add(new Bullet(MySurfaceView.bmpBossBullet, 
						x + frameW / 2, y + frameH / 2, Bullet.BULLET_BOSS, Bullet.DIR_RIGHT));
				MySurfaceView.vcBulletBoss.add(new Bullet(MySurfaceView.bmpBossBullet, 
						x + frameW / 2, y + frameH / 2, Bullet.BULLET_BOSS, Bullet.DIR_UP_LEFT));
				MySurfaceView.vcBulletBoss.add(new Bullet(MySurfaceView.bmpBossBullet, 
						x + frameW / 2, y + frameH / 2, Bullet.BULLET_BOSS, Bullet.DIR_UP_RIGHT));
				MySurfaceView.vcBulletBoss.add(new Bullet(MySurfaceView.bmpBossBullet, 
						x + frameW / 2, y + frameH / 2, Bullet.BULLET_BOSS, Bullet.DIR_DOWN_LEFT));
				MySurfaceView.vcBulletBoss.add(new Bullet(MySurfaceView.bmpBossBullet, 
						x + frameW / 2, y + frameH / 2, Bullet.BULLET_BOSS, Bullet.DIR_DOWN_RIGHT));
			}
			y += speed;
			if (y <= frameH) {
				// �ָ�����״̬
				isCrazy = false;				
				int r = rand.nextInt(2);
				if (r == 0) {
					speed = -1 * (MySurfaceView.screenH / 64);
				} else {
					speed = MySurfaceView.screenH / 64;
				}
				// ����һ��������ٴη��ʱ����
				crazyTime = rand.nextInt(50) + 150; 
			}
		}
	}
	// �ж�Boss�������ӵ�����
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
		return true;
	}
	// ����BossѪ��
	public void setHp(int hp) {
		this.hp = hp;
	}
}
