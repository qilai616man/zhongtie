package com.digital.dl2.business.core.obj;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgSpreadTag {
    private int entityId;//标签ID
    private String name ;//标签名字
    private PgBookForLibraryListEntity book;//标签内容


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

    public PgBookForLibraryListEntity getBook() {
        return book;
    }

    public void setBook(PgBookForLibraryListEntity book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return "PgSpreadTag{" +
                "entityId='" + entityId + '\'' +
                ", name='" + name + '\'' +
                ", book=" + book +
                '}';
    }
}
