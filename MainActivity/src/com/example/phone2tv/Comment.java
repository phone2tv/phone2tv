package com.example.phone2tv;

import java.util.ArrayList;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable 
{
	public Date mPostTime;
	public int mProgramId;
	public int mOwnerId;
	public String mContent;
	public int mSrcId;
	public int mId;
	public String mOwnerName;
	public ArrayList<Integer> mDisLike;
	public ArrayList<Integer> mLike;
	public ArrayList<Integer> mReply;
	public int mLikeCount;
	public Comment()
	{
		mDisLike = new ArrayList<Integer>();
		mLike    = new ArrayList<Integer>();
		mReply   = new ArrayList<Integer>();
	}
	
	public void setPostTime(Date postTime)
	{
		mPostTime = postTime;
	}
	
	public Date getPostTime()
	{
		return mPostTime;
	}
	
	public void setProgramId(int programId)
	{
		mProgramId = programId;
	}
	
	public int getProgramId()
	{
		return mProgramId;
	}
	
	public void setSrcId(int srcId)
	{
		mSrcId = srcId;
	}
	
	public int getSrcId()
	{
		return mSrcId;
	}
	
	
	public void setOwnerId(int ownerId)
	{
		mOwnerId = mOwnerId;
	}
	
	public int getOwnerId()
	{
		return mOwnerId;
	}
	
	public void setContent(String content)
	{
		mContent = content;
	}
	
	public String getContent()
	{
		return mContent;
	}
	
	public void setDisLike(ArrayList<Integer> disLikeComment)
	{
		mDisLike = disLikeComment;
	}
	
	public ArrayList<Integer> getDisLike()
	{
		return mDisLike;
	}
	
	public void setLike(ArrayList<Integer> likeComment)
	{
		mLike = likeComment;
	}
	
	public ArrayList<Integer> getLike()
	{
		return mLike;
	}
	
	public ArrayList<Integer> getReply()
	{
		return mReply;
	}
	
	public void setReply(ArrayList<Integer> reply)
	{
		mReply = reply;
	}
	
	public int getDislikeCount()
	{
		return mDisLike.size();
	}
	
	public int getLikeCount()
	{
		return mLikeCount;
	}
	
	public int getReplyCount()
	{
		return mReply.size();
	}
	
	public int getId()
	{
		return mId;
	}
	
	public void setId(int id)
	{
		mId = id;
	}
	
	public String getOwnerName()
	{
		return mOwnerName;
	}
	
	public void setOwnerName(String name)
	{
		mOwnerName = name;
	}
	
	public void setLikeCount(int count)
	{
		mLikeCount = count;
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
		dest.writeSerializable(mPostTime);
		dest.writeInt(mProgramId);
		dest.writeInt(mOwnerId);
		dest.writeString(mContent);
		dest.writeInt(mSrcId);
		dest.writeInt(mId);
		dest.writeString(mOwnerName);
		dest.writeList(mLike);
		dest.writeList(mLike);
		dest.writeList(mReply);
		dest.writeInt(mLikeCount);

	}
	
	public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() 
	{   
		public Comment createFromParcel(Parcel source) 
		{   
			Comment p = new Comment();
			p.mPostTime  = (Date)source.readSerializable();
			p.mProgramId = source.readInt();
			p.mOwnerId   = source.readInt();
			p.mContent   = source.readString();
			p.mSrcId     = source.readInt();
			p.mId        = source.readInt();
			p.mOwnerName = source.readString();
			p.mDisLike   = source.readArrayList(ArrayList.class.getClassLoader());
			p.mLike      = source.readArrayList(ArrayList.class.getClassLoader());
			p.mReply     = source.readArrayList(ArrayList.class.getClassLoader());
			p.mLikeCount = source.readInt();
		    return p;
		 } 
		public Comment[] newArray(int size) 
		{
			// TODO Auto-generated method stub   
			return new Comment[size];
		}
	};
}
