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
	// 线程消亡的标识位
	private boolean thLive;
	private Canvas canvas;
	public static int screenW, screenH;	
	
	// 定义游戏状态常量
	public static final int GAME_MENU = 0; //游戏菜单
	public static final int GAMEING = 1; //游戏中
	public static final int GAME_WIN = 2; //游戏胜利
	public static final int GAME_LOST = 3; //游戏失败
	public static final int GAME_PAUSE = -1; //游戏暂停
	// 当前游戏状态
	public static int gameState = GAME_MENU;
	// 声明一个Resources实例便于加载图片
	private Resources res = this.getResources();
	// 声明游戏需要用到的图片资源（图片声明）
	private Bitmap bmpBackGround1; // 游戏背景
	private Bitmap bmpBackGround; // 游戏背景
	private Bitmap bmpBoom; // 爆炸效果
	private Bitmap bmpBossBoom; // Boss爆炸效果
	private Bitmap bmpButton; // 游戏开始按钮
	private Bitmap bmpButtonPress; // 游戏开始按钮被点击
	private Bitmap bmpEnemyDuck; // 怪物鸭子
	private Bitmap bmpEnemyDuckr; // 怪物鸭子
	private Bitmap bmpEnemyFly; // 怪物苍蝇
	private Bitmap bmpEnemyBoss; // 怪物猪头Boss
	private Bitmap bmpGameWin; // 游戏胜利背景
	private Bitmap bmpGameLost; // 游戏失败背景
	private Bitmap bmpPlayer; // 游戏主角飞机
	private Bitmap bmpPlayerHp; // 主角飞机血量
	private Bitmap bmpMenu; // 菜单背景
	public static Bitmap bmpBullet; // 子弹
	public static Bitmap bmpEnemyBullet; // 敌机子弹
	public static Bitmap bmpBossBullet; // Boss子弹
	private GameMenu gameMenu;
	// 声明一个滚动游戏背景对象
	private GameBg backGround;
	// 声明主角对象
	private Player player;
	//　声明一个敌机容器
	private Vector<Enemy> vcEnemy;
	// 每次生产敌机的时间（毫秒）
	private int createEnemyTime = 50;
	private int count; //计数器
	// 敌人数组：1、2、3表示敌机的种类，-1表示Boss
	// 二维数组的每一维都是一组怪物
	private int enemyArray[][] = {
			{1, 1, 1, 1}, {1, 1, 1, 1}, {2, 2, 2, 2}, {3, 3, 3, 3},
			{1, 2, 3}, {1, 2, 3}, {1, 2}, {3, 2}, {1, 3},
			{2, 2}, {3, 3}, {1, 2, 3}, {2, 3}, {1, 2},
			{1, 1, 3, 3}, {1, 3}, {2, 3}, {1, 1}, {1, 1},{-1}
	};
	// 当前取出一维数组的下标
	private int enemyArrayIndex;
	// 是否出现Boss标识位
	private boolean isBoss;
	// 随机库， 为创建的敌机赋予随机坐标
	private Random random;
	// 敌机子弹的容器
	private Vector<Bullet> vcBullet = new Vector<Bullet>();
	// 敌机子弹的计数器
	private int countEnemyBullet;
	// 主角子弹的容器
	private Vector<Bullet> vcBulletPlayer = new Vector<Bullet>();
	// 主角子弹的计数器
	private int countPlayerBullet;
	// 爆炸效果容器
	private Vector<Boom> vcBoom = new Vector<Boom>();
	// 声明Boss
	private Boss boss;
	// Boss的子弹容器
	public static Vector<Bullet> vcBulletBoss;	
	
	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		sfh = this.getHolder();
		sfh.addCallback(this);
		paint = new Paint();
		paint.setColor(Color.BLACK);
		setFocusable(true);
		setFocusableInTouchMode(true); //返回键捕捉
		setKeepScreenOn(true);		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub				
		screenW = this.getWidth();
		screenH = this.getHeight();
		
		initGame(); // 初始化游戏
		thLive = true;
		th = new Thread(this);
		th.start();		
	}	
	
	private void initGame() {
		// 放置游戏切入后台重新进入游戏时，游戏被重置
		// 当游戏状态处于菜单时，才会重置游戏
		if (gameState == GAME_MENU) {		
			// 加载游戏资源
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
		// 实例菜单对象
		gameMenu = new GameMenu(bmpMenu, bmpButton, bmpButtonPress);
		// 实例游戏背景
		backGround = new GameBg(bmpBackGround);
		// 实例主角
		player = new Player(bmpPlayer, bmpPlayerHp);
		// 实例敌机容器
		vcEnemy = new Vector<Enemy>();
		// 实例随机库
		random = new Random();
		// 实例Boss对象
		boss = new Boss(bmpEnemyBoss);
		// 实例Boss子弹容器
		vcBulletBoss = new Vector<Bullet>();
		
	}
	
	public void myDraw() {		
		try {
			canvas = sfh.lockCanvas();
	
			if (canvas != null) {							
				switch (gameState) {
				case GAME_MENU:
					// 菜单的绘图函数
					canvas.drawColor(Color.WHITE);
					gameMenu.draw(canvas, paint);
					break;
				case GAMEING:
					canvas.drawColor(Color.WHITE);
					// 游戏背景					
					backGround.draw(canvas, paint);
					// 主角绘图函数
					player.draw(canvas, paint);
					// 敌机绘制
					if (isBoss == false) {
						// 敌机绘制						
						for (int i=0; i<vcEnemy.size(); i++) {
							vcEnemy.elementAt(i).draw(canvas, paint);
						}
						// 敌机子弹绘制
						for (int i=0; i<vcBullet.size(); i++) {							
							vcBullet.elementAt(i).draw(canvas, paint);
						}
					} else {						
						// Boss的绘制
						boss.draw(canvas, paint);
						// Boss子弹绘制
						for (int i=0; i<vcBulletBoss.size(); i++) {
							vcBulletBoss.elementAt(i).draw(canvas, paint);
						}
					}
					// 处理主角子弹绘制
					for (int i=0; i<vcBulletPlayer.size(); i++) {
						vcBulletPlayer.elementAt(i).draw(canvas, paint);
					}		
					// 爆炸效果绘制
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
	
	// 按键按下
	public void buttonDown(View v) {
		switch (gameState) {
		case GAME_MENU:			
			break;
		case GAMEING:
			// 主角的按键按下事件
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
	
	// 按键弹起
	public void buttonUp(View v) {
		switch (gameState) {
		case GAME_MENU:
			
			break;
		case GAMEING:
			// 主角按键抬起事件
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
			// 菜单的触屏事件处理
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
			// 游戏胜利、失败、进行时都默认返回菜单
			if (gameState == GAMEING || gameState == GAME_WIN || gameState == GAME_LOST) {
				gameState = GAME_MENU;
				isBoss = false;
				// 重置游戏
				initGame();
				// 重置怪物出场
				enemyArrayIndex = 0;
			} else if (gameState == GAME_MENU) {
				// 当前状态在菜单界面，默认返回按键退出游戏
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 *  游戏逻辑
	 */
	private void logic() {
		switch (gameState) {
		case GAME_MENU:
			
			break;
		case GAMEING:
			// 背景逻辑
			backGround.logic();
			// 主角逻辑
			player.logic();			
			// 敌机逻辑
			if (isBoss == false) {
				for (int i=vcEnemy.size() - 1; i>=0; i--) {
					Enemy en = vcEnemy.elementAt(i);					
					// 因为容器不断添加敌机， 那么对敌机isDead判定,
					// 如果已死亡，那么就从容器中删除，对容器起到了优化作用；
					if (en.isDead) {
						vcEnemy.removeElementAt(i);
					} else {
						en.logic();
					}
				}
				// 生成敌机				
				count++;
				if (count % createEnemyTime == 0) {
					int x = random.nextInt(screenW - 80);
					int y = random.nextInt(20);
					int z =random.nextInt(20);
					for (int i=0; i<enemyArray[enemyArrayIndex].length; i++) {
						//苍蝇
						if (enemyArray[enemyArrayIndex][i] == 1) {							
							vcEnemy.addElement(new Enemy(bmpEnemyFly, 1, 
									x + i * bmpEnemyFly.getWidth() / 10 * 3 / 2, -50));
						// 鸭子左
						} else if (enemyArray[enemyArrayIndex][i] == 2) {														
							vcEnemy.addElement(new Enemy(bmpEnemyDuck, 2, -50, 
									y + i * bmpEnemyFly.getHeight() * 2));
						// 鸭子右
						} else if (enemyArray[enemyArrayIndex][i] == 3) {							
							vcEnemy.addElement(new Enemy(bmpEnemyDuckr, 3, screenW, 
									z + i * bmpEnemyFly.getHeight() * 2));
						}
					}
					
					//　这里判断下一组是否为最后一组（Boss）
					if (enemyArrayIndex == enemyArray.length - 1) {						
						isBoss = true;
						// Boss出现，所有敌机让其死亡
						for (int i=vcEnemy.size() - 1; i>=0; i--) {
							Enemy en = vcEnemy.elementAt(i);	
							en.isDead = true;
							vcEnemy.removeElementAt(i);									
						}
					} else {
						enemyArrayIndex++;
					}
				} 
				// 每2秒添加一个敌机子弹
				countEnemyBullet++;
				if (countEnemyBullet % 60 == 0) {
					countEnemyBullet = 0;
					for (int i = 0; i<vcEnemy.size(); i++) {
						Enemy en = vcEnemy.elementAt(i);
						// 不同类型敌机不同的子弹运行轨迹
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
				// 处理敌机子弹逻辑
				for (int i=0; i<vcBullet.size(); i++) {
					Bullet b = vcBullet.elementAt(i);
					if (b.isDead) {
						vcBullet.removeElement(b);
					} else {
						b.logic();
					}
				}
				// 处理敌机与主角的碰撞
				for (int i=0; i<vcEnemy.size(); i++) {
					if (player.isCollisionWith(vcEnemy.elementAt(i))) {
						// 发生碰撞，主角血量-1
						player.setPlayerHp(player.getPlayerHp() - 1);
					}
				}
				// 处理敌机子弹与主角碰撞
				for (int i=0; i<vcBullet.size(); i++) {
					if (player.isCollisionWith(vcBullet.elementAt(i))) {
						// 发生碰撞， 主角血量-1
						player.setPlayerHp(player.getPlayerHp() - 1);
						// 当主角血量小于0，判断游戏失败
						if (player.getPlayerHp() <= -1) {
							gameState = GAME_LOST;
						}
					}
				}			
				// 处理主角子弹与敌机碰撞
				for (int i=0; i<vcBulletPlayer.size(); i++) {
					// 取出主角子弹容器的每个元素
					Bullet blPlayer = vcBulletPlayer.elementAt(i);
					for (int j=0; j<vcEnemy.size(); j++) {
						// 添加爆炸效果
						// 取出敌机容器的每个元素与主角子弹遍历判断
						if (vcEnemy.elementAt(j).isCollisionWith(blPlayer)) {
							vcBoom.add(new Boom(bmpBoom, 
									vcEnemy.elementAt(j).x, vcEnemy.elementAt(j).y, 7));
						}
					}
				}
			// Boss相关逻辑
			} else {			
				// 每0.5秒添加一个Boss子弹
				boss.logic();
				if (countPlayerBullet % 10 == 0) {
					// Boss没发疯的普通子弹
					vcBulletBoss.add(new Bullet(bmpBossBullet, boss.x + boss.frameW / 2, 
							boss.y + boss.frameH / 2, Bullet.BULLET_FLY));
				}
				// Boss子弹运行逻辑
				for (int i=0; i<vcBulletBoss.size(); i++) {
					Bullet b = vcBulletBoss.elementAt(i);
					if (b.isDead) {
						vcBulletBoss.removeElement(b);
					} else {
						b.logic();
					}
				}
				// Boss子弹与主角的碰撞
				for (int i=0; i<vcBulletBoss.size(); i++) {
					if (player.isCollisionWith(vcBulletBoss.elementAt(i))) {
						// 发生碰撞，主角血量-1
						player.setPlayerHp(player.getPlayerHp() - 1);
						// 当主角血量小于0，判断游戏失败
						if (player.getPlayerHp() <= 0) {
							gameState = GAME_LOST;
						}
					}
				}
				// Boss被主角子弹击中，产生爆炸效果
				for (int i=0; i<vcBulletPlayer.size(); i++) {
					Bullet b = vcBulletPlayer.elementAt(i);
					if (boss.isCollisionWith(b)) {
						if (boss.hp <= 0) {
							//游戏胜利
							gameState = GAME_WIN;	
							MainActivity.btnVisible(false);
						} else {
							//及时删除本次碰撞的子弹，防止重复判定次子弹与Boss碰撞
							b.isDead = true;
							//Boss血量-1
							boss.setHp(boss.hp - 1);
							//在Boss上添加三个Boss爆炸效果
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
			// 每1秒添加一个主角子弹
			countPlayerBullet++;
			if (countPlayerBullet % 20 == 0) {
				vcBulletPlayer.add(new Bullet(bmpBullet, 
						player.x + 15, player.y - 20, Bullet.BULLET_PLAYER));
			}
			// 处理主角子弹逻辑
			for (int i=0; i<vcBulletPlayer.size(); i++) {
				Bullet b = vcBulletPlayer.elementAt(i);
				if (b.isDead) {
					vcBulletPlayer.removeElement(b);
				} else {
					b.logic();
				}
			}
			// 爆炸效果逻辑
			for (int i=0; i<vcBoom.size(); i++) {
				Boom boom = vcBoom.elementAt(i);
				if (boom.playEnd) {
					// 播放完毕的从容器中删除
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
	// 修改图片大小
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
