package com.digital.dl2.business.net.obj;

/**
 * Created by digital.dl2 on 15/9/25.
 */
public class NetLibrary {
    private int org_id;
    private String logo;
    private String name;
    private String description;

    @Override
    public String toString() {
        return "NetLibrary{" +
                "org_id=" + org_id +
                ", logo='" + logo + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public int getOrg_id() {
        return org_id;
    }

    public void setOrg_id(int org_id) {
        this.org_id = org_id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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
}
