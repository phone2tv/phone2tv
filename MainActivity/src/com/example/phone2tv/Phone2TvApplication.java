package com.example.phone2tv;

import android.content.Intent;
import android.net.Uri;



import greendroid.app.GDApplication;

public class Phone2TvApplication extends GDApplication
{
    @Override
    public Class<?> getHomeActivityClass() {
        return Phone2TvApplication.class;
    }
    
    @Override
    public Intent getMainApplicationIntent() {
        return new Intent(Intent.ACTION_VIEW , null);
    }
}
