package com.crphdm.dl2.activity.personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.cloud_bookstore.CloudBookstoreBookDetailActivity;
import com.crphdm.dl2.activity.library.LibraryBookDetailsSecondActivity;
import com.crphdm.dl2.activity.resource.ResourceBookDetailsActivity;
import com.crphdm.dl2.fragments.resource.ResourceListFragment;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.crphdm.dl2.views.TimerProgressDialog;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.obj.PgBookForLibraryListEntity;
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

/**
 * 我的收藏  我的预订  我的购买
 */

public class CollectionReserveBuyListActivity extends AppCompatActivity {
    public static final int TYPE_COLLECTION = 0x01;
    public static final int TYPE_RESERVATION = 0x02;
    public static final int TYPE_BOUGHT = 0x03;

    public static final String INTENT_TYPE = "type";
    public static final String INTENT_TITLE = "title";

    private String mTitle;
    private int mType;
    @Bind(R.id.load)
    FrameLayout load;
    @Bind(R.id.recycler)
    RecyclerView recycler;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;

    private CustomAdapter mAdapter;

    private List<PgBookForLibraryListEntity> mList = new ArrayList<>();

    boolean loading = false;
    boolean end = false;

    private TimerProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_reserve_buy_list);
        ButterKnife.bind(this);

        mTitle = getIntent().getStringExtra(INTENT_TITLE);
        mType = getIntent().getIntExtra(INTENT_TYPE, -1);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(mTitle);

        if (mType == TYPE_COLLECTION || mType == TYPE_BOUGHT) {//我的收藏
            mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);
        } else if (mType == TYPE_RESERVATION) {//我的预订
            mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND);
        }
    }

    //没有网络时
    @OnClick(R.id.lLError)
    public void onErrorClick() {
        initMembers();
    }
    //初始化成员
    private void initMembers() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new CustomAdapter();
        recycler.setAdapter(mAdapter);

        if (mType == TYPE_COLLECTION) {//我的收藏
            refreshCollection(1);
        } else if (mType == TYPE_RESERVATION) {//我的预订
            refreshReservation(1);
        } else if (mType == TYPE_BOUGHT) { //我的购买
            refreshBought(1);
        }

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                Ln.d("CollectionReserveBuyListActivity:refresh:index:" + mAdapter.getItemCount());
                if (end)
                    return;
                if (mAdapter != null && newState == RecyclerView.SCROLL_STATE_IDLE && !loading
                        && mAdapter.getItemCount() > 0 && (((LinearLayoutManager) recycler.getLayoutManager()).findLastVisibleItemPosition() + 1) == mAdapter.getItemCount()) {
                    loading = true;
                    load.setVisibility(View.VISIBLE);
//                    ShoppingModule.getInstance().getUserRecommendMining(mAdapter.getItemCount(), COUNT)
                    if (mType == TYPE_COLLECTION) {//我的收藏
                        refreshCollection(mAdapter.getItemCount() + 1);
                    } else if (mType == TYPE_RESERVATION) {//我的预订
                        refreshReservation(mAdapter.getItemCount() + 1);
                    } else if (mType == TYPE_BOUGHT) { //我的购买
                        refreshBought(mAdapter.getItemCount() + 1);
                    }
                }
            }
        });
    }

    /**
     * 刷新我的收藏数据
     *
     * @param index
     */
    private void refreshCollection(final int index) {
        Ln.d("CollectionReserveBuyListActivity:refreshCollection:index:" + index);
        if (mProgressDialog == null) {
            //mProgressDialog = ProgressDialog.show(CollectionReserveBuyListActivity.this, null, "加载中...");
            mProgressDialog = new TimerProgressDialog(this);
        }

        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        PersonalCenterManager.getInstance().getMyCollectList(mUserInfo.getUserid(), s, index, 50)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<List<PgBookForLibraryListEntity>>() {
                                    @Override
                                    public void call(List<PgBookForLibraryListEntity> pgRecommendMiningMenuEntities) {
                                        if (index == 1) {
                                            mList = pgRecommendMiningMenuEntities;
                                            return;
                                        }

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

                                        if (lLError != null) {
                                            tvError.setText("亲爱的用户，看不到我？请用手机连接互联网试一试？");
                                            lLError.setVisibility(View.VISIBLE);
                                        }
                                        loading = false;
                                        load.setVisibility(View.GONE);
//                                        Toast.makeText(CollectionReserveBuyListActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }, new Action0() {
                                    @Override
                                    public void call() {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                            mProgressDialog = null;
                                        }

                                        if (mList.size() == 0) {
                                            if (lLError != null) {
                                                tvError.setText("暂无数据");
                                                lLError.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            if (lLError != null) {
                                                lLError.setVisibility(View.GONE);
                                            }
                                        }

                                        loading = false;
                                        load.setVisibility(View.GONE);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }

                        if (lLError != null) {
                            tvError.setText("亲爱的用户，看不到我？请用手机连接互联网试一试？");
                            lLError.setVisibility(View.VISIBLE);
                        }

//                        Toast.makeText(CollectionReserveBuyListActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {

                    }
                });
    }

    /**
     * 刷新我的预订数据
     *
     * @param index
     */
    private void refreshReservation(final int index) {
        Ln.d("CollectionReserveBuyListActivity:refreshReservation:index:" + index);
        if (mProgressDialog == null) {
            //mProgressDialog = ProgressDialog.show(CollectionReserveBuyListActivity.this, null, "加载中...");
            mProgressDialog = new TimerProgressDialog(this);
        }

        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_SECOND)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        PersonalCenterManager.getInstance().getMyDestineList(mUserInfo.getUserid(), s, index, 50)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<List<PgBookForLibraryListEntity>>() {
                                    @Override
                                    public void call(List<PgBookForLibraryListEntity> pgRecommendMiningMenuEntities) {
                                        if (index == 1) {
                                            mList = pgRecommendMiningMenuEntities;
                                            return;
                                        }

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

                                        loading = false;
                                        load.setVisibility(View.GONE);
                                        if (lLError != null) {
                                            tvError.setText("亲爱的用户，不对头呦！你们单位网络把我挡外面啦，领我进去嘛 ");
                                            lLError.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }, new Action0() {
                                    @Override
                                    public void call() {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                            mProgressDialog = null;
                                        }

                                        if (mList.size() == 0) {
                                            if (lLError != null) {
                                                lLError.setVisibility(View.VISIBLE);
                                                tvError.setText("暂无数据");
                                            }
                                        } else {
                                            if (lLError != null) {
                                                lLError.setVisibility(View.GONE);
                                            }
                                        }
                                        loading = false;
                                        load.setVisibility(View.GONE);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        if (lLError != null) {
                            tvError.setText("亲爱的用户，不对头呦！你们单位网络把我挡外面啦，领我进去嘛 ");
                            lLError.setVisibility(View.VISIBLE);
                        }
//                        Toast.makeText(CollectionReserveBuyListActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {

                    }
                });

    }

    /**
     * 刷新我的购买数据
     *
     * @param index
     */
    private void refreshBought(final int index) {
        Ln.d("CollectionReserveBuyListActivity:refreshBought:index:" + index);
        if (mProgressDialog == null) {
            //mProgressDialog = ProgressDialog.show(CollectionReserveBuyListActivity.this, null, "加载中...");
            mProgressDialog = new TimerProgressDialog(this);
        }

        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        PersonalCenterManager.getInstance().getMyBuyList(mUserInfo.getUserid(), s, index, 50)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<List<PgBookForLibraryListEntity>>() {
                                    @Override
                                    public void call(List<PgBookForLibraryListEntity> pgRecommendMiningMenuEntities) {
                                        if (index == 1) {
                                            mList = pgRecommendMiningMenuEntities;
                                            return;
                                        }

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

                                        loading = false;
                                        load.setVisibility(View.GONE);
                                        if (lLError != null) {
                                            tvError.setText("亲爱的用户，看不到我？请用手机连接互联网试一试？");
                                            lLError.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }, new Action0() {
                                    @Override
                                    public void call() {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                            mProgressDialog = null;
                                        }

                                        if (mList.size() == 0) {
                                            if (lLError != null) {
                                                lLError.setVisibility(View.VISIBLE);
                                                tvError.setText("暂无数据");
                                            }
                                        } else {
                                            if (lLError != null) {
                                                lLError.setVisibility(View.GONE);
                                            }
                                        }

                                        loading = false;
                                        load.setVisibility(View.GONE);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        if (lLError != null) {
                            tvError.setText("亲爱的用户，看不到我？请用手机连接互联网试一试？");
                            lLError.setVisibility(View.VISIBLE);
                        }
//                        Toast.makeText(CollectionReserveBuyListActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {

                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                    }
                });

    }

    //处理菜单被选中运行后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    //适配器
    public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_collection_reserve_buy, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final PgBookForLibraryListEntity pgBookForLibraryListEntity = mList.get(position);
            Glide.with(CollectionReserveBuyListActivity.this)
                    .load(pgBookForLibraryListEntity.getFrontCover())
                    .placeholder(R.drawable.drw_book_default)
                    .into(holder.bookCover);

            holder.title.setText(pgBookForLibraryListEntity.getName());
            holder.author.setText("作者：" + pgBookForLibraryListEntity.getAuthor());
            holder.desc.setText(pgBookForLibraryListEntity.getIntroduction());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//跳转到图书详情
                    if (mType == TYPE_COLLECTION) {//我的收藏
                        Intent intent = new Intent(CollectionReserveBuyListActivity.this, CloudBookstoreBookDetailActivity.class);
                        intent.putExtra(Constant.BOOK_ID, pgBookForLibraryListEntity.getEntityId());
                        if(pgBookForLibraryListEntity.getType() == 1){//图书
                            intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, Constant.CLOUD_BOOKSTORE_BOOK);
                        }else {
                            intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, Constant.CLOUD_BOOKSTORE_POD);
                        }
                        startActivity(intent);

                    } else if (mType == TYPE_RESERVATION) {//我的预订
                        Intent intent = new Intent(CollectionReserveBuyListActivity.this, LibraryBookDetailsSecondActivity.class);
                        intent.putExtra(Constant.BOOK_ID, pgBookForLibraryListEntity.getEntityId());
                        intent.putExtra(Constant.LIBRARY_DETAIL_TYPE, Constant.LIBRARY_DETAIL_E_BOOK);
                        startActivity(intent);

                    } else if (mType == TYPE_BOUGHT) { //我的购买
                        if (pgBookForLibraryListEntity.getIsResource() == 1) {//图书
                            Intent intent = new Intent(CollectionReserveBuyListActivity.this, CloudBookstoreBookDetailActivity.class);
                            intent.putExtra(Constant.BOOK_ID, pgBookForLibraryListEntity.getEntityId());
                            intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, Constant.CLOUD_BOOKSTORE_BOOK);
                            startActivity(intent);
                        } else {//资源
                            Intent intent = new Intent(CollectionReserveBuyListActivity.this, ResourceBookDetailsActivity.class);
                            intent.putExtra(Constant.BOOK_ID, pgBookForLibraryListEntity.getEntityId());
                            intent.putExtra(Constant.RESOURCE_TYPE, ResourceListFragment.TYPE_ALL);
                            startActivity(intent);
                        }
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
        @Bind(R.id.desc)
        TextView desc;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    //恢复
    public void onResume() {
        super.onResume();
        initMembers();
        MobclickAgent.onPageStart("我的预订、收藏和购买页");
        MobclickAgent.onResume(this);
    }
    //暂停
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("我的预订、收藏和购买页");
        MobclickAgent.onPause(this);
    }
}
