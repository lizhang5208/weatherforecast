package com.example.weatherforecast;

import java.util.List;

public class DataService {
	List<Daily> daily_forecast;
	//DailyForecast daily_forecast;
	Aqi aqi;
	Suggestion suggestion;
	String status;
	Now now;
	Basic basic;
	List<Hourly> hourly_forecast;
	//HourlyForecast hourly_forecast;	
	public String getStatus() {
		return status;
	}
	public Aqi getAqi() {
		return aqi;
	}
	public Suggestion getSuggestion() {
		return suggestion;
	}
	public List<Daily> getDaily() {
		return daily_forecast;
	}
	public Now getNow() {
		return now;
	}
	public Basic getBasic() {
		return basic;
	}
	public List<Hourly> getHourly() {
		return hourly_forecast;
	}
}

class Basic {
	String city;
	String cnty;
	String id;
	String lat;
	String lon;
	Update update;
	public String getCity() {
		return city;
	}
	public String getCnty() {
		return cnty;
	}
	public String getLat() {
		return lat;
	}
	public String getLon() {
		return lon;
	}
	public Update getUpdate() {
		return update;
	}
}
class Update {
	String loc;
	String utc;
	public String getLoc() {
		return loc;
	}
}

class Now {
	Cond cond;
	String fl;
	String hum;
	String pcpn;
	String pres;
	String tmp;
	String vis;
	Wind wind;
	public Cond getCond() {
		return cond;
	}
	public Wind getWind() {
		return wind;
	}
	public String getHum() {
		return hum;
	}
	public String getPcpn() {
		return pcpn;
	}
	public String getTmp() {
		return tmp;
	}
	public String getVis() {
		return vis;
	}	
}
class Cond {
	String code;
	String txt;
	public String getTxt() {
		return txt;
	}
}
class Wind {
	String deg;
	String dir;
	String sc;
	String spd;
	public String getDir() {
		return dir;
	}
	public String getSc() {
		return sc;
	}
	public String getSpd() {
		return spd;
	}
}

class Aqi {
	City city;
	public City getCity() {
		return city;
	}
}
class City {
	String aqi;
	String co;
	String no2;
	String o3;
	String pm10;
	String pm25;
	String qlty;
	String so2;
	public String getAqi() {
		return aqi;
	}
	public String getPm25() {
		return pm25;
	}
	public String getQlty() {
		return qlty;
	}
}

//class DailyForecast {
	//Daily[] daily;
	//List<Daily> daily;
	class Daily {
		String date;
		Astro astro;
		class Astro {
			String sr;
			String ss;
		}
		CondDayNight cond;
		class CondDayNight {
			String code_d;
			String code_n;
			String txt_d;
			String txt_n;
		}
		String hum;
		String pcpn;
		String pop;
		String pres;
		Tmp tmp;
		class Tmp {
			String max;
			String min;
		}
		String vis;
		Wind wind;
	}
//}

//class HourlyForecast {
	//Hourly[] hourly;
	//List<Hourly> hourly;
	class Hourly {
		String date;
		String hum;
		String pop;
		String pres;
		String tmp;
		Wind wind;
	}
//}

class Suggestion {
	BrfTxt comf;
	BrfTxt cw;
	BrfTxt drsg;
	BrfTxt flu;
	BrfTxt sport;
	BrfTxt trav;
	BrfTxt uv;
	public BrfTxt getUv() {
		return uv;
	}
}
class BrfTxt {
	String brf;
	String txt;
	public String getBrf() {
		return brf;
	}
	public String getTxt() {
		return txt;
	}
}