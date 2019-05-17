package com.digital.dl2.business.core.obj;

import java.io.File;

/**
 * Created by digital.dl2 on 15/9/26.
 */
public class PgUploadResourceDetail {
    private String title;
    private String author;
    private int org_id;
    private String description;
    private float price;
    private String thumb;
    private int scope;
    private File file;

    @Override
    public String toString() {
        return "PgUploadResourceDetail{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", org_id=" + org_id +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", thumb='" + thumb + '\'' +
                ", scope=" + scope +
                ", file=" + file +
                '}';
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

    public int getOrg_id() {
        return org_id;
    }

    public void setOrg_id(int org_id) {
        this.org_id = org_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
