package com.digital.dl2.business.database.obj;

/**
 * Created by digital.dl2 on 15/9/23.
 */
public class DbBookshelfEntity {
    private int id;
    private int entityId;
    private String frontCover;
    private String name;
    private String author;
    private String introduction;
    private long lastReadTime;
    private String downloadUrl;
    private long borrowedTime;
    private long fileSize;
    private String localUrl;
    private int unzipState;
    private int source;
    private int type;
    private String group;//分组
    private int status;

    @Override
    public String toString() {
        return "DbBookshelfEntity{" +
                "id=" + id +
                ", entityId=" + entityId +
                ", frontCover='" + frontCover + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", introduction='" + introduction + '\'' +
                ", lastReadTime=" + lastReadTime +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", borrowedTime=" + borrowedTime +
                ", fileSize=" + fileSize +
                ", localUrl='" + localUrl + '\'' +
                ", unzipState=" + unzipState +
                ", source=" + source +
                ", type=" + type +
                ", group='" + group + '\'' +
                ", status=" + status +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public long getBorrowedTime() {
        return borrowedTime;
    }

    public void setBorrowedTime(long borrowedTime) {
        this.borrowedTime = borrowedTime;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}


