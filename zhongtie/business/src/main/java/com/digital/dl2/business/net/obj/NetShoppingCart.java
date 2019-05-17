package com.digital.dl2.business.net.obj;

/**
 * Created by digital.dl2 on 15/10/8.
 */
public class NetShoppingCart {
    private boolean status;
    private int error_code;
    private String message;
    private NetShoppingCartTotalData data;

    @Override
    public String toString() {
        return "NetShoppingCart{" +
                "status=" + status +
                ", error_code=" + error_code +
                ", message='" + message + '\'' +
                ", data=" + data +
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

    public NetShoppingCartTotalData getData() {
        return data;
    }

    public void setData(NetShoppingCartTotalData data) {
        this.data = data;
    }
}
