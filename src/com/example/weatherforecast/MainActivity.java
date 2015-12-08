package com.example.weatherforecast;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	private EditText city;
	private Button search;
	private TextView weatherInfo;
	private String httpUrl = "http://apis.baidu.com/heweather/weather/free";
	private static final int SHOW_RESPONSE=1;
	private static final int SHOW_ERROR=2;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		city=(EditText)findViewById(R.id.city);
		search=(Button)findViewById(R.id.search);
		weatherInfo=(TextView)findViewById(R.id.weather_info);
		search.setOnClickListener(this);
	}
		
	@Override
	public void onClick(View view){
		switch(view.getId()){
		case R.id.search:
			String cityName=city.getText().toString();
			final Message msg=new Message();
			if(cityName!=null){
				String address=httpUrl+"?"+"city="+cityName;
				showProgressDialog();
				HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
					
					@Override
					public void onFinish(String response) {
						// TODO Auto-generated method stub
						//weatherInfo.setText(response);
						//Message msg=new Message();
						msg.what=SHOW_RESPONSE;
						msg.obj=response;
						handler.sendMessage(msg);
					}
					
					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
						//weatherInfo.setText("search weather info failed...");
						//Toast.makeText(this, "search weather info failed...", Toast.LENGTH_LONG).show();
						//Message msg=new Message();
						msg.what=SHOW_ERROR;
						handler.sendMessage(msg);
					}
				});
			}
			break;
		default:
			break;
		}	
	}
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case SHOW_RESPONSE:
				closeProgressDialog();
				//weatherInfo.setText((String)msg.obj);
				parseJSONWithJSONObject((String)msg.obj);
				break;
			case SHOW_ERROR:
				closeProgressDialog();
				weatherInfo.setText("search weather info failed...");
				break;
			default:
				break;
			}			
		}
	};
	
	/**
	*显示进度对话框
	*/
	private void showProgressDialog(){
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	/**
	*关闭进度对话框
	*/
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	private void parseJSONWithJSONObject(String jsonData){
		try{
			StringBuilder builder=new StringBuilder();
			JSONObject jsonObject=new JSONObject(jsonData);
			JSONArray jsonArray=jsonObject.getJSONArray("HeWeather data service 3.0");
			//
			JSONObject jsonObject1=jsonArray.getJSONObject(0);
			builder.append("status: "+jsonObject1.getString("status")+"//接口状态"+"\n");
			builder.append("basic: "+"//基本信息"+"\n");	
				JSONObject jsonObject11=jsonObject1.getJSONObject("basic");
				builder.append("city: "+jsonObject11.getString("city")+"//城市名称"+"\n");
				builder.append("cnty: "+jsonObject11.getString("cnty")+"//国家"+"\n");
				builder.append("id: "+jsonObject11.getString("id")+"//城市ID"+"\n");
				builder.append("lat: "+jsonObject11.getString("lat")+"//城市维度"+"\n");
				builder.append("lon: "+jsonObject11.getString("lon")+"//城市经度"+"\n");
				builder.append("update: "+"//更新时间"+"\n");
					JSONObject jsonObject111=jsonObject11.getJSONObject("update");
					builder.append("loc: "+jsonObject111.getString("loc")+"//当地时间"+"\n");
					builder.append("utc: "+jsonObject111.getString("utc")+"//UTC时间"+"\n");
			//weatherInfo.setText(builder);
			builder.append("now: "+"//实况天气"+"\n");
				JSONObject jsonObject21=jsonObject1.getJSONObject("now");
				builder.append("cond: "+"//天气状况"+"\n");
					JSONObject jsonObject211=jsonObject21.getJSONObject("cond");
					builder.append("code: "+jsonObject211.getString("code")+"//天气状况代码"+"\n");
					builder.append("txt: "+jsonObject211.getString("txt")+"//天气状况描述"+"\n");
				builder.append("fl: "+jsonObject21.getString("fl")+"//体感温度"+"\n");
				builder.append("hum: "+jsonObject21.getString("hum")+"//相对湿度（%）"+"\n");
				builder.append("pcpn: "+jsonObject21.getString("pcpn")+"//降水量（mm）"+"\n");
				builder.append("pres: "+jsonObject21.getString("pres")+"//气压"+"\n");
				builder.append("tmp: "+jsonObject21.getString("tmp")+"//温度"+"\n");
				builder.append("vis: "+jsonObject21.getString("vis")+"//能见度（km）"+"\n");
				builder.append("wind: "+"//风力风向"+"\n");
					JSONObject jsonObject212=jsonObject21.getJSONObject("wind");
					builder.append("deg: "+jsonObject212.getString("deg")+"//风向（360度）"+"\n");
					builder.append("dir: "+jsonObject212.getString("dir")+"//风向"+"\n");
					builder.append("sc: "+jsonObject212.getString("sc")+"//风力"+"\n");
					builder.append("spd: "+jsonObject212.getString("spd")+"//风速（kmph）"+"\n");
			//weatherInfo.setText(builder);
			builder.append("aqi: "+"//空气质量"+"\n");
				JSONObject jsonObject31=jsonObject1.getJSONObject("aqi");
				builder.append("city: "+"//当前时间"+"\n");
					JSONObject jsonObject311=jsonObject31.getJSONObject("city");
					builder.append("aqi: "+jsonObject311.getString("aqi")+"//空气质量指数"+"\n");
					builder.append("co: "+jsonObject311.getString("co")+"//一氧化碳1小时平均值(ug/m³)"+"\n");
					builder.append("no2: "+jsonObject311.getString("no2")+"//二氧化氮1小时平均值(ug/m³)"+"\n");
					builder.append("o3: "+jsonObject311.getString("o3")+"//臭氧1小时平均值(ug/m³)"+"\n");
					builder.append("pm10: "+jsonObject311.getString("pm10")+"//PM10 1小时平均值(ug/m³)"+"\n");
					builder.append("pm25: "+jsonObject311.getString("pm25")+"//PM2.5 1小时平均值(ug/m³)"+"\n");
					builder.append("qlty: "+jsonObject311.getString("qlty")+"//空气质量类别"+"\n");
					builder.append("so2: "+jsonObject311.getString("so2")+"//二氧化硫1小时平均值(ug/m³)"+"\n");
			//weatherInfo.setText(builder);
			builder.append("daily_forecast: "+"//7天天气预报"+"\n");
			JSONArray jsonArray4=jsonObject1.getJSONArray("daily_forecast");
			for(int i=0;i<jsonArray4.length();i++){
				JSONObject jsonObject4in=jsonArray4.getJSONObject(i);
				builder.append("date: "+jsonObject4in.getString("date")+"//预报日期"+"\n");
					builder.append("astro: "+"//天文数值"+"\n");
					JSONObject jsonObject4in1=jsonObject4in.getJSONObject("astro");
					builder.append("sr: "+jsonObject4in1.getString("sr")+"//日出时间"+"\n");
					builder.append("ss: "+jsonObject4in1.getString("ss")+"//日落时间"+"\n");
					builder.append("cond: "+"//天气状况"+"\n");
					JSONObject jsonObject4in2=jsonObject4in.getJSONObject("cond");
					builder.append("code_d: "+jsonObject4in2.getString("code_d")+"//白天天气状况代码"+"\n");
					builder.append("code_n: "+jsonObject4in2.getString("code_n")+"//夜间天气状况代码"+"\n");
					builder.append("txt_d: "+jsonObject4in2.getString("txt_d")+"//白天天气状况描述"+"\n");
					builder.append("txt_n: "+jsonObject4in2.getString("txt_n")+"//夜间天气状况描述"+"\n");
				builder.append("hum: "+jsonObject4in.getString("hum")+"//相对湿度（%）"+"\n");
				builder.append("pcpn: "+jsonObject4in.getString("pcpn")+"//降水量（mm）"+"\n");
				builder.append("pop: "+jsonObject4in.getString("pop")+"//降水概率"+"\n");
				builder.append("pres: "+jsonObject4in.getString("pres")+"//气压"+"\n");
					builder.append("tmp: "+"//温度"+"\n");
					JSONObject jsonObject4in3=jsonObject4in.getJSONObject("tmp");
					builder.append("max: "+jsonObject4in3.getString("max")+"//最高温度"+"\n");
					builder.append("min: "+jsonObject4in3.getString("min")+"//最低温度"+"\n");
				builder.append("vis: "+jsonObject4in.getString("vis")+"//能见度（km）"+"\n");
					builder.append("wind: "+"//风力风向"+"\n");
					JSONObject jsonObject4in4=jsonObject4in.getJSONObject("wind");
					builder.append("deg: "+jsonObject4in4.getString("deg")+"//风向（360度）"+"\n");
					builder.append("dir: "+jsonObject4in4.getString("dir")+"//风向"+"\n");
					builder.append("sc: "+jsonObject4in4.getString("sc")+"//风力"+"\n");
					builder.append("spd: "+jsonObject4in4.getString("spd")+"//风速（kmph）"+"\n");
			}
			//weatherInfo.setText(builder);
			builder.append("hourly_forecast: "+"//每三小时天气预报"+"\n");
			JSONArray jsonArray5=jsonObject1.getJSONArray("hourly_forecast");
			for(int i=0;i<jsonArray5.length();i++){
				JSONObject jsonObject5in=jsonArray5.getJSONObject(i);
				builder.append("date: "+jsonObject5in.getString("date")+"//时间"+"\n");
				builder.append("hum: "+jsonObject5in.getString("hum")+"//相对湿度（%）"+"\n");
				builder.append("pop: "+jsonObject5in.getString("pop")+"//降水概率"+"\n");
				builder.append("pres: "+jsonObject5in.getString("pres")+"//气压"+"\n");
				builder.append("tmp: "+jsonObject5in.getString("tmp")+"//温度"+"\n");
				builder.append("wind: "+"//风力风向"+"\n");
					JSONObject jsonObject5in1=jsonObject5in.getJSONObject("wind");
					builder.append("deg: "+jsonObject5in1.getString("deg")+"//风向（360度）"+"\n");
					builder.append("dir: "+jsonObject5in1.getString("dir")+"//风向"+"\n");
					builder.append("sc: "+jsonObject5in1.getString("sc")+"//风力"+"\n");
					builder.append("spd: "+jsonObject5in1.getString("spd")+"//风速（kmph）"+"\n");	
			}
			//weatherInfo.setText(builder);	
			builder.append("suggestion: "+"//生活指数"+"\n");
			JSONObject jsonObject61=jsonObject1.getJSONObject("suggestion");
			builder.append("comf: "+"//舒适度指数"+"\n");
				JSONObject jsonObject611=jsonObject61.getJSONObject("comf");
				builder.append("brf: "+jsonObject611.getString("brf")+"//简介"+"\n");
				builder.append("txt: "+jsonObject611.getString("txt")+"//详细描述"+"\n");
			builder.append("cw: "+"//洗车指数"+"\n");
				JSONObject jsonObject612=jsonObject61.getJSONObject("cw");
				builder.append("brf: "+jsonObject612.getString("brf")+"//简介"+"\n");
				builder.append("txt: "+jsonObject612.getString("txt")+"//详细描述"+"\n");
			builder.append("drsg: "+"//穿衣指数"+"\n");
				JSONObject jsonObject613=jsonObject61.getJSONObject("drsg");
				builder.append("brf: "+jsonObject613.getString("brf")+"//简介"+"\n");
				builder.append("txt: "+jsonObject613.getString("txt")+"//详细描述"+"\n");
			builder.append("flu: "+"//感冒指数"+"\n");
				JSONObject jsonObject614=jsonObject61.getJSONObject("flu");
				builder.append("brf: "+jsonObject614.getString("brf")+"//简介"+"\n");
				builder.append("txt: "+jsonObject614.getString("txt")+"//详细描述"+"\n");
			builder.append("sport: "+"//运动指数"+"\n");
				JSONObject jsonObject615=jsonObject61.getJSONObject("sport");
				builder.append("brf: "+jsonObject615.getString("brf")+"//简介"+"\n");
				builder.append("txt: "+jsonObject615.getString("txt")+"//详细描述"+"\n");
			builder.append("trav: "+"//旅游指数"+"\n");
				JSONObject jsonObject616=jsonObject61.getJSONObject("trav");
				builder.append("brf: "+jsonObject616.getString("brf")+"//简介"+"\n");
				builder.append("txt: "+jsonObject616.getString("txt")+"//详细描述"+"\n");
			builder.append("uv: "+"//紫外线指数"+"\n");
				JSONObject jsonObject617=jsonObject61.getJSONObject("uv");
				builder.append("brf: "+jsonObject617.getString("brf")+"//简介"+"\n");
				builder.append("txt: "+jsonObject617.getString("txt")+"//详细描述"+"\n");
		weatherInfo.setText(builder);
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(this, "parse Json data failed...", Toast.LENGTH_LONG).show();
		}
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}