package com.crphdm.dl2.user.obj.net;

/**
 * Created by gaoyufei on 15/10/9.
 */
public class NetUpdateUserInfoResult {
    private boolean status;
    private int error_code;
    private String message;
    private NetUserInfoData user_info;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NetUserInfoData getUser_info() {
        return user_info;
    }

    public void setUser_info(NetUserInfoData user_info) {
        this.user_info = user_info;
    }
}
