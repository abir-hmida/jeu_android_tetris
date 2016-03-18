package com.mime.cassetete;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;


public class ActivityGame extends Activity {

	private static final String TAG = "ActivityGame";
	casseteteView mCasseView = null;
	
//	private static final int DIALOG_ID = 1;
	
	public void onCreate(Bundle saved)
	{
		super.onCreate(saved);
		//definition l'affichage de la fenetre:non titre
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		init();
	}
	
	/**
	 * la fonction initialisation,instancier la classe TetrisView
	 * definition un intent pour obtenir les infos retournees de la classe ActivityMain
	 */
	private void init()
	{
		mCasseView = new casseteteView(this);
		Intent intent = getIntent();
		int level = intent.getIntExtra(ActivityMain.LEVEL,1);
		mCasseView.setLevel(level);
		int flag = intent.getFlags();
		if(flag == ActivityMain.FLAG_CONTINUE_LAST_GAME)
		{
			mCasseView.restoreGame();
		}
		// obtenir l'etat d'activation de son du jeu precedent
		boolean isVoice = intent.getBooleanExtra(ActivityMain.VOICE,true);
		mCasseView.setVoice(isVoice);
		setContentView(mCasseView);
	}
	
	public void onPause()
	{
//		ranking();
		mCasseView.onPause();
		super.onPause();
		
	}
	
	public void onResume()
	{
		super.onResume();
		mCasseView.onResume();
		
		
	}
	
	public void onStop()
	{
		super.onStop();
		mCasseView.saveGame();
		mCasseView.freeResources();
		
	}
	
//	public void ranking()
//	{
//		showDialog(DIALOG_ID);
//		Log.i(TAG,"ranking now");
//	}
	
//	protected Dialog onCreateDialog(int id)
//	{
//		if(id == DIALOG_ID)
//		{
//			Builder builder = new AlertDialog.Builder(this);
//			builder.setIcon(R.drawable.icon);
//			builder.setTitle("恭喜进入前三名");
//			return builder.create();
//		}
//		return null;
//	}

}
