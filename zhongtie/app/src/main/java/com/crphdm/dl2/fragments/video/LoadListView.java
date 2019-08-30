package com.crphdm.dl2.fragments.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.crphdm.dl2.R;

/**
 * Created by szx1 on 2019/8/29.
 */

public class LoadListView extends ListView implements AbsListView.OnScrollListener{
    private int lastVisibleItem;//最后一个可见的item
               private int totalItemCount;//总的item
               private boolean isLoading = false;//是否正在加载数据
               private ILoadListener mListener;//回调接口，用来加载数据
    private View footer;

    public LoadListView(Context context) {
        super(context);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private void initView(Context context){
        footer = LayoutInflater.from(context).inflate(R.layout.listview_footer, null);
        this.addFooterView(footer);
        //footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
        this.setOnScrollListener(this);

    }
    //禁止ListView滑动
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    private float mLastY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        //重点在这里
        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                super.onInterceptTouchEvent(ev);
                //不允许上层viewGroup拦截事件.
                break;
            case MotionEvent.ACTION_MOVE:
                //满足listView滑动到顶部，如果继续下滑，那就让scrollView拦截事件
                if (getFirstVisiblePosition() == 0 && (ev.getY() - mLastY) > 0) {
                    //允许上层viewGroup拦截事件
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                //满足listView滑动到底部，如果继续上滑，那就让scrollView拦截事件
                else if (getLastVisiblePosition() == getCount() - 1 && (ev.getY() - mLastY) < 0) {
                    //允许上层viewGroup拦截事件
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    //不允许上层viewGroup拦截事件
                    //getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                //不允许上层viewGroup拦截事件
                //getParent().requestDisallowInterceptTouchEvent(true);
                break;
        }

        mLastY = ev.getY();
        return super.dispatchTouchEvent(ev);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }



    public interface ILoadListener{
          void loadData();
      }

    public void setOnILoadListener(ILoadListener listener){
        this.mListener = listener;
    }

    public void loadFinish(){
        isLoading = false;//不再加载了
        //底布局也要隐藏
        //footer.setVisibility(View.GONE);
    }

    public void loadStart(){
        isLoading = true;
        footer.findViewById(R.id.load_layout).setVisibility(View.VISIBLE);
        mListener.loadData();
        //设置底布局可见
    }



    public void onScrollStateChanged(AbsListView view, int scrollState) {
                 //如果最后一个可见item等于总的item，且当前滚动状态为滚动停止，就应该开始加加载数据了
        if(lastVisibleItem == totalItemCount && scrollState==SCROLL_STATE_IDLE){
            if(!isLoading){
                isLoading = true;
                //加载数据
                mListener.loadData();
                //设置底布局可见
                footer.findViewById(R.id.load_layout).setVisibility(View.VISIBLE);
            }
        }

    }


    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }
}
