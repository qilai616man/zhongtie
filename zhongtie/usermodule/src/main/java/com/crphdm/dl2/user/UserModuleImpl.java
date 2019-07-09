package com.crphdm.dl2.user;

import android.accounts.NetworkErrorException;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.crphdm.dl2.user.obj.PgResult;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.user.obj.net.NetExhibition;
import com.crphdm.dl2.user.obj.net.NetLocationResult;
import com.crphdm.dl2.user.obj.net.NetLoginResult;
import com.crphdm.dl2.user.obj.net.NetObject;
import com.crphdm.dl2.user.obj.net.NetUpdateUserInfoResult;
import com.crphdm.dl2.user.utils.Constant;
import com.crphdm.dl2.user.utils.MD5;
import com.crphdm.dl2.user.utils.NetHelper;
import com.crphdm.dl2.user.utils.Urls;
import com.google.gson.Gson;
import com.goyourfly.gdownloader.utils.Ln;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.paperdb.Paper;
import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observables.BlockingObservable;

/**
 * Modify by songzixuan on 19/07/02.
 */
public class UserModuleImpl extends UserModule {
    private Context mContext;
    private int mNetState;
    private long time;

    public UserModuleImpl(Context context) {
        mContext = context;
    }

    @Override
    public void setNetStateLocal(int netState) {
        mNetState = netState;

//        if (mNetState == UserModule.NET_STATE_ALL) {//全网络畅通
//            if (!isLogin(UserModule.NET_CENTER_FIRST)) {//未登陆一级
//                if (!isLogin(UserModule.NET_CENTER_SECOND)) {//一级二级均未登陆
//
//                } else {//未登陆一级
//
//                }
//            }
//        } else if (mNetState == UserModule.NET_STATE_FIRST_LEVEL) {//一级网络
//            if (!isLogin(UserModule.NET_CENTER_FIRST)) {//未登陆一级
//
//            }
//        } else if (mNetState == UserModule.NET_STATE_SECOND_LEVEL) {//二级网络
//            if (!isLogin(UserModule.NET_CENTER_SECOND)) {//未登陆二级
//
//            }
//        } else {//无网
//
//        }

    }

    @Override
    public int getNetStateLocal() {
        return mNetState;
    }

    /**
     * 获取网络状态，非阻塞执行
     *
     * @return 返回网络状态
     * 一级中心
     * NET_STATE_FIRST_LEVEL
     * 二级中心
     * NET_STATE_SECOND_LEVEL
     */
    @Override
    public Observable<Integer> getNetState() {
        //没有网络
        if (getNetWorkType(mContext) == Constant.NETWORK_TYPE_INVALID) {

            Ln.d("UserModule:getNetState:NET_STATE_UNKNOWN");

            return Observable.just(UserModule.NET_STATE_UNKNOWN);
        } else {
            //获取时间毫秒
            final long t1 = System.currentTimeMillis();
            return Observable.defer(new Func0<Observable<Integer>>() {
                @Override
                public Observable<Integer> call() {
                    Ln.d("UserModule:getNetState:FirstCenter");

                    long t2 = System.currentTimeMillis() - t1;
                    Ln.d("UserModule:getNetState:time2:" + t2);

                    //1.先判断是否在一级网络
                    try {
                        String result = NetHelper.getContent5Second(Urls.URL_FIRST_CENTER);
//                      String result = NetHelper.getContent5Second("https://www.google.com.hk/?gws_rd=ssl#q=hello");
                        Ln.d("UserModule:getNetState:FirstCenter:result:" + result);
                        NetLocationResult netObject = new Gson().fromJson(result, NetLocationResult.class);
                        if (netObject != null && NetLocationResult.LOCATION_MASTER.equals(netObject.getLocation()))
                            return Observable.just(UserModule.NET_STATE_FIRST_LEVEL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return Observable.just(UserModule.NET_STATE_UNKNOWN);
                }
            }).flatMap(new Func1<Integer, Observable<Integer>>() {
                @Override
                public Observable<Integer> call(Integer integer) {
                    Ln.d("UserModule:getNetState:SecondCenter");

                    long t3 = System.currentTimeMillis() - t1;
                    Ln.d("UserModule:getNetState:time3:" + t3);

                    //2.判断是否在二级网络
                    try {
                        String result = NetHelper.getContent5Second(Urls.URL_SECOND_CENTER);
                        Ln.d("UserModule:getNetState:SecondCenter:result:" + result);
                        NetLocationResult netObject = new Gson().fromJson(result, NetLocationResult.class);
                        //3.根据两次判断进行最终结果的判断
                        if (netObject != null && NetLocationResult.LOCATION_SLAVE.equals(netObject.getLocation())) {
                            //4.即在一级又在二级
                            if (integer == UserModule.NET_STATE_FIRST_LEVEL) {
                                return Observable.just(UserModule.NET_STATE_ALL);
                            } else {
                                //5.只在二级网络
                                return Observable.just(UserModule.NET_STATE_SECOND_LEVEL);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //6.未知网络
                    return Observable.just(integer);
                }
            });
        }

    }

    @Override
    public boolean isLogin() {
        return Paper.book().read("LOGIN_STATE", false);
//        return getUserInfoLocal(netType) != null;
    }

    //旧Ip登录
    public Observable<UserInfo> loginOld(final String username, final String password, final int userType){
        Ln.d("UserModule:loginOld");

        if (!userNameIsSame(username)) {
            Ln.d("UserModule:loginOld:不同用户登录，清除数据");
            logout();

            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_CLEAN_DATA);
            mContext.sendBroadcast(intent);
        }

        return getNetState().flatMap(new Func1<Integer, Observable<NetLoginResult>>() {
            @Override
            public Observable<NetLoginResult> call(Integer integer) {
                Ln.d("UserModule:loginOld:integer:" + integer);
                //一级网络或者一级二级网络都有
                if (integer == NET_STATE_FIRST_LEVEL) {
                    Log.i("netnet:","old"+NET_STATE_FIRST_LEVEL);
                    return loginFirst(username, password, userType);
                } else if (integer == NET_STATE_SECOND_LEVEL) {
                    Log.i("netnet:","old"+NET_STATE_SECOND_LEVEL);
                    return loginSecondOld(username, password, userType, getCipherText());
                } else if (integer == NET_STATE_ALL) {
                    //同时存在
                    Log.i("netnet:","old"+NET_STATE_ALL);
                    return loginAllOld(username, password, userType);
                } else {
                    //无连接或者连接超时
                    Exceptions.propagate(new NetworkErrorException("未知网络状态"));
                    return null;
                }
            }
        }).flatMap(new Func1<NetLoginResult, Observable<UserInfo>>() {
            @Override
            public Observable<UserInfo> call(NetLoginResult netLoginResult) {
                Ln.d("UserModule:loginOld:netLoginResult:" + netLoginResult);
                return Observable.just(netLoginResult.getUser_info());
            }
        });


    }
    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @param userType 用户名类型
     * @return
     */
    @Override
    public Observable<UserInfo> login(final String username, final String password, final int userType) {
        Ln.d("UserModule:login");

        if (!userNameIsSame(username)) {
            Ln.d("UserModule:login:不同用户登录，清除数据");
            logout();

            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_CLEAN_DATA);
            mContext.sendBroadcast(intent);
        }

        return getNetState().flatMap(new Func1<Integer, Observable<NetLoginResult>>() {
            @Override
            public Observable<NetLoginResult> call(Integer integer) {
                Ln.d("UserModule:login:integer:" + integer);
                //一级网络或者一级二级网络都有
                if (integer == NET_STATE_FIRST_LEVEL) {
                    Log.i("netnet:",NET_STATE_FIRST_LEVEL+"");
                    return loginFirst(username, password, userType);
                } else if (integer == NET_STATE_SECOND_LEVEL) {
                    //二级网络
//                    if (getCipherText() == null)
//                        Exceptions.propagate(new NetworkErrorException("当前在二级网络，但您从来都没有登录过一级网络，所以无法登录"));
                    Log.i("netnet:",NET_STATE_SECOND_LEVEL+"");
                    return loginSecond(username, password, userType, getCipherText());
                } else if (integer == NET_STATE_ALL) {
                    //同时存在
                    Log.i("netnet:",NET_STATE_ALL+"");
                    return loginAll(username, password, userType);
                } else {
                    //无连接或者连接超时
                    Exceptions.propagate(new NetworkErrorException("未知网络状态"));
                    return null;
                }
            }
        }).flatMap(new Func1<NetLoginResult, Observable<UserInfo>>() {
            @Override
            public Observable<UserInfo> call(NetLoginResult netLoginResult) {
                Ln.d("UserModule:login:netLoginResult:" + netLoginResult);
                return Observable.just(netLoginResult.getUser_info());
            }
        });
    }
            //用户是否相同
    private boolean userNameIsSame(String username) {
        UserInfo userInfoFirst = getUserInfoLocal(UserModule.NET_CENTER_FIRST);
        UserInfo userInfoSecond = getUserInfoLocal(UserModule.NET_CENTER_SECOND);
        Ln.d("UserModule:userNameIsSame:username:" + username);

        if (userInfoFirst != null) {
            Ln.d("UserModule:userNameIsSame:UserInfoFirst:" + userInfoFirst.getUsername());
            //登录的账号跟以前不同，清除本地数据
            if (username.equals(userInfoFirst.getUsername())) {
                return true;
            }
        }

        if (userInfoSecond != null) {
            Ln.d("UserModule:userNameIsSame:UserInfoSecond:" + userInfoSecond.getUsername());
            //登录的账号跟以前不同，清除本地数据
            if (username.equals(userInfoSecond.getUsername())) {
                return true;
            }
        }

        return false;
    }
            //二次登陆
    @Override
    public Observable<UserInfo> loginSecond(String username, String password, int userType) {
        return loginSecond(username, password, userType, null)
                .flatMap(new Func1<NetLoginResult, Observable<UserInfo>>() {
                    @Override
                    public Observable<UserInfo> call(NetLoginResult netLoginResult) {
                        Ln.d("UserModule:loginSecond:result:" + netLoginResult.toString());
                        saveRole(netLoginResult.getRole());
                        return Observable.just(netLoginResult.getUser_info());
                    }
                });
    }

    /**
     * 注册
     *
     * @param username 用户名
     * @param password 密码  已MD5加密
     * @param userType 用户类型
     * @param code     验证码
     * @return
     */
    @Override
    public Observable<Boolean> register(String username, final String password, int userType, String code) {
        final StringBuffer stringBuffer = new StringBuffer(Urls.URL_REGISTER);
        stringBuffer.append("&user_name").append("=").append(username);
        stringBuffer.append("&user_password").append("=").append(password);
        stringBuffer.append("&user_type").append("=").append(userType);
        stringBuffer.append("&code").append("=").append(code);
        Ln.d("UserModule:register:url:" + stringBuffer.toString());
        return Observable.just(stringBuffer.toString())
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return NetHelper.getData(s);
                    }
                })
                .flatMap(new Func1<String, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(String s) {
                        Ln.d("UserModuleImpl:register:result:" + s);
                        saveMD5Password(NET_CENTER_FIRST, new MD5().md5(password));
                        NetObject netObject = new Gson().fromJson(s, NetObject.class);
                        if (netObject.isStatus()) {
                            return Observable.just(Boolean.TRUE);
                        } else {
                            Exceptions.propagate(new NetworkErrorException(netObject.getMessage()));
                        }
                        return null;
                    }
                });
    }

    /**
     * 获取验证码
     *
     * @param username 用户名
     * @param userType 类型  1.手机号  2.邮箱
     * @return
     */
    @Override
    public Observable<PgResult> getVerifyCode(String username, int userType) {
        StringBuffer stringBuffer = new StringBuffer(Urls.URL_GET_VERIFY_CODE);
        stringBuffer.append("&account").append("=").append(username);
        stringBuffer.append("&type").append("=").append(userType);
        Ln.d("UserModule:getVerifyCode:url:" + stringBuffer.toString());
        return Observable.just(stringBuffer.toString())
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return NetHelper.getData(s);
                    }
                }).flatMap(new Func1<String, Observable<PgResult>>() {
                    @Override
                    public Observable<PgResult> call(String s) {
                        Ln.d("UserModule:getVerifyCode:result:" + s);
                        NetObject netObject = new Gson().fromJson(s, NetObject.class);

                        PgResult pg = PgResult.getPgByNet(netObject);

                        return Observable.just(pg);
                    }
                });
    }
         /**
         *重设密码
         */
    @Override
    public Observable<PgResult> resetPassword(String username, int resetType, String code, String pwd) {
        final String newPassword = new MD5().md5(pwd);
        StringBuffer stringBuffer = new StringBuffer(Urls.URL_LOOKING_FOR_PASSWORD);
        stringBuffer.append("&user_name").append("=").append(username);
        stringBuffer.append("&reset_type").append("=").append(resetType);
        stringBuffer.append("&code").append("=").append(code);
        stringBuffer.append("&new_password").append("=").append(newPassword);
        Ln.d("UserModule:resetPassword:url:" + stringBuffer.toString());
        return Observable.just(stringBuffer.toString())
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return NetHelper.getData(s);
                    }
                })
                .flatMap(new Func1<String, Observable<PgResult>>() {
                    @Override
                    public Observable<PgResult> call(String s) {
                        NetObject netObject = new Gson().fromJson(s, NetObject.class);
                        PgResult pg = PgResult.getPgByNet(netObject);
                        return Observable.just(pg);
                    }
                });
    }

    @Override
    public UserInfo getUserInfoLocal(int netType) {
        return Paper.book().read("USER-INFO" + netType);
    }

    private Observable<NetLoginResult> loginFirst(String username, final String password, final int userType) {

        StringBuffer stringBuffer = new StringBuffer(Urls.URL_LOGIN_FIRST_CENTER);
        stringBuffer.append("&user_name").append("=").append(username);
        stringBuffer.append("&user_password").append("=").append(password);
        stringBuffer.append("&machine_id").append("=").append(getDeviceId(mContext));
        Ln.d("UserModule:loginFirst:url:" + stringBuffer.toString());
//        Toast.makeText(mContext,userType+"-URL_LOGIN_FIRST_CENTER",Toast.LENGTH_SHORT).show();
        return loginUrl(stringBuffer.toString()).doOnNext(new Action1<NetLoginResult>() {
            @Override
            public void call(NetLoginResult netLoginResult) {
                Ln.d("UserModule:loginFirst:net:" + netLoginResult.toString());
                if (netLoginResult.isStatus()) {
                    Ln.d("UserModule:loginFirst:登录成功");

                  //  Paper.book().read()
                    saveToken(NET_CENTER_FIRST, netLoginResult.getLogin_token());
                    saveCipherText(netLoginResult.getCiphertext());
                    saveTokenTimeOut(NET_CENTER_FIRST, netLoginResult.getToken_expire_time());
                    saveUserInfo(NET_CENTER_FIRST, netLoginResult.getUser_info());
                    saveMD5Password(NET_CENTER_FIRST, password);
                    saveRole(netLoginResult.getRole());
                    saveLoginType(NET_STATE_FIRST_LEVEL);
                    saveUserType(userType);
                    saveLoginState(true);
                } else {
                    Ln.d("UserModule:loginFirst:登录失败");
                    Exceptions.propagate(new NetworkErrorException(netLoginResult.getMessage()));
                }
            }

        });
    }

    /**
     *二次登陆
     */
    private Observable<NetLoginResult> loginSecond(String username, final String password, final int userType, String ciphertext) {
        StringBuffer stringBuffer = new StringBuffer(Urls.URL_LOGIN_SECOND_CENTER);
//        StringBuffer stringBuffer = new StringBuffer(Urls.URL_LOGIN_SECOND_CENTER_OLD);
        stringBuffer.append("&user_name").append("=").append(username);
        stringBuffer.append("&user_password").append("=").append(password);
        stringBuffer.append("&machine_id").append("=").append(getDeviceId(mContext));
        if (ciphertext != null)
            stringBuffer.append("&ciphertext").append("=").append(ciphertext);
        Ln.d("UserModule:loginSecond:url:" + stringBuffer.toString());
//        Toast.makeText(mContext,userType+"--URL_LOGIN_SECOND_CENTER",Toast.LENGTH_SHORT).show();
        return loginUrl(stringBuffer.toString()).doOnNext(new Action1<NetLoginResult>() {
            @Override
            public void call(NetLoginResult netLoginResult) {
                Ln.d("UserModule:loginSecond:net:" + netLoginResult.toString());
                if (netLoginResult.isStatus()) {
                    Ln.d("UserModule:loginSecond:登录成功");
                    if (netLoginResult.getLogin_token() == null)
                        throw new NullPointerException("Token为空");
                    saveToken(NET_CENTER_SECOND, netLoginResult.getLogin_token());
                    saveTokenTimeOut(NET_CENTER_SECOND, netLoginResult.getToken_expire_time());
                    saveUserInfo(NET_CENTER_SECOND, netLoginResult.getUser_info());
                    saveMD5Password(NET_CENTER_SECOND, password);
                    saveLoginType(NET_STATE_SECOND_LEVEL);
                    saveRole(netLoginResult.getRole());//只有二级网络时
                    saveUserType(userType);
                    saveLoginState(true);
                } else {
                    Ln.d("UserModule:loginSecond:登录失败");
                    Exceptions.propagate(new NetworkErrorException(netLoginResult.getMessage()));
                }
            }
        });
    }
    /**
     *全部登陆
     */
    private Observable<NetLoginResult> loginAll(final String username, final String password, final int userType) {
        StringBuffer stringBuffer = new StringBuffer(Urls.URL_LOGIN_FIRST_CENTER);
        stringBuffer.append("&user_name").append("=").append(username);
        stringBuffer.append("&user_password").append("=").append(password);
        stringBuffer.append("&machine_id").append("=").append(getDeviceId(mContext));
        Ln.d("UserModule:loginFirst:url:" + stringBuffer.toString());
//        Toast.makeText(mContext,userType+"---URL_LOGIN_FIRST_CENTER",Toast.LENGTH_SHORT).show();
        return loginUrl(stringBuffer.toString())
                .flatMap(new Func1<NetLoginResult, Observable<NetLoginResult>>() {
                    @Override
                    public Observable<NetLoginResult> call(final NetLoginResult netLoginResultFirst) {
                        Ln.d("UserModule:loginFirst:netLoginResultFirst:" + netLoginResultFirst.toString());
                        if (netLoginResultFirst.isStatus()) {
                            Ln.d("UserModule:loginFirst:登录成功");
                            saveToken(NET_CENTER_FIRST, netLoginResultFirst.getLogin_token());
                            saveCipherText(netLoginResultFirst.getCiphertext());
                            saveTokenTimeOut(NET_CENTER_FIRST, netLoginResultFirst.getToken_expire_time());
                            saveUserInfo(NET_CENTER_FIRST, netLoginResultFirst.getUser_info());
                            saveMD5Password(NET_CENTER_FIRST, password);
                            saveLoginType(NET_STATE_FIRST_LEVEL);
                            saveRole(netLoginResultFirst.getRole());
                            saveUserType(userType);
                            saveLoginState(true);
                        }

                        StringBuffer stringBuffer = new StringBuffer(Urls.URL_LOGIN_SECOND_CENTER);
                        stringBuffer.append("&user_name").append("=").append(username);
                        stringBuffer.append("&user_password").append("=").append(password);
                        stringBuffer.append("&machine_id").append("=").append(getDeviceId(mContext));
                        if (netLoginResultFirst.isStatus()) {
                            stringBuffer.append("&ciphertext").append("=").append(netLoginResultFirst.getCiphertext());
                        }

                        Ln.d("UserModule:loginAllSecond:url:" + stringBuffer.toString());
                        return loginUrl(stringBuffer.toString())
                                .flatMap(new Func1<NetLoginResult, Observable<NetLoginResult>>() {
                                    @Override
                                    public Observable<NetLoginResult> call(NetLoginResult netLoginResult) {
                                        Ln.d("UserModule:loginAllSecond:netLoginResult:" + netLoginResult.toString());
                                        if (netLoginResult.isStatus()) {
                                            Ln.d("UserModule:loginAllSecond:登录成功");
                                            saveToken(NET_CENTER_SECOND, netLoginResult.getLogin_token());
                                            saveTokenTimeOut(NET_CENTER_SECOND, netLoginResult.getToken_expire_time());
                                            saveUserInfo(NET_CENTER_SECOND, netLoginResult.getUser_info());
                                            saveMD5Password(NET_CENTER_SECOND, password);
                                            saveUserType(userType);
                                            saveLoginState(true);
                                            if (netLoginResultFirst.isStatus()) {
                                                saveLoginType(NET_STATE_ALL);
                                            } else {
                                                saveLoginType(NET_STATE_SECOND_LEVEL);
                                            }

                                            return Observable.just(netLoginResult);
                                        } else {
                                            Ln.d("UserModule:loginAllSecond:登录失败");
                                            if (netLoginResultFirst.isStatus()) {
                                                return Observable.just(netLoginResultFirst);
                                            } else {
                                                Exceptions.propagate(new NetworkErrorException(netLoginResultFirst.getMessage()));
                                            }
                                        }
                                        return Observable.empty();
                                    }
                                });
                    }
                });
    }

    private Observable<NetLoginResult> loginSecondOld(String username, final String password, final int userType, String ciphertext) {
        StringBuffer stringBuffer = new StringBuffer(Urls.URL_LOGIN_SECOND_CENTER_OLD);
        stringBuffer.append("&user_name").append("=").append(username);
        stringBuffer.append("&user_password").append("=").append(password);
        stringBuffer.append("&machine_id").append("=").append(getDeviceId(mContext));
        if (ciphertext != null)
            stringBuffer.append("&ciphertext").append("=").append(ciphertext);
        Ln.d("UserModule:loginSecondOld:url:" + stringBuffer.toString());
        return loginUrl(stringBuffer.toString()).doOnNext(new Action1<NetLoginResult>() {
            @Override
            public void call(NetLoginResult netLoginResult) {
                Ln.d("UserModule:loginSecondOld:net:" + netLoginResult.toString());
                if (netLoginResult.isStatus()) {
                    Ln.d("UserModule:loginSecondOld:登录成功");
                    if (netLoginResult.getLogin_token() == null)
                        throw new NullPointerException("Token为空");
                    saveToken(NET_CENTER_SECOND, netLoginResult.getLogin_token());
                    saveTokenTimeOut(NET_CENTER_SECOND, netLoginResult.getToken_expire_time());
                    saveUserInfo(NET_CENTER_SECOND, netLoginResult.getUser_info());
                    saveMD5Password(NET_CENTER_SECOND, password);
                    saveLoginType(NET_STATE_SECOND_LEVEL);
                    saveRole(netLoginResult.getRole());//只有二级网络时
                    saveUserType(userType);
                    saveLoginState(true);
                } else {
                    Ln.d("UserModule:loginSecondOld:登录失败");
                    Exceptions.propagate(new NetworkErrorException(netLoginResult.getMessage()));
                }
            }
        });
    }

    private Observable<NetLoginResult> loginAllOld(final String username, final String password, final int userType) {
        StringBuffer stringBuffer = new StringBuffer(Urls.URL_LOGIN_FIRST_CENTER);
        stringBuffer.append("&user_name").append("=").append(username);
        stringBuffer.append("&user_password").append("=").append(password);
        stringBuffer.append("&machine_id").append("=").append(getDeviceId(mContext));
        Ln.d("UserModule:loginAllOld:url:" + stringBuffer.toString());

        return loginUrl(stringBuffer.toString())
                .flatMap(new Func1<NetLoginResult, Observable<NetLoginResult>>() {
                    @Override
                    public Observable<NetLoginResult> call(final NetLoginResult netLoginResultFirst) {
                        Ln.d("UserModule:loginAllOld:netLoginResultFirst:" + netLoginResultFirst.toString());
                        if (netLoginResultFirst.isStatus()) {
                            Ln.d("UserModule:loginAllOld:登录成功");
                            saveToken(NET_CENTER_FIRST, netLoginResultFirst.getLogin_token());
                            saveCipherText(netLoginResultFirst.getCiphertext());
                            saveTokenTimeOut(NET_CENTER_FIRST, netLoginResultFirst.getToken_expire_time());
                            saveUserInfo(NET_CENTER_FIRST, netLoginResultFirst.getUser_info());
                            saveMD5Password(NET_CENTER_FIRST, password);
                            saveLoginType(NET_STATE_FIRST_LEVEL);
                            saveRole(netLoginResultFirst.getRole());
                            saveUserType(userType);
                            saveLoginState(true);
                        }

                        StringBuffer stringBuffer = new StringBuffer(Urls.URL_LOGIN_SECOND_CENTER_OLD);
                        stringBuffer.append("&user_name").append("=").append(username);
                        stringBuffer.append("&user_password").append("=").append(password);
                        stringBuffer.append("&machine_id").append("=").append(getDeviceId(mContext));
                        if (netLoginResultFirst.isStatus()) {
                            stringBuffer.append("&ciphertext").append("=").append(netLoginResultFirst.getCiphertext());
                        }

                        Ln.d("UserModule:loginAllSecondOld:url:" + stringBuffer.toString());
                        return loginUrl(stringBuffer.toString())
                                .flatMap(new Func1<NetLoginResult, Observable<NetLoginResult>>() {
                                    @Override
                                    public Observable<NetLoginResult> call(NetLoginResult netLoginResult) {
                                        Ln.d("UserModule:loginAllSecondOld:netLoginResult:" + netLoginResult.toString());
                                        if (netLoginResult.isStatus()) {
                                            Ln.d("UserModule:loginAllSecondOld:登录成功");
                                            saveToken(NET_CENTER_SECOND, netLoginResult.getLogin_token());
                                            saveTokenTimeOut(NET_CENTER_SECOND, netLoginResult.getToken_expire_time());
                                            saveUserInfo(NET_CENTER_SECOND, netLoginResult.getUser_info());
                                            saveMD5Password(NET_CENTER_SECOND, password);
                                            saveUserType(userType);
                                            saveLoginState(true);
                                            if (netLoginResultFirst.isStatus()) {
                                                saveLoginType(NET_STATE_ALL);
                                            } else {
                                                saveLoginType(NET_STATE_SECOND_LEVEL);
                                            }

                                            return Observable.just(netLoginResult);
                                        } else {
                                            Ln.d("UserModule:loginAllSecondOld:登录失败");
                                            if (netLoginResultFirst.isStatus()) {
                                                return Observable.just(netLoginResultFirst);
                                            } else {
                                                Exceptions.propagate(new NetworkErrorException(netLoginResultFirst.getMessage()));
                                            }
                                        }
                                        return Observable.empty();
                                    }
                                });
                    }
                });
    }
    /**
     *登陆Url
     */
    private Observable<NetLoginResult> loginUrl(String url) {
        return Observable.just(url)
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return NetHelper.getData(s);
                    }
                }).flatMap(new Func1<String, Observable<NetLoginResult>>() {
                    @Override
                    public Observable<NetLoginResult> call(String s) {
                        return Observable.just(new Gson().fromJson(s, NetLoginResult.class));
                    }
                });
    }

    private String getCipherText() {
        return Paper.book().read("CIPHER-TEXT");
    }

    @Override
    public String getToken(int userType) {
        Ln.d("GetToken:" + userType);
        if (!isTokenTimeout(userType)) {
            Ln.d("GetToken:Token is time out");
            return getTokenLocal(userType);
        }

        UserInfo userInfo = getUserInfoLocal(userType);
        if (userInfo == null)
            return null;
        if (getMD5Password(userType) == null)
            return null;

        try {
            switch (userType) {
                case NET_CENTER_FIRST:
                    return BlockingObservable.from(loginFirst(userInfo.getUsername(), getMD5Password(userType), userType)).single().getLogin_token();
                case NET_CENTER_SECOND:
                    return BlockingObservable.from(loginSecond(userInfo.getUsername(), getMD5Password(userType), userType, getCipherText())).single().getLogin_token();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    private String getMD5Password(int netType) {
        return Paper.book().read("PASSWORD" + netType);
    }

    private String getTokenLocal(int type) {
        return Paper.book().read("TOKEN" + type);
    }

    private boolean isTokenTimeout(int netType) {
        if (!Paper.book().exist("TOKEN_TIMEOUT" + netType))
            return true;
        long timeout = Paper.book().read("TOKEN_TIMEOUT" + netType);

        return timeout - System.currentTimeMillis() < 60 * 1000;
    }

    public int getRole() {
        if (!Paper.book().exist("ROLE"))
            return -1;
        return Paper.book().read("ROLE");
    }

    private void saveTokenTimeOut(int netType, int tokenExpireTime) {
        Paper.book().write("TOKEN_TIMEOUT" + netType, System.currentTimeMillis() + tokenExpireTime * 1000);
    }

    private void saveToken(int netType, String token) {
        Paper.book().write("TOKEN" + netType, token);
    }

    private void saveUserType(int type) {
        Paper.book().write("USER_TYPE", type);
    }

    /**
     * 用于token失效踢人
     */
    public void saveLoginState(boolean isLogin) {
        Paper.book().write("LOGIN_STATE", isLogin);
    }

    @Override
    public Observable<Boolean> getExhibition() {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                String url = "http://192.168.4.253/?app=book&controller=forTwo&action=experience";
//                String url = "http://172.29.224.1/?app=book&controller=forTwo&action=experience";

                return NetHelper.getData(url);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                NetExhibition net = new Gson().fromJson(s, NetExhibition.class);

                return Observable.just(net.isStatus());
            }
        });
    }

    private void saveLoginType(int type) {
        Paper.book().write("LOGIN_TYPE", type);
    }

    private void saveRole(int role) {
        Paper.book().write("ROLE", role);
    }

    private void saveUserInfo(int netType, UserInfo userInfo) {
        Paper.book().write("USER-INFO" + netType, userInfo);
    }


    private void saveMD5Password(int netType, String password) {
        Paper.book().write("PASSWORD" + netType, password);
    }

    private void saveCipherText(String cipherText) {
        if (cipherText == null || cipherText.isEmpty())
            return;
        Paper.book().write("CIPHER-TEXT", cipherText);
    }

    /**
     * 异步获取token
     *
     * @param type
     * @return
     */
    @Override
    public Observable<String> getTokenAsync(final int type) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return Observable.just(getToken(type));
            }
        });
    }

    @Override
    public String getPassword(int netType) {
        return Paper.book().read("PASSWORD" + netType);
    }

    @Override
    public int getLoginType() {
        if (Paper.book() != null) {
            return Paper.book().read("LOGIN_TYPE");
        }
        return 0;
    }

    @Override
    public int getUserType() {
        return Paper.book().read("USER_TYPE");
    }

    /**
     * 更新个人信息
     *
     * @param iconUrl
     * @param trueName
     * @param nickName
     * @param gender
     * @param netType
     * @return
     */
    @Override
    public Observable<UserInfo> updateUserInfo(final String iconUrl, final String trueName, final String nickName, final int gender, final int netType) {
        return getTokenAsync(netType).flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                StringBuffer stringBuffer = new StringBuffer(Urls.URL_UPDATE_USER_INFO);
                stringBuffer.append("&user_id").append("=").append(getUserInfoLocal(netType).getUserid());
                stringBuffer.append("&login_token").append("=").append(s);
                stringBuffer.append("&iconUrl").append("=").append(iconUrl);
                stringBuffer.append("&truename").append("=").append(trueName);
                stringBuffer.append("&nickname").append("=").append(nickName);
                stringBuffer.append("&gender").append("=").append(gender);

                Ln.d("UserModule:updateUserInfo:url:" + stringBuffer.toString());
                return NetHelper.getData(stringBuffer.toString());
            }
        }).flatMap(new Func1<String, Observable<UserInfo>>() {
            @Override
            public Observable<UserInfo> call(String s) {
                Ln.d("UserModule:updateUserInfo:result:" + s);
                NetUpdateUserInfoResult net = new Gson().fromJson(s, NetUpdateUserInfoResult.class);

                Ln.d("UserModule:updateUserInfo:net:" + net.toString());
                UserInfo userInfo = getUserInfoLocal(UserModule.NET_CENTER_FIRST);

                if (!net.isStatus())
                    Exceptions.propagate(new NetworkErrorException(net.getMessage()));

                if (net.getUser_info() != null) {
                    userInfo.setUserid(net.getUser_info().getUserid());
                    userInfo.setUsername(net.getUser_info().getUsername());
                    userInfo.setNickname(net.getUser_info().getNickname());
                    userInfo.setTruename(net.getUser_info().getTruename());
                    userInfo.setEmail(net.getUser_info().getEmail());
                    userInfo.setSex(net.getUser_info().getGender());
                    userInfo.setPhoto(net.getUser_info().getPhoto());
                }

                Ln.d("UserModule:updateUserInfo:userInfo:" + userInfo.toString());

                saveUserInfo(NET_CENTER_FIRST, userInfo);

                return Observable.just(userInfo);
            }
        });
    }

    /**
     * 更新绑定状态
     *
     * @param type    1.绑定手机   2.绑定机构
     * @param mobile
     * @param orgName 1.未绑定  2. 审核中  3.申请成功  4.申请失败
     */
    @Override
    public void updateUserInfoByState(int type, String mobile, String orgName) {
        UserInfo userInfo = getUserInfoLocal(UserModule.NET_CENTER_FIRST);

        if (type == 1) {//绑定手机
            userInfo.setMobile(mobile);
        } else if (type == 2) {//绑定机构
            userInfo.setBind_org_state(2);
            userInfo.setOrg_name(orgName);
        }

        Ln.d("UserModule:updateUserInfoByState:userInfo:" + userInfo.toString());
        saveUserInfo(NET_CENTER_FIRST, userInfo);
    }


    @Override
    public String[] getUUIDs() {
        String uuidFirst = null;
        String uuidSecond = null;
        UserInfo userInfoFirst = getUserInfoLocal(UserModule.NET_CENTER_FIRST);
        UserInfo userInfoSecond = getUserInfoLocal(UserModule.NET_CENTER_SECOND);

        if (userInfoSecond != null) {
            Ln.d("UserModule:getUUIDs:userInfoSecond:" + userInfoSecond.toString());
            uuidSecond = userInfoSecond.getSecondKey();
            Ln.d("UserModule:getUUIDs:mUUIDSecond:" + uuidSecond);
        } else {
            Ln.d("UserModule:getUUIDs:userInfoSecond == null");
        }

        if (userInfoFirst != null) {
            Ln.d("UserModule:getUUIDs:userInfoFirst:" + userInfoFirst.toString());
            uuidFirst = userInfoFirst.getFirstKey();

            Ln.d("UserModule:getUUIDs:mUUIDFirst:" + uuidFirst);
        } else {
            Ln.d("UserModule:getUUIDs:userInfoFirst == null");
        }

        if (uuidFirst == null || uuidFirst.isEmpty()) {
            if (uuidSecond == null || uuidSecond.isEmpty()) {
                return new String[]{};
            } else {
                return new String[]{uuidSecond};
            }
        }else {
            if (uuidSecond == null || uuidSecond.isEmpty()) {
                return new String[]{uuidFirst};
            } else {
                return new String[]{uuidFirst,uuidSecond};
            }
        }

//        return new String[]{ uuidSecond};
    }

    @Override
    public void logout() {
        Paper.book().destroy();
    }

    /**
     * 获取网络状态，wifi,wap,2g,3g.
     *
     * @param context 上下文
     * @return int 网络状态
     */
//    {@link #Constant.NETWORKTYPE_2G},
//    {@link #NETWORKTYPE_3G},
//    {@link #NETWORKTYPE_INVALID},
//    {@link #NETWORKTYPE_WAP}* <p>
//    {@link #Constant.NETWORKTYPE_WIFI}
    public static int getNetWorkType(Context context) {
        int mNetWorkType = 0;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = Constant.NETWORK_TYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                mNetWorkType = TextUtils.isEmpty(proxyHost)
                        ? (isFastMobileNetwork(context) ? Constant.NETWORK_TYPE_3G : Constant.NETWORK_TYPE_2G)
                        : Constant.NETWORK_TYPE_WAP;
            }
        } else {
            mNetWorkType = Constant.NETWORK_TYPE_INVALID;
        }
        return mNetWorkType;
    }

    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    /**
     * 获取android设备唯一标识
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();
        Ln.d("PublicModule:szImei = " + szImei);
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits
        Ln.d("PublicModule:m_szDevIDShort = " + m_szDevIDShort);
        //String m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        //Log.d(tag,"m_szAndroidID = " + m_szAndroidID);
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        Ln.d("PublicModule:m_szWLANMAC = " + m_szWLANMAC);
        BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String m_szBTMAC = m_BluetoothAdapter.getAddress();
        Ln.d("PublicModule:m_szBTMAC = " + m_szBTMAC);
        String m_szLongID = szImei + "_" + m_szDevIDShort
                + "_" + m_szWLANMAC + "_" + m_szBTMAC;
        // compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF)
                m_szUniqueID += "0";
            // add number to string
            m_szUniqueID += Integer.toHexString(b);
        }   // hex string to uppercase
        m_szUniqueID = m_szUniqueID.toUpperCase();
        return m_szUniqueID;
    }
}
