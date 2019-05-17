package com.digital.dl2.business.core.obj;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgBookTypeEntity {
    private int type;//图书类型（1，实体书，2：矢量图PDF，3：代码PDF，4，双层PDF，5：XML，6：POD）
    private float price;//价格
    private float pressPrice;//定价
    private float podPrice;;//POD价格（type＝6时使用）
    private String downloadUrl;//下载链接

    @Override
    public String toString() {
        return "PgBookTypeEntity{" +
                "type=" + type +
                ", price=" + price +
                ", pressPrice=" + pressPrice +
                ", podPrice=" + podPrice +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getPressPrice() {
        return pressPrice;
    }

    public void setPressPrice(float pressPrice) {
        this.pressPrice = pressPrice;
    }

    public float getPodPrice() {
        return podPrice;
    }

    public void setPodPrice(float podPrice) {
        this.podPrice = podPrice;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
