package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetBookCategoryData;
import com.digital.dl2.business.net.obj.NetOrderFormData;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by digital.dl2 on 15/10/12.
 */
public class PgPayManagement {
    private int orderId;
    private String orderNumber;
    private int type;//1.实体书  2.电子书
    private float price;
    private long buyTime;
    private int status;
    private List<PgBookCategory> bookCategory;

    public static PgPayManagement getPgByNet(NetOrderFormData net){
        Ln.d("PgPayManagement:getPgByNet:" + net.toString());
        List<PgBookCategory> list = new ArrayList<>();

        if(net != null && net.getNetBookCategories().length != 0){
            for(NetBookCategoryData netBookCategory : net.getNetBookCategories()){
                PgBookCategory pgBookCategory = new PgBookCategory();
                pgBookCategory.setCategoryId(netBookCategory.getTypeid());
                pgBookCategory.setCategoryName(netBookCategory.getName());
                pgBookCategory.setCategoryNumber(netBookCategory.getNumber());
                list.add(pgBookCategory);
            }
        }

        PgPayManagement pg = new PgPayManagement();
        pg.setOrderId(net.getOrder_id());
        pg.setOrderNumber(net.getOrder_sn());
        pg.setType(net.getType());
        pg.setPrice(net.getPay_amount());
        pg.setBuyTime(net.getConfirm_time());
        pg.setStatus(net.getOrder_status());
        pg.setBookCategory(list);

        return pg;
    }

    public static List<PgPayManagement> getPgListByNetList(NetOrderFormData[] netList){
        Ln.d("PgPayManagement:getPgListByNetList:" + Arrays.toString(netList));
        List<PgPayManagement> list = new ArrayList<>();
        for(NetOrderFormData net : netList){
            PgPayManagement pg = getPgByNet(net);
            Ln.d("PgPayManagement:getPgListByNetList:pg:" + pg.toString());
            list.add(pg);
        }
        return list;
    }

    @Override
    public String toString() {
        return "PgPayManagement{" +
                "orderId=" + orderId +
                ", orderNumber='" + orderNumber + '\'' +
                ", type=" + type +
                ", price=" + price +
                ", buyTime=" + buyTime +
                ", status=" + status +
                ", bookCategory=" + bookCategory +
                '}';
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public long getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(long buyTime) {
        this.buyTime = buyTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<PgBookCategory> getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(List<PgBookCategory> bookCategory) {
        this.bookCategory = bookCategory;
    }
}
