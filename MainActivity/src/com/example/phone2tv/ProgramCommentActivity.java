package com.example.phone2tv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.System;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Button;
import android.widget.AbsListView.OnScrollListener;
import android.widget.RadioGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
public class ProgramCommentActivity extends Activity implements OnClickListener , OnTouchListener
{
	 private final static String TAG="ProgramCommentActivity";
	 private ImageView mSubscribeBt;
	 private ImageView mActivityBt;
	 private ImageView mExpandBt;
	 private ImageView mCommentBt;
	 private ImageView mCheckinBt;
	 private ImageView mIntroduceBackground;
	 private ImageView mBanner;
	 private TextView  mTitle;
	 private TextView  mTextProgramDetail;
	 private ListView mCommentList;
	 private int     mScrollState;
	 View            mProgressView;
	 View            mLoadingTextView;
	 View            mLoadingView;
	 private boolean mHidden;
	 private boolean mSubscribe;
	 private ImageView mSendBt;
	 private int      mReplySrcId = -1;
	 private EditText mInputWidget;
	 ArrayList<HashMap<String, Object>> mCommentItem; 
	 CommentAdapter listItemAdapter;
	 NetworkAdapter mHttpSession;
	 String         mProgramName;
	 int            mProgramId;
	 Program        mProgramDetail;
	 LoginInfo      mLoginInfo = null;
	 class ItemSubHoldView
	 {
		 public ImageView mCommentExpandBt;
		 public ImageView mCommentFolderBt;
		 public ImageView mCommentReplyBt;
		 public ImageView mCommentLikeBt;
		 public ImageView mCommentShareBt;
		 public TextView  mUserName;
		 public TextView  mCommentTime;
		 public TextView  mReplyNum;
		 public TextView  mLoveNum;
		 public TextView  mCommentContent;
		 public LinearLayout mLinearLayoutCanvas;
		 public int          mPosition;
		 public int          mId;
		 public boolean      mIsExpand;
	 };
	 
	 public class ItemClickListener implements OnClickListener , OnTouchListener
	 {
		 private ItemSubHoldView mViewHold;
		 public ItemClickListener(ItemSubHoldView hold)
		 {
			 mViewHold = hold;
		 }
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				ItemSubHoldView sub = mViewHold;
				if(v.getId() == sub.mCommentExpandBt.getId())
				{
					sub.mCommentFolderBt.setVisibility(View.VISIBLE);
					sub.mCommentReplyBt.setVisibility(View.VISIBLE);
					sub.mCommentShareBt.setVisibility(View.VISIBLE);
					sub.mCommentLikeBt.setVisibility(View.VISIBLE);
					sub.mCommentExpandBt.setVisibility(View.GONE);
					mCommentItem.get(sub.mPosition).put("expand" , 1);
				}
				else if(v.getId() == sub.mCommentFolderBt.getId())
				{
					sub.mCommentFolderBt.setVisibility(View.GONE);
					sub.mCommentReplyBt.setVisibility(View.GONE);
					sub.mCommentShareBt.setVisibility(View.GONE);
					sub.mCommentLikeBt.setVisibility(View.GONE);
					sub.mCommentExpandBt.setVisibility(View.VISIBLE);
					mCommentItem.get(sub.mPosition).put("expand" , 0);
				}
				else if(v.getId() == sub.mCommentReplyBt.getId())
				{
					setSoftInputVisible(true);
					mCommentList.setSelection(sub.mPosition);
					mReplySrcId = sub.mId;
					//Log.d(TAG , "click comment id:" + sub.mId + "mReplySrcId id :" + mReplySrcId);
				}
				else if(v.getId() == sub.mCommentLikeBt.getId())
				{
					asyncPostShortComment(sub.mId , 0);
				}
			}
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				ImageView view = (ImageView)v;
				switch(event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_UP:
					break;
				}
				return false;
			}
		}		 
	 
	 public class CommentAdapter extends SimpleAdapter  
	 {
		 private int mResource;
		 private LayoutInflater mInflater;
		 private Context mContext;

		 private ArrayList<HashMap<String , Object>> mItemData;
		 public CommentAdapter(Context context, List<? extends Map<String, ?>> data, 
				 int resource, 
				 String[] from, 
				 int[] to)
		 
		 {
			 super(context , data , resource , from ,to);
			 mContext = context;
			 mItemData =(ArrayList<HashMap<String , Object>>)data;
			 mResource = resource;
			 mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		 }
		 
		 public void bindView(int position , View convertView)
		 {
			 ItemSubHoldView subWidget = null;
			 subWidget = new ItemSubHoldView();
			 subWidget.mCommentExpandBt = (ImageView)convertView.findViewById(R.id.image_comment_expand);
			 subWidget.mCommentFolderBt = (ImageView)convertView.findViewById(R.id.image_comment_folder);
			 subWidget.mCommentReplyBt  = (ImageView)convertView.findViewById(R.id.image_comment_reply);
			 subWidget.mCommentShareBt  = (ImageView)convertView.findViewById(R.id.image_comment_share);
			 subWidget.mCommentLikeBt   = (ImageView)convertView.findViewById(R.id.image_comment_like);
			 subWidget.mCommentTime     = (TextView)convertView.findViewById(R.id.string_comment_time);
			 subWidget.mReplyNum        = (TextView)convertView.findViewById(R.id.string_comment_reply_num);
			 subWidget.mLoveNum         = (TextView)convertView.findViewById(R.id.string_comment_love_num);
			 subWidget.mCommentContent  = (TextView)convertView.findViewById(R.id.string_comment_content);
			 subWidget.mUserName        = (TextView)convertView.findViewById(R.id.string_user_name);
			 subWidget.mLinearLayoutCanvas = (LinearLayout)convertView.findViewById(R.id.linearlayout_reply_canvas);
			 subWidget.mPosition           = position;
			 subWidget.mIsExpand           = false;
			 ItemClickListener itemClickListener = new ItemClickListener(subWidget);
			 subWidget.mCommentExpandBt.setOnClickListener(itemClickListener);
			 subWidget.mCommentExpandBt.setOnTouchListener(itemClickListener);
			 subWidget.mCommentFolderBt.setOnClickListener(itemClickListener);
			 subWidget.mCommentReplyBt.setOnClickListener(itemClickListener);
			 subWidget.mCommentShareBt.setOnClickListener(itemClickListener);
			 subWidget.mCommentLikeBt.setOnClickListener(itemClickListener);
			 
			 convertView.setTag(subWidget);
		 }
		 
		 public void fillViewFromData(int position , View convertView )
		 {
			 HashMap<String , Object> map = mItemData.get(position);
			 ItemSubHoldView sub = (ItemSubHoldView)convertView.getTag();
			
			 String content = (String)map.get("comment_content");
			 
			 Integer replyNum = (Integer)map.get("comment_reply_num");
			 String replyNumStr = String.valueOf(replyNum);
			 
			 String postTime = (String)map.get("comment_time");
			 
			 String userName = (String)map.get("comment_owner_name");
			 
			 Integer loveNum = (Integer)map.get("comment_love_num");
			 String loveNumStr = String.valueOf(loveNum);
			 
			 sub.mPosition = position;
			 sub.mCommentContent.setText(content.toCharArray() , 0 , content.length());
			 sub.mReplyNum.setText(replyNumStr.toCharArray() ,  0 , replyNumStr.length());
			 sub.mCommentTime.setText(postTime.toCharArray() , 0 , postTime.length());
			 sub.mUserName.setText(userName.toCharArray() , 0 , userName.length());
			 sub.mId = (Integer)map.get("discuss_id");
			 sub.mLoveNum.setText(loveNumStr.toCharArray() , 0 , loveNumStr.length());
			 Object replys = map.get("replys");
			 if(replys != null)
			 { 
				 fillReplyViewFromData((ArrayList<HashMap<String , Object>>)replys , sub);
			 }
			 else
				 sub.mLinearLayoutCanvas.setVisibility(View.GONE);
			 
			 if((Integer)map.get("expand") == 1)
			 {
				 sub.mCommentFolderBt.setVisibility(View.VISIBLE);
				 sub.mCommentReplyBt.setVisibility(View.VISIBLE);
				 sub.mCommentShareBt.setVisibility(View.VISIBLE);
				 sub.mCommentLikeBt.setVisibility(View.VISIBLE);
				 sub.mCommentExpandBt.setVisibility(View.GONE); 
			 }
			 else
			 {
				 sub.mCommentFolderBt.setVisibility(View.GONE);
				 sub.mCommentReplyBt.setVisibility(View.GONE);
				 sub.mCommentShareBt.setVisibility(View.GONE);
				 sub.mCommentLikeBt.setVisibility(View.GONE);
				 sub.mCommentExpandBt.setVisibility(View.VISIBLE);
			 }
		 }
		 
		 public void fillReplyViewFromData(ArrayList<HashMap<String , Object>> replys , 
				                           ItemSubHoldView holder)
		 {
			// int childCount = holder.mLinearLayoutCanvas.getChildCount();
			 holder.mLinearLayoutCanvas.setVisibility(View.VISIBLE);
			 holder.mLinearLayoutCanvas.removeAllViews();
			 
			 int replyCount = replys.size();
			 for(int i = 0 ; i < replyCount ; i++)
			 {
				 HashMap<String , Object> reply = replys.get(i);
				 holder.mLinearLayoutCanvas.addView(composeReplyView(reply , holder));
			 }
			
		 }
		 
		 public View composeReplyView(HashMap<String , Object> reply , 
				                      ItemSubHoldView holder)
		 {
			 
			 RelativeLayout layout =(RelativeLayout)mInflater.inflate(R.layout.item_reply_comment, null);
			 TextView content = (TextView)layout.findViewById(R.id.string_reply_content);
			 String str =(String)reply.get("comment_owner_name");
			 str+=":";
			 str+= reply.get("comment_content");
			 content.setText(str.toCharArray() , 0 , str.length());
			 
			 TextView postTime = (TextView)layout.findViewById(R.id.string_reply_time);
			 String time = (String)reply.get("comment_time");
			 postTime.setText(time.toCharArray(), 0, time.length());
			 
			 return layout;
		 }
		 
		 //@Overriden
		 public View getView(int position, View convertView, ViewGroup parent) 
		 {
			 View view;
			 Log.d(TAG , "getView: "+position);
			 if(null == convertView)
			 {
				 Log.d(TAG , "getView new: "+position);
				 convertView = mInflater.inflate(mResource, null);
				 bindView(position , convertView);
			 }
			 
			 fillViewFromData(position , convertView);
			 
			 return convertView;
		 }
		 
		@Override
		public int getCount()
		{
			int nSize = mItemData.size();
			//Log.d(TAG , "getCount:" +nSize);
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
	 
	 protected void onCreate(Bundle savedInstanceState)
	 {
		 Log.e(TAG , "onCreate");
		 super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE); 
		 setContentView(R.layout.program__view);
		 Intent intent = getIntent();
		 mProgramName = intent.getStringExtra("program_name");
		 mProgramId   = intent.getIntExtra("program_id", -1);
		 mHidden = false;
		 mLoginInfo     = Phone2TvComm.getLoginInfoFromIntent(intent);
		 initControl();
		 mTitle.setText(mProgramName.toCharArray(), 0 , mProgramName.length());
		 mHttpSession = new NetworkAdapter(mLoginInfo.getServerAddress() , mLoginInfo.getServerPort());
		 asycLoadProgramDetail();
	 }
	 
	 protected void initControl()
	 {
		 mSubscribeBt = (ImageView)findViewById(R.id.image_sub_bt);
		 mActivityBt   = (ImageView)findViewById(R.id.image_active_bt);
		 mExpandBt    = (ImageView)findViewById(R.id.image_fold_bt);
		 mCommentBt   =(ImageView)findViewById(R.id.image_comment_bt);
		 mCheckinBt   = (ImageView)findViewById(R.id.image_checkin_bt);
		 mIntroduceBackground = (ImageView)findViewById(R.id.image_introduce_background);
		 mBanner =(ImageView)findViewById(R.id.image_program_cover);
		 mSendBt =(ImageView)findViewById(R.id.image_input_right);
		 mInputWidget = (EditText)findViewById(R.id.input_editText1);
		 mTitle       =(TextView)findViewById(R.id.text_program_name);
		 mTextProgramDetail =(TextView)findViewById(R.id.text_program_detail);
		 initList();
		 mCheckinBt.setOnClickListener(this);
		 mCheckinBt.setOnTouchListener(this);
		 
		 mCommentBt.setOnClickListener(this);
		 mCommentBt.setOnTouchListener(this);
		 
		 mSubscribeBt.setOnClickListener(this);
		 mSubscribeBt.setOnTouchListener(this);
		 
		 mActivityBt.setOnClickListener(this);
		 mActivityBt.setOnTouchListener(this);
		 
		 mExpandBt.setOnClickListener(this);
		 mExpandBt.setOnTouchListener(this);
		 
		 mSendBt.setOnClickListener(this);
		 
	 }
	 
	 protected void initList()
	 {
		 mCommentList = (ListView)findViewById(R.id.comment_list_view);
		 mCommentItem = new ArrayList<HashMap<String , Object>>();
		 listItemAdapter = new CommentAdapter(this , mCommentItem , 
				 R.layout.item_comment , 
				 new String[]{"comment_time" , 
				              "comment_reply_num" , 
				              "comment_love_num", 
				              "comment_content" ,
				              "comment_owner_name"} ,
				 new int[]{R.id.string_comment_time, 
				           R.id.string_comment_reply_num ,
				           R.id.string_comment_love_num,
				           R.id.string_comment_content,
				           R.id.string_user_name});
		 
		 
		 mLoadingView = (View)getLayoutInflater().inflate(R.layout.more_view, null);
		 mProgressView = mLoadingView.findViewById(R.id.progress);
		 mLoadingTextView = mLoadingView.findViewById(R.id.loading_text);
		 mCommentList.addHeaderView(mLoadingView);
		 setHeaderVisibility(false);
		 mCommentList.setOnScrollListener(new OnScrollListener()
			{
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
				{
	                 if (firstVisibleItem == 0 && mLoadingView.getBottom() >= 0) 
	                 {
	                	 if(mScrollState == SCROLL_STATE_TOUCH_SCROLL)
	                	 {   
	                		 setHeaderVisibility(true);
	                		 asycLoadProgramDetail();
	                	 }
	                 }
	            }
				
				public void onScrollStateChanged(AbsListView view,int scrollState) 
				{
	       		  mScrollState = scrollState;
	       		}
			});
		 mCommentList.setAdapter(listItemAdapter);
	 }
	 
	 protected void hideBanner(boolean hidden)
	 {
		 if(!hidden)
		 {
		 	mBanner.setVisibility(View.GONE);
		 	mTextProgramDetail.setVisibility(View.GONE);
		 	mIntroduceBackground.getLayoutParams().height = 91;
		 	
		 }
		 else
		 {
			 mBanner.setVisibility(View.VISIBLE);
			 mTextProgramDetail.setVisibility(View.VISIBLE);
			 mIntroduceBackground.getLayoutParams().height = 176;
		 }
	 }
	 
	 protected void subscribeProgram()
	 {
		 asyncSubscribeProgram();
	 }
	 
	 public void onClick(View v) 
	 {
		// TODO Auto-generated method stub
		 switch(v.getId())
		 {
			case R.id.image_sub_bt:
				Log.d(TAG , "click on subscribe button!\n");
				if(!mSubscribe)
				{
					mSubscribe = true;
					subscribeProgram();
				}
		   		break;
			case R.id.image_active_bt:
				joinActivity();
				break;
			case R.id.image_fold_bt:
				hideBanner(mHidden);
				mHidden = !mHidden;
				break;
			case R.id.image_input_right:
				sendMessage();
				break;
			}
	}
	 
	public void joinActivity()
	{
		 Intent intent = new Intent();
		 intent.setClass(this , NbaActivity.class);
		 startActivity(intent);
	}
	
	public void sendMessage()
	{
		asyncPostComment(mInputWidget.getText().toString() , mReplySrcId);
	}
	 
	 public Program buildCommentsTree(Program program)
	 {
		 Iterator iter = program.getComments().keySet().iterator();
		 while(iter.hasNext())
		 {
			 int key = (Integer)iter.next();
			 Comment oneComment = program.getComment(key);
			 if(oneComment.getSrcId() != -1)
				 program.getComment(oneComment.getSrcId()).mReply.add(key);
		 }
		 return program;
	 }
	 
	 private String DateToString(Date date)
	 {
		 SimpleDateFormat simple =  new SimpleDateFormat("MM-dd HH:mm" , Locale.CHINA);
		 return simple.format(date);
	 }
	 
	 public void buildCommentData(Program program)
	 {
		 HashMap<String , Object> pair = null;
		 Iterator iter = program.getComments().keySet().iterator();  
		 while(iter.hasNext())
		 {
			 int key =(Integer)iter.next();
			 Comment oneComment = program.getComment(key);
			 //oneComment is not reply
			 if(oneComment.getSrcId() == -1)
			 {
				 pair = new HashMap<String , Object>();
				 pair.put("comment_content",oneComment.getContent());
				 pair.put("comment_reply_num", oneComment.getReplyCount());
				 pair.put("discuss_id", key);
				 if(oneComment.getReplyCount() > 0 )
				 {
					 pair.put("replys" , buildReplyData(program , oneComment.getReply()));
				 }
				 else
					 pair.put("replys", null);
				 String postTime = Phone2TvComm.DateToString(oneComment.getPostTime(), Phone2TvComm.mFormat[1]);//DateToString(oneComment.getPostTime());
			     pair.put("comment_time", postTime);
			     String userName = oneComment.getOwnerName();
			     pair.put("comment_owner_name", userName);
			     pair.put("comment_love_num" , oneComment.getLikeCount());
			     pair.put("expand", 0);
			     mCommentItem.add(pair);
			 }
		 }
		 listItemAdapter.notifyDataSetChanged();
	 }
	 
	 ArrayList<HashMap<String , Object >> buildReplyData(Program program , ArrayList<Integer> replys)
	 {
		 ArrayList<HashMap<String  , Object>> replyList = new ArrayList<HashMap<String  , Object>>();
		 HashMap<String , Object> replyMap = null;
		 for(int i = 0 ; i < replys.size() ; i++)
		 {
			 Comment oneReply = program.getComment(replys.get(i));
			 replyMap = new HashMap<String , Object>();
			 
			 replyMap.put("comment_owner_name", oneReply.getOwnerName());
			 replyMap.put("comment_content" , oneReply.getContent());
			 String postTime = Phone2TvComm.DateToString(oneReply.getPostTime() , Phone2TvComm.mFormat[1]);
			 replyMap.put("comment_time", postTime);
			 replyMap.put("discuss_id", oneReply.getId());
			 replyList.add(replyMap);
		 }
		 
		 return replyList;
	 }
	 
	 Handler mHandler = new Handler()
	 {
		 public void handleMessage(Message msg)
		 {
			 switch(msg.what)
			 {
			 case 0:
				 
				 try
				 {
					 Program program = (Program)msg.obj;
					 onGetProgramDetailSucc(program);
					 
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }
				 setHeaderVisibility(false);
				 mCommentList.setSelection(0);
				 break;
			 case 1:
				 onPostCommentSucc();
				 break;
			 case 2:
				 break;
			 case 3:
				 onSubsrcibeProgramSucc();
				 break;
			 case 4:
				 onPostShortCommentSucc();
				 break;
			 }
		 }
	 };
	 
	 protected void setSoftInputVisible(boolean flag)
	 {
		 InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE); 
		 if(flag)
		 {
			 imm.showSoftInput(mInputWidget , InputMethodManager.SHOW_FORCED);
		 }
		 else
		 {
			 imm.hideSoftInputFromWindow(mInputWidget.getWindowToken(), 0);
		 }

	 }
	 
	 protected void onPostShortCommentSucc()
	 {
		 Phone2TvExceptionToast.showExceptionToast(this , "成功喜欢该评论");
	 }
	 
	 protected void onSubsrcibeProgramSucc()
	 {
		// mSubscribeBt.setImageResource(R.drawable.subscribed);
		 Phone2TvExceptionToast.showExceptionToast(this , "收藏成功");
	 }
	 
	 protected void onGetProgramDetailSucc(Program program)
	 {
		 setSoftInputVisible(false);
		 program = buildCommentsTree(program);
		 mProgramDetail = program;
		 mCommentItem.clear();
		 buildCommentData(program);
	 }
	 
	 protected void onPostCommentSucc()
	 {
		 setSoftInputVisible(false);
		 mInputWidget.setText("");
		 mReplySrcId = -1;
		 asycLoadProgramDetail();
	 }
	 
	 protected void asyncPostShortComment(int targetId , int operId)
	 {
		 final int commentId = targetId;
		 new Thread()
		 {
			 public void run()
			 {
				 try
				 {
					 mHttpSession.requestPostShortComment(commentId, 
							                              mLoginInfo.getUserId(), 
							                              mLoginInfo.getAuthToken(), 
							                              0);
					 Message msg = Message.obtain();
					 msg.what = 4;
					 mHandler.sendMessage(msg);
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
				 }
			 }
		 }.start();
	 }
	 
	 protected void asyncSubscribeProgram()
	 {
		 new  Thread()
		 {
			 public void run()
			 {
				 try
				 {
					 mHttpSession.requestPostSubscribeOper(mProgramId, 
							                               mLoginInfo.getAuthToken(), 
							                               mLoginInfo.getUserId(), 
							                               true);
					 Message message = new Message();
					 message.what = 3;
					 mHandler.sendMessage(message);
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
					 //mHandler.sendEmptyMessage(4);
				 }
			 }
		 }.start();
	 }
	 
	 protected void asyncPostComment(String content , int srcId)
	 {
		 new Thread()
		 {
			 public void run()
			 {
				 try
				 {
					 mHttpSession.requestPostComment(mProgramId, 
							                         mReplySrcId , 
							                         mLoginInfo.getAuthToken(), 
							                         mLoginInfo.getUserId(), 
							                         mInputWidget.getText().toString());
					 Message msg = new Message();
					 msg.what = 1;
					 mHandler.sendMessage(msg);
				 }
				 catch(Exception e)
				 {
					 e.printStackTrace();
					 Message msg = new Message();
					 msg.what = 2;
					 mHandler.sendMessage(msg);
				 }
			 }
		 }.start();
	 }
	 
	 void asycLoadProgramDetail() 
	 {
		new Thread() {
			public void run() {
				try {
					JSONObject programJson;
					programJson = mHttpSession.requestProgramById(mProgramId);
					Program program = mHttpSession.getProrgamById(programJson);
					Message msg = new Message();
					msg.what = 0;
					msg.obj = program;
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
					Message msg = new Message();
					msg.what = 2;
					mHandler.sendMessage(msg);
				}
			}
		}.start();
		 
	 }
	 
	 void setHeaderVisibility(boolean bShow)
	 {
		int flag = bShow?View.VISIBLE:View.GONE;
	     mProgressView.setVisibility(flag);
		 mLoadingTextView.setVisibility(flag);
		 mLoadingView.setVisibility(flag);
	 }

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		
		switch (arg1.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.e(TAG, "onTouch down");
			highLightImage(arg0);
			break;
		case MotionEvent.ACTION_UP:
			Log.e(TAG, "onTouch up");
			restoreImage(arg0);
			break;
		}
		return false;
	}
	
	private void highLightImage(View view)
	{
		ImageView imageView = (ImageView) view;
		switch (imageView.getId()) {
		case R.id.image_comment_bt:
			imageView.setImageResource(R.drawable.comment_button_b);
			break;
		case R.id.image_sub_bt:
			imageView.setImageResource(R.drawable.sub_button_b);
			break;
		case R.id.image_checkin_bt:
			imageView.setImageResource(R.drawable.checkin_button_b);
			break;
		case R.id.image_active_bt:
			imageView.setImageResource(R.drawable.active_button_b);
			break;
		}
	}
	
	private void restoreImage(View view)
	{
		ImageView imageView = (ImageView) view;
		switch (imageView.getId()) {
		case R.id.image_comment_bt:
			imageView.setImageResource(R.drawable.comment_button);
			break;
		case R.id.image_sub_bt:
			imageView.setImageResource(R.drawable.sub_button);
			break;
		case R.id.image_checkin_bt:
			imageView.setImageResource(R.drawable.checkin_button);
			break;
		case R.id.image_active_bt:
			imageView.setImageResource(R.drawable.active_button);
			break;
		}
	}
}
