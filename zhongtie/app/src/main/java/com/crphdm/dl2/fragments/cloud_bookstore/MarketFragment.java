package com.crphdm.dl2.fragments.cloud_bookstore;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.crphdm.dl2.adapter.library.FragmentAdapter;
import com.crphdm.dl2.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 云书城首页
 */
public class MarketFragment extends Fragment{
    @Bind(R.id.tl_cloud_book_market_tab)
    TabLayout mCloudBookMarketTab;
    @Bind(R.id.vp_cloud_book_market_list)
    ViewPager mCloudBookMarketList;

    private static final String ARG_NET_STATE = "ARG_NET_STATE";
    private String BOOK = "图书";
    private String POD = "POD";

    private int mNetState;

    private CloudMarketFragment mBookFragment,mPODFragment;

    public static MarketFragment newInstance(int netState) {
        MarketFragment cloudMarketFragment = new MarketFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_NET_STATE, netState);
        cloudMarketFragment.setArguments(bundle);
        return cloudMarketFragment;
    }
    //初始化Fragment。可通过参数savedInstanceState获取之前保存的值。
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNetState = getArguments().getInt(ARG_NET_STATE);
        }

        if(mNetState == Constant.NET_STATE_FIRST_LEVEL || mNetState == Constant.NET_STATE_ALL){

        }else {
            //显示无网
//            Toast.makeText(getActivity(), "网络异常,无法访问北控中心!", Toast.LENGTH_SHORT).show();
        }
    }
    //通过参数savedInstanceState获取之前保存的值
    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }
    //初始化Fragment的布局。
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_cloud_book_market,container,false);
        ButterKnife.bind(this,view);
        initView();
        return view;
    }
    //初始化布局
    private void initView() {
        List<String> titles = new ArrayList<>();
        titles.add(BOOK);
        titles.add(POD);

        mCloudBookMarketTab.addTab(mCloudBookMarketTab.newTab().setText(titles.get(0)));
        mCloudBookMarketTab.addTab(mCloudBookMarketTab.newTab().setText(titles.get(1)));

        List<Fragment> fragmentList = new ArrayList<>();
        mBookFragment = CloudMarketFragment.newInstance(Constant.CLOUD_BOOKSTORE_BOOK);
        mPODFragment = CloudMarketFragment.newInstance(Constant.CLOUD_BOOKSTORE_POD);

        fragmentList.add(mBookFragment);
        fragmentList.add(mPODFragment);

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getChildFragmentManager(), fragmentList, titles);
        mCloudBookMarketList.setAdapter(fragmentAdapter);
        mCloudBookMarketTab.setupWithViewPager(mCloudBookMarketList);
        mCloudBookMarketTab.setTabsFromPagerAdapter(fragmentAdapter);
    }

}
