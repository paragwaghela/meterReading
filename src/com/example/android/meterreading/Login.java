package com.example.android.meterreading;

import com.example.android.meterreading.Login;
import com.example.android.meterreading.threads.LoginThread.LoginThreadInterface;
//import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
//import android.os.Looper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
//import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements LoginThreadInterface {
	//private SharedPreferences mSharedPreferences;
	AlertDialogManager alert = new AlertDialogManager();
	private ProgressDialog dialog = null;
	private EditText loginId, loginPassword;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        loginId=(EditText)findViewById(R.id.editloginId);
		loginPassword=(EditText)findViewById(R.id.editPassword);
}

    
    public void doLogin(View view) {
		final String username = loginId.getText().toString().trim();
		
		if(username.length() <= 0) {
			Toast.makeText(this, "Enter username", Toast.LENGTH_LONG).show();
			return;
		}
		final String password = loginPassword.getText().toString().trim();
		Log.d("Password",password);
		if(password.length() <= 0) {
			Toast.makeText(this, "Enter password", Toast.LENGTH_LONG).show();
			return;
		}
		//dialog = ProgressDialog.show(Login.this, "", "Login...", true);
		Intent i=new Intent(Login.this,MeterReadingActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
		//new LoginThread(Login.this,username,password).start();
	}
    private final static int LOGIN_DATA_TRUE=1;
	private final static int LOGIN_DATA_FALSE=2;
	private final static int LOGIN_ERROR=3;

	private final Handler mHandler=new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			dialog.dismiss();
			switch (msg.what) {
			case LOGIN_DATA_TRUE:
			{
				//SharedData.mMySharedPref.setUserName(email);
				//SharedData.mMySharedPref.setPassword(password);
				Intent i=new Intent(Login.this,MeterReadingActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			}
			break;
			case LOGIN_DATA_FALSE:
			{
				alert.showAlertDialog(Login.this,"Alert!", "Login Failed : "+msg.obj.toString(),false);
				//Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
				//ShowSimpleDialog("Alert!", "Login Failed : "+msg.obj.toString());
			}
			break;

			case LOGIN_ERROR:
			{
				alert.showAlertDialog(Login.this,"Alert!","App Server not reachable ",false);
				//ShowSimpleDialog("Alert!", "App Server not reachable.");
			}
			break;
			}
		}
	};

	//@Override
	public void onLoginThreadDataReturned(Boolean isSuccess,String msg) {
		if(isSuccess)
			mHandler.sendMessage(mHandler.obtainMessage(LOGIN_DATA_TRUE,null));
		else
			mHandler.sendMessage(mHandler.obtainMessage(LOGIN_DATA_FALSE,msg));
	}

	//@Override
	public void onLoginThreadErrorReturned() {
		mHandler.sendMessage(mHandler.obtainMessage(LOGIN_ERROR,null));
	}
	
	public void register(View view) {
		Intent intent = new Intent(Login.this, Register.class);
		startActivity(intent);
		finish();
		
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
      //  getMenuInflater().inflate(R.menu.activity_register, menu);
        return true;
    }
}
