package com.example.phone2tv;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.phone2tv.Program;
public class TvStationProgram implements Parcelable 
{
	public int                mTvIndex;
	public String             mTvName;
	public ArrayList<Program> mPrograms;
	public TvStationProgram()
	{
		mTvIndex = -1;
		mTvName = null;
		mPrograms = new ArrayList<Program>();
	}
	
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
	
	public boolean addProgram(Program program)
	{
		mPrograms.add(program);
		return true;
	}
	
	public void setPrograms(ArrayList<Program> programs)
	{
		mPrograms = programs;
	}
	
	public String getTvName()
	{
		return mTvName;
	}
	
	public void setTvName(String name)
	{
		mTvName = name;
	}
	
	@Override
	public int describeContents() 
	{
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
	
	public static final Parcelable.Creator<TvStationProgram> CREATOR = new Parcelable.Creator<TvStationProgram>() 
	{   
		public TvStationProgram createFromParcel(Parcel source) 
		{
			TvStationProgram p = new TvStationProgram();
			p.mTvIndex = source.readInt();
			p.mTvName  = source.readString();
			p.mPrograms = source.readArrayList(Program.class.getClassLoader());
			return p;
		} 
		
		public TvStationProgram[] newArray(int size) 
		{
			// TODO Auto-generated method stub   
			return new TvStationProgram[size];
		}
	};
}
