package com.digital.dl2.business.net.obj;

/**
 * Created by digital.dl2 on 15/10/12.
 */
public class NetMiningMenuDetailBookItemData {
    private int id;
    private int org_id;
    private int goods_id;
    private String thumb;
    private int goods_number;
    private String title;
    private String author;
    private float pressprice;
    private float price;
    private long add_time;

    @Override
    public String toString() {
        return "NetMiningMenuDetailBookItemData{" +
                "id=" + id +
                ", org_id=" + org_id +
                ", goods_id=" + goods_id +
                ", thumb='" + thumb + '\'' +
                ", goods_number=" + goods_number +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", pressprice=" + pressprice +
                ", price=" + price +
                ", add_time=" + add_time +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrg_id() {
        return org_id;
    }

    public void setOrg_id(int org_id) {
        this.org_id = org_id;
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

    public int getGoods_number() {
        return goods_number;
    }

    public void setGoods_number(int goods_number) {
        this.goods_number = goods_number;
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

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }
}
