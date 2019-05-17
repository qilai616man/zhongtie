package org.geometerplus.android.fbreader.extBkMarkDB;

import java.util.List;

import org.geometerplus.zlibrary.ui.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BookMarkAdapter extends BaseAdapter {

	private List<BookMark> bookMarks; // 绑定的数据集
	private int source; // 资源文件
	private LayoutInflater inflater; // 布局填充器，Android的内置服务，作用：使用xml文件来生成对应的view对象
	private Context mContext;

	public BookMarkAdapter(Context context, List<BookMark> bookMarks, int source) {
		this.bookMarks = bookMarks;
		this.source = source;
		// 得到布局填充服务
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
	}

	public void setBookMarks(List<BookMark> bookMarks) {
		this.bookMarks = bookMarks;
	}

	public int getCount() {
		return bookMarks.size();
	}

	public Object getItem(int arg0) {
		return bookMarks.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	// 取得代表条目的view对象
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		TextView textView = null;

		// 判断是否为第一页
		// 提供缓存机制
		if (arg1 == null) {
			// 为条目创建View对象,生成条目界面对象
			arg1 = inflater.inflate(source, arg2, false);

			// 得到当前条目的数据
			textView = (TextView) arg1.findViewById(R.id.toc_tree_item_text);

			ViewCache cache = new ViewCache();

			cache.textView = textView;

			// 用视图标识临时存放缓存数据
			arg1.setTag(cache);
		} else {
			ViewCache cache = (ViewCache) arg1.getTag();
			textView = cache.textView;
		}

		// 得到当前条目对象
		BookMark bk = bookMarks.get(arg0);

		// 为当前条目赋值
		textView.setText(bk.getContent());
		textView.setTextColor(mContext.getResources().getColor(
				R.color.text_unselected));

		return arg1;
	}

	private final class ViewCache {
		public TextView textView;
	}
}