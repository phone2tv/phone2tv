package com.example.phone2tv;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ProgramListAdapter extends SimpleAdapter {
	public class ProgramViewHolder
	{
		TextView mBeginTime;
		TextView mEndTime;
		TextView mProgramName;
		TextView mDisscussNum;
	};

	private final static String TAG="ProgramListAdapter";
	private ArrayList<HashMap<String , Object>> mItemData;
	private Context mContext;
	private int     mResource;
	public ProgramListAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) 
	{
		super(context, null , resource , null , null);
		// TODO Auto-generated constructor stub
		mItemData = (ArrayList<HashMap<String , Object>>)data;
		mContext = context;
		mResource = resource;
	}
	
	private void bindView(View convertView)
	{
		ProgramViewHolder holder = new ProgramViewHolder();
		holder.mBeginTime = (TextView)convertView.findViewById(R.id.broadcast_begin_time);
		holder.mEndTime   = (TextView)convertView.findViewById(R.id.broadcast_end_time);
		holder.mProgramName = (TextView)convertView.findViewById(R.id.program_name);
		convertView.setTag(holder);
	}
	
	private void fillViewFromData(int position , View convertView , HashMap<String , Object> data)
	{
		ProgramViewHolder holder = (ProgramViewHolder)convertView.getTag();
		Program one = (Program)data.get("program");
		Date curDate = new Date(System.currentTimeMillis());
		int color = Color.rgb(48, 48, 48);
		if(one.getEndTime().before(curDate))
			color = Color.rgb(125, 125, 125);
		
		String tmp = Phone2TvComm.DateToString(one.getBeginTime(), Phone2TvComm.mFormat[2]);
		holder.mBeginTime.setText(tmp.toCharArray() , 0, tmp.length());
		holder.mBeginTime.setTextColor(color);
		
		tmp = Phone2TvComm.DateToString(one.getEndTime(), Phone2TvComm.mFormat[2]);
		holder.mEndTime.setText(tmp.toCharArray(), 0, tmp.length());
		holder.mEndTime.setTextColor(color);
		
		tmp = one.getProgramName();
		holder.mProgramName.setText(tmp.toCharArray() , 0 , tmp.length());
		holder.mProgramName.setTextColor(color);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View view;
		Log.d(TAG , "getView: "+position);
		if(null == convertView)
		{
			LayoutInflater inflater = null;
			inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(mResource, null);
			bindView(convertView);
		}
		
		fillViewFromData(position , convertView , mItemData.get(position));
		
		return convertView;
	}
	
	
	 
	@Override
	public int getCount()
	{
		int nSize = mItemData.size();
		return nSize;
	}

	@Override
	public Object getItem(int position)
	{
		Log.d(TAG , "getItem :"+position);
		return mItemData.get(position);
	}
	
	@Override
	public long getItemId(int position) 
	{
		Log.d(TAG , "getItemId :"+position);
		return position;
	}		 

}
