package com.crphdm.dl2.fragments.video;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.crphdm.dl2.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import recycler.coverflow.RecyclerCoverFlow;

/**
 * Created by Administrator on 2019/8/27.
 */

public class VideoListFragment extends Fragment implements LoadListView.ILoadListener{
    LoadListView listView;
    private List<GTVideoListBean.DataBean> list;
    RecyclerCoverFlow coverFlow;
    private List<GTVideoListBean.DataBean> beanss;
    private List<GTVideoListBean.DataBean> beans;
    private VideoRecyclerCoverFlowAdapter flowAdapter;
    private VideoAdapter adapter;
    LinearLayout layout;
    ScrollView scrollView;

    int z = 5;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        listView = (LoadListView) view.findViewById(R.id.video_listView_id);
        coverFlow = (RecyclerCoverFlow) view.findViewById(R.id.video_recycler_coverFlow_id);
        layout = (LinearLayout) view.findViewById(R.id.lLError);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView_id);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (isNetworkOnline() == false) {
            layout.setVisibility(View.VISIBLE);
        }else {
            init();
        }
    }
    public void shipin(String url, String title) {
        Intent intent = new Intent(getActivity(),VideoDetailActivity.class);
        intent.putExtra("videoUrl",url);
        intent.putExtra("videoTitle",title);
        startActivity(intent);
    }

    public void init(){
        listView.setOnILoadListener(this);
        list = new ArrayList<>();
        final Gson gson = new Gson();
        String data = null;
        String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=getList";
        try {
            data = GTWMOkHttpUtil.getInstance().getSysn(url);
            //Log.e(TAG, "init:--------- "+data );
        } catch (Exception e) {
            e.printStackTrace();
        }
        GTVideoListBean bean = gson.fromJson(data, GTVideoListBean.class);
        list.addAll(bean.getData());
        //Log.e(TAG, "onActivityCreated: " + list.size());
        beanss = new ArrayList<>();
        beans = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getType() == 4) {
                beanss.add(list.get(i));
                //Log.e("tag", "onActivityCreated: " +list.get(i).getThumbnail());
            } else {
                if (j < 5){
                    j++;
                    beans.add(list.get(i));
                }
            }
        }

        flowAdapter = new VideoRecyclerCoverFlowAdapter(this, beanss, getActivity());
        coverFlow.setAdapter(flowAdapter);
        adapter = new VideoAdapter(this, beans, getActivity());
        listView.setAdapter(adapter);
        coverFlow.smoothScrollToPosition(beanss.size() / 2);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_MOVE:
                        if(scrollView.getChildAt(0).getMeasuredHeight() <= scrollView.getHeight() + scrollView.getScrollY()){
                            listView.loadStart();
                        }
                        break;
                }
                return false;
            }
        });
    }







    public boolean isNetworkOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("ping -c 1 www.baidu.com");
            int exitValue = ipProcess.waitFor();
            Log.i("Avalible", "Process:"+exitValue);
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void getLoadData(int j) {
        for (int i = j; i < j+10; i++) {
            beans.add(list.get(i));
        }
    }

    public void loadData() {
        z += 10;
        Log.e("tag", "loadData: "+z);
                //获得加载数据
                getLoadData(z);
                //然后通知MyListView刷新界面
                adapter.notifyDataSetChanged();
                //然后通知加载数据已经完成了
                listView.loadFinish();
    }

}
