package com.example.phone2tv;

import java.util.Timer;
import java.util.TimerTask;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class ScanActivity extends Activity 
{
	 private static final String TAG = "ScanActivity"; 
	 private  boolean mBackfromScan = false;
	protected void onCreate(Bundle savedInstanceState)
	{
		 Log.d("ScanActivity" , "onCreate begin");
		 super.onCreate(savedInstanceState);
	     requestWindowFeature(Window.FEATURE_NO_TITLE); 
	     setContentView(R.layout.scan_view);
	}
	
	protected void onResume()
	{
		super.onResume();
		if(!mBackfromScan)
		{
			 Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			 intent.putExtra("SCAN_MODE", "QR_CODE_MODE");//for Qr code, its "QR_CODE_MODE" instead of "PRODUCT_MODE"
			 intent.putExtra("SAVE_HISTORY", false);//this stops saving ur barcode in barcode scanner app's history
			 startActivityForResult(intent, 0);
		}
		mBackfromScan = false;
	}
	
	protected void onPause()
	{
		super.onPause();
		Log.d(TAG , "onPause");
	}
	
	protected void onDestrory()
	{
		super.onDestroy();
		Log.d("ScanActivity" , "onDestrory");
	}
	
	 
	 
	 
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	 {
		 Log.e(TAG , "onActivityResult");
	        super.onActivityResult(requestCode, resultCode, data);
	        
	        String contents = null;
	        if(resultCode == RESULT_OK)
	        {
	        	contents = data.getStringExtra("SCAN_RESULT");
	        	Dialog operDialog = new AlertDialog.Builder(ScanActivity.this)
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
	        mBackfromScan = true;
	 }
	 
	 protected void openUrl(String content)
	 {
		 Intent intent = new Intent();        
		 intent.setAction("android.intent.action.VIEW");
		 Uri content_url = Uri.parse("http://www.cnblogs.com");
		 intent.setData(content_url);
		 intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");   
		 startActivity(intent);
	 }

}
