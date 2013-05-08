package com.example.phone2tv;

import java.util.ArrayList;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
public class TvChannel implements Parcelable 
{
	public int                mTvIndex;
	public String             mTvName;
	public ArrayList<Program> mPrograms;
	
	public int getTvIndex()
	{
		return mTvIndex;
	}
	
	public void setTvIndex(int index)
	{
		mTvIndex = index;
	}
	
	public ArrayList<Program> getPrograms()
	{
		return mPrograms;
	}
	
	public void setPrograms(ArrayList<Program> programs)
	{
		mPrograms = programs;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		// TODO Auto-generated method stub
		dest.writeInt(mTvIndex);
		dest.writeString(mTvName);
		dest.writeList(mPrograms);
	}
	
	public static final Parcelable.Creator<TvChannel> CREATOR = new Parcelable.Creator<TvChannel>() 
	{   
		public TvChannel createFromParcel(Parcel source) 
		{
			TvChannel p = new TvChannel();
			p.mTvIndex = source.readInt();
			p.mTvName  = source.readString();
			p.mPrograms = source.readArrayList(ArrayList.class.getClassLoader());
			return p;
		} 
		
		public TvChannel[] newArray(int size) 
		{
			// TODO Auto-generated method stub   
			return new TvChannel[size];
		}
	};

}
