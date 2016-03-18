package com.mime.cassetete;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;


public class casseteteView extends View implements Runnable{
	//definition de la taille de l'ecran
	final static int SCREEN_WIDTH = 320;
	final static int SCREEN_HEIGHT = 455;
	//etat des jeu
	final int STATE_MENU   = 0;
	final int STATE_PLAY   = 1;
	final int STATE_PAUSE  = 2;
	final int STATE_OVER   = 3;
	
//	final int MENU_NEWGAME = 10;
//	final int MENU_SETTING = 11;
//	final int MENU_HELP    = 12;
//	
//	final int SETTING_MUSIC_ON  = 21;
//	final int SETTING_MUSIC_OFF = 22;
//	final int SETTING_LEVEL     = 23;
	
//	final int OPTION_RESUME = 31;
//	final int OPTION_MENU   = 32;
//  final int OPTION_QUIT   = 33;
	//dans ce jeu,on a 6 niveau de vitesse au choix.
	public static final int MAX_LEVEL = 6;
	
	public static final String TAG = "TetrisView";
	public static final String DATAFILE = "save.dt";
	
	
	int mGamestate = STATE_PLAY;
	
	int mScore = 0;
	int mSpeed = 1;
	int mDeLine = 0;
	
	boolean mIsCombo = false; //
	boolean mIsPaused = false;
	boolean mIsVoice = true;
	//mMoveDelay correspond au niveau de vitesse,le temps de dormir de thread
	long mMoveDelay = 600;
	long mLastMove  = 0;
	
//	int mAtPause = OPTION_RESUME;
	
	private Context mContext = null;
	private Paint mPaint = new Paint();
	
	RefreshHandler mRefreshHandler = null;
	
	TileView mCurrentTile = null;
	TileView mNextTile = null;
	Court mCourt = null;
	ResourceStore mResourceStore = null;
	
	
	///////////
	MusicPlayer mMPlayer = null;
	
	public casseteteView(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}

	//definition la fonction init qui derive de context pour fournir une interface
	//de informations global
	protected void init(Context context)
	{
		mContext        = context;
		mCurrentTile    = new TileView(context);
		Log.i("tetris","mCurrentTile builed");
		mNextTile       = new TileView(context);
		mCourt          = new Court(context);
		mRefreshHandler = new RefreshHandler(this);	
		mResourceStore  = new ResourceStore(context);
		
		mMPlayer = new MusicPlayer(context);
		
		//initialisation de niveau de vitesse quand on commence le jeu,par defaut 1.
		setLevel(1);
		//initialisation de anti-alias et la couleur dans la Tetris view.
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.RED);
		
		setFocusable(true);
		//lancer le thread
		new Thread(this).start();
	}
	//la fonction d'initialisation de l'etat de jeu.
	public void logic()
	{
		switch(mGamestate)
		{
		case STATE_MENU:
			//
			mGamestate = STATE_PLAY;
			break;
		case STATE_PLAY:
			//startGame();
			playGame();
			break;
		case STATE_PAUSE:
//			if(mAtPause == OPTION_RESUME)
//			{
//				mIsPaused = false;
//				mGamestate = STATE_PLAY;
//				break;
//			}
			//if(mPusedChoose = )
			break;
		case STATE_OVER:
//			startGame();
//			mGamestate = STATE_PLAY;
			break;
		default:;
		}
	}
	
	public void startGame()
	{
		mGamestate = STATE_PLAY;
		mCourt.clearCourt();
		mCurrentTile = new TileView(mContext);
		mNextTile    = new TileView(mContext);
		
		// mSpeed = speed choosed
		// mScore
		// mLine
		setLevel(1);
		mScore = 0;
		mDeLine = 0;
		mIsPaused = false;
		mIsCombo = false;
		
		playGame();
	}
	//la fonction playGame() pour compter le temps des deplacement de block
	//en meme temps calcul les etat de block:collision,diminuation,terminaison et
	//rotation etc,,
	//dans les 2 etat collision et rotation,on ajoute un son une fois que les action
	//se sont produites.
	public void playGame()
	{
		long now = System.currentTimeMillis();
		if(now - mLastMove > mMoveDelay)
		{
			if(mIsPaused)
			{
				return;
			}
			if(mIsCombo)
			{
				mCourt.placeTile(mCurrentTile);
				//////
				mMPlayer.playMoveVoice();
				
				if(mCourt.isGameOver() )
				{
					mGamestate = STATE_OVER;
					return;
				}
				int line = mCourt.removeLines();
				if(line > 0 )
				{
					mMPlayer.playBombVoice();
				}
				mDeLine += line;
				countScore(line);
			
				mCurrentTile = mNextTile;
				mNextTile = new TileView(mContext);
				
				mIsCombo = false;
			}
			moveDown();
			
			mLastMove = now;
		}
	}
	//la fonction pour compter le score par rapport de diminuation de rangee.
	//et une juge logique pour incrementer le niveau de vitesse selon le score.
	private void countScore(int line)
	{
		switch(line)
		{
		case 1: mScore += 100;break;
		case 2: mScore += 300;break;
		case 3: mScore += 600;break;
		case 4: mScore += 1000;break;
		default: ;
		}
		if(mScore >= 2000 && mScore <4000)
		{
			setLevel(2);
		}
		else if(mScore >= 4000 && mScore < 6000)
		{
			setLevel(3);
		}
		else if(mScore >= 6000 && mScore < 8000)
		{
			setLevel(4);
		}
		else if(mScore >= 8000 && mScore < 10000)
		{
			setLevel(5);
		}
		else if(mScore >= 10000)
		{
			setLevel(6);
		}
	}
	//on dessine le canvas pour le jeu
	protected void onDraw(Canvas canvas)
	{
		switch(mGamestate)
		{
		case STATE_MENU:
			paintMenu(canvas);
			break;
		case STATE_PLAY:
			paintGame(canvas);
			break;
		case STATE_PAUSE:
			paintPause(canvas);
			break;
		case STATE_OVER:
			paintOver(canvas);
			break;
		default:;
		}
	}
	
	public boolean isGameOver()
	{
		return mCourt.isGameOver();
	}
	
	//Creer un evenement de touche:avant arriere,gauche,droite,et puis,definition 
	//des contidions quand on appuie une touche correspondante.
	public boolean onKeyDown(int keyCode,KeyEvent event)
	{
		switch(keyCode)
		{
		case KeyEvent.KEYCODE_DPAD_UP:
			if(mGamestate == STATE_PLAY)
			{
				if(!mIsPaused)
				{
					rotate();
					mMPlayer.playMoveVoice();
				}
			}
			else if(mGamestate == STATE_PAUSE)
			{
			}
			else if(mGamestate == STATE_MENU)
			{
				
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if(mGamestate == STATE_PLAY)
			{
				if(!mIsPaused)
				{
					moveDown();
					mMPlayer.playMoveVoice();
				}
			}
			else if(mGamestate == STATE_PAUSE)
			{
			}
			else if(mGamestate == STATE_MENU)
			{
				
			}
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if(mGamestate == STATE_PLAY)
			{
				if(!mIsPaused)
				{
					moveLeft();
					mMPlayer.playMoveVoice();
				}
			}
			else if(mGamestate == STATE_PAUSE)
			{
			}
			else if(mGamestate == STATE_MENU)
			{
				
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if(mGamestate == STATE_PLAY)
			{
				if(!mIsPaused)
				{
					moveRight();
					mMPlayer.playMoveVoice();
				}
			}
			else if(mGamestate == STATE_PAUSE)
			{
			}
			else if(mGamestate == STATE_MENU)
			{
				
			}
			break;
		case KeyEvent.KEYCODE_ENTER: ;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if(mGamestate == STATE_PLAY)
			{
				if(!mIsPaused)
				{
					fastDrop();
					mMPlayer.playMoveVoice();
				}
			}
			else if(mGamestate == STATE_PAUSE)
			{
			}
			else if(mGamestate == STATE_MENU)
			{
			}
			break;
			//
		case KeyEvent.KEYCODE_S:
			if(mGamestate == STATE_PLAY)
			{
				mIsPaused = true;
			}
			else if(mGamestate == STATE_PAUSE)
			{
				mIsPaused = false;
			}
			else if(mGamestate == STATE_MENU)
			{
				
			}
			break;
		case KeyEvent.KEYCODE_SPACE:
			mIsPaused = !mIsPaused;
			if(mIsPaused)
			{
				mRefreshHandler.pause();
			}
			else
			{
				mRefreshHandler.resume();
			}
			break;
			
		default: ;
		}
		return super.onKeyDown(keyCode,event);
	}
	
	//verifier l'etat de collision,si il y a pas de collision,on rapelle la 
	//fonction rotate()
	private void rotate()
	{
	
		if(!mIsCombo)
			mCurrentTile.rotateOnCourt(mCourt);
	}
	//verifier l'etat de collision,si il y a pas de collision,on rapelle la 
	//fonction moveDown()
	private void moveDown()
	{
		if(!mIsCombo)
		{
			if( ! mCurrentTile.moveDownOnCourt(mCourt) )
				mIsCombo = true;
		}
	}
	//verifier l'etat de collision,si il y a pas de collision,on rapelle la 
	//fonction moveLeft()
	private void moveLeft()
	{
		if(!mIsCombo)
		{
			mCurrentTile.moveLeftOnCourt(mCourt);
			
		}
	}
	//verifier l'etat de collision,si il y a pas de collision,on rapelle la 
	//fonction moveRight()
	private void moveRight()
	{
		if(!mIsCombo)
		{
			mCurrentTile.moveRightOnCourt(mCourt);
			
		}
		
	}
	
	private void fastDrop()
	{
		if(!mIsCombo)
		{
			mCurrentTile.fastDropOnCourt(mCourt);
			mIsCombo = true;
		}
	}
	//la fonction pour dessiner le menu
	private void paintMenu(Canvas canvas)
	{
		DrawTool.paintImage(canvas,mResourceStore.getMenuBackground(),0,0);
		DrawTool.paintImage(canvas,mResourceStore.getMenu(),0,SCREEN_HEIGHT/2 - mResourceStore.getMenu().getHeight()/2 );
		
	}
	//la fonction pour dessiner les objets de jeu
	private void paintGame(Canvas canvas)
	{
		mCourt.paintCourt(canvas);
		mCurrentTile.paintTile(canvas);
		mPaint.setTextSize(20);
		paintNextTile(canvas);
		paintSpeed(canvas);
		paintScore(canvas);
		paintDeLine(canvas);
	}
	
	private void paintNextTile(Canvas canvas)
	{
		int i,j;
		for(i = 0;i<4;i++)
		{
			for(j = 0;j<4;j++)
			{
				if(mNextTile.mTile[i][j] != 0)
				{
					DrawTool.paintImage( canvas,mResourceStore.getBlock(mNextTile.getColor()-1), 
							   (int)(Court.BEGIN_DRAW_X+getBlockDistance(Court.COURT_WIDTH) + getBlockDistance((float) (i+0.5)) ),
							   (int)( getBlockDistance((float)(j+0.5) ) )
						      );
				}
			}
		}
	}
	//la fonction pour dessiner le niveau de vitesse de jeu
	private void paintSpeed(Canvas canvas)
	{
		mPaint.setColor(Color.BLUE);
		canvas.drawText("Niveau:",getBlockDistance(Court.COURT_WIDTH)+getRightMarginToCourt(), getBlockDistance(9),mPaint);
		mPaint.setColor(Color.RED);
		canvas.drawText(String.valueOf(mSpeed),getBlockDistance(Court.COURT_WIDTH)+ 2*getRightMarginToCourt(), getBlockDistance(11),mPaint);
	}
	//la fonction pour dessiner le score dans le jeu
	private void paintScore(Canvas canvas)
	{
		mPaint.setColor(Color.BLUE);
		canvas.drawText("Score:",getBlockDistance(Court.COURT_WIDTH)+getRightMarginToCourt(), getBlockDistance(13),mPaint);
		mPaint.setColor(Color.RED);
		canvas.drawText(String.valueOf(mScore),getBlockDistance(Court.COURT_WIDTH)+ 2*getRightMarginToCourt(), getBlockDistance(15),mPaint);
	}
	//la fonction pour dessiner les rangees diminuee dans le jeu
	private void paintDeLine(Canvas canvas)
	{
		mPaint.setColor(Color.BLUE);
		canvas.drawText("Rangee:",getBlockDistance(Court.COURT_WIDTH)+getRightMarginToCourt(), getBlockDistance(17),mPaint);
		mPaint.setColor(Color.RED);
		canvas.drawText(String.valueOf(mDeLine),getBlockDistance(Court.COURT_WIDTH)+2*getRightMarginToCourt(), getBlockDistance(19),mPaint);
	}
	
	private float getBlockDistance(float blockNum)
	{
		return blockNum * Court.BLOCK_WIDTH;
	}
	
	private float getRightMarginToCourt()
	{
		return (float)10.0;
	}
	
	private void paintPause(Canvas canvas)
	{
		
	}
	//la fonction pour dessiner le terminaison de jeu
	private void paintOver(Canvas canvas)
	{
		paintGame(canvas);
		Paint paint = new Paint();
		paint.setTextSize(40);
		paint.setAntiAlias(true);
		paint.setARGB(0xe0,0xff,0x00,0x00);
		canvas.drawText("Game Over",getBlockDistance(1),getBlockDistance(Court.COURT_HEIGHT/2-2),paint);
		//DrawTool.paintImage(canvas,mResourceStore.getGameover(),0,SCREEN_HEIGHT/2 - mResourceStore.getGameover().getHeight()/2 );
	}
	
	


	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!Thread.currentThread().isInterrupted() )
		{
			Message ms = new Message();
			ms.what = RefreshHandler.MESSAGE_REFRESH;
			this.mRefreshHandler.sendMessage(ms);
			try
			{/*RefreshHandler.DELAY_MILLIS le temps de dormir de thread*/
				Thread.sleep(mMoveDelay);
			}
			catch(InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
			
		}
		
	}


	public void setLevel(int level)
	{
		mSpeed = level;	
		mMoveDelay = (long) (600*(1.0 - (double)mSpeed / 7.0 ) );
	}
	
	public void setVoice(boolean isVoice)
	{
		mIsVoice = isVoice;
		mMPlayer.setMute(!mIsVoice);
	}

	//la fonction pour transmettre les types de  toutes les informations.
	public void restoreGame()
	{
		Properties pro = new Properties();
		try
		{
			FileInputStream in = mContext.openFileInput(DATAFILE);
			pro.load(in);
			in.close();
		}
		catch(IOException e)
		{
			Log.i(TAG,"file open failed in restoreGame()");
			return;
		}
		
		mGamestate = Integer.valueOf(pro.get("gamestate").toString() );
		mSpeed = Integer.valueOf(pro.get("speed").toString() );
		setLevel(mSpeed);
		mScore = Integer.valueOf(pro.get("score").toString() );
		mDeLine = Integer.valueOf(pro.get("deLine").toString() );
		mIsVoice = Boolean.valueOf(pro.get("isVoice").toString() );
		mIsCombo =  Boolean.valueOf(pro.get("isCombo").toString() );
		mIsPaused =  Boolean.valueOf(pro.get("isPaused").toString() );
		
		restoreCourt(pro);
		restoreTile(pro,mCurrentTile);
		restoreTile(pro,mNextTile);
	}
	//la fonction pour transmettre le map precedent de jeu quand on revient dans le jeu
	private void restoreCourt(Properties pro)
	{
		int[][] matrix = mCourt.getMatrix();
		int i,j;
		for(i = 0;i<Court.COURT_WIDTH;i++)
		{
			for(j = 0;j<Court.COURT_HEIGHT;j++)
			{
				matrix[i][j] = Integer.valueOf(pro.get("courtMatrix"+i+j).toString() );
			}
		}
	}
	//la fonction pour transmettre le informations de block precedent de jeu 
	private void restoreTile(Properties pro,TileView tile)
	{
		int[][] matrix = tile.getMatrix();
		int i,j;
		for(i = 0;i<4;i++)
		{
			for(j = 0;j<4;j++)
			{
				matrix[i][j] = Integer.valueOf(pro.get("tileMatrix"+i+j).toString() );
			}
		}
		tile.setColor(Integer.valueOf(pro.get("tileColor").toString() ));
		tile.setShape(Integer.valueOf(pro.get("tileShape").toString() ));
		tile.setOffsetX(Integer.valueOf(pro.get("tileOffsetX").toString() ));
		tile.setOffsetY(Integer.valueOf(pro.get("tileOffsetY").toString() ));
	}
	
	//la fonction pour sauvegarder toutes les information de jeu
	public void saveGame()
	{
		Properties pro = new Properties();
		
		pro.put("gamestate",String.valueOf(mGamestate));
		pro.put("speed",String.valueOf(mSpeed));
		pro.put("score",String.valueOf(mScore));
		pro.put("deLine",String.valueOf(mDeLine));
		Boolean b = new Boolean(mIsVoice);
		pro.put("isVoice",b.toString());
		b = new Boolean(mIsCombo);
		pro.put("isCombo",b.toString());
		b = new Boolean(mIsPaused);
		pro.put("isPaused",b.toString());
		
		saveCourt(pro);
		saveTile(pro,mCurrentTile);
		saveTile(pro,mNextTile);
		
		try
		{
			FileOutputStream stream = mContext.openFileOutput(DATAFILE,Context.MODE_WORLD_WRITEABLE);
			pro.store(stream,"");
			stream.close();
		}
		catch(IOException e)
		{
			Log.i(TAG,"ioexeption in saveGame()");
			return;
			
		}
		
	}
	//la fonction pour sauvegarder le matrice de map
	private void saveCourt(Properties pro)
	{
		int[][] court = mCourt.getMatrix();
		int i,j;
		for(i = 0;i<Court.COURT_WIDTH;i++)
		{
			for(j = 0;j<Court.COURT_HEIGHT;j++)
			{
				pro.put("courtMatrix"+i+j,String.valueOf(court[i][j]) );
			}
		}
	}
	//la fonction pour sauvegarder les infos de block
	private void saveTile(Properties pro,TileView tile)
	{
		int[][] matrix = tile.getMatrix();
		int i,j;
		for(i =0;i<4;i++)
		{
			for(j = 0;j<4;j++)
			{
				pro.put("tileMatrix"+i+j,String.valueOf(matrix[i][j]) );
			}
		}
		pro.put("tileColor",String.valueOf(tile.getColor() ) );
		pro.put("tileShape",String.valueOf(tile.getShape() ) );
		pro.put("tileOffsetX",String.valueOf(tile.getOffsetX() ) );
		pro.put("tileOffsetY",String.valueOf(tile.getOffsetY() ) );
	}

	public void onPause() {
		mRefreshHandler.pause();
		mIsPaused = true;
		
	}


	public void onResume() {
		mRefreshHandler.resume();
		mIsPaused = false;
	}
	
	public void freeResources()
	{
		mMPlayer.free();
	}
}
