package com.crphdm.dl2.activity.login;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.crphdm.dl2.R;
import com.crphdm.dl2.fragments.login.EmailLoginFragment;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;


import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Modify by songzixuan on 19/07/11.
 */
public class LoginOldActivity extends AppCompatActivity implements
        EmailLoginFragment.OnEmailLoginFragmentListener{

    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_old);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

    }
        //邮箱登录
    @Override
    public void onEmailLoginOk() {
        Ln.d("LoginActivity:邮箱登录");
        onBackPressed();
    }

     //适配器
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new EmailLoginFragment();
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return "邮箱";
        }
    }
    //选择项目选项
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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