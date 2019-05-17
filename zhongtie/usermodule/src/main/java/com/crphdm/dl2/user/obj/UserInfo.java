package com.crphdm.dl2.user.obj;

import com.google.gson.annotations.Expose;

/**
 * Created by gaoyufei on 15/10/9.
 */
public class UserInfo {
    @Expose
    int userid;
    @Expose
    String photo;
    @Expose
    String username;
    @Expose
    String nickname;
    @Expose
    String truename;
    @Expose
    int sex;
    @Expose
    String mobile;
    @Expose
    String email;
    @Expose
    String org_name;
    @Expose
    int bind_org_state;
    @Expose
    int org_id;
    @Expose
    String firstKey;
    @Expose
    String secondKey;

    @Override
    public String toString() {
        return "UserInfo{" +
                "userid=" + userid +
                ", photo='" + photo + '\'' +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", truename='" + truename + '\'' +
                ", sex=" + sex +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", org_name='" + org_name + '\'' +
                ", bind_org_state=" + bind_org_state +
                ", org_id=" + org_id +
                ", firstKey='" + firstKey + '\'' +
                ", secondKey='" + secondKey + '\'' +
                '}';
    }

    public UserInfo() {

    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public int getBind_org_state() {
        return bind_org_state;
    }

    public void setBind_org_state(int bind_org_state) {
        this.bind_org_state = bind_org_state;
    }

    public int getOrg_id() {
        return org_id;
    }

    public void setOrg_id(int org_id) {
        this.org_id = org_id;
    }

    public String getFirstKey() {
        return firstKey;
    }

    public void setFirstKey(String firstKey) {
        this.firstKey = firstKey;
    }

    public String getSecondKey() {
        return secondKey;
    }

    public void setSecondKey(String secondKey) {
        this.secondKey = secondKey;
    }
}
