package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetBookForBookstoreDetailData;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgBookForBookstoreDetail {
    private int entityId;//图书ID
    private String frontCover;//图书封面URL
    private String name;//图书名字
    private String author;//图书作者
    private float price;
    private float pressPrice;
    private float podPrice;
    private String typeName;
    private boolean isCollect;//图书是否已收藏
    private boolean isBuy;//图书是否已购买
    private String introduction;//图书简介
    private String publishDate;//图书出版时间
    private String bookNumber;//书号
    private String publishingHouse;//图书出版社
    private String[] catalog;//图书目录，数组内容为String
    private List<PgProbationImage> probationImages;//试读，数组内容为ProbationImage对象
    private String downloadUrl;//图书下载地址
    private String qrCode;//图书二维码
    private String localUrl;//图书本地地址
    private boolean isHaveRealBook;
    private PgCloudBookstoreRealBook realBook;
    private int downloadState;//下载状态
    private float downloadProgress;//下载进度

    public static PgBookForBookstoreDetail getPgByNet(NetBookForBookstoreDetailData net) {
        Ln.d("PgBook:net:" + net.toString());
        PgBookForBookstoreDetail pg = new PgBookForBookstoreDetail();
        List<PgProbationImage> pgProbationImageList = new ArrayList<>();

        Ln.d("PgBook:getPgByNet(1)");

        for (String s : net.getTrial()) {
            PgProbationImage pgProbationImage = new PgProbationImage();
            pgProbationImage.setImageUrl(s);
            pgProbationImageList.add(pgProbationImage);
        }

        Ln.d("PgBook:getPgByNet(2)");

        pg.setEntityId(net.getGoods_id());
        pg.setFrontCover(net.getThumb());
        pg.setName(net.getTitle());
        pg.setAuthor(net.getAuthor());
        pg.setTypeName(net.getTypeName());
        pg.setPrice(net.getPrice());
        pg.setPressPrice(net.getPressprice());
        pg.setPodPrice(net.getPodPrice());
        pg.setIsCollect(net.isCollect());
        pg.setIsBuy(net.isBuy());
        pg.setIntroduction(net.getDescription());
        pg.setPublishDate(net.getPubdate());
        pg.setBookNumber(net.getUnbn());
        pg.setPublishingHouse(net.getPublishingHouse());
        pg.setCatalog(net.getCatalog());
        pg.setProbationImages(pgProbationImageList);
        pg.setQrCode(net.getQrCode());
        pg.setDownloadUrl(net.getDownloadUrl());
        pg.setIsHaveRealBook(net.isHaveRealBook());

        Ln.d("PgBook:getPgByNet(3)");

        if (net.getRealBookInfo() != null) {
            Ln.d("PgBook:net.getRealBookInfo():" + net.getRealBookInfo().toString());
            PgCloudBookstoreRealBook realBook = new PgCloudBookstoreRealBook();
            realBook.setBookId(net.getRealBookInfo().getBookId());
            realBook.setPressPrice(net.getRealBookInfo().getPressprice());
            realBook.setPrice(net.getRealBookInfo().getPrice());
            pg.setRealBook(realBook);
            Ln.d("PgBook:realBook:" + realBook.toString());
        }

        Ln.d("PgBook:pg:" + pg.toString());

        return pg;
    }

    public static List<PgBookForBookstoreDetail> getPgListByNetList(NetBookForBookstoreDetailData[] netList) {
        List<PgBookForBookstoreDetail> list = new ArrayList<>();
        if (netList != null && netList.length != 0) {
            for (NetBookForBookstoreDetailData net : netList) {
                PgBookForBookstoreDetail pg = getPgByNet(net);
                list.add(pg);
            }
        }

        return list;
    }

    @Override
    public String toString() {
        return "PgBookForBookstoreDetail{" +
                "entityId=" + entityId +
                ", frontCover='" + frontCover + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", price=" + price +
                ", pressPrice=" + pressPrice +
                ", podPrice=" + podPrice +
                ", isCollect=" + isCollect +
                ", isBuy=" + isBuy +
                ", introduction='" + introduction + '\'' +
                ", publishDate=" + publishDate +
                ", bookNumber='" + bookNumber + '\'' +
                ", publishingHouse='" + publishingHouse + '\'' +
                ", catalog=" + Arrays.toString(catalog) +
                ", probationImages=" + probationImages +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", qrCode='" + qrCode + '\'' +
                ", localUrl='" + localUrl + '\'' +
                ", isHaveRealBook=" + isHaveRealBook +
                ", realBook=" + realBook +
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getPressPrice() {
        return pressPrice;
    }

    public void setPressPrice(float pressPrice) {
        this.pressPrice = pressPrice;
    }

    public float getPodPrice() {
        return podPrice;
    }

    public void setPodPrice(float podPrice) {
        this.podPrice = podPrice;
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

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
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

    public String[] getCatalog() {
        return catalog;
    }

    public void setCatalog(String[] catalog) {
        this.catalog = catalog;
    }

    public List<PgProbationImage> getProbationImages() {
        return probationImages;
    }

    public void setProbationImages(List<PgProbationImage> probationImages) {
        this.probationImages = probationImages;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public boolean isHaveRealBook() {
        return isHaveRealBook;
    }

    public void setIsHaveRealBook(boolean isHaveRealBook) {
        this.isHaveRealBook = isHaveRealBook;
    }

    public PgCloudBookstoreRealBook getRealBook() {
        return realBook;
    }

    public void setRealBook(PgCloudBookstoreRealBook realBook) {
        this.realBook = realBook;
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
