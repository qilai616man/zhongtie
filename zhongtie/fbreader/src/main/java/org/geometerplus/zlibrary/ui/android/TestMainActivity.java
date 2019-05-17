package org.geometerplus.zlibrary.ui.android;

import java.io.File;

import org.geometerplus.android.fbreader.FBReader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TestMainActivity extends Activity {
	private EditText txt = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_main);
		Button btn = (Button)findViewById(R.id.openBook);
		txt = (EditText)findViewById(R.id.fileName);
		btn.setOnClickListener(openBookListener);
	}

	public OnClickListener openBookListener = new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			String bookPath = txt.getText().toString();
			Intent intent = new Intent();
			//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			intent.setData(Uri.fromFile(new File(bookPath)));
			intent.setClass(TestMainActivity.this, FBReader.class);
			startActivity(intent);
			
//			Book myBook = null;
//			FBReader.openBookActivity(getApplicationContext(), myBook, null);
		}
		
	};
}
