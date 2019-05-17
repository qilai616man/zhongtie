package com.crphdm.dl2.activity.library;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.crphdm.dl2.R;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.manager.LibraryManager;
import com.digital.dl2.business.core.obj.PgBookForLibraryListEntity;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LibraryInfoActivity extends AppCompatActivity {
    //更多图书馆  图书馆详情
    @Bind(R.id.recycler)
    RecyclerView recycler;
    @Bind(R.id.iv_press_logo)
    ImageView ivPressLogo;
    @Bind(R.id.tv_press_name)
    TextView tvPressName;
    @Bind(R.id.tv_press_info)
    TextView tvPressInfo;
    @Bind(R.id.load)
    FrameLayout load;
    @Bind(R.id.header)
    RecyclerViewHeader header;

    private int mLibraryId;
    private String mLibraryName;
    private String mLibraryLogo;
    private String mLibraryDes;

    private List<PgBookForLibraryListEntity> mListData;
    private MyAdapter mAdapter;

    private BitmapDrawable drawable;
    private int index = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_info);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLibraryId = getIntent().getIntExtra(Constant.LIBRARY_ID, 0);
        mLibraryName = getIntent().getStringExtra(Constant.LIBRARY_NAME);
        mLibraryLogo = getIntent().getStringExtra(Constant.LIBRARY_LOGO);
        mLibraryDes = getIntent().getStringExtra(Constant.LIBRARY_DESCRIPTION);

        Ln.d("LibraryInfoActivity:onCreate:mLibraryId:" + mLibraryId);
        Ln.d("LibraryInfoActivity:onCreate:mLibraryName:" + mLibraryName);
        Ln.d("LibraryInfoActivity:onCreate:mLibraryLogo:" + mLibraryLogo);
        Ln.d("LibraryInfoActivity:onCreate:mLibraryDes:" + mLibraryDes);

        initData();

    }

    private ProgressDialog mProgressDialog;

    private void initData() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, null, "加载中...");
        }
        mAdapter = new MyAdapter();
//        Glide.with(this).load(mLibraryLogo).placeholder(R.drawable.user_face_icon).into(ivPressLogo);
//        Glide.with(LibraryInfoActivity.this)
//                .load(mLibraryLogo)
//                .placeholder(R.drawable.user_face_icon)
//                .into(ivPressLogo);

        Glide.with(LibraryInfoActivity.this)
                .load(mLibraryLogo)
                .placeholder(R.drawable.drw_book_default)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        ivPressLogo.setImageDrawable(resource.getCurrent());
                    }
                });

        tvPressName.setText(mLibraryName);
        tvPressInfo.setText(mLibraryDes);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        header.attachTo(recycler, true);

        LibraryManager.getInstance().getBookListToFirst("123456", mLibraryId, 1, 50)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgBookForLibraryListEntity>>() {
                    @Override
                    public void call(List<PgBookForLibraryListEntity> pgBookForLibraryListEntities) {
                        mListData = pgBookForLibraryListEntities;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        mAdapter.setData(mListData);
                        recycler.setAdapter(mAdapter);
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

                    LibraryManager.getInstance().getBookListToFirst("123456", mLibraryId, index++, 20)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(new Action1<List<PgBookForLibraryListEntity>>() {
                                @Override
                                public void call(List<PgBookForLibraryListEntity> pgBookForLibraryListEntities) {
                                    if (pgBookForLibraryListEntities == null || pgBookForLibraryListEntities.isEmpty()) {
                                        end = true;
                                        return;
                                    }
                                    mListData.addAll(pgBookForLibraryListEntities);
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


    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<PgBookForLibraryListEntity> pgBookForLibraryListEntities;

        public void setData(List<PgBookForLibraryListEntity> pgBookForLibraryListEntities) {
            this.pgBookForLibraryListEntities = pgBookForLibraryListEntities;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.item_institution_library_bookcase, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {

            final PgBookForLibraryListEntity book = pgBookForLibraryListEntities.get(i);

            Glide.with(LibraryInfoActivity.this)
                    .load(book.getFrontCover())
                    .placeholder(R.drawable.drw_book_default)
                    .into(viewHolder.bookCover);

            viewHolder.author.setText(book.getAuthor());
            viewHolder.title.setText(book.getName());
            viewHolder.desc.setText(book.getIntroduction());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(LibraryInfoActivity.this,"跳转到图书馆图书详情页",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LibraryInfoActivity.this, LibraryBookDetailsFirstActivity.class);
                    intent.putExtra(Constant.BOOK_ID, book.getEntityId());
//                    intent.putExtra(Constant.LIBRARY_ID, mLibraryId);
//                    intent.putExtra(Constant.LIBRARY_NAME, mLibraryName);
//                    intent.putExtra(Constant.LIBRARY_LOGO, mLibraryLogo);
//                    intent.putExtra(Constant.LIBRARY_DESCRIPTION, mLibraryDes);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return pgBookForLibraryListEntities == null ? 0 : pgBookForLibraryListEntities.size();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("一级图书馆详情页");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("一级图书馆详情页");
        MobclickAgent.onPause(this);
    }
}
