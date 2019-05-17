package com.digital.dl2.business.core.module;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;

import com.digital.dl2.business.core.manager.PublicManager;
import com.digital.dl2.business.core.obj.PgAdvertisement;
import com.digital.dl2.business.core.obj.PgResult;
import com.digital.dl2.business.core.obj.PgSearchBook;
import com.digital.dl2.business.core.obj.PgSelectSearchBook;
import com.digital.dl2.business.database.DatabaseManager;
import com.digital.dl2.business.net.NetHelper;
import com.digital.dl2.business.net.obj.NetAdvertisement;
import com.digital.dl2.business.net.obj.NetResult;
import com.digital.dl2.business.net.obj.NetSearchBook;
import com.digital.dl2.business.net.obj.NetSelectSearchBook;
import com.digital.dl2.business.util.Constant;
import com.google.gson.Gson;
import com.goyourfly.gdownloader.GDownloader;
import com.goyourfly.gdownloader.db.DbDownloadExt;
import com.goyourfly.gdownloader.utils.Ln;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Func0;
import rx.functions.Func1;

//import com.jiuwei.easy_download.GDownloader;
//import com.jiuwei.easy_download.helper.DownloadHelper;

/**
 * Created by digital.dl2 on 15/9/22.
 */
//implements DownloadHelper.DownloadListener
public class PublicModule extends PublicManager {
    private int mNetworkState;
    private Context mContext;

    public PublicModule(Context context) {
        mContext = context;
    }

    /**
     * 更新网络连接状态
     * service使用
     */
    @Override
    public void updateNetworkConnectionStatus() {
        //心跳连接
        mNetworkState = Constant.NETWORK_STATUS_TWO_CENTRE;
    }

    /**
     * 获取当前网络连接状态
     *
     * @return
     */
    @Override
    public int getNetworkConnectionStatus() {
        return mNetworkState;
    }

    /**
     * 下载图书
     *
     * @param entityId
     * @return
     */
    @Override
    public Observable<Integer> downloadBookById(String entityId) {
        return null;
    }

    /**
     * 获取当前用户角色
     *
     * @return
     */
    @Override
    public Observable<Integer> getCurrentUseCharacter() {
        return null;
    }

    /**
     * 获取当前网络级别
     *
     * @return
     */
    @Override
    public Observable<Integer> getCurrentNetworkLevel() {
        return null;
    }

    //采选搜索图书
    public Observable<List<PgSelectSearchBook>> selectSearchBook(final int user_id,final String token, final String keyword, final int selectType) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                StringBuffer stringBuffer = null;
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=searchByNameOfSelecting";
//                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=searchByName";
                stringBuffer = new StringBuffer(url)
                        .append("&user_id=").append(user_id)
                        .append("&login_token=").append(token)
                        .append("&keyword=").append(keyword)
                        .append("&type=").append(selectType)
                        .append("&page=").append(1)
                        .append("&page_size=").append(100);
                Ln.d("PublicModule:selectSearchBook:urlStr:" + stringBuffer.toString());
                return NetHelper.getData(stringBuffer.toString());
            }
        }).flatMap(new Func1<String, Observable<List<PgSelectSearchBook>>>() {
            @Override
            public Observable<List<PgSelectSearchBook>> call(String s) {
                Ln.d("PublicModule:selectSearchBook:url:" + s);
                List<PgSelectSearchBook> list = new ArrayList<>();
                NetSelectSearchBook net = new Gson().fromJson(s, NetSelectSearchBook.class);
                Ln.d("PublicModule:selectSearchBook:net:" + net.toString());
                if (net.getData().length != 0) {
//                    list = PgSearchBook.getPgListByNetList(net.getData());
                    list = PgSelectSearchBook.getPgListByList(net.getData());
                    Ln.d("PublicModule:selectSearchBook:list:" + list);
                }
                return Observable.just(list);
            }
        });
    }
    /**
     * 搜索图书
     *
     * @param token
     * @param keyword
     * @param fromType    1.云书城  2.共享资源 3.图书馆
     * @return
     */
    @Override
    public Observable<List<PgSearchBook>> searchBook(final String token, final String keyword, final int fromType, final int libraryType) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                StringBuffer stringBuilder = null;

                if (fromType == 3) {//从图书馆进入搜索页面
                    String url = "http://ml.crphdm.com/?app=book&controller=forTwo&action=searchByName";
//                    String url = "http://192.168.4.253/?app=book&controller=forTwo&action=searchByName";
                    stringBuilder = new StringBuffer(url)
                            .append("&login_token=").append(token)
                            .append("&keyword=").append(keyword)
                            .append("&type=").append(libraryType)
                            .append("&page=").append(1)
                            .append("&page_size=").append(100);
                } else {// 从云书城和共享资源进搜索页面
                    String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=searchByName";
                    stringBuilder = new StringBuffer(url)
                            .append("&login_token=").append(token)
                            .append("&keyword=").append(keyword)
                            .append("&type=").append(fromType)
                            .append("&page=").append(1)
                            .append("&page_size=").append(100);
                }

//                String urlStr = new StringBuffer(url)
//                        .append("&login_token=").append(token)
//                        .append("&keyword=").append(keyword)
//                        .append("&page=").append(1)
//                        .append("&page_size=").append(100)
//                        .toString();

                Ln.d("PublicModule:searchBook:urlStr:" + stringBuilder.toString());

                return NetHelper.getData(stringBuilder.toString());

            }
        }).flatMap(new Func1<String, Observable<List<PgSearchBook>>>() {
            @Override
            public Observable<List<PgSearchBook>> call(String s) {
                Ln.d("PublicModule:searchBook:url:" + s);
                List<PgSearchBook> list = new ArrayList<>();
                NetSearchBook net = new Gson().fromJson(s, NetSearchBook.class);
                Ln.d("PublicModule:searchBook:net:" + net.toString());

                if (!net.isStatus()) {
                    if (net.getError_code() == Constant.TOKEN_ERROR) {
//                        Exceptions.propagate(new NetworkErrorException(Constant.TOKEN_ERROR_MESSAGE));
                        Intent intent = new Intent();
                        intent.setAction(Constant.ACTION_TOKEN_ERROR);
                        intent.putExtra(Constant.INTENT_ERROR_MESSAGE, net.getMessage());
                        mContext.sendBroadcast(intent);
                    }

                    if (net.getError_code() == Constant.TOKEN_EXPIRED) {
//                        Exceptions.propagate(new NetworkErrorException(Constant.TOKEN_ERROR_MESSAGE));
                        Intent intent = new Intent();
                        intent.setAction(Constant.ACTION_TOKEN_EXPIRED);
                        intent.putExtra(Constant.INTENT_ERROR_MESSAGE, net.getMessage());
                        mContext.sendBroadcast(intent);
                    }

                    Exceptions.propagate(new NetworkErrorException(net.getMessage()));
                }

                if (net.getData().length != 0) {
                    list = PgSearchBook.getPgListByNetList(net.getData());
                    Ln.d("PublicModule:searchBook:list:" + list);
                }

                return Observable.just(list);
            }
        });
    }

//    /**
//     * 搜索图书 （云书城）
//     * @param token
//     * @param keyword
//     * @return
//     */
//    @Override
//    public Observable<List<PgSearchBook>> searchBookForCloudBookstore(String token,String keyword) {
//        return null;
//    }

    /**
     * 判断图书是否已经下载
     *
     * @param downloadUrl
     * @return
     */
    @Override
    public boolean isDownloadOk(String downloadUrl) {
        DbDownloadExt dbDownloadExt = GDownloader.getInstance().getDownloadState(downloadUrl);
        if (dbDownloadExt == null)
            return false;

        if (dbDownloadExt.getDownloadState() != DbDownloadExt.DOWNLOAD_STATE_DOWNLOADED)
            return false;
        File file = GDownloader.getInstance().getFile(downloadUrl);

        if (!file.exists() || file.length() == 0)
            return false;

        return true;

    }

    /**
     * 获取文件路径
     *
     * @param downloadUrl
     * @return
     */
    @Override
    public String getFilePath(String downloadUrl) {
        File file = GDownloader.getInstance().getFile(downloadUrl);
        if (file.exists()) {
            return file.getPath();
        }
        return null;
    }

    /**
     * 下载图书
     *
     * @param downloadUrl
     */
    @Override
    public void downloadBook(String downloadUrl) {
        GDownloader.getInstance().download(downloadUrl);//下载
    }

    /**
     * 获取广告
     *
     * @param pageType
     * @return List<PgAdvertisement>
     */
    @Override
    public Observable<List<PgAdvertisement>> getAdvertisementsByPage(final int pageType) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=getad";
                String urlStr = new StringBuffer(url)
                        .append("&type=").append(pageType)
                        .toString();

                Ln.d("PublicModule:getAdvertisementsByPage:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgAdvertisement>>>() {
            @Override
            public Observable<List<PgAdvertisement>> call(String s) {
                Ln.d("PublicModule:getAdvertisementsByPage:result:" + s);
                List<PgAdvertisement> list = new ArrayList<>();
                NetAdvertisement net = new Gson().fromJson(s, NetAdvertisement.class);
                Ln.d("PublicModule:getAdvertisementsByPage:net:" + net.toString());

//                if (!net.isStatus()) {
//                    if (net.getError_code() == Constant.TOKEN_ERROR) {
////                        Exceptions.propagate(new NetworkErrorException(Constant.TOKEN_ERROR_MESSAGE));
//                        Intent intent = new Intent();
//                        intent.setAction(Constant.ACTION_TOKEN_ERROR);
//                        intent.putExtra(Constant.INTENT_ERROR_MESSAGE, net.getMessage());
//                        mContext.sendBroadcast(intent);
//                    }
//
//                    if (net.getError_code() == Constant.TOKEN_EXPIRED) {
////                        Exceptions.propagate(new NetworkErrorException(Constant.TOKEN_ERROR_MESSAGE));
//                        Intent intent = new Intent();
//                        intent.setAction(Constant.ACTION_TOKEN_EXPIRED);
//                        intent.putExtra(Constant.INTENT_ERROR_MESSAGE, net.getMessage());
//                        mContext.sendBroadcast(intent);
//                    }
//
//                    Exceptions.propagate(new NetworkErrorException(net.getMessage()));
//                }

                if (net.isStatus() && net.getData() != null) {
                    list = PgAdvertisement.getPgListByNetList(net.getData());
                    Ln.d("PublicModule:getAdvertisementsByPage:list:" + list);
                }

                return Observable.just(list);
            }
        });
    }

    public void clearData() {
        DatabaseManager.getInstance().clearDb();
    }

    public Observable<PgResult> test() {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=testToken";

                Ln.d("PublicModule:test:urlStr:" + url);
                return NetHelper.getData(url);
            }
        }).flatMap(new Func1<String, Observable<PgResult>>() {
            @Override
            public Observable<PgResult> call(String s) {
                Ln.d("PublicModule:test:result:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("PublicModule:test:net:" + net.toString());

                if (!net.isStatus()) {
                    if (net.getError_code() == Constant.TOKEN_ERROR) {
//                        Exceptions.propagate(new NetworkErrorException(Constant.TOKEN_ERROR_MESSAGE));
                        Intent intent = new Intent();
                        intent.setAction(Constant.ACTION_TOKEN_ERROR);
                        intent.putExtra(Constant.INTENT_ERROR_MESSAGE, net.getMessage());
                        mContext.sendBroadcast(intent);
                    }

                    if (net.getError_code() == Constant.TOKEN_EXPIRED) {
//                        Exceptions.propagate(new NetworkErrorException(Constant.TOKEN_ERROR_MESSAGE));
                        Intent intent = new Intent();
                        intent.setAction(Constant.ACTION_TOKEN_EXPIRED);
                        intent.putExtra(Constant.INTENT_ERROR_MESSAGE, net.getMessage());
                        mContext.sendBroadcast(intent);
                    }

                    Exceptions.propagate(new NetworkErrorException(net.getMessage()));
                }

                PgResult pg = PgResult.getPgByNet(net);

                Ln.d("PublicModule:test:pg:" + pg.toString());

                return Observable.just(pg);
            }
        });
    }

}
