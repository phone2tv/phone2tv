package com.example.phone2tv;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Phone2TvExceptionToast 
{
	public static void showExceptionToast(Context context , String content)
	{
		Toast toast = Toast.makeText(context ,
									 content, Toast.LENGTH_SHORT);
		                             toast.setGravity(Gravity.CENTER, 0, 0);
		                     
		toast.show();
	}
}
