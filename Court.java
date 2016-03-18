package com.mime.cassetete;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;



public class Court {
	public final static int COURT_WIDTH  = 11;
	public final static int COURT_HEIGHT = 23+4;
	
	public final static int BLOCK_WIDTH  = 20;
	public final static int BLOCK_HEIGHT = 20;
	
	public final static int ABOVE_VISIBLE_TOP = 4;
	//les variables BEGIN_DRAW_X et BEGIN_DRAW_Y definissent la position de depart
	//de block,icic BEGIN_DRAW_Y est effectivement negative,parce qu'on voudras laisser les blocks partir
	//a l'exterieur de l'ecran,ca veut dire une vue cachee.
	public final static int BEGIN_DRAW_X = 0;
	public final static int BEGIN_DRAW_Y = casseteteView.SCREEN_HEIGHT - Court.BLOCK_WIDTH * Court.COURT_HEIGHT;

	
	private int[][] mCourt = new int[COURT_WIDTH][COURT_HEIGHT];
	private Context mContext = null;
	private ResourceStore mRs = null;
	
	public Court(Context context)
	{
		mContext = context;
		mRs = new ResourceStore(context);
		clearCourt();
		
	}
	//la fonction clearCourt() pour initialisation de map,tout est a 0..
	public void clearCourt()
	{
		int i,j;
		for(i=0;i<COURT_WIDTH;i++)
		{
			for(j=0;j<COURT_HEIGHT;j++)
			{
				mCourt[i][j] = 0;
			}
		}
	}
	//la fonction pour tester la terminaison de jeu,on fait un boucle pour savoir
	//est-ce que la ranggee le plus haut a block ou pas,si oui,le jeu termine
	//sinon,on continue.Ici on teste le map[i][4],parce que on a definit une partie
	//cachee dans notre vue de map,la varible ABOVE_VISIBLE_TOP correspond a
	//l'axe X reel au depart de block
	public boolean isGameOver()
	{
	
		for(int i=0;i<COURT_WIDTH;i++)
		{
			if(mCourt[i][ABOVE_VISIBLE_TOP] != 0)
				return true;
		}
		return false;
		
	}
	//la fonction pour determiner est-ce qu'il y a block dans la rangee ou pas
	//ca sert aux fonctions highestFullRowIndex() et lowestFullRowIndex()
	//pour diminuer les rangees qui sont completes.
	public boolean isSpace(int posX,int posY)
	{
		if (posX < 0 || posX >= COURT_WIDTH) 
			return false;
		if (posY < 0 || posY >= COURT_HEIGHT) 
			return false;
		if(0 == mCourt[posX][posY])
			return true;
		return false;
	}
	
	public boolean availableForTile(int[][] tile,int x,int y)
	{
		for (int i = 0; i < 4; i++) {
			for (int j  = 0; j < 4; j++) {
				if (tile[i][j] != 0) {
					if (!isSpace(x + i, y + j)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public void placeTile(TileView tile)
	{
		int i,j;
		for (i = 0; i < 4; i++)
		{
			for (j = 0; j < 4; j++) 
			{
				if (tile.mTile[i][j] != 0)
				{
					mCourt[tile.getOffsetX() + i][tile.getOffsetY() + j] = tile.getColor();
				}
			}
		}
	}
	//la fonction pour diminuer la rangee par rapport au nombre de rangee
	public int removeLines()
	{
		int high = 0;
		int low = COURT_HEIGHT;
		
		high = highestFullRowIndex();
		low  = lowestFullRowIndex();
		int lineCount = low - high +1;
		if(lineCount > 0)
		{
			eliminateRows(high,lineCount);
			return lineCount;
		}
		return 0;
		
		
		
//		for (i = 0; i < COURT_HEIGHT; i++)
//		{
//			removeable = true;
//			for (j = 0; j < COURT_WIDTH && removeable ; j++)
//			{
//				if (isSpace(j, i))
//				{
//					beginRow++;
//					removeable = false;
//					break;
//				}
//			}
//			if(removeable)
//			{
//				lineCount++;
//			}
//			else if()
//		}
//		if(lineCount != 0)
//		{
//			
//			eliminateRows(beginRow,lineCount);
//		}
	}
	
	private void eliminateRows(int highRow,int rowAmount)
	{
		int i,j;
		for(i = highRow+rowAmount-1;i >= rowAmount;i--)
		{
			for(j = 0;j < COURT_WIDTH;j++)
			{
				mCourt[j][i] = mCourt[j][i-rowAmount];
			}
		}
	}
	
	private int highestFullRowIndex()
	{
		int result = 0;
		boolean removeable = true;
		int i,j;
		for(i = 0;i < COURT_HEIGHT;i++)
		{
			removeable = true;
			for(j = 0;j<COURT_WIDTH && removeable;j++)
			{
				if(isSpace(j,i) )
				{
					result++;
					removeable = false;
				}
			}
			if(removeable)
				break;
		}
		return result;
	}
	
	private int lowestFullRowIndex()
	{
		int result = COURT_HEIGHT-1;
		boolean removeable = true;
		int i,j;
		for(i = COURT_HEIGHT - 1;i >= 0;i--)
		{
			removeable = true;
			for(j = 0;j<COURT_WIDTH && removeable;j++)
			{
				if(isSpace(j,i) )
				{
					result--;
					removeable = false;
				}
			}
			if(removeable)
				break;
		}
		return result;
	}
	
	
	
//	private void removeSingleLine(int lineIndex)
//	{
//		int i,j;
//		for(i=lineIndex; i > 0; i--)
//		{
//			for (j = 0; j < COURT_WIDTH; j++)
//			{
//				mCourt[j][i] = mCourt[j][i - 1];
//			}
//		}
//	}
	
	private void removeSingleLine(int lineIndex,int time)
	{
		int t,i,j;
		for(t = 0;t<time;t++)
		{
			for(i = lineIndex; i > 0; i--)
			{
				for (j = 0; j < COURT_WIDTH; j++)
				{
					mCourt[j][i] = mCourt[j][i - 1];
				}
			}

		}
	}
	
	public void paintCourt(Canvas canvas)
	{
		
		Paint paint = new Paint();
//		paint.setColor(Color.WHITE);
//		canvas.drawRect(0, 0, Court.BLOCK_WIDTH * Court.COURT_WIDTH, TetrisView.SCREEN_HEIGHT, paint);
		paint.setAlpha(0x60);
		canvas.drawBitmap(mRs.getCourtBackground(),0, 0, paint);
		paint.setAlpha(0xee);
		for (int i = 0; i < Court.COURT_WIDTH; i++) 
		{
			for (int j = 0; j < Court.COURT_HEIGHT; j++)
			{
				if (mCourt[i][j] != 0) 
				{
					canvas.drawBitmap(mRs.getBlock(mCourt[i][j] - 1), BEGIN_DRAW_X + i
							* Court.BLOCK_WIDTH, BEGIN_DRAW_Y + j * Court.BLOCK_WIDTH, paint);
				}
			}
		}
		
	}
	
	public int[][] getMatrix()
	{
		return mCourt;
	}
	
}
