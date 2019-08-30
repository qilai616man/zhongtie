package com.crphdm.dl2.fragments.cloud_bookstore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.WebActivity;
import com.crphdm.dl2.activity.cloud_bookstore.ClassifyDetailActivity;
import com.crphdm.dl2.activity.cloud_bookstore.CloudBookstoreBookDetailActivity;
import com.crphdm.dl2.activity.cloud_bookstore.ZhongTuActivity;
import com.crphdm.dl2.adapter.library.ParallaxRecyclerAdapter;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.manager.CloudBookstoreManager;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.manager.PublicManager;
import com.digital.dl2.business.core.obj.PgAdvertisement;
import com.digital.dl2.business.core.obj.PgBookForCloudMarket;
import com.digital.dl2.business.core.obj.PgResult;
import com.github.why168.LoopViewPagerLayout;
import com.github.why168.listener.OnBannerItemClickListener;
import com.github.why168.loader.OnDefaultImageViewLoader;
import com.github.why168.modle.BannerInfo;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 云书城下面的fragment
 */
public class CloudMarketFragment extends Fragment {
    private static final String BUNDLE_CARD_ID = "BUNDLE_CARD_ID";

    @Bind(R.id.rcl_fragment_book_market_list)
    RecyclerView mRclFragmentBookMarketList;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;

    private String mType = "全部";
    private View mParentView;
    //    private List<PgSpreadTag> mCloudBookTagList = new ArrayList<>();
    private View mHeader;
    private ParallaxRecyclerAdapter mParallaxAdapter;

    private List<PgAdvertisement> mUrlList;
    private List<PgBookForCloudMarket> mListData;
    private CloudBookstoreManager mCloudBookstoreManager;
    private int mCardId;//1.电子书  2.pod

    private ProgressDialog mProgressDialog;
    private String mToken;
    private UserInfo mUserInfo;

    public static CloudMarketFragment newInstance(int cardId) {
        CloudMarketFragment cloudMarketFragment = new CloudMarketFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_CARD_ID, cardId);
        cloudMarketFragment.setArguments(bundle);
        return cloudMarketFragment;
    }
    //初始化Fragment。
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCardId = getArguments().getInt(BUNDLE_CARD_ID, Constant.CLOUD_BOOKSTORE_BOOK);
            Ln.d("CloudMarketFragment:onCreate:" +mCardId);
        }
    }
    //初始化Fragment的布局。
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mParentView = inflater.inflate(R.layout.fragment_book_market_layout, container, false);
        ButterKnife.bind(this, mParentView);

        initMembers();
        initAdvertisement();
        setupType();

        return mParentView;
    }
    //销毁与Fragment有关的视图，但未与Activity解除绑定
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    //初始化成员
    private void initMembers() {
        mCloudBookstoreManager = CloudBookstoreManager.getInstance();
        mParallaxAdapter = new ParallaxRecyclerAdapter();
        mParallaxAdapter.setShouldClipView(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRclFragmentBookMarketList.setLayoutManager(linearLayoutManager);
        mRclFragmentBookMarketList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mHeader = getActivity().getLayoutInflater().inflate(R.layout.cloud_book_market_loop_view_pager, null);

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);

        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getActivity(), null, "加载中...");
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
//                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        initData();
                    }
                });
    }
    //初始化数据
    private void initData() {
        mCloudBookstoreManager.getBookForLibraryListEntityByPromotionTagId(mCardId, mToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgBookForCloudMarket>>() {
                    @Override
                    public void call(List<PgBookForCloudMarket> pgBookForLibraryListEntities) {
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
                            if(mListData != null && !mListData.isEmpty()){
                                mParallaxAdapter.setData(mListData);
                                mParallaxAdapter.notifyDataSetChanged();
                                mRclFragmentBookMarketList.setAdapter(mParallaxAdapter);
                                lLError.setVisibility(View.GONE);
                            }else {
                                lLError.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                });

        setupRecycler();
    }

    //广告
    private void initAdvertisement() {
        final ImageView advertisementError = (ImageView) mHeader.findViewById(R.id.iv_advertisement_error);

        PublicManager.getInstance().getAdvertisementsByPage(mCardId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgAdvertisement>>() {
                    @Override
                    public void call(List<PgAdvertisement> urlList) {
                        mUrlList = urlList;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        advertisementError.setVisibility(View.VISIBLE);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        advertisementError.setVisibility(View.GONE);
                        setupHeader(mUrlList);
                    }
                });
    }
    //设置类型
    private void setupType() {
        LinearLayout zhongtufenlei = (LinearLayout) mHeader.findViewById(R.id.ll_zhongtufenlei);
        LinearLayout zhuanyefenlei = (LinearLayout) mHeader.findViewById(R.id.ll_zhuanyefenlei);

        zhuanyefenlei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ZhongTuActivity.class);
                intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, mCardId);
                intent.putExtra(Constant.CLOUD_BOOKSTORE_TYPE_ID, Constant.CLOUD_BOOKSTORE_FIGURE);
                startActivity(intent);
            }
        });

        zhongtufenlei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ZhongTuActivity.class);
                intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, mCardId);
                intent.putExtra(Constant.CLOUD_BOOKSTORE_TYPE_ID, Constant.CLOUD_BOOKSTORE_PROFESSIONAL);
                startActivity(intent);
            }
        });
    }
    //设置采选员
    private void setupRecycler() {
        mParallaxAdapter.implementRecyclerAdapterMethods(new ParallaxRecyclerAdapter.RecyclerAdapterMethods() {
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
                ViewHolder holder = (ViewHolder) viewHolder;

                Ln.d("CloudMarketFragment:mListData:" + mListData.get(position).toString());

                final PgBookForCloudMarket book = mListData.get(position);

                holder.tvTag.setText(book.getType_name());
                holder.title.setText(book.getTitle());
                holder.author.setText(book.getAuthor());
                holder.desc.setText(book.getDescription());

                holder.mSelectLL.setVisibility(View.INVISIBLE);

                if (UserModule.getInstance().getRole() == Constant.USER_ROLE_CAIXUANYUAN) {//采选员
                    holder.mSelectLL.setVisibility(View.VISIBLE);
                }

                holder.price.setText("￥" + ( (book.getPrice() * 100)) / 100.00f);
                holder.oldPrice.setText("￥" + ( (book.getPressprice() * 100)) / 100.00f);
                holder.oldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

//                if (book.getType() == Constant.PAPER_BOOK) {
//                    holder.type.setText("实体书");
//                    holder.type.setTextColor(getResources().getColor(R.color.color_blue_bg));
//                    holder.type.setBackgroundResource(R.drawable.frame_blue);
//                } else if (book.getType() == Constant.E_BOOK) {
//                    holder.type.setText("电子书");
//                    holder.type.setTextColor(getResources().getColor(R.color.color_text_54c25b));
//                    holder.type.setBackgroundResource(R.drawable.frame_green);
//                }

                if (mCardId == Constant.CLOUD_BOOKSTORE_POD) {//电子书
                    holder.type.setText("实体书");
                    holder.type.setTextColor(getResources().getColor(R.color.color_blue_bg));
                    holder.type.setBackgroundResource(R.drawable.frame_blue);
                } else{
                    holder.type.setText("电子书");
                    holder.type.setTextColor(getResources().getColor(R.color.color_text_54c25b));
                    holder.type.setBackgroundResource(R.drawable.frame_green);
                }

                Glide.with(getActivity())
                        .load(book.getThumb())
                        .placeholder(R.drawable.drw_book_default)
                        .crossFade()
                        .into(holder.bookCover);

                holder.mSelectLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mCardId == Constant.CLOUD_BOOKSTORE_POD){
                            Toast.makeText(getActivity(),"实体书无法采选",Toast.LENGTH_SHORT).show();
                        }else {
                            if(book.isAddMiningList()){
                                book.setAddMiningList(false);
                                deleteMiningList(book.getGoods_id(), book.getType(),position);
                            }else {
                                book.setAddMiningList(true);
                                addMiningList(book.getGoods_id(), book.getType(),position);
                            }
                        }

                    }
                });

//                holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        if (isChecked) {
//                            Ln.d("CloudMarketFragment:cbSelect:选中");
//                            book.setAddMiningList(true);
//                            addMiningList(book.getGoods_id(), book.getType(),position);
//                        } else {
//                            Ln.d("CloudMarketFragment:cbSelect:取消选中");
//                            book.setAddMiningList(false);
//                            deleteMiningList(book.getGoods_id(), book.getType(),position);
//                        }
//                        book.setAddMiningList(isChecked);
//                    }
//                });
                if(book.isAddMiningList()){
                    holder.cbSelect.setImageDrawable(getResources().getDrawable(R.drawable.draw_cloud_book_market_select));
                }else {
                    holder.cbSelect.setImageDrawable(getResources().getDrawable(R.drawable.draw_cloud_book_market_unselect));
                }

                //更多
                holder.llMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(getActivity(), i + "", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(getActivity(), ClassifyDetailActivity.class));
                        Intent intent = new Intent(getActivity(), ClassifyDetailActivity.class);
                        intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, mCardId);
                        intent.putExtra(Constant.CLOUD_BOOKSTORE_TYPE_ID, 1);
                        intent.putExtra(Constant.CLOUD_BOOKSTORE_PARENT_ID, book.getTag_id());
                        intent.putExtra(Constant.CLOUD_BOOKSTORE_PARENT_NAME,book.getType_name());
                        intent.putExtra(Constant.CLOUD_MARKET_TAG, Constant.CLOUD_MARKET_TAG_NO);
                        startActivity(intent);
                    }
                });

                holder.itemLinerLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CloudBookstoreBookDetailActivity.class);
                        intent.putExtra(Constant.BOOK_ID, book.getGoods_id());
                        intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, mCardId);
                        startActivity(intent);
//                        startActivity(new Intent(getActivity(), CloudBookstoreBookDetailActivity.class));
                    }
                });
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_cloud_bookcase, viewGroup, false));
            }

            @Override
            public int getItemCount() {
                return mListData == null ? 0 : mListData.size();
            }

        });
        mParallaxAdapter.setParallaxHeader(mHeader, mRclFragmentBookMarketList);
        mRclFragmentBookMarketList.setAdapter(mParallaxAdapter);

    }

    private void modifySelectState(){
//        mParallaxAdapter.
    }
    //加入采选单
    private void addMiningList(final int bookId, final int type, final int position) {
        if (mUserInfo != null) {

//            if (mProgressDialog == null) {
//                mProgressDialog = ProgressDialog.show(getActivity(), null, "加载中...");
//            }

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
                            Ln.d("CloudMarketFragment:addMiningList:getToken:error:" + throwable.getMessage());
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
                            Ln.d("CloudMarketFragment:addMiningList:getToken:ok");
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
                                            Ln.d("CloudMarketFragment:addMiningList:addMiningListById:result:" + result.toString());
                                            if (result.isStatus()) {
                                                Toast.makeText(getActivity(), "加入采选单成功", Toast.LENGTH_SHORT).show();
                                                mListData.get(position).setAddMiningList(true);
                                            }else {
                                                Toast.makeText(getActivity(),result.getMessage(),Toast.LENGTH_SHORT).show();
                                                mListData.get(position).setAddMiningList(false);
                                            }

                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            Ln.d("CloudMarketFragment:addMiningList:addMiningListById:error:" + throwable.getMessage());
                                            Toast.makeText(getActivity(),"提交的采选单正在审核",Toast.LENGTH_SHORT).show();
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }

                                            mListData.get(position).setAddMiningList(false);
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
    //取消采选
    private void deleteMiningList(final int bookId, final int type, final int position) {
        if (mUserInfo != null) {
//            if (mProgressDialog == null) {
//                mProgressDialog = ProgressDialog.show(getActivity(), null, "加载中...");
//            }

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
                                                mListData.get(position).setAddMiningList(false);
                                            }else {
                                                mListData.get(position).setAddMiningList(true);
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }
                                            mListData.get(position).setAddMiningList(true);
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
    //设置头部
    private void setupHeader(List<PgAdvertisement> list) {

        Log.d("setupHeader : ", list.toString());

//        EndlessViewPager loopViewPager = (EndlessViewPager) mHeader.findViewById(R.id.viewPager);
          LoopViewPagerLayout  loopViewPager= (LoopViewPagerLayout) mHeader.findViewById(R.id.viewPager);
//          AdAdapter pagerAdapter = new AdAdapter();
//          pagerAdapter.setItem(list);
//          loopViewPager.setAdapter(pagerAdapter);
        loopViewPager.initializeData(getActivity().getApplicationContext());
        ArrayList<BannerInfo> bannerInfos=new ArrayList<>();
        for(PgAdvertisement pgAdvertisement:list){
            bannerInfos.add(new BannerInfo<PgAdvertisement>(pgAdvertisement,null));
        }
        loopViewPager.setOnLoadImageViewListener(new OnDefaultImageViewLoader() {
            @Override
            public void onLoadImageView(ImageView imageView, Object parameter) {
                Glide.with(getActivity())
                        .load(((PgAdvertisement)parameter).getImageUrl())
                        .into(imageView);
            }
        });
        loopViewPager.setOnBannerItemClickListener(new OnBannerItemClickListener() {
            @Override
            public void onBannerClick(int index, ArrayList<BannerInfo> banner) {
                PgAdvertisement advertisement= (PgAdvertisement) banner.get(index).data;

                int type = advertisement.getType();
                Log.i("cccccccccccccc","type========"+type);
                if(type == 1){//广告
                    Intent intent =  new Intent(getActivity(), WebActivity.class);
                    intent.putExtra(WebActivity.INTENT_POST_URL,advertisement.getLinkUrl());
                    startActivity(intent);
                }else {//图书
                    Intent intent = new Intent(getActivity(), CloudBookstoreBookDetailActivity.class);
                    intent.putExtra(Constant.BOOK_ID, advertisement.getBookId());
//                        intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, mCardId);
                    if(type == 2){
                        intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, Constant.CLOUD_BOOKSTORE_BOOK);
                    }else {
                        intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, Constant.CLOUD_BOOKSTORE_POD);
                    }
                    startActivity(intent);
                }

            }
        });
        loopViewPager.setLoopData(bannerInfos);


//
//        EndlessCirclePageIndicator indicator = (EndlessCirclePageIndicator) mHeader.findViewById(R.id.viewPagerIndicator);
//        indicator.setViewPager(loopViewPager);
    }

//    private Observable<List<PgAdvertisement>> fakeHeader() {
//        List<PgAdvertisement> list = new ArrayList<>();
//        Random random = new Random();
//        for (int i = 0; i < 5; i++) {
//            PgAdvertisement pgAdvertisement = new PgAdvertisement();
//            int r = random.nextInt(3);
//            if (r == 0)
//                pgAdvertisement.setImageUrl("http://d.hiphotos.baidu.com/image/pic/item/d058ccbf6c81800ac29f7279b33533fa828b474b.jpg");
//            else if (r == 1)
//                pgAdvertisement.setImageUrl("http://c.hiphotos.baidu.com/image/pic/item/d009b3de9c82d158a65450b0820a19d8bd3e42d8.jpg");
//            else
//                pgAdvertisement.setImageUrl("http://a.hiphotos.baidu.com/image/pic/item/ae51f3deb48f8c5444f332973b292df5e0fe7f62.jpg");
//            list.add(pgAdvertisement);
//        }
//        return Observable.just(list);
//    }

    class AdAdapter extends PagerAdapter {
        private List<PgAdvertisement> mList;

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        public void setItem(List<PgAdvertisement> list) {
            mList = list;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (getCount() == 0)
                return null;

            final PgAdvertisement advertisement = mList.get(position);
            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getActivity(), "广告", Toast.LENGTH_SHORT).show();
                    int type = advertisement.getType();
                    if(type == 1){//广告
                        Intent intent =  new Intent(getActivity(), WebActivity.class);
                        intent.putExtra(WebActivity.INTENT_POST_URL,advertisement.getLinkUrl());
                        startActivity(intent);
                    }else {//图书
                        Intent intent = new Intent(getActivity(), CloudBookstoreBookDetailActivity.class);
                        intent.putExtra(Constant.BOOK_ID, advertisement.getBookId());
//                        intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, mCardId);
                        if(type == 2){
                            intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, Constant.CLOUD_BOOKSTORE_BOOK);
                        }else {
                            intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, Constant.CLOUD_BOOKSTORE_POD);
                        }
                        startActivity(intent);
                    }

                }
            });

            container.addView(imageView);
            Glide.with(getActivity())
                    .load(advertisement.getImageUrl())
                    .into(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //listview滚动的时候快速设置值，而不必每次都重新创建很多对象，从而提升性能。
    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_tag)
        TextView tvTag;
        @Bind(R.id.ll_more)
        LinearLayout llMore;
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
        @Bind(R.id.desc)
        TextView desc;
        @Bind(R.id.price)
        TextView price;
        @Bind(R.id.old_price)
        TextView oldPrice;
        @Bind(R.id.cb_select)
        ImageView cbSelect;
        @Bind(R.id.ll_select)
        LinearLayout mSelectLL;
        @Bind(R.id.tv_select)
        TextView tvSelect;
        @Bind(R.id.item_liner_layout)
        LinearLayout itemLinerLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    //执行该方法时，Fragment处于活动状态，用户可与之交互。
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("云书城页");
    }
    //执行该方法时，Fragment处于暂停状态，但依然可见，用户不能与之交互。
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("云书城页");
    }

}
