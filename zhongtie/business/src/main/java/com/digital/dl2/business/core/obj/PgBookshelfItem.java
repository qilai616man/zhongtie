package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.database.obj.DbBookshelfEntity;
import com.digital.dl2.business.net.obj.NetBookshelfData;
import com.digital.dl2.business.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgBookshelfItem {
    private int entityId;//图书ID
    private String frontCover;//图书封面URL
    private String name;//图书名字
    private String author;//图书作者
    private String introduction;//图书简介
    private long lastReadTime;//上次阅读时间
    private long borrowTime;//借阅时间
    private String downloadUrl;//图书下载地址
    private long fileSize;//文件大小
    private String localUrl;//图书本地地址
    private int unzipState;
    private int source;//图书来源（1：购买，2：借阅）
    private int type;
    private int downloadState;
    private float downloadProgress;
    private String group;

    @Override
    public String toString() {
        return "PgBookshelfItem{" +
                "entityId=" + entityId +
                ", frontCover='" + frontCover + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", introduction='" + introduction + '\'' +
                ", lastReadTime=" + lastReadTime +
                ", borrowTime=" + borrowTime +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", fileSize=" + fileSize +
                ", localUrl='" + localUrl + '\'' +
                ", unzipState=" + unzipState +
                ", source=" + source +
                ", type=" + type +
                ", downloadState=" + downloadState +
                ", downloadProgress=" + downloadProgress +
                ", group='" + group + '\'' +
                '}';
    }

    public static PgBookshelfItem getPgByDb(DbBookshelfEntity dbBook) {

        if (dbBook != null) {
            PgBookshelfItem pgBook = new PgBookshelfItem();

            pgBook.setEntityId(dbBook.getEntityId());
            pgBook.setFrontCover(dbBook.getFrontCover());
            pgBook.setName(dbBook.getName());
            pgBook.setAuthor(dbBook.getAuthor());
            pgBook.setIntroduction(dbBook.getIntroduction());
            pgBook.setLastReadTime(dbBook.getLastReadTime());
            pgBook.setBorrowTime(dbBook.getBorrowedTime());
            pgBook.setDownloadUrl(dbBook.getDownloadUrl());
            pgBook.setFileSize(dbBook.getFileSize());
            pgBook.setLocalUrl(dbBook.getLocalUrl());
            pgBook.setUnzipState(dbBook.getUnzipState());
            pgBook.setSource(dbBook.getSource());
            pgBook.setType(dbBook.getType());
            pgBook.setGroup(dbBook.getGroup());

            return pgBook;
        }

        return null;
    }

    public static List<PgBookshelfItem> getPgListByDbList(List<DbBookshelfEntity> dbList) {
        List<PgBookshelfItem> list = new ArrayList<>();
        for (DbBookshelfEntity db : dbList) {
            PgBookshelfItem pg = getPgByDb(db);

            list.add(pg);
        }

        return list;
    }

    public static PgBookshelfItem getPgByNet(NetBookshelfData net) {
        PgBookshelfItem pgBook = new PgBookshelfItem();
        pgBook.setEntityId(net.getGoods_id());
        pgBook.setFrontCover(net.getThumb());
        pgBook.setName(net.getTitle());
        pgBook.setAuthor(net.getAuthor());
        pgBook.setIntroduction(net.getDescription());
        pgBook.setLastReadTime(net.getLast_read_time());
        pgBook.setBorrowTime(net.getBorrow_time());
        pgBook.setDownloadUrl(net.getDownload_url());
        pgBook.setLocalUrl(net.getLocal_url());
        pgBook.setSource(net.getSource());
        pgBook.setType(net.getType());
        return pgBook;
    }

    //我的借书列表  存入数据库
    public static List<PgBookshelfItem> getPgListByNetList(NetBookshelfData[] netList) {
        List<PgBookshelfItem> list = new ArrayList<>();
        for (NetBookshelfData net : netList) {
            PgBookshelfItem pgBook = new PgBookshelfItem();

            pgBook.setEntityId(net.getGoods_id());
            pgBook.setFrontCover(net.getThumb());
            pgBook.setName(net.getTitle());
            pgBook.setAuthor(net.getAuthor());
            pgBook.setIntroduction(net.getDescription());
            pgBook.setLastReadTime(net.getLast_read_time());
            pgBook.setBorrowTime(net.getBorrow_time());
            pgBook.setDownloadUrl(net.getDownload_url());

            pgBook.setSource(Constant.BOOK_SOURCE_BORROWED);
            pgBook.setType(net.getType());
            list.add(pgBook);
        }

        return list;
    }

    //我的购买列表 存入数据库
    public static List<PgBookshelfItem> getPgListByLibraryBookList(List<PgBookForLibraryListEntity> libraryBookList) {
        List<PgBookshelfItem> list = new ArrayList<>();
        for (PgBookForLibraryListEntity book : libraryBookList) {
            PgBookshelfItem pgBook = new PgBookshelfItem();

            pgBook.setEntityId(book.getEntityId());
            pgBook.setFrontCover(book.getFrontCover());
            pgBook.setName(book.getName());
            pgBook.setAuthor(book.getAuthor());
            pgBook.setIntroduction(book.getIntroduction());
            pgBook.setDownloadUrl(book.getDownloadUrl());
            pgBook.setSource(Constant.BOOK_SOURCE_BUY);

            list.add(pgBook);
        }

        return list;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public long getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(long borrowTime) {
        this.borrowTime = borrowTime;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public int getUnzipState() {
        return unzipState;
    }

    public void setUnzipState(int unzipState) {
        this.unzipState = unzipState;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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
