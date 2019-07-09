package com.crphdm.dl2.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.google.gson.Gson;
import com.goyourfly.gdownloader.utils.Ln;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Modify by songzixuan on 19/07/04.
 * 网络广播接收器
 */
public class NetworkBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!UserModule.getInstance().isLogin())
            return;
        //网络状态变化
//        Toast.makeText(context, "网络状态变化", Toast.LENGTH_SHORT).show();
        final int loginType = UserModule.getInstance().getLoginType();
        final int netType = (loginType == UserModule.NET_STATE_ALL ? UserModule.NET_STATE_FIRST_LEVEL : loginType);
        final UserInfo userInfo = UserModule.getInstance().getUserInfoLocal(netType);
        UserModule.getInstance().getNetState().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Ln.d("BeforeLoginType:" + loginType + ",BeforeNetType:" + netType + ",currentNetType:" + integer);
                        if (integer != loginType && loginType != UserModule.getInstance().NET_STATE_ALL) {
                            UserModule.getInstance().login(userInfo.getUsername(), UserModule.getInstance().getPassword(netType), UserModule.getInstance().getUserType())
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<UserInfo>() {
                                        @Override
                                        public void call(UserInfo userInfo) {
                                            Ln.d("LoginResult:" + new Gson().toJson(userInfo));
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            throwable.printStackTrace();
                                        }
                                    });
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }
}
