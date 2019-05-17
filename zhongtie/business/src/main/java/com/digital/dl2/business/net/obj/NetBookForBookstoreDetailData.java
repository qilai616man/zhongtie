package com.digital.dl2.business.net.obj;

import java.util.Arrays;

/**
 * Created by digital.dl2 on 15/9/28.
 */
public class NetBookForBookstoreDetailData {
    private int goods_id;
    private String thumb;
    private String title;
    private float price;
    private float pressprice;
    private float podPrice;
    private String typeName;
    private String author;
    private boolean isCollect;
    private boolean isBuy;
    private String description;
    private String pubdate;
    private String unbn;
    private String publishingHouse;
    private String[] catalog;
    private String qrCode;
    private String downloadUrl;
    private String[] trial;
    private boolean isHaveRealBook;
    private NetCloudBookstoreBookTypes realBookInfo;

    @Override
    public String toString() {
        return "NetBookForBookstoreDetailData{" +
                "goods_id=" + goods_id +
                ", thumb='" + thumb + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", pressprice=" + pressprice +
                ", podPrice=" + podPrice +
                ", typeName='" + typeName + '\'' +
                ", author='" + author + '\'' +
                ", isCollect=" + isCollect +
                ", isBuy=" + isBuy +
                ", description='" + description + '\'' +
                ", pubdate=" + pubdate +
                ", unbn='" + unbn + '\'' +
                ", publishingHouse='" + publishingHouse + '\'' +
                ", catalog=" + Arrays.toString(catalog) +
                ", qrCode='" + qrCode + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", trial=" + Arrays.toString(trial) +
                ", isHaveRealBook=" + isHaveRealBook +
                ", realBookInfo=" + realBookInfo +
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getPressprice() {
        return pressprice;
    }

    public void setPressprice(float pressprice) {
        this.pressprice = pressprice;
    }

    public float getPodPrice() {
        return podPrice;
    }

    public void setPodPrice(float podPrice) {
        this.podPrice = podPrice;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setIsCollect(boolean isCollect) {
        this.isCollect = isCollect;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setIsBuy(boolean isBuy) {
        this.isBuy = isBuy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getUnbn() {
        return unbn;
    }

    public void setUnbn(String unbn) {
        this.unbn = unbn;
    }

    public String getPublishingHouse() {
        return publishingHouse;
    }

    public void setPublishingHouse(String publishingHouse) {
        this.publishingHouse = publishingHouse;
    }

    public String[] getCatalog() {
        return catalog;
    }

    public void setCatalog(String[] catalog) {
        this.catalog = catalog;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String[] getTrial() {
        return trial;
    }

    public void setTrial(String[] trial) {
        this.trial = trial;
    }

    public boolean isHaveRealBook() {
        return isHaveRealBook;
    }

    public void setIsHaveRealBook(boolean isHaveRealBook) {
        this.isHaveRealBook = isHaveRealBook;
    }

    public NetCloudBookstoreBookTypes getRealBookInfo() {
        return realBookInfo;
    }

    public void setRealBookInfo(NetCloudBookstoreBookTypes realBookInfo) {
        this.realBookInfo = realBookInfo;
    }
}
