package com.ereader.zip;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button bt = (Button)findViewById(R.id.button1);
		
		bt.setOnClickListener(listener);
		

	}
	private  OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Button bt = (Button)v;
			if(bt.getId() == R.id.button1){
				openzip zip  =  new openzip("/sdcard/test111.tdp");
				TextView tv = (TextView)findViewById(R.id.textView1);
				tv.append(zip.getTitleString());
				tv.append(zip.getCreatorString());
				tv.append(zip.getIdentifierString());
				InputStream in =  zip.getCover();
				if(in != null){				
					byte[] bytes = new byte[10];
					try{
						in.read(bytes);
					}
					catch(IOException e){
					
					}
					int i = bytes[0];
				}

			}
		}
	};

}
