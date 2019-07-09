package com.crphdm.dl2.activity.library;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.crphdm.dl2.R;
import com.crphdm.dl2.fragments.library.InstitutionLibraryListFragment;
import com.umeng.analytics.MobclickAgent;
/**
 * 更多图书馆
 */
public class MoreFragmentActivity extends AppCompatActivity {
        //创建
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_fragment);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new InstitutionLibraryListFragment())
                .commit();
    }
        //选择项目选项
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
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
