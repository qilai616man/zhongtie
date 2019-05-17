package com.digital.dl2.business.core.manager;

import android.content.Context;

import com.digital.dl2.business.core.module.LibraryModule;
import com.digital.dl2.business.core.obj.PgBookForLibraryDetail;
import com.digital.dl2.business.core.obj.PgBookForLibraryListEntity;
import com.digital.dl2.business.core.obj.PgLibrary;
import com.digital.dl2.business.core.obj.PgResourcesDetail;
import com.digital.dl2.business.core.obj.PgResourcesListEntity;
import com.digital.dl2.business.core.obj.PgUploadResourceDetail;

import java.io.File;
import java.util.List;

import rx.Observable;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public abstract class LibraryManager {

    private static LibraryManager mLibraryManager;

    public static LibraryManager getInstance() {
        if (mLibraryManager == null)
            throw new NullPointerException("LibraryManager is not init");
        return mLibraryManager;
    }

    //初始化1
    public static LibraryManager init(Context context) {
        if (mLibraryManager == null)
            mLibraryManager = new LibraryModule(context);
        return mLibraryManager;
    }

    //获取图书馆列表
    public abstract Observable<List<PgLibrary>> getLibraryList(String token, int page, int page_size);

    //获取图书馆图书列表(一级网络)
    public abstract Observable<List<PgBookForLibraryListEntity>> getBookListToFirst(String token, int orgId, int page, int page_size);

    //获取图书馆图书列表(二级网络)
    public abstract Observable<List<PgBookForLibraryListEntity>> getBookListToSecond(String token, int type, int page, int page_size);

    //获取图书馆图书详情(一级网络)
    public abstract Observable<PgBookForLibraryDetail> getFirstBookDetailById(String token, int bookId, int userId);

    //获取图书馆图书详情(二级网络)
    public abstract Observable<PgBookForLibraryDetail> getSecondBookDetailById(String token, int bookId, int userId, int type);

    //获取资源列表
    public abstract Observable<List<PgResourcesListEntity>> getResourcesListByLibraryId(int user_id, String token, int tag_id, int org_id, int page, int page_size);

    //获取资源详情
    public abstract Observable<PgResourcesDetail> getResourcesDetailById(int userId,String token,int resourceId);

    //借阅图书
    public abstract Observable<Boolean> borrowingBookById(int userId, String token, int bookId);

    //预订图书
    public abstract Observable<Boolean> reservationsBookById(int userId, String token, int entityId, int orgId);

    //上传图片
    public abstract Observable<String> uploadImage(int netType,int userId,String token,File imageFil);

    //上传资源
    public abstract Observable<Boolean> uploadResources(int userId,String token,PgUploadResourceDetail resources);

}
