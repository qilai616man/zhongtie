package com.crphdm.dl2.activity.cloud_bookstore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainsoon.utils.BookUtils;
import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.personal.PickActivity;

import com.crphdm.dl2.adapter.personal.PreviewListAdapter;
import com.crphdm.dl2.service.MyService;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.utils.Util;
import com.crphdm.dl2.views.BeautifulProgressbar;
import com.digital.dl2.business.core.manager.BookshelfManager;
import com.digital.dl2.business.core.manager.CloudBookstoreManager;
import com.digital.dl2.business.core.manager.PublicManager;
import com.digital.dl2.business.core.manager.ShoppingManager;
import com.digital.dl2.business.core.module.BookshelfModule;
import com.digital.dl2.business.core.obj.PgBookForBookstoreDetail;
import com.digital.dl2.business.core.obj.PgCloudBookstoreRealBook;
import com.digital.dl2.business.core.obj.PgProbationImage;
import com.digital.dl2.business.core.obj.PgResult;
import com.digital.dl2.business.core.obj.PgShoppingCart;
import com.digital.dl2.business.util.DownloadEvent;
import com.goyourfly.gdownloader.db.DbDownloadExt;
import com.goyourfly.gdownloader.utils.ErrorUtils;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
/**
 * 图书详情页面
 */
public class CloudBookstoreBookDetailActivity extends AppCompatActivity {
    @Bind(R.id.bookCover)
    ImageView mBookCover;
    @Bind(R.id.progress)
    BeautifulProgressbar progressbar;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.rd_book)
    RadioButton mBook;
    @Bind(R.id.rd_ebook)
    RadioButton mEbook;
    @Bind(R.id.rd_e_book_one)
    RadioButton mEBookOne;
    @Bind(R.id.rd_e_book_two)
    RadioButton mEBookTwo;
    @Bind(R.id.rd_e_book_three)
    RadioButton mEBookThree;
    @Bind(R.id.rd_e_book_four)
    RadioButton mEBookFour;
    @Bind(R.id.ll_select)
    LinearLayout mSelect;
    @Bind(R.id.ll_like)
    LinearLayout mLike;
    @Bind(R.id.ll_buy)
    LinearLayout mBuy;
    @Bind(R.id.ll_recommend_pick)
    LinearLayout mRecommendPick;
    @Bind(R.id.ll_pick)
    LinearLayout mPick;
    @Bind(R.id.ll_shopcart)
    LinearLayout mShopcart;
    @Bind(R.id.tv_author)
    TextView mAuthor;
    @Bind(R.id.tv_time)
    TextView mTime;
    @Bind(R.id.tv_number)
    TextView mNumber;
    @Bind(R.id.tv_publish)
    TextView mPublish;
    @Bind(R.id.tv_desc)
    TextView mDesc;
    @Bind(R.id.tv_contents)
    TextView mContents;
    @Bind(R.id.rd_book_group)
    RadioGroup mBookGroup;
    @Bind(R.id.cb_like)
    ImageView mCBLike;
    @Bind(R.id.rcl_preview_list)
    RecyclerView mPreviewList;
    @Bind(R.id.iv_buyicon)
    ImageView mBuyIcon;
    @Bind(R.id.iv_shopcarticon)
    ImageView mShopcarticon;
    @Bind(R.id.tv_likeText)
    TextView mLikeText;
    @Bind(R.id.tv_buyText)
    TextView mBuyText;
    @Bind(R.id.iv_recommend_pick_icon)
    ImageView mRecommendPickIcon;
    @Bind(R.id.tv_recommend_pick_text)
    TextView mRecommendPickText;
    @Bind(R.id.iv_pick_icon)
    ImageView mPickIcon;
    @Bind(R.id.tv_pick_text)
    TextView mPickText;
    @Bind(R.id.tv_shopcartText)
    TextView mShopcartText;
    @Bind(R.id.rd_book_type_group)
    RadioGroup mBookTypeGroup;
    @Bind(R.id.tv_book_price)
    TextView mBookPrice;
    @Bind(R.id.tv_book_fixprice)
    TextView mBookFixprice;
    @Bind(R.id.ll_provisionality)
    LinearLayout mProvisioalityLL;

    private TextView mMenuChopcastCountTextView, mMenuPickDetailCountTextView;

    private boolean isLike = true;
    private boolean isBuy = true;
    private boolean isPick = true;
    private boolean isRecommendPick = false;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private int SHITISHU = 1;
    private int SHILIANGTU = 2;
    private int DAIMA = 3;
    private int SHUANGCENG = 4;
    private int XML = 5;
    private int POD = 6;

    private Context mContext;

    private PgCloudBookstoreRealBook mRealBook;

    private int mShopCartCount, mPickCount;

    private PreviewListAdapter mPreviewListAdapter;
    private CloudBookstoreManager mCloudBookstoreManager;
    private List<PgBookForBookstoreDetail> mListData;
    private PgBookForBookstoreDetail mBookDetail;

    private UserInfo mUserInfo;

    private String mToken;
    private int mBookId;
    private int mCardId;

    private String[] uuids;
    private String mUUIDFirst;
    private String mUUIDSecond;

    private int mBookType = Constant.BOOK_TYPE_E_BOOK;

    private ProgressDialog mProgressDialog;

    private Menu mMenu;

    @OnClick(R.id.ll_like)
    void like() {
        if(mBookDetail == null){
            Toast.makeText(this,"找不到图书信息",Toast.LENGTH_SHORT).show();
            return;
        }
        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
            Toast.makeText(this,"此用户为临时用户",Toast.LENGTH_SHORT).show();
        }else{
            if (mUserInfo != null) {
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(CloudBookstoreBookDetailActivity.this, null, "处理中");
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
                                if (mBookDetail.isCollect()) {// 已收藏   执行取消收藏操作
                                    mCloudBookstoreManager.cancelCollectBookById(mUserInfo.getUserid(), mToken, mBookDetail.getEntityId())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.newThread())
                                            .subscribe(new Action1<Boolean>() {
                                                @Override
                                                public void call(Boolean aBoolean) {
                                                    mBookDetail.setIsCollect(!aBoolean);
                                                    if (aBoolean) {
                                                        Ln.d("CloudBookstoreBookDetailActivity:取消收藏成功");
                                                        Toast.makeText(CloudBookstoreBookDetailActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Ln.d("CloudBookstoreBookDetailActivity:取消收藏失败");
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
                                                    Toast.makeText(
                                                            CloudBookstoreBookDetailActivity.this,
                                                            "取消收藏失败:" + ErrorUtils.getError(throwable),
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            },  new Action0() {
                                                @Override
                                                public void call() {
                                                    //完成
                                                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                        mProgressDialog.dismiss();
                                                        mProgressDialog = null;
                                                    }
                                                    if (mBookDetail.isCollect()) {
                                                        mCBLike.setImageResource(R.drawable.draw_book_detail_like_blue);
                                                        mLikeText.setText("已收藏");
                                                        isLike = mBookDetail.isCollect();
                                                    } else {
                                                        mCBLike.setImageResource(R.drawable.draw_book_detail_like);
                                                        mLikeText.setText("收藏");
                                                        isLike = mBookDetail.isCollect();
                                                    }
                                                }
                                            });
                                } else { //未收藏   执行收藏操作
                                    mCloudBookstoreManager.collectBookById(mUserInfo.getUserid(), mToken, mBookDetail.getEntityId())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.newThread())
                                            .subscribe(new Action1<Boolean>() {
                                                @Override
                                                public void call(Boolean aBoolean) {
                                                    mBookDetail.setIsCollect(aBoolean);
                                                    if (aBoolean) {
                                                        Ln.d("CloudBookstoreBookDetailActivity:收藏成功");
                                                    } else {
                                                        Ln.d("CloudBookstoreBookDetailActivity:收藏失败");
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
//                                                Toast.makeText(CloudBookstoreBookDetailActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }, new Action0() {
                                                @Override
                                                public void call() {
                                                    //完成
                                                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                        mProgressDialog.dismiss();
                                                        mProgressDialog = null;
                                                    }
                                                    if (mBookDetail.isCollect()) {
                                                        mCBLike.setImageResource(R.drawable.draw_book_detail_like_blue);
                                                        mLikeText.setText("已收藏");
                                                        isLike = mBookDetail.isCollect();
                                                    } else {
                                                        mCBLike.setImageResource(R.drawable.draw_book_detail_like);
                                                        mLikeText.setText("收藏");
                                                        isLike = mBookDetail.isCollect();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }
        }


    }

    /**
     * 2017.11.08 ymd
     * 修改加入购物车、购买不可用（隐藏）
     */
    //购买
//    @OnClick(R.id.ll_buy)
//    void buy() {
//        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
//            Toast.makeText(this,"此用户为临时用户",Toast.LENGTH_SHORT).show();
//
//        }else{
//            if (mCardId == Constant.CLOUD_BOOKSTORE_BOOK) {//图书模式
//                if (mBookType == Constant.BOOK_TYPE_E_BOOK) {//电子书
//                    if (mBookDetail.isBuy()) {//已购买   执行阅读操作
//                        readBook();
//                    } else {//购买  执行购买操作
//                        //跳转到选择地址页  然后到生成订单
//                        startActivityToEditAddress();
//                    }
//                } else {//实体书或POD  执行购买操作
//                    //跳转到选择地址页  然后到生成订单
//                    startActivityToEditAddress();
//                }
//
//            } else {//POD模式
//                if (mBookDetail.isBuy()) {//已购买  显示已购买  不执行任何操作
//
//                } else {//购买  执行购买操作
//                    //跳转到选择地址页  然后到生成订单
//                    startActivityToEditAddress();
//                }
//            }
//        }
//
//    }

    //采选按钮
    @OnClick(R.id.ll_pick)
    void pick() {
        if(mBookDetail == null){
            Toast.makeText(this,"找不到图书信息",Toast.LENGTH_SHORT).show();
            return;
        }
        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
            Toast.makeText(this,"此用户为临时用户",Toast.LENGTH_SHORT).show();
        }else{
            Ln.d("CloudBookstoreBookDetailActivity:pick:mCardId:" + mCardId);
            Ln.d("CloudBookstoreBookDetailActivity:pick:mBookType:" + mBookType);
            if (mCardId == Constant.CLOUD_BOOKSTORE_POD || mBookType == Constant.BOOK_TYPE_REAL_BOOK) {//实体书无法采选
                Toast.makeText(mContext, "实体书无法采选", Toast.LENGTH_SHORT).show();
            } else {
                if (mUserInfo != null) {
                    if (mProgressDialog == null) {
                        mProgressDialog = ProgressDialog.show(CloudBookstoreBookDetailActivity.this, null, "处理中");
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
                                    mCloudBookstoreManager.addMiningListById(
                                            mUserInfo.getOrg_id(),
                                            mUserInfo.getUserid(),
                                            mToken,
                                            mBookDetail.getEntityId(),
                                            Constant.BOOK_TYPE_E_BOOK,
                                            1)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.newThread())
                                            .subscribe(new Action1<PgResult>() {
                                                @Override
                                                public void call(PgResult result) {
                                                    if (result.isStatus()) {
                                                        Ln.d("CloudBookstoreBookDetailActivity:采选成功");
                                                        mPickText.setText("已采选");
                                                        mPick.setEnabled(false);
                                                    } else {
                                                        Ln.d("CloudBookstoreBookDetailActivity:采选失败");
                                                        mPickText.setText("加入采选单");
                                                        mPick.setEnabled(true);
                                                        Toast.makeText(mContext, result.getMessage(), Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(CloudBookstoreBookDetailActivity.this, "提交的采选单正在审核", Toast.LENGTH_SHORT).show();
                                                }
                                            }, new Action0() {
                                                @Override
                                                public void call() {
                                                    //完成
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
        }

    }

    //推荐采选
    @OnClick(R.id.ll_recommend_pick)
    void recommendPick() {
        if(mBookDetail == null){
            Toast.makeText(this,"找不到图书信息",Toast.LENGTH_SHORT).show();
            return;
        }
        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
            Toast.makeText(this,"此用户为临时用户",Toast.LENGTH_SHORT).show();
        }else{
            if (mCardId == Constant.CLOUD_BOOKSTORE_POD || mBookType == Constant.BOOK_TYPE_REAL_BOOK) {//实体书不能被推荐采选
                Toast.makeText(mContext, "实体书不能被推荐采选", Toast.LENGTH_SHORT).show();
            } else {
                if (UserModule.getInstance().getRole() != Constant.USER_ROLE_PUTONG) {//普通用户

                    if (mUserInfo != null) {

                        if (mProgressDialog == null) {
                            mProgressDialog = ProgressDialog.show(CloudBookstoreBookDetailActivity.this, null, "处理中");
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
                                        mCloudBookstoreManager.recommendMiningById(
                                                mUserInfo.getUserid(),
                                                mToken,
                                                mUserInfo.getOrg_id(),
                                                mBookDetail.getEntityId())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribeOn(Schedulers.newThread())
                                                .subscribe(new Action1<Boolean>() {
                                                    @Override
                                                    public void call(Boolean aBoolean) {
                                                        if (aBoolean) {
                                                            Ln.d("CloudBookstoreBookDetailActivity:推荐采选成功");
                                                        } else {
                                                            Ln.d("CloudBookstoreBookDetailActivity:推荐采选失败");
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
//                                            Toast.makeText(CloudBookstoreBookDetailActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }, new Action0() {
                                                    @Override
                                                    public void call() {
                                                        //完成
                                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                            mProgressDialog.dismiss();
                                                            mProgressDialog = null;
                                                        }
                                                        mRecommendPickIcon.setImageResource(R.drawable.draw_book_detail_pick_blue);
                                                        mRecommendPickText.setText("已推荐");
                                                        isRecommendPick = true;
                                                        mRecommendPick.setEnabled(false);
                                                    }
                                                });
                                    }
                                });
                    }

                } else {
                    Toast.makeText(mContext, "非机构用户不能推荐采选", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    //加入购物车
    @OnClick(R.id.ll_shopcart)
    void shopCart() {
        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
            Toast.makeText(this,"此用户为临时用户",Toast.LENGTH_SHORT).show();
        }else{
            if (mUserInfo != null) {
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(CloudBookstoreBookDetailActivity.this, null, "处理中");
                }

                UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                mToken = s;
                                Ln.d("CloudBookstoreBookDetailActivity:加入购物车:getTokenAsync:token:" + mToken);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                //错误
                                Ln.d("CloudBookstoreBookDetailActivity:加入购物车:getTokenAsync:error");
                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                    mProgressDialog.dismiss();
                                    mProgressDialog = null;
                                }
                            }
                        }, new Action0() {
                            @Override
                            public void call() {
                                //完成
                                Ln.d("CloudBookstoreBookDetailActivity:加入购物车:bookId:" + mBookDetail.getEntityId());
                                ShoppingManager.getInstance()
                                        .addBookToShoppingCart(mUserInfo.getUserid(), mToken, mBookDetail.getEntityId(), mBookType, 1)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.newThread())
                                        .subscribe(new Action1<List<PgShoppingCart>>() {
                                            @Override
                                            public void call(List<PgShoppingCart> pgShoppingCarts) {
                                                Ln.d("CloudBookstoreBookDetailActivity:加入购物车成功");
//                                                setShopCartState(false);
                                            }
                                        }, new Action1<Throwable>() {
                                            @Override
                                            public void call(Throwable throwable) {
                                                Ln.d("CloudBookstoreBookDetailActivity:加入购物车:error：" + throwable.getMessage());
                                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                    mProgressDialog.dismiss();
                                                    mProgressDialog = null;
                                                }
                                                Toast.makeText(CloudBookstoreBookDetailActivity.this, "加入购物车失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }, new Action0() {
                                            @Override
                                            public void call() {
                                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                    mProgressDialog.dismiss();
                                                    mProgressDialog = null;
                                                }
                                                setupActionbar();
                                            }
                                        });
                            }
                        });
            } else {
                Ln.d("CloudBookstoreBookDetailActivity:加入购物车:用户信息为空");
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("图书详情");
        }
        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
            mProvisioalityLL.setVisibility(View.GONE);
        }else{
            mProvisioalityLL.setVisibility(View.VISIBLE);
        }
        mBookId = getIntent().getIntExtra(Constant.BOOK_ID, 0);
        mCardId = getIntent().getIntExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, Constant.CLOUD_BOOKSTORE_BOOK);

        Ln.d("CloudBookstoreBookDetailActivity:mBookId:" + mBookId);
        Ln.d("CloudBookstoreBookDetailActivity:mCardId:" + mCardId);

        initView();
        setupActionbar();
//        setShopCartState(true);
    }

    private void initView() {
        mContext = this;
        mCloudBookstoreManager = CloudBookstoreManager.getInstance();
        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);

        if (mUserInfo != null) {
            mUUIDFirst = mUserInfo.getFirstKey();
            mUUIDSecond = mUserInfo.getSecondKey();
            Ln.d("BookcaseFragment:initMembers:mUUIDFirst:" + mUUIDFirst);
            Ln.d("BookcaseFragment:initMembers:mUUIDSecond:" + mUUIDSecond);

            if (mUUIDFirst == null) {
//                Toast.makeText(mContext, "获取First_UUID失败", Toast.LENGTH_SHORT).show();
            }

            if (mUUIDSecond == null) {
//                Toast.makeText(mContext, "获取Second_UUID失败", Toast.LENGTH_SHORT).show();
            }

            uuids = new String[]{mUUIDFirst, mUUIDSecond};

            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(CloudBookstoreBookDetailActivity.this, null, "加载中");
            }

            UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            Ln.d("CloudBookstoreBookDetailActivity:token:" + s);

                            mCloudBookstoreManager.getBookDetail(mUserInfo.getUserid(), s, mCardId, mBookId)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<List<PgBookForBookstoreDetail>>() {
                                        @Override
                                        public void call(List<PgBookForBookstoreDetail> bookList) {
                                            mListData = bookList;

                                            Ln.d("CloudBookstoreBookDetailActivity:mListData:" + mListData);
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

                                            initEBookType(mBookId);
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
                        }
                    }, new Action0() {
                        @Override
                        public void call() {

                        }
                    });
        }
    }

    private void initEBookType(int bookId) {
        mEBookOne.setVisibility(View.INVISIBLE);
        mEBookTwo.setVisibility(View.INVISIBLE);
        mEBookThree.setVisibility(View.INVISIBLE);
        mEBookFour.setVisibility(View.INVISIBLE);

        Ln.d("CloudBookstoreBookDetailActivity:initEBookType:mListData.size:" + mListData.size());
        Ln.d("CloudBookstoreBookDetailActivity:initEBookType:mListData:" + mListData);

        if (mListData.size() > 0) {
            mEBookOne.setVisibility(View.VISIBLE);
            mEBookOne.setText(mListData.get(0).getTypeName());
            if (mListData.size() > 1) {
                mEBookTwo.setVisibility(View.VISIBLE);
                mEBookTwo.setText(mListData.get(1).getTypeName());
                if (mListData.size() > 2) {
                    mEBookThree.setVisibility(View.VISIBLE);
                    mEBookThree.setText(mListData.get(2).getTypeName());
                    if (mListData.size() > 3) {
                        mEBookFour.setVisibility(View.VISIBLE);
                        mEBookFour.setText(mListData.get(3).getTypeName());
                    }
                }
            }
        }

        initBookDetail(bookId);
    }

    private void initBookDetail(int bookId) {
        for (int i = 0; i < mListData.size(); i++) {
            PgBookForBookstoreDetail book = mListData.get(i);
            if (bookId == book.getEntityId()) {
                //设置书籍信息
                setupBookInfo(book);

                if (i == 0) {
                    mEBookOne.setChecked(true);
                } else if (i == 1) {
                    mEBookTwo.setChecked(true);
                } else if (i == 2) {
                    mEBookThree.setChecked(true);
                } else if (i == 3) {
                    mEBookFour.setChecked(true);
                }

            }
        }

    }

    private void setupPrice(boolean isRealBook) {
        Ln.d("CloudBookstoreBookDetailActivity:setupPrice:price:" + mBookDetail.getPrice());
        Ln.d("CloudBookstoreBookDetailActivity:setupPrice:pressPrice:" + mBookDetail.getPressPrice());
        if (isRealBook) {//显示实体书价格
            mBookPrice.setText("￥" + mBookDetail.getRealBook().getPrice());
            mBookFixprice.setText("￥" + mBookDetail.getRealBook().getPressPrice());
        } else {//显示电子书价格
            mBookPrice.setText("￥" + mBookDetail.getPrice());
            mBookFixprice.setText("￥" + mBookDetail.getPressPrice());
        }
    }

    private void setupBookInfo(PgBookForBookstoreDetail bookstoreDetail) {
        Ln.d("CloudBookstoreBookDetailActivity:setupBookInfo:bookstoreDetail:" + bookstoreDetail.toString());

        mBookDetail = bookstoreDetail;

        mRealBook = mBookDetail.getRealBook();

        isBuy = mBookDetail.isBuy();
        isLike = mBookDetail.isCollect();

        boolean isHaveRealBook = mBookDetail.isHaveRealBook();

        if (isHaveRealBook) {
            mBook.setEnabled(true);
        } else {
            mBook.setEnabled(false);
        }

        Glide.with(CloudBookstoreBookDetailActivity.this)
                .load(mBookDetail.getFrontCover())
                .placeholder(R.drawable.drw_book_default)
                .crossFade()
                .into(mBookCover);

        mTitle.setText(mBookDetail.getName());
        mAuthor.setText(mBookDetail.getAuthor());
        mTime.setText(mBookDetail.getPublishDate());
        mNumber.setText(mBookDetail.getBookNumber());
        mPublish.setText(mBookDetail.getPublishingHouse());
        mDesc.setText(mBookDetail.getIntroduction());

        if (UserModule.getInstance().getRole() == Constant.USER_ROLE_CAIXUANYUAN) {//采选员
            mRecommendPick.setVisibility(View.GONE);
            mPick.setVisibility(View.VISIBLE);
        } else {//普通用户
            mRecommendPick.setVisibility(View.VISIBLE);
            mPick.setVisibility(View.GONE);
        }

        if (mCardId != Constant.CLOUD_BOOKSTORE_BOOK) {//pod  隐藏电子书
            mBookType = Constant.BOOK_TYPE_POD;
            mSelect.setVisibility(View.GONE);
            mEbook.setVisibility(View.GONE);
            mBook.setChecked(true);
            setupPrice(true);
        }

        //设置目录
        setupContents(mBookDetail.getCatalog());
        //设置购买状态
        setupIsBuy(mBookDetail.isBuy());
        //设置收藏状态
        setupLike(mBookDetail.isCollect());
        //设置图书格式状态
        setupBookGroupButton();
        //设置预览页状态
        setupPreviewList();

        //设置价格
        if (mBookType == Constant.BOOK_TYPE_REAL_BOOK) {//实体书
            setupPrice(true);
        } else {
            setupPrice(false);
        }

        //设置加入购物车状态
//        setShopCartState(true);
        //设置电子书格式
        setupBookTypeGroup();
    }

    //目录
    private void setupContents(String[] contents) {
        StringBuffer buffer = new StringBuffer();
        for (String string : contents) {
            buffer.append(string + "\n");
        }
        mContents.setText(buffer.toString());
    }

    //是否购买
    private void setupIsBuy(boolean isBuy) {
        if (mCardId == Constant.CLOUD_BOOKSTORE_BOOK) {
            if (isBuy) {//已购买
                mBuyIcon.setImageResource(R.drawable.drw_book_detail_read_select);
                mBuyText.setText("阅读");
            } else {
//                mBuyIcon.setImageResource(R.drawable.draw_book_detail_buy);
//                mBuyText.setText("购买");
            }
        } else {
//
//            mBuyIcon.setImageResource(R.drawable.draw_book_detail_buy);
//            mBuyText.setText("购买");

        }
    }

    //是否收藏
    private void setupLike(boolean isLike) {
        if (isLike) {
            mCBLike.setImageResource(R.drawable.draw_book_detail_like_blue);
            mLikeText.setText("已收藏");
        } else {
            mCBLike.setImageResource(R.drawable.draw_book_detail_like);
            mLikeText.setText("收藏");
        }
    }

    //实体书还是电子书
    private void setupBookGroupButton() {
        mBookGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == mBook.getId()) {    //点击实体书
                    mRecommendPickIcon.setImageResource(R.drawable.draw_book_detail_pick);
                    mRecommendPickText.setText("推荐采选");
                    mRecommendPick.setEnabled(true);
                    mBookType = Constant.BOOK_TYPE_REAL_BOOK;
                    mSelect.setVisibility(View.GONE);
//                    mBuyIcon.setImageResource(R.drawable.draw_book_detail_buy);
//                    mBuyText.setText("购买");
//                    setShopCartState(true);
                    setupPrice(true);
                } else if (checkedId == mEbook.getId()) {   //点击电子书
                    if (isRecommendPick) {
                        mRecommendPickIcon.setImageResource(R.drawable.draw_book_detail_pick_blue);
                        mRecommendPickText.setText("已推荐");
                        mRecommendPick.setEnabled(false);
                    } else {
                        mRecommendPickIcon.setImageResource(R.drawable.draw_book_detail_pick);
                        mRecommendPickText.setText("推荐采选");
                        mRecommendPick.setEnabled(true);
                    }

                    if (isBuy) {//已购买
                        mBuyIcon.setImageResource(R.drawable.drw_book_detail_read_select);
                        mBuyText.setText("阅读");
                    } else {
//                        mBuyIcon.setImageResource(R.drawable.draw_book_detail_buy);
//                        mBuyText.setText("购买");
                    }

                    mBookType = Constant.BOOK_TYPE_E_BOOK;
                    mSelect.setVisibility(View.VISIBLE);
//                    setShopCartState(true);
                    mEBookOne.setChecked(true);
                    setupPrice(false);
                }


            }
        });
    }

    //电子书的四种格式
    private void setupBookTypeGroup() {
        mBookTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Ln.d("CloudBookstoreBookDetailActivity:setupBookTypeGroup:checkedId:" + checkedId);
                Ln.d("CloudBookstoreBookDetailActivity:setupBookTypeGroup:checkedId:" + checkedId);
                if (mEBookOne != null) {
                    Ln.d("CloudBookstoreBookDetailActivity:setupBookTypeGroup:mEBookOne.getId():" + mEBookOne.getId());
                }

                if (mEBookTwo != null) {
                    Ln.d("CloudBookstoreBookDetailActivity:setupBookTypeGroup:mEBookTwo.getId():" + mEBookTwo.getId());
                }

                if (mEBookThree != null) {
                    Ln.d("CloudBookstoreBookDetailActivity:setupBookTypeGroup:mEBookThree.getId():" + mEBookThree.getId());
                }

                if (mEBookFour != null) {
                    Ln.d("CloudBookstoreBookDetailActivity:setupBookTypeGroup:mEBookFour.getId():" + mEBookFour.getId());
                }

                if (checkedId == mEBookOne.getId()) {
                    PgBookForBookstoreDetail book = mListData.get(0);
                    Ln.d("CloudBookstoreBookDetailActivity:setupBookTypeGroup:mEBookOne:" + book.toString());
                    //设置书籍信息
                    setupBookInfo(book);
                } else if (checkedId == mEBookTwo.getId()) {
                    PgBookForBookstoreDetail book = mListData.get(1);
                    Ln.d("CloudBookstoreBookDetailActivity:setupBookTypeGroup:mEBookTwo:" + book.toString());
                    //设置书籍信息
                    setupBookInfo(book);
                } else if (checkedId == mEBookThree.getId()) {
                    PgBookForBookstoreDetail book = mListData.get(2);
                    Ln.d("CloudBookstoreBookDetailActivity:setupBookTypeGroup:mEBookThree:" + book.toString());
                    //设置书籍信息
                    setupBookInfo(book);
                } else if (checkedId == mEBookFour.getId()) {
                    PgBookForBookstoreDetail book = mListData.get(3);
                    Ln.d("CloudBookstoreBookDetailActivity:setupBookTypeGroup:mEBookFour:" + book.toString());
                    //设置书籍信息
                    setupBookInfo(book);
                }
//                else{//helong
//                    PgBookForBookstoreDetail book = mListData.get(3);
//                    Ln.d("CloudBookstoreBookDetailActivity:setupBookTypeGroup:mEBookFour:" + book.toString());
//                    //设置书籍信息
//                    setupBookInfo(book);
//                }


            }
        });
    }

    //试读列表
    private void setupPreviewList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPreviewList.setLayoutManager(linearLayoutManager);
        mPreviewListAdapter = new PreviewListAdapter(CloudBookstoreBookDetailActivity.this);
//        mPreviewListAdapter.setData( mBookDetail.getProbationImages());

        Ln.d("CloudBookstoreBookDetailActivity:setupPreviewList:" + mBookDetail.getProbationImages());
        List<String> imageUrlList = new ArrayList<>();
        for (PgProbationImage pgImage : mBookDetail.getProbationImages()) {
            imageUrlList.add(pgImage.getImageUrl());
        }

//        mPreviewListAdapter.setData(mBookDetail.getProbationImages());
        mPreviewListAdapter.setData(imageUrlList);

        mPreviewList.setAdapter(mPreviewListAdapter);
    }

    //设置actionbar购物车和采选单数量
    private void setupActionbar() {
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
                            //错误
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            //完成
                            ShoppingManager.getInstance().getShoppingCartBookNumber(mUserInfo.getUserid(), mToken)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<Integer>() {
                                        @Override
                                        public void call(Integer integer) {
                                            mShopCartCount = integer;
                                            Ln.d("CloudBookstoreBookDetailActivity:mShopCartCount:" + mShopCartCount);
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {

                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {
                                            if (mShopCartCount > 0) {
                                                mMenuChopcastCountTextView.setVisibility(View.VISIBLE);
                                                mMenuChopcastCountTextView.setText(mShopCartCount + "");
                                            } else {
                                                mMenuChopcastCountTextView.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                        }
                    });


        } else {
//            Toast.makeText(CloudBookstoreBookDetailActivity.this, "mUserInfo=null", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 2017.11.08 ymd
     * 修改加入购物车、购买不可用（隐藏）
     */
    //设置加入购物车显示状态
//    private void setShopCartState(boolean state) {
//        if (state) {
//            mShopcarticon.setImageResource(R.drawable.shopcart_selector);
//            mShopcartText.setText("加入购物车");
//            mShopcart.setEnabled(true);
//        } else {
//            mShopcarticon.setImageResource(R.drawable.draw_book_detail_shopcart);
//            mShopcartText.setText("已加入购物车");
//            mShopcart.setEnabled(false);
//        }
//    }

    //直接购买  跳转到选择地址页
//    private void startActivityToEditAddress() {
//        Intent intent = new Intent(CloudBookstoreBookDetailActivity.this, EditAddressActivity.class);
//        intent.putExtra(Constant.SHOPPING_CART_ORDER_TYPE, mBookType);
//        intent.putExtra(Constant.BOOK_ID, mBookId);
//        startActivity(intent);
//    }

    public void onEventMainThread(DownloadEvent event) {
        final int bookId = Util.getBookIdByUrl(event.url);

        if (bookId == mBookDetail.getEntityId()) {
            mBookDetail.setDownloadState(event.state);

            Ln.d("onEventMainThread:" + event.state + "," + event.downloadedBytes + "/" + event.totalLength);

            switch (event.event) {
                case DownloadEvent.EVENT_PROGRESS:
                    mBookDetail.setDownloadProgress((float) event.downloadedBytes / (float) event.totalLength);
                    refreshDownloadState();
                    break;

                case DownloadEvent.EVENT_FINISH:
                    mBookDetail.setDownloadState(DbDownloadExt.DOWNLOAD_STATE_DOWNLOADED);
                    break;
                default:
                    refreshDownloadState();
                    break;
            }
        }
    }

    private void refreshDownloadState() {
        if (mBookDetail.getDownloadProgress() != 0) {
            if (mBookDetail.getDownloadProgress() < 0.01) {
                progressbar.to(0.01F);
            } else {
                progressbar.to(mBookDetail.getDownloadProgress());
            }
        }

        switch (mBookDetail.getDownloadState()) {
            case DbDownloadExt.DOWNLOAD_STATE_NOT_DOWNLOAD:
                progressbar.setVisibility(View.GONE);
                break;
            case DbDownloadExt.DOWNLOAD_STATE_PREPARING:
                progressbar.setVisibility(View.VISIBLE);
                break;
            case DbDownloadExt.DOWNLOAD_STATE_DOWNLOADING:
                progressbar.setVisibility(View.VISIBLE);
                break;
            case DbDownloadExt.DOWNLOAD_STATE_PAUSE:
                progressbar.setVisibility(View.VISIBLE);
                break;
            case DbDownloadExt.DOWNLOAD_STATE_WAITING:
                progressbar.setVisibility(View.VISIBLE);
                break;
            case DbDownloadExt.DOWNLOAD_STATE_DOWNLOADED:
                progressbar.setVisibility(View.GONE);
                break;
            case DbDownloadExt.DOWNLOAD_STATE_ERROR:
                progressbar.setVisibility(View.VISIBLE);
                break;
            default:
                progressbar.setVisibility(View.VISIBLE);
                break;
        }
    }

    //阅读图书
    private void readBook() {
        if (BookshelfManager.getInstance().getBookById(mBookDetail.getEntityId()) == null) {
            BookshelfManager.getInstance().insertBookForCloudBookstore(mBookDetail);
        }

        if (mBookDetail.getDownloadUrl() != null && !mBookDetail.getDownloadUrl().equals("")) {

            if (PublicManager.getInstance().isDownloadOk(mBookDetail.getDownloadUrl())) {//已经下载
//                Toast.makeText(mContext, "阅读图书", Toast.LENGTH_SHORT).show();

                final String fileUrl = PublicManager.getInstance().getFilePath(mBookDetail.getDownloadUrl());
                final String readUrl = com.digital.dl2.business.util.Constant.FILE_READ_PATH + String.valueOf(mBookDetail.getEntityId());

                Ln.d("CloudBookstoreBookDetailActivity:readBook:阅读:download:" + fileUrl);
                Ln.d("CloudBookstoreBookDetailActivity:readBook:阅读:read:" + readUrl);

                if (fileUrl != null) {

                    int unzipState = BookshelfManager.getInstance().getBookById(mBookDetail.getEntityId()).getUnzipState();
                    Ln.d("CloudBookstoreBookDetailActivity:readBook:阅读:unzipState:" + unzipState);
                    if (unzipState == com.digital.dl2.business.util.Constant.BOOK_UNZIP_STATE_SUCCESS) {
                        if (uuids.length != 0) {

                            String succeed = BookUtils.openTdpBook(
                                    fileUrl,
                                    readUrl,
                                    uuids,
                                    mContext);

                            Ln.d("CloudBookstoreBookDetailActivity:readBook:阅读:succeed:" + succeed);
                        } else {
                            Toast.makeText(mContext, "用户信息出错,请重新登录", Toast.LENGTH_SHORT).show();
                        }

                    } else if (unzipState == com.digital.dl2.business.util.Constant.BOOK_UNZIP_STATE_ING) {
                        Toast.makeText(mContext, "文件正在解压", Toast.LENGTH_SHORT).show();
                    } else {
                        Ln.d("CloudBookstoreBookDetailActivity:readBook:文件未解压");

                        Toast.makeText(mContext, "文件解压失败，正在重新尝试", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, MyService.class);
                        intent.putExtra(MyService.INTENT_CMD, MyService.CMD_UN_ZIP_FILE);
                        intent.putExtra(Constant.BOOK_ID, mBookDetail.getEntityId());
                        mContext.startService(intent);
                    }

                } else {
                    Ln.d("CloudBookstoreBookDetailActivity:readBook:文件路径为空");

                    Toast.makeText(mContext, "获取路径失败,请重新尝试", Toast.LENGTH_SHORT).show();
                }

            } else {
                Ln.d("CloudBookstoreBookDetailActivity:readBook:未下载下载:downloadUrl:" + mBookDetail.getDownloadUrl());
                PublicManager.getInstance().downloadBook(mBookDetail.getDownloadUrl());
                BookshelfModule.getInstance().insertBookstoreBook(mBookDetail);
//                             itemView.setEnabled(false);
            }
        } else {
            Ln.d("CloudBookstoreBookDetailActivity:readBook:下载路径出错:" + mBookDetail.getDownloadUrl());
            Toast.makeText(mContext, "获取下载路径失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cloud_bookstore_book_detail, mMenu);
        RelativeLayout shopcartLayout = (RelativeLayout) menu.findItem(R.id.action_shopcart).getActionView();
        RelativeLayout pickDetailLayout = (RelativeLayout) menu.findItem(R.id.action_pick_detail).getActionView();
        mMenuChopcastCountTextView = (TextView) shopcartLayout.findViewById(R.id.menu_shopcart_count);
        mMenuPickDetailCountTextView = (TextView) pickDetailLayout.findViewById(R.id.menu_pick_count);

        shopcartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 15/10/26
                if (UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY) {
                    Toast.makeText(CloudBookstoreBookDetailActivity.this, "此用户为临时用户", Toast.LENGTH_SHORT).show();
                } else {
                    Ln.d("CloudBookstoreBookDetailActivity:点击购物车");
//                    startActivity(new Intent(CloudBookstoreBookDetailActivity.this, ShoppingCartActivity.class));
                }

            }
        });

        pickDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 15/10/26
                if (UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY) {
                    Toast.makeText(CloudBookstoreBookDetailActivity.this, "此用户为临时用户", Toast.LENGTH_SHORT).show();
                } else {
                    Ln.d("CloudBookstoreBookDetailActivity:点击采选单");
                    Intent intent = new Intent(CloudBookstoreBookDetailActivity.this, PickActivity.class);
                    intent.putExtra(Constant.PICK_ACTIVITY_BY_WHICH, Constant.FROM_CLOUD_BOOK_MARKET);
                    startActivity(intent);
                }

            }
        });

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem pickDetailLayout = menu.findItem(R.id.action_pick_detail);
        if (UserModule.getInstance().getRole() != Constant.USER_ROLE_CAIXUANYUAN) {//采选员
            pickDetailLayout.setVisible(false);
        }
        MenuItem shopDetailLayout = menu.findItem(R.id.action_shopcart);
        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
            shopDetailLayout.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        setupActionbar();
//        setShopCartState(true);
        MobclickAgent.onPageStart("云书城图书详情页");
        MobclickAgent.onResume(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("云书城图书详情页");
        MobclickAgent.onPause(this);
    }
}
