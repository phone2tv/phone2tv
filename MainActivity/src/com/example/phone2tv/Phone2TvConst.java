package com.example.phone2tv;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

public class Phone2TvConst 
{
	public static HashMap<Integer , Integer> mDataNormalImage = new  HashMap<Integer , Integer>()
	{
		{
			put(0, R.drawable.jintian);
			put(1, R.drawable.mingtian);
			put(2, R.drawable.houtian);
		}
		
	};
	
	public static HashMap<Integer , Integer> mDataHighImage = new HashMap<Integer , Integer>()
	{
		{
			put(0, R.drawable.jintian_h);
			put(1, R.drawable.mingtian_h);
			put(2, R.drawable.houtian_h);
		}
	};

	public static HashMap<String , Integer> mProgramTab = new HashMap<String , Integer>()
	{
		{
			put("0" , 0);
			put("1",  1);
			put("2" , 2);
		}
	};
	
	//Main Tab Const
	public static HashMap<Integer , Integer> mMainTabNormalImage = new HashMap<Integer , Integer>()
	{
		{
			 put(0, R.drawable.sao);
			 put(1 , R.drawable.shuo);
			 put(2 , R.drawable.yao);
			 put(3 , R.drawable.kan);
			 put(4, R.drawable.you);
		}
	};
	
	public static HashMap<Integer , Integer> mMainTabHighImage = new HashMap<Integer , Integer>()
	{
		{
			put(0, R.drawable.sao_h);
			put(1, R.drawable.shuo_h);
			put(2, R.drawable.yao_h);
			put(3, R.drawable.kan_h);
			put(4, R.drawable.you_h);
		}
	};
	
	public static HashMap<String , Integer>  mPosition = new HashMap<String , Integer>()
	{
		{
			put("0" , 0);
			put("1", 1);
			put("2", 2);
			put("3" , 3);
			put("4" , 4);
		}
	};
	
	public static HashMap<Integer , Class<?>> mMainTabSepc = new HashMap<Integer , Class<?>>()
	{
		{
			put(0 , ScanActivity.class);
			put(1 , TopicActivity.class);
			put(2 , ShakeActivity.class);
			put(3 , TvChannelActivity.class);
			put(4 , PeopleActivity.class);
		}
	};
	
}
