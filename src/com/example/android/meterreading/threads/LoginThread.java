package com.example.android.meterreading.threads;

import org.json.JSONObject;

import com.example.android.meterreading.utils.HttpCalls;
import com.example.android.meterreading.utils.SharedData;

import android.util.Log;

public class LoginThread extends Thread {
	
	private static String TAG="LoginThread";
	
	public interface LoginThreadInterface
	{
		public void onLoginThreadDataReturned(Boolean isSuccess,String msg);
		public void onLoginThreadErrorReturned();
	}
	private LoginThreadInterface mLoginThreadInterface;
	private String mEmail;
	private String mPassword;
	//private String mDeviceId;
	
	public LoginThread(LoginThreadInterface n, final String email, final String password)
	{
		mLoginThreadInterface=n;
		mEmail=email;
		mPassword=password;
	}	
	
	@Override
	public void run() {
		super.run();
		
		try 
		{
			JSONObject obj=new JSONObject();
			obj.put("email",mEmail);
			obj.put("password",mPassword);
			//obj.put("device_id",SharedData.mMySharedPref.getDeviceId());
			
			Log.i("Parameter :",obj.toString());
			final String result=HttpCalls.getPOSTResponseString(SharedData.SERVER_URL+"login", obj.toString());
			obj=new JSONObject(result);
			boolean resultVal=obj.getBoolean("result");
			String msg = null;
			/*if(resultVal)
			{
				long user_id=obj.getLong("user_id");
				SharedData.mMySharedPref.setUserId(user_id);
			}
			else
				msg=obj.getString("msg");*/
			mLoginThreadInterface.onLoginThreadDataReturned(resultVal,msg);
		} 
		catch (Exception e) {			
			e.printStackTrace();
			mLoginThreadInterface.onLoginThreadErrorReturned();
		}
	}

}
