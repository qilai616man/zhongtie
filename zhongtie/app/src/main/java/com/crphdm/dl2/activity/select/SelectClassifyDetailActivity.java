package com.crphdm.dl2.activity.select;

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
import com.crphdm.dl2.adapter.SelectClassifyDetailAdapter;
import com.crphdm.dl2.listener.OnClassDetailChangeListener;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.manager.CloudBookstoreManager;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.manager.SelectManager;
import com.digital.dl2.business.core.obj.PgBookForLibraryListEntity;
import com.digital.dl2.business.core.obj.PgResult;
import com.digital.dl2.business.core.obj.PgSelectBookCategory;
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

public class SelectClassifyDetailActivity extends AppCompatActivity {
    //采选图书分类页面
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
    private int page = 1;
    private SelectManager mCloudBookstoreManager;

    private Map<Integer, Integer> mTagMap = new HashMap<>();
    private List<PgSelectBookCategory> mGridViewListData;
    private List<PgBookForLibraryListEntity> mListViewListData;

    private int mSelectType;
    private int mTypeId;
    private int mParentId;
    private int mIsHaveTag;
    private String mParentName;

    private SelectClassifyDetailAdapter mClassifyDetailAdapter;

    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_classify_detail);
        mParentName = getIntent().getStringExtra(Constant.CLOUD_BOOKSTORE_PARENT_NAME);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (mParentName != null) {//专业分类
                getSupportActionBar().setTitle(mParentName);
            }
        }

        ButterKnife.bind(this);

        mSelectType = getIntent().getIntExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, Constant.CLOUD_BOOKSTORE_BOOK);
        mTypeId = getIntent().getIntExtra(Constant.CLOUD_BOOKSTORE_TYPE_ID, Constant.CLOUD_BOOKSTORE_FIGURE);
        mParentId = getIntent().getIntExtra(Constant.CLOUD_BOOKSTORE_PARENT_ID, 1);
        mIsHaveTag = getIntent().getIntExtra(Constant.CLOUD_MARKET_TAG, Constant.CLOUD_MARKET_TAG_NO);
        Ln.d("SelectClassifyDetailActivity:onCreate:mCardId:" + mSelectType);
        Ln.d("SelectClassifyDetailActivity:onCreate:mTypeId:" + mTypeId);
        Ln.d("SelectClassifyDetailActivity:onCreate:mParentId:" + mParentId);
        Ln.d("SelectClassifyDetailActivity:onCreate:mParentName:" + mParentName);
        Ln.d("SelectClassifyDetailActivity:onCreate:mIsHaveTag:" + mIsHaveTag);
        initMembers();

        setLintener();

        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(SelectClassifyDetailActivity.this, null, "加载中...");
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
                        Ln.d("SelectClassifyDetailActivity:getToken:失败:" + throwable.getMessage());
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
        mCloudBookstoreManager.getSelectBookCategoryById(mUserInfo.getUserid(), mToken, mSelectType, mTypeId, parentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgSelectBookCategory>>() {
                    @Override
                    public void call(List<PgSelectBookCategory> pgBookCategories) {
                        mGridViewListData = pgBookCategories;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //错误
                        Ln.d("SelectClassifyDetailActivity:getData:失败:" + throwable.getMessage());
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

        Ln.d("SelectClassifyDetailActivity:initData:parentId:" + parentId);

        mLabelView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onTagClick(int i) {
                mParentId = mTagMap.get(i);
                refreshData(mParentId);
            }
        });
    }

    private void refreshData(int parentId) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(SelectClassifyDetailActivity.this, null, "加载中...");
        }

        mScrollView.setVisibility(View.VISIBLE);
        mCloudBookstoreManager.getSelectBookSubCategoryByType(mUserInfo.getUserid(), mToken, mSelectType, mTypeId, parentId, 1, 50)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgBookForLibraryListEntity>>() {
                    @Override
                    public void call(List<PgBookForLibraryListEntity> pgBookForLibraryListEntities) {
                        Ln.d("SelectClassifyDetailActivity:refreshData:list:" + pgBookForLibraryListEntities);
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
        mCloudBookstoreManager = SelectManager.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRclClassifyDetailList.setLayoutManager(linearLayoutManager);
        mClassifyDetailAdapter = new SelectClassifyDetailAdapter(this, mSelectType);

        mRclClassifyDetailList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRclClassifyDetailList.setAdapter(mClassifyDetailAdapter);

    }
    private void setLintener() {
        Ln.d("SelectClassifyDetailActivity:setListener");
        mClassifyDetailAdapter.setListener(new OnClassDetailChangeListener() {
            @Override
            public void onSelect(int position, int id, int type) {
                Ln.d("SelectClassifyDetailActivity:setListener:onSelect:id:" + id + ",type:" + type);
                if (mSelectType == Constant.CLOUD_BOOKSTORE_POD) {
                    Toast.makeText(SelectClassifyDetailActivity.this, "图书已经采选了", Toast.LENGTH_SHORT).show();
                } else {
                    addMiningList(position, id, type);
                }

            }

            @Override
            public void onCancel(int position, int id, int type) {
                Ln.d("SelectClassifyDetailActivity:setListener:onCancel:id:" + id + ",type:" + type);
                deleteMiningList(position, id, type);
            }

            @Override
            public void onRecommend(int id, int position) {
                Ln.d("SelectClassifyDetailActivity:setListener:onCancel:id:" + id + ",position:" + position);
                addRecommendList(id,position);
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

                    mCloudBookstoreManager.getSelectBookSubCategoryByType(
                            mUserInfo.getUserid(),
                            mToken,
                            mSelectType,
                            mTypeId,
                            mParentId,
                            ++page,
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

                                    Ln.d("SelectClassifyDetailActivity:refreshData:list:" + pgBookForLibraryListEntities);
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
    private void addRecommendList(final int bookId,final int position){
        if (mUserInfo !=null) {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(SelectClassifyDetailActivity.this, null, "处理中");
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
                            //错误
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            //完成
                            CloudBookstoreManager.getInstance().recommendMiningById(mUserInfo.getUserid(),
                                    mToken,
                                    mUserInfo.getOrg_id(),
                                    bookId)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<Boolean>() {
                                        @Override
                                        public void call(Boolean aBoolean) {
                                            if (aBoolean) {
                                                Ln.d("SelectClassifyDetailAdapter:推荐采选成功");
                                                Toast.makeText(SelectClassifyDetailActivity.this, "推荐采选成功", Toast.LENGTH_SHORT).show();
                                                mListViewListData.get(position).setIsAddRecommend(true);
                                            } else {
                                                Ln.d("SelectClassifyDetailAdapter:推荐采选失败");
                                                Toast.makeText(SelectClassifyDetailActivity.this,"推荐采选失败",Toast.LENGTH_SHORT).show();
                                                mListViewListData.get(position).setIsAddRecommend(false);
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
                    });

        }
    }
    private void addMiningList(final int position, final int bookId, final int type) {
        if (mUserInfo != null) {
            if (mSelectType == Constant.CLOUD_BOOKSTORE_BOOK && mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(SelectClassifyDetailActivity.this, null, "加载中...");
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
//                            Toast.makeText(ClassifyDetailActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(SelectClassifyDetailActivity.this, "加入采选单成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                mListViewListData.get(position).setAddMiningList(false);
                                                Toast.makeText(SelectClassifyDetailActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(SelectClassifyDetailActivity.this,"提交的采选单正在审核",Toast.LENGTH_SHORT).show();
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
                mProgressDialog = ProgressDialog.show(SelectClassifyDetailActivity.this, null, "加载中...");
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
//                            Toast.makeText(ClassifyDetailActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(SelectClassifyDetailActivity.this, "取消采选", Toast.LENGTH_SHORT).show();
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

    private List<String> getList(List<PgSelectBookCategory> pgBookCategories) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < pgBookCategories.size(); i++) {
            list.add(pgBookCategories.get(i).getCategoryName());
            mTagMap.put(i, pgBookCategories.get(i).getCategoryId());

            Ln.d("SelectClassifyDetailActivity:getData:index:" + pgBookCategories.get(i).getCategoryId());
        }
        return list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("采选二级分类页");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("采选二级分类页");
        MobclickAgent.onPause(this);
    }
}
