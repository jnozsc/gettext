package com.gettext;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewHistory extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		MyDatabase db = new MyDatabase(this);
		String str = db.getAllText();
		TextView t1 = (TextView) findViewById(R.id.history);

		t1.setText(str);
	}
}
