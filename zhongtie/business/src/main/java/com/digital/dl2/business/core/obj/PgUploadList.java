package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetUploadListData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qinqi on 15/12/18.
 */
public class PgUploadList {
    private int goods_id;
    private String title;
    private String author;
    private String description;
    private String thumb;

    public static List<PgUploadList> getPgListByNetList(NetUploadListData[] netList){
        List<PgUploadList> list = new ArrayList<>();

        for(NetUploadListData net : netList){
            PgUploadList pg = new PgUploadList();
            pg.setGoods_id(net.getGoods_id());
            pg.setTitle(net.getTitle());
            pg.setAuthor(net.getAuthor());
            pg.setDescription(net.getDescription());
            pg.setThumb(net.getThumb());

            list.add(pg);
        }
        return list;
    }

    @Override
    public String toString() {
        return "PgUploadList{" +
                "goods_id=" + goods_id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", thumb='" + thumb + '\'' +
                '}';
    }

    public int getGoods_id() {

        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
