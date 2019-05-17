package com.crphdm.dl2.user.obj.net;

import com.google.gson.annotations.Expose;

/**
 * Created by gaoyufei on 15/10/9.
 */
public class NetObject {
    @Expose
    boolean status;
    @Expose
    int error_code;
    @Expose
    String message;

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
}
