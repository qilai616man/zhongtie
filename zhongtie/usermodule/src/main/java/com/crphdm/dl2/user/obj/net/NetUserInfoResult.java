package com.crphdm.dl2.user.obj.net;

import com.crphdm.dl2.user.obj.UserInfo;
import com.google.gson.annotations.Expose;

/**
 * Created by gaoyufei on 15/10/9.
 */
public class NetUserInfoResult extends NetObject{
    @Expose
    UserInfo user_info;

    public UserInfo getUser_info() {
        return user_info;
    }

    public void setUser_info(UserInfo user_info) {
        this.user_info = user_info;
    }
}
