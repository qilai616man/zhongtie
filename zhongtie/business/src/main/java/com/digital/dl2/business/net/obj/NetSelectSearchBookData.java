package com.digital.dl2.business.net.obj;

/**
 * Created by Administrator on 2016/5/6.
 */
public class NetSelectSearchBookData {

    private int goods_id;
    private String thumb;
    private String title;
    private String author;
    private String description;
    private int book_type;
    private int selectType;
    private int type;
    private float pressprice;
    private float price;

    @Override
    public String toString() {
        return "NetSelectSearchBookData{" +
                "goods_id=" + goods_id +
                ", thumb='" + thumb + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", book_type=" + book_type +
                ", selectType=" + selectType +
                ", type=" + type +
                ", pressprice=" + pressprice +
                ", price=" + price +
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

    public int getBook_type() {
        return book_type;
    }

    public void setBook_type(int book_type) {
        this.book_type = book_type;
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
