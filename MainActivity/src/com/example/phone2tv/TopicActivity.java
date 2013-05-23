package com.example.phone2tv;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import greendroid.app.GDActivity;
public class TopicActivity extends GDActivity implements OnClickListener
{
	private final static int SLOT_NUM = 5;
	private final static String TAG="TopicActivity";
	private ArrayList<Program> mSubscribes = null;
	private NetworkAdapter         mHttpSession;
	private LoginInfo              mLoginInfo = null;
	
	
	protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setActionBarContentView(R.layout.mytopics_view);
        setTitle("我的收藏");
        Intent intent = getIntent(); 
        mLoginInfo = Phone2TvComm.getLoginInfoFromIntent(intent);
        mHttpSession = new NetworkAdapter(mLoginInfo.getServerAddress() , 
        		                          mLoginInfo.getServerPort());
        Log.d(TAG , "onCreate asyncLoadSubscribes");
    }
	
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG , "onResume asyncLoadSubscribes");
		asyncLoadSubscribes();
	}
	
	private Handler mUpdateUI = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case 0:
				mSubscribes = (ArrayList<Program>)msg.obj;
				UpdateSubscribeUI(mSubscribes);
				break;
			case 1:
				Log.d(TAG , "handleMessage asyncLoadSubscribes");
				asyncLoadSubscribes();
				break;
			}
		}
	};
	
	private void UpdateSubscribeUI(ArrayList<Program> programs)
	{
		LinearLayout subCanvas = (LinearLayout)findViewById(R.id.linearlayout_subslot);
		int subChildCount = subCanvas.getChildCount();
		if(subChildCount > 0)
			subCanvas.removeViews(0, subChildCount);
		
		int i = 0 ;
		int size = programs.size();
		for(; i < size ; i++)
		{
			InsertSubscribe(subCanvas , programs.get(i) , i);
		}
		
		for(; i < SLOT_NUM ; i++)
		{
			Log.d(TAG , "i= "+i);
			InsertSlot(subCanvas);
		}
	}
	
	private void InsertSubscribe(LinearLayout subCanvas , Program oneProgram , int index)
	{
		RelativeLayout subItem = (RelativeLayout)getLayoutInflater().inflate(R.layout.subscribe_program_stand,  null);
		subItem.setOnClickListener(this);
		boolean bExpand = false;
		subItem.setTag(bExpand);
		
		//fill name
		TextView program_name =(TextView)subItem.findViewById(R.id.text_program_name);
		String name = oneProgram.getProgramName();
		program_name.setText(name.toCharArray(), 0 , name.length());
		
		//fill subscribe num
		TextView subCount = (TextView)subItem.findViewById(R.id.subscribe_count);
		String count ="订阅人数(";
		count+=String.valueOf(oneProgram.getSubscribeCount());
		count+=")";
		subCount.setText(count.toCharArray() , 0 , count.length());
		
		//fill comment num
		TextView commentCount = (TextView)subItem.findViewById(R.id.text_comment_num);
		count = String.valueOf(oneProgram.getCommentCount());
		commentCount.setText(count.toCharArray() , 0 , count.length());
		
		//fill activity num
		TextView activityCount = (TextView)subItem.findViewById(R.id.text_activity_num);
		count = String.valueOf(0);
		activityCount.setText(count.toCharArray() , 0 , count.length());
		
		ImageView subscribe_comment = (ImageView)subItem.findViewById(R.id.subscribe_comment);
		subscribe_comment.setOnClickListener(this);
		subscribe_comment.setTag(index);
		
		ImageView subscrbie_activity = (ImageView)subItem.findViewById(R.id.subscribe_activity);
		subscrbie_activity.setOnClickListener(this);
		subscrbie_activity.setTag(index);
		
		ImageView subscrbie_unsub = (ImageView)subItem.findViewById(R.id.subscribe_unsubscribe);
		subscrbie_unsub.setOnClickListener(this);
		subscrbie_unsub.setTag(index);
		subCanvas.addView(subItem);
	}
	
	private void InsertSlot(LinearLayout subCanvas)
	{
		Log.d(TAG , "InsertSlot");
		RelativeLayout subItem = (RelativeLayout)getLayoutInflater().inflate(R.layout.subscribe_empty_slot,  null);
		subCanvas.addView(subItem);
	}
	
	private void asyncLoadSubscribes()
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					JSONArray json = null;
					json = mHttpSession.requestSubscribeProgramByUser(mLoginInfo.getUserId(), 
							                                          mLoginInfo.getAuthToken());
					ArrayList<Program> programs = mHttpSession.getSubscribePrograms(json);
					Message msg = new Message();
					msg.what = 0;
					msg.obj = programs;
					mUpdateUI.sendMessage(msg);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.image_subscribe_icon :
				Program targetProgram = (Program)v.getTag();
				UnSubscribeProgram(targetProgram);
				break;
			case R.id.subscribe_relativelayout:
				boolean b = (Boolean) v.getTag();
				if(!b)
					expandSubscribeItem(v);
				else 
					folderSubscribeItem(v);
				v.setTag(!b);
				break;
			case R.id.subscribe_unsubscribe:
				int index = (Integer)v.getTag();
				UnSubscribeProgram(mSubscribes.get(index));
				break;
			case R.id.subscribe_activity:
			case R.id.subscribe_comment:
				index = (Integer)v.getTag();
				StartProgramCommentActivity(mSubscribes.get(index));
				break;
		}
	}
	
	public void expandSubscribeItem(View v)
	{
		RelativeLayout selectItem = (RelativeLayout)v;
		RelativeLayout bg =(RelativeLayout)selectItem.findViewById(R.id.subscribe_background);
		bg.setBackgroundResource(R.drawable.subscribe_expand_bg);
		
		LinearLayout buttons = (LinearLayout)selectItem.findViewById(R.id.subscribe_buttons);
		buttons.setVisibility(View.VISIBLE);
	}
	
	public void folderSubscribeItem(View v)
	{
		RelativeLayout selectItem = (RelativeLayout)v;
		RelativeLayout bg =(RelativeLayout)selectItem.findViewById(R.id.subscribe_background);
		bg.setBackgroundResource(R.drawable.subscribe_stand_bg);
		LinearLayout buttons = (LinearLayout)selectItem.findViewById(R.id.subscribe_buttons);
		buttons.setVisibility(View.GONE);
	}
	
	private void StartProgramCommentActivity(Program targetProgram)
	{
		 Intent intent = new Intent();
		 intent.setClass(TopicActivity.this, ProgramCommentActivity.class);
    	 String programName  = targetProgram.getProgramName();
    	 Integer    programId    = targetProgram.getIndex();
    	 intent.putExtra("program_name", programName);
    	 intent.putExtra("program_id", programId);
    	 Bundle bundle = new Bundle();
    	 bundle = Phone2TvComm.putLoginInfo(bundle, mLoginInfo);
    	 intent.putExtras(bundle);
		 startActivity(intent);
	}
	
	private void UnSubscribeProgram(final Program targetProgram)
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					int id = targetProgram.getIndex();
					mHttpSession.requestPostSubscribeOper(id , 
							                              mLoginInfo.getAuthToken() , 
							                              mLoginInfo.getUserId(), false);
					mUpdateUI.sendEmptyMessage(1);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					mUpdateUI.sendEmptyMessage(2);
				}
			}
		}.start();
	}
}

