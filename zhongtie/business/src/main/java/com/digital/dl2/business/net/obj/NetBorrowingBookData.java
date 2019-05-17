package com.digital.dl2.business.net.obj;

/**
 * Created by qinqi on 15/11/24.
 */
public class NetBorrowingBookData {
    private int goods_id;
    private String thumb;
    private String title;
    private String author;
    private String description;
    private long borrow_time;
    private String download_url;

    @Override
    public String toString() {
        return "NetBorrowingBookData{" +
                "goods_id=" + goods_id +
                ", thumb='" + thumb + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", borrow_time=" + borrow_time +
                ", download_url='" + download_url + '\'' +
                '}';
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
}
