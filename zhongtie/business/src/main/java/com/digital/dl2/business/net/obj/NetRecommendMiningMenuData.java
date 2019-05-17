package com.digital.dl2.business.net.obj;

/**
 * Created by digital.dl2 on 15/10/3.
 */
public class NetRecommendMiningMenuData {
    private int id;//ID
    private int user_id;//推荐者ID
    private int org_id;//机构ID
    private int goods_id;//资源ID
    private String name;//资源名称
    private String author;//资源作者
    private String front_cover;//资源图片url
    private long add_time;//添加时间
    private long update_time;//更新时间
    private String user_name;//用户真实姓名

    private float pressprice;//原价
    private float price;//优惠后的价格
    private int goods_num;//采选数量

    @Override
    public String toString() {
        return "NetRecommendMiningMenuData{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", user_name='" + user_name + '\'' +
                ", org_id=" + org_id +
                ", goods_id=" + goods_id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", front_cover='" + front_cover + '\'' +
                ", add_time=" + add_time +
                ", update_time=" + update_time +
                ", market_price=" + pressprice +
                ", price=" + price +
                ", goods_num=" + goods_num +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getOrg_id() {
        return org_id;
    }

    public void setOrg_id(int org_id) {
        this.org_id = org_id;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFront_cover() {
        return front_cover;
    }

    public void setFront_cover(String front_cover) {
        this.front_cover = front_cover;
    }

    public long getAdd_time() {
        return add_time;
    }

    public void setAdd_time(long add_time) {
        this.add_time = add_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public float getPressprice() {
        return pressprice;
    }

    public void setPressprice(float pressprice) {
        this.pressprice = pressprice;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getGoods_num() {
        return goods_num;
    }

    public void setGoods_num(int goods_num) {
        this.goods_num = goods_num;
    }
}
