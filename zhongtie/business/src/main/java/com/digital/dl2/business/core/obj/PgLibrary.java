package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetLibrary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgLibrary {
    private int libraryId;//图书馆ID
    private String iconUrl;//图书馆图标
    private String name;//图书馆名字
    private String introduction;//图书馆简介

    public static List<PgLibrary> getPgListByNetList(NetLibrary[] netList) {
        List<PgLibrary> pgList = new ArrayList<>();

        for (NetLibrary net : netList) {
            PgLibrary pg = new PgLibrary();

            pg.setLibraryId(net.getOrg_id());
            pg.setIconUrl(net.getLogo());
            pg.setName(net.getName());
            pg.setIntroduction(net.getDescription());

            pgList.add(pg);
        }

        return pgList;

    }

    @Override
    public String toString() {
        return "PgLibrary{" +
                "libraryId='" + libraryId + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", name='" + name + '\'' +
                ", introduction='" + introduction + '\'' +
                '}';
    }

    public int getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(int libraryId) {
        this.libraryId = libraryId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
