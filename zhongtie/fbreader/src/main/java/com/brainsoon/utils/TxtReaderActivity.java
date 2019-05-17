package com.brainsoon.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.geometerplus.zlibrary.ui.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;



public class TxtReaderActivity extends Activity implements OnTouchListener {

	private TextView txt_show;
	private List<String> contentList;
	private String fileName, fileCode;
	private int curPage = 0;
	private float downX = 0, downY = 0, upX = 0, upY = 0;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_txt_reader);

		txt_show = (TextView) findViewById(R.id.txt_show);
		txt_show.setOnTouchListener(this);

		fileName = getIntent().getStringExtra("fileName");
		fileCode = getIntent().getStringExtra("FILECODE");
		if (fileCode == null || "".equals(fileCode)) {
			fileCode = "gbk";
		}
		contentList = new ArrayList<String>();

		File file = new File(fileName);
		if (!file.exists()) {
			Toast.makeText(TxtReaderActivity.this, "文件路径错误！",
					Toast.LENGTH_SHORT).show();
		} else {
			try {
				FileInputStream fis = new FileInputStream(file);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fis, fileCode));
				Display defaultDisplay = getWindowManager().getDefaultDisplay();
				int height = defaultDisplay.getHeight();
				while (br.ready()) {
					StringBuffer sBuffer = new StringBuffer();
					// 当前显示10行内容
					for (int i = 0; i < height / 60; i++) {
						String readLine = br.readLine();
						if (readLine != null) {
							sBuffer.append(readLine + "\n");
						} else {
							sBuffer.append("\n");
						}
					}
					contentList.add(sBuffer.toString());
				}
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			txt_show.setText(contentList.get(curPage));
			txt_show.setTextSize(30);
		}

	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getRawX();
			downY = event.getRawY();
			break;
		case MotionEvent.ACTION_UP:
			upX = event.getRawX();
			upY = event.getRawY();
			if (downX == upX && downY == upY) {
				Toast.makeText(TxtReaderActivity.this, "点击事件",
						Toast.LENGTH_LONG).show();
			} else {
				float distanceX = upX - downX;
				if (distanceX > 50.0) {
					if (curPage == 0) {
						Toast.makeText(TxtReaderActivity.this, "已到达首页",
								Toast.LENGTH_LONG).show();
					} else {
						txt_show.setText(contentList.get(--curPage));
					}
				}
				if (distanceX < -50.0) {
					if (curPage == contentList.size() - 1) {
						Toast.makeText(TxtReaderActivity.this, "已到达最后一页",
								Toast.LENGTH_LONG).show();
					} else {
						txt_show.setText(contentList.get(++curPage));
					}
				}
			}
			break;
		}
		return true;
	}

}
