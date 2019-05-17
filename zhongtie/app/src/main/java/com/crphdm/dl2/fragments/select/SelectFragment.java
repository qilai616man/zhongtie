package com.crphdm.dl2.fragments.select;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crphdm.dl2.R;
import com.crphdm.dl2.adapter.library.FragmentAdapter;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.utils.Constant;
import com.digital.dl2.business.core.manager.SelectManager;
import com.digital.dl2.business.net.obj.NetSelectSource;
import com.goyourfly.gdownloader.utils.Ln;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SelectFragment extends Fragment{
    //资源采选首页
    @Bind(R.id.tl_select_book_market_tab)
    TabLayout mSelectBookMarketTab;
    @Bind(R.id.vp_select_book_market_list)
    ViewPager mSelectBookMarketList;

    private static final String ARG_NET_STATE = "ARG_NET_STATE";
    private String NOTSELECT = "未采选";
    private String ALREADYSELECT = "已采选";

    private int mNetState;

    private SelectSourceFragment mNotSelectFragment,mAlreadySelectFragment;
    private ProgressDialog mProgressDialog;
    public static SelectFragment newInstance(int netState) {
        SelectFragment selectFragment = new SelectFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_NET_STATE, netState);
        selectFragment.setArguments(bundle);
        return selectFragment;
    }

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_select, container,false);
        ButterKnife.bind(this, view);
        mSelectBookstoreManager = SelectManager.getInstance();
        initBookNumber();
//        initView();

        return view;
    }
    private SelectManager mSelectBookstoreManager;
    private NetSelectSource mNetSelectSource;
    private int mNoSelectBookNumber;
    private int mSelectBookNumber;
    private void initBookNumber(){
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getActivity(), null, "处理中");
        }
        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mSelectBookstoreManager.getSelectBookNumber(UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST).getUserid(), s, 1, 1, 50)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(new Action1<NetSelectSource>() {
                                    @Override
                                    public void call(NetSelectSource netSelectSource) {
                                        mNetSelectSource = netSelectSource;
                                        Ln.d("SelectFragment:mNetSelectSource:" + mNetSelectSource);
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        //错误
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                            mProgressDialog = null;
                                        }
                                    }
                                }, new Action0() {
                                    @Override
                                    public void call() {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                            mProgressDialog = null;
                                        }
                                        //完成
                                        mNoSelectBookNumber = mNetSelectSource.getNoSelect_bookNumber();
                                        mSelectBookNumber = mNetSelectSource.getSelected_bookNumber();
                                        Ln.d("SelectFragment:initBookNumber:mNoSelectBookNumber:" + mNoSelectBookNumber);
                                        Ln.d("SelectFragment:initBookNumber:mSelectBookNumber:" + mSelectBookNumber);
                                        initView();
                                    }
                                });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                }, new Action0() {
                    @Override
                    public void call() {

                    }
                });
    }
    private void initView() {
        List<String> titles = new ArrayList<>();
        titles.add(NOTSELECT + "(" + mNoSelectBookNumber + ")");
        titles.add(ALREADYSELECT + "(" + mSelectBookNumber + ")");

        mSelectBookMarketTab.addTab(mSelectBookMarketTab.newTab().setText(titles.get(0)));
        mSelectBookMarketTab.addTab(mSelectBookMarketTab.newTab().setText(titles.get(1)));
        List<Fragment> fragmentList = new ArrayList<>();
        mNotSelectFragment = SelectSourceFragment.newInstance(Constant.NOT_SELECT_BOOKSTORE);
        mAlreadySelectFragment = SelectSourceFragment.newInstance(Constant.ALREADY_SELECT_BOOKSTORE);

        fragmentList.add(mNotSelectFragment);
        fragmentList.add(mAlreadySelectFragment);

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getChildFragmentManager(), fragmentList, titles);
        mSelectBookMarketList.setAdapter(fragmentAdapter);
        mSelectBookMarketTab.setupWithViewPager(mSelectBookMarketList);
        mSelectBookMarketTab.setTabsFromPagerAdapter(fragmentAdapter);
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//
//        try {
//            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//            childFragmentManager.setAccessible(true);
//            childFragmentManager.set(this,null);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }catch (IllegalAccessException e){
//            e.printStackTrace();
//        }catch (IllegalArgumentException e){
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this , null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }
}
