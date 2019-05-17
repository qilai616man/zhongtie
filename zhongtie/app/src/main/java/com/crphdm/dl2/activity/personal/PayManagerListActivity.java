package com.crphdm.dl2.activity.personal;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.crphdm.dl2.R;
import com.crphdm.dl2.fragments.personal.PayManagerFragment;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PayManagerListActivity extends AppCompatActivity {
    //支付管理
    SectionsPagerAdapter mSectionsPagerAdapter;

    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.container)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_manager_list);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setTabsFromPagerAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(2);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            int type = PayManagerFragment.TYPE_PAYED;

            if (position == 0) {
                type = PayManagerFragment.TYPE_ALL;
            } else if (position == 1) {
                type = PayManagerFragment.TYPE_PAYED;
            } else if (position == 2)
                type = PayManagerFragment.TYPE_UNPAYED;

            return PayManagerFragment.newInstance(type);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "全部订单";
                case 1:
                    return "已支付";
                case 2:
                    return "未支付";
            }
            return null;
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
