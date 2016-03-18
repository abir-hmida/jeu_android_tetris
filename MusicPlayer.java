package com.mime.cassetete;

import com.mime.cassetete.R;

import android.content.Context;
import android.media.MediaPlayer;


//on a definit 2 ressource de son dans le repertoire raw,l'un est active quand on
//deplacer les blocks,l'autre est active quand les blocks atteint sur le fond de map
public class MusicPlayer {
	
	private MediaPlayer mMoveVoice = null;
	private MediaPlayer mBombVoice = null;
	private boolean mIsMute = false;
	
	
	public MusicPlayer(Context context)
	{
		mMoveVoice = MediaPlayer.create(context,R.raw.move);
		mBombVoice = MediaPlayer.create(context,R.raw.bomb);
	}
	
	public void playMoveVoice()
	{
		if(mIsMute)
			return;
		mMoveVoice.start();
	}
	
	public void playBombVoice()
	{
		if(mIsMute)
		{
			return;
		}
		mBombVoice.start();
	}

	public void setMute(boolean b)
	{
		mIsMute = b;
	}

	public void free()
	{
		mMoveVoice.release();
		mBombVoice.release();
	}
			

}
