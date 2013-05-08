package com.example.phone2tv;

import java.io.IOException;
import java.util.List;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

public class Phone2TvCamera implements SurfaceHolder.Callback
{
	  private SurfaceHolder holder = null;  
	  private Camera mCamera = null;  
	  private int width,height;  
	  private Camera.PreviewCallback previewCallback;  
	  
	  public Phone2TvCamera(SurfaceHolder holder,int w,int h,Camera.PreviewCallback previewCallback) {  
	        this.holder = holder;    
	        this.holder.addCallback(this);    
	        this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
	        width=w;  
	        height=h;  
	        this.previewCallback=previewCallback; 
	    }  
	  
	  @Override  
	  public void surfaceCreated(SurfaceHolder arg0) 
	  {  
			  mCamera = Camera.open();//启动服务    
	        try {    
	            mCamera.setPreviewDisplay(holder);//设置预览   
	            mCamera.setDisplayOrientation(90);
	            
	        } catch (IOException e) {    
	            mCamera.release();//释放    
	            mCamera = null;    
	        }  
	          
	    }  
	  
	    @Override  
	    public void surfaceDestroyed(SurfaceHolder arg0) {  
	    	Log.e("Camera","surfaceDestroyed");  
	        mCamera.setPreviewCallback(null); 
	        mCamera.stopPreview();//停止预览 
	        mCamera.release();
	        this.holder.removeCallback(this);
	        mCamera = null;
	    } 
	    
	    public void AutoFocusAndPreviewCallback()  
	    {  
	        if(mCamera!=null)  
	        {
	        	Log.e("Camera" , "AutoFocusAndPreviewCallback");
	            mCamera.autoFocus(mAutoFocusCallBack);  
	        }
	    }  
	    
	    private Camera.AutoFocusCallback mAutoFocusCallBack = new Camera.AutoFocusCallback() {    
            
	        @Override    
	        public void onAutoFocus(boolean success, Camera camera) {        
	          if (success) {  //对焦成功，回调Camera.PreviewCallback  
	              mCamera.setOneShotPreviewCallback(previewCallback);   
	        	  Log.e("Camera" , "auto focus");
	            }    
	        }    
	    };

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2 , int arg3) 
		{
			 Log.e("Camera","surfaceChanged"); 
			// TODO Auto-generated method stub
			Camera.Parameters parameters = mCamera.getParameters();    
			
	        List<Camera.Size> temp = parameters.getSupportedPictureSizes();
	        printSize(temp);
	        parameters.setPreviewSize(width , height);
	        parameters.setPictureFormat(PixelFormat.JPEG);  
	        mCamera.setParameters(parameters);    
	        mCamera.startPreview();//开始预览  
	        Log.e("Camera","surfaceChanged end"); 
	        AutoFocusAndPreviewCallback();
		}  
		
		public void printSize(List<Camera.Size> sizes)
		{
			for(int i = 0 ; i< sizes.size() ; i++)
			{
				Camera.Size one = sizes.get(i);
				Log.d("Size" , "weight :" + one.width + "height :" + one.height);
			}
		}
}
