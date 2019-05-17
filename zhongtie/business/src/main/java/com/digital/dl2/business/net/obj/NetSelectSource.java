package com.digital.dl2.business.net.obj;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/5/6.
 */
public class NetSelectSource {
    private boolean status;
    private int error_code;
    private String message;
    private int noSelect_bookNumber;
    private int selected_bookNumber;
    private NetSelectSourceData[] data;

    @Override
    public String toString() {
        return "NetSelectSource{" +
                "status=" + status +
                ", error_code=" + error_code +
                ", message='" + message + '\'' +
                ", noSelect_bookNumber=" + noSelect_bookNumber +
                ", selected_bookNumber=" + selected_bookNumber +
                ", data=" + Arrays.toString(data) +
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

    public int getNoSelect_bookNumber() {
        return noSelect_bookNumber;
    }

    public void setNoSelect_bookNumber(int noSelect_bookNumber) {
        this.noSelect_bookNumber = noSelect_bookNumber;
    }

    public int getSelected_bookNumber() {
        return selected_bookNumber;
    }

    public void setSelected_bookNumber(int selected_bookNumber) {
        this.selected_bookNumber = selected_bookNumber;
    }

    public NetSelectSourceData[] getData() {
        return data;
    }

    public void setData(NetSelectSourceData[] data) {
        this.data = data;
    }
}
