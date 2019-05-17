package com.digital.dl2.business.core.manager;

import android.content.Context;

import com.digital.dl2.business.core.module.PublicModule;
import com.digital.dl2.business.core.obj.PgAdvertisement;
import com.digital.dl2.business.core.obj.PgResult;
import com.digital.dl2.business.core.obj.PgSearchBook;
import com.digital.dl2.business.core.obj.PgSelectSearchBook;

import java.util.List;

import rx.Observable;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public abstract class PublicManager {

    private static PublicManager mPublicManager;

    public static PublicManager getInstance() {
        if (mPublicManager == null)
            throw new NullPointerException("PublicManager is not init");
        return mPublicManager;
    }

    //初始化1
    public static PublicManager init(Context context) {
        if (mPublicManager == null)
            mPublicManager = new PublicModule(context);
        return mPublicManager;
    }

    //更新网络连接状态
    public abstract void updateNetworkConnectionStatus();

    //获取当前网络连接状态
    public abstract int getNetworkConnectionStatus();

    //获取广告
    public abstract Observable<List<PgAdvertisement>> getAdvertisementsByPage(int pageType);

    //下载图书
    public abstract Observable<Integer> downloadBookById(String entityId);

    //获取当前用户角色
    public abstract Observable<Integer> getCurrentUseCharacter();

    //获取当前网络级别
    public abstract Observable<Integer> getCurrentNetworkLevel();

    //搜索图书(云书城)
    public abstract Observable<List<PgSearchBook>> searchBook(String token,String keyword,int fromType,int libraryType);

    //采选搜索图书
    public abstract Observable<List<PgSelectSearchBook>> selectSearchBook(int user_id,String token,String keyword,int selectType);

//    //搜索图书(图书馆)
//    public abstract Observable<List<PgSearchBook>> searchBookForLibrary(String token,String keyword);

    //获取下载状态
    public abstract boolean isDownloadOk(String downloadUrl);

    //获取文件路径
    public abstract String getFilePath(String downloadUrl);

    //下载
    public abstract void downloadBook(String downloadUrl);

    public abstract void clearData();

    public abstract Observable<PgResult> test();
}
