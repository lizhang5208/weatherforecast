package com.example.weatherforecast;

public class WeatherItem {
	
	private String weatherDetail;
	private String item; 

	public WeatherItem(String weatherDetail, String item){
		this.weatherDetail=weatherDetail;
		this.item=item;
	}
	
	public String getWeatherDetail(){
		return weatherDetail;
	}
	
	public String getItem(){
		return item;
	}
}
