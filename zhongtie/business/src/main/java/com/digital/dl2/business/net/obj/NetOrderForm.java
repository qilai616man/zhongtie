package com.digital.dl2.business.net.obj;

import java.util.Arrays;

/**
 * Created by digital.dl2 on 15/10/10.
 */
public class NetOrderForm {
    private boolean status;
    private int error_code;
    private String message;
    private NetOrderFormData[] data;

    @Override
    public String toString() {
        return "NetOrderForm{" +
                "status=" + status +
                ", error_code=" + error_code +
                ", message='" + message + '\'' +
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

    public NetOrderFormData[] getData() {
        return data;
    }

    public void setData(NetOrderFormData[] data) {
        this.data = data;
    }
}
