package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetShoppingCartBookData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgShoppingCartItem {
    private int entityId;
    private String frontCover;//封面Url
    private int number;//数量
    private String name;//名字
    private String author;//作者
    private PgBookTypeEntity bookType;

    public static List<PgShoppingCartItem> getPgListByNetList(NetShoppingCartBookData[] netList){
        List<PgShoppingCartItem> list = new ArrayList<>();

        for(NetShoppingCartBookData net : netList){
            PgShoppingCartItem pg = new PgShoppingCartItem();
            pg.setEntityId(net.getGoods_id());
            pg.setFrontCover(net.getThumb());
            pg.setNumber(net.getGoods_number());
            pg.setName(net.getTitle());
            pg.setAuthor(net.getAuthor());

            PgBookTypeEntity pgBookTypeEntity = getPgBookTypeEntityByNet(net.getType(),net.getPrice(),net.getPressprice());
            pg.setBookType(pgBookTypeEntity);

            list.add(pg);
        }

        return list;
    }

    private static PgBookTypeEntity getPgBookTypeEntityByNet(int type,float price,float discountedPrice){
        PgBookTypeEntity pg = new PgBookTypeEntity();
        pg.setType(type);
        pg.setPrice(price);
        pg.setPressPrice(discountedPrice);

        if(type == 6){
            pg.setPodPrice(price);
        }

        return pg;
    }

    @Override
    public String toString() {
        return "PgShoppingCartItem{" +
                "entityId=" + entityId +
                ", frontCover='" + frontCover + '\'' +
                ", number=" + number +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", bookType=" + bookType +
                '}';
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public PgBookTypeEntity getBookType() {
        return bookType;
    }

    public void setBookType(PgBookTypeEntity bookType) {
        this.bookType = bookType;
    }

}
