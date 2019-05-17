package com.digital.dl2.business.bean;

/**
 * Created by Administrator on 2017/11/29.
 */

public class ExceedTime {
    /**
     * status : false
     * error_code : 100
     * user_id : 100
     * username : swqwe
     * time : 100
     */

    private boolean status;
    private int error_code;
    private int user_id;
    private String username;
    private int time;

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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
    /**
     * 参数
     {
     login_token:(string)
     user_id:(int)
     }
     返回值：
     {
     status: true/false,
     error_code: (int)"错误码，无错误返回0，其他具体错误再商定吧",
     user_id:(int)用户ID
     username:(string)
     time: (int)借阅期限时间：0为永久不过期，其他数字为多少秒后过期，该字段可能为空。
     }
     */


    /**
     * {"status": false,
     "error_code": 100,
     "user_id":100,
     "username":"swqwe",
     "time": 100}
     */

}
