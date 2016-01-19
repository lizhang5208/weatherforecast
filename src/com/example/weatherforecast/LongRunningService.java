package com.example.weatherforecast;

import android.annotation.SuppressLint;

//import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.widget.Toast;

public class LongRunningService extends Service {
	
	private String cityName;
	private MyBinder mBinder = new MyBinder(); 	
	private int aMinute=DEFAULT_REFRESH_RATE;
	public static final int DEFAULT_REFRESH_RATE=5000;
	private boolean onToastDebug;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
		//return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		/*new Thread(new Runnable(){
			@Override
			public void run(){

			}
		}).start();*/
		final Message msg=new Message();
		if(cityName!=null){
			try {
				String address=HttpUtil.HTTP_URL+"?"+"city="+cityName;
				HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
					@Override
					public void onFinish(String response) {
						// TODO Auto-generated method stub
						msg.what=HttpUtil.SHOW_RESPONSE;
						msg.obj=response;
						handler.sendMessage(msg);
					}					
					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
						msg.what=HttpUtil.SHOW_ERROR;
						msg.obj=e.toString();
						handler.sendMessage(msg);
					}
				});
			} catch (Exception e) {
				// TODO: handle exception
				//e.printStackTrace();
				Toast.makeText(this, "service auto request weather info failed...", Toast.LENGTH_SHORT).show();
			}
		}
		
		//Toast.makeText(this, new Date(System.currentTimeMillis()).toString(), Toast.LENGTH_SHORT).show();
		if(onToastDebug){
			Toast.makeText(this, cityName, Toast.LENGTH_SHORT).show();	
		}		
		AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);		
		long triggerAtTime=SystemClock.elapsedRealtime() + aMinute;
		Intent i=new Intent(this, AlarmReceiver.class);
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg){
			String weatherResultFromService=(String)msg.obj;
			int showResult=(int)msg.what;
			Intent intent=new Intent();
			intent.putExtra("weatherResultFromService", weatherResultFromService);
			intent.putExtra("showResult", showResult);
			intent.setAction("com.example.weatherforecast.LongRunningService");
			sendBroadcast(intent);			
		}
	};
	
	class MyBinder extends Binder {
		
		public void setCityName(String cityName){
			LongRunningService.this.cityName=cityName;
		}
		
		public void setRefreshRatePerThisSecond(int aMinute){
			if(aMinute<=0){
				LongRunningService.this.aMinute=DEFAULT_REFRESH_RATE;
			}else{
				LongRunningService.this.aMinute=aMinute;
			}
		}
		
		public void setToastDebug(boolean onToastDebug){
			LongRunningService.this.onToastDebug=onToastDebug;
		}
  
    }
	
}
