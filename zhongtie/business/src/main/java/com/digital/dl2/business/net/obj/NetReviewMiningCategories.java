package com.digital.dl2.business.net.obj;

/**
 * Created by qinqi on 15/12/9.
 */
public class NetReviewMiningCategories {
    private String name;
    private int number;

    @Override
    public String toString() {
        return "NetMiningMenuCategories{" +
                "cat_name='" + name + '\'' +
                ", number=" + number +
                '}';
    }

    public String getCat_name() {
        return name;
    }

    public void setCat_name(String cat_name) {
        this.name = cat_name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}
