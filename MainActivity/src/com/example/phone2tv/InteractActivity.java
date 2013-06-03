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
	        //ʵ����WebView���� 
	        webView = new WebView(this); 
	        //����WebView���ԣ��ܹ�ִ��Javascript�ű� 
	        webView.getSettings().setJavaScriptEnabled(true); 
	        //������Ҫ��ʾ����ҳ 
	        webView.loadUrl("http://jquerymobile.com/demos/1.1.0/docs/pages/page-links.html"); 
	        //����Web��ͼ 
	        setContentView(webView); 
	 } 
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{ 
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) 
        { 
            webView.goBack(); //goBack()��ʾ����WebView����һҳ�� 
            return true; 
        }
        else
        	finish();
        
        return false;
    }
	 
	 
}
