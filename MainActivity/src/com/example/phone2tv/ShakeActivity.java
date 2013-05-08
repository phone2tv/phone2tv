package com.example.phone2tv;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup.LayoutParams;

public class ShakeActivity extends Activity implements OnClickListener
{
	private static final String TAG = "ShakeActivity";
	private SensorManager sensorManager;
    private Vibrator vibrator;
    private ImageView mShakeIcon;
    private View mShakeIconSmall;
    private View mHint;
    private LinearLayout mLinearHotSubsribe;
    private LinearLayout mLinearHotComment;
    private int mOffset = -240;
    public volatile boolean  mRefreshing = false;
    private int mStatus = 0;
    private ImageView mHotSubSlot1;
    private ImageView mHotSubSlot2;
    private ImageView mHotSubSlot3;
    private LoginInfo mLoginInfo = null;
    
	protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.shake_view);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        mShakeIcon =(ImageView)findViewById(R.id.image_shake_big);
        mShakeIconSmall = findViewById(R.id.image_shake_small);
        mLinearHotSubsribe  = (LinearLayout)findViewById(R.id.line_hot_subscribe);
  //      mLinearHotComment   = (LinearLayout)findViewById(R.id.line_hot_comment);
        mHint = findViewById(R.id.text_poster);
        
        mHotSubSlot1 = (ImageView)findViewById(R.id.imageView1);
        mHotSubSlot1.setOnClickListener(this);
        
        mHotSubSlot2 = (ImageView)findViewById(R.id.imageView2);
        mHotSubSlot2.setOnClickListener(this);
        
        mHotSubSlot3 = (ImageView)findViewById(R.id.imageView3);
        mHotSubSlot3.setOnClickListener(this);
        
        Intent intent = getIntent();
//        m_authToken   = intent.getStringExtra("auth_token");
//        m_userId      = intent.getStringExtra("user_id");
        mLoginInfo = Phone2TvComm.getLoginInfoFromIntent(intent);
    }
	
	@Override
    protected void onResume() 
	{
            super.onResume(); // 注册监听器
            Log.d(TAG , "onResume");
            if(sensorManager == null)
            	sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensorManager.registerListener(mSensorEventListener,
                            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                            sensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() 
    {
    	super.onStop();
        Log.d(TAG , "onStop");
        if(sensorManager != null) 
        {
        	sensorManager.unregisterListener(mSensorEventListener);// 解绑定Listener
            sensorManager = null;
        }
    }
    
    protected void onPause()
    {
    	super.onStop();
        Log.d(TAG , "onStop");
        if(sensorManager != null) 
        {
        	sensorManager.unregisterListener(mSensorEventListener);// 解绑定Listener
            sensorManager = null;
        }
    }
    
    private SensorEventListener mSensorEventListener = new SensorEventListener() 
    {
        
        @Override
        public void onSensorChanged(SensorEvent arg0) 
        {
        	   int sensorType = arg0.sensor.getType();
               float[] values = arg0.values;
               
               if (sensorType == Sensor.TYPE_ACCELEROMETER && mRefreshing == false)
               {
            	   ;
                   if (Math.abs(values[0]) > 12 || Math.abs(values[1]) > 12 )
                   {
                	    mRefreshing = true;
                	    mUpdateHandler.sendEmptyMessage(3);
                   }
               }
        }
        
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) 
        {
        	
        }
        
    };
    
    protected void onShakeIconHandler(Message msg)
    {
    //	if(mStatus == 1)
    //	{
    		android.widget.LinearLayout.LayoutParams params =(android.widget.LinearLayout.LayoutParams)mLinearHotSubsribe.getLayoutParams();
    		mOffset = mOffset + 24;
    		params.topMargin = mOffset;
    		mLinearHotSubsribe.setLayoutParams(params);
    //	}
    //	else
    //	{
    //		android.widget.LinearLayout.LayoutParams params =(android.widget.LinearLayout.LayoutParams)mLinearHotComment.getLayoutParams();
    //		mOffset = mOffset + 24;
    //		params.topMargin = mOffset;
    //		mLinearHotComment.setLayoutParams(params);
    //	}
    }
    
    protected void setVisibility(boolean reset) 
    {  
    	if(reset)
    	{
    		mShakeIcon.setVisibility(View.VISIBLE);
    		mShakeIconSmall.setVisibility(View.GONE);
    		mLinearHotSubsribe.setVisibility(View.GONE);
    	//	mLinearHotComment.setVisibility(View.GONE);
    		mHint.setVisibility(View.GONE);
    		android.widget.LinearLayout.LayoutParams params =(android.widget.LinearLayout.LayoutParams)mLinearHotSubsribe.getLayoutParams();
        	mOffset = -240;
        	params.topMargin = mOffset;
        	mLinearHotSubsribe.setLayoutParams(params);
        	
        	
    	}
    	else
    	{
    		mShakeIcon.setVisibility(View.GONE);
    		mShakeIconSmall.setVisibility(View.VISIBLE);
    		mLinearHotSubsribe.setVisibility(View.VISIBLE);
    		mHint.setVisibility(View.VISIBLE);
    	}
    }
    
    Handler mUpdateHandler = new Handler()
    {
    	public void handleMessage(Message msg)
    	{
    		switch(msg.what)
    		{
    			case 0:
    				onShakeIconHandler(msg);
    			break;
    			
    			case 1:
    				mRefreshing = false;
    			break;
    			
    			case 2:
    				    setVisibility(false);
            	    	ShakeIconAnimation(10,50);
    			break;
    			
    			case 3:
    				setVisibility(true);
    				vibrator.vibrate(100);
    				asyncGetRecommendation(0);
    			break;
    		}
    	}
    };
    protected void asyncGetRecommendation(int way)
    {
    	new Thread()
    	{
    		public void run()
    		{
    		
    				try 
    				{
    					Thread.sleep(1000);
    				}
    				catch(Exception e)
    				{
    					e.printStackTrace();
    				}
    				Log.d(TAG , "asyncGetRecommendation");
    				mUpdateHandler.sendEmptyMessage(2);
    		}
    	}.start();
    }
    
    protected void ShakeIconAnimation(int count , int nSleep)
    {
    	Log.d(TAG , "ShakeIconAnimation");
    	final int nCount = count;
    	final int nTimeinterval = nSleep;
    	setVisibility(false);
    	new Thread()
    	{
    		public void run()
    		{
    			int i = nCount;
    			while(i-- > 0)
    			{
    				try 
    				{
    					Thread.sleep(nTimeinterval);
    				}
    				catch(Exception e)
    				{
    					e.printStackTrace();
    				}
    				mUpdateHandler.sendEmptyMessage(0);
    			}
    			mUpdateHandler.sendEmptyMessage(1);
    		}
    	}.start();
    }

	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		 Intent intent = new Intent();
		 intent.setClass(ShakeActivity.this, ProgramCommentActivity.class);
    	 String programName  = "天下足球";
    	 intent.putExtra("program_name", programName);
    	 intent.putExtra("program_id", 16);
    	 Bundle bundle = new Bundle();
    	 bundle = Phone2TvComm.putLoginInfo(bundle, mLoginInfo);
    	 intent.putExtras(bundle);
		 startActivity(intent);
	}
}
