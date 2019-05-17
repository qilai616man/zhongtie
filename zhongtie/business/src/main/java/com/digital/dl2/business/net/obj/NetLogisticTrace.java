package com.digital.dl2.business.net.obj;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/6/22.
 */
public class NetLogisticTrace {
    private String company;
    private String com;
    private String no;
    private int status;
    private NetLogisticTraceDetail[] list;

    @Override
    public String toString() {
        return "NetLogisticTrace{" +
                ", company='" + company + '\'' +
                ", com='" + com + '\'' +
                ", no='" + no + '\'' +
                ", status=" + status +
                ", list=" + Arrays.toString(list) +
                '}';
    }

    public NetLogisticTraceDetail[] getList() {
        return list;
    }

    public void setList(NetLogisticTraceDetail[] list) {
        this.list = list;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
