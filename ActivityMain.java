package com.mime.cassetete;

import com.mime.cassetete.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


public class ActivityMain extends Activity {
	//Declaration des variables boutons,fanions des etat etc...
	public static final int FLAG_NEW_GAME = 0;
	public static final int FLAG_CONTINUE_LAST_GAME = 1;
	
	public static final String FILENAME = "settingInfo";
	public static final String LEVEL = "level";
	public static final String VOICE = "voice";
	
	
	private int mLevel = 1;
	
	private Button btNewgame = null;
	private Button btContinue = null;
	private Button btHelp = null;
	private Button btRank = null;
	private Button btPre = null;
	private Button btNext = null;
	private Button btExit = null;
	
	private TextView tvLevel = null;
	private CheckBox cbVoice = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
       /** mise en page des boutons,Textview et checkbox pour l'activation du son
        *  par rapport au layout menu.
        *  ensuite definition des evenements a l'ecoute pour chaque bouton
        */
        btNewgame = (Button)findViewById(R.id.bt_new);
        btContinue = (Button)findViewById(R.id.bt_continue);
        btHelp = (Button)findViewById(R.id.bt_help);
        btRank = (Button)findViewById(R.id.bt_rank);
        btPre = (Button)findViewById(R.id.bt_pre);
        btNext = (Button)findViewById(R.id.bt_next);
        btExit = (Button)findViewById(R.id.bt_exit);
        
        tvLevel = (TextView)findViewById(R.id.tv_speed);
       
        cbVoice = (CheckBox)findViewById(R.id.cb_voice);
        
        btNewgame.setOnClickListener(buttonListener);
        btContinue.setOnClickListener(buttonListener);
        btHelp.setOnClickListener(buttonListener);
        btRank.setOnClickListener(buttonListener);
        btPre.setOnClickListener(buttonListener);
        btNext.setOnClickListener(buttonListener);
        btExit.setOnClickListener(buttonListener);
        restoreSettings();
    }
    
    private Button.OnClickListener buttonListener = new Button.OnClickListener()
    {

		@Override
		public void onClick(View v) {
			if(v == btNewgame)
			{
				Intent intent = new Intent(ActivityMain.this,ActivityGame.class);
				intent.setFlags(FLAG_NEW_GAME);
				intent.putExtra(VOICE,cbVoice.isChecked());
				intent.putExtra(LEVEL,mLevel);
				startActivity(intent);
				return;
			}
			if(v == btContinue)
			{
				Intent intent = new Intent(ActivityMain.this,ActivityGame.class);
				intent.setFlags(FLAG_CONTINUE_LAST_GAME);
				intent.putExtra(VOICE,cbVoice.isChecked());
				startActivity(intent);
				return;
			}
			if(v == btHelp)
			{
				Intent intent = new Intent(ActivityMain.this,ActivityHelp.class);
				startActivity(intent);
				return;
			}
			if(v == btRank)
			{
				Intent intent = new Intent(ActivityMain.this,ActivityRank.class);
				startActivity(intent);
				return;
			}
		    //On a definit,dans Activity Main,2 boutons pour choisir le niveau
		    //de jeu(la vitesse de block) parmis les 6 niveaus.
			//algo: btPre et btNext ci-dessous
		       
		     if(v == btPre)
			{
				btPre.setBackgroundColor(0xffc0c0c0);
				String s = tvLevel.getText().toString();
				int level = Integer.parseInt(s);
				--level;
				level = (level-1+casseteteView.MAX_LEVEL) % casseteteView.MAX_LEVEL;
				++level;
				s = String.valueOf(level);
				tvLevel.setText(s);
				mLevel = level;
				btPre.setBackgroundColor(0x80cfcfcf);
				return;
			}
			if(v == btNext)
			{
				btNext.setBackgroundColor(0xffc0c0c0);
				String s = tvLevel.getText().toString();
				int level = Integer.parseInt(s);
				--level;
				level = (level+1) % casseteteView.MAX_LEVEL;
				++level;
				s = String.valueOf(level);
				tvLevel.setText(s);
				mLevel = level;
				btNext.setBackgroundColor(0x80cfcfcf);
				return;
			}
			if(v == btExit)
			{
				ActivityMain.this.finish();
			}
		}
    };
    /** 
     *  Dans les fonctions saveSettings() et restoreSettings()
     *  definition de SharedPreferences dans la fonction saveSettings pour  
     *  sauvegarder les infos d'utilisateur qui est utilisees dans le jeu precedent
     *  sharedpreferences est comme la fonction intent,mais il est souvant utilisee
     *  pour stocker les donnees legeres.
     *  
     */
    private void saveSettings()
    {
    	SharedPreferences settings = getSharedPreferences(FILENAME,0);
    	settings.edit()
    	.putInt(LEVEL,mLevel)
    	.putBoolean(VOICE,cbVoice.isChecked())
    	.commit();
    }
    
    private void restoreSettings()
    {
    	SharedPreferences settings = getSharedPreferences(FILENAME,0);
    	mLevel = settings.getInt(LEVEL,1);
    	boolean hasVoice = settings.getBoolean(VOICE,true);
    	tvLevel.setText(String.valueOf(mLevel));
    	cbVoice.setChecked(hasVoice);
    }
    
    public void onStop()
    {
    	super.onStop();
    	saveSettings();
    }
}