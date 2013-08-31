package com.example.learnservice;

import android.os.Handler;
import android.os.Message;

public class UIHandler extends Handler {

	 private MainActivity mActivity;
	 public static final String MSG = "msg";
	 public String finalReaminTime = "00:00:00" ;
	 
	 public UIHandler(MainActivity activity) {
	  super();
	  	this.mActivity = activity;
	 }

	@Override
	 public void handleMessage(Message msg) {
		 String text = msg.getData().getString(MSG);
		 this.mActivity.setRemainTimeText(text);
		 
		 if (text.equals(finalReaminTime)){
			 this.mActivity.changeButtonState();
			 this.mActivity.makeFinishToast();
		 }
	 }
}
