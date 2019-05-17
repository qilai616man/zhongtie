package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetSearchBookData;
import com.digital.dl2.business.net.obj.NetSelectSearchBookData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/6.
 */
public class PgSelectSearchBook {
    private int goodsId;
    private String thumb;
    private String title;
    private String author;
    private String description;
    private int bookType;
    private int selectType;
    private int type;
    private float pressprice;
    private float price;

    private static PgSelectSearchBook getPgByNet(NetSelectSearchBookData net){
        PgSelectSearchBook pg = new PgSelectSearchBook();
        if(net !=null){
            pg.setGoodsId(net.getGoods_id());
            pg.setThumb(net.getThumb());
            pg.setTitle(net.getTitle());
            pg.setAuthor(net.getAuthor());
            pg.setDescription(net.getDescription());
            pg.setBookType(net.getBook_type());
            pg.setSelectType(net.getSelectType());
            pg.setType(net.getType());
            pg.setPressprice(net.getPressprice());
            pg.setPrice(net.getPrice());
        }
        return pg;
    }

//    public static List<PgSearchBook> getPgListByNetList(NetSearchBookData[] netList){
//        List<PgSearchBook> list = new ArrayList<>();
//        if(netList != null && netList.length != 0){
//            for (NetSearchBookData net : netList ) {
//                PgSearchBook pg = getPgByNet(net);
//                list.add(pg);
//            }
//        }
//        return list;
//    }
    public static List<PgSelectSearchBook> getPgListByList(NetSelectSearchBookData[] netList){
        List<PgSelectSearchBook> list = new ArrayList<>();
        if(netList != null && netList.length != 0){
            for(NetSelectSearchBookData net : netList){
                PgSelectSearchBook pg = getPgByNet(net);
                list.add(pg);
            }
        }
        return list;
    }

    @Override
    public String toString() {
        return "PgSelectSearchBook{" +
                "goodsId=" + goodsId +
                ", thumb='" + thumb + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", bookType=" + bookType +
                ", selectType=" + selectType +
                ", type=" + type +
                ", pressprice=" + pressprice +
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

    public int getSelectType() {
        return selectType;
    }

    public void setSelectType(int selectType) {
        this.selectType = selectType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getPressprice() {
        return pressprice;
    }

    public void setPressprice(float pressprice) {
        this.pressprice = pressprice;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
