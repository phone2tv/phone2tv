package com.example.phone2tv;

public class User 
{
	private String mName =  null;
	private int    mCheckinCount = 0;
	private int    mFollowersCount = 0;
	private int    mFolloweesCount = 0;
	private int    mDiscussCount   = 0;
	private Program mLastCheckin = null;
	
	public String getUserName()
	{
		return mName;
	}
	
	public void setUserName(String name)
	{
		mName = name;
	}
	
	public int getCheckinCount()
	{
		return mCheckinCount;
	}
	
	public void setCheckinCount(int count)
	{
		mCheckinCount = count;
	}
	
	public Program getCheckinProgram()
	{
		return mLastCheckin;
	}
	
	public void setCheckinProgram(Program program)
	{
		mLastCheckin = program;
	}
	
	public void setFollowersCount(int count)
	{
		mFollowersCount = count;
	}
	
	public int getFollowersCount()
	{
		return mFollowersCount;
	}
	
	public void setFolloweesCount(int count)
	{
		mFolloweesCount = count;
	}
	
	public int getFolloweesCount()
	{
		return mFolloweesCount;
	}
	
	public void setDiscussCount(int count)
	{
		mDiscussCount = count;
	}
	
	public int getDiscussCount()
	{
		return mDiscussCount;
	}
}
