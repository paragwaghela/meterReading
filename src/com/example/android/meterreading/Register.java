package com.example.android.meterreading;

import com.example.android.meterreading.threads.RegisterThread;
import com.example.android.meterreading.threads.RegisterThread.RegisterThreadInterface;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity implements OnClickListener,RegisterThreadInterface {

    
	Button btn;
	String FirstName = "", LastName = "", ContactNo = "", LoginId = "", Password = "";
	EditText first_name, last_name, contact_no, login_id, login_password;
	AlertDialogManager alert = new AlertDialogManager();
	private ProgressDialog dialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_register);
			btn = (Button) findViewById(R.id.reg);
			btn.setOnClickListener(this);
			first_name = (EditText) findViewById(R.id.editText1);
			last_name = (EditText) findViewById(R.id.editText2);
			contact_no = (EditText) findViewById(R.id.editText3);
			login_id = (EditText) findViewById(R.id.editText4);
			login_password = (EditText) findViewById(R.id.editText5);
			
		} catch (Exception ex) {
			Log.e("MainActivity.java", Log.getStackTraceString(ex));
		}
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		FirstName = first_name.getText().toString().trim();
		LastName = last_name.getText().toString().trim();
		ContactNo = contact_no.getText().toString().trim();
		LoginId= login_id.getText().toString().trim();
		Password = login_password.getText().toString().trim();
		
		//check input fileds
		if (FirstName.equals("")) {
			Toast.makeText(getApplicationContext(), "FirstName is missing.",
					Toast.LENGTH_LONG).show();
			first_name.setText("");
			first_name.requestFocus();
		} else if (LastName.equals("")) {
			Toast.makeText(getApplicationContext(), "LastName is missing.",
					Toast.LENGTH_LONG).show();
			last_name.setText("");
			last_name.requestFocus();
		} else if (ContactNo.equals("")) {
			Toast.makeText(getApplicationContext(), "ContactNo is missing.",
					Toast.LENGTH_LONG).show();
			contact_no.setText("");
			contact_no.requestFocus();
		} else if (LoginId.equals("")) {
			Toast.makeText(getApplicationContext(), "LoginId is missing.",
					Toast.LENGTH_LONG).show();
			login_id.setText("");
			login_id.requestFocus();
		} else if (Password.equals("")) {
			Toast.makeText(getApplicationContext(), "Password is missing.",
					Toast.LENGTH_LONG).show();
			login_password.setText("");
			login_password.requestFocus();
		}else {
			//Send data to the server
			dialog = ProgressDialog.show(Register.this, "", "Registering...", true);
			new RegisterThread(Register.this,FirstName,LastName,ContactNo,LoginId,Password).start();
		}
	}
	private final static int REGISTER_DATA_TRUE=1;
	private final static int REGISTER_DATA_FALSE=2;
	private final static int REGISTER_ERROR=3;

	private final Handler mHandler=new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			dialog.dismiss();
			switch (msg.what) {
			case REGISTER_DATA_TRUE:
			{
				Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
			case REGISTER_DATA_FALSE:
			{
				alert.showAlertDialog(Register.this,"Alert!", "Registration Failed : "+msg.obj.toString(),false);
				
			}
			break;
			case REGISTER_ERROR:
			{
				alert.showAlertDialog(Register.this,"Alert!", "App Server not reachable : "+msg.obj.toString(),false);
			
			}
			break;
			}
		}
	};

		//@Override
		public void onRegisterThreadDataReturned(boolean isSuccess,String msg) {
			if(isSuccess)
				mHandler.sendMessage(mHandler.obtainMessage(REGISTER_DATA_TRUE,msg));
			else
				mHandler.sendMessage(mHandler.obtainMessage(REGISTER_DATA_FALSE,msg));
		}
	
		//@Override
		public void onRegisterThreadErrorReturned() {	
			mHandler.sendMessage(mHandler.obtainMessage(REGISTER_ERROR,null));
		}

    
}
