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
//	public String    mServerAddress = null;
//	public String    mServerPort    = null;
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
		JSONObject server = getServerInfo();
		String serverAddress = null;
		String serverPort = null;
		try
		{
			serverAddress = server.getString("address");
			serverPort    = server.getString("port");
			Log.d(TAG , "server address : " + serverAddress + "server port :" + serverPort);
		}
		catch(Exception e)
		{
			serverAddress = "tvshow.ap01.aws.af.cm";
			serverPort = "80";
			Log.d(TAG , "default address");
		}
		
		mLoginInfo = new LoginInfo();
		mLoginInfo.setServerAddress(serverAddress);
		mLoginInfo.setServerPort(serverPort);
		
		mHttpSession = new NetworkAdapter(mLoginInfo.getServerAddress() , 
				                          mLoginInfo.getServerPort());
	}
	
	protected void DefaultLoginInfo()
	{
		try
		{
			JSONObject obj = getLoginInfo();
			String defUserName = obj.getString("user_name");
			String defPasswd   = obj.getString("passwd");
			if(defUserName.length() != 0)
			{
				mUserNameEditor.setText(defUserName);
				mUserNameImg.setImageResource(R.drawable.user_input_b);
			}
			if(defPasswd.length() != 0)
			{
				mPasswdEditor.setText(defPasswd);
				mPasswdImg.setImageResource(R.drawable.code_input_b);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	protected JSONObject readFromConfig(String fileName , String sectionName)
	{
		JSONObject section = null;
		try
		{
			String content = readRecordInfo(fileName);
			JSONObject configFile = StringToJson(content);
			section = configFile.getJSONObject(sectionName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return section;		
	}
	
	protected JSONObject getServerInfo()
	{
		JSONObject serverInfo = null;
		serverInfo = readFromConfig("phone2tvConfig.json" , "server_info");
		return serverInfo;
	}
	
	protected JSONObject getLoginInfo()
	{
		JSONObject loginInfo = null;
		loginInfo = readFromConfig("phone2tvConfig.json" , "login_info");
		return loginInfo;
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
	
	protected String readRecordInfo(String fileName)
	{
		String configContent;
		try
		{
			InputStream is = getAssets().open(fileName);
			int size = is.available();
			byte buffer[] = new byte[size];
			is.read(buffer);
			is.close();
			configContent = new String(buffer , "UTF-8");
			return  configContent;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
		
		
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
			if(mIsSaveCode)
			{
				mSaveCodeBt.setImageResource(R.drawable.code_save);
			}
			else
			{
				mSaveCodeBt.setImageResource(R.drawable.code_save_b);
			}
			mIsSaveCode = !mIsSaveCode;
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
				     "ÍøÂç´íÎó", Toast.LENGTH_LONG);
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
				}
				else
				{
					Toast toast = Toast.makeText(getApplicationContext(),
						     "ÓÃ»§Ãû/ÃÜÂë´íÎó", Toast.LENGTH_LONG);
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
