package com.example.learnservice;

import java.util.Calendar;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class TimerService extends Service {
    
	private static UIHandler handler;
	public static final String ACTION_PLAY = "com.example.learnservice.action.PLAY";
	public static final String ACTION_STOP = "com.example.learnservice.action.STOP";
	public static final String TAG_TOTAL_SECONT = "TotoalSecond";

    enum State{
    	Stopped,
    	Running
    };
    
    static State mState = State.Stopped;

    private static String stringRemainTimer = "00:00:00";
    private static int remainSeconds;
    private int finalSeconds;
    private Boolean isRun = true;
    
    private static ServiceThread serviceThread; 
    
//    NotificationManager mNotificationManager;
    Notification mNotification = null;
    final int NOTIFICATION_ID = 1;
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

    	String action = intent.getAction();
    	if (action.equals(ACTION_PLAY)){
    		remainSeconds = intent.getIntExtra(TimerService.TAG_TOTAL_SECONT, 0);
    		isRun = true;
    		startTimer();
    	}else if(action.equals(ACTION_STOP)){
    		stopTimer();
    	}
        return START_NOT_STICKY; 
                                
    }

    private void stopTimer() {
		// stop thread
    	mState = State.Stopped;
    	isRun = false;
    	stopForeground(true);
	}

	private void startTimer() {
		Calendar c = Calendar.getInstance(); 
		int currentSeconds = (int) (c.getTimeInMillis()/1000);	
		finalSeconds = currentSeconds + remainSeconds;
		
		mState = State.Running;
		serviceThread = new ServiceThread(handler);
//		new Thread(new ServiceThread(handler)).start();
		new Thread(serviceThread).start();
		
		setUpAsForeground("計時中...");
	}

	
    public static State getTimerState(){
		return mState;   	
    }
    
    public static void registerHandler(Handler uiHandler) {
        handler = (UIHandler) uiHandler;
    }
    
    public static void setRemainSeconds(int seconds) {
    	remainSeconds = seconds;
    }
    
	public static int getRemainSeconds(){
		return remainSeconds;
	}
	
	public static String getStringRemainSeconds(){
		return stringRemainTimer;
	}
    
	public static UIHandler getUIHandler(){
		return handler;
	}
	
	public static void resetServiceThreadHandler(){
		serviceThread.threadHandler = handler;
	}
	
	public class ServiceThread implements Runnable {
		
		private UIHandler threadHandler;
		
		 public ServiceThread( UIHandler handler) {
		  super();
		  this.threadHandler = handler;
		 }

		 @Override
		 public void run() {
			 
			 while (isRun){
				 try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				 Calendar c = Calendar.getInstance(); 
				 int currentSeconds = (int) (c.getTimeInMillis()/1000);
				 
				 if (currentSeconds > finalSeconds){
					 stopTimer();
				 }else {
					 remainSeconds = finalSeconds - currentSeconds;
					 int hours = remainSeconds/3600;
					 int mins = (remainSeconds%3600)/60;
					 int secs = remainSeconds%60;
					 
					 if( hours == 0){
						 stringRemainTimer = "00:";
					 }else if ( 0 < hours && hours <10){
						 stringRemainTimer = "0" + Integer.toString(hours) + ":";
					 }else {
						 stringRemainTimer = Integer.toString(hours) + ":";
					 }
					 
					 if( mins == 0){
						 stringRemainTimer = stringRemainTimer +"00:";
					 }else if ( 0 < mins && mins <10){
						 stringRemainTimer = stringRemainTimer + "0" + Integer.toString(mins) + ":";
					 }else {
						 stringRemainTimer = stringRemainTimer + Integer.toString(mins) + ":";
					 }
					 
					 if( secs == 0){
						 stringRemainTimer = stringRemainTimer + "00";
					 }else if ( 0 < secs && secs <10){
						 stringRemainTimer = stringRemainTimer + "0" + Integer.toString(secs);
					 }else {
						 stringRemainTimer = stringRemainTimer + Integer.toString(secs);
					 }
									 
					 Message msg = this.threadHandler.obtainMessage();
					 msg.getData().putString(UIHandler.MSG, stringRemainTimer);
					 threadHandler.sendMessage(msg);
					 Log.v("TEST", "still runing");
				 }
				
				
			 }
		 }
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
    }
	
	@SuppressWarnings("deprecation")
	void setUpAsForeground(String text) {
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification = new Notification();
        mNotification.tickerText = text;
        mNotification.icon = R.drawable.ic_launcher;
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        mNotification.setLatestEventInfo(getApplicationContext(), "Timer",
                text, pi);
        startForeground(NOTIFICATION_ID, mNotification);
    }
	
}