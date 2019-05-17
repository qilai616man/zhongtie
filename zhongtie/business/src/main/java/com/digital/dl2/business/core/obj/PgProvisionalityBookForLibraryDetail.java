package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetBookForLibraryDetailData;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/5/18.
 */
public class PgProvisionalityBookForLibraryDetail {
    private int entityId;//图书ID
    private String frontCover;//图书封面URL
    private String name;//图书名字
    private String author;//图书作者
    private String introduction;//图书简介
    private float price;//原价格
    private String publishDate;//图书出版时间
    private String bookNumber;//图书号
    private String publishingHouse;//图书出版社
    private int type;//1.图书  2.自有资源   3.共享资源
    private boolean isCanBorrowing;//是否可以借阅
    private boolean isBorrowing;//是否已借阅
    private boolean isReservations;//是否已预订
    private boolean isCanRenew;//是否可以续借
    private String qrCode;
    private String[] trial;
    private String[] catalog;//图书目录，数组内容为String
    private String downloadUrl;
    private int downloadState;//下载状态
    private float downloadProgress;//下载进度

    public static PgProvisionalityBookForLibraryDetail getPgByNet(NetBookForLibraryDetailData net){
        PgProvisionalityBookForLibraryDetail pg = new PgProvisionalityBookForLibraryDetail();
        pg.setEntityId(net.getGoods_id());
        pg.setFrontCover(net.getThumb());
        pg.setPrice(net.getPrice());
        pg.setName(net.getTitle());
        pg.setAuthor(net.getAuthor());
        pg.setPublishDate(net.getPublish_date());
        pg.setBookNumber(net.getBook_number());
        pg.setPublishingHouse(net.getIssuers());
        pg.setIntroduction(net.getDescription());
        pg.setType(net.getType());
        pg.setIsBorrowing(net.is_borrowing());
        pg.setIsCanBorrowing(net.is_can_borrowing());
        pg.setIsCanRenew(net.is_can_renew());
        pg.setIsReservations(net.is_reservations());
        pg.setQrCode(net.getQr_code());
        pg.setTrial(net.getTrial());
        pg.setCatalog(net.getCatalog());
        pg.setDownloadUrl(net.getDownload_url());

        return pg;

    }

    @Override
    public String toString() {
        return "PgBookForLibraryDetail{" +
                "entityId=" + entityId +
                ", frontCover='" + frontCover + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", introduction='" + introduction + '\'' +
                ", price=" + price +
                ", publishDate=" + publishDate +
                ", bookNumber='" + bookNumber + '\'' +
                ", publishingHouse='" + publishingHouse + '\'' +
                ", type=" + type +
                ", isCanBorrowing=" + isCanBorrowing +
                ", isBorrowing=" + isBorrowing +
                ", isReservations=" + isReservations +
                ", isCanRenew=" + isCanRenew +
                ", qrCode='" + qrCode + '\'' +
                ", trial=" + Arrays.toString(trial) +
                ", catalog=" + Arrays.toString(catalog) +
                ", downloadUrl='" + downloadUrl + '\'' +
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
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

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(String bookNumber) {
        this.bookNumber = bookNumber;
    }

    public String getPublishingHouse() {
        return publishingHouse;
    }

    public void setPublishingHouse(String publishingHouse) {
        this.publishingHouse = publishingHouse;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isBorrowing() {
        return isBorrowing;
    }

    public void setIsBorrowing(boolean isBorrowing) {
        this.isBorrowing = isBorrowing;
    }

    public boolean isCanBorrowing() {
        return isCanBorrowing;
    }

    public void setIsCanBorrowing(boolean isCanBorrowing) {
        this.isCanBorrowing = isCanBorrowing;
    }

    public boolean isCanRenew() {
        return isCanRenew;
    }

    public void setIsCanRenew(boolean isCanRenew) {
        this.isCanRenew = isCanRenew;
    }

    public boolean isReservations() {
        return isReservations;
    }

    public void setIsReservations(boolean isReservations) {
        this.isReservations = isReservations;
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

    public String[] getTrial() {
        return trial;
    }

    public void setTrial(String[] trial) {
        this.trial = trial;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public float getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(float downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

}
