package com.digital.dl2.business.net.obj;

/**
 * Created by Administrator on 2016/5/6.
 */
public class NetSelectSourceData {

    private int goods_id;
    private String title;
    private String author;
    private String thumb;
    private String description;
    private int type;
    private int is_resource;
    private float pressprice;
    private float price;
    private String download_url;

    @Override
    public String toString() {
        return "NetSelectSourceData{" +
                "goods_id=" + goods_id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", thumb='" + thumb + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", is_resource=" + is_resource +
                ", pressprice=" + pressprice +
                ", price=" + price +
                ", download_url=" + download_url +
                '}';
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
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

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
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

    public int getIs_resource() {
        return is_resource;
    }

    public void setIs_resource(int is_resource) {
        this.is_resource = is_resource;
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

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }
}
