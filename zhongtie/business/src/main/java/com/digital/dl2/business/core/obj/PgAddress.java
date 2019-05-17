package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetAddressData;
import com.digital.dl2.business.net.obj.NetOrderAddressData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgAddress {
    private int addressId;//地址ID
    private String name;//收货人名字
    private String phone;//手机号码
    private String address;//详细地址

    public static PgAddress getPgByNet(NetAddressData net){
        PgAddress pg = new PgAddress();
        pg.setAddressId(net.getAddress_id());
        pg.setName(net.getAddress_name());
        pg.setPhone(net.getAddress_phone());
        pg.setAddress(net.getAddress_detail());
        return pg;
    }

    public static PgAddress getPgByNetPayOrder(NetOrderAddressData net){
        PgAddress pg = new PgAddress();
        pg.setAddressId(net.getAddress_id());
        pg.setName(net.getAddress_name());
        pg.setPhone(net.getAddress_phone());
        pg.setAddress(net.getAddress_detail());
        return pg;
    }

    public static List<PgAddress> getPgListByNetList(NetAddressData[] netList){
        List<PgAddress> pgList = new ArrayList<>();

        for(NetAddressData net : netList){
            PgAddress pg = getPgByNet(net);
            pgList.add(pg);
        }

        return pgList;
    }

    @Override
    public String toString() {
        return "PgAddress{" +
                "addressId=" + addressId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public int getAddressId() {

        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
