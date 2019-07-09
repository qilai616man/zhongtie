package com.crphdm.dl2.listener;

/**
 * Modify by songzixuan on 19/07/04.
 * 类详细信息改变监听器
 */
public interface OnClassDetailChangeListener {
    void onSelect(int position,int id,int type);
    void onCancel(int position,int id,int type);
    void onRecommend(int id,int position);
}
