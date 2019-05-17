package com.digital.dl2.business.net.obj;

/**
 * Created by digital.dl2 on 15/10/8.
 */
public class NetShoppingCartBookData {
    private int goods_id;
    private String thumb;
    private int goods_number;
    private String title;
    private String author;
    private float price;
    private float pressprice;
    private int type;

    @Override
    public String toString() {
        return "NetShoppingCartBookData{" +
                "goods_id=" + goods_id +
                ", thumb='" + thumb + '\'' +
                ", goods_number=" + goods_number +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", price=" + price +
                ", pressprice=" + pressprice +
                ", type=" + type +
                '}';
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
