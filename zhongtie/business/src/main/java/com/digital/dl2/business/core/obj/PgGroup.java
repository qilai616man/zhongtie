package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.database.obj.DbGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/10/14.
 */
public class PgGroup {
    private int id;
    private String name;

    public static PgGroup getPgByDb(DbGroup db){
        PgGroup pg = new PgGroup();
        pg.setId(db.getId());
        pg.setName(db.getName());

        return pg;
    }

    public static List<PgGroup> getPgListByDbList(List<DbGroup> dbList){
        List<PgGroup> list = new ArrayList<>();

        for(DbGroup db : dbList){
            PgGroup pg = getPgByDb(db);
            list.add(pg);
        }

        return list;
    }

    @Override
    public String toString() {
        return "PgGroup{" +
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
