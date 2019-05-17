package com.digital.dl2.business.core.manager;

import android.content.Context;

import com.digital.dl2.business.core.module.CloudBookstoreModule;
import com.digital.dl2.business.core.obj.PgBookCategory;
import com.digital.dl2.business.core.obj.PgBookForBookstoreDetail;
import com.digital.dl2.business.core.obj.PgBookForCloudMarket;
import com.digital.dl2.business.core.obj.PgBookForLibraryListEntity;
import com.digital.dl2.business.core.obj.PgResult;

import java.util.List;

import rx.Observable;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public abstract class CloudBookstoreManager {

    private static CloudBookstoreManager mCloudBookstoreManager;

    public static CloudBookstoreManager getInstance() {
        if (mCloudBookstoreManager == null)
            throw new NullPointerException("CloudBookstoreManager is not init");
        return mCloudBookstoreManager;
    }

    //初始化1
    public static CloudBookstoreManager init(Context context) {
        if (mCloudBookstoreManager == null)
            mCloudBookstoreManager = new CloudBookstoreModule(context);
        return mCloudBookstoreManager;
    }

    //获取云商城图书列表分类
//    public abstract Observable<List<PgSpreadTag>> getSpreadTagByType(int type, int offset, int count);

    //获取推广图书列表
    public abstract Observable<List<PgBookForCloudMarket>> getBookForLibraryListEntityByPromotionTagId(int type,String token);

    //获取分类列表
    public abstract Observable<List<PgBookCategory>> getBookCategoryById(int userId,String token,int type,int catId, int parentId);

//    获取子分类列表
    public abstract Observable<List<PgBookForLibraryListEntity>> getBookSubCategoryByType(int userId,String token,int isBook,int type,int catId,int page,int pageSize);

    //获取分类图书列表
    public abstract Observable<List<PgBookForLibraryListEntity>> getBookForLibraryListEntityByCategoryId(int userId, String token, int tagId,int type, int page, int pageSize);

    //获取图书详情
    public abstract Observable<List<PgBookForBookstoreDetail>> getBookDetail(int userId,String token,int type,int bookId);

    //收藏图书
    public abstract Observable<Boolean> collectBookById(int userId,String token,int bookId);

    //取消收藏
    public abstract Observable<Boolean> cancelCollectBookById(int userId,String token,int bookId);

    //购买图书
    public abstract Observable<Boolean> buyBookById(int userId,String token,int is_st,int goods_id,int addr_id);

    //加入采选单
    public abstract Observable<PgResult> addMiningListById(int orgId,int userId,String token,int goodsId,int type,int num);

    //推荐采选
    public abstract Observable<Boolean> recommendMiningById(int userId,String token,int orgId,int goodsId);


}
