package com.jiuwei.upgrade_package_new.lib.obj;

/**
 * Created by qinqi on 15/12/21.
 */
public class KbAppResult {
    private String name;
    private String brief;
    private String description;
    private long publishData;
    private int fileSize;
    private String versionDescription;
    private String versionName;
    private int versionCode;
    private String packageUrl;

    @Override
    public String toString() {
        return "KbAppResult{" +
                "name='" + name + '\'' +
                ", brief='" + brief + '\'' +
                ", description='" + description + '\'' +
                ", publishData=" + publishData +
                ", fileSize=" + fileSize +
                ", versionDescription='" + versionDescription + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", packageUrl='" + packageUrl + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPublishData() {
        return publishData;
    }

    public void setPublishData(long publishData) {
        this.publishData = publishData;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getPackageUrl() {
        return packageUrl;
    }

    public void setPackageUrl(String packageUrl) {
        this.packageUrl = packageUrl;
    }
}
