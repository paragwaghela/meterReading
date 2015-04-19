package com.example.android.meterreading.threads;

import org.json.JSONObject;

import com.example.android.meterreading.utils.HttpCalls;
import com.example.android.meterreading.utils.SharedData;

import android.util.Log;

public class RegisterThread extends Thread {
	private static String TAG="RegisterThread";
	
	public interface RegisterThreadInterface
	{
		public void onRegisterThreadDataReturned(boolean isSuccess,String msg);
		public void onRegisterThreadErrorReturned();
	}
	
	private RegisterThreadInterface mRegisterThreadInterface;
	private String mFName,mLName,mContactNo,mloginId,mPasswd;
	public RegisterThread(RegisterThreadInterface n, String fname, String lname,
			String contact,String login_id,String passWd) {
		
		this.mRegisterThreadInterface=n;
		this.mFName = fname;
		this.mLName = lname;
		this.mContactNo = contact;
		this.mloginId = login_id ;
		this.mPasswd = passWd;
	}

	@Override
	public void run() {
		super.run();
		
		try {
			JSONObject obj=new JSONObject();
			obj.put("fname",mFName);
			obj.put("lname",mLName);
			obj.put("contactno",mContactNo);
			obj.put("loginId",mloginId);
			obj.put("upassword",mPasswd);
			Log.i("Parameter :",obj.toString());
			final String result = HttpCalls.getPOSTResponseString(
					SharedData.SERVER_URL+"register.php", obj.toString());
			JSONObject object=new JSONObject(result);
			boolean resultVal=object.getBoolean("result");
			String msg=object.getString("msg");
			/*if(resultVal)
				SharedData.mMySharedPref.setUserId(object.getLong("user_id"));
			*/
			mRegisterThreadInterface.onRegisterThreadDataReturned(resultVal,msg);
		} catch (Exception e) {
			e.printStackTrace();			
			mRegisterThreadInterface.onRegisterThreadErrorReturned();
		}
	}

}
