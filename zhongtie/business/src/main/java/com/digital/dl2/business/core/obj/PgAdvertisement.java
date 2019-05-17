package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetAdvertisementData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgAdvertisement {

    private String imageUrl;//图片Url
    private String linkUrl;//链接地址
    private String slogan;//广告语
    private int type;//1.链接  2.图书
    private int bookId;//图书ID  图书时使用

    public static List<PgAdvertisement> getPgListByNetList(NetAdvertisementData[] netList) {
        List<PgAdvertisement> list = new ArrayList<>();

        if (netList != null && netList.length != 0) {
            for (NetAdvertisementData net : netList) {
                PgAdvertisement pg = new PgAdvertisement();
                pg.setBookId(net.getGoods_id());
                pg.setImageUrl(net.getUrl());
                pg.setLinkUrl(net.getLink_url());
                pg.setType(net.getType());

                list.add(pg);
            }
        }


        return list;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    @Override
    public String toString() {
        return "Advertisement{" +
                "imageUrl='" + imageUrl + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", slogan='" + slogan + '\'' +
                ", type=" + type +
                ", bookId='" + bookId + '\'' +
                '}';
    }
}
