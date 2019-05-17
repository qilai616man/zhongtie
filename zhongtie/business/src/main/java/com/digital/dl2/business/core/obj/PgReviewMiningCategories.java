package com.digital.dl2.business.core.obj;

/**
 * Created by qinqi on 15/12/9.
 */
public class PgReviewMiningCategories {
    private String name;
    private int number;

    @Override
    public String toString() {
        return "NetMiningMenuCategories{" +
                "cat_name='" + name + '\'' +
                ", number=" + number +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String cat_name) {
        this.name = cat_name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
