package com.mime.cassetete;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class DrawTool
{

	public static void paintImage(Canvas canvas,Bitmap bitmap,int x,int y)
	{
		canvas.drawBitmap(bitmap,x,y,null);
	}
	
	

}
