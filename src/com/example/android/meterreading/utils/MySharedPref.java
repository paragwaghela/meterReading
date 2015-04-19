package com.example.android.meterreading.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MySharedPref {
	
	private SharedPreferences mSharedPreferences;
	private Editor mEditor;
	
	private static final String USER_ID = "USER_ID";	
	private static final String DEVICE_ID="DEVICE_ID";
	private static final String USERNAME="USERNAME";
	private static final String PASSWORD="PASSWORD";
	
	public MySharedPref(final Context ctx) {
		mSharedPreferences = ctx.getSharedPreferences("Automotive",
				Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
	}
	
	public long getUserId()
	{
		return mSharedPreferences.getLong(USER_ID, 0);
	}
	
	public void setUserId(long val)
	{
		mEditor.putLong(USER_ID, val);
		mEditor.commit();
	}
	
	public String getDeviceId()
	{
		return mSharedPreferences.getString(DEVICE_ID, "");
	}
	
	public void setDeviceId(String val)
	{
		mEditor.putString(DEVICE_ID, val);
		mEditor.commit();
	}
	
	public String getUserName()
	{
		return mSharedPreferences.getString(USERNAME, "");
	}
	
	public void setUserName(String val)
	{
		mEditor.putString(USERNAME, val);
		mEditor.commit();
	}
	
	public String getPassword()
	{
		return mSharedPreferences.getString(PASSWORD, "");
	}
	
	public void setPassword(String val)
	{
		mEditor.putString(PASSWORD, val);
		mEditor.commit();
	}
}
