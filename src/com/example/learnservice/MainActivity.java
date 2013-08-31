package com.example.learnservice;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
    /**
     *  MainActivity 透過 intent 啟動 TimerService,
     *  要把 totalSec 給傳過去.
     * 
     */
	
	private TextView textTimer;
	private Button buttonToggle;
	private Context mContext;
	private UIHandler mUIHandler;
	private static final String iniRemainTime = "00:00:00";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textTimer = (TextView) findViewById (R.id.text_timer);
		buttonToggle = (Button) findViewById (R.id.button_toggle);
		mContext = this;
		
		
		textTimer.setOnClickListener(new TextView.OnClickListener(){ 
            @Override
            public void onClick(View v) {
            	
            	if (TimerService.mState.equals(TimerService.State.Stopped)){
            		showTimerDialog();            	
            	}else{
            		Toast.makeText(mContext, "倒數中...", Toast.LENGTH_SHORT).show();
            	}
            }

			         
        });
		
		buttonToggle.setOnClickListener(new Button.OnClickListener(){ 
            @Override
            public void onClick(View v) {
            	 if (TimerService.getTimerState().equals(TimerService.State.Stopped)){
            		 if(textTimer.getText().equals(iniRemainTime)){
            			 Toast.makeText(mContext, "請先設置時間!!", Toast.LENGTH_SHORT).show();
            		 }else{
            			 Intent intent = new Intent(TimerService.ACTION_PLAY);
                		 intent.putExtra(TimerService.TAG_TOTAL_SECONT, TimerService.getRemainSeconds());
                		 startService(intent);
                		 buttonToggle.setText("停止");
            		 }          		             		 
            	 }else{
            		 // send intent to make service stop
            		 Intent intent = new Intent(TimerService.ACTION_STOP);
            		 startService(intent);
            		 buttonToggle.setText("開始");  
            	 }
            }         
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void setRemainTimeText(String text){
		textTimer.setText(text);
	}
	
	public void changeButtonState(){
		buttonToggle.setText("開始");
	}
	
	public void makeFinishToast(){
		Toast.makeText(mContext, "倒數結束!!", Toast.LENGTH_SHORT).show();
	}
	
	private void showTimerDialog() {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
    	View customDialog = inflater.inflate(R.layout.custom_dialog, null);
    	final EditText editHour = (EditText) customDialog.findViewById(R.id.edit_hour);
    	final EditText editMin = (EditText) customDialog.findViewById(R.id.edit_min);
    	final EditText editSec = (EditText) customDialog.findViewById(R.id.edit_sec);
    	editHour.setRawInputType(Configuration.KEYBOARD_12KEY);
    	editMin.setRawInputType(Configuration.KEYBOARD_12KEY);
    	editSec.setRawInputType(Configuration.KEYBOARD_12KEY);
    	
    	AlertDialog.Builder settingDialog = new AlertDialog.Builder(mContext)
		.setTitle("設置倒數時間")
		.setView(customDialog)
		.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						String stringTimer = "";
						int intHour = 0;
						int intMin = 0;
						int intSec = 0;
						if (!editHour.getText().toString().equals("")){
							intHour = Integer.valueOf(editHour.getText().toString());
						}
						if (!editMin.getText().toString().equals("")){
							intMin = Integer.valueOf(editMin.getText().toString());
						}
						if (!editSec.getText().toString().equals("")){
							intSec = Integer.valueOf(editSec.getText().toString());
						}
					   						
						if (intHour == 0){
							stringTimer = "00:";
						}else if ( 0 < intHour && intHour < 10){
							stringTimer = "0"+ editHour.getText().toString() + ":";
						}else{
							stringTimer =  editHour.getText().toString() + ":";
						}
						
						if (intMin == 0){
							stringTimer = stringTimer + "00:";
						}else if ( 0 < intMin && intMin < 10){
							stringTimer = stringTimer + "0"+ editMin.getText().toString() + ":";
						}else{
							stringTimer =  stringTimer + editMin.getText().toString() + ":";
						}
						
						if (intSec == 0){
							stringTimer = stringTimer + "00";
						}else if ( 0 < intSec && intSec < 10){
							stringTimer = stringTimer + "0"+ editSec.getText().toString();
						}else{
							stringTimer =  stringTimer + editSec.getText().toString();
						}
						
						textTimer.setText(stringTimer);
						int totalSec = intHour * 3600 + intMin * 60  + intSec;
						TimerService.setRemainSeconds(totalSec);
					}
				})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
    	settingDialog.show();
	}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    
	    if(TimerService.getTimerState().equals(TimerService.State.Running)){
	    	buttonToggle.setText("停止");
	    	textTimer.setText(TimerService.getStringRemainSeconds());
	    	
	    	mUIHandler = new UIHandler(MainActivity.this);
	    	TimerService.registerHandler(mUIHandler);
	    	TimerService.resetServiceThreadHandler();
	    	
	    }else{
	    	mUIHandler = new UIHandler(MainActivity.this);
			TimerService.registerHandler(mUIHandler);
			textTimer.setText(TimerService.getStringRemainSeconds());
	    }
	    
	}
	
}
