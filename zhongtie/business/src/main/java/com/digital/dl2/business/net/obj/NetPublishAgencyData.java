package com.digital.dl2.business.net.obj;

/**
 * Created by digital.dl2 on 15/10/3.
 */
public class NetPublishAgencyData {
    private int organizationid;//机构ID
    private String name;
    private String description;
    private String logo;

    @Override
    public String toString() {
        return "NetPublishAgencyData{" +
                "organizationid=" + organizationid +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }

    public int getOrganizationid() {
        return organizationid;
    }

    public void setOrganizationid(int organizationid) {
        this.organizationid = organizationid;
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
