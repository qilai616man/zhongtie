package com.digital.dl2.business.core.module;

import android.content.Context;

import com.digital.dl2.business.core.manager.SelectManager;
import com.digital.dl2.business.core.obj.PgBookForLibraryListEntity;
import com.digital.dl2.business.core.obj.PgSelectBookCategory;
import com.digital.dl2.business.core.obj.PgSelectSource;
import com.digital.dl2.business.net.NetHelper;
import com.digital.dl2.business.net.obj.NetBookForLibraryList;
import com.digital.dl2.business.net.obj.NetSelectBookCategory;
import com.digital.dl2.business.net.obj.NetSelectSource;
import com.google.gson.Gson;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * Created by Administrator on 2016/5/6.
 */
public class SelectModule extends SelectManager {
    private Context mContext;
    public SelectModule (Context context){
        mContext = context;
    }

    public Observable<NetSelectSource> getSelectBookNumber(final int userId, final String login_token, final int selectType, final int page, final int page_size){
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=selectingBooks";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(login_token)
                        .append("&selectType=").append(selectType)
                        .append("&page=").append(page)
                        .append("&page_size=").append(page_size)
                        .toString();
                Ln.d("SelectModule:getSelectBookNumber:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<NetSelectSource>>() {
            @Override
            public Observable<NetSelectSource> call(String s) {
                Ln.d("SelectModule:getSelectBookNumber:result:" + s);
                NetSelectSource net = new Gson().fromJson(s, NetSelectSource.class);
                Ln.d("SelectModule:getSelectBookNumber:netLibrary:" + net.toString());
                return Observable.just(net);
            }
        });
    }

    /**
     * @param userId
     * @param login_token
     * @param selectType
     * @param page
     * @param page_size
     * @return
     */
    public Observable<List<PgSelectSource>> getSelectSourceBookList(final int userId, final String login_token, final int selectType, final int page, final int page_size){
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=selectingBooks";
//                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=tagDetail";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(login_token)
                        .append("&selectType=").append(selectType)
//                        .append("&type=").append(type)
//                        .append("&tag_id=").append(cat_id)
                        .append("&page=").append(page)
                        .append("&page_size=").append(page_size)
                        .toString();
                Ln.d("SelectModule:getSelectSourceBookList:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgSelectSource>>>() {
            @Override
            public Observable<List<PgSelectSource>> call(String s) {
                Ln.d("SelectModule:getSelectSourceBookList:result:" + s);
                List<PgSelectSource> list = new ArrayList<PgSelectSource>();
                NetSelectSource net = new Gson().fromJson(s, NetSelectSource.class);

                Ln.d("SelectModule:getSelectSourceBookList:netLibrary:" + net.toString());

                if (net.isStatus() && net.getData() != null) {
                    list = PgSelectSource.getPgListByNetList(net.getData());
                }
                Ln.d("SelectModule:getSelectSourceBookList:list:" + list);
                return Observable.just(list);
            }
        });
    }

    /**
     * @param user_id
     * @param login_token
     * @param selectType
     * @param cat_id
     * @param parent_id
     * @return
     */
    public Observable<List<PgSelectBookCategory>> getSelectBookCategoryById(final int user_id,final String login_token,final int selectType,final int cat_id, final int parent_id){
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=getCategoryOfSelecting";
//                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=getCategory";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(user_id)
                        .append("&login_token=").append(login_token)
                        .append("&type=").append(selectType)
                        .append("&cat_id=").append(cat_id)
                        .append("&parent_id=").append(parent_id)
                        .toString();
                Ln.d("SelectModule:getSelectBookCategoryById:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgSelectBookCategory>>>() {
            @Override
            public Observable<List<PgSelectBookCategory>> call(String s) {
                Ln.d("SelectModule:getSelectBookCategoryById:result:" + s);
                List<PgSelectBookCategory> list = new ArrayList<PgSelectBookCategory>();
                NetSelectBookCategory net = new Gson().fromJson(s, NetSelectBookCategory.class);
                Ln.d("SelectModule:getSelectBookCategoryById:netLibrary:" + net.toString());
                if(net.isStatus() && net.getData() != null){
                    list = PgSelectBookCategory.getPgListByNetList(net.getData());
                }
                Ln.d("SelectModule:getSelectBookCategoryById:list:" + list);
                return Observable.just(list);
            }
        });
    }

    /**
     * @param userId
     * @param token
     * @param selectType
     * @param type
     * @param catId
     * @param page
     * @param pageSize
     * @return
     */
    public Observable<List<PgBookForLibraryListEntity>> getSelectBookSubCategoryByType(final int userId, final String token, final int selectType, final int type, final int catId, final int page, final int pageSize){
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=catBooksOfSelecting";
//                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=catBooks";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&selectType=").append(selectType)
//                        .append("&isbook=").append(selectType)
                        .append("&type=").append(type)
                        .append("&cat_id=").append(catId)
                        .append("&page=").append(page)
                        .append("&page_size=").append(pageSize)
                        .toString();
                Ln.d("SelectModule:getSelectBookSubCategoryByType:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgBookForLibraryListEntity>>>() {
            @Override
            public Observable<List<PgBookForLibraryListEntity>> call(String s) {
                Ln.d("SelectModule:getSelectBookSubCategoryByType:result:" + s);
                List<PgBookForLibraryListEntity> list = new ArrayList<PgBookForLibraryListEntity>();
                NetBookForLibraryList net = new Gson().fromJson(s, NetBookForLibraryList.class);
                Ln.d("SelectModule:getSelectBookSubCategoryByType:net:" + net.toString());
                if(net.isStatus() && net.getData().length != 0){
                    list = PgBookForLibraryListEntity.getPgListByNetList(net.getData());
                }
                Ln.d("SelectModule:getSelectBookSubCategoryByType:list:" + list);
                return Observable.just(list);
            }
        });
    }

}
