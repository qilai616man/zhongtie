package org.geometerplus.android.fbreader;

import java.util.ArrayList;
import java.util.List;

import org.geometerplus.android.fbreader.extBkMarkDB.BookMark;
import org.geometerplus.android.fbreader.extBkMarkDB.BookMarkAdapter;
import org.geometerplus.android.fbreader.extBkMarkDB.BookMarkDBHelper;
import org.geometerplus.android.util.ViewUtil;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.tree.ZLTree;
import org.geometerplus.zlibrary.text.view.ZLTextFixedPosition;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;
import org.geometerplus.zlibrary.ui.android.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * 
 * 书目
 * 
 * @author zuo
 * 
 */
public class BookCatalogActivity extends Activity {

	private LinearLayout linearLayoutTop;
	private ListView listCatalog;
	private ListView listAnnotate;
	private Button tabCatalog;
	private Button tabBookMark;
	private TOCAdapter myAdapter;
	private ZLTree<?> mySelectedItem;
	// private static Drawable catalogSelectTab;
	// private static Drawable catalogTab;
	// private static Drawable bookMarkSelectTab;
	// private static Drawable bookMarkTab;
	private BookMarkAdapter mkAdapter;
	private Long bookId;

	@SuppressWarnings("unused")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new org.geometerplus.zlibrary.ui.android.library.UncaughtExceptionHandler(
				this));
		setContentView(R.layout.book_catalog);
		listCatalog = (ListView) findViewById(R.id.list_catalog);
		listAnnotate = (ListView) findViewById(R.id.list_annotate);
		tabCatalog = (Button) findViewById(R.id.tab_catalog);
		tabBookMark = (Button) findViewById(R.id.tab_annotate);
		linearLayoutTop = (LinearLayout) findViewById(R.id.linearLayoutTop);

		// 注册事件
		tabCatalog.setOnClickListener(myListener);
		tabBookMark.setOnClickListener(myListener);

		listAnnotate.setVisibility(View.INVISIBLE);

		final FBReaderApp fbreader = (FBReaderApp) ZLApplication.Instance();

		final TOCTree root = fbreader.Model.TOCTree;
		myAdapter = new TOCAdapter(listCatalog, root);
		final ZLTextWordCursor cursor = fbreader.BookTextView.getStartCursor();
		int index = cursor.getParagraphIndex();
		if (cursor.isEndOfParagraph()) {
			++index;
		}
		TOCTree treeToSelect = fbreader.getCurrentTOCElement();
		myAdapter.selectItem(treeToSelect);
		mySelectedItem = treeToSelect;

		listCatalog.setAdapter(myAdapter);

		// 处理书签，批注
		List<BookMark> marks = new ArrayList<BookMark>();
		mkAdapter = new BookMarkAdapter(getApplicationContext(), marks,
				R.layout.toc_tree_item);
		// 添加并且显示
		listAnnotate.setAdapter(mkAdapter);
		// 注册事件
		listAnnotate.setOnItemClickListener(listAnnotateListener);
		listAnnotate.setOnItemLongClickListener(listAnnotateLongListener);

		// catalogSelectTab =
		// getResources().getDrawable(R.drawable.catalog_select_tab);
		// catalogTab = getResources().getDrawable(R.drawable.catalog_tab);
		// bookMarkSelectTab =
		// getResources().getDrawable(R.drawable.annotate_select_tab);
		// bookMarkTab = getResources().getDrawable(R.drawable.annotate_tab);

		bookId = fbreader.Model.Book.getId();
		Thread loginThread = new Thread(new getBookMarksThread(bookId));

		loginThread.start();

		LayoutInflater inflater = LayoutInflater.from(this);
		View mBackToolView = inflater.inflate(R.layout.catalog_back, null);
		ImageView backMain = (ImageView) mBackToolView
				.findViewById(R.id.btn_back_main);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		// //设置出现的位置(悬浮于顶部)
		// params.topMargin = 0;
		// params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		addContentView(mBackToolView, params);
		backMain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
	}

	private static final int PROCESS_TREE_ITEM_ID = 0;
	private static final int READ_BOOK_ITEM_ID = 1;

	public void openBookText(BookMark bk) {
		int startParagraphIndex = bk.getStartParagraphIndex();
		int elementIndex = bk.getElementIndex();
		final FBReaderApp fbreader = (FBReaderApp) ZLApplication.Instance();
		fbreader.addInvisibleBookmark();
		Log.d("跳转到:", "startParagraphIndex:" + startParagraphIndex
				+ " , elementIndex:" + elementIndex);
		ZLTextFixedPosition position = new ZLTextFixedPosition(
				startParagraphIndex, elementIndex, 0);
		fbreader.BookTextView.gotoPosition(position);
		// fbreader.BookTextView.gotoPosition(paragraphIndex, wordIndex, 0);
		fbreader.showBookTextView();
		// fbreader.setView(fbreader.BookTextView);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final int position = ((AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo()).position;
		final TOCTree tree = (TOCTree) myAdapter.getItem(position);
		switch (item.getItemId()) {
		case PROCESS_TREE_ITEM_ID:
			myAdapter.runTreeItem(tree);
			return true;
		case READ_BOOK_ITEM_ID:
			myAdapter.openBookText(tree);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private final class TOCAdapter extends ZLTreeAdapter {
		TOCAdapter(ListView listCatalog, TOCTree root) {
			super(listCatalog, root);
		}

		@Override
		public void onCreateContextMenu(ContextMenu menu, View view,
				ContextMenu.ContextMenuInfo menuInfo) {
			final int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
			final TOCTree tree = (TOCTree) getItem(position);
			if (tree.hasChildren()) {
				menu.setHeaderTitle(tree.getText());
				final ZLResource resource = ZLResource.resource("tocView");
				menu.add(
						0,
						PROCESS_TREE_ITEM_ID,
						0,
						resource.getResource(
								isOpen(tree) ? "collapseTree" : "expandTree")
								.getValue());
				menu.add(0, READ_BOOK_ITEM_ID, 0,
						resource.getResource("readText").getValue());
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final View view = (convertView != null) ? convertView
					: LayoutInflater.from(parent.getContext()).inflate(
							R.layout.toc_tree_item, parent, false);
			final TOCTree tree = (TOCTree) getItem(position);
			view.setBackgroundColor(0);
			int color = tree == mySelectedItem ? getResources().getColor(
					R.color.text_selected) : getResources().getColor(
					R.color.text_unselected);
			setIcon(ViewUtil.findImageView(view, R.id.toc_tree_item_icon), tree);
			// 设置书目信息
			ViewUtil.findTextView(view, R.id.toc_tree_item_text).setTextColor(
					color);
			ViewUtil.findTextView(view, R.id.toc_tree_item_text).setText(
					tree.getText());
			return view;
		}

		void openBookText(TOCTree tree) {
			final TOCTree.Reference reference = tree.getReference();
			if (reference != null) {
				finish();
				final FBReaderApp fbreader = (FBReaderApp) ZLApplication
						.Instance();
				fbreader.addInvisibleBookmark();
				int paragraphIndex = reference.ParagraphIndex;
				if (paragraphIndex > 1) {
					paragraphIndex = reference.ParagraphIndex - 1;
				}
				Log.d("跳转到ParagraphIndex:=", paragraphIndex + "");
				fbreader.BookTextView.gotoPosition(paragraphIndex, 0, 0);
				fbreader.showBookTextView();
			}
		}

		@Override
		protected boolean runTreeItem(ZLTree<?> tree) {
			if (super.runTreeItem(tree)) {
				return true;
			}
			openBookText((TOCTree) tree);
			return true;
		}
	}

	// 选项卡点击事件
	public OnClickListener myListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.tab_catalog) {
				// tabCatalog.setImageDrawable(catalogSelectTab);
				// tabBookMark.setImageDrawable(bookMarkTab);
				linearLayoutTop.setBackgroundResource(R.drawable.func_toc_show);
				tabCatalog.setTextColor(getResources().getColor(
						R.color.func_toc_active));
				tabBookMark.setTextColor(getResources().getColor(
						R.color.func_toc_normal));
				listCatalog.setVisibility(View.VISIBLE);
				listAnnotate.setVisibility(View.INVISIBLE);
			} else if (id == R.id.tab_annotate) {
				// tabCatalog.setImageDrawable(catalogTab);
				// tabBookMark.setImageDrawable(bookMarkSelectTab);
				linearLayoutTop
						.setBackgroundResource(R.drawable.func_annotation_show);
				tabBookMark.setTextColor(getResources().getColor(
						R.color.func_toc_active));
				tabCatalog.setTextColor(getResources().getColor(
						R.color.func_toc_normal));
				listCatalog.setVisibility(View.INVISIBLE);
				listAnnotate.setVisibility(View.VISIBLE);
			}
		}
	};

	/**
	 * 批注点击事件
	 * 
	 * @author zuo
	 */
	private OnItemClickListener listAnnotateListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			ListView lView = (ListView) arg0;
			if (null != lView) {
				BookMark bk = (BookMark) lView.getItemAtPosition(arg2);
				finish();
				if (null != bk) {
					// Log.d("批注点击",""+arg2);
					openBookText(bk);
				}
			}
		}
	};
	/**
	 * 长按批注事件
	 */
	private OnItemLongClickListener listAnnotateLongListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			ListView lView = (ListView) arg0;
			if (null != lView) {
				BookMark bk = (BookMark) lView.getItemAtPosition(arg2);
				if (null != bk) {
					Log.d("长按批注", "" + arg2);
					openDelMarkDialog(bk.get_id());
				}
			}
			return true;
		}
	};

	private void openDelMarkDialog(final Long id) {
		Builder builder = new Builder(BookCatalogActivity.this);
		builder.setMessage("您确定要删除该批注吗？");
		builder.setTitle("删除批注");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				BookMarkDBHelper.getInstance(getApplicationContext())
						.deleteMarkById(id);
				Thread loginThread = new Thread(new getBookMarksThread(bookId));
				loginThread.start();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}

	// 获取批注线程类
	class getBookMarksThread implements Runnable {
		Long bookId;

		public getBookMarksThread(Long bookId) {
			this.bookId = bookId;
		}

		@Override
		public void run() {
			List<BookMark> bookMarks = BookMarkDBHelper.getInstance(
					getApplicationContext()).getBookById(bookId);
			// Log.d("获取到批注大小：", bookMarks.size()+"");
			mkAdapter.setBookMarks(bookMarks);
			Message msg = refreshHandler.obtainMessage();
			msg.what = 0;
			refreshHandler.sendMessage(msg);
		}

	}

	@SuppressLint("HandlerLeak")
	Handler refreshHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mkAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}

		}
	};
}
