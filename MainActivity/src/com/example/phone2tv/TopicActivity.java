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

public class TopicActivity extends Activity implements OnClickListener
{
	private final static String TAG="TopicActivity";
	private ArrayList<Program> mSubscribes = null;
//	private String                 m_authToken;
//	private String                 m_userId;
	private NetworkAdapter         mHttpSession;
	private LoginInfo              mLoginInfo = null;
//	private String                 mServerAddress;
//	private String                 mServerPort;

	
	protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.mytopics_view);
        Intent intent = getIntent(); 
        mLoginInfo = Phone2TvComm.getLoginInfoFromIntent(intent);
//        m_authToken = intent.getStringExtra("auth_token");
//        m_userId    = intent.getStringExtra("user_id");
//        mServerAddress = intent.getStringExtra("server_address");
//        mServerPort    = intent.getStringExtra("server_port");
        mHttpSession = new NetworkAdapter(mLoginInfo.getServerAddress() , 
        		                          mLoginInfo.getServerPort());
        asyncLoadSubscribes();
    }
	
	protected void onResume()
	{
		super.onResume();
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
				asyncLoadSubscribes();
				break;
			}
		}
	};
	
	private void UpdateSubscribeUI(ArrayList<Program> programs)
	{
		LinearLayout subCanvas = (LinearLayout)findViewById(R.id.linearlayout_subslot);
		int subChildCount = subCanvas.getChildCount();
		if(subChildCount > 1)
			subCanvas.removeViews(1, subChildCount - 1);
		
		int i = 0 ;
		int size = programs.size();
		for(; i < size ; i++)
		{
			InsertSubscribe(subCanvas , programs.get(i));
		}
		
		for(; i < 5 ; i++)
		{
			InsertSlot(subCanvas);
		}
	}
	
	private void InsertSubscribe(LinearLayout subCanvas , Program oneProgram)
	{
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , RelativeLayout.LayoutParams.WRAP_CONTENT) ;
		param.leftMargin = 16;
		param.rightMargin = 16;
		param.topMargin = 4;
		
		LinearLayout subItem = (LinearLayout)getLayoutInflater().inflate(R.layout.item_topic,  null);
		subItem.setOnClickListener(this);
		subItem.setTag(oneProgram);
		subItem.setLayoutParams(param);
		ImageView button = (ImageView)subItem.findViewById(R.id.image_subscribe_icon);
		button.setOnClickListener(this);
		button.setTag(oneProgram);
		
		TextView program_name =(TextView)subItem.findViewById(R.id.text_topic_name);
		String name = oneProgram.getProgramName();
		program_name.setText(name.toCharArray(), 0 , name.length());
		subCanvas.addView(subItem);
	}
	
	private void InsertSlot(LinearLayout subCanvas)
	{
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , RelativeLayout.LayoutParams.WRAP_CONTENT) ;
		param.leftMargin = 16;
		param.rightMargin = 16;
		param.topMargin = 4;
		LinearLayout subItem = (LinearLayout)getLayoutInflater().inflate(R.layout.item_topic_slot,  null);
		subItem.setLayoutParams(param);
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
			default:
				Program selectProgram = (Program)v.getTag();
				StartProgramCommentActivity(selectProgram);
				break;
		}
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

