package com.crphdm.dl2.activity.personal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.crphdm.dl2.adapter.personal.ShoppingCartAdapter;
import com.crphdm.dl2.listener.OnChangeCountListener;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.utils.FullyLinearLayoutManager;
import com.crphdm.dl2.views.Decoration;
import com.digital.dl2.business.core.manager.ShoppingManager;
import com.digital.dl2.business.core.obj.PgShoppingCart;
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

public class ShoppingCartActivity extends AppCompatActivity {
    //购物车页面
    @Bind(R.id.ll_5_shopping_cart_real_book)
    LinearLayout mRealBookLL;
    @Bind(R.id.tv_5_shopping_cart_real_book_type)
    TextView mTypeOneText;
    @Bind(R.id.rcl_shoppoing_cart_book_list)
    RecyclerView mRclShoppoingCartBookList;
    @Bind(R.id.tv_real_count)
    TextView mRealCount;
    @Bind(R.id.tv_total_real_price)
    TextView mRealTotalPrice;
    @Bind(R.id.tv_real_discount)
    TextView mRealDiscount;
    @Bind(R.id.tv_pay_for_real_book)
    TextView mPayForRealBook;

    @Bind(R.id.ll_5_shopping_cart_e_book)
    LinearLayout mEBookLL;
    @Bind(R.id.tv_5_shopping_cart_e_book_type)
    TextView mTypeTwoText;
    @Bind(R.id.rcl_shoppoing_cart_ebook_list)
    RecyclerView mShoppoingCartEbookList;
    @Bind(R.id.tv_ebook_count)
    TextView mEbookCount;
    @Bind(R.id.tv_total_ebook_price)
    TextView mTotalEbookPrice;
    @Bind(R.id.tv_ebook_discount)
    TextView mEbookDiscount;
    @Bind(R.id.tv_pay_for_ebook)
    TextView mPayForEbook;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;
    @Bind(R.id.item_liner_layout)
    LinearLayout itemLinearLayout;

    private ShoppingManager mShoppingManager;
    private List<PgShoppingCart> mListData;
    private PgShoppingCart mRealBookShoppingCart;
    private PgShoppingCart mEBookShoppingCart;

    private ProgressDialog mProgressDialog;

    private ShoppingCartAdapter mBookAdapter;
    private ShoppingCartAdapter mEBookAdapter;

    private UserInfo mUserInfo;
    private String mToken;


    @OnClick(R.id.tv_pay_for_real_book)
    void OnPayForBook() {
//        Toast.makeText(ShoppingCartActivity.this, "结算实体书", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ShoppingCartActivity.this, EditAddressActivity.class);
        intent.putExtra(Constant.SHOPPING_CART_ORDER_TYPE, Constant.PAPER_BOOK);
        startActivity(intent);
    }

    @OnClick(R.id.tv_pay_for_ebook)
    void OnPayForEbook() {
//        Toast.makeText(ShoppingCartActivity.this, "结算电子书", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ShoppingCartActivity.this, EditAddressActivity.class);
        intent.putExtra(Constant.SHOPPING_CART_ORDER_TYPE, Constant.E_BOOK);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);

        initMembers();
    }

    private void initMembers() {
        mShoppingManager = ShoppingManager.getInstance();
        mBookAdapter = new ShoppingCartAdapter();
        mEBookAdapter = new ShoppingCartAdapter();

        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(ShoppingCartActivity.this, null, "加载中...");
        }

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);

        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Ln.d("ShoppingCartActivity:initMembers:mToken:" + s);
                        mToken = s;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Ln.d("ShoppingCartActivity:initMembers:mToken:error:" + throwable.getMessage());
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        if (lLError != null) {
                            lLError.setVisibility(View.VISIBLE);
                        }
                        throwable.printStackTrace();
//                        Toast.makeText(ShoppingCartActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Ln.d("ShoppingCartActivity:initMembers:mToken:ok");
                        initData(mToken);
                        setListener();
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                    }
                });
    }

    @OnClick(R.id.lLError)
        public void onErrorClick() {
        initMembers();
    }

    private void initData(String token) {
        mShoppingManager.getShoppingCartList(mUserInfo.getUserid(), token, 1, 50)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgShoppingCart>>() {
                    @Override
                    public void call(List<PgShoppingCart> pgShoppingCarts) {
                        mListData = pgShoppingCarts;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //错误
                        throwable.printStackTrace();
//                        Toast.makeText(ShoppingCartActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
                            if (mListData.size() == 0) {
                                lLError.setVisibility(View.VISIBLE);
                                tvError.setText("暂无数据");
                            } else {
                                lLError.setVisibility(View.GONE);
                                itemLinearLayout.setVisibility(View.VISIBLE);
                            }
                        }
                        refreshData();
                    }
                });
    }

    //电子书和实体书分别用了两个recyclerview
    private void refreshData() {
        Ln.d("ShoppingCartActivity:refreshData");
        mRealBookLL.setVisibility(View.GONE);
        mEBookLL.setVisibility(View.GONE);

        if (mListData != null && !mListData.isEmpty()) {
            Ln.d("ShoppingCartActivity:refreshData:mListData:" + mListData.size());
            if (mListData.size() == 1) {
                if (mListData.get(0).getType() == 1) {
                    mRealBookShoppingCart = mListData.get(0);
                    mRealBookLL.setVisibility(View.VISIBLE);
                } else {
                    mEBookShoppingCart = mListData.get(0);
                    mEBookLL.setVisibility(View.VISIBLE);
                }
            } else if (mListData.size() == 2) {
                mRealBookLL.setVisibility(View.VISIBLE);
                mEBookLL.setVisibility(View.VISIBLE);

                if (mListData.get(0).getType() == 1) {
                    mRealBookShoppingCart = mListData.get(0);
                    mEBookShoppingCart = mListData.get(1);
                } else if (mListData.get(0).getType() == 2) {
                    mEBookShoppingCart = mListData.get(0);
                    mRealBookShoppingCart = mListData.get(1);
                }
            }

            if (mRealBookShoppingCart != null) {
                if (mRealBookShoppingCart.getType() == 1) {
                    mTypeOneText.setText("实体书");
                } else {
                    mTypeOneText.setText("电子书");
                }
                mRealCount.setText("共" + mRealBookShoppingCart.getNumber() + "本可购买");
                mRealTotalPrice.setText(String.valueOf(mRealBookShoppingCart.getAllPrice()));
                mRealDiscount.setText(String.valueOf(mRealBookShoppingCart.getPreferentialPrice()));

                mRclShoppoingCartBookList.setLayoutManager(new FullyLinearLayoutManager(ShoppingCartActivity.this));
//                mRclShoppoingCartBookList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
                Decoration dividerLine = new Decoration(Decoration.VERTICAL);
                dividerLine.setSize(1);
                dividerLine.setColor(0xFFDDDDDD);
                mRclShoppoingCartBookList.addItemDecoration(dividerLine);
                mBookAdapter.setData(ShoppingCartActivity.this, mRealBookShoppingCart.getType(), mRealBookShoppingCart.getBookItem());
                mRclShoppoingCartBookList.setAdapter(mBookAdapter);
            }

            if (mEBookShoppingCart != null) {
                if (mEBookShoppingCart.getType() == 1) {
                    mTypeTwoText.setText("实体书");
                } else {
                    mTypeTwoText.setText("电子书");
                }

                mEbookCount.setText("共" + mEBookShoppingCart.getNumber() + "本可购买");
                mTotalEbookPrice.setText(String.valueOf(mEBookShoppingCart.getAllPrice()));
                mEbookDiscount.setText(String.valueOf(mEBookShoppingCart.getPreferentialPrice()));

                mShoppoingCartEbookList.setLayoutManager(new FullyLinearLayoutManager(ShoppingCartActivity.this));
                Decoration dividerLine1 = new Decoration(Decoration.VERTICAL);
                dividerLine1.setSize(1);
                dividerLine1.setColor(0xFFDDDDDD);
                mShoppoingCartEbookList.addItemDecoration(dividerLine1);
//                mShoppoingCartEbookList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
                mEBookAdapter.setData(ShoppingCartActivity.this, mEBookShoppingCart.getType(), mEBookShoppingCart.getBookItem());
                mShoppoingCartEbookList.setAdapter(mEBookAdapter);
            }
        }else{
            if (lLError != null) {
                if (mListData.size() == 0) {
                    lLError.setVisibility(View.VISIBLE);
                    tvError.setText("暂无数据");
                } else {
                    lLError.setVisibility(View.GONE);
                    itemLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void setListener() {
        Ln.d("ShoppingCartActivity:setListener");

        mBookAdapter.setListener(new OnChangeCountListener() {
            @Override
            public void onAdd(final int id, final int type) {
//                Toast.makeText(ShoppingCartActivity.this, "实体书", Toast.LENGTH_SHORT).show();
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
                                mShoppingManager.addBookToShoppingCart(mUserInfo.getUserid(), mToken, id, type, 1)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.newThread())
                                        .subscribe(new Action1<List<PgShoppingCart>>() {
                                            @Override
                                            public void call(List<PgShoppingCart> pgShoppingCarts) {
                                                mListData = pgShoppingCarts;

                                            }
                                        }, new Action1<Throwable>() {
                                            @Override
                                            public void call(Throwable throwable) {
                                                throwable.printStackTrace();
//                                                Toast.makeText(ShoppingCartActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }, new Action0() {
                                            @Override
                                            public void call() {
                                                refreshData();
                                            }
                                        });
                            }
                        });

            }

            @Override
            public void onReduce(final int id, final int type) {
//                Toast.makeText(ShoppingCartActivity.this, "减少", Toast.LENGTH_SHORT).show();
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
                                mShoppingManager.deleteBookFromShoppingCart(mUserInfo.getUserid(), mToken, id, type, 1)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.newThread())
                                        .subscribe(new Action1<List<PgShoppingCart>>() {
                                            @Override
                                            public void call(List<PgShoppingCart> pgShoppingCarts) {
                                                mListData = pgShoppingCarts;

                                            }
                                        }, new Action1<Throwable>() {
                                            @Override
                                            public void call(Throwable throwable) {
                                                throwable.printStackTrace();
//                                                Toast.makeText(ShoppingCartActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }, new Action0() {
                                            @Override
                                            public void call() {
                                                refreshData();
                                            }
                                        });
                            }
                        });

            }

            @Override
            public void onDelete(final int id, final int type, final int number) {
//                Toast.makeText(ShoppingCartActivity.this, "删除", Toast.LENGTH_SHORT).show();
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
                                mShoppingManager.deleteBookFromShoppingCart(mUserInfo.getUserid(), mToken, id, type, number)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.newThread())
                                        .subscribe(new Action1<List<PgShoppingCart>>() {
                                            @Override
                                            public void call(List<PgShoppingCart> pgShoppingCarts) {
                                                mListData = pgShoppingCarts;

                                            }
                                        }, new Action1<Throwable>() {
                                            @Override
                                            public void call(Throwable throwable) {
                                                throwable.printStackTrace();
//                                                Toast.makeText(ShoppingCartActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }, new Action0() {
                                            @Override
                                            public void call() {
                                                refreshData();
                                            }
                                        });
                            }
                        });

            }
        });

        mEBookAdapter.setListener(new OnChangeCountListener() {
            @Override
            public void onAdd(int id, int type) {
                Toast.makeText(ShoppingCartActivity.this, "电子书无法修改数量", Toast.LENGTH_SHORT).show();
//                mShoppingManager.addBookToShoppingCart(1375, "123456", id, type, 1)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeOn(Schedulers.newThread())
//                        .subscribe(new Action1<List<PgShoppingCart>>() {
//                            @Override
//                            public void call(List<PgShoppingCart> pgShoppingCarts) {
//                                mListData = pgShoppingCarts;
//
//                            }
//                        }, new Action1<Throwable>() {
//                            @Override
//                            public void call(Throwable throwable) {
//
//                            }
//                        }, new Action0() {
//                            @Override
//                            public void call() {
//                                refreshData();
//                            }
//                        });
            }

            @Override
            public void onReduce(int id, int type) {
                Toast.makeText(ShoppingCartActivity.this, "电子书无法修改数量", Toast.LENGTH_SHORT).show();
//                mShoppingManager.deleteBookFromShoppingCart(1375, "123456", id, type, 1)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeOn(Schedulers.newThread())
//                        .subscribe(new Action1<List<PgShoppingCart>>() {
//                            @Override
//                            public void call(List<PgShoppingCart> pgShoppingCarts) {
//                                mListData = pgShoppingCarts;
//
//                            }
//                        }, new Action1<Throwable>() {
//                            @Override
//                            public void call(Throwable throwable) {
//
//                            }
//                        }, new Action0() {
//                            @Override
//                            public void call() {
//                                refreshData();
//                            }
//                        });
            }

            @Override
            public void onDelete(int id, int type, int number) {
                mShoppingManager.deleteBookFromShoppingCart(mUserInfo.getUserid(), mToken, id, type, number)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Action1<List<PgShoppingCart>>() {
                            @Override
                            public void call(List<PgShoppingCart> pgShoppingCarts) {
                                mListData = pgShoppingCarts;

                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
//                                Toast.makeText(ShoppingCartActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }, new Action0() {
                            @Override
                            public void call() {
                                refreshData();
                            }
                        });
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_shopping_cart, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        initMembers();
        MobclickAgent.onPageStart("购物车页面");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("购物车页面");
        MobclickAgent.onPause(this);
    }
}
