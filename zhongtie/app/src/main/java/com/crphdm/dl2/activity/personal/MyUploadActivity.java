package com.crphdm.dl2.activity.personal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.module.PersonalCenterModule;
import com.digital.dl2.business.core.obj.PgUploadList;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MyUploadActivity extends AppCompatActivity {
    @Bind(R.id.recycler)
    RecyclerView recycler;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;
    @Bind(R.id.load)
    FrameLayout load;
    private int index = 1;
    private List<PgUploadList> mListData;

    private UserInfo mUserInfo;

    //我的上传
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_upload);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);
        initMembers();

    }

    @OnClick(R.id.lLError)
    public void onErrorClick() {
        initMembers();
    }


    private ProgressDialog mProgressDialog;
    private MyAdapter mAdapter;
    private String mToken;

    private void initMembers() {

        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, null, "加载中...");
        }

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new MyAdapter();
        recycler.setAdapter(mAdapter);

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND);

        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_SECOND)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mToken = s;
                        Ln.d("MyUploadActivity:initMembers:mToken:" + mToken);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }

                        if (lLError != null)
                            lLError.setVisibility(View.VISIBLE);

                        Ln.d("MyUploadActivity:initMembers:mToken:error:" + throwable.getMessage());
                        throwable.printStackTrace();
//                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Ln.d("MyUploadActivity:initMembers:mToken:完成");
                        refreshData();
                    }
                });
    }

    private void refreshData() {
        Ln.d("MyUploadActivity:refreshData");

        if (mUserInfo != null) {
            PersonalCenterModule.getInstance().getMyUploadList(mUserInfo.getUserid(), mToken, 0, 10)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<List<PgUploadList>>() {
                        @Override
                        public void call(List<PgUploadList> pgUploadLists) {
                            Ln.d("MyUploadActivity:refreshData:pgUploadLists:" + pgUploadLists);
                            mListData = pgUploadLists;
                            if (pgUploadLists.size() == 0 && lLError != null) {
                                lLError.setVisibility(View.VISIBLE);
                                tvError.setText("暂无数据");
                            } else {
                                lLError.setVisibility(View.GONE);
                                mAdapter.setData(mListData);
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                            Ln.d("MyUploadActivity:refreshData:error:" + throwable.getMessage());
                            throwable.printStackTrace();
                            if (lLError != null)
                                lLError.setVisibility(View.VISIBLE);
                            //Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                            Ln.d("MyUploadActivity:refreshData:完成");
                            mAdapter.notifyDataSetChanged();
                        }
                    });


            recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                boolean loading = false;
                boolean end = false;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (end)
                        return;
                    if (mAdapter != null && newState == RecyclerView.SCROLL_STATE_IDLE && !loading
                            && mAdapter.getItemCount() > 0 && (((LinearLayoutManager) recycler.getLayoutManager()).findLastVisibleItemPosition() + 1) == mAdapter.getItemCount()) {
                        loading = true;
                        load.setVisibility(View.VISIBLE);

                        PersonalCenterModule.getInstance().getMyUploadList(mUserInfo.getUserid(), mToken, index++, 10)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(new Action1<List<PgUploadList>>() {
                                    @Override
                                    public void call(List<PgUploadList> pgUploadLists) {
                                        if (pgUploadLists == null || pgUploadLists.isEmpty()) {
                                            end = true;
                                            return;
                                        }
                                        mListData.addAll(pgUploadLists);
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        loading = false;
                                        load.setVisibility(View.GONE);
                                    }
                                }, new Action0() {
                                    @Override
                                    public void call() {
                                        loading = false;
                                        load.setVisibility(View.GONE);
                                        mAdapter.notifyDataSetChanged();

                                    }
                                });
                    }
                }
            });

        } else {
//            Toast.makeText(getApplicationContext(), "用户信息为空", Toast.LENGTH_SHORT).show();
            Ln.d("MyUploadActivity:refreshData:mUserInfo == null");
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            if (lLError != null)
                lLError.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<PgUploadList> mListData;

        public void setData(List<PgUploadList> pgUploadLists) {
            this.mListData = pgUploadLists;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.item_institution_library_bookcase, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {

            Glide.with(MyUploadActivity.this)
                    .load(mListData.get(i).getThumb())
                    .placeholder(R.drawable.drw_book_default)
                    .into(viewHolder.bookCover);
            viewHolder.author.setText(mListData.get(i).getAuthor());
            viewHolder.title.setText(mListData.get(i).getTitle());
            viewHolder.desc.setText(mListData.get(i).getDescription());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return mListData == null ? 0 : mListData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
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
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("我的上传页");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("我的上传页");
        MobclickAgent.onPause(this);
    }

}
