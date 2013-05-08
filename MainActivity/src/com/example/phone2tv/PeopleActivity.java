package com.example.phone2tv;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class PeopleActivity extends Activity 
{
	 private static final String TAG = "PeopleActivity";
	 private ListView  mFriendsList = null;
	 private ArrayList<HashMap<String, Object>> mFriends = null;
	 private SimpleAdapter                              mFriendsAdapter = null; 
	 private LoginInfo mLoginInfo = null;
     NetworkAdapter mHttpSession = null;
     ArrayList<User>  mUsers = new ArrayList<User>();
     Handler        mHandler = new Handler()
     {
    	 public void handleMessage(Message msg)
    	 {
    		 switch(msg.what)
    		 {
    		 case 0:
    			 ArrayList<User> users = (ArrayList<User>)msg.obj;
    			 fillFriendsList(users);
    			 break;
    		 }
    	 }
     };
     
     protected void fillFriendsList(ArrayList<User> users)
     {
    	 mFriends.clear();
    	 mUsers.clear();
    	 HashMap<String , Object> oneItem;
    	 for(User one : users)
    	 {
    		 oneItem  = new HashMap<String , Object>();
    		 oneItem.put("user_name", one.getUserName());
    		 oneItem.put("follower_num" , one.getFollowersCount());
    		 oneItem.put("followee_num" , one.getFolloweesCount());
    		 oneItem.put("checkin_num"  , one.getCheckinCount());
    		 if(0 != one.getCheckinCount())
    			 oneItem.put("checkin_program_name" , one.getCheckinProgram().getProgramName());
    		 mFriends.add(oneItem);
    	 }
    	 mUsers = users;
    	 mFriendsAdapter.notifyDataSetChanged();
     }
	 
	 protected void onCreate(Bundle savedInstanceState)
	 {
		 super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE); 
	     setContentView(R.layout.people_view);
	     Intent intent = getIntent();
	     mLoginInfo = Phone2TvComm.getLoginInfoFromIntent(intent);
	     Log.e(TAG , "login :"+mLoginInfo.getServerAddress() + "auth :" + mLoginInfo.getAuthToken());
	     mHttpSession = new NetworkAdapter(mLoginInfo.getServerAddress() , 
	    		                           mLoginInfo.getServerPort());
	     initFriendList();
	     
	 }
	 
	 protected void initFriendList()
	 {
		 mFriendsList = (ListView)findViewById(R.id.list_friends);
		 mFriends = new ArrayList<HashMap<String, Object>>();
		 mFriendsAdapter =  new SimpleAdapter(this , 
				                              mFriends ,
				 							  R.layout.item_friend ,
				 							  new String[]{ "user_name" , "follower_num" , "followee_num" , 
				                                            "checkin_num" , "checkin_program_name"} ,
		                                      new int[]{R.id.text_user_name , R.id.text_follower_num , 
				                                        R.id.text_followee_num , R.id.text_checkin_num ,
				                                        R.id.text_cur_program_name}
		                                      );
		 mFriendsList.setAdapter(mFriendsAdapter);
		 mFriendsList.setOnItemLongClickListener(mListOnItemLongClick);
		 Log.e(TAG , "asyncGetFollers");
		 asyncGetFollers();
	 }
	 
	 void asyncGetFollers()
	 {
		 new Thread()
		 {
			 public void run()
			 {
				 try
				 {
					 JSONArray jsonArray = null;
					 jsonArray = mHttpSession.requestFollowers(mLoginInfo.getAuthToken() , 
							                                   mLoginInfo.getUserId());
					 ArrayList<User> users = null;
					 users = mHttpSession.getFollowers(jsonArray);
					 Message msg = Message.obtain();
					 msg.what = 0;
					 msg.obj = users;
					 mHandler.sendMessage(msg);
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }
				 
			 }
		 }.start();
	 }
	 
	 protected void testData()
	 {
		 HashMap<String, Object> map = new HashMap<String, Object>();
		 map.put("user_name", "bill_tang");
		 
		 mFriends.add(map);
		 
		 map = new HashMap<String, Object>();
		 map.put("user_name", "xiaoning");
		 
		 mFriends.add(map);
		 mFriendsAdapter.notifyDataSetChanged();
	 }
	 
	 String buildMenuName(int index , String name)
	 {
		 String menuName = "暂无签到节目";
		 User one = mUsers.get(index);
		 if(one.getCheckinCount() != 0)
		 {
			 menuName = name;
			 menuName += one.getCheckinProgram().getProgramName();
		 }
		 return menuName;
	 }
	 
	 AdapterView.OnItemLongClickListener mListOnItemLongClick  = new AdapterView.OnItemLongClickListener()
	 {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) 
		{
			Log.d(TAG , "onItemLongClick");
			String[] operArray = new String[] {"进入:" , "收藏" ,"签到"}; 
			operArray[0] = buildMenuName(arg2 , operArray[0]);
			Dialog operDialog = new AlertDialog.Builder(PeopleActivity.this)
			                        .setTitle("操作") 
				                    .setItems(operArray, new DialogInterface.OnClickListener() 
				                    {
				                    	@Override 
	                                    public void onClick(DialogInterface dialog, int which) 
				                    	{
				                    		
				                    	}
				                    }).show();
			return false;
		}
		 
	 };
}
