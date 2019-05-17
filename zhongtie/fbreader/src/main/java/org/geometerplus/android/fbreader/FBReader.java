/*
 * Copyright (C) 2009-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.brainsoon.utils.BookUtils;

import org.geometerplus.android.fbreader.api.ApiListener;
import org.geometerplus.android.fbreader.api.ApiServerImplementation;
import org.geometerplus.android.fbreader.api.PluginApi;
import org.geometerplus.android.fbreader.extBkMarkDB.BookMark;
import org.geometerplus.android.fbreader.extBkMarkDB.BookMarkDBHelper;
import org.geometerplus.android.fbreader.library.BookInfoActivity;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.android.util.UIUtil;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.book.SerializerUtil;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.CancelMenuHelper;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.library.ZLibrary;
import org.geometerplus.zlibrary.text.view.ZLTextPosition;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.application.ZLAndroidApplicationWindow;
import org.geometerplus.zlibrary.ui.android.library.UncaughtExceptionHandler;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;
import org.geometerplus.zlibrary.ui.android.view.AndroidFontUtil;
import org.geometerplus.zlibrary.ui.android.view.ZLAndroidWidget;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public final class FBReader extends Activity {
	public static final String ACTION_OPEN_BOOK = "android.fbreader.action.VIEW";
	public static final String BOOK_KEY = "fbreader.book";
	public static final String BOOKMARK_KEY = "fbreader.bookmark";

	static final int ACTION_BAR_COLOR = Color.DKGRAY;

	public static final int REQUEST_PREFERENCES = 1;
	public static final int REQUEST_CANCEL_MENU = 2;

	public static final int RESULT_DO_NOTHING = RESULT_FIRST_USER;
	public static final int RESULT_REPAINT = RESULT_FIRST_USER + 1;

	public static void openBookActivity(Context context, Book book,
			Bookmark bookmark) {
		context.startActivity(new Intent(context, FBReader.class)
				.setAction(ACTION_OPEN_BOOK)
				.putExtra(BOOK_KEY, SerializerUtil.serialize(book))
				.putExtra(BOOKMARK_KEY, SerializerUtil.serialize(bookmark))
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
	}

	private static ZLAndroidLibrary getZLibrary() {
		return (ZLAndroidLibrary) ZLAndroidLibrary.Instance();
	}

	private FBReaderApp myFBReaderApp;
	private volatile Book myBook;

	private RelativeLayout myRootView;
	private ZLAndroidWidget myMainView;

	private boolean myShowStatusBarFlag;
	private boolean myShowActionBarFlag;
	private boolean myActionBarIsVisible;

	private View vEditMarkMenu;
	private PopupWindow pwEditMark;
	private View cancelView;
	private Button markEditOk;
	private Button markEditCancel;
	private EditText markEditInfo;
	private TextView markEditTitle;
	// private static Drawable imgAnnotateSelected;
	// private static Drawable imgAnnotate;

	private static final String PLUGIN_ACTION_PREFIX = "___";
	private final List<PluginApi.ActionInfo> myPluginActions = new LinkedList<PluginApi.ActionInfo>();
	private final BroadcastReceiver myPluginInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final ArrayList<PluginApi.ActionInfo> actions = getResultExtras(
					true).<PluginApi.ActionInfo> getParcelableArrayList(
					PluginApi.PluginInfo.KEY);
			if (actions != null) {
				synchronized (myPluginActions) {
					int index = 0;
					while (index < myPluginActions.size()) {
						myFBReaderApp.removeAction(PLUGIN_ACTION_PREFIX
								+ index++);
					}
					myPluginActions.addAll(actions);
					index = 0;
					for (PluginApi.ActionInfo info : myPluginActions) {
						myFBReaderApp.addAction(PLUGIN_ACTION_PREFIX + index++,
								new RunPluginAction(FBReader.this,
										myFBReaderApp, info.getId()));
					}
					if (!myPluginActions.isEmpty()) {
						invalidateOptionsMenu();
					}
				}
			}
		}
	};

	private synchronized void openBook(Intent intent, Runnable action,
			boolean force) {
		if (!force && myBook != null) {
			return;
		}

		myBook = SerializerUtil
				.deserializeBook(intent.getStringExtra(BOOK_KEY));
		final Bookmark bookmark = SerializerUtil.deserializeBookmark(intent
				.getStringExtra(BOOKMARK_KEY));
		if (myBook == null) {
			final Uri data = intent.getData();
			if (data != null) {
				myBook = createBookForFile(ZLFile.createFileByPath(data
						.getPath()));
			}
		}
		if (null != myBook) {
			// 加入解密product.xml ro节点的密钥
			String aesKey = intent.getStringExtra("aesKey");
			Log.d("进入EPUB阅读器，密钥为:", aesKey + "");
			// if (!"".equals(aesKey)) {
			myBook.setAesKey(aesKey);
			// }
		}

		myFBReaderApp.openBook(myBook, bookmark, action);
	}

	private Book createBookForFile(ZLFile file) {
		if (file == null) {
			return null;
		}
		Book book = myFBReaderApp.Collection.getBookByFile(file);
		if (book != null) {
			return book;
		}
		if (file.isArchive()) {
			for (ZLFile child : file.children()) {
				book = myFBReaderApp.Collection.getBookByFile(child);
				if (book != null) {
					return book;
				}
			}
		}
		return null;
	}

	private Runnable getPostponedInitAction() {
		return new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						DictionaryUtil.init(FBReader.this);
					}
				});
			}
		};
	}

	@Override
	protected void onCreate(Bundle icicle) {
		Log.d("打开epub阅读器", "onCreate");
		super.onCreate(icicle);

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(
				this));
		// 禁止系统休眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		final ZLAndroidLibrary zlibrary = getZLibrary();
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				zlibrary.ShowStatusBarOption.getValue() ? 0
						: WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (!zlibrary.ShowActionBarOption.getValue()) {
			requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		}
		setContentView(R.layout.fbreadermain);
		myRootView = (RelativeLayout) findViewById(R.id.root_view);
		myMainView = (ZLAndroidWidget) findViewById(R.id.main_view);
		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

		zlibrary.setActivity(this);
		myFBReaderApp = (FBReaderApp) FBReaderApp.Instance();
		if (myFBReaderApp == null) {
			myFBReaderApp = new FBReaderApp(new BookCollectionShadow());
		}
        Log.i("ccccccc","widget====1"+myFBReaderApp.getViewWidget()+"");
		if (null != myFBReaderApp.getViewWidget()) {
			Log.i("ccccccc","widget====2"+myFBReaderApp.getViewWidget()+"");
			myFBReaderApp.getViewWidget().reset();
		}

		getCollection().bindToService(this, null);
		myBook = null;

		final ZLAndroidApplication androidApplication = (ZLAndroidApplication) getApplication();
		//gy 0922 if判断注释  只是为了保证myFBReaderApp的mywindow不为空
//		if (androidApplication.myMainWindow == null) {
			androidApplication.myMainWindow = new ZLAndroidApplicationWindow(
					myFBReaderApp);
			myFBReaderApp.initWindow();
//		}

		myShowStatusBarFlag = zlibrary.ShowStatusBarOption.getValue();
		// myShowActionBarFlag = zlibrary.ShowActionBarOption.getValue();
		myShowActionBarFlag = zlibrary.ShowActionBarOption.getValue();
		myActionBarIsVisible = myShowActionBarFlag;

//		final ActionBar bar = getActionBar();
//		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
//				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_TITLE);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//			bar.setDisplayUseLogoEnabled(false);
//		}

		// setTitle(myFBReaderApp.getTitle());

		if (myFBReaderApp.getPopupById(TextSearchPopup.ID) == null) {
			new TextSearchPopup(myFBReaderApp);
		}

		// 添加编辑菜单
		if (myFBReaderApp.getPopupById(SelectionPopup.ID) == null) {
			new SelectionPopup(myFBReaderApp);
		}

		myFBReaderApp.addAction(ActionCode.SHOW_LIBRARY, new ShowLibraryAction(
				this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_PREFERENCES,
				new ShowPreferencesAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_BOOK_INFO,
				new ShowBookInfoAction(this, myFBReaderApp));
		// 屏蔽原有书目
		// myFBReaderApp.addAction(ActionCode.SHOW_TOC, new ShowTOCAction(this,
		// myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_BOOKMARKS,
				new ShowBookmarksAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.PROCESS_HYPERLINK,
				new ProcessHyperlinkAction(this, myFBReaderApp));

		// 隐藏工具栏
		myFBReaderApp.addAction(ActionCode.TOGGLE_BARS, new ToggleBarsAction(
				this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SEARCH, new SearchAction(this,
				myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHARE_BOOK, new ShareBookAction(
				this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SELECTION_SHOW_PANEL,
				new SelectionShowPanelAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_HIDE_PANEL,
				new SelectionHidePanelAction(this, myFBReaderApp));
		
		myFBReaderApp.addAction(ActionCode.SELECTION_COPY_TO_CLIPBOARD,
				new SelectionCopyAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_SHARE,
				new SelectionShareAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_TRANSLATE,
				new SelectionTranslateAction(this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SELECTION_BOOKMARK,
				new SelectionBookmarkAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SHOW_CANCEL_MENU,
				new ShowCancelMenuAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_SYSTEM,
				new SetScreenOrientationAction(this, myFBReaderApp,
						ZLibrary.SCREEN_ORIENTATION_SYSTEM));
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_SENSOR,
				new SetScreenOrientationAction(this, myFBReaderApp,
						ZLibrary.SCREEN_ORIENTATION_SENSOR));
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_PORTRAIT,
				new SetScreenOrientationAction(this, myFBReaderApp,
						ZLibrary.SCREEN_ORIENTATION_PORTRAIT));
		myFBReaderApp.addAction(ActionCode.SET_SCREEN_ORIENTATION_LANDSCAPE,
				new SetScreenOrientationAction(this, myFBReaderApp,
						ZLibrary.SCREEN_ORIENTATION_LANDSCAPE));
		if (ZLibrary.Instance().supportsAllOrientations()) {
			myFBReaderApp.addAction(
					ActionCode.SET_SCREEN_ORIENTATION_REVERSE_PORTRAIT,
					new SetScreenOrientationAction(this, myFBReaderApp,
							ZLibrary.SCREEN_ORIENTATION_REVERSE_PORTRAIT));
			myFBReaderApp.addAction(
					ActionCode.SET_SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
					new SetScreenOrientationAction(this, myFBReaderApp,
							ZLibrary.SCREEN_ORIENTATION_REVERSE_LANDSCAPE));
		}
//		 myFBReaderApp.addAction(ActionCode.OPEN_WEB_HELP, new
//		 OpenWebHelpAction(this, myFBReaderApp));
		BookUtils.mFBContext = FBReader.this;
		LayoutInflater inflater = LayoutInflater.from(this);
		// 创建控制工具
		createMarkHint(inflater);
		createTools(inflater);

		vEditMarkMenu = inflater.inflate(R.layout.edit_book_mark, null);
		cancelView =vEditMarkMenu.findViewById(R.id.cancelView);
		markEditOk = (Button) vEditMarkMenu.findViewById(R.id.btn_ok);
		markEditCancel = (Button) vEditMarkMenu.findViewById(R.id.btn_cancel);
		markEditInfo = (EditText) vEditMarkMenu
				.findViewById(R.id.txt_mark_info);
		markEditTitle = (TextView) vEditMarkMenu
				.findViewById(R.id.txt_bkmark_title);
		markEditOk.setOnClickListener(markListener);
		markEditCancel.setOnClickListener(markListener);
		cancelView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pwEditMark.dismiss();
			}
		});
		pwEditMark = new PopupWindow(vEditMarkMenu, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
//		 imgAnnotateSelected = getResources().getDrawable(
//		 R.drawable.annotate_selected);
//		 imgAnnotate = getResources().getDrawable(R.drawable.annotate);

//		markEditInfo.setOnLongClickListener(new OnLongClickListener() {
//			@Override
//			public boolean onLongClick(View v) {
//				ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//				markEditInfo.setText(cmb.getText().toString().trim());
//				return false;
//			}
//		});

	}


	/**
	 * 添加批注
	 */
	private OnClickListener markListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Button btn = (Button) v;
			int id = btn.getId();
			if (id == R.id.btn_ok) {
				String content = markEditInfo.getText().toString();
				if (null == content || content.equals("")) {
					showMsg("请输入批注信息!");
					return;
				}
				saveMarkInfo(content);
			}
			if (pwEditMark.isShowing()) {
				// 隐藏窗口
				pwEditMark.dismiss();
			}
		}
	};

	public void saveMarkInfo(String content) {
		BookModel model = myFBReaderApp.Model;
		if (null != model) {
			BookMark bk = getCurPageBookMark();
			if (null == bk) {
				ZLTextPosition position = myFBReaderApp.getTextView()
						.getStartCursor();
				TOCTree toc = myFBReaderApp.getCurrentTOCElement();

				int paragraphIndex = position.getParagraphIndex();
				if (null != toc) {
					paragraphIndex = toc.getReference().ParagraphIndex;
				}
				int elementIndex = position.getElementIndex();
				int charIndex = position.getCharIndex();
				// Log.d("保存批注",paragraphIndex +"=======" + elementIndex +
				// "======" + charIndex);
				bk = new BookMark();
				Long bookId = model.Book.getId();
				bk.setContent(content);
				bk.setCreateTime(new Date());
				bk.setBookId(bookId);
				bk.setParagraphIndex(paragraphIndex);
				bk.setElementIndex(elementIndex);
				bk.setCharIndex(charIndex);
				bk.setStartParagraphIndex(position.getParagraphIndex());
				BookMarkDBHelper.getInstance(getApplicationContext())
						.addBookMark(bk);
			} else {
				BookMarkDBHelper.getInstance(getApplicationContext())
						.updateContentById(bk.get_id(), content);
			}
		}
	}

	private void showMsg(String msg) {
		Toast msgToast = Toast.makeText(FBReader.this, msg, Toast.LENGTH_SHORT);
		msgToast.setGravity(Gravity.CENTER, 0, 0);
		msgToast.show();
	}

	private ViewAnimator mTopTools;
	private ImageView mToolMinify;
	private ImageView mToolBookmark;

	/**
	 * 自定义工具栏
	 * 
	 * @param inflater
	 */
	private void createTools(LayoutInflater inflater) {
		View mToolsView = inflater.inflate(R.layout.tools, null);
		mTopTools = (ViewAnimator) mToolsView.findViewById(R.id.top_tools);
		mToolMinify = (ImageView) mToolsView.findViewById(R.id.btn_minify);
		ImageView mToolEnlarge = (ImageView) mToolsView
				.findViewById(R.id.btn_enlarge);
		ImageView mToolSearch = (ImageView) mToolsView
				.findViewById(R.id.btn_search);
		mToolBookmark = (ImageView) mToolsView.findViewById(R.id.btn_bookmark);
		ImageView mToolDirectory = (ImageView) mToolsView
				.findViewById(R.id.btn_directory);
		ImageView mToolBack = (ImageView) mToolsView
				.findViewById(R.id.btn_back);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		// 设置出现的位置(悬浮于顶部)
		params.topMargin = 0;
		params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		addContentView(mToolsView, params);
		mTopTools.setVisibility(View.INVISIBLE);

		// 绑定事件
		mToolMinify.setOnClickListener(myToolsListener);
		mToolEnlarge.setOnClickListener(myToolsListener);
		mToolSearch.setOnClickListener(myToolsListener);
		mToolBookmark.setOnClickListener(myToolsListener);
		mToolBookmark.setOnLongClickListener(myToolsLongListener);
		mToolDirectory.setOnClickListener(myToolsListener);
		mToolBack.setOnClickListener(myToolsListener);

	}

	private ImageButton mBtnMkHint;
	private TextView mTxtMkHintContent;

	/**
	 * 页内提示
	 * 
	 * @param inflater
	 */
	private void createMarkHint(LayoutInflater inflater) {
		View mMkHintView = inflater.inflate(R.layout.bookmark_hint, null);
		mBtnMkHint = (ImageButton) mMkHintView
				.findViewById(R.id.btn_mark_show_hint);
		mTxtMkHintContent = (TextView) mMkHintView
				.findViewById(R.id.txt_mark_hint);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		// 设置出现的位置(悬浮于顶部)
		// params.topMargin = 0;
		// params.gravity = Gravity.TOP | Gravity.RIGHT;
		addContentView(mMkHintView, params);
		mBtnMkHint.setVisibility(View.INVISIBLE);
		mTxtMkHintContent.setVisibility(View.INVISIBLE);
		mBtnMkHint.setOnClickListener(hitListener);
	}

	public OnClickListener hitListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int vs = mTxtMkHintContent.getVisibility();
			if (vs == View.VISIBLE) {
				mTxtMkHintContent.setVisibility(View.INVISIBLE);
			} else {
				mTxtMkHintContent.setVisibility(View.VISIBLE);
			}

		}
	};

	public ZLAndroidWidget getMainView() {
		return myMainView;
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		Log.d("FBReader", "onNewIntent");
		final String action = intent.getAction();
		final Uri data = intent.getData();

		if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
			super.onNewIntent(intent);
		} else if (Intent.ACTION_VIEW.equals(action) && data != null
				&& "fbreader-action".equals(data.getScheme())) {
			myFBReaderApp.runAction(data.getEncodedSchemeSpecificPart(),
					data.getFragment());
		} else if (Intent.ACTION_VIEW.equals(action)
				|| ACTION_OPEN_BOOK.equals(action)) {
			getCollection().bindToService(this, new Runnable() {
				public void run() {
					openBook(intent, null, true);
				}
			});
		} else if (Intent.ACTION_SEARCH.equals(action)) {
			final String pattern = intent.getStringExtra(SearchManager.QUERY);
			final Runnable runnable = new Runnable() {
				public void run() {
					final TextSearchPopup popup = (TextSearchPopup) myFBReaderApp
							.getPopupById(TextSearchPopup.ID);
					popup.initPosition();
					myFBReaderApp.TextSearchPatternOption.setValue(pattern);
					if (myFBReaderApp.getTextView().search(pattern, true,
							false, false, false) != 0) {
						runOnUiThread(new Runnable() {
							public void run() {
								myFBReaderApp.showPopup(popup.getId());
								hideBars();
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								UIUtil.showErrorMessage(FBReader.this,
										"textNotFound");
								popup.StartPosition = null;
							}
						});
					}
				}
			};
			UIUtil.wait("search", runnable, this);
		} else {
			super.onNewIntent(intent);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		getCollection().bindToService(this, new Runnable() {
			public void run() {
				new Thread() {
					public void run() {
						openBook(getIntent(), getPostponedInitAction(), false);
						Log.i("ccccccc","widget====3"+myFBReaderApp.getViewWidget()+"");
						myFBReaderApp.getViewWidget().repaint();
					}
				}.start();

				myFBReaderApp.getViewWidget().repaint();
			}
		});

		initPluginActions();

		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary) ZLibrary
				.Instance();

		if (zlibrary.ShowStatusBarOption.getValue() != myShowStatusBarFlag
				|| zlibrary.ShowActionBarOption.getValue() != myShowActionBarFlag) {
			finish();
			startActivity(new Intent(this, getClass()));
		}

		SetScreenOrientationAction.setOrientation(this, zlibrary
				.getOrientationOption().getValue());

		((PopupPanel) myFBReaderApp.getPopupById(TextSearchPopup.ID))
				.setPanelInfo(this, myRootView);
		((PopupPanel) myFBReaderApp.getPopupById(SelectionPopup.ID))
				.setPanelInfo(this, myRootView);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		switchWakeLock(hasFocus
				&& getZLibrary().BatteryLevelToTurnScreenOffOption.getValue() < myFBReaderApp
						.getBatteryLevel());
	}

	private void initPluginActions() {
		synchronized (myPluginActions) {
			if (!myPluginActions.isEmpty()) {
				int index = 0;
				while (index < myPluginActions.size()) {
					myFBReaderApp.removeAction(PLUGIN_ACTION_PREFIX + index++);
				}
				myPluginActions.clear();
				invalidateOptionsMenu();
			}
		}

		sendOrderedBroadcast(
				new Intent(PluginApi.ACTION_REGISTER)
						.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES),
				null, myPluginInfoReceiver, null, RESULT_OK, null, null);
	}

	@Override
	protected void onResume() {
		super.onResume();

		myStartTimer = true;
		final int brightnessLevel = getZLibrary().ScreenBrightnessLevelOption
				.getValue();
		if (brightnessLevel != 0) {
			setScreenBrightness(brightnessLevel);
		} else {
			setScreenBrightnessAuto();
		}
		if (getZLibrary().DisableButtonLightsOption.getValue()) {
			setButtonLight(false);
		}

		registerReceiver(myBatteryInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		PopupPanel.restoreVisibilities(myFBReaderApp);

		hideBars();

		ApiServerImplementation.sendEvent(this,
				ApiListener.EVENT_READ_MODE_OPENED);

		getCollection().bindToService(this, new Runnable() {
			public void run() {
				final BookModel model = myFBReaderApp.Model;
				if (model == null || model.Book == null) {
					return;
				}
				onPreferencesUpdate(myFBReaderApp.Collection
						.getBookById(model.Book.getId()));
			}
		});
	}

	@Override
	protected void onPause() {
		try {
			unregisterReceiver(myBatteryInfoReceiver);
		} catch (IllegalArgumentException e) {
			// do nothing, this exception means myBatteryInfoReceiver was not
			// registered
		}
		myFBReaderApp.stopTimer();
		if (getZLibrary().DisableButtonLightsOption.getValue()) {
			setButtonLight(true);
		}
		myFBReaderApp.onWindowClosing();
		super.onPause();
	}

	@Override
	protected void onStop() {
		ApiServerImplementation.sendEvent(this,
				ApiListener.EVENT_READ_MODE_CLOSED);
		PopupPanel.removeAllWindows(myFBReaderApp, this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d("退出epub", "onDestroy");
		getCollection().removeBook(myBook, false);
		getCollection().unbind();
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		myFBReaderApp.onWindowClosing();
		super.onLowMemory();
	}

	@Override
	public boolean onSearchRequested() {
		final FBReaderApp.PopupPanel popup = myFBReaderApp.getActivePopup();
		myFBReaderApp.hideActivePopup();
		final SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
		manager.setOnCancelListener(new SearchManager.OnCancelListener() {
			public void onCancel() {
				if (popup != null) {
					myFBReaderApp.showPopup(popup.getId());
				}
				manager.setOnCancelListener(null);
			}
		});
		startSearch(myFBReaderApp.TextSearchPatternOption.getValue(), true,
				null, false);
		return true;
	}

	/**
	 * 显示选中区域
	 */
	public void showSelectionPanel() {
		final ZLTextView view = myFBReaderApp.getTextView();
		((SelectionPopup) myFBReaderApp.getPopupById(SelectionPopup.ID)).move(
				view.getSelectionStartY(), view.getSelectionEndY());
		myFBReaderApp.showPopup(SelectionPopup.ID);
		hideBars();
	}

	public void hideSelectionPanel() {
		final FBReaderApp.PopupPanel popup = myFBReaderApp.getActivePopup();
		if (popup != null && popup.getId() == SelectionPopup.ID) {
			myFBReaderApp.hideActivePopup();
		}
	}

	private void onPreferencesUpdate(Book book) {
		AndroidFontUtil.clearFontCache();
		myFBReaderApp.onBookUpdated(book);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_PREFERENCES:
			if (resultCode != RESULT_DO_NOTHING) {
				invalidateOptionsMenu();
				final Book book = BookInfoActivity.bookByIntent(data);
				if (book != null) {
					getCollection().bindToService(this, new Runnable() {
						public void run() {
							onPreferencesUpdate(book);
						}
					});
				}
			}
			break;
		case REQUEST_CANCEL_MENU:
			runCancelAction(data);
			break;
		}
	}

	private void runCancelAction(Intent intent) {
		myFBReaderApp.runCancelAction(CancelMenuHelper.ActionType.close, null);
	}

	private Menu addSubMenu(Menu menu, String id) {
		final ZLAndroidApplication application = (ZLAndroidApplication) getApplication();
		return application.myMainWindow.addSubMenu(menu, id);
	}

	@SuppressWarnings("unused")
	private void addMenuItem(Menu menu, String actionId, String name) {
		final ZLAndroidApplication application = (ZLAndroidApplication) getApplication();
		application.myMainWindow.addMenuItem(menu, actionId, null, name, false,
				false);
	}

	/**
	 * 添加菜单
	 * 
	 * @param menu
	 * @param actionId
	 * @param iconId
	 * @param always
	 *            是否总是显示
	 */
	private void addMenuItem(Menu menu, String actionId, int iconId,
			boolean always) {
		final ZLAndroidApplication application = (ZLAndroidApplication) getApplication();
		application.myMainWindow.addMenuItem(menu, actionId, iconId, null,
				myActionBarIsVisible, always);
	}

	private void addMenuItem(Menu menu, String actionId) {
		final ZLAndroidApplication application = (ZLAndroidApplication) getApplication();
		application.myMainWindow.addMenuItem(menu, actionId, null, null, false,
				false);
	}

	private void setupMenu(Menu menu) {
		// addMenuItem(menu, ActionCode.SHOW_TOC,
		// R.drawable.ic_menu_toc,false);//目录
		addMenuItem(menu, ActionCode.SHOW_BOOKMARKS,
				R.drawable.ic_menu_bookmarks, true);// 书签
		addMenuItem(menu, ActionCode.SWITCH_TO_NIGHT_PROFILE,
				R.drawable.ic_menu_night, false);// 黑底白字
		addMenuItem(menu, ActionCode.SWITCH_TO_DAY_PROFILE,
				R.drawable.ic_menu_day, false);// 白底黑字
		addMenuItem(menu, ActionCode.SHOW_PREFERENCES);// 阅读相关设置
		addMenuItem(menu, ActionCode.SHOW_BOOK_INFO);// 书籍信息
		// final Menu subMenu = addSubMenu(menu, "screenOrientation");//屏幕方向
		// addMenuItem(subMenu, ActionCode.SET_SCREEN_ORIENTATION_SYSTEM);
		// addMenuItem(subMenu, ActionCode.SET_SCREEN_ORIENTATION_SENSOR);
		// addMenuItem(subMenu, ActionCode.SET_SCREEN_ORIENTATION_PORTRAIT);
		// addMenuItem(subMenu, ActionCode.SET_SCREEN_ORIENTATION_LANDSCAPE);
		// if (ZLibrary.Instance().supportsAllOrientations()) {
		// addMenuItem(subMenu,
		// ActionCode.SET_SCREEN_ORIENTATION_REVERSE_PORTRAIT);
		// addMenuItem(subMenu,
		// ActionCode.SET_SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
		// }

		final ZLAndroidApplication application = (ZLAndroidApplication) getApplication();
		application.myMainWindow.refresh();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		setupMenu(menu);

		return true;
	}

	private NavigationPopup myNavigationPopup;

	boolean barsAreShown() {
		return myNavigationPopup != null;
	}

	private void setStatusBarVisibility(boolean visible) {
		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary) ZLibrary
				.Instance();
		if (!zlibrary.isKindleFire()
				&& !zlibrary.ShowStatusBarOption.getValue()) {
			myMainView.setPreserveSize(visible);
			if (visible) {
				getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
			} else {
				getWindow()
						.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			}
		}
	}

	/**
	 * 隐藏工具栏
	 */
	void hideBars() {
		if (myNavigationPopup != null) {
			myNavigationPopup.stopNavigation();
			myNavigationPopup = null;
		}

		final ZLAndroidLibrary zlibrary = (ZLAndroidLibrary) ZLibrary
				.Instance();
		if (!zlibrary.ShowActionBarOption.getValue()) {
//			getActionBar().hide();
//			myActionBarIsVisible = false;
			mTopTools.setVisibility(View.INVISIBLE);
			// mPageTools.setVisibility(View.INVISIBLE);
		}

		if (zlibrary.DisableButtonLightsOption.getValue()) {
			myRootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		}
		setStatusBarVisibility(false);
	}

	/**
	 * 显示工具栏
	 */
	void showBars() {
		setStatusBarVisibility(true);
		// refreshToolBookmark();
		myActionBarIsVisible = true;
		mTopTools.setVisibility(View.VISIBLE);
		myRootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

		if (myNavigationPopup == null) {
			myFBReaderApp.hideActivePopup();
			myNavigationPopup = new NavigationPopup(myFBReaderApp);
			myNavigationPopup.runNavigation(this, myRootView);
		}
	}

	/**
	 * 获取当前页的批注
	 * 
	 * @return
	 */
	public BookMark getCurPageBookMark() {
		BookModel model = myFBReaderApp.Model;
		BookMark bk = null;
		if (null != model) {
			Long bookId = model.Book.getId();
			TOCTree toc = myFBReaderApp.getCurrentTOCElement();
			ZLTextPosition start = myFBReaderApp.getTextView().getStartCursor();

			int paragraphIndex = 0;
			if (null != toc) {
				paragraphIndex = toc.getReference().ParagraphIndex;
			}
			int startParagraphIndex = start.getParagraphIndex();
			// 获取当前章的索引
			int startElementIndex = start.getElementIndex();
			int endElementIndex = myFBReaderApp.getTextView().getEndCursor()
					.getElementIndex();
			// Log.d("索引", "paragraphIndex:"+
			// paragraphIndex+"   startElementIndex:"+startElementIndex+"   endElementIndex:"+endElementIndex+"  start:"+startParagraphIndex);

			List<BookMark> bkms = BookMarkDBHelper.getInstance(
					getApplicationContext()).getBookMarkByParagraphIndex(
					bookId, paragraphIndex);
			if (null != bkms && bkms.size() > 0) {
				// 判断当前区域是否包含此段落的批注
				if (startElementIndex == 0 && endElementIndex == 0) {
					// 因为目前只显示一个，所以获取最后一个
					bk = bkms.get(bkms.size() - 1);
				} else {
					for (BookMark bookMark : bkms) {
						int curElementIndex = bookMark.getElementIndex();
						int curStartParagraphIndex = bookMark
								.getStartParagraphIndex();
						// Log.d("索引",
						// "curElementIndex:"+curElementIndex+"  ,  curStartParagraphIndex:"+curStartParagraphIndex);
						if ((endElementIndex == 0 || endElementIndex > curElementIndex)
								&& startElementIndex <= curElementIndex
								&& startParagraphIndex <= curStartParagraphIndex) {
							bk = bookMark;
						}
					}
				}
			}
		}
		return bk;
	}

	/**
	 * 刷新批注的图标
	 */
	// public void refreshToolBookmark() {
	// if (mTopTools.getVisibility() == View.VISIBLE) {
	// // 查询是否有批注,有需要改变批注图片
	// BookMark mk = getCurPageBookMark();
	// if (null != mk) {
	// mToolBookmark.setImageDrawable(imgAnnotateSelected);
	// } else {
	// mToolBookmark.setImageDrawable(imgAnnotate);
	// }
	// }
	// }

	public void refreshBookmarkHint() {
		// Log.d("更新提示批注", "bg");
		// 当工具栏隐藏的时候再显示
		if (myNavigationPopup == null) {
			// 查询是否有批注,有需要改变批注图片
			BookMark mk = getCurPageBookMark();
			if (null != mk) {
				mTxtMkHintContent.setText(mk.getContent());
				if (mBtnMkHint.getVisibility() == View.INVISIBLE) {
					mBtnMkHint.setVisibility(View.VISIBLE);
					// mTxtMkHintContent.setVisibility(View.VISIBLE);
				}
			} else {
				if (mBtnMkHint.getVisibility() == View.VISIBLE) {
					mBtnMkHint.setVisibility(View.INVISIBLE);
					mTxtMkHintContent.setVisibility(View.INVISIBLE);
				}
			}
		} else {
			if (mBtnMkHint.getVisibility() == View.VISIBLE) {
				mBtnMkHint.setVisibility(View.INVISIBLE);
				mTxtMkHintContent.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * 刷新
	 */
	public void refresh() {
		if (myNavigationPopup != null) {
			// refreshToolBookmark();
			mBtnMkHint.setVisibility(View.INVISIBLE);
			mTxtMkHintContent.setVisibility(View.INVISIBLE);
			myNavigationPopup.update();
		} else {
			refreshBookmarkHint();
		}
	}

	/**
	 * 放大字体
	 */
	public void increaseFont() {
		myFBReaderApp.runAction(ActionCode.INCREASE_FONT);
	}

	/**
	 * 缩小字体
	 */
	public void decreaseFont() {
		myFBReaderApp.runAction(ActionCode.DECREASE_FONT);
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
//		final TextView view = (TextView) getActionBar().getCustomView();
//		if (view != null) {
//			view.setText(title);
//			view.postInvalidate();
//		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return (myMainView != null && myMainView.onKeyDown(keyCode, event))
				|| super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return (myMainView != null && myMainView.onKeyUp(keyCode, event))
				|| super.onKeyUp(keyCode, event);
	}

	private void setButtonLight(boolean enabled) {
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.buttonBrightness = enabled ? -1.0f : 0.0f;
		getWindow().setAttributes(attrs);
	}

	private PowerManager.WakeLock myWakeLock;
	private boolean myWakeLockToCreate;
	private boolean myStartTimer;

	public final void createWakeLock() {
		if (myWakeLockToCreate) {
			synchronized (this) {
				if (myWakeLockToCreate) {
					myWakeLockToCreate = false;
					myWakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
							.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
									"FBReader");
					myWakeLock.acquire();
				}
			}
		}
		if (myStartTimer) {
			myFBReaderApp.startTimer();
			myStartTimer = false;
		}
	}

	private final void switchWakeLock(boolean on) {
		if (on) {
			if (myWakeLock == null) {
				myWakeLockToCreate = true;
			}
		} else {
			if (myWakeLock != null) {
				synchronized (this) {
					if (myWakeLock != null) {
						myWakeLock.release();
						myWakeLock = null;
					}
				}
			}
		}
	}

	private BroadcastReceiver myBatteryInfoReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			final int level = intent.getIntExtra("level", 100);
			final ZLAndroidApplication application = (ZLAndroidApplication) getApplication();
			application.myMainWindow.setBatteryLevel(level);
			switchWakeLock(hasWindowFocus()
					&& getZLibrary().BatteryLevelToTurnScreenOffOption
							.getValue() < level);
		}
	};

	private void setScreenBrightnessAuto() {
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.screenBrightness = -1.0f;
		getWindow().setAttributes(attrs);
	}

	public void setScreenBrightness(int percent) {
		if (percent < 1) {
			percent = 1;
		} else if (percent > 100) {
			percent = 100;
		}
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.screenBrightness = percent / 100.0f;
		getWindow().setAttributes(attrs);
		getZLibrary().ScreenBrightnessLevelOption.setValue(percent);
	}

	public int getScreenBrightness() {
		final int level = (int) (100 * getWindow().getAttributes().screenBrightness);
		return (level >= 0) ? level : 50;
	}

	private BookCollectionShadow getCollection() {
		return (BookCollectionShadow) myFBReaderApp.Collection;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void finish() {
		super.finish();
		myFBReaderApp.closeWindow();
	}

	/**
	 * 工具栏点击事件
	 */
	public OnClickListener myToolsListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ImageView btn = (ImageView) v;
			int id = btn.getId();
			Log.d("点击工具栏", id + "");
			if (id == R.id.btn_minify) {
				myFBReaderApp.runAction(ActionCode.DECREASE_FONT);
			} else if (id == R.id.btn_enlarge) {
				myFBReaderApp.runAction(ActionCode.INCREASE_FONT);
			} else if (id == R.id.btn_search) {
				myFBReaderApp.runAction(ActionCode.SEARCH);
			} else if (id == R.id.btn_bookmark) {
				hideBars();
				BookMark bk = getCurPageBookMark();
				if (bk != null) {
					markEditTitle.setText("修改批注");
					markEditInfo.setText(bk.getContent());
				} else {
					markEditTitle.setText("添加批注");
					markEditInfo.setText("");
				}
				if (!pwEditMark.isShowing()) {
					pwEditMark.showAtLocation(mToolBookmark, Gravity.BOTTOM, 0,
							0);
					pwEditMark.showAsDropDown(vEditMarkMenu);
				}
			} else if (id == R.id.btn_directory) {
				Intent intent = new Intent();
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// intent.setData(Uri.fromFile(new File(bookPath)));
				intent.setClass(FBReader.this, BookCatalogActivity.class);
				startActivity(intent);
			} else if (id == R.id.btn_back) {
				finish();
			} else {
			}
		}
	};

	public OnLongClickListener myToolsLongListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			ImageView btn = (ImageView) v;
			int id = btn.getId();
			Log.d("长按工具栏", id + "");
			if (id == R.id.btn_bookmark) {
				BookMark bk = getCurPageBookMark();
				if (bk != null) {
					BookMarkDBHelper.getInstance(getApplicationContext())
							.deleteMarkById(bk.get_id());
					// refreshToolBookmark();
				}
			}
			return true;
		}
	};
}
