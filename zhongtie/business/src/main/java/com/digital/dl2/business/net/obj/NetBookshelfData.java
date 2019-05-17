package com.digital.dl2.business.net.obj;

/**
 * Created by digital.dl2 on 15/9/25.
 */
public class NetBookshelfData {
    private int goods_id;
    private String thumb;
    private String title;
    private String author;
    private String description;
    private long last_read_time;
    private long borrow_time;
    private String download_url;
    private String local_url;
    private int source;
    private int type;

    @Override
    public String toString() {
        return "NetBookshelfData{" +
                "goods_id=" + goods_id +
                ", thumb='" + thumb + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", last_read_time=" + last_read_time +
                ", borrow_time=" + borrow_time +
                ", download_url='" + download_url + '\'' +
                ", local_url='" + local_url + '\'' +
                ", source=" + source +
                ", type=" + type +
                '}';
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLast_read_time() {
        return last_read_time;
    }

    public void setLast_read_time(long last_read_time) {
        this.last_read_time = last_read_time;
    }

    public long getBorrow_time() {
        return borrow_time;
    }

    public void setBorrow_time(long borrow_time) {
        this.borrow_time = borrow_time;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getLocal_url() {
        return local_url;
    }

    public void setLocal_url(String local_url) {
        this.local_url = local_url;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }
}
