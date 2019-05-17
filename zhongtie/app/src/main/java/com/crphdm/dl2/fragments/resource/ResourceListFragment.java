package com.crphdm.dl2.fragments.resource;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.resource.ResourceBookDetailsActivity;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.manager.LibraryManager;
import com.digital.dl2.business.core.obj.PgResourcesListEntity;
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

public class ResourceListFragment extends Fragment {
    //资源页的fragment
    public static final int TYPE_ALL = 1;
    public static final int TYPE_HOT = 2;
    public static final int TYPE_NEW = 3;
    public static final String INTENT_TYPE = "type";
    private static final String TAG = ResourceListFragment.class.getSimpleName();

    @Bind(R.id.recycler)
    RecyclerView recycler;
    @Bind(R.id.load)
    FrameLayout load;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;

    private int mType;
    private MyAdapter mAdapter;

    private UserInfo mUserInfo;
    private String mToken;
    private ProgressDialog mProgressDialog;
    private int index = 2;
    private List<PgResourcesListEntity> mListData;

    public static ResourceListFragment newInstance(int type) {
        ResourceListFragment libraryResourceListFragment = new ResourceListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_TYPE, type);
        libraryResourceListFragment.setArguments(bundle);

        Ln.d("ResourceListFragment:newInstance");
        return libraryResourceListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ln.d("ResourceListFragment:onCreate:");
        if (getArguments() != null) {
            mType = getArguments().getInt(INTENT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_resource, container, false);
        ButterKnife.bind(this, view);

        initMembers();
        Ln.d("ResourceListFragment:onCreateView:mType:" + mType);
        return view;
    }

    @OnClick(R.id.lLError)
    public void onErrorClick() {
        initMembers();
    }

    private void initMembers() {

        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getActivity(), null, "加载中...");
        }

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new MyAdapter();
        recycler.setAdapter(mAdapter);

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);

        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mToken = s;
                        Ln.d("ResourceListFragment:initMembers:mToken:" + mToken);
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

                        Ln.d("ResourceListFragment:initMembers:mToken:error:" + throwable.getMessage());
                        throwable.printStackTrace();
//                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Ln.d("ResourceListFragment:initMembers:mToken:完成");
                        refreshData();
                    }
                });
    }

    private void refreshData() {
        Ln.d("ResourceListFragment:refreshData");

        if (mUserInfo != null) {

            LibraryManager.getInstance().getResourcesListByLibraryId(mUserInfo.getUserid(), mToken, mType, 0, 1, 50)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<List<PgResourcesListEntity>>() {
                        @Override
                        public void call(List<PgResourcesListEntity> pgResourcesListEntities) {
                            mListData = pgResourcesListEntities;
                            Ln.d("ResourceListFragment:refreshData:pgResourcesListEntities:" + pgResourcesListEntities);
                            if (lLError != null) {
                                if (pgResourcesListEntities.size() == 0) {
                                    lLError.setVisibility(View.VISIBLE);
                                    tvError.setText("暂无数据");
                                } else {
                                    lLError.setVisibility(View.GONE);
                                    mAdapter.setData(pgResourcesListEntities);
                                }
                            }

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                            Ln.d("ResourceListFragment:refreshData:error:" + throwable.getMessage());
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
                            Ln.d("ResourceListFragment:refreshData:完成");
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

                        LibraryManager.getInstance().getResourcesListByLibraryId(mUserInfo.getUserid(), mToken, mType, 0, index++, 24)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(new Action1<List<PgResourcesListEntity>>() {
                                    @Override
                                    public void call(List<PgResourcesListEntity> pgResourcesListEntities) {
                                        if (pgResourcesListEntities == null || pgResourcesListEntities.isEmpty()) {
                                            end = true;
                                            return;
                                        }
                                        mListData.addAll(pgResourcesListEntities);
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
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }

            if (lLError != null)
                lLError.setVisibility(View.VISIBLE);
//            Toast.makeText(getActivity(), "用户信息为空", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<PgResourcesListEntity> mListData;

        public void setData(List<PgResourcesListEntity> pgBookForLibraryListEntities) {
            this.mListData = pgBookForLibraryListEntities;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.item_institution_library_bookcase, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {

            Glide.with(getActivity())
                    .load(mListData.get(i).getFrontCover())
                    .placeholder(R.drawable.drw_book_default)
                    .into(viewHolder.bookCover);
            viewHolder.author.setText(mListData.get(i).getAuthor());
            viewHolder.title.setText(mListData.get(i).getName());
            viewHolder.desc.setText(mListData.get(i).getIntroduction());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ResourceBookDetailsActivity.class);
                    intent.putExtra(Constant.BOOK_ID, mListData.get(i).getEntityId());
                    intent.putExtra(Constant.RESOURCE_TYPE, mType);
                    startActivity(intent);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("资源列表页");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("资源列表页");
    }

}
