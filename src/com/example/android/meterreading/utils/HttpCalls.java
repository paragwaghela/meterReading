package com.example.android.meterreading.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class HttpCalls {

	final private static String TAG = "HttpCalls";

	public static String getJSONString(String full_url) {
		Log.d(TAG, "getJSONString url=" + full_url);

		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(full_url)
					.openConnection();
			
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type",
			"application/x-www-form-urlencoded");
			conn.setInstanceFollowRedirects(true);
			conn.setUseCaches(false);
			
			Thread.sleep(2000);
						
			InputStream sIn = conn.getInputStream();
			byte[] data = readStream(sIn);
			sIn.close();
			conn.disconnect();

			final String res = new String(data);
			Log.d(TAG, "getJSONString xres=" + res);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private static byte[] readStream(InputStream sIn) throws IOException {
		int read;
		byte[] chunck = new byte[1024];
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();

		while (((read = sIn.read(chunck)) > 0))
			bOut.write(chunck, 0, read);

		return bOut.toByteArray();
	}

	public static String getPOSTResponseString(final String url,
			final ArrayList<NameValuePair> nameValuePairs) throws Exception {
		Log.d(TAG, "getResponseString " + url);

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		if (nameValuePairs != null) {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		Thread.sleep(2000);
		byte[] data = readStream(entity.getContent());
		return new String(data);
	}

	public static String getPOSTResponseString(final String url,
			final String urlParameters) throws Exception {
		Log.d(TAG, "getPOSTResponseString " + url);

		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length",
				"" + Integer.toString(urlParameters.getBytes().length));
		conn.setRequestProperty("Content-Language", "en-US");
		conn.setInstanceFollowRedirects(true);
		conn.setUseCaches(false);
		conn.setDoInput(true);
		
		//Thread.sleep(5000);
		// Send request
		if (urlParameters != null)
		{
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
		}
		Thread.sleep(2000);
		InputStream sIn = conn.getInputStream();
		byte[] data = readStream(sIn);
		sIn.close();
		conn.disconnect();

		final String res = new String(data);
		Log.d(TAG, "getJSONString n res=" + res);
		return res;
	}
}
