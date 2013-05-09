package com.example.phone2tv;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

public class MainTabActivity extends TabActivity 
{
	private static final String TAG = "MainTabActivity";
	private Phone2TvTabHost           mTabHost = null;
	private LoginInfo                 mLoginInfo = null;
	private OnPhone2TvTabHostListener mTabListener = new OnPhone2TvTabHostListener()
	{

		@Override
		public void OnInitialTabHost(String tabTag, Intent intent) 
		{
			// TODO Auto-generated method stub
			Bundle bundle = new Bundle();
			bundle = Phone2TvComm.putLoginInfo(bundle , mLoginInfo);
			intent.putExtras(bundle);
		}

		@Override
		public boolean OnTabChanged(String tabTag) 
		{
			// TODO Auto-generated method stub
			return true;
		}
	};
		
	public boolean dispatchKeyEvent(KeyEvent event) 
	{
		if (event.getKeyCode()== KeyEvent.KEYCODE_BACK && 
			event.getRepeatCount() == 0 &&
		    event.getAction() == KeyEvent.ACTION_DOWN)
		{
			ExitDialog(MainTabActivity.this).show();  
		    return false;
		}
		return super.dispatchKeyEvent(event);
	}
		
		protected void onCreate(Bundle savedInstanceState) 
		{
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE); 
	        setContentView(R.layout.tab_view);
	        Intent intent = getIntent();
	        
	        mLoginInfo = Phone2TvComm.getLoginInfoFromIntent(intent);
	        mTabHost = new Phone2TvTabHost(this , 
	        		 						getTabHost() ,
	        								R.layout.tab_widget_item_view ,
	        								R.id.image_view ,
	        								Phone2TvConst.mMainTabNormalImage ,
	        								Phone2TvConst.mMainTabHighImage ,
	        								Phone2TvConst.mMainTabSepc ,
	        								Phone2TvConst.mPosition);
	        mTabHost.setPhone2TvTabHostListener(mTabListener);
	        mTabHost.initialTabHost(3);      
	 }
		
     private Dialog ExitDialog(Context context) 
     {  
    	 AlertDialog.Builder builder = new AlertDialog.Builder(context);  
	     builder.setMessage("确定要退出程序吗?");
	     builder.setPositiveButton("退出",  
			            new DialogInterface.OnClickListener() 
	                        {  
	    	                     public void onClick(DialogInterface dialog, int whichButton) 
	    	                     {           
	    	                    	finish();
	    	                     }
			                });  
	     builder.setNegativeButton("取消",  
			            new DialogInterface.OnClickListener() {  
			                public void onClick(DialogInterface dialog, int whichButton) {  
			                }  
			            });  
		return builder.create();
     }

}
