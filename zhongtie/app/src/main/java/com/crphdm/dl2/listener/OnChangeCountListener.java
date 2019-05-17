package com.crphdm.dl2.listener;

/**
 * Created by sunbaochun on 15/10/8.
 */
public interface OnChangeCountListener {
    void onAdd(int id,int type);
    void onReduce(int id,int type);
    void onDelete(int id,int type,int number);
}
