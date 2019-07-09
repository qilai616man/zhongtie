package com.crphdm.dl2.user.obj;

import com.crphdm.dl2.user.obj.net.NetObject;

/**
 * Modify by songzixuan on 19/07/03.
 */
public class PgResult {
    //状态
    private boolean status;
    //错误
    private int errorCode;
    //信息
    private String message;

    public static PgResult getPgByNet(NetObject net) {
        PgResult pg = new PgResult();

        if (net != null) {
            pg.setStatus(net.isStatus());
            pg.setErrorCode(net.getError_code());
            pg.setMessage(net.getMessage());
        }

        return pg;
    }

    @Override
    public String toString() {
        return "PgResult{" +
                "status=" + status +
                ", errorCode=" + errorCode +
                ", message='" + message + '\'' +
                '}';
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
