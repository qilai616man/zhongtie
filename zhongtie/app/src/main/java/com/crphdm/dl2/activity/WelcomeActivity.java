package com.crphdm.dl2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.login.LoginActivity;
import com.crphdm.dl2.service.MyService;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.UserModuleImpl;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.user.utils.MD5;
import com.crphdm.dl2.utils.Constant;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by qinqi on 15/11/19.
 */
public class WelcomeActivity extends Activity {
    private static final int ACTIVITY_REQUEST_CODE_LOGIN_ACTIVITY = 1;
    private Integer mNetState;
    private boolean isExhibition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        refreshNetState();
//        getNetExhibition();
    }


    @Override
    protected void onResume() {
        JPushInterface.onResume(this);
        MobclickAgent.onPageStart("欢迎页");
        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        JPushInterface.onPause(this);
        MobclickAgent.onPageEnd("欢迎页");
        MobclickAgent.onPause(this);
        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Ln.d("WelcomeActivity:onActivityResult:登录成功");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void refreshNetState() {
        UserModuleImpl.getInstance().getNetState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mNetState = integer;
//                        Toast.makeText(WelcomeActivity.this, "mNetState:" + mNetState, Toast.LENGTH_LONG).show();
                        Ln.d("WelcomeActivity:getNetState:integer:" + integer);
                        UserModule.getInstance().setNetStateLocal(integer);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Ln.d("WelcomeActivity:getNetState:error");

                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Ln.d("WelcomeActivity:getNetState:ok");
                        if(mNetState == UserModule.NET_STATE_UNKNOWN){
                            getNarmal();
                        }else if(mNetState == UserModule.NET_STATE_FIRST_LEVEL){
                            getNarmal();
                        }else if(mNetState == UserModule.NET_STATE_SECOND_LEVEL){
                            getNetExhibition();
                        }else if(mNetState == UserModule.NET_STATE_ALL){
                            getNetExhibition();
                        }else{
                            getNarmal();
                        }
                    }
                });
    }

    private void getNetExhibition(){
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
                        getNarmal();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if(isExhibition){
                            getExhibition();
                        }else {
                            getNarmal();
                        }
                    }
                });

    }
    private void getExhibition(){
        Intent serviceIntent = new Intent(WelcomeActivity.this, MyService.class);
        startService(serviceIntent);

        String showWelcome = getIntent().getStringExtra(Constant.NOT_SHOW_WELCOME);
        if(!TextUtils.isEmpty(showWelcome)){

            if (!UserModuleImpl.getInstance().isLogin()) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                intent.putExtra(LoginActivity.ACTIVITY_FROM,LoginActivity.ACTIVITY_FROM_WELCOME_ACTIVITY);
                startActivityForResult(intent, ACTIVITY_REQUEST_CODE_LOGIN_ACTIVITY);
            }
        }else{
            new CountDownTimer(1 * 1000, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    if (UserModuleImpl.getInstance().isLogin()) {
                        setResult(RESULT_OK);
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));

                        finish();
                    } else {
                        final String username = "t200100001";
//                        final String username = "t232100001";
                        String password = "123456";

                        final String md5Password = new MD5().md5(password);
                        UserModule.getInstance().login(username, md5Password, UserModule.USER_TYPE_EMAIL).subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<UserInfo>() {
                                    @Override
                                    public void call(UserInfo userInfo) {
                                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        String throwableString = String.valueOf(throwable);
                                        if (throwableString.contains("java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $")) {
                                            UserModule.getInstance().loginOld(username, md5Password, UserModule.USER_TYPE_EMAIL)
                                                    .subscribeOn(Schedulers.newThread())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new Action1<UserInfo>() {
                                                        @Override
                                                        public void call(UserInfo userInfo) {
                                                            setResult(RESULT_OK);
                                                            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                                            finish();
                                                        }
                                                    });

                                        }else{
                                            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                                            intent.putExtra(LoginActivity.ACTIVITY_FROM,LoginActivity.ACTIVITY_FROM_WELCOME_ACTIVITY);
                                            startActivityForResult(intent, ACTIVITY_REQUEST_CODE_LOGIN_ACTIVITY);
                                        }
                                    }
                                });
                    }

                }
            }.start();
        }
    }
    private void getNarmal(){
        Intent serviceIntent = new Intent(WelcomeActivity.this, MyService.class);
        startService(serviceIntent);

        String showWelcome = getIntent().getStringExtra(Constant.NOT_SHOW_WELCOME);
        if(!TextUtils.isEmpty(showWelcome)){

            if (!UserModuleImpl.getInstance().isLogin()) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                intent.putExtra(LoginActivity.ACTIVITY_FROM,LoginActivity.ACTIVITY_FROM_WELCOME_ACTIVITY);
                startActivityForResult(intent, ACTIVITY_REQUEST_CODE_LOGIN_ACTIVITY);
            }
        }else{
            new CountDownTimer(1 * 1000, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    if (UserModuleImpl.getInstance().isLogin()) {
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                        intent.putExtra(LoginActivity.ACTIVITY_FROM,LoginActivity.ACTIVITY_FROM_WELCOME_ACTIVITY);
                        startActivityForResult(intent, ACTIVITY_REQUEST_CODE_LOGIN_ACTIVITY);
                    }

                }
            }.start();
        }
    }
}
