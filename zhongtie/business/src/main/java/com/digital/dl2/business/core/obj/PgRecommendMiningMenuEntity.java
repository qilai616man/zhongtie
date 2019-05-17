package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetRecommendMiningMenuData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgRecommendMiningMenuEntity {
    private int entityId;//图书ID
    private String frontCover;//图书封面URL
    private String name;//图书名字
    private String author;//图书作者
    private float originalPrice;//原价格
    private float discountedPrice;//优惠价格
    private String recommendPeople;//推荐人


    public static PgRecommendMiningMenuEntity getPgByNet(NetRecommendMiningMenuData net){
        PgRecommendMiningMenuEntity pg = new PgRecommendMiningMenuEntity();
        pg.setEntityId(net.getGoods_id());
        pg.setName(net.getName());
        pg.setFrontCover(net.getFront_cover());
        pg.setAuthor(net.getAuthor());
        pg.setOriginalPrice(net.getPressprice());
        pg.setDiscountedPrice(net.getPrice());
        pg.setRecommendPeople(net.getUser_name());

        return pg;
    }

    public static List<PgRecommendMiningMenuEntity> getPgListByNetList(NetRecommendMiningMenuData[] netRecommendMiningMenuDatas){
        List<PgRecommendMiningMenuEntity> pgList = new ArrayList<>();

        for (NetRecommendMiningMenuData net : netRecommendMiningMenuDatas){
            PgRecommendMiningMenuEntity pg = getPgByNet(net);
            pgList.add(pg);
        }

        return pgList;
    }

    @Override
    public String toString() {
        return "PgRecommendMiningMenuEntity{" +
                "entityId=" + entityId +
                ", frontCover='" + frontCover + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", originalPrice=" + originalPrice +
                ", discountedPrice=" + discountedPrice +
                ", recommendPeople='" + recommendPeople + '\'' +
                '}';
    }

    public float getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(float originalPrice) {
        this.originalPrice = originalPrice;
    }

    public float getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(float discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String getFrontCover() {
        return frontCover;
    }

    public void setFrontCover(String frontCover) {
        this.frontCover = frontCover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRecommendPeople() {
        return recommendPeople;
    }

    public void setRecommendPeople(String recommendPeople) {
        this.recommendPeople = recommendPeople;
    }
}
