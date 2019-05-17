package com.crphdm.dl2.fragments.select;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.cloud_bookstore.CloudBookstoreBookDetailActivity;
import com.crphdm.dl2.activity.select.SelectClassifyActivity;
import com.crphdm.dl2.adapter.library.ParallaxRecyclerAdapter;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.manager.CloudBookstoreManager;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.manager.SelectManager;
import com.digital.dl2.business.core.module.CloudBookstoreModule;
import com.digital.dl2.business.core.obj.PgResult;
import com.digital.dl2.business.core.obj.PgSelectSource;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SelectSourceFragment extends Fragment {
    //云书城下面的fragment
    private static final String BUNDLE_CARD_ID = "BUNDLE_CARD_ID";

    @Bind(R.id.rcl_fragment_select_source)
    RecyclerView mRclFragmentSelectSource;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;
    @Bind(R.id.load)
    FrameLayout load;

    private View mParentView;
    private View mHeader;
    private ParallaxRecyclerAdapter mParallaxAdapter;

    private List<PgSelectSource> mListData;
    private SelectManager mSelectBookstoreManager;
    private int mSelectType;//1.未采选  2.已采选
    private ProgressDialog mProgressDialog;
    private String mToken;
    private UserInfo mUserInfo;

    public static SelectSourceFragment newInstance(int cardId) {
        SelectSourceFragment selectSourceFragment = new SelectSourceFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_CARD_ID, cardId);
        selectSourceFragment.setArguments(bundle);
        return selectSourceFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSelectType = getArguments().getInt(BUNDLE_CARD_ID, Constant.NOT_SELECT_BOOKSTORE);
            Ln.d("SelectSourceFragment:onCreate:" +mSelectType);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mParentView = inflater.inflate(R.layout.fragment_select_source, container, false);
        ButterKnife.bind(this, mParentView);
        mParallaxAdapter = new ParallaxRecyclerAdapter();
        initMembers();
        setupType();
        setLintener();

        return mParentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void setLintener(){
        mRclFragmentSelectSource.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean loading = false;
            boolean end = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (end)
                    return;
                if (mParallaxAdapter != null && newState == RecyclerView.SCROLL_STATE_IDLE && !loading
                        && mParallaxAdapter.getItemCount() > 0 && ((LinearLayoutManager) mRclFragmentSelectSource.getLayoutManager()).findLastVisibleItemPosition() + 1 == mParallaxAdapter.getItemCount()) {
                    loading = true;
                    load.setVisibility(View.VISIBLE);
                    mSelectBookstoreManager.getSelectSourceBookList(mUserInfo.getUserid(),
                            mToken, mSelectType,
                            /*1, 1,*/
                            mParallaxAdapter.getItemCount(), Constant.PAGE_SIZE)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(new Action1<List<PgSelectSource>>() {
                                @Override
                                public void call(List<PgSelectSource> pgSelectSources) {
                                    if (pgSelectSources == null || pgSelectSources.isEmpty()) {
                                        end = true;
                                        return;
                                    }
                                    mListData.addAll(pgSelectSources);
                                    mParallaxAdapter.setData(mListData);
                                    if (mListData.size() == 0) {
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
                                    mParallaxAdapter.notifyDataSetChanged();
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initMembers() {
        mSelectBookstoreManager = SelectManager.getInstance();
//        mParallaxAdapter = new ParallaxRecyclerAdapter();
        mParallaxAdapter.setShouldClipView(false);
        mCloudBookstoreManager = CloudBookstoreModule.getInstance();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRclFragmentSelectSource.setLayoutManager(linearLayoutManager);
        mRclFragmentSelectSource.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mHeader = getActivity().getLayoutInflater().inflate(R.layout.select_source_book_view_pager, null);

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);
//        if (mProgressDialog == null) {
//            mProgressDialog = ProgressDialog.show(getActivity(), null, "处理中");
//        }
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
                        initData();
                    }
                });
    }

    private void initData() {
        mSelectBookstoreManager.getSelectSourceBookList(mUserInfo.getUserid(), mToken,mSelectType,/*1,1,*/1,20)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgSelectSource>>() {
                    @Override
                    public void call(List<PgSelectSource> pgBookForLibraryListEntities) {
                        mListData = pgBookForLibraryListEntities;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //错误
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        if (lLError != null) {
                            lLError.setVisibility(View.VISIBLE);
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

                        if (lLError != null) {
                            if (mListData != null && !mListData.isEmpty()) {
                                mParallaxAdapter.setData(mListData);
                                mParallaxAdapter.notifyDataSetChanged();
                                mRclFragmentSelectSource.setAdapter(mParallaxAdapter);
                                lLError.setVisibility(View.GONE);
                            } else {
                                lLError.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                });

        setupRecycler();
    }
    


    private void setupType() {
        LinearLayout zhongtufenlei = (LinearLayout) mHeader.findViewById(R.id.ll_zhongtufenlei);
        LinearLayout zhuanyefenlei = (LinearLayout) mHeader.findViewById(R.id.ll_zhuanyefenlei);

        zhuanyefenlei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectClassifyActivity.class);
                intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, mSelectType);
                intent.putExtra(Constant.CLOUD_BOOKSTORE_TYPE_ID, Constant.CLOUD_BOOKSTORE_FIGURE);
                startActivity(intent);
            }
        });

        zhongtufenlei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectClassifyActivity.class);
                intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, mSelectType);
                intent.putExtra(Constant.CLOUD_BOOKSTORE_TYPE_ID, Constant.CLOUD_BOOKSTORE_PROFESSIONAL);
                startActivity(intent);
            }
        });
    }

    private void setupRecycler() {
        mParallaxAdapter.implementRecyclerAdapterMethods(new ParallaxRecyclerAdapter.RecyclerAdapterMethods() {
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
                final ViewHolder holder = (ViewHolder) viewHolder;

                Ln.d("SelectSourceFragment:mListData:" + mListData.get(position).toString());

                final PgSelectSource book = mListData.get(position);

                holder.title.setText(book.getName());
                holder.author.setText(book.getAuthor());

                if (UserModule.getInstance().getRole() == Constant.USER_ROLE_CAIXUANYUAN) {//采选员,领导
                    holder.mSelectLL.setVisibility(View.VISIBLE);
                    holder.mRecommendLL.setVisibility(View.GONE);
                } else {
                    holder.mSelectLL.setVisibility(View.GONE);
                    if(mSelectType == Constant.ALREADY_SELECT_BOOKSTORE){
                        holder.mRecommendLL.setVisibility(View.GONE);
                    }else{
                        holder.mRecommendLL.setVisibility(View.VISIBLE);
                    }
                }
                holder.type.setVisibility(View.GONE);
                if (mSelectType == Constant.ALREADY_SELECT_BOOKSTORE) {//已采选
                    holder.type.setText("已采选");
                    holder.type.setTextColor(getResources().getColor(R.color.color_blue_bg));
                    holder.type.setBackgroundResource(R.drawable.frame_blue);
                } else{
                    holder.type.setText("未采选");
                    holder.type.setTextColor(getResources().getColor(R.color.color_text_54c25b));
                    holder.type.setBackgroundResource(R.drawable.frame_green);
                }

                Glide.with(getActivity())
                        .load(book.getFrontCover())
                        .placeholder(R.drawable.drw_book_default)
                        .crossFade()
                        .into(holder.bookCover);

                holder.mSelectLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if(mSelectType == Constant.ALREADY_SELECT_BOOKSTORE){
//                            Toast.makeText(getActivity(),"图书已经采选了",Toast.LENGTH_SHORT).show();
//                        }else {
                        if (book.isAddMiningList()) {
                            book.setIsAddMiningList(false);
                            deleteMiningList(book.getEntityId(), book.getType(), position);
                        } else {
                            book.setIsAddMiningList(true);
                            Ln.d("SelectSourceFragment:addMiningList:book.getType():" + book.getType());
                            addMiningList(book.getEntityId(), book.getType(), position);
                        }
//                        }

                    }
                });

                holder.mRecommendLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(book.isAddRecommend()){
                            book.setIsAddRecommend(false);
                        }else{
                            book.setIsAddRecommend(true);
                            addRecommendList(book.getEntityId(),position);
                        }
                    }
                });
                if(book.isAddMiningList()){
                    holder.cbSelect.setImageDrawable(getResources().getDrawable(R.drawable.draw_cloud_book_market_select));
                }else {
                    holder.cbSelect.setImageDrawable(getResources().getDrawable(R.drawable.draw_cloud_book_market_unselect));
                }
                if (book.isAddRecommend()){
                    holder.cbRecommend.setImageDrawable(getResources().getDrawable(R.drawable.draw_cloud_book_market_select));
                    holder.tvRecommend.setText("已推荐");
                    holder.mRecommendLL.setEnabled(false);
                }else{
                    holder.cbRecommend.setImageDrawable(getResources().getDrawable(R.drawable.draw_cloud_book_market_unselect));
                    holder.tvRecommend.setText("推荐采选");
                    holder.mRecommendLL.setEnabled(true);
                }

                holder.itemLinerLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CloudBookstoreBookDetailActivity.class);
                        intent.putExtra(Constant.BOOK_ID, book.getEntityId());
                        intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, 1);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_select_bookcase, viewGroup, false));
            }

            @Override
            public int getItemCount() {
                return mListData == null ? 0 : mListData.size();
            }

        });
        mParallaxAdapter.setParallaxHeader(mHeader, mRclFragmentSelectSource);
        mRclFragmentSelectSource.setAdapter(mParallaxAdapter);

    }
    private void addRecommendList(final int bookId,final int position){
        if (mUserInfo !=null) {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(getActivity(), null, "处理中");
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
                            mCloudBookstoreManager.recommendMiningById(mUserInfo.getUserid(),
                                    mToken,
                                    mUserInfo.getOrg_id(),
                                    bookId)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<Boolean>() {
                                        @Override
                                        public void call(Boolean aBoolean) {
                                            if (aBoolean) {
                                                Ln.d("SelectSourceFragment:推荐采选成功");
                                                Toast.makeText(getActivity(), "推荐采选成功", Toast.LENGTH_SHORT).show();
                                                mListData.get(position).setIsAddRecommend(true);
                                            } else {
                                                Ln.d("SelectSourceFragment:推荐采选失败");
                                                Toast.makeText(getActivity(),"推荐采选失败",Toast.LENGTH_SHORT).show();
                                                mListData.get(position).setIsAddRecommend(false);
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

                                            mParallaxAdapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    });

        }
    }
    private void addMiningList(final int bookId, final int type, final int position) {
        if (mUserInfo != null) {

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
                            Ln.d("SelectSourceFragment:addMiningList:getToken:error:" + throwable.getMessage());
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                            throwable.printStackTrace();
//                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            Ln.d("SelectSourceFragment:addMiningList:getToken:ok");
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
                                            Ln.d("SelectSourceFragment:addMiningList:addMiningListById:result:" + result.toString());
                                            if (result.isStatus()) {
                                                Toast.makeText(getActivity(), "加入采选单成功", Toast.LENGTH_SHORT).show();
                                                mListData.get(position).setIsAddMiningList(true);
                                            }else {
                                                Toast.makeText(getActivity(),result.getMessage(),Toast.LENGTH_SHORT).show();
                                                mListData.get(position).setIsAddMiningList(false);
                                            }

                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            Ln.d("SelectSourceFragment:addMiningList:addMiningListById:error:" + throwable.getMessage());
                                            Toast.makeText(getActivity(),"提交的采选单正在审核",Toast.LENGTH_SHORT).show();
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }

                                            mListData.get(position).setIsAddMiningList(false);
                                            mParallaxAdapter.notifyDataSetChanged();
                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }
                                            mParallaxAdapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    });
        }
    }

    private void deleteMiningList(final int bookId, final int type, final int position) {
        if (mUserInfo != null) {
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
//                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(getActivity(), "取消采选", Toast.LENGTH_SHORT).show();
                                                mListData.get(position).setIsAddMiningList(false);
                                            }else {
                                                mListData.get(position).setIsAddMiningList(true);
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }
                                            mListData.get(position).setIsAddMiningList(true);
                                            mParallaxAdapter.notifyDataSetChanged();

                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }
                                            mParallaxAdapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    });
        }
    }


private CloudBookstoreManager mCloudBookstoreManager;
    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.bookCover)
        ImageView bookCover;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.type)
        TextView type;
        @Bind(R.id.rl_title)
        RelativeLayout rlTitle;
        @Bind(R.id.author)
        TextView author;
        @Bind(R.id.cb_select)
        ImageView cbSelect;
        @Bind(R.id.ll_select)
        LinearLayout mSelectLL;
        @Bind(R.id.tv_select)
        TextView tvSelect;
        @Bind(R.id.item_liner_layout)
        LinearLayout itemLinerLayout;

        @Bind(R.id.ll_recommend)
        LinearLayout mRecommendLL;
        @Bind(R.id.cb_recommend)
        ImageView cbRecommend;
        @Bind(R.id.tv_recommend)
        TextView tvRecommend;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("采选页");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("采选页");
    }
    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this,null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }
}
