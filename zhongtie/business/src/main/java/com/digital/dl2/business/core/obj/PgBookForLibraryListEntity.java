package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetBookForLibraryListData;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgBookForLibraryListEntity {
    private int entityId ; //图书ID
    private String frontCover ; //图书封面URL
    private String name ; //图书名字
    private String author ; //图书作者
    private String introduction ; //简介
    private String downloadUrl; //1.图书  0.其他资源
    private float pressPrice;
    private float price;
    private int type ; //图书类型（1：实体，2：电子，3：实体和电子）
    private int isResource;//1.图书  2.资源
    private boolean isAddMiningList;
    private boolean isAddRecommend;

    public static List<PgBookForLibraryListEntity> getPgListByNetList(NetBookForLibraryListData[] netList){
        List<PgBookForLibraryListEntity> pgList = new ArrayList<>();

        Ln.d("PgBookForLibraryListEntity:netList.length：" + netList.length);

        for(NetBookForLibraryListData net : netList){
            Ln.d("PgBookForLibraryListEntity:net：" + net.toString());
            PgBookForLibraryListEntity pg = new PgBookForLibraryListEntity();

            pg.setEntityId(net.getGoods_id());
            pg.setFrontCover(net.getThumb());
            pg.setName(net.getTitle());
            pg.setAuthor(net.getAuthor());
            pg.setIntroduction(net.getDescription());
            pg.setType(net.getType());
            pg.setIsResource(net.getIs_resource());
            pg.setPressPrice(net.getPressprice());
            pg.setPrice(net.getPrice());
            pg.setDownloadUrl(net.getDownload_url());
//            pg.setTypeName(net.getType_name());
//            pg.getOriginalPrice();

            Ln.d("PgBookForLibraryListEntity:pg：" + pg.toString());

            pgList.add(pg);
        }

        Ln.d("PgBookForLibraryListEntity:pgList：" + pgList);

        return pgList;
    }

    @Override
    public String toString() {
        return "PgBookForLibraryListEntity{" +
                "entityId=" + entityId +
                ", frontCover='" + frontCover + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", introduction='" + introduction + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", pressPrice=" + pressPrice +
                ", price=" + price +
                ", type=" + type +
                ", isResource=" + isResource +
                ", isAddMiningList=" + isAddMiningList +
                ", isAddRecommend=" + isAddRecommend +
                '}';
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String getFrontCover() {
        return frontCover;
    }

    public void setFrontCover(String frontCover) {
        this.frontCover = frontCover;
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

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIsResource() {
        return isResource;
    }

    public void setIsResource(int isResource) {
        this.isResource = isResource;
    }

    public float getPressPrice() {
        return pressPrice;
    }

    public void setPressPrice(float pressPrice) {
        this.pressPrice = pressPrice;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public boolean isAddMiningList() {
        return isAddMiningList;
    }

    public void setAddMiningList(boolean addMiningList) {
        isAddMiningList = addMiningList;
    }

    public boolean isAddRecommend() {
        return isAddRecommend;
    }

    public void setIsAddRecommend(boolean isAddRecommend) {
        this.isAddRecommend = isAddRecommend;
    }
}
