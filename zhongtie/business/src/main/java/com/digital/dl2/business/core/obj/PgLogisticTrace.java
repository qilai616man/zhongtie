package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetLogisticTraceDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/22.
 */
public class PgLogisticTrace {
    private String datetime;
    private String remark;
    private String zone;

    public static List<PgLogisticTrace> getPgListByNet(NetLogisticTraceDetail[] netList){
        List<PgLogisticTrace> pgList = new ArrayList<>();
        for(NetLogisticTraceDetail net : netList){
            PgLogisticTrace pg = new PgLogisticTrace();
            pg.setDatetime(net.getDatetime());
            pg.setRemark(net.getRemark());
            pg.setZone(net.getZone());
            pgList.add(pg);
        }
        return pgList;

    }

    @Override
    public String toString() {
        return "PgLogisticTrace{" +
                "datetime='" + datetime + '\'' +
                ", remark='" + remark + '\'' +
                ", zone='" + zone + '\'' +
                '}';
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }
}
