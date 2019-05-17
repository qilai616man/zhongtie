package com.digital.dl2.business.net.obj;

/**
 * Created by digital.dl2 on 15/9/28.
 */
public class NetBookCategoryData {
    private int typeid;
    private String name;
    private int number;

    @Override
    public String toString() {
        return "NetBookCategoryData{" +
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
