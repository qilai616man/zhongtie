package com.digital.dl2.business.core.manager;

import android.content.Context;

import com.digital.dl2.business.core.module.SelectModule;
import com.digital.dl2.business.core.obj.PgBookForLibraryListEntity;
import com.digital.dl2.business.core.obj.PgSelectBookCategory;
import com.digital.dl2.business.core.obj.PgSelectSource;
import com.digital.dl2.business.net.obj.NetSelectSource;

import java.util.List;

import rx.Observable;

/**
 * Created by Administrator on 2016/5/6.
 */
public abstract class SelectManager {
    private static SelectManager mSelectManager;
    public static SelectManager getInstance(){
        if (mSelectManager == null){
            throw new NullPointerException("SelectManager is not init");
        }
        return mSelectManager;
    }

    //初始化
    public static SelectManager init(Context context){
        if(mSelectManager == null)
            mSelectManager = new SelectModule(context);
        return mSelectManager;
    }

    //获取图书数量
    public abstract Observable<NetSelectSource> getSelectBookNumber(int user_id,String login_token, int selectType, int page, int page_size);
    //获取图书列表
    public abstract Observable<List<PgSelectSource>> getSelectSourceBookList(int user_id, String login_token, int selectType,  int page, int page_size);
    //获取分类列表
    public abstract Observable<List<PgSelectBookCategory>> getSelectBookCategoryById(int userId,String token,int selectType,int catId, int parentId);
    //获取子分类列表
    public abstract Observable<List<PgBookForLibraryListEntity>> getSelectBookSubCategoryByType(int userId,String token,int isBook,int type,int catId,int page,int pageSize);
}




