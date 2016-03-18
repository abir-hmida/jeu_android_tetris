package com.mime.cassetete;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


//definiton de la classe RefreshHandler pour actualiser l'etat de UI,ici,on envoie 
//le message dans le fil d'attente pour realiser 2 fonctions invalidate()
// et logic();
class RefreshHandler extends Handler
{
	final static int MESSAGE_REFRESH = 0xeeeeeeee;
	
	final static int DELAY_MILLIS = 100;
	casseteteView mV = null;
	boolean mIsPaused = false;
	
	public RefreshHandler(casseteteView v)
	{
		super();
		mV = v;
	}
	
	public void handleMessage(Message ms)
	{
		if( !mIsPaused )
		{
			if(ms.what == MESSAGE_REFRESH)
			{
				mV.logic();
				mV.invalidate();
			}
		}
	}
		
	
	public void pause()
	{
		mIsPaused = true;
	}
	

	public void resume()
	{
		mIsPaused = false;
	}
	
}
