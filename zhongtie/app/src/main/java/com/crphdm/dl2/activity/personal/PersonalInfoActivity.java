package com.crphdm.dl2.activity.personal;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.WelcomeActivity;
import com.crphdm.dl2.activity.login.LoginActivity;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.UserModuleImpl;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.utils.UpgradeHelper;
import com.digital.dl2.business.core.module.ShoppingModule;
import com.goyourfly.gdownloader.utils.Ln;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PersonalInfoActivity extends AppCompatActivity {
    //个人中心
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    //头像显示
    @Bind(R.id.profile_image)
    CircleImageView profileImage;
    @Bind(R.id.layout_user_name)
    TextView mUserName;
    //购物车数量显示TextVew
    @Bind(R.id.badage_1)
    TextView badageShoppingCart;
    @Bind(R.id.layout_select)
    RelativeLayout mSelect;//采选单
    @Bind(R.id.layout_user_advice_select)
    RelativeLayout mUserAdviceSelect;//用户推荐采选
    @Bind(R.id.layout_check_select)
    RelativeLayout mCheckSelect;//审核采选单

//    TextView phoneBindState;
    @Bind(R.id.layout_org)
    RelativeLayout layoutOrg;
    @Bind(R.id.bind_org)
    TextView bindOrg;
    @Bind(R.id.tv_personal_org_bind_state)
    TextView orgBindState;
    @Bind(R.id.layout_my_order)
    RelativeLayout mMyYuDing;
    @Bind(R.id.layout_my_upload)
    RelativeLayout mMyUpload;

    @Bind(R.id.tv_personal_version)
    TextView version;
    @Bind(R.id.layout_icon)
    LinearLayout mIconLayout;

    @Bind(R.id.layout_logout)
    RelativeLayout mLayoutLogout;

    private UserInfo mUserInfoFirst;
    private UserInfo mUserInfoSecond;
    private String mToken;
    private String mShoppingCartBookNumberStr;

    private String mVersionName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        ButterKnife.bind(this);

        Ln.d("PersonalInfoActivity:onCreate");

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("个人中心");
        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
            mIconLayout.setEnabled(false);
        }else{
            mIconLayout.setEnabled(true);
        }
        getIsExhibition();
        initMembers();
    }

    private boolean isExhibition;
    private void getIsExhibition(){
        UserModuleImpl.getInstance().getExhibition()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        isExhibition = aBoolean;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (isExhibition) {
                            mLayoutLogout.setVisibility(View.GONE);
                        } else {
                            mLayoutLogout.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void initMembers() {
        mUserInfoFirst = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);
        mUserInfoSecond = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND);
        if (mUserInfoFirst != null) {
            Ln.d("PersonalInfoActivity:mUserInfoFirst:" + mUserInfoFirst.toString());
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
                        }
                    });

            if(mUserInfoFirst.getNickname() != null && !mUserInfoFirst.getNickname().equals("")){
                mUserName.setText(mUserInfoFirst.getNickname());
            }else {
                if(mUserInfoFirst.getTruename() != null && !mUserInfoFirst.getTruename().equals("")){
                    mUserName.setText(mUserInfoFirst.getTruename());
                }else {
                    mUserName.setText(mUserInfoFirst.getUsername() == null ? "" : mUserInfoFirst.getUsername());
                }
            }

            Ln.d("PersonalInfoActivity:mUserInfoFirst:photo:" + mUserInfoFirst.getPhoto());
            if (mUserInfoFirst.getPhoto() != null && !mUserInfoFirst.getPhoto().equals("")) {
                profileImage.setImageResource(R.drawable.drw_1_touxiang_new);
                ImageLoader.getInstance().displayImage(mUserInfoFirst.getPhoto(), profileImage);
            } else {
                profileImage.setImageResource(R.drawable.drw_1_touxiang_new);
            }

            if (mUserInfoFirst.getMobile() != null && !mUserInfoFirst.getMobile().equals("")) {
//                phoneNum.setText(mUserInfoFirst.getMobile());
//                phoneBindState.setVisibility(View.GONE);
//                layoutPhone.setEnabled(false);
            } else {
//                phoneBindState.setVisibility(View.VISIBLE);
//                layoutPhone.setEnabled(true);
            }

            if (mUserInfoFirst.getOrg_name() != null && !mUserInfoFirst.getOrg_name().equals("")) {
                bindOrg.setText(mUserInfoFirst.getOrg_name());
                orgBindState.setVisibility(View.GONE);
            } else {
                orgBindState.setVisibility(View.VISIBLE);
            }

            if (mUserInfoFirst.getBind_org_state() == Constant.ORG_BIND_STATE_BINDING) {
                orgBindState.setText("绑定中");
                layoutOrg.setEnabled(false);
            }else if(mUserInfoFirst.getBind_org_state() == Constant.ORG_BIND_STATE_BIND_SUCCESS){
                layoutOrg.setEnabled(false);
            } else {
                layoutOrg.setEnabled(true);
            }

            mSelect.setVisibility(View.GONE);
            mUserAdviceSelect.setVisibility(View.GONE);
            mCheckSelect.setVisibility(View.GONE);

            Ln.d("PersonalInfoActivity:Role:" + UserModule.getInstance().getRole());
            if (UserModule.getInstance().getRole() == Constant.USER_ROLE_PUTONG) {//普通用户

            } else if (UserModule.getInstance().getRole() == Constant.USER_ROLE_JIGOU) {//机构用户

            } else if (UserModule.getInstance().getRole() == Constant.USER_ROLE_CAIXUANYUAN) {//采选员
                mSelect.setVisibility(View.VISIBLE);
                mUserAdviceSelect.setVisibility(View.VISIBLE);
            } else if (UserModule.getInstance().getRole() == Constant.USER_ROLE_LINGDAO) {//机构领导
                mCheckSelect.setVisibility(View.VISIBLE);
            } else {

            }

            if(mUserInfoSecond != null && mUserInfoSecond.getUserid() != 0){
                mMyYuDing.setVisibility(View.VISIBLE);
            }else {
                mMyYuDing.setVisibility(View.GONE);
            }

            UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            Ln.d("PersonalInfoActivity:mToken:" + s);
                            initShoppingCartBookNumber(s);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Ln.d("PersonalInfoActivity:mToken:error:" + throwable.getMessage());

                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            Ln.d("PersonalInfoActivity:mToken:ok");
                        }
                    });

        } else {
            Ln.d("PersonalInfoActivity:mUserInfoFirst == null");
            if (mUserInfoSecond != null) {
                Ln.d("PersonalInfoActivity:mUserInfoSecond:" + mUserInfoSecond.toString());
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
                            }
                        });

                if(mUserInfoSecond.getNickname() != null && !mUserInfoSecond.getNickname().equals("")){
                    mUserName.setText(mUserInfoSecond.getNickname());
                }else {
                    if(mUserInfoSecond.getTruename() != null && !mUserInfoSecond.getTruename().equals("")){
                        mUserName.setText(mUserInfoSecond.getTruename());
                    }else {
                        mUserName.setText(mUserInfoSecond.getUsername() == null ? "" : mUserInfoSecond.getUsername());
                    }
                }

                Ln.d("PersonalInfoActivity:mUserInfoSecond:photo:" + mUserInfoSecond.getPhoto());
                if (mUserInfoSecond.getPhoto() != null && !mUserInfoSecond.getPhoto().equals("")) {
                    profileImage.setImageResource(R.drawable.drw_1_touxiang_new);
                    ImageLoader.getInstance().displayImage(mUserInfoSecond.getPhoto(), profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.drw_1_touxiang_new);
                }

                if (mUserInfoSecond.getMobile() != null && !mUserInfoSecond.getMobile().equals("")) {
//                    phoneNum.setText(mUserInfoSecond.getMobile());
//                    phoneBindState.setVisibility(View.GONE);
//                layoutPhone.setEnabled(false);
                } else {
//                    phoneBindState.setVisibility(View.VISIBLE);
//                    layoutPhone.setEnabled(true);
                }

                if (mUserInfoSecond.getOrg_name() != null && !mUserInfoSecond.getOrg_name().equals("")) {
                    bindOrg.setText(mUserInfoSecond.getOrg_name());
                    orgBindState.setVisibility(View.GONE);
                } else {
                    orgBindState.setVisibility(View.VISIBLE);
                }

                if (mUserInfoSecond.getBind_org_state() == Constant.ORG_BIND_STATE_BINDING) {
                    orgBindState.setText("绑定中");
                    layoutOrg.setEnabled(false);
                }else if(mUserInfoSecond.getBind_org_state() == Constant.ORG_BIND_STATE_BIND_SUCCESS){
                    layoutOrg.setEnabled(false);
                } else {
                    layoutOrg.setEnabled(true);
                }

                mSelect.setVisibility(View.GONE);
                mUserAdviceSelect.setVisibility(View.GONE);
                mCheckSelect.setVisibility(View.GONE);

                mMyYuDing.setVisibility(View.VISIBLE);
//                mMyUpload.setVisibility(View.VISIBLE);

//                if (mUserInfoSecond != null && mUserInfoSecond.getUserid() != 0) {
//                    mMyYuDing.setVisibility(View.VISIBLE);
//                    mMyUpload.setVisibility(View.VISIBLE);
//                } else {
//                    mMyYuDing.setVisibility(View.GONE);
//                    mMyUpload.setVisibility(View.GONE);
//                }

            }else {
                mMyYuDing.setVisibility(View.GONE);
//                mMyUpload.setVisibility(View.GONE);
            }
        }

        try {
            mVersionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            version.setText("V" + mVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initShoppingCartBookNumber(String token) {
        ShoppingModule.getInstance().getShoppingCartBookNumber(mUserInfoFirst.getUserid(), token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mShoppingCartBookNumberStr = String.valueOf(integer);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //错误
                        throwable.printStackTrace();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        badageShoppingCart.setText(mShoppingCartBookNumberStr);
                    }
                });
    }

    /**
     * 头像
     */
    @OnClick(R.id.layout_icon)
    public void onIconClick() {
        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
            Toast.makeText(PersonalInfoActivity.this,"此用户为临时用户",Toast.LENGTH_SHORT).show();
        }else{
            if (UserModuleImpl.getInstance().isLogin()) {
//            startActivity(new Intent(this, UpdateUserInfoActivity.class));
                startActivityForResult(new Intent(this, UpdateUserInfoActivity.class), UpdateUserInfoActivity.REQUEST_CODE);
            } else {
                Intent intent = new Intent(PersonalInfoActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

    }

//    /**
//     *
//     */
//    @OnClick(R.id.bg_img)
//    public void onBgImgClick() {
//        startActivity(new Intent(this, UpdateUserInfoActivity.class));
//    }

    /**
     * 购物车
     */
    @OnClick(R.id.layout_shopping_cart)
    public void onShoppingCartClick() {
        if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_FIRST_LEVEL ||
                UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {//1级网络和1级2级共有
//            startActivity(new Intent(this, ShoppingCartActivity.class));
        } else {
            Toast.makeText(PersonalInfoActivity.this, "亲爱的用户，看不到我？请用手机连接互联网试一试", Toast.LENGTH_SHORT).show();
        }

    }



    /**
     * 采选单
     */
    @OnClick(R.id.layout_select)
    public void onSelectClick() {
//        startActivity(new Intent(this, PickActivity.class));
        if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_FIRST_LEVEL ||
                UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {//1级网络和1级2级共有
            Intent intent = new Intent(this, PickActivity.class);
            intent.putExtra(Constant.PICK_ACTIVITY_BY_WHICH, Constant.FROM_USER_CENTER);
            startActivity(intent);
        } else {
            Toast.makeText(PersonalInfoActivity.this, "亲爱的用户，看不到我？请用手机连接互联网试一试", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 用户推荐采选单
     */
    @OnClick(R.id.layout_user_advice_select)
    public void onUserRecommendSelectClick() {
        if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_FIRST_LEVEL ||
                UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {//1级网络和1级2级共有
            startActivity(new Intent(this, UserRecommendSelectListActivity.class));
        } else {
            Toast.makeText(PersonalInfoActivity.this, "亲爱的用户，看不到我？请用手机连接互联网试一试", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 审批采选单
     */
    @OnClick(R.id.layout_check_select)
    public void onCheckSelectClick() {
        if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_FIRST_LEVEL ||
                UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {//1级网络和1级2级共有
            startActivity(new Intent(this, ApprovalListActivity.class));
        } else {
            Toast.makeText(PersonalInfoActivity.this, "亲爱的用户，看不到我？请用手机连接互联网试一试", Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * 绑定机构
     */
    @OnClick(R.id.layout_org)
    public void onBindOrgClick() {
        if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_FIRST_LEVEL ||
                UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
            startActivityForResult(new Intent(this, BindOrganizationActivity.class), BindOrganizationActivity.REQUEST_CODE);
        } else {
            Toast.makeText(PersonalInfoActivity.this, "亲爱的用户，看不到我？请用手机连接互联网试一试", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 我的分组
     */
    @OnClick(R.id.layout_my_group)
    public void onGroupClick() {
        Intent intent = new Intent(this, MyGroupActivity.class);
        startActivity(intent);
    }

    /**
     * 我的收藏
     */
    @OnClick(R.id.layout_favorite)
    public void onFavoriteClick() {
        if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_FIRST_LEVEL ||
                UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
            Intent intent = new Intent(this, CollectionReserveBuyListActivity.class);
            intent.putExtra(CollectionReserveBuyListActivity.INTENT_TITLE, "我的收藏");
            intent.putExtra(CollectionReserveBuyListActivity.INTENT_TYPE, CollectionReserveBuyListActivity.TYPE_COLLECTION);
            startActivity(intent);
        }else {
            Toast.makeText(PersonalInfoActivity.this,"亲爱的用户，看不到我？请用手机连接互联网试一试？",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 我的预订
     */
    @OnClick(R.id.layout_my_order)
    public void onMyOrderClick() {
        if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_SECOND_LEVEL ||
                UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
            Intent intent = new Intent(this, CollectionReserveBuyListActivity.class);
            intent.putExtra(CollectionReserveBuyListActivity.INTENT_TITLE, "我的预订");
            intent.putExtra(CollectionReserveBuyListActivity.INTENT_TYPE, CollectionReserveBuyListActivity.TYPE_RESERVATION);
            startActivity(intent);
        }else {
            Toast.makeText(PersonalInfoActivity.this,"亲爱的用户，不对头呦！你们单位网络把我挡外面啦，领我进去嘛",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 我的购买
     */
    @OnClick(R.id.layout_my_goods)
    public void onMyGoodsClick() {
        if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_FIRST_LEVEL ||
                UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
            Intent intent = new Intent(this, CollectionReserveBuyListActivity.class);
            intent.putExtra(CollectionReserveBuyListActivity.INTENT_TITLE, "已购买");
            intent.putExtra(CollectionReserveBuyListActivity.INTENT_TYPE, CollectionReserveBuyListActivity.TYPE_BOUGHT);
            startActivity(intent);
        }else {
            Toast.makeText(PersonalInfoActivity.this,"亲爱的用户，看不到我？请用手机连接互联网试一试？",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 我的上传
     */
    @OnClick(R.id.layout_my_upload)
    public void onMyUploadClick() {
        startActivity(new Intent(this, MyUploadActivity.class));
    }

    /**
     * 意见反馈
     */
    @OnClick(R.id.layout_feedback)
    public void onFeedbackClick() {
        if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_FIRST_LEVEL ||
                UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
            startActivity(new Intent(this, FeedbackActivity.class));
        }else {
            Toast.makeText(PersonalInfoActivity.this,"亲爱的用户，看不到我？请用手机连接互联网试一试？",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 退出登录
     */
    @OnClick(R.id.layout_logout)
    public void onLogout() {
//        UserModuleImpl.getInstance().logout();
//        PublicManager.getInstance().clearData();
//        BookshelfManager.getInstance().clearData();
//        GDownloader.getInstance().clearAll();

        UserModule.getInstance().saveLoginState(false);

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constant.NOT_SHOW_WELCOME, Constant.NOT_SHOW_WELCOME);
        startActivity(intent);
        finish();
    }

    /**
     * 系统升级
     */
    @OnClick(R.id.layout_upgrade)
    public void onUpgradeClick() {
        UpgradeHelper.checkUpgrade(
                PersonalInfoActivity.this,
                false,
                com.jiuwei.upgrade_package_new.lib.Constant.DIALOG_STYLE_ELDERLY_ASSISTANT);

//        Toast.makeText(PersonalInfoActivity.this, "暂无新版本", Toast.LENGTH_SHORT).show();
    }

    public void onResume() {
        super.onResume();
        initMembers();
        MobclickAgent.onPageStart("个人中心");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("个人中心");
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Ln.d("PersonalInfoActivity:onActivityResult:requestCode:" + requestCode);
        Ln.d("PersonalInfoActivity:onActivityResult:resultCode:" + resultCode);
        if (requestCode == BindPhoneActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Ln.d("PersonalInfoActivity:onActivityResult:data:" + data.getStringExtra(BindPhoneActivity.INTENT_PHONE));
//                phoneNum.setText(data.getStringExtra(BindPhoneActivity.INTENT_PHONE));
//                phoneBindState.setVisibility(View.GONE);
//                layoutPhone.setEnabled(false);
            }
        } else if (requestCode == BindOrganizationActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Ln.d("PersonalInfoActivity:onActivityResult:data:" + data.getStringExtra(BindOrganizationActivity.INTENT_ORG));
                bindOrg.setText(data.getStringExtra(BindOrganizationActivity.INTENT_ORG));
                orgBindState.setText("绑定中");
            }
        }else if(requestCode == UpdateUserInfoActivity.REQUEST_CODE){
            if(resultCode == RESULT_OK){
                initMembers();
            }
        }
    }
}
