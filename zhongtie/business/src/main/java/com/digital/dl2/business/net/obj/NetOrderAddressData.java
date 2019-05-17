package com.digital.dl2.business.net.obj;

/**
 * Created by qinqi on 15/12/11.
 */
public class NetOrderAddressData {
    private int address_id;
    private String address_name;
    private String address_detail;
    private String address_phone;

    @Override
    public String toString() {
        return "NetOrderAddressData{" +
                "address_id=" + address_id +
                ", address_name='" + address_name + '\'' +
                ", address_phone='" + address_phone + '\'' +
                ", address_detail='" + address_detail + '\'' +
                '}';
    }

    public int getAddress_id() {
        return address_id;
    }

    public void setAddress_id(int address_id) {
        this.address_id = address_id;
    }

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public String getAddress_phone() {
        return address_phone;
    }

    public void setAddress_phone(String address_phone) {
        this.address_phone = address_phone;
    }

    public String getAddress_detail() {
        return address_detail;
    }

    public void setAddress_detail(String address_detail) {
        this.address_detail = address_detail;
    }
}
