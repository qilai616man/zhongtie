package com.crphdm.dl2.activity.personal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.ParallaxRecyclerAdapter;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.manager.ShoppingManager;
import com.digital.dl2.business.core.obj.PgPayOrderDetail;
import com.digital.dl2.business.core.obj.PgPayOrderInfo;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
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
 * 订单详情
 */

public class PayManagerDetailActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 5312;
    public static final String ORDER_ID = "orderId";
    @Bind(R.id.recycler)
    RecyclerView mRecycler;
    private List<PgPayOrderDetail> mList = new ArrayList<>();
    private ParallaxRecyclerAdapter<PgPayOrderDetail> mAdapter;
    private View mHeader;
    private HeaderViewHolder mHeaderViewHolder;

    private int mAddressId;
    private int mShoppingCartBookType;
    private int mShoppingCatyBookId;
    private int mOrderId;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;
    private PgPayOrderInfo mPgPayOrderInfo;
    private boolean mPaySuccess;

    private float mPayPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_manager_detail);
        ButterKnife.bind(this);
//        refreshData();
//        submitOrderByBook();
        Ln.d("PayManagerDetailActivity:onCreate");

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAddressId = getIntent().getIntExtra(Constant.ADDRESS_ID, 0);
        mShoppingCartBookType = getIntent().getIntExtra(Constant.SHOPPING_CART_ORDER_TYPE, 0);
        mShoppingCatyBookId = getIntent().getIntExtra(Constant.BOOK_ID, 0);
        mOrderId = getIntent().getIntExtra(ORDER_ID, 0);

        Ln.d("PayManagerDetailActivity:onCreate:mAddressId:" + mAddressId);
        Ln.d("PayManagerDetailActivity:onCreate:mShoppingCartBookType:" + mShoppingCartBookType);
        Ln.d("PayManagerDetailActivity:onCreate:mShoppingCatyBookId:" + mShoppingCatyBookId);
        Ln.d("PayManagerDetailActivity:onCreate:mOrderId:" + mOrderId);

        initMembers();
        initData();
    }

    private void initMembers() {
        Ln.d("PayManagerDetailActivity:initMembers");
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mHeader = getLayoutInflater().inflate(R.layout.layout_pay_detail_header, null);
        mHeaderViewHolder = new HeaderViewHolder(mHeader);

        mAdapter = new ParallaxRecyclerAdapter<>(mList);
        mAdapter.setShouldClipView(false);
        mAdapter.setParallaxHeader(mHeader, mRecycler);
        mAdapter.implementRecyclerAdapterMethods(new ParallaxRecyclerAdapter.RecyclerAdapterMethods() {
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                ViewHolder holder = (ViewHolder) viewHolder;
                PgPayOrderDetail pgPayOrderDetail = mList.get(i);
                holder.goodsName.setText(pgPayOrderDetail.getGoodsName());
                holder.bookCount.setText(pgPayOrderDetail.getGoodsNumber() + "本");
                holder.priceOrigin.setText(String.valueOf(pgPayOrderDetail.getPressPrice()));
//                holder.pricePrivilege.setText(String.valueOf(pgPayOrderDetail.getPressPrice()));
                holder.priceReal.setText(String.valueOf(pgPayOrderDetail.getPrice()));
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new ViewHolder(getLayoutInflater().inflate(R.layout.item_personal_good_info, viewGroup, false));
            }

            @Override
            public int getItemCount() {
                return mList == null ? 0 : mList.size();
            }
        });

        mRecycler.setAdapter(mAdapter);

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);


    }

    private void initData() {
        Ln.d("PayManagerDetailActivity:initData");
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
                            Ln.d("PayManagerDetailActivity:initData:getTokenAsync:mToken:" + mToken);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                            Ln.d("PayManagerDetailActivity:initData:getTokenAsync:error:" + throwable.getMessage());
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            refreshData();
                            Ln.d("PayManagerDetailActivity:initData:getTokenAsync:ok");
                        }
                    });
        }
    }

    private void refreshData() {
        Ln.d("PayManagerDetailActivity:refreshData");
        if (mShoppingCatyBookId != 0) {//直接购买
            Ln.d("PayManagerDetailActivity:refreshData:直接购买");
            submitOrderByBook();
        } else {
            if (mOrderId != 0) {//获取订单详情
                Ln.d("PayManagerDetailActivity:refreshData:获取订单详情");
                getOrderDetail();
            } else {//购物车生成订单
                Ln.d("PayManagerDetailActivity:refreshData:购物车生成订单");
                submitOrderByShoppingCart();
            }
        }
    }

    /**
     * 直接购买
     */
    private void submitOrderByBook() {
        ShoppingManager.getInstance().submitOrderByBook(
                mUserInfo.getUserid(),
                mToken,
                mShoppingCartBookType,
                mShoppingCatyBookId,
                mAddressId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<PgPayOrderInfo>() {
                    @Override
                    public void call(PgPayOrderInfo pgPayOrderInfo) {
                        Ln.d("PayManagerDetailActivity:refreshData:submitOrderByBook:pgOrderForm:" + pgPayOrderInfo.toString());
                        mList.clear();
                        mList = pgPayOrderInfo.getOrderDetailList();
                        setupHeader(pgPayOrderInfo);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        Ln.d("PayManagerDetailActivity:refreshData:submitOrderByBook:error:" + throwable.getMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        Ln.d("PayManagerDetailActivity:refreshData:submitOrderByBook:ok");
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * 获取订单详情
     */
    private void getOrderDetail() {
        PersonalCenterManager.getInstance().getOrderDetail(mToken, mOrderId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<PgPayOrderInfo>() {
                    @Override
                    public void call(PgPayOrderInfo orderInfo) {
                        Ln.d("PayManagerDetailActivity:refreshData:getOrderDetail:orderInfo:" + orderInfo.toString());
                        setupHeader(orderInfo);
                        mList.clear();
                        mList.addAll(orderInfo.getOrderDetailList());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        Ln.d("PayManagerDetailActivity:refreshData:getOrderDetail:error:" + throwable.getMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        mAdapter.notifyDataSetChanged();
                        Ln.d("PayManagerDetailActivity:refreshData:getOrderDetail:ok");
                    }
                });
    }

    /**
     * 生成订单
     */
    private void submitOrderByShoppingCart() {
        ShoppingManager.getInstance().submitOrderByShoppingCart(
                mUserInfo.getUserid(),
                mToken,
                mShoppingCartBookType,
                mAddressId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<PgPayOrderInfo>() {
                    @Override
                    public void call(PgPayOrderInfo pgPayOrderInfo) {
                        Ln.d("PayManagerDetailActivity:refreshData:submitOrderByShoppingCart:pgOrderForm:" + pgPayOrderInfo.toString());
                        setupHeader(pgPayOrderInfo);
                        mList.clear();
                        mList.addAll(pgPayOrderInfo.getOrderDetailList());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        Ln.d("PayManagerDetailActivity:refreshData:submitOrderByShoppingCart:error:" + throwable.getMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        mAdapter.notifyDataSetChanged();
                        Ln.d("PayManagerDetailActivity:refreshData:submitOrderByShoppingCart:ok");
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mPaySuccess) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        super.onBackPressed();
    }

    private void setupHeader(final PgPayOrderInfo pgPayOrderInfo) {
        mPgPayOrderInfo = pgPayOrderInfo;
        Ln.d("PayManagerDetailActivity:setupHeader:PgOrderForm:" + pgPayOrderInfo.toString());
        mOrderId = pgPayOrderInfo.getOrderId();
        if(pgPayOrderInfo.getAddress() != null){
            mAddressId = pgPayOrderInfo.getAddress().getAddressId();
        }
        mPayPrice = mPgPayOrderInfo.getPreferentialPrice();

        Ln.d("PayManagerDetailActivity:setupHeader:one");

        mHeaderViewHolder.orderNumber.setText(pgPayOrderInfo.getOrderSn());
        if(pgPayOrderInfo.getType() == 2){
            mHeaderViewHolder.paperBook.setVisibility(View.GONE);
            mHeaderViewHolder.digitalBook.setVisibility(View.VISIBLE);
        }else {
            mHeaderViewHolder.paperBook.setVisibility(View.VISIBLE);
            mHeaderViewHolder.digitalBook.setVisibility(View.GONE);
        }
//        mHeaderViewHolder.digitalBook.setVisibility(pgPayOrderInfo.getType() == 2 ? View.VISIBLE : View.GONE);
//        mHeaderViewHolder.paperBook.setVisibility(pgPayOrderInfo.getType() == 1 ? View.VISIBLE : View.GONE);
//        mHeaderViewHolder.buyState.setText(pgPayOrderInfo.getStatus() == 2 ? "已付款" : "未付款");

        Ln.d("PayManagerDetailActivity:setupHeader:two");

        if (pgPayOrderInfo.getStatus() == 0) {//未支付
            mHeaderViewHolder.buyState.setText("未付款");
        } else if (pgPayOrderInfo.getStatus() == 1) {//已支付
            mHeaderViewHolder.buyState.setText("已支付");
        } else if (pgPayOrderInfo.getStatus() == 2) {//支付中遇到问题
            mHeaderViewHolder.buyState.setText("支付中遇到问题");
        } else if (pgPayOrderInfo.getStatus() == 3) {//取消或者删除
            mHeaderViewHolder.buyState.setText("已取消");
        }

        Ln.d("PayManagerDetailActivity:setupHeader:three");

        if (pgPayOrderInfo.getStatus() == 1 || pgPayOrderInfo.getStatus() == 3) {
            mHeaderViewHolder.btnCancel.setVisibility(View.GONE);
            mHeaderViewHolder.btnPay.setVisibility(View.GONE);
        } else {
            mHeaderViewHolder.btnCancel.setVisibility(View.VISIBLE);
            mHeaderViewHolder.btnPay.setVisibility(View.VISIBLE);
        }
        Ln.d("PayManagerDetailActivity:setupHeader:three:mAddressId:" + mAddressId);

        if (mHeaderViewHolder.address != null) {
            Ln.d("PayManagerDetailActivity:setupHeader:address != null");
        } else {
            Ln.d("PayManagerDetailActivity:setupHeader:address == null");
        }
        if(pgPayOrderInfo.getStatus() == 1 && pgPayOrderInfo.getType() == 1){
            mHeaderViewHolder.btnQuery.setVisibility(View.VISIBLE);
        }else{
            mHeaderViewHolder.btnQuery.setVisibility(View.GONE);
        }
        mHeaderViewHolder.btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayManagerDetailActivity.this ,LogisticTraceActivity.class);
                intent.putExtra("getOrderSn" ,pgPayOrderInfo.getOrderSn());
                startActivity(intent);
            }
        });
        if (mAddressId == 0) {
            mHeaderViewHolder.addressLL.setVisibility(View.GONE);

        } else {
            mHeaderViewHolder.addressLL.setVisibility(View.VISIBLE);

            mHeaderViewHolder.userName.setText(pgPayOrderInfo.getAddress().getName());
            mHeaderViewHolder.userPhone.setText(pgPayOrderInfo.getAddress().getPhone());
            mHeaderViewHolder.address.setText(pgPayOrderInfo.getAddress().getAddress());
        }

        mHeaderViewHolder.buyTime.setText(format.format(pgPayOrderInfo.getBuyTime() * 1000));
        mHeaderViewHolder.priceAll.setText(String.valueOf(pgPayOrderInfo.getAllPrice()));
        mHeaderViewHolder.priceReal.setText(String.valueOf(pgPayOrderInfo.getPreferentialPrice()));

//        mHeaderViewHolder.priceReal.setText("￥" + pgPayOrderInfo.get());
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.goods_name)
        TextView goodsName;
        @Bind(R.id.book_count)
        TextView bookCount;
        @Bind(R.id.price_origin)
        TextView priceOrigin;
        //        @Bind(R.id.price_privilege)
//        TextView pricePrivilege;
        @Bind(R.id.price_real)
        TextView priceReal;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class HeaderViewHolder {
        @Bind(R.id.order_number)
        TextView orderNumber;
        @Bind(R.id.paper_book)
        TextView paperBook;
        @Bind(R.id.digital_book)
        TextView digitalBook;
        @Bind(R.id.buy_time)
        TextView buyTime;
        @Bind(R.id.buy_state)
        TextView buyState;
        @Bind(R.id.price_all)
        TextView priceAll;
        //        @Bind(R.id.price_privilege)
//        TextView pricePrivilege;
        @Bind(R.id.price_real)
        TextView priceReal;
        @Bind(R.id.ll_address)
        LinearLayout addressLL;
        @Bind(R.id.tv_user_name)
        TextView userName;
        @Bind(R.id.tv_user_phone)
        TextView userPhone;
        @Bind(R.id.address)
        TextView address;
        @Bind(R.id.btn_cancel)
        Button btnCancel;
        @Bind(R.id.btn_pay)
        Button btnPay;
        @Bind(R.id.btn_query)
        Button btnQuery;
        @OnClick(R.id.btn_cancel)
        public void onCancelClick() {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(PayManagerDetailActivity.this, null, "加载中...");
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
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            PersonalCenterManager.getInstance().cancelOrder(mToken, mOrderId)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<Boolean>() {
                                        @Override
                                        public void call(Boolean aBoolean) {
                                            if (aBoolean) {
                                                Toast.makeText(PayManagerDetailActivity.this,
                                                        "取消订单成功", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }
                                            Ln.d("PayManagerDetailActivity:cancelOrder:error:" + throwable.getMessage());
                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }
                                            onBackPressed();
                                        }
                                    });
                        }
                    });
        }

        @OnClick(R.id.btn_pay)
        public void onPayClick() {
//            Toast.makeText(PayManagerDetailActivity.this, "支付", Toast.LENGTH_SHORT).show();
            //支付按钮
            if (mPgPayOrderInfo != null) {

//                if (mPayPrice < 0.01f) {//钱太少,就不跳转支付宝了
//                    ShoppingManager.getInstance().freePurchase()
//                } else {//进入支付宝   付款买书
                    String desc = "";
                    int i = 0;
                    for (PgPayOrderDetail detail : mList) {
                        if (i > 5) {
                            desc += "...";
                            break;
                        }
                        desc += detail.getGoodsName() + ",";
                        i++;
                    }

                    Intent intent = new Intent(PayManagerDetailActivity.this, PayActivity.class);
                    intent.putExtra(PayActivity.PARAMS_STRING_NAME, "图书");
                    intent.putExtra(PayActivity.PARAMS_STRING_DESC, desc);
                    intent.putExtra(PayActivity.PARAMS_FLOAT_PRICE, mPayPrice);
                    intent.putExtra(PayActivity.PARAMS_INT_ORDER_ID, mPgPayOrderInfo.getOrderId());
                    intent.putExtra(PayActivity.PARAMS_INT_ORDER_SN, mPgPayOrderInfo.getOrderSn());
                    startActivityForResult(intent, PayActivity.REQUEST_CODE_PAY);

//                }
            }
        }

        HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (PayActivity.REQUEST_CODE_PAY == requestCode) {
            if (resultCode == RESULT_OK) {
                mPaySuccess = true;
                Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
                mHeaderViewHolder.buyState.setText("已支付");
                mHeaderViewHolder.btnCancel.setVisibility(View.GONE);
                mHeaderViewHolder.btnPay.setVisibility(View.GONE);
//                getOrderDetail();
            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this,"支付失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onResume() {
        super.onResume();

        MobclickAgent.onPageStart("订单详情页");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("订单详情页");
        MobclickAgent.onPause(this);
    }
}
