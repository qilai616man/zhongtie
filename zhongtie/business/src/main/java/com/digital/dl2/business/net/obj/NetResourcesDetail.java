package com.digital.dl2.business.net.obj;

/**
 * Created by digital.dl2 on 15/9/28.
 */
public class NetResourcesDetail {
    private boolean status;
    private int error_code;
    private String message;
    private NetResourcesDetailData data;

    @Override
    public String toString() {
        return "NetResourcesDetail{" +
                "status=" + status +
                ", error_code=" + error_code +
                ", message='" + message + '\'' +
                ", resourcesDetailData=" + data.toString() +
                '}';
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

    public NetResourcesDetailData getData() {
        return data;
    }

    public void setData(NetResourcesDetailData data) {
        this.data = data;
    }

    public boolean isStatus() {

        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
