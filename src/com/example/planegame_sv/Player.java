package com.example.planegame_sv;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class Player {
	// ���ǵ�Ѫ����Ѫ��λͼ
	// Ĭ��3Ѫ
	private int playerHp = 3;
	private Bitmap bmpPlayerHp;
	// ���ǵ������Լ�λͼ
	public int x, y;
	public Bitmap bmpPlayer;
	// �����ƶ��ٶ�
	private int speed = 15;
	// �����ƶ���ʶ
	private boolean isUp, isDown, isLeft, isRight;
	// ��ײ�����޵�ʱ��
	// ��ʱ��
	private int noCollisionCount = 0;
	// �޵�ʱ��
	private int noCollisionTime = 60;
	// �Ƿ���ײ�ı�ʶλ
	private boolean isCollision;
	
	// ���ǵĹ��캯��
	public Player(Bitmap bmpPlayer, Bitmap bmpPlayerHp) {
		this.bmpPlayer = bmpPlayer;
		this.bmpPlayerHp = bmpPlayerHp;
		x = MySurfaceView.screenW / 2 - bmpPlayer.getWidth() / 2;
		y = MySurfaceView.screenH - bmpPlayer.getHeight();
	}
	// ���ǵĻ�ͼ����
	public void draw(Canvas canvas, Paint paint) {
		// ��������
		//�����Ǵ����޵�ʱ�䣬��������˸
		if (isCollision) {
			// ÿ2����Ϸѭ��������һ������
			if (noCollisionCount % 2 == 0) {
				canvas.drawBitmap(bmpPlayer, x, y, paint);
			}
		} else {
			canvas.drawBitmap(bmpPlayer, x, y, paint);
		}
		// ��������Ѫ��
		for (int i=0; i<playerHp; i++) {
			canvas.drawBitmap(bmpPlayerHp, i * bmpPlayerHp.getWidth(), 
					MySurfaceView.screenH - bmpPlayerHp.getHeight(), paint);
		}
	}
	// ��������
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
	// ��������
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
	// ���ǵ��߼�
	public void logic() {
		// ���������ƶ�
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
		// �ж���ĻX�߽�
		if (x + bmpPlayer.getWidth() >= MySurfaceView.screenW) {
			x = MySurfaceView.screenW - bmpPlayer.getWidth();
		} else if (x < 0) {
			x = 0;
		}
		// �ж���ĻY�߽�
		if (y + bmpPlayer.getHeight() >= MySurfaceView.screenH) {
			y = MySurfaceView.screenH - bmpPlayer.getHeight();
		} else if (y < 0) {
			y = 0;
		}		
		//�����޵�״̬
		if (isCollision) {
			// ��ʱ����ʼ��ʱ
			noCollisionCount++;
			if (noCollisionCount >= noCollisionTime) {
				// �޵�ʱ����󣬽����޵�״̬����ʼ��������
				isCollision = false;
				noCollisionCount = 0;
			}
		}
	}
	// ��������Ѫ��
	public void setPlayerHp(int hp) {
		this.playerHp = hp;
	}
	// ��ȡ����Ѫ��
	public int getPlayerHp() {
		return playerHp;
	}
	// �ж���ײ��������л���
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
			// ��ײ�������޵�״̬			
			isCollision = true;
			return true;		
		} else {
			return false;
		}
	}
	// �ж���ײ��������л��ӵ���
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
			// ��ײ�������޵�״̬			
			isCollision = true;
			return true;		
		} else {
			return false;
		}
	}	
	
	
}
