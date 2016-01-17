package com.example.weatherforecast;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WeatherAdapter extends ArrayAdapter<WeatherItem> {
	
	private int resourceId;
	
	public WeatherAdapter(Context context, int textViewResourceId, List<WeatherItem> WeatherList){
		super(context, textViewResourceId, WeatherList);
		resourceId=textViewResourceId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		WeatherItem weatherItem=getItem(position);
		View view;
		ViewHolder viewHolder;
		if(convertView==null){
			view=LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder=new ViewHolder();
			viewHolder.weatherDetail=(TextView)view.findViewById(R.id.weather_detail);
			viewHolder.item=(TextView)view.findViewById(R.id.item);
			view.setTag(viewHolder);//��ViewHolder�洢��View��
		}
		else{
			view=convertView;
			viewHolder=(ViewHolder)view.getTag();//���»�ȡViewHolder
		}
		viewHolder.weatherDetail.setText(weatherItem.getWeatherDetail());
		viewHolder.item.setText(weatherItem.getItem());
		return view;
	}

}

class ViewHolder {
	TextView weatherDetail;
	TextView item;
}
