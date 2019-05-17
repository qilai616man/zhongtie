package com.brainsoon.outlookactivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.artifex.mupdfdemo.R;
import com.brainsoon.outlook.db.BookmarkItem;
import com.brainsoon.outlook.db.PDFMarkDbAdapter;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class OutLookActivity extends Activity {

	private LinearLayout linearLayoutTop;
	private Button tabCatalog;
	private Button tabBookMark;
	private ListView listCatalog, listAnnotate;

	// private TabHost mTabHost;
	private OutLookData mOutlineData;
	private PDFMarkDbAdapter markDbAdapter;
	private ArrayList<HashMap<String, Object>> mOutLookListItem;
	private SimpleAdapter mOutLookListItemAdapter;
	private ArrayList<HashMap<String, Object>> mBookMarkListItem;
	private SimpleAdapter mBookMarkListItemAdapter;
	private ImageButton mGobackImageButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		mOutlineData = (OutLookData) intent
				.getSerializableExtra(OutLookData.OUT_LINE);

		markDbAdapter = new PDFMarkDbAdapter(this);
		markDbAdapter.open(mOutlineData.getBookName());

		setContentView(R.layout.activity4outlook);
		linearLayoutTop = (LinearLayout) findViewById(R.id.linearLayoutTop);
		tabCatalog = (Button) findViewById(R.id.tab_catalog);
		tabBookMark = (Button) findViewById(R.id.tab_annotate);
		listCatalog=(ListView) findViewById(R.id.listCatalog);
		listAnnotate=(ListView) findViewById(R.id.listAnnotate);

		// mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		// mTabHost.setup();
		// mTabHost.addTab(mTabHost.newTabSpec("").setIndicator(createTabView("outlook")).setContent(R.id.listViewOutLook));
		// mTabHost.addTab(mTabHost.newTabSpec("").setIndicator(createTabView("bookmark")).setContent(R.id.listViewBookMark));
		mGobackImageButton = (ImageButton) findViewById(R.id.imageButtonGoBack);
		initOutLookListView(listCatalog);
		initBookMarkOutListView(listAnnotate);

		tabCatalog.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				linearLayoutTop.setBackgroundResource(R.drawable.func_toc_show);
				tabCatalog.setTextColor(getResources().getColor(
						R.color.func_toc_active));
				tabBookMark.setTextColor(getResources().getColor(
						R.color.func_toc_normal));
				listCatalog.setVisibility(View.VISIBLE);
				listAnnotate.setVisibility(View.INVISIBLE);
			}
		});
		tabBookMark.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				linearLayoutTop
						.setBackgroundResource(R.drawable.func_annotation_show);
				tabBookMark.setTextColor(getResources().getColor(
						R.color.func_toc_active));
				tabCatalog.setTextColor(getResources().getColor(
						R.color.func_toc_normal));
				listCatalog.setVisibility(View.INVISIBLE);
				listAnnotate.setVisibility(View.VISIBLE);
			}
		});

		mGobackImageButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// mTabHost.setCurrentTab(0);
		listCatalog.setVisibility(View.VISIBLE);
		listAnnotate.setVisibility(View.INVISIBLE);
	}

	private View createTabView(String text) {
		View view;
		if (text.compareTo("outlook") == 0) {
			view = LayoutInflater.from(this).inflate(
					R.layout.tab_indicator_outlook, null);
			return view;
		} else if (text.compareTo("bookmark") == 0) {
			view = LayoutInflater.from(this).inflate(
					R.layout.tab_indicator_bookmark, null);
			return view;
		}
		return null;
	}

	private ListView initOutLookListView(ListView list) {

		if (mOutlineData.isDataNull()) {
			return list;
		}

		mOutLookListItem = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < mOutlineData.getData().length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemText", mOutlineData.getData()[i].title);
			mOutLookListItem.add(map);
		}
		mOutLookListItemAdapter = new SimpleAdapter(this, mOutLookListItem,
				R.layout.item_text, new String[] { "ItemText" },
				new int[] { R.id.item_text });
		list.setAdapter(mOutLookListItemAdapter);

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView text = (TextView) arg1;
				text.setTextColor(Color.rgb(0, 0, 255));
				Uri uri = Uri.parse(mOutlineData.getData()[arg2].index);
				Intent intent = new Intent();
				intent.setData(uri);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		return list;
	}

	private ListView initBookMarkOutListView(ListView list) {

		mBookMarkListItem = new ArrayList<HashMap<String, Object>>();
		List<BookmarkItem> rs = markDbAdapter.getall();
		for (int i = 0; i < rs.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemText", rs.get(i).markDescribe);
			map.put("index", rs.get(i).mIndex);
			mBookMarkListItem.add(map);
		}

		mBookMarkListItemAdapter = new SimpleAdapter(this, mBookMarkListItem,
				R.layout.item_text, new String[] { "ItemText" },
				// TextView ID
				new int[] { R.id.item_text });

		list.setAdapter(mBookMarkListItemAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView text = (TextView) arg1;
				text.setTextColor(Color.rgb(0, 0, 255));
				Uri uri = Uri.parse(mBookMarkListItem.get(arg2).get("index")
						.toString());
				Intent intent = new Intent();
				intent.setData(uri);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				final int index = arg2;
				AlertDialog alert = new AlertDialog.Builder(
						OutLookActivity.this).setTitle("确认")
						.setMessage("确定要删除该节点吗？")
						.setPositiveButton("是", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								if (markDbAdapter.delete(mBookMarkListItem
										.get(index).get("index").toString())) {
									mBookMarkListItem.remove(index);
									mBookMarkListItemAdapter
											.notifyDataSetChanged();
								}
							}
						}).setNegativeButton("否", null).show();
				return true;
			}

		});
		return list;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		markDbAdapter.close();
		super.onDestroy();
	}
}
