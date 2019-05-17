package com.digital.dl2.business.net.obj;

/**
 * Created by qinqi on 15/11/30.
 */
public class NetAdvertisementData {
    private String url;
    private int goods_id;
    private int type;
    private String link_url;

    @Override
    public String toString() {
        return "NetAdvertisementData{" +
                "url='" + url + '\'' +
                ", goods_id=" + goods_id +
                ", type=" + type +
                ", link_url='" + link_url + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
    }
}
