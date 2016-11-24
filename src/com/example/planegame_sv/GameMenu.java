package com.example.planegame_sv;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class GameMenu {
	// �˵�����ͼ
	private Bitmap bmpMenu;
	// ��ťͼƬ��Դ�����º�δ���£�--
	private Bitmap bmpButton, bmpButtonPress;
	// ��ť�����
	private int btnX, btnY;
	// ��ť�Ƿ��µı�ʶλ
	private Boolean isPress;	
	
	// �˵���ʼ��
	public GameMenu(Bitmap bmpMenu, Bitmap bmpButton, Bitmap bmpButtonPress) {
		MainActivity.btnVisible(false);
		this.bmpMenu = bmpMenu;
		this.bmpButton = bmpButton;
		this.bmpButtonPress = bmpButtonPress;
		// X���У�Y������Ļ�ײ�
		btnX = MySurfaceView.screenW / 2 - bmpButton.getWidth() / 2;
		btnY = MySurfaceView.screenH / 4 * 3 - bmpButton.getHeight() / 2;
		isPress = false;		

	}
	// �˵���ͼ����
	public void draw(Canvas canvas, Paint paint) {
		// ���Ʋ˵�����ͼ		
		canvas.drawBitmap(bmpMenu, 0, 0, null);
		// ����δ���°�ťͼ		
		if (isPress) {
			canvas.drawBitmap(bmpButtonPress, btnX, btnY, null);
		} else {
			canvas.drawBitmap(bmpButton, btnX, btnY, null);
		}
	}
	// �˵������¼�������Ҫ���ڴ��?ť�¼�
	public void onTouchEvent(MotionEvent event) {
		// ��ȡ�û���ǰ����λ��
		int pointX = (int)event.getX();
		int pointY = (int)event.getY();

		if (event.getAction() == MotionEvent.ACTION_DOWN ||
				event.getAction() == MotionEvent.ACTION_MOVE) {
			if (pointX > btnX && pointX < btnX + bmpButton.getWidth()) {
				if (pointY > btnY && pointY < btnY + bmpButton.getHeight()) {
					isPress = true;
				} else {
					isPress = false;
				}
			} else {
				isPress = false;
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (pointX > btnX && pointX < btnX + bmpButton.getWidth()) {
				if (pointY > btnY && pointY < btnY + bmpButton.getHeight()) {
					isPress = false;
					MySurfaceView.gameState = MySurfaceView.GAMEING;
					MainActivity.btnVisible(true);					
				}
			}
		}
	}
}
