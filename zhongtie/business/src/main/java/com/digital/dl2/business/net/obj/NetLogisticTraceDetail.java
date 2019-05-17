package com.digital.dl2.business.net.obj;

/**
 * Created by Administrator on 2016/6/22.
 */
public class NetLogisticTraceDetail {
    private String datetime;
    private String remark;
    private String zone;

    @Override
    public String toString() {
        return "NetLogisticTraceDetail{" +
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
