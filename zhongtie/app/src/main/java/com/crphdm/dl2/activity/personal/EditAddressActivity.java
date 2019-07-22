package com.crphdm.dl2.activity.personal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.crphdm.dl2.adapter.library.AddressListAdapter;
import com.crphdm.dl2.listener.OnAddressChangeListener;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.manager.ShoppingManager;
import com.digital.dl2.business.core.obj.PgAddress;
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
/**
 * 修改地址页面
 */
public class EditAddressActivity extends AppCompatActivity {
    @Bind(R.id.et_name)
    EditText mName;
    @Bind(R.id.et_phone)
    EditText mPhone;
    @Bind(R.id.et_address)
    EditText mAddress;
    @Bind(R.id.et_save)
    TextView mSave;
    @Bind(R.id.rcl_address_list)
    RecyclerView mAddressList;

    private AddressListAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;

    private int mAddressId = 0;
    private int mModify = 0;

    private String mNameStr = "";
    private String mPhoneStr = "";
    private String mAddressStr = "";

    private int mShoppingCartBookType;
    private int mShoppingCatyBookId;

    //保存地址按钮
    @OnClick(R.id.et_save)
    void save() {
//        onBackPressed();
        if (mAddressId != 0) {//选择地址
            if (mModify != 0) {//修改地址
                updateAddress(mAddressId);
            } else {//选中地址
                startActivityPayManagerDetail();
            }
        } else {//新增地址
            addAddress();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mShoppingCartBookType = getIntent().getIntExtra(Constant.SHOPPING_CART_ORDER_TYPE, Constant.PAPER_BOOK);
        mShoppingCatyBookId = getIntent().getIntExtra(Constant.BOOK_ID,0);

        if(mShoppingCartBookType == Constant.E_BOOK){
            startActivityPayManagerDetail();
        }

        initMembers();
        initData();
        setListener();

    }

    private void initMembers() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mAddressList.setLayoutManager(linearLayoutManager);
        mAdapter = new AddressListAdapter(EditAddressActivity.this);

        mAddressList.setAdapter(mAdapter);
        mAddressList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);
    }

    //初始化曾用过地址列表
    //这里接受上一个页面传进来的参数
    private void initData() {
        mName.setText("");
        mPhone.setText("");
        mAddress.setText("");
        mSave.setText("使用此地址");
        mAddressId = 0;
        mModify = 0;

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
                            refreshData();
                        }
                    });
        }
    }
    //刷新数据
    private void refreshData() {
        ShoppingManager.getInstance().getCommonAddress(mUserInfo.getUserid(), mToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgAddress>>() {
                    @Override
                    public void call(List<PgAddress> pgAddresses) {
                        Ln.d("EditAddressActivity:refreshData:pgAddresses:" + pgAddresses);
                        mAdapter.setData(pgAddresses);
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
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        Ln.d("EditAddressActivity:refreshData:ok");
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }
    //设置监听
    private void setListener() {
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Ln.d("EditAddressActivity:setListener:mName:beforeTextChanged");

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Ln.d("EditAddressActivity:setListener:mName:onTextChanged");
                if (mAddressId == 0) {
                    mSave.setText("保存");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Ln.d("EditAddressActivity:setListener:mName:afterTextChanged");
                if(!mNameStr.equals(s.toString())){
                    mModify++;
                    mNameStr = s.toString();
                }
            }
        });

        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Ln.d("EditAddressActivity:setListener:mPhone:beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Ln.d("EditAddressActivity:setListener:mPhone:onTextChanged");
                if (mAddressId == 0) {
                    mSave.setText("保存");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Ln.d("EditAddressActivity:setListener:mPhone:afterTextChanged");
                if(!mPhoneStr.equals(s.toString())){
                    mModify++;
                    mPhoneStr = s.toString();
                }
            }
        });

        mAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Ln.d("EditAddressActivity:setListener:mAddress:beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Ln.d("EditAddressActivity:setListener:mAddress:onTextChanged");
                if (mAddressId == 0) {
                    mSave.setText("保存");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Ln.d("EditAddressActivity:setListener:mAddress:afterTextChanged");
                if(!mAddressStr.equals(s.toString())){
                    mModify++;
                    mAddressStr = s.toString();
                }
            }
        });

        mAdapter.setListener(new OnAddressChangeListener() {
            @Override
            public void onSelect(PgAddress address) {
                mAddressStr = address.getAddress();
                mPhoneStr = address.getPhone();
                mNameStr = address.getName();

                mAddressId = address.getAddressId();
                mName.setText(mNameStr);
                mPhone.setText(mPhoneStr);
                mAddress.setText(mAddressStr);
            }

            @Override
            public void onDelete(int id) {
                deleteAddress(id);
            }
        });
    }
    //添加地址
    private void addAddress() {
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

                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            ShoppingManager.getInstance().addAddress(
                                    mUserInfo.getUserid(),
                                    mToken,
                                    mNameStr,
                                    mPhoneStr,
                                    mAddressStr)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<Boolean>() {
                                        @Override
                                        public void call(Boolean aBoolean) {
                                            if (aBoolean) {
                                                Toast.makeText(EditAddressActivity.this, "添加地址成功", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {

                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {
                                            initData();
                                        }
                                    });
                        }
                    });
        }
    }
    //更新地址
    private void updateAddress(final int id) {
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

                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            ShoppingManager.getInstance().changeAddress(
                                    mUserInfo.getUserid(),
                                    mToken,
                                    id,
                                    mNameStr,
                                    mPhoneStr,
                                    mAddressStr)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<Boolean>() {
                                        @Override
                                        public void call(Boolean aBoolean) {
                                            if (aBoolean) {
                                                Toast.makeText(EditAddressActivity.this, "修改地址成功", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {

                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {
                                            startActivityPayManagerDetail();
                                        }
                                    });
                        }
                    });
        }
    }
    //删除地址
    private void deleteAddress(final int id) {
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

                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            ShoppingManager.getInstance().deleteAddress(
                                    mUserInfo.getUserid(),
                                    mToken,
                                    id)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<Boolean>() {
                                        @Override
                                        public void call(Boolean aBoolean) {
                                            if (aBoolean) {
                                                Toast.makeText(EditAddressActivity.this, "删除地址成功", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {

                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {
                                            initData();
                                        }
                                    });
                        }
                    });
        }
    }
    //启动支付页面
    private void startActivityPayManagerDetail() {
        Ln.d("EditAddressActivity:startActivity:mAddressId:" + mAddressId);
        Ln.d("EditAddressActivity:startActivity:mShoppingCartBookType:" + mShoppingCartBookType);

        Intent intent = new Intent(EditAddressActivity.this, PayManagerDetailActivity.class);
        intent.putExtra(Constant.SHOPPING_CART_ORDER_TYPE, mShoppingCartBookType);
        intent.putExtra(Constant.BOOK_ID,mShoppingCatyBookId);
        intent.putExtra(Constant.ADDRESS_ID, mAddressId);
        startActivity(intent);
        finish();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_edit_address, menu);
//        return true;
//    }
    //处理菜单被选中运行后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //恢复
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("修改地址页面");
        MobclickAgent.onResume(this);
    }
    //暂停
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("修改地址页面");
        MobclickAgent.onPause(this);
    }
}
