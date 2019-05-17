package com.crphdm.dl2.user;

import android.content.Context;

import com.crphdm.dl2.user.obj.PgResult;
import com.crphdm.dl2.user.obj.UserInfo;

import rx.Observable;

/**
 * Created by gaoyufei on 15/10/9.
 */
public abstract class UserModule {
    public static final int NET_STATE_UNKNOWN = 0x00;
    public static final int NET_STATE_FIRST_LEVEL = 0x01;
    public static final int NET_STATE_SECOND_LEVEL = 0x02;
    public static final int NET_STATE_ALL = 0x03;

    public static final int USER_TYPE_PHONE = 0x1;
    public static final int USER_TYPE_EMAIL = 0x2;
    public static final int USER_TYPE_JOB_NUMBER = 0x3;

    public static final int NET_CENTER_FIRST = 0x01;
    public static final int NET_CENTER_SECOND = 0x02;

    public static final int ROLE_NORMAL = 6;
    public static final int ROLE_ORG = 8;
    public static final int ROLE_ORG_SELECTOR = 9;
    public static final int ROLE_ORG_BOSS = 10;

    private static UserModule module;

    public static UserModule getInstance() {
        if (module == null)
            throw new NullPointerException("UserModule is not init");
        return module;
    }

    public static UserModule init(Context context) {
        if (module == null)
            module = new UserModuleImpl(context);
        return module;
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
    public abstract Observable<Integer> getNetState();

    public abstract void setNetStateLocal(int state);

    public abstract int getNetStateLocal();

    public abstract boolean isLogin();

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @param userType 用户名类型
     * @return
     */
    public abstract Observable<UserInfo> login(String username, String password, int userType); /**
     * 登录(旧地址)
     *
     * @param username 用户名
     * @param password 密码
     * @param userType 用户名类型
     * @return
     */
    public abstract Observable<UserInfo> loginOld(String username, String password, int userType);

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @param userType 用户名类型
     * @return
     */
    public abstract Observable<UserInfo> loginSecond(String username, String password, int userType);


    /**
     * 注册
     *
     * @param username 用户名
     * @param password 密码
     * @param userType 用户类型
     * @param code     验证码
     * @return
     */
    public abstract Observable<Boolean> register(String username, String password, int userType, String code);

    /**
     * 获取验证码
     *
     * @param username 用户名
     * @param userType 类型
     * @return
     */
    public abstract Observable<PgResult> getVerifyCode(String username, int userType);


    /**
     * 找回密码
     *
     * @param username    用户名
     * @param resetType   重置方式
     * @param code        验证码
     * @param newPassword 新密码
     * @return
     */
    public abstract Observable<PgResult> resetPassword(String username, int resetType, String code, String newPassword);

    /**
     * 获取用户信息
     *
     * @return
     */
    public abstract UserInfo getUserInfoLocal(int netType);

    /**
     * 获取Token，阻塞
     *
     * @return
     */
    public abstract String getToken(int netType);

    /**
     * 获取Role
     *
     * @return int
     */
    public abstract int getRole();

    /**
     * 获取Token，异步
     *
     * @return
     */
    public abstract Observable<String> getTokenAsync(int netType);

    /**
     * 获取密码
     *
     * @return
     */
    public abstract String getPassword(int netType);

    /**
     * 获取登录类型
     *
     * @return
     */
    public abstract int getLoginType();
    /**
     * 获取用户类型
     *
     * @return
     */
    public abstract int getUserType();

    /**
     * 修改用户信息
     *
     * @param trueName
     * @param nickname
     * @param gender
     * @return
     */
    public abstract Observable<UserInfo> updateUserInfo(String iconUrl, String trueName, String nickname, int gender, int netType);

    public abstract void updateUserInfoByState(int type,String mobile,String orgName);

    public abstract String[] getUUIDs();

    public abstract void logout();

    public abstract void saveLoginState(boolean isLogin);

    //判断是否是展示版
    public abstract Observable<Boolean> getExhibition();
}
