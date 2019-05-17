package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetBookCategoryData;
import com.digital.dl2.business.net.obj.NetOrderListBookData;
import com.digital.dl2.business.net.obj.NetSelectBookCategoryData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgSelectBookCategory {
    private int categoryId;//类别ID
    private String categoryName;//类别名字
    private int categoryNumber;//数量

    public static List<PgSelectBookCategory> getPgListByNetList(NetSelectBookCategoryData[] netList) {
        List<PgSelectBookCategory> pgList = new ArrayList<>();

        for (NetSelectBookCategoryData net : netList) {
            PgSelectBookCategory pg = new PgSelectBookCategory();

            pg.setCategoryId(net.getTypeid());
            pg.setCategoryName(net.getName());
            pg.setCategoryNumber(net.getNumber());

            pgList.add(pg);
        }
        return pgList;
    }


    public static List<PgSelectBookCategory> getPgListByNetList(NetOrderListBookData[] netList) {
        List<PgSelectBookCategory> pgList = new ArrayList<>();

        for (NetOrderListBookData net : netList) {
            PgSelectBookCategory pg = new PgSelectBookCategory();

            pg.setCategoryName(net.getGoods_name());
            pg.setCategoryNumber(net.getNumber());

            pgList.add(pg);
        }
        return pgList;
    }

    @Override
    public String toString() {
        return "PgBookCategory{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", categoryNumber=" + categoryNumber +
                '}';
    }

    public int getCategoryNumber() {
        return categoryNumber;
    }

    public void setCategoryNumber(int categoryNumber) {
        this.categoryNumber = categoryNumber;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
