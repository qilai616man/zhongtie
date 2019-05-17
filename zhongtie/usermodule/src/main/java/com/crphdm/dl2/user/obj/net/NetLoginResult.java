package com.crphdm.dl2.user.obj.net;

import com.crphdm.dl2.user.obj.UserInfo;
import com.google.gson.annotations.Expose;

/**
 * Created by gaoyufei on 15/10/9.
 */
public class NetLoginResult extends NetObject {
    @Expose
    String ciphertext;
    @Expose
    int role;
    @Expose
    UserInfo user_info;
    @Expose
    String login_token;
    @Expose
    int token_expire_time;

    @Override
    public String toString() {
        return "NetLoginResult{" +
                "ciphertext='" + ciphertext + '\'' +
                ", role=" + role +
                ", user_info=" + user_info +
                ", login_token='" + login_token + '\'' +
                ", token_expire_time=" + token_expire_time +
                '}';
    }

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public UserInfo getUser_info() {
        return user_info;
    }

    public void setUser_info(UserInfo user_info) {
        this.user_info = user_info;
    }

    public String getLogin_token() {
        return login_token;
    }

    public void setLogin_token(String login_token) {
        this.login_token = login_token;
    }

    public int getToken_expire_time() {
        return token_expire_time;
    }

    public void setToken_expire_time(int token_expire_time) {
        this.token_expire_time = token_expire_time;
    }
}
