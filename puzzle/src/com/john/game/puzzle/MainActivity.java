package com.john.game.puzzle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.TextView;

import com.john.game.puzzle.view.PuzzleLayout;
import com.john.game.puzzle.view.PuzzleLayout.PuzzleListener;


public class MainActivity extends Activity {
		
		private PuzzleLayout mPuzzleLayout;
		private TextView  mLevel;
		private TextView  mTime;
		
	
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main); 
			
			mTime =  (TextView) findViewById(R.id.id_time);
			mLevel = (TextView) findViewById(R.id.id_level);
			
			mPuzzleLayout = (PuzzleLayout) findViewById(R.id.id_puzzle);
			mPuzzleLayout.setTimeOff(true);
			mPuzzleLayout.setOnPuzzleListener(new PuzzleListener() {
				
				@Override
				public void timeChanged(int currentTime) {
					mTime.setText("" + currentTime);
					
				}
				
				@Override
				public void nextLevel(final int nextLevel) {
					
					new AlertDialog.Builder(MainActivity.this)
					.setTitle("Game Info").setMessage("恭喜通关")
					.setPositiveButton("下一关", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							mPuzzleLayout.nextLevel();
							mLevel.setText(""+nextLevel);
						}
					}).show();
					
				}
				
				@Override
				public void gameover() {
						new AlertDialog.Builder(MainActivity.this)
						.setTitle("Game Info").setMessage("时间到")
						.setPositiveButton("重新开始", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							mPuzzleLayout.restart();
						}
					}).setNegativeButton("退出游戏", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
									finish();
						}
					})
					.show();
					
				}
			});
		}
		
		@Override
		protected void onPause() {
		// TODO Auto-generated method stub
			super.onPause();
			mPuzzleLayout.pause();
		}
		
		@Override
		protected void onResume() {
		// TODO Auto-generated method stub
			super.onResume();
			mPuzzleLayout.resume();
		}
}
