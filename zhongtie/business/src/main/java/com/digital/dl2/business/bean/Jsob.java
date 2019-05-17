package com.digital.dl2.business.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/7.
 */

public class Jsob {
    /**
     * status : true
     * message : 获取数据成功
     * data : ["6","7","8"]
     */

    private boolean status;
    private String message;
    private List<String> data;
    /**
     * time : 5
     */

    private int time;

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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
/**
 * {"status":true,"time":5,"message":"\u83b7\u53d6\u6570\u636e\u6210\u529f","data":["6","7","8"]}

 */

}
