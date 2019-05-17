package com.digital.dl2.business.net.obj;

import java.util.Arrays;

/**
 * Created by digital.dl2 on 15/9/28.
 */
public class NetBookCategory {
    private boolean status;
    private int error_code;
    private String message;
    private NetBookCategoryData[] data;

    @Override
    public String toString() {
        return "NetBookCategory{" +
                "status=" + status +
                ", error_code=" + error_code +
                ", message='" + message + '\'' +
                ", netBookCategoryDatas=" + Arrays.toString(data) +
                '}';
    }

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

    public NetBookCategoryData[] getData() {
        return data;
    }

    public void setData(NetBookCategoryData[] data) {
        this.data = data;
    }
}
