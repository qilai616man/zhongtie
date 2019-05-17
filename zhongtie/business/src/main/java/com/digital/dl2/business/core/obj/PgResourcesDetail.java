package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetResourcesDetailData;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgResourcesDetail {
    private int entityId;//图书ID
    private int scope;//上传范围（1：本机构，2：全部机构）
    private int type;//资源类型？
    private boolean isBuy;//是否已购买
    private String downloadUrl;//下载链接
    private String name;//资源名字
    private String author;//资源作者
    private long uploadTime;//上传时间
    private String agencyName;//所属机构名称
    private int agencyId;//所属机构ID
    private String introduction;//资源简介
    private float price;//资源定价
    private String frontCover;//资源封面
    private int downloadState;//下载状态
    private float downloadProgress;//下载进度

    public static PgResourcesDetail getPgByNet(NetResourcesDetailData net) {
        PgResourcesDetail pg = new PgResourcesDetail();
        pg.setEntityId(net.getGoods_id());
        pg.setScope(net.getScope());
        pg.setType(net.getType());
        pg.setIsBuy(net.isBuy());
        pg.setDownloadUrl(net.getDownloadUrl());
        pg.setName(net.getTitle());
        pg.setAuthor(net.getAuthor());
        pg.setUploadTime(net.getUpload_time());
        pg.setAgencyName(net.getOrgName());
        pg.setAgencyId(net.getOrgId());
        pg.setIntroduction(net.getDescription());
        pg.setPrice(net.getPrice());
        pg.setFrontCover(net.getThumb());

        return pg;
    }


    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setIsBuy(boolean isBuy) {
        this.isBuy = isBuy;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
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

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public int getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(int agencyId) {
        this.agencyId = agencyId;
    }

    public String getAgencyName() {

        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getFrontCover() {
        return frontCover;
    }

    public void setFrontCover(String frontCover) {
        this.frontCover = frontCover;
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

    @Override
    public String toString() {
        return "PgResourcesDetail{" +
                "entityId=" + entityId +
                ", scope=" + scope +
                ", type=" + type +
                ", isBuy=" + isBuy +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", uploadTime='" + uploadTime + '\'' +
                ", agencyName='" + agencyName + '\'' +
                ", agencyId=" + agencyId +
                ", introduction='" + introduction + '\'' +
                ", price=" + price +
                ", frontCover='" + frontCover + '\'' +
                '}';
    }
}
