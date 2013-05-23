package com.example.phone2tv;

import java.util.HashMap;
import java.util.Iterator;

import android.R.id;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

interface OnPhone2TvTabHostListener
{
	public void OnInitialTabHost(String tabTag , Intent intent);
	public boolean OnTabChanged(String tabTag);
}

public class Phone2TvTabHost 
{
	private Context mContext = null;
	private TabHost mTabHost = null;
	private int     mWidgetLayoutId = -1;
	private int     mImageLayoutId = -1;
	private HashMap<Integer , Integer>  mNormalImage = null;
	private HashMap<Integer , Integer>  mHighImage = null;
	private HashMap<Integer , Class<?>> mTriggerClass = null;
	private Integer                     mTriggerId   = null;
	private HashMap<String , Integer>  mPosition     = null;
	private OnPhone2TvTabHostListener  mOnPhone2TvTabHostListener = null;
	private int                     mCurrentId = -1;
	private int                     mPreId  = -1;
	
	public Phone2TvTabHost(Context context,
			               TabHost tab,
			               int layoutId ,
			               int imageLayoutId ,
			               HashMap<Integer , Integer>  normalImage ,
			               HashMap<Integer , Integer>  highImage   , 
			               HashMap<Integer , Class<?>> triggerClass,
			               HashMap<String , Integer>  position)
	{
		mContext = context;
		mTabHost = tab;
		mWidgetLayoutId = layoutId;
		mImageLayoutId = imageLayoutId;
		mNormalImage = normalImage;
		mHighImage   = highImage;
		mTriggerClass = triggerClass;
		mPosition     = position;
		mOnPhone2TvTabHostListener = null;
	}
	
	public Phone2TvTabHost(Context context,
			               TabHost tab , 
			               int layoutId ,
			               int imageLayoutId ,
			               HashMap<Integer , Integer>  normalImage ,
			               HashMap<Integer , Integer>  highImage   , 
			               Integer triggerId,
			               HashMap<String , Integer>  position)
	{
		mContext = context;
		mTabHost = tab;
		mWidgetLayoutId = layoutId;
		mImageLayoutId = imageLayoutId;
		mNormalImage = normalImage;
		mHighImage   = highImage;
		mTriggerId   = triggerId;
		mPosition     = position;
		mOnPhone2TvTabHostListener = null;
	}
	
	public void initialTabHost(int pos)
	{
		
		for(int i = 0 ; i < mPosition.size() ; i++)
		{
			String tag = String.valueOf(i);
			int tabPos = i;
			TabSpec spec = mTabHost.newTabSpec(tag);
			spec.setIndicator(createTabView(mNormalImage.get(tabPos)));
			if(mTriggerClass != null)
			{
				Intent intent = new Intent();
				Class<?> cls = mTriggerClass.get(tabPos);
				if(cls != null)
					intent.setClass(mContext, cls);
				if(mOnPhone2TvTabHostListener != null)
					mOnPhone2TvTabHostListener.OnInitialTabHost(tag , intent);
				spec.setContent(intent);
			}
			else if(mTriggerId != null)
			{
				spec.setContent(mTriggerId);
			}
			mTabHost.addTab(spec);
		}

		
		mTabHost.setOnTabChangedListener(new OnTabChangeListener()
		{

			@Override
			public void onTabChanged(String tag) 
			{
				boolean flag = true;
				int tabId = mPosition.get(tag);
				// TODO Auto-generated method stub
				if(mOnPhone2TvTabHostListener != null)
				{
					flag = mOnPhone2TvTabHostListener.OnTabChanged(tag);
					if(flag == false)
					{
						//OnTabChanged failed
						mTabHost.setCurrentTab(mCurrentId);
						return;
					}
				}
				lightCurrentTab(tabId);
			}
			
		});
		mTabHost.setCurrentTab(pos);
		lightCurrentTab(pos);
	}
	
	public void lightCurrentTab(int tabId)
	{
		mPreId = mCurrentId;
		mCurrentId = tabId;
		setTabHighlight(mCurrentId);
		if(mPreId != -1 && mPreId != mCurrentId)
			setTabNormal(mPreId);
	}
	
	public int getTabPosition(String tab)
	{
		return mPosition.get(tab);
	}
	
	public int getCurrentTabPos()
	{
		return mCurrentId;
	}
	
	public int getPreTabPos()
	{
		return mPreId;
	}
	
	
	public void setPhone2TvTabHostListener(OnPhone2TvTabHostListener l)
	{
		mOnPhone2TvTabHostListener = l;
	}
	
	private boolean setTabHighlight(int tabId )
	 {
		 TabWidget widget = mTabHost.getTabWidget();
		 View view = widget.getChildAt(tabId); 
		 ImageView imgView = (ImageView)view.findViewById(mImageLayoutId);
		 imgView.setImageResource(mHighImage.get(tabId));
		 return true;
	 }
	 
	 private boolean setTabNormal(int tabId)
	 {
		 TabWidget widget = mTabHost.getTabWidget();
		 View view = widget.getChildAt(tabId);
		 ImageView imgView = (ImageView)view.findViewById(mImageLayoutId);
		 imgView.setImageResource(mNormalImage.get(tabId));
		 return true;
	 }
	 
	 
	 private View createTabView(int imgId)
	 {
		 View tabWidgetView     = LayoutInflater.from(mContext).inflate(mWidgetLayoutId, null);
		 ImageView tabWidgetImg = (ImageView)tabWidgetView.findViewById(mImageLayoutId);
		 tabWidgetImg.setImageResource(imgId);
		 return tabWidgetView;
	 }
	
}
