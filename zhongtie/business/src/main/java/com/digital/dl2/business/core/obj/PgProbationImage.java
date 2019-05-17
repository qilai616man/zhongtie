package com.digital.dl2.business.core.obj;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgProbationImage {
    private String imageUrl;//图片Url
    private int imageIndex;//图片索引

    @Override
    public String toString() {
        return "PgProbationImage{" +
                "imageUrl='" + imageUrl + '\'' +
                ", imageIndex=" + imageIndex +
                '}';
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }
}
