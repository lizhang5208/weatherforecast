package com.example.weatherforecast;

public interface HttpCallbackListener {
	
	public void onFinish(String response);
	
	public void onError(Exception e);

}
