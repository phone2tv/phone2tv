package com.example.phone2tv;

import android.R.id;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class ProgramActivity extends Activity implements OnClickListener
{
	private static final String TAG = "ProgramActivity";
	private Phone2TvTabHost mTimeLineTab;
	int mRequestTab = -1;

	private boolean mRefreshing;
	private int mScrollPreState;
	private int mScrollState;
	private ListView m_programList;
	private ArrayList<HashMap<String, Object>> m_currentProgram;
	private HashMap<Integer, ArrayList<HashMap<String, Object>>> m_programParades;
	ProgramListAdapter m_programAdapter;
	NetworkAdapter m_httpSession;
	View m_progressView;
	View m_loadingTextView;
	View m_loadingView;
	TextView mCurrentProgramView = null;
	TextView mBroadcastView = null;
	TextView mIntroduceView = null;
	TextView mBroadcastRateView = null;
	TextView mWatchingCountView = null;
	View mBanner = null;
	Program mCurrentBroadcastingProgram =null;

	LoginInfo mLoginInfo = null;
	TvStationProgram mTvStation = null;
	TabHost tabs = null;

	protected TabHost getTabHost()
	{
		return tabs;
	}

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_timeline_view);
		mBanner = findViewById(R.id.relativelayout_banner);
		mBanner.setOnClickListener(this);
		m_programParades = new HashMap<Integer, ArrayList<HashMap<String, Object>>>();
		Intent intent = getIntent();
		mLoginInfo = Phone2TvComm.getLoginInfoFromIntent(intent);
		mTvStation = Phone2TvComm.getTvStationFromIntent(intent);
		m_httpSession = new NetworkAdapter(mLoginInfo.getServerAddress(),
				mLoginInfo.getServerPort());
		TextView title = (TextView) findViewById(R.id.text_channel_name);
		title.setText(mTvStation.getTvName());
		initProgramList();
		tabs = (TabHost) findViewById(id.tabhost);
		tabs.setup();
		mTimeLineTab = new Phone2TvTabHost(this, getTabHost(),
				R.layout.tab_widget_item_view, R.id.image_view,
				Phone2TvConst.mDataNormalImage, Phone2TvConst.mDataHighImage,
				R.id.list_program, Phone2TvConst.mProgramTab);

		mTimeLineTab.initialTabHost(0);
		getTabHost().setCurrentTab(2);
		getTabHost().setCurrentTab(0);
		mTimeLineTab.setPhone2TvTabHostListener(mTabListener);
		mRequestTab = 0;

		initCurrentProgram();

		asyncLoadProgramParade();
	}
	
	private int computerBroadcastRate(Date begin , Date end)
	{
		int rate = 0;
		Date curDate = new Date();
		long totle = end.getTime() - begin.getTime();
		long past = curDate.getTime() - begin.getTime();
		if(totle < 0 || past < 0)
		{
			return -1;
		}
		Log.d(TAG , "past: "+ String.valueOf(past) + "totle: "+ String.valueOf(totle));
		rate = (int) ((int) past*100/totle);
		return rate;
	}

	private void initCurrentProgram()
	{
		mCurrentProgramView = (TextView) findViewById(R.id.text_curprogram_name);
		//mBroadcastView = (TextView) findViewById(R.id.text_curprogram_time);
		mBroadcastRateView = (TextView)findViewById(R.id.text_past_time);
		mIntroduceView = (TextView) findViewById(R.id.text_curprogram_introduce);
		mWatchingCountView = (TextView)findViewById(R.id.text_watch_count);
		
		if (0 == mTvStation.getPrograms().size())
		{
			mCurrentProgramView.setText("节目已经结束");
		}
		else
		{
			mCurrentProgramView.setText(mTvStation.getPrograms().get(0)
					.getProgramName());
		}

	}

	private void initProgramList()
	{
		m_loadingView = (View) getLayoutInflater().inflate(R.layout.more_view,
				null);
		m_progressView = m_loadingView.findViewById(R.id.progress);
		m_loadingTextView = m_loadingView.findViewById(R.id.loading_text);

		m_programList = (ListView) findViewById(R.id.list_program);
		m_programList.addHeaderView(m_loadingView);
		m_currentProgram = new ArrayList<HashMap<String, Object>>();

		m_programAdapter = new ProgramListAdapter(this, m_currentProgram,
				R.layout.item_program, new String[] { "begin_time", "end_time",
						"name", "discuss_count" }, new int[] {
						R.id.broadcast_begin_time, R.id.broadcast_end_time,
						R.id.program_name, R.id.program_discuss_num });
		m_programList.setAdapter(m_programAdapter);
		m_programList.setOnItemClickListener(mlistOnItemClick);
		m_programList.setOnItemLongClickListener(mListOnItemLongClick);
		m_programList.setOnScrollListener(new OnScrollListener()
		{
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount)
			{
				if (firstVisibleItem == 0 && m_loadingView.getBottom() >= 0)
				{
					if (mScrollState == SCROLL_STATE_TOUCH_SCROLL
							&& !mRefreshing)
					{
						mRefreshing = true;
						setHeaderVisible(true);
						asyncLoadProgramParade();
					}
				}
			}

			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				mScrollState = scrollState;
			}
		});

	}

	public void onPause()
	{
		super.onPause();
		Log.d(TAG, "onPause");
	}

	public void onStop()
	{
		super.onStop();
		Log.d(TAG, "onStop");
	}

	public void onDestrory()
	{
		super.onDestroy();
		Log.d(TAG, "onDestory");
	}

	public void onRestart()
	{
		super.onRestart();
		Log.d(TAG, "onRestart");
	}

	private ArrayList<HashMap<String, Object>> getItemListByDayOffset(
			int dayOffset)
	{
		ArrayList<HashMap<String, Object>> parade = null;
		parade = m_programParades.get(dayOffset);
		if (null == parade)
		{
			parade = new ArrayList<HashMap<String, Object>>();
			m_programParades.put(dayOffset, parade);
		}
		return parade;
	}

	private boolean broadcasting(Date begin, Date end)
	{
		boolean beginFlag = false;
		boolean endFlag = false;
		Date now = new Date();
		beginFlag = now.after(begin);
		endFlag = now.before(end);
		if (beginFlag && endFlag)
		{
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private void sortProgramByTime(TvStationProgram program)
	{
		ProgramCompare compare = new ProgramCompare();
		Collections.sort(program.mPrograms, compare);
		for (int i = 0; i < program.getPrograms().size(); i++)
		{
			Log.d(TAG, program.getPrograms().get(i).mName);
		}

	}

	private void fillItemFromData(TvStationProgram program, int dayOffset)
	{
		ArrayList<HashMap<String, Object>> temp = getItemListByDayOffset(dayOffset);
		temp.clear();
		HashMap<String, Object> oneItem = null;
		sortProgramByTime(program);
		for (int i = 0; i < program.getPrograms().size(); i++)
		{
			oneItem = new HashMap<String, Object>();
			oneItem.put("program", program.getPrograms().get(i));
			temp.add(oneItem);
		}

		if (mRequestTab == mTimeLineTab.getCurrentTabPos())
		{
			m_currentProgram.clear();
			m_currentProgram.addAll(temp);
			m_programAdapter.notifyDataSetChanged();
		} else
		{
			Log.d(TAG, "refreshing tab is not currenttab");
		}

	}
	
	private void  updateCurrentProgram(TvStationProgram todayParade)
	{
		for(Program  program : todayParade.getPrograms())
		{
			if(broadcasting(program.getBeginTime() , program.getEndTime()))
			{
				mCurrentBroadcastingProgram = program;
				Log.d(TAG , "broad casting: "+program.getProgramName());
				int rate = computerBroadcastRate(program.getBeginTime() , program.getEndTime());
				if( rate > 0)
				{
					String rateStr = String.valueOf(rate);
					rateStr+="%";
					mBroadcastRateView.setText(rateStr.toCharArray() , 0 , rateStr.length());
				}
				
				mCurrentProgramView.setText(program.getProgramName().toCharArray() 
						                    , 0 
						                    , program.getProgramName().length());
				
				String countStr = String.valueOf(program.getCheckinCount());
				mWatchingCountView.setText(countStr.toCharArray() , 0 , countStr.length());
				break;
			}
		}
	}

	private Handler listHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				TvStationProgram programParade = (TvStationProgram) msg.obj;
				fillItemFromData(programParade, mRequestTab);
				if(mRequestTab == 0)
					updateCurrentProgram(programParade);
				finishAsyncLoad();
				break;
			case 1:
				Phone2TvExceptionToast.showExceptionToast(
						getApplicationContext(), "获取电视预告表失败");
				finishAsyncLoad();
				break;
			case 2:
				Phone2TvExceptionToast.showExceptionToast(
						getApplicationContext(), "订阅节目成功");
				break;
			case 3:
				Phone2TvExceptionToast.showExceptionToast(
						getApplicationContext(), "签到成功");
				break;
			}

		}
	};

	AdapterView.OnItemLongClickListener mListOnItemLongClick = new AdapterView.OnItemLongClickListener()
	{

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3)
		{
			// TODO Auto-generated method stub
			Log.d(TAG, "onItemLongClick");
			final String[] operArray = new String[] { "预约", "签到", "收藏" };
			int selectedItem = arg2 - 1;
			int currentTabId = mTimeLineTab.getCurrentTabPos();
			ArrayList<HashMap<String, Object>> temp = getItemListByDayOffset(currentTabId);
			HashMap<String, Object> map = temp.get(selectedItem);
			final int programId = (Integer) map.get("program_id");
			Dialog operDialog = new AlertDialog.Builder(ProgramActivity.this)
					.setTitle("操作")
					.setItems(operArray, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							switch (which)
							{
							case 0:
								asyncBooking();
								break;
							case 1:
								asyncCheckinProgram(programId);
								break;
							case 2:
								asyncSubscribeProgram(programId);
								break;
							}
						}
					}).show();
			return true;
		}
	};

	protected void asyncBooking()
	{
		Phone2TvExceptionToast.showExceptionToast(getApplicationContext(),
				"暂时不能预约");
	}

	protected void asyncCheckinProgram(int id)
	{
		final int programId = id;
		new Thread()
		{
			public void run()
			{
				try
				{
					m_httpSession.requestCheckin(programId,
							mLoginInfo.getAuthToken(), mLoginInfo.getUserId());
					Message msg = Message.obtain();
					msg.what = 3;
					listHandler.sendMessage(msg);
				} catch (Exception e)
				{

				}
			}
		}.start();
	}

	protected void asyncSubscribeProgram(int id)
	{

		final int programId = id;
		new Thread()
		{
			public void run()
			{
				try
				{
					m_httpSession.requestPostSubscribeOper(programId,
							mLoginInfo.getAuthToken(), mLoginInfo.getUserId(),
							true);
					Message message = new Message();
					message.what = 2;
					listHandler.sendMessage(message);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}.start();
	}

	AdapterView.OnItemClickListener mlistOnItemClick = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3)
		{
			int currentTabId = mTimeLineTab.getCurrentTabPos();
			ArrayList<HashMap<String, Object>> list = getItemListByDayOffset(currentTabId);
			//Intent intent = new Intent();
			//intent.setClass(ProgramActivity.this, ProgramCommentActivity.class);
			Program selectOne = (Program) list.get(arg2 - 1).get("program");
			//Bundle bundle = new Bundle();
			//bundle = Phone2TvComm.putLoginInfo(bundle, mLoginInfo);
			//bundle = Phone2TvComm.putTvStation(bundle, mTvStation);
			//bundle = Phone2TvComm.putProgram(bundle, selectOne);
			//intent.putExtras(bundle);
			//startActivity(intent);
			startProgramCommentActivity(selectOne , mLoginInfo , mTvStation);
		}
	};
	
	protected void startProgramCommentActivity(Program program , LoginInfo info , TvStationProgram tv)
	{
		Intent intent = new Intent();
		intent.setClass(ProgramActivity.this, ProgramCommentActivity.class);
		Bundle bundle = new Bundle();
		bundle = Phone2TvComm.putLoginInfo(bundle, info);
		bundle = Phone2TvComm.putTvStation(bundle, tv);
		bundle = Phone2TvComm.putProgram(bundle, program);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	protected void finishAsyncLoad()
	{
		setHeaderVisible(false);
		mRequestTab = -1;
		mRefreshing = false;
	}

	protected void setHeaderVisible(boolean bVisible)
	{
		int flag = 0;
		if (bVisible)
		{
			flag = View.VISIBLE;
		} else
		{
			flag = View.GONE;
		}
		m_progressView.setVisibility(flag);
		m_loadingTextView.setVisibility(flag);
	}

	private void asyncLoadProgramParade()
	{
		setHeaderVisible(true);
		new Thread()
		{
			public void run()
			{
				try
				{
					JSONObject jsonObj = m_httpSession
							.requestProgramByTvstation(mTvStation.getTvIndex(),
									mRequestTab);
					TvStationProgram programParade = m_httpSession
							.getProgramsByTvStation(jsonObj);
					Message msg = new Message();
					msg.what = 0;
					msg.obj = programParade;
					listHandler.sendMessage(msg);
				} catch (Exception e)
				{
					Message msg = new Message();
					msg.what = 1;
					listHandler.sendMessage(msg);
					e.printStackTrace();
				}
			}
		}.start();
	}

	private OnPhone2TvTabHostListener mTabListener = new OnPhone2TvTabHostListener()
	{

		@Override
		public void OnInitialTabHost(String tabTag, Intent intent)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public boolean OnTabChanged(String tabTag)
		{
			// TODO Auto-generated method stub
			if (mRequestTab != -1)
			{
				if (mRequestTab != mTimeLineTab.getCurrentTabPos())
					;
				Phone2TvExceptionToast.showExceptionToast(
						getApplicationContext(), "正在获取节目预告，请稍后切换");

				return false;
			}

			mRequestTab = mTimeLineTab.getTabPosition(tabTag);
			ArrayList<HashMap<String, Object>> temp = getItemListByDayOffset(mRequestTab);
			if (temp.size() == 0)
			{
				m_currentProgram.clear();
				m_programAdapter.notifyDataSetChanged();
				asyncLoadProgramParade();
			} else
			{
				m_currentProgram.clear();
				m_currentProgram.addAll(temp);
				m_programAdapter.notifyDataSetChanged();
				finishAsyncLoad();
			}
			return true;
		}

	};

	public class ProgramCompare implements Comparator
	{

		@Override
		public int compare(Object arg0, Object arg1)
		{
			// TODO Auto-generated method stub
			Program p1 = (Program) arg0;
			Program p2 = (Program) arg1;
			if (p2.mBeginTime.before(p1.mBeginTime))
				return 1;

			return -1;
		}

	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.relativelayout_banner:
			if(mCurrentBroadcastingProgram !=  null)
				startProgramCommentActivity(mCurrentBroadcastingProgram , mLoginInfo  , mTvStation);
			break;
		}
	}
}
