package com.example.phone2tv;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.os.Parcel;
import android.os.Parcelable;

public class Program implements Parcelable 
{
	public int     mId;
	public String  mName;
	public String  mTvName;
	public int     mTvId;
	public Date    mBeginTime;
	public Date    mEndTime;
	public int     mSubScribeCount;
	public int     mDiscussCount;
	public int     mCheckinCount;
	public HashMap<Integer , Comment> mComments;
	public Program()
	{
		mComments = new HashMap<Integer , Comment>();
	}
	public int getIndex()
	{
		return mId;
	}
	
	public void setIndex(int id)
	{
		mId = id;
	}
	
	public String getProgramName()
	{
		return mName;
	}
	
	public void setProgramName(String name)
	{
		mName = name;
	}
	
	public String getTvName()
	{
		return mTvName;
	}
	
	public void setTvName(String name)
	{
		mTvName = name;
	}
	
	public Date getBeginTime()
	{
		return mBeginTime;
	}
	
	public void setBeginTime(Date beginTime)
	{
		mBeginTime = beginTime;
	}
	
	public Date getEndTime()
	{
		return mEndTime;
	}
	
	public void setEndTime(Date endTime)
	{
		mEndTime = endTime;
	}
	
	public int getSubscribeCount()
	{
		return mSubScribeCount;
	}
	
	public void setSubscribeCount(int subscribeCount)
	{
		mSubScribeCount = subscribeCount;
	}
	
	public int getDisscusCount()
	{
		return mDiscussCount;
	}
	
	public void setDiscussCount(int discussCount)
	{
		mDiscussCount = discussCount;
	}
	
	public HashMap<Integer , Comment> getComments()
	{
		return mComments;
	}
	
	public void setComments(HashMap<Integer , Comment> comments)
	{
		mComments = comments;
	}
	
	public void addComment(Comment comment)
	{
		mComments.put(comment.mId, comment);
	}
	
	public int getCommentCount()
	{
		return mComments.size();
	}
	public Comment getComment(int index)
	{
		return mComments.get(index);
	}
	
	public int getTvId()
	{
		return mTvId;
	}
	
	public void setTvId(int tvId)
	{
		mTvId = tvId;
	}
	
	public int getCheckinCount()
	{
		return mCheckinCount;
	}
	
	public void setCheckinCount(int count)
	{
		mCheckinCount = count;
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
		dest.writeInt(mId);
		dest.writeString(mName);
		dest.writeString(mTvName);
		dest.writeInt(mTvId);
		dest.writeSerializable(mBeginTime);
		dest.writeSerializable(mEndTime);
		dest.writeInt(mSubScribeCount);
		dest.writeInt(mDiscussCount);
		dest.writeInt(mCheckinCount);		
	}
	
	public static final Parcelable.Creator<Program> CREATOR = new Parcelable.Creator<Program>()
	{
	    public Program createFromParcel(Parcel source) 
		{
	    	Program p  = new Program();
	    	p.mId      = source.readInt();
	    	p.mName    = source.readString();
	    	p.mTvName  = source.readString();
	    	p.mTvId    = source.readInt();
	    	p.mBeginTime = (Date)source.readSerializable();
	    	p.mEndTime = (Date)source.readSerializable();
	    	p.mSubScribeCount = source.readInt();
	    	p.mDiscussCount   = source.readInt();
	    	p.mCheckinCount   = source.readInt();
			return p;   
		} 
		public Program[] newArray(int size) 
		{
			// TODO Auto-generated method stub   
			return new Program[size];
		}
	};
	

};