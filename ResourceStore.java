package com.mime.cassetete;

import com.mime.cassetete.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;



public class ResourceStore {
	private static Bitmap mBackground = null;
	private static Bitmap[] mBlocks = null;
	private static Bitmap mMenuBackground = null;
	private static Bitmap mMenu = null;
	private static Bitmap mSpeed = null;
	private static Bitmap mLine = null;
	private static Bitmap mScore = null;
	private static Bitmap mGameover = null;
	
	private Context mContext = null;
	private Resources mR     = null;
	
	// definition une Ressources mR et un Context en utilisant la getResources()
	//pour obtenir les ressources dans le projet,et puis les creer.
	public ResourceStore(Context context)
	{
		mContext = context;
		mR       = mContext.getResources();
		if(mBackground == null)
			mBackground = createImage(mR.getDrawable(R.drawable.courtbg), Court.COURT_WIDTH*Court.BLOCK_WIDTH, casseteteView.SCREEN_HEIGHT );
		if(mMenuBackground == null)
			mMenuBackground = createImage(mR.getDrawable(R.drawable.menubg),casseteteView.SCREEN_WIDTH,casseteteView.SCREEN_HEIGHT );
		if(mMenu == null)
			mMenu           = createImage(mR.getDrawable(R.drawable.menu),200,100 );
		if(mSpeed == null)
			mSpeed          = createImage(mR.getDrawable(R.drawable.speed),200,100);
		if(mLine == null)
			mLine           = createImage(mR.getDrawable(R.drawable.line),200,100);
		if(mScore == null)
			mScore          = createImage(mR.getDrawable(R.drawable.score),200,100);
		if(mGameover == null)
			mGameover       = createImage(mR.getDrawable(R.drawable.gameover),200,100);

		
		//pour chaque block,on a utilise la differente couleur qui a ete ajoutees
		//dans le repertoire de res/drawable,de block0 a block7
		if(mBlocks == null)
		{
			mBlocks = new Bitmap[8];
			for(int i = 0;i<8;i++)
			{
			//			loadImage(R.drawable.block0+i,mBlocks[i]);
				mBlocks[i] = createImage(mR.getDrawable(R.drawable.block0+i),Court.BLOCK_WIDTH,Court.BLOCK_WIDTH);
			}
		}
	}
	
	public Bitmap getCourtBackground()
	{
		return mBackground;
	}
	
	public Bitmap getMenuBackground()
	{
		return mMenuBackground;
	}
	
	public Bitmap getMenu()
	{
		return mMenu;
	}
	
	public Bitmap getBlock(int index)
	{
		return mBlocks[index];
	}
	
	public Bitmap getGameover()
	{
		return mGameover;
	}
	
	
	public void loadImage(int index,Bitmap bitmap)
	{
		bitmap = ( (BitmapDrawable)mContext.getResources().getDrawable(index) ).getBitmap();
	}
	//overloading la fonction createImage pour que on puisse regler les differents
	//type de donnees.
	public static Bitmap createImage(Drawable tile, int w, int h) {
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		tile.setBounds(0, 0, w, h);
		tile.draw(canvas);
		return bitmap;
	}
}
