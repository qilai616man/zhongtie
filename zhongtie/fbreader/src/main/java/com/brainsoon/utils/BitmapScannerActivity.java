package com.brainsoon.utils;

import java.util.ArrayList;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.geometerplus.zlibrary.ui.android.R;

public class BitmapScannerActivity extends Activity implements OnTouchListener {

	private ImageView bmp_scan;
	private TextView bmp_page;
	private ArrayList<String> bmpList;
	private int curPos = 0;
	private Bitmap curBmp = null;
	private float downX = 0, downY = 0, upX = 0, upY = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bitmap_scanner);

		bmp_page = (TextView) findViewById(R.id.bmp_page);
		bmp_scan = (ImageView) findViewById(R.id.bmp_scan);
		bmp_scan.setOnTouchListener(this);

		bmpList = getIntent().getStringArrayListExtra("bmpList");

		setBitmap(curPos);
	}

	private void setBitmap(int position) {
		if (curBmp != null) {
			curBmp.recycle();
		}
		curBmp = BitmapFactory.decodeFile(bmpList.get(position));
		bmp_scan.setImageBitmap(curBmp);
		bmp_page.setText((curPos + 1) + "/" + bmpList.size());
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getRawX();
			downY = event.getRawY();
			break;
		case MotionEvent.ACTION_UP:
			upX = event.getRawX();
			upY = event.getRawY();
			if (downX == upX && downY == upY) {
				Toast.makeText(BitmapScannerActivity.this, "点击事件",
						Toast.LENGTH_LONG).show();
			} else {
				float distanceX = upX - downX;
				if (distanceX > 50.0) {
					if (curPos == 0) {
						Toast.makeText(BitmapScannerActivity.this, "已到达首页",
								Toast.LENGTH_LONG).show();
					} else {
						setBitmap(--curPos);
					}
				}
				if (distanceX < -50.0) {
					if (curPos == bmpList.size() - 1) {
						Toast.makeText(BitmapScannerActivity.this, "已到达最后一页",
								Toast.LENGTH_LONG).show();
					} else {
						setBitmap(++curPos);
					}
				}
			}
			break;
		}
		return true;
	}

}
