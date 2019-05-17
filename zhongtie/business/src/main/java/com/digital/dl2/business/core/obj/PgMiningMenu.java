package com.digital.dl2.business.core.obj;

import java.util.List;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgMiningMenu {
    private int entityId;//采选单ID
    private long miningDate;//采选时间
    private int state;//采选单状态（1：正在审核，2：采选完成，3：审核不通过，4：未审核）
    private String annotation;//批注
    private String submitPeople;//提交人
    private int number;//图书数量
    private float allPrice;//总价格
    private float preferentialPrice;//优惠
    private List<PgShoppingCartItem> shoppingCartItems;

    @Override
    public String toString() {
        return "PgMiningMenu{" +
                "entityId='" + entityId + '\'' +
                ", miningDate=" + miningDate +
                ", state=" + state +
                ", annotation='" + annotation + '\'' +
                ", submitPeople='" + submitPeople + '\'' +
                ", number=" + number +
                ", allPrice=" + allPrice +
                ", preferentialPrice=" + preferentialPrice +
                ", shoppingCartItems=" + shoppingCartItems +
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

    public List<PgShoppingCartItem> getShoppingCartItems() {
        return shoppingCartItems;
    }

    public void setShoppingCartItems(List<PgShoppingCartItem> shoppingCartItems) {
        this.shoppingCartItems = shoppingCartItems;
    }
}
