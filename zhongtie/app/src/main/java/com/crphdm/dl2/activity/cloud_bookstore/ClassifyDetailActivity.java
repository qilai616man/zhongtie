package com.crphdm.dl2.activity.cloud_bookstore;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.crphdm.dl2.adapter.library.ClassifyDetailAdapter;
import com.crphdm.dl2.listener.OnClassDetailChangeListener;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.manager.CloudBookstoreManager;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.obj.PgBookCategory;
import com.digital.dl2.business.core.obj.PgBookForLibraryListEntity;
import com.digital.dl2.business.core.obj.PgResult;
import com.digital.dl2.business.net.obj.NetResult;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.next.tagview.TagCloudView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 图书分类页面
 */
public class ClassifyDetailActivity extends AppCompatActivity {
    @Bind(R.id.sv_classify_detail)
    ScrollView mScrollView;
    @Bind(R.id.rcl_classify_detail_list)
    RecyclerView mRclClassifyDetailList;
    @Bind(R.id.label_view)
    TagCloudView mLabelView;
    @Bind(R.id.load)
    FrameLayout load;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;

    private CloudBookstoreManager mCloudBookstoreManager;

    private Map<Integer, Integer> mTagMap = new HashMap<>();
    private List<PgBookCategory> mGridViewListData;
    private List<PgBookForLibraryListEntity> mListViewListData;

    private int mCardId;
    private int mTypeId;
    private int mParentId;
    private int mIsHaveTag;
    private String mParentName;

    private ClassifyDetailAdapter mClassifyDetailAdapter;

    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify_detail);
        mParentName = getIntent().getStringExtra(Constant.CLOUD_BOOKSTORE_PARENT_NAME);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (mParentName != null) {//专业分类
                getSupportActionBar().setTitle(mParentName);
            }
        }

        ButterKnife.bind(this);

        mCardId = getIntent().getIntExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, Constant.CLOUD_BOOKSTORE_BOOK);
        mTypeId = getIntent().getIntExtra(Constant.CLOUD_BOOKSTORE_TYPE_ID, Constant.CLOUD_BOOKSTORE_FIGURE);
        mParentId = getIntent().getIntExtra(Constant.CLOUD_BOOKSTORE_PARENT_ID, 1);
        mIsHaveTag = getIntent().getIntExtra(Constant.CLOUD_MARKET_TAG, Constant.CLOUD_MARKET_TAG_NO);
        Ln.d("ClassifyDetailActivity:onCreate:mCardId:" + mCardId);
        Ln.d("ClassifyDetailActivity:onCreate:mTypeId:" + mTypeId);
        Ln.d("ClassifyDetailActivity:onCreate:mParentId:" + mParentId);
        Ln.d("ClassifyDetailActivity:onCreate:mParentName:" + mParentName);
        Ln.d("ClassifyDetailActivity:onCreate:mIsHaveTag:" + mIsHaveTag);
        initMembers();

        setLintener();

        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(ClassifyDetailActivity.this, null, "加载中...");
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
                        Ln.d("ClassifyDetailActivity:getToken:失败:" + throwable.getMessage());
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }

                        throwable.printStackTrace();
//                        Toast.makeText(ClassifyDetailActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        refreshData(mParentId);
                        if (mIsHaveTag == Constant.CLOUD_MARKET_TAG_YES) {
                            initData(mParentId);
                        }
                    }
                });
    }

    //初始化数据
    private void initData(int parentId) {
        mScrollView.setVisibility(View.VISIBLE);
        mCloudBookstoreManager.getBookCategoryById(mUserInfo.getUserid(), mToken, mCardId, mTypeId, parentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgBookCategory>>() {
                    @Override
                    public void call(List<PgBookCategory> pgBookCategories) {
                        mGridViewListData = pgBookCategories;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //错误
                        Ln.d("ClassifyDetailActivity:getData:失败:" + throwable.getMessage());
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        throwable.printStackTrace();
//                        Toast.makeText(ClassifyDetailActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        //完成
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        mLabelView.setTags(getList(mGridViewListData));
                    }
                });

        Ln.d("ClassifyDetailActivity:initData:parentId:" + parentId);

        mLabelView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onTagClick(int i) {
//                Toast.makeText(ClassifyDetailActivity.this, mTagMap.get(i) + "", Toast.LENGTH_SHORT).show();
                mParentId = mTagMap.get(i);
                refreshData(mParentId);
            }
        });
    }

    private void refreshData(int parentId) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(ClassifyDetailActivity.this, null, "加载中...");
        }

        if (mIsHaveTag == Constant.CLOUD_MARKET_TAG_NO) {
            mScrollView.setVisibility(View.GONE);
            mCloudBookstoreManager.getBookForLibraryListEntityByCategoryId(mUserInfo.getUserid(), mToken, parentId, mCardId, 1, 50)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<List<PgBookForLibraryListEntity>>() {
                        @Override
                        public void call(List<PgBookForLibraryListEntity> pgBookForLibraryListEntities) {
                            Ln.d("ClassifyDetail:refreshData:list:" + pgBookForLibraryListEntities);
                            mListViewListData = pgBookForLibraryListEntities;
                            mClassifyDetailAdapter.setData(mListViewListData);

                            if (mListViewListData.size() == 0) {
                                lLError.setVisibility(View.VISIBLE);
                                tvError.setText("暂无数据");
                            } else {
                                lLError.setVisibility(View.GONE);
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
//                        Toast.makeText(ClassifyDetailActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            //完成
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }

                            mClassifyDetailAdapter.notifyDataSetChanged();

                        }
                    });
        } else
            mScrollView.setVisibility(View.VISIBLE);
            mCloudBookstoreManager.getBookSubCategoryByType(mUserInfo.getUserid(), mToken, mCardId, mTypeId, parentId, 1, 50)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<List<PgBookForLibraryListEntity>>() {
                        @Override
                        public void call(List<PgBookForLibraryListEntity> pgBookForLibraryListEntities) {
                            Ln.d("ClassifyDetail:refreshData:list:" + pgBookForLibraryListEntities);
                            mListViewListData = pgBookForLibraryListEntities;
                            mClassifyDetailAdapter.setData(mListViewListData);

                            if (mListViewListData.size() == 0) {
                                lLError.setVisibility(View.VISIBLE);
                                tvError.setText("暂无数据");
                            } else {
                                lLError.setVisibility(View.GONE);
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
//                        Toast.makeText(ClassifyDetailActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            //完成
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }

                            mClassifyDetailAdapter.notifyDataSetChanged();

                        }
                    });
    }


    private void initMembers() {
        mCloudBookstoreManager = CloudBookstoreManager.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRclClassifyDetailList.setLayoutManager(linearLayoutManager);
        mClassifyDetailAdapter = new ClassifyDetailAdapter(this, mCardId);

        mRclClassifyDetailList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRclClassifyDetailList.setAdapter(mClassifyDetailAdapter);

    }
    private NetResult result=new NetResult();
    private void setLintener() {
        Ln.d("ClassifyDetailActivity:setListener");
        mClassifyDetailAdapter.setListener(new OnClassDetailChangeListener() {
            @Override
            public void onSelect(int position,int id, int type) {
                Ln.d("ClassifyDetailActivity:setListener:onSelect:id:" + id + ",type:" + type);
                if(mCardId == Constant.CLOUD_BOOKSTORE_POD){
                    Toast.makeText(ClassifyDetailActivity.this,"实体书无法采选",Toast.LENGTH_SHORT).show();
                }else{
                    addMiningList(position,id, type);
                }

            }
            @Override
            public void onCancel(int position,int id, int type) {
                Ln.d("ClassifyDetailActivity:setListener:onCancel:id:" + id + ",type:" + type);
                deleteMiningList(position,id, type);
            }

            @Override
            public void onRecommend(int id, int position) {

            }
        });

        mRclClassifyDetailList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean loading = false;
            boolean end = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (end)
                    return;
                if (mClassifyDetailAdapter != null && newState == RecyclerView.SCROLL_STATE_IDLE && !loading
                        && mClassifyDetailAdapter.getItemCount() > 0 && (((LinearLayoutManager) mRclClassifyDetailList.getLayoutManager()).findLastVisibleItemPosition() + 1) == mClassifyDetailAdapter.getItemCount()) {
                    loading = true;
                    load.setVisibility(View.VISIBLE);

                    if (mIsHaveTag == Constant.CLOUD_MARKET_TAG_NO) {
                        mCloudBookstoreManager.getBookForLibraryListEntityByCategoryId(
                                mUserInfo.getUserid(),
                                mToken, mParentId, mCardId,
                                mClassifyDetailAdapter.getItemCount(), Constant.PAGE_SIZE)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(new Action1<List<PgBookForLibraryListEntity>>() {
                                    @Override
                                    public void call(List<PgBookForLibraryListEntity> pgBookForLibraryListEntities) {
                                        if (pgBookForLibraryListEntities == null || pgBookForLibraryListEntities.isEmpty()) {
                                            end = true;
                                            return;
                                        }
                                        Ln.d("ClassifyDetail:refreshData:list:" + pgBookForLibraryListEntities);
                                        mListViewListData.addAll(pgBookForLibraryListEntities);
                                        mClassifyDetailAdapter.setData(mListViewListData);

                                        if (mListViewListData.size() == 0) {
                                            lLError.setVisibility(View.VISIBLE);
                                            tvError.setText("暂无数据");
                                        } else {
                                            lLError.setVisibility(View.GONE);
                                        }
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        //错误
                                        loading = false;
                                        load.setVisibility(View.GONE);
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                            mProgressDialog = null;
                                        }
                                        throwable.printStackTrace();
//                        Toast.makeText(ClassifyDetailActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }, new Action0() {
                                    @Override
                                    public void call() {
                                        //完成

                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                            mProgressDialog = null;
                                        }

                                        loading = false;
                                        load.setVisibility(View.GONE);
                                        mClassifyDetailAdapter.notifyDataSetChanged();
                                    }
                                });
                    } else
                        mCloudBookstoreManager.getBookSubCategoryByType(
                                mUserInfo.getUserid(),
                                mToken,
                                mCardId,
                                mTypeId,
                                mParentId,
                                mClassifyDetailAdapter.getItemCount(),
                                50)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(new Action1<List<PgBookForLibraryListEntity>>() {
                                    @Override
                                    public void call(List<PgBookForLibraryListEntity> pgBookForLibraryListEntities) {
                                        if (pgBookForLibraryListEntities == null || pgBookForLibraryListEntities.isEmpty()) {
                                            end = true;
                                            return;
                                        }

                                        Ln.d("ClassifyDetail:refreshData:list:" + pgBookForLibraryListEntities);
                                        mListViewListData.addAll(pgBookForLibraryListEntities);
                                        mClassifyDetailAdapter.setData(mListViewListData);

                                        if (mListViewListData.size() == 0) {
                                            lLError.setVisibility(View.VISIBLE);
                                            tvError.setText("暂无数据");
                                        } else {
                                            lLError.setVisibility(View.GONE);
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
                                        loading = false;
                                        load.setVisibility(View.GONE);

                                    }
                                }, new Action0() {
                                    @Override
                                    public void call() {
                                        //完成
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                            mProgressDialog = null;
                                        }

                                        loading = false;
                                        load.setVisibility(View.GONE);
                                        mClassifyDetailAdapter.notifyDataSetChanged();

                                    }
                                });
                }
            }
        });
    }

    private void addMiningList(final int position, final int bookId, final int type) {
        if (mUserInfo != null) {
            if (mCardId == Constant.CLOUD_BOOKSTORE_BOOK && mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(ClassifyDetailActivity.this, null, "加载中...");
            }

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
                            throwable.printStackTrace();
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            CloudBookstoreManager.getInstance().addMiningListById(
                                    mUserInfo.getOrg_id(),
                                    mUserInfo.getUserid(),
                                    mToken,
                                    bookId, type, 1)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<PgResult>() {
                                        @Override
                                        public void call(PgResult result) {
                                            if (result.isStatus()) {
                                                mListViewListData.get(position).setAddMiningList(true);
                                                Toast.makeText(ClassifyDetailActivity.this, "加入采选单成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                mListViewListData.get(position).setAddMiningList(false);
                                                Toast.makeText(ClassifyDetailActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }
                                            mListViewListData.get(position).setAddMiningList(false);
                                            mClassifyDetailAdapter.notifyDataSetChanged();
                                            Toast.makeText(ClassifyDetailActivity.this,"提交的采选单正在审核",Toast.LENGTH_SHORT).show();
                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }
                                            mClassifyDetailAdapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    });
        }
    }

    private void deleteMiningList(final int position, final int bookId, final int type) {
        if (mUserInfo != null) {

            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(ClassifyDetailActivity.this, null, "加载中...");
            }

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
                            throwable.printStackTrace();
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            PersonalCenterManager.getInstance().deleteMiningList(
                                    mUserInfo.getUserid(),
                                    mUserInfo.getOrg_id(),
                                    mToken,
                                    bookId, type, 1)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<Boolean>() {
                                        @Override
                                        public void call(Boolean aBoolean) {
                                            if (aBoolean) {
                                                mListViewListData.get(position).setAddMiningList(false);
                                                Toast.makeText(ClassifyDetailActivity.this, "取消采选", Toast.LENGTH_SHORT).show();
                                            }else {
                                                mListViewListData.get(position).setAddMiningList(true);
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }
                                            mListViewListData.get(position).setAddMiningList(true);
                                            mClassifyDetailAdapter.notifyDataSetChanged();
                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }
                                            mClassifyDetailAdapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    });
        }
    }

    private List<String> getList(List<PgBookCategory> pgBookCategories) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < pgBookCategories.size(); i++) {
            list.add(pgBookCategories.get(i).getCategoryName());
            mTagMap.put(i, pgBookCategories.get(i).getCategoryId());

            Ln.d("ClassifyDetailActivity:getData:index:" + pgBookCategories.get(i).getCategoryId());
        }
        return list;
    }


    /**
     * 处理操作栏项目点击此处。 操作栏将
     * 自动处理Home / Up按钮上的点击，这么久
     * 在AndroidManifest.xml中指定父活动时。
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //点击home界面
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("云书城二级分类页");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("云书城二级分类页");
        MobclickAgent.onPause(this);
    }
}
