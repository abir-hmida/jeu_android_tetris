package com.mime.cassetete;

import com.mime.cassetete.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;



//dans avtivityRank,on a definition une fonction de classer les scores de chaque
//joueur en utilisant la base des donnees android
public class ActivityRank extends Activity {

	private RankDatabase mDatabase = null;
	private ListView mListView = null;
	
	public void onCreate(Bundle saved)
	{
		super.onCreate(saved);
		setTitle("Claseement des scores");
		setContentView(R.layout.rank);
//		init();
	}
	
//	private void init()
//	{
//		// ..get databse
//		ListView mListView = (ListView)findViewById(R.id.rank_list);
//		
//		SimpleAdapter adapter = new SimpleCursorAdapter(this,
//				cur,
//				R.layout.list_item,
//				new String[]{"rank,scroe,name"},
//				new int[] {R.id.list_item_rank,R.id.list_item_score,R.list_item_name} );
//		mListView.setAdapter(adapter);
//	}
		
}
