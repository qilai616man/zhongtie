package com.crphdm.dl2.activity.personal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.crphdm.dl2.views.StrikeTextView;
import com.digital.dl2.business.core.manager.CloudBookstoreManager;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.obj.PgRecommendMiningMenuEntity;
import com.digital.dl2.business.core.obj.PgResult;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class UserRecommendSelectListActivity extends AppCompatActivity {
    //用户推荐采选
    public static final int COUNT = 30;

    private List<PgRecommendMiningMenuEntity> mList = new ArrayList<>();
    @Bind(R.id.recycler)
    RecyclerView recycler;
    private CustomAdapter mAdapter;
    @Bind(R.id.load)
    FrameLayout load;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;

    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;

    private boolean loading = false;
    private boolean end = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_recommend_select_list);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);

        initMembers();
        refreshData(1);
    }

    @OnClick(R.id.lLError)
    public void onErrorClick() {
        initMembers();
    }

    private void initMembers() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new CustomAdapter();
        recycler.setAdapter(mAdapter);

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (end)
                    return;
                if (mAdapter != null && newState == RecyclerView.SCROLL_STATE_IDLE && !loading
                        && mAdapter.getItemCount() > 0 && (((LinearLayoutManager) recycler.getLayoutManager()).findLastVisibleItemPosition() + 1) == mAdapter.getItemCount()) {
                    loading = true;
                    load.setVisibility(View.VISIBLE);

                    refreshData(mAdapter.getItemCount());
                }
            }
        });
    }

    private void refreshData(final int page) {
        if (mUserInfo != null) {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(this, null, "加载中...");
            }

            UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            mToken = s;
                            Ln.d("UserRecommendSelectListActivity:refreshData:getToken:" + mToken);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Ln.d("UserRecommendSelectListActivity:refreshData:getToken:error:" + throwable.getMessage());
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                            if(lLError !=null){
                                lLError.setVisibility(View.VISIBLE);
                            }
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            Ln.d("UserRecommendSelectListActivity:refreshData:getToken:ok");
                            PersonalCenterManager.getInstance().getUserRecommendMining(
                                    mUserInfo.getOrg_id(),
                                    mUserInfo.getUserid(),
                                    mToken, page, 50)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<List<PgRecommendMiningMenuEntity>>() {
                                        @Override
                                        public void call(List<PgRecommendMiningMenuEntity> pgRecommendMiningMenuEntities) {
                                            Ln.d("UserRecommendSelectListActivity:getUserRecommendMining:data:" + pgRecommendMiningMenuEntities);
                                            if (pgRecommendMiningMenuEntities == null || pgRecommendMiningMenuEntities.isEmpty()) {
                                                end = true;
                                                return;
                                            }
                                            mList.addAll(pgRecommendMiningMenuEntities);
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }
                                            Ln.d("UserRecommendSelectListActivity:getUserRecommendMining:error:" + throwable.getMessage());
                                            loading = false;
                                            load.setVisibility(View.GONE);
                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {
                                            Ln.d("UserRecommendSelectListActivity:getUserRecommendMining:ok");
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }

                                            if(mList.size()==0){
                                                if(lLError !=null){
                                                    lLError.setVisibility(View.VISIBLE);
                                                    tvError.setText("暂无数据");
                                                }
                                            }else {
                                                if(lLError !=null){
                                                    lLError.setVisibility(View.GONE);
                                                }
                                            }

                                            loading = false;
                                            load.setVisibility(View.GONE);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_recommend_select_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_clear) {
//            Toast.makeText(this, "清空列表", Toast.LENGTH_SHORT).show();

            if (mUserInfo != null) {
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(this, null, "加载中...");
                }

                UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                mToken = s;
                                Ln.d("UserRecommendSelectListActivity:refreshData:getToken:" + mToken);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Ln.d("UserRecommendSelectListActivity:refreshData:getToken:error:" + throwable.getMessage());
                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                    mProgressDialog.dismiss();
                                    mProgressDialog = null;
                                }
                            }
                        }, new Action0() {
                            @Override
                            public void call() {
                                Ln.d("UserRecommendSelectListActivity:refreshData:getToken:ok");
                                PersonalCenterManager.getInstance().deleteAdviceMiningList(
                                        mUserInfo.getUserid(), mToken, mUserInfo.getOrg_id())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.newThread())
                                        .subscribe(new Action1<Boolean>() {
                                            @Override
                                            public void call(Boolean aBoolean) {

                                            }
                                        }, new Action1<Throwable>() {
                                            @Override
                                            public void call(Throwable throwable) {

                                            }
                                        }, new Action0() {
                                            @Override
                                            public void call() {
                                                mList.clear();
                                                refreshData(1);
                                            }
                                        });
                            }
                        });
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_user_recomend_select, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final PgRecommendMiningMenuEntity pgRecommendMiningMenuEntity = mList.get(position);
            final int bookId = pgRecommendMiningMenuEntity.getEntityId();

            Glide.with(UserRecommendSelectListActivity.this)
                    .load(pgRecommendMiningMenuEntity.getFrontCover())
                    .placeholder(R.drawable.drw_book_default)
                    .into(holder.bookCover);

            holder.title.setText(pgRecommendMiningMenuEntity.getName());
            holder.author.setText(pgRecommendMiningMenuEntity.getAuthor());
            holder.recommendPeople.setText(pgRecommendMiningMenuEntity.getRecommendPeople());
            holder.priceOrigin.setText("￥"+String.valueOf(pgRecommendMiningMenuEntity.getOriginalPrice()));
            holder.priceReal.setText("￥"+String.valueOf(pgRecommendMiningMenuEntity.getDiscountedPrice()));

            holder.layoutJoinList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //加入采选单
                    if (mUserInfo != null) {
                        if (mProgressDialog == null) {
                            mProgressDialog = ProgressDialog.show(UserRecommendSelectListActivity.this, null, "加载中...");
                        }

                        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(new Action1<String>() {
                                    @Override
                                    public void call(String s) {
                                        mToken = s;
                                        Ln.d("UserRecommendSelectListActivity:CustomAdapter:getToken:" + mToken);
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        Ln.d("UserRecommendSelectListActivity:CustomAdapter:getToken:error:" + throwable.getMessage());

                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                            mProgressDialog = null;
                                        }
                                    }
                                }, new Action0() {
                                    @Override
                                    public void call() {
                                        Ln.d("UserRecommendSelectListActivity:CustomAdapter:getToken:ok");
                                        CloudBookstoreManager.getInstance().addMiningListById(
                                                mUserInfo.getOrg_id(),
                                                mUserInfo.getUserid(), mToken, bookId, 2, 1)
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribeOn(Schedulers.newThread())
                                                .subscribe(new Action1<PgResult>() {
                                                    @Override
                                                    public void call(PgResult result) {
                                                        Ln.d("UserRecommendSelectListActivity:CustomAdapter:addMiningListById:" + result.toString());
                                                        if(result.isStatus()){
                                                            Toast.makeText(UserRecommendSelectListActivity.this, "加入采选单成功", Toast.LENGTH_SHORT).show();
                                                        }else {
                                                            Toast.makeText(UserRecommendSelectListActivity.this,result.getMessage(),Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }, new Action1<Throwable>() {
                                                    @Override
                                                    public void call(Throwable throwable) {
                                                        Ln.d("UserRecommendSelectListActivity:CustomAdapter:addMiningListById:error:" + throwable.getMessage());
                                                        Toast.makeText(UserRecommendSelectListActivity.this,"提交的采选单正在审核",Toast.LENGTH_SHORT).show();
                                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                            mProgressDialog.dismiss();
                                                            mProgressDialog = null;
                                                        }
                                                    }
                                                }, new Action0() {
                                                    @Override
                                                    public void call() {
                                                        Ln.d("UserRecommendSelectListActivity:CustomAdapter:addMiningListById:ok");
//                                                        holder.layoutJoinList.setEnabled(false);
                                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                            mProgressDialog.dismiss();
                                                            mProgressDialog = null;
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.bookCover)
        ImageView bookCover;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.author)
        TextView author;
        @Bind(R.id.layout_join_list)
        LinearLayout layoutJoinList;
        @Bind(R.id.recommend_people)
        TextView recommendPeople;
        @Bind(R.id.price_real)
        TextView priceReal;
        @Bind(R.id.price_origin)
        StrikeTextView priceOrigin;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("用户推荐采选");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("用户推荐采选");
        MobclickAgent.onPause(this);
    }
}
