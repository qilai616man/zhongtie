package com.crphdm.dl2.activity.login;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.crphdm.dl2.R;
import com.crphdm.dl2.fragments.login.EmailRegisterFragment;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * 注册页
 */
public class RegisterActivity extends AppCompatActivity {
    //邮箱注册/手机号注册选项卡
    @Bind(R.id.tb_institution_library)
    TabLayout tbInstitutionLibrary;
    //邮箱注册/手机号注册页面
    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_old);
        ButterKnife.bind(this);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        tbInstitutionLibrary.setTabMode(TabLayout.MODE_FIXED);
        tbInstitutionLibrary.setupWithViewPager(mViewPager);
        tbInstitutionLibrary.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
        //处理菜单被选中运行后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
        //注册页面适配器
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return new EmailRegisterFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return "邮箱";
        }
    }
        //恢复
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
        //暂停
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
