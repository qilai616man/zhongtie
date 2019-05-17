package com.crphdm.dl2.fragments.library;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.library.LibraryInfoActivity;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.manager.LibraryManager;
import com.digital.dl2.business.core.obj.PgLibrary;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class InstitutionLibraryListFragment extends Fragment {
    //更多图书馆 下面的fragment

    @Bind(R.id.recycler)
    RecyclerView recycler;
    @Bind(R.id.load)
    FrameLayout load;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;

    private int index = 2;

    private static final String ARG_NET_STATE = "ARG_NET_STATE";
    private int mNetState;

    private List<PgLibrary> mListData;
    private MyAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNetState = getArguments().getInt(ARG_NET_STATE);
        }
    }

    public static InstitutionLibraryListFragment newInstance(int netState) {
        InstitutionLibraryListFragment fragment = new InstitutionLibraryListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_NET_STATE, netState);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_institution_library_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private ProgressDialog mProgressDialog;

    private void initData() {


        mAdapter = new MyAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

//        if (mProgressDialog == null) {
//            mProgressDialog = ProgressDialog.show(getActivity(), null, "加载中...");
//        }

        LibraryManager.getInstance().getLibraryList("123456", 1, 20)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgLibrary>>() {
                    @Override
                    public void call(List<PgLibrary> pgLibraries) {
                        mListData = pgLibraries;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Ln.d("Library:加载图书馆失败");
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        throwable.printStackTrace();

                        if (lLError != null) {
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
                        if (lLError != null) {
                            lLError.setVisibility(View.GONE);
                        }

                        mAdapter.setData(mListData);
                        recycler.setAdapter(mAdapter);
                    }
                });

        recycler.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    boolean loading = false;
                    boolean end = false;

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        if (end)
                            return;
                        if (mAdapter != null && newState == RecyclerView.SCROLL_STATE_IDLE && !loading
                                && mAdapter.getItemCount() > 0 && (((LinearLayoutManager) recycler.getLayoutManager()).findLastVisibleItemPosition() + 1) == mAdapter.getItemCount()) {
                            loading = true;
                            if(load != null){
                                load.setVisibility(View.VISIBLE);
                            }

                            LibraryManager.getInstance().getLibraryList("123456", index++, 20)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(
                                            new Action1<List<PgLibrary>>() {
                                                @Override
                                                public void call(List<PgLibrary> pgLibraries) {
                                                    if (pgLibraries == null || pgLibraries.isEmpty()) {
                                                        end = true;
                                                        return;
                                                    }
                                                    mListData.addAll(pgLibraries);
                                                }
                                            }, new Action1<Throwable>() {
                                                @Override
                                                public void call(Throwable throwable) {
                                                    loading = false;
                                                    if(load != null){
                                                        load.setVisibility(View.GONE);
                                                    }
                                                }
                                            }, new Action0() {
                                                @Override
                                                public void call() {
                                                    loading = false;
                                                    if(load != null){
                                                        load.setVisibility(View.GONE);
                                                    }
                                                    mAdapter.notifyDataSetChanged();

                                                }
                                            }
                                    );
                        }
                    }
                }
        );
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<PgLibrary> pgLibraries;

        public void setData(List<PgLibrary> pgLibraries) {
            this.pgLibraries = pgLibraries;
        }

        private PgLibrary getItem(int position) {
            return pgLibraries.get(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.item_library_list, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
            final PgLibrary library = getItem(i);

            Glide.with(getActivity()).load(library.getIconUrl())
                    .placeholder(R.drawable.drw_book_default)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            viewHolder.ivPressLogo.setImageDrawable(resource.getCurrent());
                        }
                    });

            viewHolder.tvPressName.setText(library.getName());
            viewHolder.tvPressInfo.setText(library.getIntroduction());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Ln.d("LibraryList:libraryId:" + library.getLibraryId());
                    Ln.d("LibraryList:libraryName:" + library.getName());
                    Ln.d("LibraryList:libraryLogo:" + library.getIconUrl());
                    Ln.d("LibraryList:libraryDes:" + library.getIntroduction());

                    Intent intent = new Intent(getActivity(), LibraryInfoActivity.class);
                    intent.putExtra(Constant.LIBRARY_ID, library.getLibraryId());
                    intent.putExtra(Constant.LIBRARY_NAME, library.getName());
                    intent.putExtra(Constant.LIBRARY_LOGO, library.getIconUrl());
                    intent.putExtra(Constant.LIBRARY_DESCRIPTION, library.getIntroduction());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return pgLibraries == null ? 0 : pgLibraries.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @Bind(R.id.iv_press_logo)
            ImageView ivPressLogo;
            @Bind(R.id.tv_press_name)
            TextView tvPressName;
            @Bind(R.id.tv_press_info)
            TextView tvPressInfo;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("一级图书馆列表页");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("一级图书馆列表页");
    }


}
