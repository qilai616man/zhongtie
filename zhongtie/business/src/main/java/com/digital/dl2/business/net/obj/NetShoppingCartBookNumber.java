package com.digital.dl2.business.net.obj;

/**
 * Created by digital.dl2 on 15/10/9.
 */
public class NetShoppingCartBookNumber {
    private boolean status;
    private int error_code;
    private String message;
    private int num;

    @Override
    public String toString() {
        return "NetShoppingCartBookNumber{" +
                "status=" + status +
                ", error_code=" + error_code +
                ", message='" + message + '\'' +
                ", num=" + num +
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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
