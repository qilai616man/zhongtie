package com.crphdm.dl2.user.obj.net;

import com.google.gson.annotations.Expose;

/**
 * Created by gaoyufei on 15/11/9.
 */
public class NetLocationResult extends NetObject{
    public static final String LOCATION_MASTER = "MASTER";
    public static final String LOCATION_SLAVE = "SLAVE";
    @Expose
    String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
