package org;

import java.util.List;

/**
 * Created by Administrator on 2017/8/7.
 */

public class jsob {

    /**
     * status : true
     * message : 获取数据成功
     * data : ["6"]
     */

    private boolean status;
    private String message;
    private List<String> data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
