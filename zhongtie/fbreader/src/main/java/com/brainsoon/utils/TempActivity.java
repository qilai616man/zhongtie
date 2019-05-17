package com.brainsoon.utils;

import java.io.File;
import java.util.ArrayList;

import org.geometerplus.android.fbreader.FBReader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFActivity;

public class TempActivity extends Activity implements OnClickListener {

	private ArrayList<String> bmpList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v=new View(this);
		setContentView(v);

		Intent fileFromIntent = getIntent();
		if (fileFromIntent != null) {
			String fileType = fileFromIntent.getStringExtra("FILETYPE");
			String filePath = fileFromIntent.getStringExtra("FILEPATH");

			if (filePath != null && !"".equals(filePath)) {
				File rootFile = new File(filePath);
				if (!rootFile.exists()) {
					Toast.makeText(TempActivity.this, "文件路径错误！",
							Toast.LENGTH_SHORT).show();
					finish();
				}
				if ("EPUB".equals(fileType)) {
					Uri uri = Uri.fromFile(new File(filePath));
					Intent epubIntent = new Intent(this, FBReader.class);
					epubIntent.setData(uri);
					epubIntent.putExtra("FILENAME", filePath);
					startActivity(epubIntent);
				} else if ("PDF".equals(fileType)) {
					Uri uri = Uri.parse(filePath);
					Intent pdfIntent = new Intent(this, MuPDFActivity.class);
					pdfIntent.setAction(Intent.ACTION_VIEW);
					pdfIntent.setData(uri);
					startActivity(pdfIntent);
				} else if ("WPS".equals(fileType)) {
					BookUtils utils = new BookUtils(this);
//					utils.openFile(filePath);
				} else if ("TXT".equals(fileType)) {
					Intent txtIntent = new Intent(this, TxtReaderActivity.class);
					txtIntent.putExtra("FILECODE",
							fileFromIntent.getStringExtra("FILECODE"));
					txtIntent.putExtra("fileName", filePath);
					startActivity(txtIntent);
				} else if ("IMAGES".equals(fileType)) {
					bmpList = new ArrayList<String>();
					getFiles(rootFile);
					Intent bmpIntent = new Intent(this,
							BitmapScannerActivity.class);
					bmpIntent.putStringArrayListExtra("bmpList", bmpList);
					startActivity(bmpIntent);
				} else {
					Toast.makeText(TempActivity.this, "文件样式错误！",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
		finish();
	}

	private void getFiles(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				File childFile = files[i];
				getFiles(childFile);
			}
		} else {
			String fileName = null;
			fileName = file.getName();
			if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
				String absolutePath = file.getAbsolutePath();
				bmpList.add(absolutePath);
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		// case R.id.btn_scan:
		// Intent intent = new Intent(MainActivity.this,
		// BitmapScannerActivity.class);
		// intent.putStringArrayListExtra("bmpList", bmpList);
		// startActivity(intent);
		// break;
		// case R.id.btn_scanPDF:
		// // filename为PDF文件路径
		// String filename = Environment.getExternalStorageDirectory()
		// .getAbsolutePath() + File.separator + "495C.pdf";
		// Uri uri = Uri.parse(filename);
		// Intent pdfIntent = new Intent(MainActivity.this,
		// MuPDFActivity.class);
		// pdfIntent.setAction(Intent.ACTION_VIEW);
		// pdfIntent.setData(uri);
		// startActivity(pdfIntent);
		// break;
		}
	}
}
