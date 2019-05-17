package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetMiningMenuDetailBookItemData;
import com.digital.dl2.business.net.obj.NetMiningMenuDetailData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/10/9.
 */
public class PgMiningMenuDetail {
    private int entityId;
    private long miningDate;
    private int state;//采选单状态  1.正在审核 2.采选完成 3.审核不通过 4.未审核
    private String annotation;//批注
    private String submitPeople;
    private int number;
    private float allPrice;
    private float preferentialPrice;
    private List<PgMiningMenuDetailItem> shoppingCartItemList;

    private  static List<PgMiningMenuDetailItem> getShoppingCartItem(NetMiningMenuDetailBookItemData[] netList){
        List<PgMiningMenuDetailItem> shoppingCartItemList = new ArrayList<>();

        for(NetMiningMenuDetailBookItemData net : netList){
            PgMiningMenuDetailItem pg = new PgMiningMenuDetailItem();
            pg.setId(net.getId());
            pg.setOrgId(net.getOrg_id());
            pg.setGoodsId(net.getGoods_id());
            pg.setThumb(net.getThumb());
            pg.setNumber(net.getGoods_number());
            pg.setTitle(net.getTitle());
            pg.setAuthor(net.getAuthor());
            pg.setPrice(net.getPrice());
            pg.setOldPrice(net.getPressprice());
            pg.setAddTime(net.getAdd_time());

            shoppingCartItemList.add(pg);
        }

        return shoppingCartItemList;
    }

    public static PgMiningMenuDetail getPgByNet(NetMiningMenuDetailData net){
        List<PgMiningMenuDetailItem> shoppingCartItemList = new ArrayList<>();

        PgMiningMenuDetail pg = new PgMiningMenuDetail();
        pg.setEntityId(net.getDemandid());
        pg.setMiningDate(net.getSubmit_time());
        pg.setState(net.getStatus());
        pg.setAnnotation(net.getReject());
        pg.setSubmitPeople(net.getSubmit_people());
        pg.setNumber(net.getNumber());
        pg.setAllPrice(net.getAll_price());
        pg.setPreferentialPrice(net.getPreferential_price());

        if(net.getShopping_cart_item() != null && net.getShopping_cart_item().length != 0){
            shoppingCartItemList = getShoppingCartItem(net.getShopping_cart_item());
        }
        pg.setShoppingCartItemList(shoppingCartItemList);

        return pg;
    }

    @Override
    public String toString() {
        return "PgMiningMenuDetail{" +
                "entityId=" + entityId +
                ", miningDate=" + miningDate +
                ", state=" + state +
                ", annotation='" + annotation + '\'' +
                ", submitPeople='" + submitPeople + '\'' +
                ", number=" + number +
                ", allPrice=" + allPrice +
                ", preferentialPrice=" + preferentialPrice +
                ", shoppingCartItemList=" + shoppingCartItemList +
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

    public List<PgMiningMenuDetailItem> getShoppingCartItemList() {
        return shoppingCartItemList;
    }

    public void setShoppingCartItemList(List<PgMiningMenuDetailItem> shoppingCartItemList) {
        this.shoppingCartItemList = shoppingCartItemList;
    }
}
