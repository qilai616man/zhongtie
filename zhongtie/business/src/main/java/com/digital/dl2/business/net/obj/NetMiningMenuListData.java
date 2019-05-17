package com.digital.dl2.business.net.obj;

import java.util.Arrays;

/**
 * Created by digital.dl2 on 15/10/12.
 */
public class NetMiningMenuListData {
    private int demandid;//采选单ID
    private int submit_time;//采选时间
    private int status;//1.待审核  2.已审核  3.未通过 打回
    private String reject;//备注
    private int num;//图书数量
    private float all_price;//总价
    private float preferential_price;//优惠价格
    private NetMiningMenuCategories[] categories;//商品列表

    @Override
    public String toString() {
        return "NetMiningMenuListData{" +
                "demandid=" + demandid +
                ", submit_time=" + submit_time +
                ", status=" + status +
                ", reject='" + reject + '\'' +
                ", num=" + num +
                ", all_price=" + all_price +
                ", preferential_price=" + preferential_price +
                ", categories=" + Arrays.toString(categories) +
                '}';
    }

    public int getDemandid() {
        return demandid;
    }

    public void setDemandid(int demandid) {
        this.demandid = demandid;
    }

    public int getSubmit_time() {
        return submit_time;
    }

    public void setSubmit_time(int submit_time) {
        this.submit_time = submit_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReject() {
        return reject;
    }

    public void setReject(String reject) {
        this.reject = reject;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
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

    public NetMiningMenuCategories[] getCategories() {
        return categories;
    }

    public void setCategories(NetMiningMenuCategories[] categories) {
        this.categories = categories;
    }
}
