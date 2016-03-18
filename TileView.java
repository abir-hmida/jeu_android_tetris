package com.mime.cassetete;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;




public class TileView{
	/*un tableau 2 dimension pour chauqe block*/
	int[][] mTile = new int[4][4];
	Random mRand = null;
	int mColor = 1;
	/*type de block*/
	int mShape = 0;
	/*la position X et Y de depart*/
	int mOffsetX = (Court.COURT_WIDTH-4)/2+1;
	int mOffsetY = 0;
	
	private Context mContext = null;
	private ResourceStore mRs = null;
	
	public TileView(Context context) {
		mContext = context;
		
		mRs = new ResourceStore(context);
		
		init();
		Log.i("casse","init in TileView OK");
		// TODO Auto-generated constructor stub
	}
	//la fonction d'initialisation pour produire les types de block
	private void init()
	{
		mRand = new Random();
		mShape = Math.abs(mRand.nextInt() % 28);
		
		mColor = Math.abs(mRand.nextInt() % 8) + 1;
		
		if(mTile == null)
		{
			return;
		}
		int i,j;
		for(i = 0;i<4;i++)
		{
			for(j = 0;j<4;j++)
			{
				mTile[i][j] = TileStore.store[mShape][i][j];
			}
		}	
	}
	
	//la fonction de rotation de block,dans le sens de l'heure
	public boolean rotateOnCourt(Court court) {
		int tempX = 0, tempY = 0;
		int tempShape;
		int[][] tempTile = new int[4][4];

		
		tempShape = mShape;
		if (tempShape % 4 > 0) {
			tempShape--;
		} else {
			tempShape += 3;
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				tempTile[i][j] = TileStore.store[tempShape][i][j];
			}
		}

		tempX = mOffsetX;
		tempY = mOffsetY;
		boolean canTurn = false;
		
		if( court.availableForTile(tempTile,tempX,tempY) )
		{
			canTurn = true;
		}	
		else if(court.availableForTile(tempTile,tempX-1,tempY) )
		{
			canTurn = true;
			tempX--;
		}
		else if(court.availableForTile(tempTile,tempX-2,tempY) )
		{
			canTurn =true;
			tempX -=2;
		}
		else if(court.availableForTile(tempTile,tempX+1,tempY) )
		{
			canTurn = true;
			tempX++;
		}
		else if(court.availableForTile(tempTile,tempX+2,tempY) )
		{
			canTurn = true;
			tempX += 2;
		}
		
		if (canTurn) {
			mShape = tempShape;
			mOffsetX = tempX;
			mOffsetY = tempY;
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					mTile[i][j] = tempTile[i][j];
				}
			}
			return true;
		}
		return false;
	}

	//la fonction pour deplacer a droite
	public boolean moveRightOnCourt(Court court) {
		Log.i("casse","here is moveRightOnCourt");
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (mTile[i][j] != 0) {
					if ( !court.isSpace(mOffsetX + i + 1, mOffsetY + j) ) {
						return false;
					}
				}
			}
		}
		++mOffsetX;
		return true;
	}
	//la fonction pour deplacer a gauche
	public boolean moveLeftOnCourt(Court court) {
		int i,j;
		for (i = 0; i < 4; i++) {
			for (j = 0; j < 4; j++) {
				if (mTile[i][j] != 0) {
					if (!court.isSpace(mOffsetX + i - 1, mOffsetY + j)) {
						return false;
					}
				}
			}
		}
		mOffsetX--;
		return true;
	}
	//la fonction pour tomper automatiquement
	public boolean moveDownOnCourt(Court court) {
		int i,j;
		for (i = 0; i < 4; i++) {
			for (j = 0; j < 4; j++) {
				if (mTile[i][j] != 0) {
					if (!court.isSpace(mOffsetX + i, mOffsetY + j + 1)
							|| isUnderBaseline(mOffsetY+j+1) ) {
						return false;
					}
				}
			}
		}
		mOffsetY++;
		return true;
	}
	

	//la fonction de tomper rapidement
	public boolean fastDropOnCourt(Court court) {
		int i,j,k;
		int step = Court.COURT_HEIGHT;
		for (i = 0; i < 4; i++) {
			for (j = 0; j < 4; j++) {
				if (mTile[i][j] != 0) {
					for (k = mOffsetY + j; k < Court.COURT_HEIGHT; k++) {
						if (!court.isSpace(mOffsetX + i, k + 1)
								|| isUnderBaseline(k+1) ) {
							if (step > k - mOffsetY - j) {
								step = k - mOffsetY - j;
							}
						}
					}
				}
			}
		}
		mOffsetY += step;
		if (step > 0)
			return true;
		return false;
	}
	//la fonction pour tester que le block arrive le plus bas au niveau de cadre
	private boolean isUnderBaseline(int posY )
	{
		if(posY >= Court.COURT_HEIGHT)
			return true;
		return false;
		
	}
	//retourner la position de decalage de X
	public int getOffsetX()
	{
		return mOffsetX;
	}
	//mise a jour de la position de X
	public void setOffsetX(int offsetX)
	{
		mOffsetX = offsetX;
	}
	//retourner la position de decalage de Y
	public int getOffsetY()
	{
		return mOffsetY;
	}
	//mise a jour de la position de X
	public void setOffsetY(int offsetY)
	{
		mOffsetY = offsetY;
	}
	//retourner la couleur
	public int getColor()
	{
		return mColor;
	}
//la fonction pour dessiner le block
	public void paintTile(Canvas canvas)
	{
		ResourceStore rs = new ResourceStore(mContext);
		Paint paint = new Paint();
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++) 
			{
				if (mTile[i][j] != 0) 
				{
					canvas.drawBitmap(mRs.getBlock(mColor -1), Court.BEGIN_DRAW_X
							+ (i + mOffsetX) * Court.BLOCK_WIDTH, Court.BEGIN_DRAW_Y
							+ (j + mOffsetY) * Court.BLOCK_WIDTH, paint);
				}
			}
		}
		
	}
	//retourner le block
	public int[][] getMatrix()
	{
		return mTile;
	}

	public int getShape() {
		// TODO Auto-generated method stub
		return mShape;
	}

	public void setColor(int color) 
	{
		mColor = color;
		
	}
	
	public void setShape(int shape)
	{
		mShape = shape;
	}
	
}
