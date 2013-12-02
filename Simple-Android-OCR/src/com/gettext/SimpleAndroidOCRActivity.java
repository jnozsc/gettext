package com.gettext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

public class SimpleAndroidOCRActivity extends Activity {
	public static final String PACKAGE_NAME = "com.gettext";
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/SimpleAndroidOCR/";

	// You should have the trained data file in assets folder
	// You can get them at:
	// http://code.google.com/p/tesseract-ocr/downloads/list
	// public static final String lang = "eng";
	public String lang = "eng";

	private static final String TAG = "gettext";

	protected Button _button;
	protected Button loadbutton;
	protected Button translateButton;
	// protected ImageView _image;
	protected EditText _field;
	protected String _path;
	protected boolean _taken;

	protected static final String PHOTO_TAKEN = "photo_taken";

	// private static final String Button = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path
							+ " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}

		// handle different input languages
		ArrayList<String> langs = new ArrayList<String>();
		langs.add("eng");
		langs.add("chi_sim");
		langs.add("hin");
		// end of different input languages

		// lang.traineddata file with the app (in assets folder)
		// You can get them at:
		// http://code.google.com/p/tesseract-ocr/downloads/list
		// This area needs work and optimization

		// here, add a for loop, copy all language files, nice, it works
		for (String lang_file : langs) {
			if (!(new File(DATA_PATH + "tessdata/" + lang_file + ".traineddata"))
					.exists()) {
				try {

					AssetManager assetManager = getAssets();
					InputStream in = assetManager.open("tessdata/" + lang_file
							+ ".traineddata");
					// GZIPInputStream gin = new GZIPInputStream(in);
					OutputStream out = new FileOutputStream(DATA_PATH
							+ "tessdata/" + lang_file + ".traineddata");

					// Transfer bytes from in to out
					byte[] buf = new byte[1024];
					int len;
					// while ((lenf = gin.read(buff)) > 0) {
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
					// gin.close();
					out.close();

					Log.v(TAG, "Copied " + lang_file + " traineddata");
				} catch (IOException e) {
					Log.e(TAG, "Was unable to copy " + lang_file
							+ " traineddata " + e.toString());
				}
			}
		}

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		// _image = (ImageView) findViewById(R.id.image);
		_field = (EditText) findViewById(R.id.field);
		_button = (Button) findViewById(R.id.button);
		_button.setOnClickListener(new ButtonClickHandler());

		loadbutton = (Button) findViewById(R.id.gallary);
		loadbutton.setOnClickListener(new LoadButtonClickHandler());

		translateButton = (Button) findViewById(R.id.translate);
		translateButton.setOnClickListener(new translateButtonClickHandler());

		_path = DATA_PATH + "/ocr.jpg";

		Spinner _spinner = (Spinner) findViewById(R.id.language_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, langs);
		_spinner.setAdapter(adapter);
		_spinner.setOnItemSelectedListener(new SpinnerXMLSelectedListener());
		_spinner.setVisibility(View.VISIBLE);

		// translate button and controller

	}

	class SpinnerXMLSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Spinner _spinner = (Spinner) findViewById(R.id.language_spinner);
			lang = (String) _spinner.getAdapter().getItem(arg2);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
	}

	public class ButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			Log.v(TAG, "Starting Camera app");
			startCameraActivity();
		}
	}

	public class LoadButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			Log.v(TAG, "Starting gallery app");
			startGalleryActivity();
		}
	}

	public class translateButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			Log.v(TAG, "Starting translate REQUEST");
			// startGalleryActivity();
			MyAsyncTask MyAsyncTask = new MyAsyncTask();
			// TODO: setting output language
			String outputLanguage = "ZH-CN";
			// end of TODO
			String result = null;
			try {
				String temp = _field.getText().toString();
				// temp = "hello";
				result = MyAsyncTask.execute(URLEncoder.encode(temp, "UTF-8"),
						language(lang), outputLanguage).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int responseStatus = 0;
			try {
				responseStatus = jsonObject.getInt("responseStatus");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (responseStatus != 200) {
				Log.v(TAG, "GET request fail.");
			} else {
				Log.v(TAG, "GET request success.");
				TextView translatedText = (TextView) findViewById(R.id.translatedText);
				String translated = "";
				try {
					translated = jsonObject.getJSONObject("responseData")
							.getString("translatedText");
					Log.v(TAG, "translated text is " + translated);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				translatedText.setText(translated);
			}
		}
	}

	public String language(String input) {
		if (input.equals("eng")) {
			return "en";
		} else if (input.equals("chi_sim")) {
			return "ZH-CN";
		} else if (input.equals("hid")) {
			return "hi";
		}
		// default
		return "en";
	}

	protected void startGalleryActivity() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);//
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				0);
	}

	// Simple android photo capture:
	// http://labs.makemachine.net/2010/03/simple-android-photo-capture/

	protected void startCameraActivity() {
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);

		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i(TAG, "resultCode: " + resultCode);

		if (resultCode == -1) {
			onPhotoTaken();
		} else {
			Log.v(TAG, "User cancelled");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(SimpleAndroidOCRActivity.PHOTO_TAKEN, _taken);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "onRestoreInstanceState()");
		if (savedInstanceState.getBoolean(SimpleAndroidOCRActivity.PHOTO_TAKEN)) {
			onPhotoTaken();
		}
	}

	protected void onPhotoTaken() {
		_taken = true;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

		try {
			ExifInterface exif = new ExifInterface(_path);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			Log.v(TAG, "Orient: " + exifOrientation);

			int rotate = 0;

			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}

			Log.v(TAG, "Rotation: " + rotate);

			if (rotate != 0) {

				// Getting width & height of the given image.
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();

				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				// Rotating Bitmap
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			}

			// Convert to ARGB_8888, required by tess
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
		}

		// _image.setImageBitmap( bitmap );

		Log.v(TAG, "Before baseApi");

		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		Log.v(TAG, "lang is " + lang);
		baseApi.init(DATA_PATH, lang);
		baseApi.setImage(bitmap);

		String recognizedText = baseApi.getUTF8Text();

		baseApi.end();

		// You now have the text in recognizedText var, you can do anything with
		// it.
		// We will display a stripped out trimmed alpha-numeric version of it
		// (if lang is eng)
		// so that garbage doesn't make it to the display.

		Log.v(TAG, "OCRED TEXT: " + recognizedText);

		if (lang.equalsIgnoreCase("eng")) {
			recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
		}

		recognizedText = recognizedText.trim();

		if (recognizedText.length() != 0) {
			_field.setText(_field.getText().toString().length() == 0 ? recognizedText
					: _field.getText() + " " + recognizedText);
			_field.setSelection(_field.getText().toString().length());
		}

		// Cycle done.
	}
}
