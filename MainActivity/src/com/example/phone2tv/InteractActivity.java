package com.example.phone2tv;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;

public class InteractActivity extends Activity
{
	public WebView webView;
	
	public void onCreate(Bundle savedInstanceState) 
	{ 
	        super.onCreate(savedInstanceState); 
	        //实例化WebView对象 
	        webView = new WebView(this); 
	        //设置WebView属性，能够执行Javascript脚本 
	        webView.getSettings().setJavaScriptEnabled(true); 
	        //加载需要显示的网页 
	        webView.loadUrl("http://jquerymobile.com/demos/1.1.0/docs/pages/page-links.html"); 
	        //设置Web视图 
	        setContentView(webView); 
	 } 
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{ 
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) 
        { 
            webView.goBack(); //goBack()表示返回WebView的上一页面 
            return true; 
        }
        else
        	finish();
        
        return false;
    }
	 
	 
}
