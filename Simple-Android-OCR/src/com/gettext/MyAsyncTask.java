package com.gettext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class MyAsyncTask extends AsyncTask<String, Void, String> {

	private static final String TAG = "gettext";
	private final String Base_URL = "http://api.mymemory.translated.net/get?";

	@Override
	// protected String doInBackground(String... params) {
	// TODO Auto-generated method stub
	// }
	protected String doInBackground(String... params) {
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// Log.v(TAG, "POST: " + params[0]);
			String seg = params[0].trim();
			String langpair_left = params[1].trim();
			String langpair_right = params[2].trim();

			// http://api.mymemory.translated.net/get?q=nyu!&langpair=en|ZH-CN
			String GET_URL = Base_URL + "q=" + seg + "&langpair="
					+ langpair_left + URLEncoder.encode("|", "UTF-8")
					+ langpair_right;
			Log.v(TAG, "GET: " + GET_URL);
			HttpGet httpget = new HttpGet(GET_URL);
			// httpget.setEntity(se);

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httpget);

			HttpEntity entity = response.getEntity();

			InputStream inputStream = entity.getContent();
			// JSON is UTF-8 by default
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();
			Log.v(TAG, "return: " + result);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		return result;
	}
}