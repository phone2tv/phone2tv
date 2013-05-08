package com.example.phone2tv;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class NbaActivity extends Activity implements OnClickListener
{
	 protected ImageView mScanBt = null;
	 protected ImageView mLotterySinaBt = null;
	 protected ImageView mLotteryTencentBt = null;
	 protected ImageView mLottery360BuyBt = null;
	 protected void onCreate(Bundle savedInstanceState)
	 {
		 super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE); 
	     setContentView(R.layout.nba_view);
	     mScanBt =(ImageView) findViewById(R.id.image_scan_bt);
	     mScanBt.setOnClickListener(this);
	     
	     mLotterySinaBt = (ImageView)findViewById(R.id.image_lottery_sina);
	     mLotterySinaBt.setOnClickListener(this);
	     
	     mLotteryTencentBt = (ImageView)findViewById(R.id.image_lottery_tencent);
	     mLotteryTencentBt.setOnClickListener(this);
	     
	     mLottery360BuyBt = (ImageView)findViewById(R.id.image_lottery_360buy);
	     mLottery360BuyBt.setOnClickListener(this);
	 }
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		if(v.getId() == mScanBt.getId())
		{
			scanTv();
		}
		else if(v.getId() == mLotterySinaBt.getId())
		{
			openUrl("http://loto.sina.cn/index");
		}
		else if(v.getId() == mLotteryTencentBt.getId())
		{
			openUrl("http://888.3g.qq.com");
		}
		
	}
	
	protected void scanTv()
	{
		 Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		 intent.putExtra("SCAN_MODE", "QR_CODE_MODE");//for Qr code, its "QR_CODE_MODE" instead of "PRODUCT_MODE"
		 intent.putExtra("SAVE_HISTORY", false);//this stops saving ur barcode in barcode scanner app's history
		 startActivityForResult(intent, 0);
	}
	
	 protected void onActivityResult(int requestCode, int resultCode, Intent data)
	 {
		 super.onActivityResult(requestCode, resultCode, data);
	        
	        String contents = null;
	        if(resultCode == RESULT_OK)
	        {
	        	contents = data.getStringExtra("SCAN_RESULT");
	        	Dialog operDialog = new AlertDialog.Builder(NbaActivity.this)
				 .setTitle(contents)
				 .setPositiveButton("打开", new DialogInterface.OnClickListener()
				 {
					 
				    @Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
				    	String url = (String)getTitle();
				    	openUrl(url);
					}
				 })
				 .setNegativeButton("取消", null)
				 .show();
	        }
	        else
	        {
	        	
	        }
	 }
	 
	 protected void openUrl(String content)
	 {
		 Intent intent = new Intent();        
		 intent.setAction("android.intent.action.VIEW");
		 Uri content_url = Uri.parse(content);
		 intent.setData(content_url);
		 intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");   
		 startActivity(intent);
	 }
}
