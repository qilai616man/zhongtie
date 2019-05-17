package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetResourcesDetailData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgResourcesListEntity {
    private int entityId;//资源ID
    private String name;//资源名字
    private String author;//资源作者
    private String introduction;//资源简介
    private String frontCover;//资源封面

    public static List<PgResourcesListEntity> getPgListByNetList(NetResourcesDetailData[] netList){
        List<PgResourcesListEntity> pgList = new ArrayList<>();

        for(NetResourcesDetailData net : netList){
            PgResourcesListEntity pg = new PgResourcesListEntity();

            pg.setEntityId(net.getGoods_id());
            pg.setName(net.getTitle());
            pg.setAuthor(net.getAuthor());
            pg.setIntroduction(net.getDescription());
            pg.setFrontCover(net.getThumb());

            pgList.add(pg);
        }

        return pgList;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
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

    public String getFrontCover() {
        return frontCover;
    }

    public void setFrontCover(String frontCover) {
        this.frontCover = frontCover;
    }

    @Override
    public String toString() {
        return "PgResourcesListEntity{" +
                "entityId='" + entityId + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", introduction='" + introduction + '\'' +
                ", frontCover='" + frontCover + '\'' +
                '}';
    }
}
