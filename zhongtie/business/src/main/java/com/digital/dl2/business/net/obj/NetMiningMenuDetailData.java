package com.digital.dl2.business.net.obj;

import java.util.Arrays;

/**
 * Created by digital.dl2 on 15/10/12.
 */
public class NetMiningMenuDetailData {
    private int demandid;//采选单ID
    private int isbuy;//1.买断  2.租借
    private int organizationid; // 机构ID
    private long submit_time;// 提交时间
    private String reject;// 备注
    private int status;// 1.待审核  2.已审核  3.未通过
    private String submit_people;//提交人
    private int number;//数量
    private float all_price;//总价格
    private float preferential_price;//优惠价格
    private NetMiningMenuDetailBookItemData[] shopping_cart_item;

    @Override
    public String toString() {
        return "NetMiningMenuDetailData{" +
                "demandid=" + demandid +
                ", isbuy=" + isbuy +
                ", organizationid=" + organizationid +
                ", submit_time=" + submit_time +
                ", reject='" + reject + '\'' +
                ", status=" + status +
                ", submit_people='" + submit_people + '\'' +
                ", number=" + number +
                ", all_price=" + all_price +
                ", preferential_price=" + preferential_price +
                ", shopping_cart_item=" + Arrays.toString(shopping_cart_item) +
                '}';
    }

    public int getDemandid() {
        return demandid;
    }

    public void setDemandid(int demandid) {
        this.demandid = demandid;
    }

    public int getIsbuy() {
        return isbuy;
    }

    public void setIsbuy(int isbuy) {
        this.isbuy = isbuy;
    }

    public int getOrganizationid() {
        return organizationid;
    }

    public void setOrganizationid(int organizationid) {
        this.organizationid = organizationid;
    }

    public long getSubmit_time() {
        return submit_time;
    }

    public void setSubmit_time(long submit_time) {
        this.submit_time = submit_time;
    }

    public String getReject() {
        return reject;
    }

    public void setReject(String reject) {
        this.reject = reject;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSubmit_people() {
        return submit_people;
    }

    public void setSubmit_people(String submit_people) {
        this.submit_people = submit_people;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public float getAll_price() {
        return all_price;
    }

    public void setAll_price(float all_price) {
        this.all_price = all_price;
    }

    public float getPreferential_price() {
        return preferential_price;
    }

    public void setPreferential_price(float preferential_price) {
        this.preferential_price = preferential_price;
    }

    public NetMiningMenuDetailBookItemData[] getShopping_cart_item() {
        return shopping_cart_item;
    }

    public void setShopping_cart_item(NetMiningMenuDetailBookItemData[] shopping_cart_item) {
        this.shopping_cart_item = shopping_cart_item;
    }
}
