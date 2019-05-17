package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetSearchBookData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qinqi on 15/12/12.
 */
public class PgSearchBook {
    private int goodsId;
    private String thumb;
    private String title;
    private String author;
    private String description;
    private int bookType;//1.图书   2.资源
    private int type;
    private float pressPrice;
    private float price;

    private static PgSearchBook getPgByNet(NetSearchBookData net){
        PgSearchBook pg = new PgSearchBook();
        if(net != null){
            pg.setGoodsId(net.getGoods_id());
            pg.setThumb(net.getThumb());
            pg.setTitle(net.getTitle());
            pg.setAuthor(net.getAuthor());
            pg.setDescription(net.getDescription());
            pg.setBookType(net.getBook_type());
            pg.setType(net.getType());
            pg.setPressPrice(net.getPressprice());
            pg.setPrice(net.getPrice());
        }

        return pg;
    }

    public static List<PgSearchBook> getPgListByNetList(NetSearchBookData[] netList){
        List<PgSearchBook> list = new ArrayList<>();

        if(netList != null && netList.length != 0){
            for (NetSearchBookData net : netList ) {
                PgSearchBook pg = getPgByNet(net);

                list.add(pg);
            }
        }

        return list;
    }

    @Override
    public String toString() {
        return "PgSearchBook{" +
                "goodsId=" + goodsId +
                ", thumb='" + thumb + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", bookType=" + bookType +
                ", type=" + type +
                ", pressPrice=" + pressPrice +
                ", price=" + price +
                '}';
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getPressPrice() {
        return pressPrice;
    }

    public void setPressPrice(float pressPrice) {
        this.pressPrice = pressPrice;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
