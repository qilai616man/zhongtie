package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetOrderListData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qinqi on 15/12/12.
 */
public class PgOrderList {
    private int orderId;
    private String orderSn;
    private long confirmTime;
    private int status;
    private float preferentialPrice;
    private int type;
    private List<PgBookCategory> book;

    public static PgOrderList getPgByNet(NetOrderListData net) {
        PgOrderList pg = new PgOrderList();

        pg.setOrderId(net.getOrder_id());
        pg.setOrderSn(net.getOrder_sn());
        pg.setConfirmTime(net.getConfirm_time());
        pg.setStatus(net.getOrder_status());
        pg.setPreferentialPrice(net.getPreferential_price());
        pg.setType(net.getType());
        if (net.getGoods() != null) {
            pg.setBook(PgBookCategory.getPgListByNetList(net.getGoods()));
        }
        return pg;
    }

    public static List<PgOrderList> getPgListByNetList(NetOrderListData[] netList) {
        List<PgOrderList> list = new ArrayList<>();

        for (NetOrderListData net: netList) {
            PgOrderList pg = getPgByNet(net);
            list.add(pg);
        }
        return list;
    }

    @Override
    public String toString() {
        return "PgOrderList{" +
                "orderId=" + orderId +
                ", orderSn='" + orderSn + '\'' +
                ", confirmTime=" + confirmTime +
                ", status=" + status +
                ", preferentialPrice=" + preferentialPrice +
                ", type=" + type +
                ", book=" + book +
                '}';
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public long getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(long confirmTime) {
        this.confirmTime = confirmTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getPreferentialPrice() {
        return preferentialPrice;
    }

    public void setPreferentialPrice(float preferentialPrice) {
        this.preferentialPrice = preferentialPrice;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<PgBookCategory> getBook() {
        return book;
    }

    public void setBook(List<PgBookCategory> book) {
        this.book = book;
    }
}
