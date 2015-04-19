package com.example.android.meterreading;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.example.android.meterreading.utils.SharedData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MeterReadingActivity extends Activity  {

	AlertDialogManager alert = new AlertDialogManager();
	Uri picUri;
	
	private static final String TAG = "upload",BITMAP_STORAGE_KEY = "viewbitmap",IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private static final int ACTION_TAKE_PHOTO_B = 1,PIC_CROP = 2,PICK_FROM_FILE=3;
	private ProgressDialog dialog = null;
	private ImageView mImageView;
	private Bitmap mImageBitmap;
	String message = "",responseBody="";
	private TextView mTextView;
	Bitmap bitmapsend,pic1=null,pic2=null;
	private String mCurrentPhotoPath,pic1Path,pic2Path;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private Button texBtn;
	HttpResponse response = null;
	/* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}

	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			Log.d("TAG","Get AlbumDir");
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	//Create Photo and save in mobile

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {

		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		return f;
	}

	//Decode a Scaled Image
	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */
		Log.d("TAG","Set pic"+mCurrentPhotoPath);
		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		bitmapsend = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		/*pic1=BitmapFactory.decodeFile(pic1Path, bmOptions);
		pic2=BitmapFactory.decodeFile(pic2Path, bmOptions);*/
		/* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(bitmapsend);
		mImageView.setVisibility(View.VISIBLE);
		mTextView.setText("");
	}

	//Take a Photo with the Camera App

	private void dispatchTakePictureIntent(int actionCode) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch(actionCode) {
		case ACTION_TAKE_PHOTO_B:
			File f = null;
			try {
				f = setUpPhotoFile();
				pic1=null;
				pic2=null;
				mCurrentPhotoPath = f.getAbsolutePath();
				Log.d("CurrntPhotoPath","Path "+Uri.fromFile(f));
				picUri = Uri.fromFile(f);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			break;

		default:
			break;			
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
	}
	private void handleBigCameraPhoto() {
		Log.d("TAG","Inside handle big pic");
		if (mCurrentPhotoPath != null) {
			setPic();
			//galleryAddPic();
			//mCurrentPhotoPath = null;
		}
		else Log.d("mCurrentpath ","Null");

	}


	/*Button.OnClickListener mTakePicOnClickListener = 
		new Button.OnClickListener() {
		public void onClick(View v) {
			dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
		}
	};*/
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		/*if(mCurrentPhotoPath!=null){
			File file = new File(mCurrentPhotoPath);
            //file.delete();
		}*/
		Log.d("TAG","increate function");
		mImageView = (ImageView) findViewById(R.id.imageView1);
		
		mTextView = (TextView) findViewById(R.id.textView1);
		mImageBitmap = null;
		texBtn = (Button) findViewById(R.id.btnIntendT);
		texBtn.setVisibility(View.INVISIBLE);

		/*setBtnListenerOrDisable( 
				picBtn, 
				mTakePicOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE
		);*/


		final String [] items			= new String [] {"Take from camera", "Select from gallery"};				
		ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
		AlertDialog.Builder builder		= new AlertDialog.Builder(this);

		builder.setTitle("Select Image");
		builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int item ) { //pick from camera
				if (item == 0) {
					dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
				} else { //pick from file
					File f = null;
					try {
						f = setUpPhotoFile();
						pic1=null;
						pic2=null;
						mCurrentPhotoPath = f.getAbsolutePath();
						Log.d("CurrntPhotoPath","Path "+Uri.fromFile(f));
						picUri = Uri.fromFile(f);
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
					} catch (IOException e) {
						e.printStackTrace();
						f = null;
						
					}

					
				}
			}
		} );
		final AlertDialog dialog1 = builder.create();
		Button picBtn = (Button) findViewById(R.id.btnIntend);
		picBtn.setOnClickListener(new View.OnClickListener() {	

			public void onClick(View v) {
				dialog1.show();
			}
		});

		texBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				dialog = ProgressDialog.show(MeterReadingActivity.this, "", "Getting Detail...", true);
				if(pic1 != null)
					Log.d("Pic1",""+pic1);
				if(pic2!= null)
					Log.d("Pic2",""+pic2);
				new UploadTask().execute(bitmapsend);
			}
		});
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
	}

	//Get the Thumbnail

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {

		case ACTION_TAKE_PHOTO_B: 
			if (resultCode == RESULT_OK) {
				texBtn.setVisibility(View.VISIBLE);
				cropCapturedImage(picUri);
			}
			break;
		case PICK_FROM_FILE :
			Log.d("TAGDATA",""+data);
			if(resultCode == RESULT_OK){
				
		
			texBtn.setVisibility(View.VISIBLE);
			picUri=data.getData();
			File f = new File(picUri.getPath());
			//mCurrentPhotoPath = f.getAbsolutePath();
			mCurrentPhotoPath = getRealPathFromURI(getApplicationContext(),picUri);
			Log.d("MyPath",mCurrentPhotoPath);
			cropCapturedImage(picUri);
			}/*else{
				Intent i=new Intent(MeterReadingActivity.this,Login.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
				
			}*/
			break;
		case PIC_CROP :
			//Create an instance of bundle and get the returned data
			Log.d("TAGDATA",""+data);
			if(data!=null){
				Bundle extras = data.getExtras();
				
				//get the cropped bitmap from extras
				if(pic1 == null){
					pic1 = extras.getParcelable("data");
				}else{
					pic2 = extras.getParcelable("data");
				}
				if(pic1!=null && pic2!=null){
					handleBigCameraPhoto();	
				}else{
					//set image bitmap to image view
					cropCapturedImage(picUri);
				}
			}
		} 
	}
	public String getRealPathFromURI(Context context, Uri contentUri) {
		  Cursor cursor = null;
		  try { 
		    String[] proj = { MediaStore.Images.Media.DATA };
		    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    return cursor.getString(column_index);
		  } finally {
		    if (cursor != null) {
		      cursor.close();
		    }
		  }
		}
	private void cropCapturedImage(Uri picUri2) {
		// TODO Auto-generated method stub
		Log.d("Inside Crop",""+picUri2);
		//call the standard crop action intent 
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		//indicate image type and Uri of image
		cropIntent.setDataAndType(picUri2, "image/*");
		//set crop properties
		cropIntent.putExtra("crop", "true");
		cropIntent.putExtra("crop", "true");
		//indicate aspect of desired crop
		cropIntent.putExtra("aspectX", 3);
		cropIntent.putExtra("aspectY", 1);
		//indicate output X and Y
		cropIntent.putExtra("outputX", 300);
		cropIntent.putExtra("outputY", 100);
		//retrieve data on return
		cropIntent.putExtra("return-data", true);
		//start the activity - we handle returning in onActivityResult
		startActivityForResult(cropIntent, PIC_CROP);
	}
	private class UploadTask extends AsyncTask<Bitmap, Void, Void> {

		
		protected Void doInBackground(Bitmap... bitmaps) {
			if (bitmaps[0] == null)
				return null;
			setProgress(0);
			Log.d("TAG:","Here in async task");
			Bitmap bitmap = bitmaps[0];
			Bitmap bitmap1 = pic1;
			Bitmap bitmap2 = pic2;
			if(bitmap1 != null){
				Log.d("Bitmap1",""+bitmap1);
			}
			else
				Log.d("Bitmap1 is null",""+bitmap1);
			if(bitmap2 != null){
				Log.d("Bitmap2",""+bitmap2);
			}else
				Log.d("Bitmap2 b=nulll",""+bitmap2);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
			ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
			bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream1); 
			bitmap2.compress(Bitmap.CompressFormat.PNG, 100, stream2); 
			InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream

			byte [] image1 = stream1.toByteArray();
			byte [] image2 = stream2.toByteArray();
			StringBuffer img_str1 = new StringBuffer(Base64.encodeToString(image1,Base64.DEFAULT));
			Log.d("Img1",img_str1.toString());
			String img_str2 = Base64.encodeToString(image2,Base64.DEFAULT);
			Log.d("Img2",img_str2.toString());
			DefaultHttpClient httpclient = new DefaultHttpClient();
			try {
				HttpPost httppost = new HttpPost(
						SharedData.SERVER_URL+"ImageUploadServlet"); // server
				MultipartEntity reqEntity = new MultipartEntity();
				
				reqEntity.addPart("meter_reading",img_str1.toString());
				reqEntity.addPart("meter_number",img_str2.toString());				
				reqEntity.addPart("main_image","main_image.png",in);
								
				httppost.setEntity(reqEntity);
				Log.i(TAG, "request " + httppost.getRequestLine());
				//HttpResponse response = null;
				try {
					response = httpclient.execute(httppost);
					responseBody = EntityUtils.toString(response.getEntity());
					Log.d("result",responseBody);
				} catch (ClientProtocolException e) {

					dialog.dismiss();
					e.printStackTrace();
				} catch (IOException e) {
					dialog.dismiss();

					e.printStackTrace();
				}
				try {
					if (response != null)
						Log.i(TAG, "response " + response.getStatusLine().toString());
				} finally {

				}
				
			} finally {

			}

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					dialog.dismiss();

					e.printStackTrace();
				}
			}

			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {

			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {

			dialog.dismiss();
			super.onPostExecute(result);
			//mTextView.setText(responseBody);
			if(response!= null){
				alert.showAlertDialog(MeterReadingActivity.this,"Detail is!","Sucessfull",false);
			}
			else 
				alert.showAlertDialog(MeterReadingActivity.this,"Detail is!","Fail",false);

		}
	}


	@Override
	protected void onResume() {

		super.onResume();
		Log.i(TAG, "onResume: " + this);
	}

	@Override
	protected void onPause() {

		super.onPause();
	}


	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
		mImageView.setImageBitmap(mImageBitmap);
		mImageView.setVisibility(
				savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? 
						ImageView.VISIBLE : ImageView.INVISIBLE
		);

	}

	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
			packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	private void setBtnListenerOrDisable( 
			Button btn, 
			Button.OnClickListener onClickListener,
			String intentName
	) {
		if (isIntentAvailable(this, intentName)) {
			btn.setOnClickListener(onClickListener);        	
		} else {
			btn.setText( 
					getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setClickable(false);
		}
	}
}