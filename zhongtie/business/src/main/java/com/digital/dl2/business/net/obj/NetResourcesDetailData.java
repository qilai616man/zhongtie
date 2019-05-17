package com.digital.dl2.business.net.obj;

/**
 * Created by digital.dl2 on 15/9/28.
 */
public class NetResourcesDetailData {
    private int goods_id;//资源ID
    private int type;//资源类型
    private int scope;//上传范围
    private String thumb;
    private String title;
    private String author;
    private boolean isBuy;
    private String description;
    private String orgName;//机构名称
    private int orgId;//机构ID
    private float price;
    private String downloadUrl;
    private long fileSize;
    private long upload_time;

    @Override
    public String toString() {
        return "NetResourcesDetailData{" +
                "goods_id=" + goods_id +
                ", type=" + type +
                ", scope=" + scope +
                ", thumb='" + thumb + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isBuy=" + isBuy +
                ", description='" + description + '\'' +
                ", orgName='" + orgName + '\'' +
                ", orgId=" + orgId +
                ", price=" + price +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", fileSize=" + fileSize +
                ", upload_time=" + upload_time +
                '}';
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
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

    public long getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(long upload_time) {
        this.upload_time = upload_time;
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

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
