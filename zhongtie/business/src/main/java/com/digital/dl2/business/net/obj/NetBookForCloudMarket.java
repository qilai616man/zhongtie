package com.digital.dl2.business.net.obj;

/**
 * Created by qinqi on 15/11/21.
 */
public class NetBookForCloudMarket {
    private boolean status;
    private int error_code;
    private String message;
    private NetBookForCloudMarketData[] data;

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

    public NetBookForCloudMarketData[] getData() {
        return data;
    }

    public void setData(NetBookForCloudMarketData[] data) {
        this.data = data;
    }
}
