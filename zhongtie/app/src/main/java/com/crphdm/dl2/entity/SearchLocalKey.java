package com.crphdm.dl2.entity;

/**
 * Created by Administrator on 2017/9/21.
 * 搜索本地密钥
 */
public class SearchLocalKey {
    private String name;
    private String count;
    private String[] assKey;
    private String localUrl;
    private String fileUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String[] getAssKey() {
        return assKey;
    }

    public void setAssKey(String[] assKey) {
        this.assKey = assKey;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
