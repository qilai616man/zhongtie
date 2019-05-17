package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetPayOrderDetail;
import com.digital.dl2.business.net.obj.NetPayOrderDetailData;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qinqi on 15/12/11.
 */
public class PgPayOrderInfo {
    private int orderId;
    private String orderSn;
    private int userId;
    private long buyTime;
    private int status;
    private float allPrice;
    private float preferentialPrice;
    private int type;
    private PgAddress address;
    private List<PgPayOrderDetail> orderDetailList;

    private static List<PgPayOrderDetail> getOrderDetail(NetPayOrderDetailData[] netList) {
        List<PgPayOrderDetail> list = new ArrayList<>();

        for (NetPayOrderDetailData net : netList) {
            PgPayOrderDetail pg = new PgPayOrderDetail();
            pg.setId(net.getId());
            pg.setGoodsId(net.getGoods_id());
            pg.setGoodsName(net.getGoods_name());
            pg.setGoodsNumber(net.getGoods_number());
            pg.setPressPrice(net.getPressprice());
            pg.setPrice(net.getPrice());
            list.add(pg);
        }

        return list;
    }

    public static PgPayOrderInfo getPgByNet(NetPayOrderDetail net) {

        PgPayOrderInfo pg = new PgPayOrderInfo();
        if (net != null && net.getOrder_info() != null) {
            Ln.d("PgPayOrderInfo:getPgByNet:net:" + net.toString());

            pg.setOrderId(net.getOrder_info().getOrder_id());
            pg.setOrderSn(net.getOrder_info().getOrder_sn());
            pg.setUserId(net.getOrder_info().getUser_id());
            pg.setBuyTime(net.getOrder_info().getConfirm_time());
            pg.setStatus(net.getOrder_info().getOrder_status());
            pg.setAllPrice(net.getOrder_info().getAll_price());
            pg.setPreferentialPrice(net.getOrder_info().getPreferential_price());
            pg.setType(net.getOrder_info().getType());

            Ln.d("PgPayOrderInfo:getPgByNet:pg(one):" + pg.toString());

            if (net.getOrder_info().getAddress() != null) {
                pg.setAddress(PgAddress.getPgByNetPayOrder(net.getOrder_info().getAddress()));
            }

            Ln.d("PgPayOrderInfo:getPgByNet:pg(two):" + pg.toString());

            if (net.getOrder_detail() != null && net.getOrder_detail().length != 0) {
                List<PgPayOrderDetail> list = getOrderDetail(net.getOrder_detail());
                pg.setOrderDetailList(list);
            }

            Ln.d("PgPayOrderInfo:getPgByNet:pg(three):" + pg.toString());
        }

        return pg;
    }

    @Override
    public String toString() {
        return "PgPayOrderInfo{" +
                "orderId=" + orderId +
                ", orderSn='" + orderSn + '\'' +
                ", userId=" + userId +
                ", buyTime=" + buyTime +
                ", status=" + status +
                ", allPrice=" + allPrice +
                ", preferentialPrice=" + preferentialPrice +
                ", type=" + type +
                ", address=" + address +
                ", orderDetailList=" + orderDetailList +
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PgAddress getAddress() {
        return address;
    }

    public void setAddress(PgAddress address) {
        this.address = address;
    }

    public List<PgPayOrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public void setOrderDetailList(List<PgPayOrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }
}
