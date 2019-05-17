package com.crphdm.dl2.user.obj.net;

/**
 * Created by qinqi on 15/12/24.
 */
public class NetUserInfoData {
    private int userid;
    private String username;
    private String truename;
    private String nickname;
    private String email;
    private int groupid;
    private int gender;
    private String bindemail;
    private String photo;

    @Override
    public String toString() {
        return "NetUserInfoData{" +
                "userid=" + userid +
                ", username='" + username + '\'' +
                ", truename='" + truename + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", groupid=" + groupid +
                ", gender=" + gender +
                ", bindemail='" + bindemail + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBindemail() {
        return bindemail;
    }

    public void setBindemail(String bindemail) {
        this.bindemail = bindemail;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
