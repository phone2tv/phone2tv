package com.example.phone2tv;

import android.os.Parcel;
import android.os.Parcelable;

public class LoginInfo implements Parcelable 
{
	private String mUserId;
	private String mAuthToken;
	private String mServerAddress;
	private String mServerPort;

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeString(mUserId);
		arg0.writeString(mAuthToken);
		arg0.writeString(mServerAddress);
		arg0.writeString(mServerPort);
	}
	
	public String getUserId()
	{
		return mUserId;
	}
	
	public void setUserId(String userId)
	{
		mUserId = userId;
	}
	
	public String getAuthToken()
	{
		return mAuthToken;
	}
	
	public void setAuthToken(String authToken)
	{
		mAuthToken = authToken;
	}
	
	public String getServerAddress()
	{
		return mServerAddress;
	}
	
	public void setServerAddress(String address)
	{
		mServerAddress = address;
	}
	
	public String getServerPort()
	{
		return mServerPort;
	}
	
	public void setServerPort(String port)
	{
		mServerPort = port;
	}
	
	public static final Parcelable.Creator<LoginInfo> CREATOR = new Parcelable.Creator<LoginInfo>() 
	{   
		public LoginInfo createFromParcel(Parcel source) 
		{   
			LoginInfo p      = new LoginInfo();   
			p.mUserId        = source.readString();
		    p.mAuthToken     = source.readString();
		    p.mServerAddress = source.readString();
		    p.mServerPort    = source.readString();
		    return p;
		 } 
		public LoginInfo[] newArray(int size) 
		{   
		            // TODO Auto-generated method stub   
		            return new LoginInfo[size]; 
		}
	};
}
