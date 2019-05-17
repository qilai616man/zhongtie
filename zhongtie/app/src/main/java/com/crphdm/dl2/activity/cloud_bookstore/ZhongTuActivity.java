package com.crphdm.dl2.activity.cloud_bookstore;

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
import com.digital.dl2.business.core.manager.CloudBookstoreManager;
import com.digital.dl2.business.core.obj.PgBookCategory;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.next.tagview.TagCloudView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ZhongTuActivity extends AppCompatActivity {
    //中图分类的activity  和专业分类公用一个
    @Bind(R.id.tag_cloud_view)
    TagCloudView mTagCloudView;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;
    private View mParentView;
    private List<PgBookCategory> mClassifyList;
    private Map<String, Integer> mMap = new HashMap<>();

    private int mCardId;
    private int mTypeId;
    private List<PgBookCategory> mListData = new ArrayList<>();

    private Map<Integer, PgBookCategory> mIndexMap = new HashMap<>();

    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhong_tu);
        ButterKnife.bind(this);

        mCardId = getIntent().getIntExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, 1);
        mTypeId = getIntent().getIntExtra(Constant.CLOUD_BOOKSTORE_TYPE_ID, Constant.CLOUD_BOOKSTORE_BOOK);
        Ln.d("ZhongTuActivity:onCreate:mCardId:" + mCardId);
        Ln.d("ZhongTuActivity:onCreate:mTypeId:" + mTypeId);

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

    @OnClick(R.id.lLError)
    public void onErrorClick(){
        initMembers();
    }

    private void initMembers() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(ZhongTuActivity.this, null, "加载中...");
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
//                        Toast.makeText(ZhongTuActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        lLError.setVisibility(View.GONE);
                        initData();
                    }
                });
    }

    private void initData() {
        if (mUserInfo != null) {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(ZhongTuActivity.this, null, "加载中...");
            }

            CloudBookstoreManager.getInstance().getBookCategoryById(mUserInfo.getUserid(), mToken, mCardId, mTypeId, 0)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<List<PgBookCategory>>() {
                        @Override
                        public void call(List<PgBookCategory> pgBookCategories) {
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
//                            Toast.makeText(ZhongTuActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            //完成
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }

                            mTagCloudView.setTags(getList(mListData));
                        }
                    });
        } else {
//            Toast.makeText(ZhongTuActivity.this, "用户信息为空", Toast.LENGTH_SHORT).show();
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    private void setListener() {
        mTagCloudView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onTagClick(int i) {

                Intent intent = new Intent(ZhongTuActivity.this, ClassifyDetailActivity.class);
                intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, mCardId);
                intent.putExtra(Constant.CLOUD_BOOKSTORE_TYPE_ID, mTypeId);
                intent.putExtra(Constant.CLOUD_BOOKSTORE_PARENT_ID, mIndexMap.get(i).getCategoryId());
                intent.putExtra(Constant.CLOUD_BOOKSTORE_PARENT_NAME, mIndexMap.get(i).getCategoryName());
                intent.putExtra(Constant.CLOUD_MARKET_TAG, Constant.CLOUD_MARKET_TAG_YES);
                startActivity(intent);

//                Toast.makeText(ZhongTuActivity.this, "" + mIndexMap.get(i), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> getList(List<PgBookCategory> pgBookCategories) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < pgBookCategories.size(); i++) {
            list.add(pgBookCategories.get(i).getCategoryName());
            mIndexMap.put(i, pgBookCategories.get(i));
            Ln.d("ZhongTuActivity:getData:index:" + pgBookCategories.get(i).getCategoryId());
        }

        return list;
    }

    private List<PgBookCategory> getBookList() {
        String[] tags = new String[]{"新书上架", "超值订购", "重磅推荐", "特价专区", "规章规范", "铁路科技", "职工培训", "学历教材", "大众图书"};
        List<PgBookCategory> list = new ArrayList<>();
        for (int i = 0; i < tags.length; i++) {
            PgBookCategory pgBookCategory = new PgBookCategory();
            pgBookCategory.setCategoryName(tags[i]);
            pgBookCategory.setCategoryId(i * 125);
            pgBookCategory.setCategoryNumber(new Random().nextInt(50));
            list.add(pgBookCategory);
        }

        return list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("云书城一级分类页");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("云书城一级分类页");
        MobclickAgent.onPause(this);
    }
}
