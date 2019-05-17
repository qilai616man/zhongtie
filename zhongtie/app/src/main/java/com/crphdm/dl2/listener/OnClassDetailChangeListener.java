package com.crphdm.dl2.listener;

/**
 * Created by sunbaochun on 15/10/8.
 */
public interface OnClassDetailChangeListener {
    void onSelect(int position,int id,int type);
    void onCancel(int position,int id,int type);
    void onRecommend(int id,int position);
}
