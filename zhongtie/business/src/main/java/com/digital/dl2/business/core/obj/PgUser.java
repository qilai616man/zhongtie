package com.digital.dl2.business.core.obj;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgUser {
    private int userId;//用户ID
    private String userName;//用户名（登录名）
    private String icon;//用户头像
    private String nickName;//昵称
    private String realName;//真实姓名
    private int sex;//1：男，2：女
    private String bindNumber;//绑定手机号
    private int bindNumberState;//绑定手机号状态（1.未绑定  2. 审核中  3.申请成功  4.申请失败）
    private String bindingAgency;//绑定机构
    private int userCharacter;//角色（1：非机构，2：机构普通用户，3：机构领导，4：机构采选员）

    @Override
    public String toString() {
        return "PgUser{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", icon='" + icon + '\'' +
                ", nickName='" + nickName + '\'' +
                ", realName='" + realName + '\'' +
                ", sex=" + sex +
                ", bindNumber='" + bindNumber + '\'' +
                ", bindNumberState=" + bindNumberState +
                ", bindingAgency='" + bindingAgency + '\'' +
                ", userCharacter=" + userCharacter +
                '}';
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getBindNumber() {
        return bindNumber;
    }

    public void setBindNumber(String bindNumber) {
        this.bindNumber = bindNumber;
    }

    public int getBindNumberState() {
        return bindNumberState;
    }

    public void setBindNumberState(int bindNumberState) {
        this.bindNumberState = bindNumberState;
    }

    public String getBindingAgency() {
        return bindingAgency;
    }

    public void setBindingAgency(String bindingAgency) {
        this.bindingAgency = bindingAgency;
    }

    public int getUserCharacter() {
        return userCharacter;
    }

    public void setUserCharacter(int userCharacter) {
        this.userCharacter = userCharacter;
    }
}
