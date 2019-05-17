package com.digital.dl2.business.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/8/7.
 */

public class Userjson {
    /**
     * status : true
     * message : 获取成功
     * data : ["m270100001","m270100002","m270100003","m270100005","m270100006","m270100007","m270100008","m270100009","m270100010","m270100011","a270100001","a270100002","a270100003","a270100005","a270100006","a270100007","a270100008","a270100009","a270100010","a270100011"]
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
    /**
     "status":true,
     "message":"获取成功",
     "data":[]
     */

/**
 * {"status":true,"time":5,"message":"\u83b7\u53d6\u6570\u636e\u6210\u529f","data":["6","7","8"]}

 */

}
