package com.example.weatherforecast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//import org.apache.http.HttpConnection;

//import android.net.Uri;

public class HttpUtil {
	
	private static String apiKey="8126487f68f7f59c67923e9306400cff";
	
	public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
		new Thread(new Runnable(){
			@Override
			public void run(){
				HttpURLConnection connection=null;
				try {
					URL url=new URL(address);
					connection=(HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					connection.setRequestProperty("apikey", apiKey);
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					connection.connect();
					InputStream in=connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(in, "UTF-8"));
					StringBuilder response=new StringBuilder();
					String Line;
					while((Line=reader.readLine())!=null){
						response.append(Line);
						//response.append("\\r\\n");
					}
					reader.close();
					if(listener!=null){
						listener.onFinish(response.toString());
					}
					
				} catch (Exception e) {
					// TODO: handle exception
					if(listener!=null){
						listener.onError(e);
					}
				} finally{
					if(connection!=null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}

}
