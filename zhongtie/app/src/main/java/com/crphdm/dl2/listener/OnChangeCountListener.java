package com.crphdm.dl2.listener;

/**
 * Modify by songzixuan on 19/07/04.
 *计数变更监听器
 */
public interface OnChangeCountListener {
    void onAdd(int id,int type);
    void onReduce(int id,int type);
    void onDelete(int id,int type,int number);
}
