package com.example.planegame_sv;

import java.util.Random;
import java.util.Vector;

import com.example.planegame_sv.MainActivity.ButtonListener;

import android.R.integer;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.os.IInterface;
import android.os.SystemClock;
import android.sax.EndElementListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Button;

public class MySurfaceView extends SurfaceView implements Callback, Runnable {	
	private SurfaceHolder sfh;
	private Paint paint;
	private int textX=10, textY=10;
	private Thread th;
	// �߳������ı�ʶλ
	private boolean thLive;
	private Canvas canvas;
	public static int screenW, screenH;	
	
	// ������Ϸ״̬����
	public static final int GAME_MENU = 0; //��Ϸ�˵�
	public static final int GAMEING = 1; //��Ϸ��
	public static final int GAME_WIN = 2; //��Ϸʤ��
	public static final int GAME_LOST = 3; //��Ϸʧ��
	public static final int GAME_PAUSE = -1; //��Ϸ��ͣ
	// ��ǰ��Ϸ״̬
	public static int gameState = GAME_MENU;
	// ����һ��Resourcesʵ�����ڼ���ͼƬ
	private Resources res = this.getResources();
	// ������Ϸ��Ҫ�õ���ͼƬ��Դ��ͼƬ������
	private Bitmap bmpBackGround1; // ��Ϸ����
	private Bitmap bmpBackGround; // ��Ϸ����
	private Bitmap bmpBoom; // ��ըЧ��
	private Bitmap bmpBossBoom; // Boss��ըЧ��
	private Bitmap bmpButton; // ��Ϸ��ʼ��ť
	private Bitmap bmpButtonPress; // ��Ϸ��ʼ��ť�����
	private Bitmap bmpEnemyDuck; // ����Ѽ��
	private Bitmap bmpEnemyDuckr; // ����Ѽ��
	private Bitmap bmpEnemyFly; // �����Ӭ
	private Bitmap bmpEnemyBoss; // ������ͷBoss
	private Bitmap bmpGameWin; // ��Ϸʤ������
	private Bitmap bmpGameLost; // ��Ϸʧ�ܱ���
	private Bitmap bmpPlayer; // ��Ϸ���Ƿɻ�
	private Bitmap bmpPlayerHp; // ���Ƿɻ�Ѫ��
	private Bitmap bmpMenu; // �˵�����
	public static Bitmap bmpBullet; // �ӵ�
	public static Bitmap bmpEnemyBullet; // �л��ӵ�
	public static Bitmap bmpBossBullet; // Boss�ӵ�
	private GameMenu gameMenu;
	// ����һ��������Ϸ��������
	private GameBg backGround;
	// �������Ƕ���
	private Player player;
	//������һ���л�����
	private Vector<Enemy> vcEnemy;
	// ÿ�������л���ʱ�䣨���룩
	private int createEnemyTime = 50;
	private int count; //������
	// �������飺1��2��3��ʾ�л������࣬-1��ʾBoss
	// ��ά�����ÿһά����һ�����
	private int enemyArray[][] = {
			{1, 1, 1, 1}, {1, 1, 1, 1}, {2, 2, 2, 2}, {3, 3, 3, 3},
			{1, 2, 3}, {1, 2, 3}, {1, 2}, {3, 2}, {1, 3},
			{2, 2}, {3, 3}, {1, 2, 3}, {2, 3}, {1, 2},
			{1, 1, 3, 3}, {1, 3}, {2, 3}, {1, 1}, {1, 1},{-1}
	};
	// ��ǰȡ��һά������±�
	private int enemyArrayIndex;
	// �Ƿ����Boss��ʶλ
	private boolean isBoss;
	// ����⣬ Ϊ�����ĵл������������
	private Random random;
	// �л��ӵ�������
	private Vector<Bullet> vcBullet = new Vector<Bullet>();
	// �л��ӵ��ļ�����
	private int countEnemyBullet;
	// �����ӵ�������
	private Vector<Bullet> vcBulletPlayer = new Vector<Bullet>();
	// �����ӵ��ļ�����
	private int countPlayerBullet;
	// ��ըЧ������
	private Vector<Boom> vcBoom = new Vector<Boom>();
	// ����Boss
	private Boss boss;
	// Boss���ӵ�����
	public static Vector<Bullet> vcBulletBoss;	
	
	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		paint.setColor(Color.BLACK);
		setFocusable(true);
		setFocusableInTouchMode(true); //���ؼ���׽
		setKeepScreenOn(true);		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub				
		screenW = this.getWidth();
		screenH = this.getHeight();
		
		initGame(); // ��ʼ����Ϸ
		thLive = true;
		th = new Thread(this);
		th.start();		
	}	
	
	private void initGame() {
		// ������Ϸ�����̨���½�����Ϸʱ����Ϸ������
		// ����Ϸ״̬���ڲ˵�ʱ���Ż�������Ϸ
		if (gameState == GAME_MENU) {		
			// ������Ϸ��Դ
			bmpBackGround = BitmapFactory.decodeResource(res, R.drawable.star);
			//Log.d("xudong_tag", "w:" + bmpBackGround.getWidth());
			//Log.d("xudong_tag", "H:" + bmpBackGround.getHeight());
			bmpBackGround = resizeBitmap(bmpBackGround, screenW, 
					screenW * bmpBackGround.getHeight() / bmpBackGround.getWidth());
			bmpBoom = BitmapFactory.decodeResource(res, R.drawable.boom);
			bmpBoom = resizeBitmap(bmpBoom, screenW / 2, 
					(screenW / 2) * bmpBoom.getHeight() / bmpBoom.getWidth());
			bmpBossBoom = BitmapFactory.decodeResource(res, R.drawable.bossboom);
			bmpBossBoom = resizeBitmap(bmpBossBoom, screenW / 2, 
					(screenW / 2) * bmpBossBoom.getHeight() / bmpBossBoom.getWidth());
			bmpButton = BitmapFactory.decodeResource(res, R.drawable.button);
			bmpButton = resizeBitmap(bmpButton, screenW / 3, 
					(screenW / 3) * bmpButton.getHeight() / bmpButton.getWidth());
			bmpButtonPress = BitmapFactory.decodeResource(res, R.drawable.button_press);
			bmpButtonPress = resizeBitmap(bmpButtonPress, screenW / 3, 
					(screenW / 3) * bmpButtonPress.getHeight() / bmpButtonPress.getWidth());
			bmpEnemyDuck = BitmapFactory.decodeResource(res, R.drawable.enemyduck);
			bmpEnemyDuck = resizeBitmap(bmpEnemyDuck, screenW, 
					screenW * bmpEnemyDuck.getHeight() / bmpEnemyDuck.getWidth());
			bmpEnemyDuckr = BitmapFactory.decodeResource(res, R.drawable.enemyduckr);
			bmpEnemyDuckr = resizeBitmap(bmpEnemyDuckr, screenW, 
					screenW * bmpEnemyDuckr.getHeight() / bmpEnemyDuckr.getWidth());
			bmpEnemyFly = BitmapFactory.decodeResource(res, R.drawable.enemyfly);
			bmpEnemyFly = resizeBitmap(bmpEnemyFly, screenW, 
					(screenW) * bmpEnemyFly.getHeight() / bmpEnemyFly.getWidth());
			bmpEnemyBoss = BitmapFactory.decodeResource(res, R.drawable.enemyboss);
			bmpEnemyBoss = resizeBitmap(bmpEnemyBoss, screenW * 2, 
					(screenW * 2) * bmpEnemyBoss.getHeight() / bmpEnemyBoss.getWidth());
			bmpGameWin = BitmapFactory.decodeResource(res, R.drawable.gamewin);
			bmpGameWin = resizeBitmap(bmpGameWin, screenW, screenH);
			bmpGameLost = BitmapFactory.decodeResource(res, R.drawable.gamelost);
			bmpGameLost = resizeBitmap(bmpGameLost, screenW, screenH);
			bmpPlayer = BitmapFactory.decodeResource(res, R.drawable.player);
			bmpPlayer = resizeBitmap(bmpPlayer, screenW / 12, 
					(screenW / 12) * (bmpPlayer.getHeight() / bmpPlayer.getWidth()));			
			bmpPlayerHp = BitmapFactory.decodeResource(res, R.drawable.hp);
			bmpPlayerHp = resizeBitmap(bmpPlayerHp, screenW / 20, 
					(screenW / 20) * (bmpPlayerHp.getHeight() / bmpPlayerHp.getWidth()));
			bmpMenu = BitmapFactory.decodeResource(res, R.drawable.menu2);
			bmpMenu = resizeBitmap(bmpMenu, screenW, screenH);
			bmpBullet = BitmapFactory.decodeResource(res, R.drawable.bullet);
			bmpBullet = resizeBitmap(bmpBullet, screenW / 26, 
					(screenW / 26) * (bmpBullet.getHeight() / bmpBullet.getWidth()));
			bmpBossBullet = BitmapFactory.decodeResource(res, R.drawable.bossbullet);
			bmpBossBullet = resizeBitmap(bmpBossBullet, screenW / 20, 
					(screenW / 20) * (bmpBossBullet.getHeight() / bmpBossBullet.getWidth()));
			bmpEnemyBullet = BitmapFactory.decodeResource(res, R.drawable.enemybullet);
			bmpEnemyBullet = resizeBitmap(bmpEnemyBullet, screenW / 30, 
					(screenW / 30) * (bmpEnemyBullet.getHeight() / bmpEnemyBullet.getWidth()));
		}
		// ʵ���˵�����
		gameMenu = new GameMenu(bmpMenu, bmpButton, bmpButtonPress);
		// ʵ����Ϸ����
		backGround = new GameBg(bmpBackGround);
		// ʵ������
		player = new Player(bmpPlayer, bmpPlayerHp);
		// ʵ���л�����
		vcEnemy = new Vector<Enemy>();
		// ʵ�������
		random = new Random();
		// ʵ��Boss����
		boss = new Boss(bmpEnemyBoss);
		// ʵ��Boss�ӵ�����
		vcBulletBoss = new Vector<Bullet>();
		
	}
	
	public void myDraw() {		
		try {
			canvas = sfh.lockCanvas();
	
			if (canvas != null) {							
				switch (gameState) {
				case GAME_MENU:
					// �˵��Ļ�ͼ����
					canvas.drawColor(Color.WHITE);
					gameMenu.draw(canvas, paint);
					break;
				case GAMEING:
					canvas.drawColor(Color.WHITE);
					// ��Ϸ����					
					backGround.draw(canvas, paint);
					// ���ǻ�ͼ����
					player.draw(canvas, paint);
					// �л�����
					if (isBoss == false) {
						// �л�����						
						for (int i=0; i<vcEnemy.size(); i++) {
							vcEnemy.elementAt(i).draw(canvas, paint);
						}
						// �л��ӵ�����
						for (int i=0; i<vcBullet.size(); i++) {							
							vcBullet.elementAt(i).draw(canvas, paint);
						}
					} else {						
						// Boss�Ļ���
						boss.draw(canvas, paint);
						// Boss�ӵ�����
						for (int i=0; i<vcBulletBoss.size(); i++) {
							vcBulletBoss.elementAt(i).draw(canvas, paint);
						}
					}
					// ���������ӵ�����
					for (int i=0; i<vcBulletPlayer.size(); i++) {
						vcBulletPlayer.elementAt(i).draw(canvas, paint);
					}		
					// ��ըЧ������
					for (int i=0; i<vcBoom.size(); i++) {
						vcBoom.elementAt(i).draw(canvas, paint);
					}
					
					break;
				case GAME_PAUSE:
					break;
				case GAME_WIN:				
					canvas.drawBitmap(bmpGameWin, 0, 0, paint);
					break;
				case GAME_LOST:					
					canvas.drawBitmap(bmpGameLost, 0, 0, paint);
					break;
				default:
					break;
				}
			} 
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (canvas != null) {
				sfh.unlockCanvasAndPost(canvas);
			}
		}

	}
	
	// ��������
	public void buttonDown(View v) {
		switch (gameState) {
		case GAME_MENU:			
			break;
		case GAMEING:
			// ���ǵİ��������¼�
			player.buttonDown(v);
			break;
		case GAME_PAUSE:

			break;
		case GAME_WIN:

			break;
		case GAME_LOST:
			
			break;
		default:
			break;
		}
	}
	
	// ��������
	public void buttonUp(View v) {
		switch (gameState) {
		case GAME_MENU:
			
			break;
		case GAMEING:
			// ���ǰ���̧���¼�
			player.buttonUp(v);
			break;
		case GAME_PAUSE:

			break;
		case GAME_WIN:

			break;
		case GAME_LOST:
			
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (gameState) {
		case GAME_MENU:
			// �˵��Ĵ����¼�����
			gameMenu.onTouchEvent(event);
			break;
		case GAMEING:
			//gameMenu.onTouchEvent(event);
			break;
		case GAME_PAUSE:
	
			break;
		case GAME_WIN:
	
			break;
		case GAME_LOST:
			
			break;
		default:
			break;
		}	
		return true;
	}	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// ��Ϸʤ����ʧ�ܡ�����ʱ��Ĭ�Ϸ��ز˵�
			if (gameState == GAMEING || gameState == GAME_WIN || gameState == GAME_LOST) {
				gameState = GAME_MENU;
				isBoss = false;
				// ������Ϸ
				initGame();
				// ���ù������
				enemyArrayIndex = 0;
			} else if (gameState == GAME_MENU) {
				// ��ǰ״̬�ڲ˵����棬Ĭ�Ϸ��ذ����˳���Ϸ
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 *  ��Ϸ�߼�
	 */
	private void logic() {
		switch (gameState) {
		case GAME_MENU:
			
			break;
		case GAMEING:
			// �����߼�
			backGround.logic();
			// �����߼�
			player.logic();			
			// �л��߼�
			if (isBoss == false) {
				for (int i=vcEnemy.size() - 1; i>=0; i--) {
					Enemy en = vcEnemy.elementAt(i);					
					// ��Ϊ����������ӵл��� ��ô�Եл�isDead�ж�,
					// �������������ô�ʹ�������ɾ���������������Ż����ã�
					if (en.isDead) {
						vcEnemy.removeElementAt(i);
					} else {
						en.logic();
					}
				}
				// ���ɵл�				
				count++;
				if (count % createEnemyTime == 0) {
					int x = random.nextInt(screenW - 80);
					int y = random.nextInt(20);
					int z =random.nextInt(20);
					for (int i=0; i<enemyArray[enemyArrayIndex].length; i++) {
						//��Ӭ
						if (enemyArray[enemyArrayIndex][i] == 1) {							
							vcEnemy.addElement(new Enemy(bmpEnemyFly, 1, 
									x + i * bmpEnemyFly.getWidth() / 10 * 3 / 2, -50));
						// Ѽ����
						} else if (enemyArray[enemyArrayIndex][i] == 2) {														
							vcEnemy.addElement(new Enemy(bmpEnemyDuck, 2, -50, 
									y + i * bmpEnemyFly.getHeight() * 2));
						// Ѽ����
						} else if (enemyArray[enemyArrayIndex][i] == 3) {							
							vcEnemy.addElement(new Enemy(bmpEnemyDuckr, 3, screenW, 
									z + i * bmpEnemyFly.getHeight() * 2));
						}
					}
					
					//�������ж���һ���Ƿ�Ϊ���һ�飨Boss��
					if (enemyArrayIndex == enemyArray.length - 1) {						
						isBoss = true;
						// Boss���֣����ел���������
						for (int i=vcEnemy.size() - 1; i>=0; i--) {
							Enemy en = vcEnemy.elementAt(i);	
							en.isDead = true;
							vcEnemy.removeElementAt(i);									
						}
					} else {
						enemyArrayIndex++;
					}
				} 
				// ÿ2�����һ���л��ӵ�
				countEnemyBullet++;
				if (countEnemyBullet % 60 == 0) {
					countEnemyBullet = 0;
					for (int i = 0; i<vcEnemy.size(); i++) {
						Enemy en = vcEnemy.elementAt(i);
						// ��ͬ���͵л���ͬ���ӵ����й켣
						int bulletType = 0;
						switch (en.type) {
						case Enemy.TYPE_FLY:
							bulletType = Bullet.BULLET_FLY;
							break;
						case Enemy.TYPE_DUCKL:
						case Enemy.TYPE_DUCKR:
							bulletType = Bullet.BULLET_DUCK;
							break;
						default:
							break;
						}
						vcBullet.add(new Bullet(bmpEnemyBullet, 
								en.x + 10, en.y + 20, bulletType));
					}
				}
				// ����л��ӵ��߼�
				for (int i=0; i<vcBullet.size(); i++) {
					Bullet b = vcBullet.elementAt(i);
					if (b.isDead) {
						vcBullet.removeElement(b);
					} else {
						b.logic();
					}
				}
				// ����л������ǵ���ײ
				for (int i=0; i<vcEnemy.size(); i++) {
					if (player.isCollisionWith(vcEnemy.elementAt(i))) {
						// ������ײ������Ѫ��-1
						player.setPlayerHp(player.getPlayerHp() - 1);
					}
				}
				// ����л��ӵ���������ײ
				for (int i=0; i<vcBullet.size(); i++) {
					if (player.isCollisionWith(vcBullet.elementAt(i))) {
						// ������ײ�� ����Ѫ��-1
						player.setPlayerHp(player.getPlayerHp() - 1);
						// ������Ѫ��С��0���ж���Ϸʧ��
						if (player.getPlayerHp() <= -1) {
							gameState = GAME_LOST;
						}
					}
				}			
				// ���������ӵ���л���ײ
				for (int i=0; i<vcBulletPlayer.size(); i++) {
					// ȡ�������ӵ�������ÿ��Ԫ��
					Bullet blPlayer = vcBulletPlayer.elementAt(i);
					for (int j=0; j<vcEnemy.size(); j++) {
						// ��ӱ�ըЧ��
						// ȡ���л�������ÿ��Ԫ���������ӵ������ж�
						if (vcEnemy.elementAt(j).isCollisionWith(blPlayer)) {
							vcBoom.add(new Boom(bmpBoom, 
									vcEnemy.elementAt(j).x, vcEnemy.elementAt(j).y, 7));
						}
					}
				}
			// Boss����߼�
			} else {			
				// ÿ0.5�����һ��Boss�ӵ�
				boss.logic();
				if (countPlayerBullet % 10 == 0) {
					// Bossû�������ͨ�ӵ�
					vcBulletBoss.add(new Bullet(bmpBossBullet, boss.x + boss.frameW / 2, 
							boss.y + boss.frameH / 2, Bullet.BULLET_FLY));
				}
				// Boss�ӵ������߼�
				for (int i=0; i<vcBulletBoss.size(); i++) {
					Bullet b = vcBulletBoss.elementAt(i);
					if (b.isDead) {
						vcBulletBoss.removeElement(b);
					} else {
						b.logic();
					}
				}
				// Boss�ӵ������ǵ���ײ
				for (int i=0; i<vcBulletBoss.size(); i++) {
					if (player.isCollisionWith(vcBulletBoss.elementAt(i))) {
						// ������ײ������Ѫ��-1
						player.setPlayerHp(player.getPlayerHp() - 1);
						// ������Ѫ��С��0���ж���Ϸʧ��
						if (player.getPlayerHp() <= 0) {
							gameState = GAME_LOST;
						}
					}
				}
				// Boss�������ӵ����У�������ըЧ��
				for (int i=0; i<vcBulletPlayer.size(); i++) {
					Bullet b = vcBulletPlayer.elementAt(i);
					if (boss.isCollisionWith(b)) {
						if (boss.hp <= 0) {
							//��Ϸʤ��
							gameState = GAME_WIN;	
							MainActivity.btnVisible(false);
						} else {
							//��ʱɾ��������ײ���ӵ�����ֹ�ظ��ж����ӵ���Boss��ײ
							b.isDead = true;
							//BossѪ��-1
							boss.setHp(boss.hp - 1);
							//��Boss���������Boss��ըЧ��
							vcBoom.add(new Boom(bmpBossBoom, 
									boss.x + boss.frameW / 2, boss.y + boss.frameH, 5));
							vcBoom.add(new Boom(bmpBossBoom, 
									boss.x - bmpBossBoom.getWidth() / 5, boss.y, 5));
							vcBoom.add(new Boom(bmpBossBoom, 
									boss.x + boss.frameW, boss.y, 5));
						}
					}
				}
			}
			// ÿ1�����һ�������ӵ�
			countPlayerBullet++;
			if (countPlayerBullet % 20 == 0) {
				vcBulletPlayer.add(new Bullet(bmpBullet, 
						player.x + 15, player.y - 20, Bullet.BULLET_PLAYER));
			}
			// ���������ӵ��߼�
			for (int i=0; i<vcBulletPlayer.size(); i++) {
				Bullet b = vcBulletPlayer.elementAt(i);
				if (b.isDead) {
					vcBulletPlayer.removeElement(b);
				} else {
					b.logic();
				}
			}
			// ��ըЧ���߼�
			for (int i=0; i<vcBoom.size(); i++) {
				Boom boom = vcBoom.elementAt(i);
				if (boom.playEnd) {
					// ������ϵĴ�������ɾ��
					vcBoom.removeElementAt(i);
				} else {
					vcBoom.elementAt(i).logic();
				}
			}			
			break;
		case GAME_PAUSE:
	
			break;
		case GAME_WIN:
	
			break;
		case GAME_LOST:
			
			break;
		default:
			break;
		}	
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (thLive) {
			long start = System.currentTimeMillis();
			myDraw();			
			logic();
			long end = System.currentTimeMillis();
			try {
				if (end - start < 50) {
					Thread.sleep(50 - (end - start));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	// �޸�ͼƬ��С
	public Bitmap resizeBitmap(Bitmap bitmap,int w,int h)
    {
        if(bitmap!=null)
        {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = w;
            int newHeight = h;
            float scaleWight = ((float)newWidth)/width;
            float scaleHeight = ((float)newHeight)/height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWight, scaleHeight);
            Bitmap res = Bitmap.createBitmap(bitmap, 0,0,width, height, matrix, true);
            return res;
        }
        else{
            return null;
        }
    }
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		thLive = false;
	}

}
