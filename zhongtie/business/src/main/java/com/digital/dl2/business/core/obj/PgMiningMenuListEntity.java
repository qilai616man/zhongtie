package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetMiningMenuCategories;
import com.digital.dl2.business.net.obj.NetMiningMenuListData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/10/9.
 */
public class PgMiningMenuListEntity {
    private int entityId;
    private long miningDate;
    private int state;//采选单状态  1.正在审核 2.采选完成 3.审核不通过 4.未审核
    private String annotation;//批注
    private String submitPeople;
    private int number;
    private float allPrice;
    private float preferentialPrice;
    private List<String> bookNameList;
    private List<PgBookCategory> bookCategories;

    public static PgMiningMenuListEntity getPgByNet(NetMiningMenuListData net){
        List<PgBookCategory> pgBookCategoryList = new ArrayList<>();
        List<String> bookNames = new ArrayList<>();

        if(net.getCategories() != null && net.getCategories().length != 0){
            for(NetMiningMenuCategories netMiningMenuCategories : net.getCategories()){
                PgBookCategory pg = new PgBookCategory();
                pg.setCategoryName(netMiningMenuCategories.getCat_name());
                pg.setCategoryNumber(netMiningMenuCategories.getNumber());

                pgBookCategoryList.add(pg);
            }
        }

        PgMiningMenuListEntity pg = new PgMiningMenuListEntity();
        pg.setEntityId(net.getDemandid());
        pg.setState(net.getStatus());
        pg.setAnnotation(net.getReject());
        pg.setNumber(net.getNum());
        pg.setAllPrice(net.getAll_price());
        pg.setPreferentialPrice(net.getPreferential_price());
        pg.setMiningDate(net.getSubmit_time());
        pg.setBookNameList(bookNames);
        pg.setBookCategories(pgBookCategoryList);

        return pg;
    }

    public static List<PgMiningMenuListEntity> getPgListByNetList(NetMiningMenuListData[] netList){
        List<PgMiningMenuListEntity> list = new ArrayList<>();
        for(NetMiningMenuListData net : netList){
            PgMiningMenuListEntity pg = getPgByNet(net);
            list.add(pg);
        }

        return list;
    }

    @Override
    public String toString() {
        return "PgMiningMenuListEntity{" +
                "entityId=" + entityId +
                ", miningDate=" + miningDate +
                ", state=" + state +
                ", annotation='" + annotation + '\'' +
                ", submitPeople='" + submitPeople + '\'' +
                ", number=" + number +
                ", allPrice=" + allPrice +
                ", preferentialPrice=" + preferentialPrice +
                ", bookNameList=" + bookNameList +
                ", bookCategories=" + bookCategories +
                '}';
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public long getMiningDate() {
        return miningDate;
    }

    public void setMiningDate(long miningDate) {
        this.miningDate = miningDate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getSubmitPeople() {
        return submitPeople;
    }

    public void setSubmitPeople(String submitPeople) {
        this.submitPeople = submitPeople;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public List<String> getBookNameList() {
        return bookNameList;
    }

    public void setBookNameList(List<String> bookNameList) {
        this.bookNameList = bookNameList;
    }

    public List<PgBookCategory> getBookCategories() {
        return bookCategories;
    }

    public void setBookCategories(List<PgBookCategory> bookCategories) {
        this.bookCategories = bookCategories;
    }
}
