package com.crphdm.dl2.adapter.library;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by sunbaochun on 15/6/8.
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;
    private List<String> mtitles;

    public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titles) {
        super(fm);
        this.fragmentList = fragmentList;
        this.mtitles = titles;
    }
    //获得Item
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }
    //获取数量
    @Override
    public int getCount() {
        return fragmentList.size();
    }
    //配置页面标题
    @Override
    public CharSequence getPageTitle(int position) {
        return mtitles.get(position);
    }
}
