package com.example.phone2tv;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;

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
	 
	 public static Bitmap base64ToBitmap(String str)
	 {
		 Bitmap bitmap=null;
		 try 
		 {
			 byte[] bitmapArray;
			 bitmapArray=Base64.decode(str, Base64.DEFAULT);
			 bitmap=BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);

		 }
		 catch (Exception e) 
		 {
			 e.printStackTrace();
		 }
		 return bitmap;
	 }
	 
	 public static Bitmap resizeBitmap(Bitmap map , int w , int h)
	 {
		 Bitmap targetBitmap = null;
		 int oldWidth = map.getWidth();
		 int oldHeight = map.getHeight();
		 float scale = 0;
		 float wscale = (float)w/oldWidth;
		 float hscale = (float)h/oldHeight;
		 int compareW = (int)(wscale * 100);
		 int compareH = (int)(hscale * 100);
		 if(compareW > 100 && compareH > 100)
		 {
			 scale = (float)1.0;
		 }
		 else
		 {
			 scale = (float)(compareW > compareH ? compareH : compareW)/100;
		 }
	
		 Matrix matrix = new Matrix();
	        
	     matrix.postScale(scale, scale);
	     targetBitmap = Bitmap.createBitmap(map, 0, 0 , oldWidth, oldHeight , matrix , true);
		 return targetBitmap;
	 }
}
