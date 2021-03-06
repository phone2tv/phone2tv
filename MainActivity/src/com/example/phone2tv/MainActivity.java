package com.example.phone2tv;

import android.net.UrlQuerySanitizer.ValueSanitizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.app.Activity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Button;
import android.content.*;
import android.util.*;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.ListView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap; 

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.SimpleAdapter; 
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.phone2tv.NetworkAdapter;

public class MainActivity extends Activity implements OnClickListener , OnTouchListener , OnFocusChangeListener 
{
	public final static String TAG ="MainActivity";
	public final static String EXTRA_MESSAGE = "com.example.phone2tv.MESSAGE";
	public ImageView mSaveCodeBt;
	public boolean   mIsSaveCode = false;
	public ImageView mLoginBt;
	public EditText  mUserNameEditor;
	public ImageView mUserNameImg;
	public EditText  mPasswdEditor;
	public ImageView mPasswdImg;
	public NetworkAdapter mHttpSession = null;
	public boolean        mIsLoging = false;
	private LoginInfo     mLoginInfo = null;

	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.activity_main);
        initControl();
        DefaultLoginInfo();
        initServer();
    }
	
	protected void initServer() 
	{
		String serverAddress = null;
		String serverPort = null;
		serverAddress = "tvshow.ap01.aws.af.cm";
		serverPort    = "80";
		mLoginInfo = new LoginInfo();
		mLoginInfo.setServerAddress(serverAddress);
		mLoginInfo.setServerPort(serverPort);
		
		mHttpSession = new NetworkAdapter(mLoginInfo.getServerAddress() , 
				                          mLoginInfo.getServerPort());
	}
	
	protected void SaveLoginInfo()
	{
		SharedPreferences loginInfo = getSharedPreferences("login_info", 0);
		loginInfo.edit().putBoolean("is_save", mIsSaveCode).commit();
		if(mIsSaveCode)
		{
			loginInfo.edit().putString("user_name", mUserNameEditor.getText().toString()).commit();
			loginInfo.edit().putString("passwd", mPasswdEditor.getText().toString()).commit();
		}
		
	}
	
	protected void DefaultLoginInfo()
	{
		SharedPreferences loginInfo = getSharedPreferences("login_info", 0);
		mIsSaveCode        = loginInfo.getBoolean("is_save" , false);
		String defUserName = null;
		String defPasswd = null;
		if(mIsSaveCode)
		{
			mSaveCodeBt.setImageResource(R.drawable.code_save_b);
			defUserName = loginInfo.getString("user_name", "");
			defPasswd   = loginInfo.getString("passwd" , "");
		}
		else
		{
			mSaveCodeBt.setImageResource(R.drawable.code_save);
			defUserName = "";
			defPasswd = "";
		}
		
		
		
		mUserNameEditor.setText(defUserName);
		if(defUserName.length() != 0)
		{
			mUserNameImg.setImageResource(R.drawable.user_input_b);
		}
		
		mPasswdEditor.setText(defPasswd);
		if(defPasswd.length() != 0)
		{
			
			mPasswdImg.setImageResource(R.drawable.code_input_b);
		}

	}
	
	protected JSONObject StringToJson(String content)
	{
		JSONObject obj = null;
		try
		{
			obj = new JSONObject(content);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return obj;
	}
	

	
	protected void initControl()
	{
		mSaveCodeBt =(ImageView)findViewById(R.id.image_code_save);
		mSaveCodeBt.setOnClickListener(this);
		mLoginBt    =(ImageView)findViewById(R.id.image_login);
		mLoginBt.setOnTouchListener(this);
		mLoginBt.setOnClickListener(this);
		mUserNameEditor = (EditText)findViewById(R.id.text_user_name);
		mUserNameEditor.setOnFocusChangeListener(this);
		mUserNameImg=(ImageView)findViewById(R.id.image_user);
		mPasswdEditor = (EditText)findViewById(R.id.text_password);
		mPasswdEditor.setOnFocusChangeListener(this);
		mPasswdImg  =(ImageView)findViewById(R.id.image_code);
	}

	@Override
	public void onClick(View arg0) 
	{
		// TODO Auto-generated method stub
		int targetId = arg0.getId();
		if(targetId == mUserNameEditor.getId())
		{
			mUserNameImg.setImageResource(R.drawable.user_input_b);
			mLoginBt.setImageResource(R.drawable.login);
		}
		else if(targetId == mPasswdEditor.getId())
		{
			mPasswdImg.setImageResource(R.drawable.code_input_b);
			mLoginBt.setImageResource(R.drawable.login);
		}
		else if(targetId == mSaveCodeBt.getId())
		{
			mIsSaveCode = !mIsSaveCode;
			if(mIsSaveCode)
			{
				mSaveCodeBt.setImageResource(R.drawable.code_save_b);
			}
			else
			{
				mSaveCodeBt.setImageResource(R.drawable.code_save);
			}
			
		}
		else if(targetId == mLoginBt.getId())
		{
			asycLoadLogin();
		}
		
	}
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case 0:
				JSONObject loginResponse = (JSONObject)msg.obj;
				handleLoginResponse(loginResponse);
				break;
			case 1:
				handleLoginFailed();
				break;
			}
			mIsLoging = false;
		}
		
		public void handleLoginFailed()
		{
			Toast toast = Toast.makeText(getApplicationContext(),
				     "�������", Toast.LENGTH_LONG);
				   toast.setGravity(Gravity.CENTER, 0, 0);
				   toast.show();
		}
		
		public void handleLoginResponse(JSONObject json)
		{
			try
			{
				JSONObject loginStatus = json.getJSONObject("session");
				String errStr = loginStatus.getString("error");
				if(errStr.contentEquals("Success"))
				{
					
					SaveLoginInfo();
					String token = loginStatus.getString("auth_token");
					String userId = loginStatus.getString("user_id");
					Intent intent = new Intent();
					mLoginInfo.setUserId(userId);
					mLoginInfo.setAuthToken(token);
					Bundle bundle = new Bundle();
					bundle = Phone2TvComm.putLoginInfo(bundle , mLoginInfo);
					intent.putExtras(bundle);
			   		intent.setClass(MainActivity.this, MainTabActivity.class);
			   		startActivity(intent);
			   		finish();
				}
				else
				{
					Toast toast = Toast.makeText(getApplicationContext(),
						     "�û���/�������", Toast.LENGTH_LONG);
						   toast.setGravity(Gravity.CENTER, 0, 0);
						   toast.show();
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}
	};
	
	public void asycLoadLogin()
	{
		if(mIsLoging == false)
		{
			mIsLoging = true;
		}
		else
			return;
		
		new Thread()
		{
			public void run()
			{
				try
				{
					JSONObject loginResponse;
					loginResponse = mHttpSession.requestLogin(mUserNameEditor.getText().toString(),
											  				  mPasswdEditor.getText().toString());
					Message msg = new Message();
					msg.what = 0;
					msg.obj = loginResponse;
					mHandler.sendMessage(msg);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					Message msg = new Message();
					msg.what = 1;
					mHandler.sendMessage(msg);
				}
			}
		}.start();
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) 
	{
		// TODO Auto-generated method stub
		int targetId = arg0.getId();
		if(targetId == mLoginBt.getId() && arg1.getAction() == MotionEvent.ACTION_DOWN)
		{
			Log.d(TAG , "onTouch down");
			mLoginBt.setImageResource(R.drawable.login_blue);
		}
		else if(targetId == mLoginBt.getId() && arg1.getAction() == MotionEvent.ACTION_UP)
		{
			Log.d(TAG , "onTouch up");
			mLoginBt.setImageResource(R.drawable.login);
		}
		return false;
	}

	@Override
	public void onFocusChange(View arg0, boolean arg1) 
	{
		// TODO Auto-generated method stub
		if(arg0.getId() == mUserNameEditor.getId() && arg1)
		{
			mUserNameImg.setImageResource(R.drawable.user_input_b);
			mLoginBt.setImageResource(R.drawable.login);
		}
		if(arg0.getId() == mPasswdEditor.getId() && arg1)
		{
			mPasswdImg.setImageResource(R.drawable.code_input_b);
			mLoginBt.setImageResource(R.drawable.login);
		}
	}
    
}
