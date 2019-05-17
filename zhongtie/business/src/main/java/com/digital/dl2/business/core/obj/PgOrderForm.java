package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetBookCategoryData;
import com.digital.dl2.business.net.obj.NetCommodity;
import com.digital.dl2.business.net.obj.NetOrderFormData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgOrderForm {
    private int orderId;//订单ID
    private String entityId;//订单号
    private long buyDate;//购买时间
    private PgAddress address;//订单地址
    private int type;//订单类型（1：实体书，2：电子书）
    private int state;//订单状态（1：未付款，2：已付款）
    private float allPrice;//商品金额
    private float preferentialPrice;//优惠金额
    private float payPrice;//实付金额
    private List<PgCommodity> commodities; // 电子书类型对象
    private List<PgBookCategory> bookCategoryList; //类名+数量

    public static PgOrderForm getPgByNet(NetOrderFormData net){
        PgOrderForm pg = new PgOrderForm();
        List<PgCommodity> commodityList = new ArrayList<>();
        List<PgBookCategory> bookCategoryList = new ArrayList<>();

        NetCommodity[] netList = net.getNetCommodities();
        for(NetCommodity netCommodity : netList){
            PgCommodity pgCommodity = new PgCommodity();
            pgCommodity.setName(netCommodity.getGoods_name());
            pgCommodity.setNumber(netCommodity.getGoods_number());
            pgCommodity.setPrice(netCommodity.getPrice());
            pgCommodity.setPreferentialPrice(netCommodity.getMarket_price());
            pgCommodity.setOldPrice(netCommodity.getGoods_price());

            commodityList.add(pgCommodity);
        }

        NetBookCategoryData[] netBookCategorieList = net.getNetBookCategories();
        for (NetBookCategoryData netBookCategory : netBookCategorieList){
            PgBookCategory pgBookCategory = new PgBookCategory();
            pgBookCategory.setCategoryId(netBookCategory.getTypeid());
            pgBookCategory.setCategoryName(netBookCategory.getName());
            pgBookCategory.setCategoryNumber(netBookCategory.getNumber());
            bookCategoryList.add(pgBookCategory);
        }

        pg.setOrderId(net.getOrder_id());
        pg.setEntityId(net.getOrder_sn());
        pg.setBuyDate(net.getConfirm_time());
//        pg.setAddress(); //有问题
        pg.setType(net.getType());
        pg.setState(net.getOrder_status());
        pg.setAllPrice(net.getGoods_amount());
        pg.setPreferentialPrice(net.getPreferential_price());
        pg.setPayPrice(net.getPay_amount());
        pg.setCommodities(commodityList);
        pg.setBookCategoryList(bookCategoryList);

        return pg;
    }

    public static List<PgOrderForm> getPgListByNetList(NetOrderFormData[] netList){
        List<PgOrderForm> list = new ArrayList<>();
        for(NetOrderFormData net : netList){
            PgOrderForm pg = getPgByNet(net);
            list.add(pg);
        }
        return list;
    }

    @Override
    public String toString() {
        return "PgOrderForm{" +
                "orderId=" + orderId +
                ", entityId='" + entityId + '\'' +
                ", buyDate=" + buyDate +
                ", address=" + address +
                ", type=" + type +
                ", state=" + state +
                ", allPrice=" + allPrice +
                ", preferentialPrice=" + preferentialPrice +
                ", payPrice=" + payPrice +
                ", commodities=" + commodities +
                ", bookCategoryList=" + bookCategoryList +
                '}';
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public long getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(long buyDate) {
        this.buyDate = buyDate;
    }

    public PgAddress getAddress() {
        return address;
    }

    public void setAddress(PgAddress address) {
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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

    public float getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(float payPrice) {
        this.payPrice = payPrice;
    }

    public List<PgCommodity> getCommodities() {
        return commodities;
    }

    public void setCommodities(List<PgCommodity> commodities) {
        this.commodities = commodities;
    }

    public List<PgBookCategory> getBookCategoryList() {
        return bookCategoryList;
    }

    public void setBookCategoryList(List<PgBookCategory> bookCategoryList) {
        this.bookCategoryList = bookCategoryList;
    }
}
