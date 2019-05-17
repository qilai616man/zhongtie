package com.crphdm.dl2.user.obj.net;


public class NetExhibition {
    private boolean status;

    @Override
    public String toString() {
        return "NetExhibition{" +
                "status=" + status +
                '}';
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
