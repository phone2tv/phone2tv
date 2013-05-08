package com.example.phone2tv;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

public class Phone2TvComm 
{
	public  static String mFormat[]= 
	{
		"yyyy-MM-dd'T'HH:mm:ss" ,
		"MM-dd HH:mm" ,
		"HH:mm"
	};
	public static LoginInfo getLoginInfoFromIntent(Intent intent)
	{
		Bundle bundle = intent.getExtras();
		LoginInfo loginInfo = bundle.getParcelable("login");
		return loginInfo;
	}
	
	public static Bundle putLoginInfo(Bundle bundle , LoginInfo login)
	{
		bundle.putParcelable("login", login);
		return bundle;
	}
	
	public static Bundle putTvStation(Bundle bundle , TvStationProgram station)
	{
		bundle.putParcelable("station" , station);
		return bundle;
	}
	
	public static TvStationProgram getTvStationFromIntent(Intent intent)
	{
		Bundle bundle = intent.getExtras();
		TvStationProgram station = bundle.getParcelable("station");
		return station;
	}
	
	public static Bundle putProgram(Bundle bundle , Program program)
	{
		bundle.putParcelable("program", program);
		return bundle;
	}
	
	public static Program getProgramFromIntent(Intent intent)
	{
		Bundle bundle = intent.getExtras();
		Program program = bundle.getParcelable("program");
		return program;
	}
	
	//Default ISO8601 format
	public static Date parseStringToLocale(String strDate , String strFormat)
	{
		return parseString(strDate , strFormat , true);
	}
		
	public static Date parseString(String strDate , String strFormat)
	{
		return parseString(strDate , strFormat , false);
	}
		
	public static Date parseString(String strDate , String format , boolean isUTC)
	{
		Date date = null;
		SimpleDateFormat simpleDate = new SimpleDateFormat(format , Locale.CHINA);
		try
		{
			date= simpleDate.parse(strDate);
			Date temp = date;
			if(isUTC)
			{
				int minuOffset = date.getTimezoneOffset();
				Calendar cal = simpleDate.getCalendar();
				cal.add(Calendar.MINUTE,-minuOffset);
				temp = cal.getTime();
			}
			return temp;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return date;
	}
	
	 public static  String DateToString(Date date , String strFormat)
	 {
		 SimpleDateFormat simple =  new SimpleDateFormat(strFormat , Locale.CHINA);
		 return simple.format(date);
	 }
}
