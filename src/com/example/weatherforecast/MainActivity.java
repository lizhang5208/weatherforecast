package com.example.weatherforecast;

//import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
//import android.view.LayoutInflater.Filter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
//import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnCheckedChangeListener{

	private EditText city;
	private Button search;
	private EditText refreshRate;
	private CheckBox onToastDebug;
	//private TextView weatherInfo;
	private ListView weatherInfo;
	private ProgressDialog progressDialog;
	
	private List<WeatherItem> weatherList=new ArrayList<WeatherItem>();
	private WeatherAdapter weatherAdapter;
	
	private MyReceiver receiver;
	private IntentFilter filter;
	
	private LongRunningService.MyBinder myBinder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		city=(EditText)findViewById(R.id.city);
		city.addTextChangedListener(textWatcher);
		search=(Button)findViewById(R.id.search);
		refreshRate=(EditText)findViewById(R.id.refresh_rate);
		onToastDebug=(CheckBox)findViewById(R.id.on_toast_debug);
		//weatherInfo=(TextView)findViewById(R.id.weather_info);
		weatherInfo=(ListView)findViewById(R.id.weather_info);
		weatherAdapter=new WeatherAdapter(MainActivity.this, R.layout.weather_item, weatherList);
		weatherInfo.setAdapter(weatherAdapter);
		search.setOnClickListener(this);
		onToastDebug.setOnCheckedChangeListener(this);
		receiver=new MyReceiver();
		filter=new IntentFilter();
		filter.addAction("com.example.weatherforecast.LongRunningService");
		registerReceiver(receiver,filter);		
		Intent i=new Intent(this, LongRunningService.class);
		bindService(i, connection, BIND_AUTO_CREATE);
		startService(i);
	}
	
	private TextWatcher textWatcher=new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			myBinder.setCityName(city.getText().toString());
		}
	};
	
	private ServiceConnection connection = new ServiceConnection() {  
		  
        @Override  
        public void onServiceDisconnected(ComponentName name) {  
        }  
  
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) {  
            myBinder = (LongRunningService.MyBinder) service;   
        }  
    };
	
	class MyReceiver extends BroadcastReceiver {
	     @Override
	     public void onReceive(Context context, Intent intent) {
	    	 int showResult=intent.getIntExtra("showResult", 0);
	    	 switch(showResult){
	    	 case HttpUtil.SHOW_RESPONSE:
	    		 String weatherResultFromService=intent.getStringExtra("weatherResultFromService");
				 parseJSONWithGSON(weatherResultFromService);
	    		 break;
	    	 case HttpUtil.SHOW_ERROR:
	    		 weatherList.clear();
	    		 weatherList.add(new WeatherItem("自动从和风公共API接口获取"+city.getText()+"天气信息","失败"));
	    		 weatherAdapter.notifyDataSetChanged();
	    		 break;
	    	 default:
	    		 break;
	    	 }    	 
	     }
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		myBinder.setToastDebug(isChecked);
	}
		
	@Override
	public void onClick(View view){
		switch(view.getId()){
		case R.id.search:
			try{
				int i=Integer.parseInt(refreshRate.getText().toString());
				myBinder.setRefreshRatePerThisSecond(i*1000);
			}catch(NumberFormatException e){
				myBinder.setRefreshRatePerThisSecond(LongRunningService.DEFAULT_REFRESH_RATE);
			}
			String cityName=city.getText().toString();
			final Message msg=new Message();
			if(cityName!=null){
				try {
					//String address=new String((httpUrl+"?"+"city="+"上海").getBytes(),"UTF-8");
					//String address=httpUrl+"?"+"city="+new String("上海".getBytes(),"UTF-8");
					String address=HttpUtil.HTTP_URL+"?"+"city="+cityName;
					showProgressDialog();
					HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
						@Override
						public void onFinish(String response) {
							// TODO Auto-generated method stub
							//weatherInfo.setText(response);
							msg.what=HttpUtil.SHOW_RESPONSE;
							msg.obj=response;
							handler.sendMessage(msg);
						}					
						@Override
						public void onError(Exception e) {
							// TODO Auto-generated method stub
							msg.what=HttpUtil.SHOW_ERROR;
							handler.sendMessage(msg);
						}
					});
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					Toast.makeText(this, "manually search weather info failed...", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		default:
			break;
		}	
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg){
			closeProgressDialog();
			switch(msg.what){
			case HttpUtil.SHOW_RESPONSE:
				//weatherInfo.setText((String)msg.obj);
				//parseJSONWithJSONObject((String)msg.obj);
				parseJSONWithGSON((String)msg.obj);
				break;
			case HttpUtil.SHOW_ERROR:
				//weatherInfo.setText("search weather info failed...");
				weatherList.clear();
				weatherList.add(new WeatherItem("手动从和风公共API接口获取"+city.getText()+"天气信息","失败"));
				weatherAdapter.notifyDataSetChanged();
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
	
	@SuppressLint("SimpleDateFormat")
	private void parseJSONWithGSON(String jsonData) {
		weatherList.clear();
		try {
			JSONObject jsonObject=new JSONObject(jsonData);
			JSONArray jsonArray=jsonObject.getJSONArray("HeWeather data service 3.0");
			Log.d("MainActivity", jsonArray.toString());
			Gson gson=new Gson();
			//Type type = new TypeToken<List<DataService>>(){}.getType();
			//List<DataService> dsList=gson.fromJson(jsonData, type);
			String temp=jsonArray.toString();
			List<DataService> ds = gson.fromJson(temp, new TypeToken<List<DataService>>(){}.getType());
			StringBuilder builder=new StringBuilder();
			SimpleDateFormat formatter=new SimpleDateFormat("yyyy年MM月dd日, HH:mm:ss");
			Date currentDate=new Date(System.currentTimeMillis());
			String str=formatter.format(currentDate);
			for(DataService dss:ds){
				weatherList.add(new WeatherItem("当前时间","time: "+str));
				builder.append("status: "+dss.getStatus()+"//接口状态"+"\n");
				weatherList.add(new WeatherItem("接口状态","status: "+dss.getStatus()));
				builder.append("city: "+dss.getBasic().city+"//城市名称"+"\n");
				weatherList.add(new WeatherItem("城市名称","city: "+dss.getBasic().city));
				builder.append("cnty: "+dss.getBasic().cnty+"//国家"+"\n");
				weatherList.add(new WeatherItem("国家","cnty: "+dss.getBasic().cnty));
				builder.append("lat: "+dss.getBasic().lat+"//城市维度"+"\n");
				weatherList.add(new WeatherItem("城市维度","lat: "+dss.getBasic().lat));
				builder.append("lon: "+dss.getBasic().lon+"//城市经度"+"\n");
				weatherList.add(new WeatherItem("城市经度","lon: "+dss.getBasic().lon));
				builder.append("loc: "+dss.getBasic().getUpdate().getLoc()+"//更新时间"+"\n");
				weatherList.add(new WeatherItem("更新时间","loc: "+dss.getBasic().getUpdate().getLoc()));
				builder.append("txt: "+dss.getNow().getCond().getTxt()+"//天气状况描述"+"\n");
				weatherList.add(new WeatherItem("天气状况描述","txt: "+dss.getNow().getCond().getTxt()));
				builder.append("hum: "+dss.getNow().getHum()+"//相对湿度（%）"+"\n");
				weatherList.add(new WeatherItem("相对湿度（%）","hum: "+dss.getNow().getHum()));
				builder.append("pcpn: "+dss.getNow().getPcpn()+"//降水量（mm）"+"\n");
				weatherList.add(new WeatherItem("降水量（mm）","pcpn: "+dss.getNow().getPcpn()));
				builder.append("tmp: "+dss.getNow().getTmp()+"//温度"+"\n");
				weatherList.add(new WeatherItem("温度","tmp: "+dss.getNow().getTmp()));
				builder.append("vis: "+dss.getNow().getVis()+"//能见度（km）"+"\n");
				weatherList.add(new WeatherItem("能见度（km）","vis: "+dss.getNow().getVis()));
				builder.append("dir: "+dss.getNow().getWind().getDir()+"//风向"+"\n");
				weatherList.add(new WeatherItem("风向","dir: "+dss.getNow().getWind().getDir()));
				builder.append("sc: "+dss.getNow().getWind().getSc()+"//风力"+"\n");
				weatherList.add(new WeatherItem("风力","status: "+"sc: "+dss.getNow().getWind().getSc()));
				builder.append("spd: "+dss.getNow().getWind().getSpd()+"//风速（kmph）"+"\n");
				weatherList.add(new WeatherItem("风速（kmph）","spd: "+dss.getNow().getWind().getSpd()));
				builder.append("aqi: "+dss.getAqi().getCity().getAqi()+"//空气质量指数"+"\n");
				weatherList.add(new WeatherItem("空气质量指数","aqi: "+dss.getAqi().getCity().getAqi()));
				builder.append("pm25: "+dss.getAqi().getCity().getPm25()+"//PM2.5 1小时平均值(ug/m³)"+"\n");
				weatherList.add(new WeatherItem("PM2.5 1小时平均值(ug/m³)","pm25: "+dss.getAqi().getCity().getPm25()));
				builder.append("qlty: "+dss.getAqi().getCity().getQlty()+"//空气质量类别"+"\n");
				weatherList.add(new WeatherItem("空气质量类别","qlty: "+dss.getAqi().getCity().getQlty()));
				builder.append("brf: "+dss.getSuggestion().getUv().getBrf()+"//简介"+"\n");
				weatherList.add(new WeatherItem("紫外线指数简介","brf: "+dss.getSuggestion().getUv().getBrf()));
				builder.append("txt: "+dss.getSuggestion().getUv().getTxt()+"//详细描述"+"\n");
				weatherList.add(new WeatherItem("详细描述","txt: "+dss.getSuggestion().getUv().getTxt()));
			}
			weatherAdapter.notifyDataSetChanged();
			//weatherInfo.setText(builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	@SuppressWarnings("unused")
	private void parseJSONWithJSONObject(String jsonData){
		try{
			StringBuilder builder=new StringBuilder();
			JSONObject jsonObject=new JSONObject(jsonData);
			JSONArray jsonArray=jsonObject.getJSONArray("HeWeather data service 3.0");
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
		//weatherInfo.setText(builder);
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(this, "parse Json data failed...", Toast.LENGTH_LONG).show();
		}
	}	
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		unregisterReceiver(receiver);
		stopService(new Intent(this, LongRunningService.class));
		unbindService(connection);
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
