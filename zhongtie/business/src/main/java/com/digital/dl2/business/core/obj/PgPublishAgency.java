package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetPublishAgencyData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgPublishAgency {
    private int entityId;//机构ID
    private String name;//机构名字
    private String description;//简介
    private String logo;

    public static PgPublishAgency getPgByNet(NetPublishAgencyData net){
        PgPublishAgency pg = new PgPublishAgency();
        pg.setEntityId(net.getOrganizationid());
        pg.setName(net.getName());
        pg.setDescription(net.getDescription());
        pg.setLogo(net.getLogo());
        return pg;
    }

    public static List<PgPublishAgency> getPgListByNetList(NetPublishAgencyData[] netList){
        List<PgPublishAgency> pgList = new ArrayList<>();

        for(NetPublishAgencyData net : netList){
            PgPublishAgency pg = getPgByNet(net);
            pgList.add(pg);
        }

        return pgList;
    }

    @Override
    public String toString() {
        return "PgPublishAgency{" +
                "entityId=" + entityId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", logo='" + logo + '\'' +
                '}';
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
