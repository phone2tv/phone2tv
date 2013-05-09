package com.example.phone2tv;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.view.View.OnTouchListener; 

public class TvChannelActivity extends Activity 
{
	private ListView m_tvList;
	private View     m_loadingView;
	private View     m_progressView;
	private View     m_loadingText;
	private boolean m_isLastRow = false;
	private boolean m_isEmpty = true;
	HashMap<String , Integer> m_tvres;
	ArrayList<HashMap<String, Object>> listItem; 
	SimpleAdapter listItemAdapter;
	NetworkAdapter mHttpSession;
	boolean        mRefreshing;
	int            mScrollState;
	LoginInfo      mLoginInfo     = null;
	private ArrayList<TvStationProgram> mTvs = new ArrayList<TvStationProgram>();
	private static final String TAG = "TvChannelActivity";
	private static final HashMap<Integer , Integer> tvId2ResId = new HashMap<Integer, Integer>()
	{
		{
			put(1 , R.drawable.cctv01);
			put(2 , R.drawable.cctv02);
			put(3 , R.drawable.cctv03);
			put(4 , R.drawable.cctv04);
			put(5 , R.drawable.cctv5);
			put(6 , R.drawable.cctv06);
			put(7 , R.drawable.cctv07);
			put(8 , R.drawable.cctv08);
			put(9 , R.drawable.cctv09);
			put(10 , R.drawable.cctv10);
			put(11 , R.drawable.cctv11);
			put(12 , R.drawable.cctv12);
			put(13 , R.drawable.cctv13);
		}
	};
	private Handler listHandler = new Handler()
	{
        public void handleMessage(Message msg) 
        {
            switch(msg.what)
            {
            case 0:
            	ArrayList<TvStationProgram> tvs = (ArrayList<TvStationProgram>)msg.obj;
            	resetList(tvs);
                break;
            case 1:
            	Phone2TvExceptionToast.showExceptionToast(getApplicationContext(),"获取电视台列表失败");
            	break;
            }
            
            finishAsyncLoad();
        }
	};
	
	private void resetList(ArrayList<TvStationProgram> tvs)
	{
		listItem.clear();
		mTvs.clear();
		fillItem(tvs);
	}
	
	
	private void fillItem(ArrayList<TvStationProgram> tvs)
	{
		for(int i = 0 ; i < tvs.size() ; i++)
		{
			Log.d(TAG , "fillItem loop");
			 HashMap<String, Object> map = new HashMap<String, Object>();
			// map.put("imgtv",tvId2ResId.get(tvs.get(i).getTvIndex()));
			 map.put("imgtv",tvId2ResId.get(1));
		     map.put("channel_name", tvs.get(i).getTvName());
		     if(tvs.get(i).getPrograms().size() != 0)
		    	 map.put("current_program" ,  tvs.get(i).getPrograms().get(0).getProgramName());
		     else
		    	 map.put("current_program", "节目已结束");
		     listItem.add(map);
		}
		mTvs.addAll(tvs);
		listItemAdapter.notifyDataSetChanged();
	}
	
	private void asycLoadTvChannels()
	{
		if(mRefreshing)
		{
			Phone2TvExceptionToast.showExceptionToast(getApplicationContext() ,
													  "正在获取节目列表，请稍后");
			return;
		}
		
		new Thread()
		{
			public void run()
			{
				try
				{
					JSONArray jsonArray = mHttpSession.requestAllTvStations();
					ArrayList<TvStationProgram> tvs = mHttpSession.getAllTvStations(jsonArray);
					Message msg = new Message();
					msg.what = 0;
					msg.obj = tvs;
					listHandler.sendMessage(msg);
				}
				catch(Exception e)
				{
					Message msg = new Message();
					msg.what = 1;
					listHandler.sendMessage(msg);
					e.printStackTrace();
				}
				
			}
		}.start();
	}
	
	protected void finishAsyncLoad()
	{
		mRefreshing = false;
		setHeaderVisible(false);
	}
	
	protected void setHeaderVisible(boolean bVisible)
	{
		int flag = 0;
		if(bVisible)
		{
			flag = View.VISIBLE;
		}
		else
		{
			flag = View.GONE;
		}
		 m_progressView.setVisibility(flag);
		 m_loadingText.setVisibility(flag);
	}

	protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tvlist_view);
        Intent intent = getIntent();
        mLoginInfo     = Phone2TvComm.getLoginInfoFromIntent(intent);
        mHttpSession = new NetworkAdapter(mLoginInfo.getServerAddress() , mLoginInfo.getServerPort());
        m_tvList = (ListView)findViewById(R.id.listView1);
        m_loadingView = getLayoutInflater().inflate(R.layout.more_view, null);
		m_progressView = m_loadingView.findViewById(R.id.progress);
		m_loadingText  = m_loadingView.findViewById(R.id.loading_text);
        m_tvList.addHeaderView(m_loadingView);
        m_tvList.setOnScrollListener(new OnScrollListener()
        {
        	  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
        	  {
                  if (firstVisibleItem == 0 && m_loadingView.getBottom() >= 0) 
                  {
                	 if(mScrollState == SCROLL_STATE_TOUCH_SCROLL)
                	 {
                		 setHeaderVisible(true);
                		 asycLoadTvChannels();
                	 }
                  } 
              }
              
        	  public void onScrollStateChanged(AbsListView view,int scrollState) 
        	  {
        		  mScrollState = scrollState;
        	  }
        });
        
        AdapterView.OnItemClickListener m_listOnItemClick = new AdapterView.OnItemClickListener() 
        {
        	  public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
        	  {
        		  Intent intent = new Intent();
        		  intent.setClass(TvChannelActivity.this, ProgramActivity.class);
        		  String channelName = mTvs.get(arg2 - 1).getTvName();
        		  int channelId = mTvs.get(arg2 - 1).getTvIndex();
        		  Bundle bundle = new Bundle();
        		  bundle = Phone2TvComm.putLoginInfo(bundle , mLoginInfo);
        		  bundle = Phone2TvComm.putTvStation(bundle ,  mTvs.get(arg2 - 1));
        		  intent.putExtras(bundle);
        		  startActivity(intent);
        	  }
        };
        m_tvList.setOnItemClickListener(m_listOnItemClick);
        
        initList();
    }
	
	public void onPause()
	{
		super.onPause();
		//System.out.print("in function OnPause\n");
		Log.d(TAG , "OnPause");
	}
	
	public void onStop()
	{
		super.onStop();
	//	System.out.print("in function Onstop\n");
		Log.d(TAG, "onStop");
	}
	
	public void onDestrory()
	{
		super.onDestroy();
	//	System.out.print("in function OnDestrory\n");
		Log.d(TAG , "onDestrory");
	}
	
	public void onRestart()
	{
		super.onRestart();
		Log.d(TAG , "onRestart");
	}
	
	
	protected void initList()
	{
		listItem = new ArrayList<HashMap<String, Object>>(); 
		listItemAdapter = new SimpleAdapter(this,listItem,   
											R.layout.item_view,
											new String[] {"imgtv"   ,  "channel_name" , "current_program"},   
											new int[] {R.id.item_imgheader  ,  R.id.item_tvname , R.id.item_curprogram}
											);  
		 m_tvList.setAdapter(listItemAdapter);	
		 
		 asycLoadTvChannels();
	}
}
