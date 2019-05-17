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
import com.crphdm.dl2.fragments.login.EmailLoginFragment;
import com.crphdm.dl2.fragments.login.PhoneLoginFragment;
import com.crphdm.dl2.fragments.login.WorkNumberLoginFragment;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by qinqi on 16/2/29.
 */
public class LoginOldActivity extends AppCompatActivity implements
        PhoneLoginFragment.OnPhoneLoginFragmentListener,
        EmailLoginFragment.OnEmailLoginFragmentListener,
        WorkNumberLoginFragment.OnWorkNumberLoginFragmentListener {
    @Bind(R.id.tb_institution_library)
    TabLayout tbInstitutionLibrary;
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

    @Override
    public void onEmailLoginOk() {
        Ln.d("LoginActivity:邮箱登录");
        onBackPressed();
    }

    @Override
    public void onPhoneLoginOk() {
        Ln.d("LoginActivity:手机号登陆");
        onBackPressed();
    }

    @Override
    public void onWorkNumberLoginOk() {
        Ln.d("LoginActivity:工号登录");
        onBackPressed();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new EmailLoginFragment();
            else if (position == 1) {
                return new PhoneLoginFragment();
            } else if (position == 2) {
                return new WorkNumberLoginFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "邮箱";
                case 1:
                    return "手机号";
                case 2:
                    return "工号";
            }
            return null;
        }
    }

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

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}