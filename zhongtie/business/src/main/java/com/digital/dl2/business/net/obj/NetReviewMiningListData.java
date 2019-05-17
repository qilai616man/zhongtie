package com.digital.dl2.business.net.obj;

import java.util.Arrays;

/**
 * Created by qinqi on 15/12/10.
 */
public class NetReviewMiningListData {
    private int demandid;
    private int isbuy;
    private int organizationid;
    private long submit_time;
    private String reject;
    private int status;//0.待审核 1.已审核 2.打回
    private String submit_people;
    private float all_price;
    private float preferential_price;
    private NetReviewMiningCategories[] categories;

    @Override
    public String toString() {
        return "NetReviewMiningData{" +
                "demandid=" + demandid +
                ", isbuy=" + isbuy +
                ", organizationid=" + organizationid +
                ", submit_time=" + submit_time +
                ", reject='" + reject + '\'' +
                ", status=" + status +
                ", submit_people='" + submit_people + '\'' +
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

    public NetReviewMiningCategories[] getCategories() {
        return categories;
    }

    public void setCategories(NetReviewMiningCategories[] categories) {
        this.categories = categories;
    }
}
