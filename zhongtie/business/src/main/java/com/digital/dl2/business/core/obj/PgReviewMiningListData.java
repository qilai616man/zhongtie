package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetReviewMiningCategories;
import com.digital.dl2.business.net.obj.NetReviewMiningListData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qinqi on 15/12/10.
 */
public class PgReviewMiningListData {
    private int demandId;
    private int isBuy;
    private int orgId;
    private long submitTime;
    private String reject;
    private int status;//0.待审核 1.已审核 2.打回
    private String submitPeople;
    private float allPrice;
    private float preferentialPrice;
    private List<PgReviewMiningCategories> categories;

    public static PgReviewMiningListData getPgByNet(NetReviewMiningListData net) {
        PgReviewMiningListData pg = new PgReviewMiningListData();
        List<PgReviewMiningCategories> pgCategoriesList = new ArrayList<>();

        if (net != null) {
            pg.setDemandId(net.getDemandid());
            pg.setIsBuy(net.getIsbuy());
            pg.setOrgId(net.getOrganizationid());
            pg.setSubmitTime(net.getSubmit_time());
            pg.setReject(net.getReject());
            pg.setStatus(net.getStatus());
            pg.setSubmitPeople(net.getSubmit_people());
            pg.setAllPrice(net.getAll_price());
            pg.setPreferentialPrice(net.getPreferential_price());

            if (net.getCategories() != null && net.getCategories().length != 0) {

                for (NetReviewMiningCategories netCategories : net.getCategories()) {
                    PgReviewMiningCategories pgCategories = new PgReviewMiningCategories();
                    pgCategories.setName(netCategories.getCat_name());
                    pgCategories.setNumber(netCategories.getNumber());

                    pgCategoriesList.add(pgCategories);
                }
                pg.setCategories(pgCategoriesList);
            }
        }

        return pg;
    }

    public static List<PgReviewMiningListData> getPgListByNetList(NetReviewMiningListData[] netList) {
        List<PgReviewMiningListData> pgList = new ArrayList<>();
        for (NetReviewMiningListData net : netList) {
            PgReviewMiningListData pg = getPgByNet(net);
            if(pg != null){
                pgList.add(pg);
            }
        }
        return pgList;
    }

    @Override
    public String toString() {
        return "PgReviewMiningListData{" +
                "demandId=" + demandId +
                ", isBuy=" + isBuy +
                ", orgId=" + orgId +
                ", submitTime=" + submitTime +
                ", reject='" + reject + '\'' +
                ", status=" + status +
                ", submitPeople='" + submitPeople + '\'' +
                ", allPrice=" + allPrice +
                ", preferentialPrice=" + preferentialPrice +
                ", categories=" + categories +
                '}';
    }

    public int getDemandId() {
        return demandId;
    }

    public void setDemandId(int demandId) {
        this.demandId = demandId;
    }

    public int getIsBuy() {
        return isBuy;
    }

    public void setIsBuy(int isBuy) {
        this.isBuy = isBuy;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(long submitTime) {
        this.submitTime = submitTime;
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

    public String getSubmitPeople() {
        return submitPeople;
    }

    public void setSubmitPeople(String submitPeople) {
        this.submitPeople = submitPeople;
    }

    public float getAllPrice() {
        return allPrice;
    }

    public void setAllPrice(float allPrice) {
        this.allPrice = allPrice;
    }

    public float getPreferentialPrice() {
        return preferentialPrice;
    }

    public void setPreferentialPrice(float preferentialPrice) {
        this.preferentialPrice = preferentialPrice;
    }

    public List<PgReviewMiningCategories> getCategories() {
        return categories;
    }

    public void setCategories(List<PgReviewMiningCategories> categories) {
        this.categories = categories;
    }
}
