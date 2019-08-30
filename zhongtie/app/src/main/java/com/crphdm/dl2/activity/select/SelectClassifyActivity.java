package com.crphdm.dl2.activity.select;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.digital.dl2.business.core.manager.SelectManager;
import com.digital.dl2.business.core.obj.PgSelectBookCategory;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.next.tagview.TagCloudView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
/**
 * 中图分类的activity  和专业分类公用一个
 */

public class SelectClassifyActivity extends AppCompatActivity {
    //中图分类的activity  和专业分类公用一个
    @Bind(R.id.tag_select_view)
    TagCloudView mTagSelectView;
    //错误提示文字
    @Bind(R.id.tvError)
    TextView tvError;
    //错误提示布局
    @Bind(R.id.lLError)
    LinearLayout lLError;

    private int mSelectType;
    private int mTypeId;
    private List<PgSelectBookCategory> mListData = new ArrayList<>();

    private Map<Integer, PgSelectBookCategory> mIndexMap = new HashMap<>();

    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;
    //创建
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_classify);
        ButterKnife.bind(this);

        mSelectType = getIntent().getIntExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, 1);
        mTypeId = getIntent().getIntExtra(Constant.CLOUD_BOOKSTORE_TYPE_ID, Constant.CLOUD_BOOKSTORE_BOOK);
        Ln.d("SelectClassifyActivity:onCreate:mCardId:" + mSelectType);
        Ln.d("SelectClassifyActivity:onCreate:mTypeId:" + mTypeId);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if (mTypeId == Constant.CLOUD_BOOKSTORE_FIGURE) {//专业分类
                getSupportActionBar().setTitle("专业分类");
            } else if (mTypeId == Constant.CLOUD_BOOKSTORE_PROFESSIONAL) {//中图分类
                getSupportActionBar().setTitle("中图分类");
            }
        }

        initMembers();
        setListener();
    }
    //错误布局事件
    @OnClick(R.id.lLError)
    public void onErrorClick(){
        initMembers();
    }
    //初始化成员
    private void initMembers() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(SelectClassifyActivity.this, null, "加载中...");
        }

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);

        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mToken = s;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        lLError.setVisibility(View.VISIBLE);
                        throwable.printStackTrace();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        lLError.setVisibility(View.GONE);
                        initData();
                    }
                });
    }
    //初始化数据
    private void initData() {
        if (mUserInfo != null) {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(SelectClassifyActivity.this, null, "加载中...");
            }

            SelectManager.getInstance().getSelectBookCategoryById(mUserInfo.getUserid(), mToken, mSelectType, mTypeId, 0)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<List<PgSelectBookCategory>>() {
                        @Override
                        public void call(List<PgSelectBookCategory> pgBookCategories) {
                            mListData = pgBookCategories;
                            if(pgBookCategories.isEmpty()|| pgBookCategories.size()==0){
                                tvError.setText("暂时没有数据");
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            //错误
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                            throwable.printStackTrace();
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            //完成
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }

                            mTagSelectView.setTags(getList(mListData));
                        }
                    });
        } else {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }
    //设置监听
    private void setListener() {
        mTagSelectView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onTagClick(int i) {

                Intent intent = new Intent(SelectClassifyActivity.this, SelectClassifyDetailActivity.class);
                intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, mSelectType);
                intent.putExtra(Constant.CLOUD_BOOKSTORE_TYPE_ID, mTypeId);
                intent.putExtra(Constant.CLOUD_BOOKSTORE_PARENT_ID, mIndexMap.get(i).getCategoryId());
                intent.putExtra(Constant.CLOUD_BOOKSTORE_PARENT_NAME, mIndexMap.get(i).getCategoryName());
                intent.putExtra(Constant.CLOUD_MARKET_TAG, Constant.CLOUD_MARKET_TAG_YES);
                startActivity(intent);

            }
        });
    }
    //获取传递的数据
    private List<String> getList(List<PgSelectBookCategory> pgBookCategories) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < pgBookCategories.size(); i++) {
            list.add(pgBookCategories.get(i).getCategoryName());
            mIndexMap.put(i, pgBookCategories.get(i));
            Ln.d("SelectClassifyActivity:getData:index:" + pgBookCategories.get(i).getCategoryId());
        }

        return list;
    }
    //处理菜单被选中运行后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    //恢复
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("采选页一级分类页");
        MobclickAgent.onResume(this);
    }
    //停止
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("采选页一级分类页");
        MobclickAgent.onPause(this);
    }
}
