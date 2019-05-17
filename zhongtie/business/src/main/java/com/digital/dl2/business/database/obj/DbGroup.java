package com.digital.dl2.business.database.obj;

/**
 * Created by digital.dl2 on 15/10/14.
 */
public class DbGroup {
    private int id;
    private String name;

    @Override
    public String toString() {
        return "DbGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
