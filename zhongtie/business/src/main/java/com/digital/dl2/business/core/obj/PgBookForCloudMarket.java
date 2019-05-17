package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetBookForCloudMarketData;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qinqi on 15/11/21.
 */
public class PgBookForCloudMarket {
    private String type_name;
    private int goods_id;
    private String thumb;
    private String title;
    private String author;
    private String description;
    private int type;//1.实体 2.电子书 3.实体和电子书
    private float pressprice;
    private float price;
    private int tag_id;
    private boolean isAddMiningList;

    public static List<PgBookForCloudMarket> getPgListByNetList(NetBookForCloudMarketData[] netList){
        List<PgBookForCloudMarket> pgList = new ArrayList<>();

        for(NetBookForCloudMarketData net : netList){
            Ln.d("PgBookForLibraryListEntity:net：" + net.toString());
            PgBookForCloudMarket pg = new PgBookForCloudMarket();

            pg.setType_name(net.getType_name());
            pg.setGoods_id(net.getGoods_id());
            pg.setThumb(net.getThumb());
            pg.setTitle(net.getTitle());
            pg.setAuthor(net.getAuthor());
            pg.setDescription(net.getDescription());
            pg.setType(net.getType());
            pg.setPressprice(net.getPressprice());
            pg.setPrice(net.getPrice());
            pg.setTag_id(net.getTag_id());

            pgList.add(pg);

        }
        return pgList;
    }

    @Override
    public String toString() {
        return "PgBookForCloudMarket{" +
                "type_name='" + type_name + '\'' +
                ", goods_id=" + goods_id +
                ", thumb='" + thumb + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", pressprice=" + pressprice +
                ", price=" + price +
                ", tag_id=" + tag_id +
                ", isAddMiningList=" + isAddMiningList +
                '}';
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
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

    public int getTag_id() {
        return tag_id;
    }

    public void setTag_id(int tag_id) {
        this.tag_id = tag_id;
    }

    public boolean isAddMiningList() {
        return isAddMiningList;
    }

    public void setAddMiningList(boolean addMiningList) {
        isAddMiningList = addMiningList;
    }
}
