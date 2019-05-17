package com.digital.dl2.business.net.obj;

/**
 * Created by Administrator on 2016/5/7.
 */
public class NetSelectBookCategoryData {
    private int typeid;
    private String name;
    private int number;

    @Override
    public String toString() {
        return "NetSelectBookCategoryData{" +
                "typeid=" + typeid +
                ", name='" + name + '\'' +
                ", number=" + number +
                '}';
    }

    public int getTypeid() {
        return typeid;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
