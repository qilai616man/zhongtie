package com.digital.dl2.business.core.module;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.digital.dl2.business.core.manager.LibraryManager;
import com.digital.dl2.business.core.obj.PgBookForLibraryDetail;
import com.digital.dl2.business.core.obj.PgBookForLibraryListEntity;
import com.digital.dl2.business.core.obj.PgLibrary;
import com.digital.dl2.business.core.obj.PgResourcesDetail;
import com.digital.dl2.business.core.obj.PgResourcesListEntity;
import com.digital.dl2.business.core.obj.PgUploadResourceDetail;
import com.digital.dl2.business.net.NetHelper;
import com.digital.dl2.business.net.obj.NetBookForLibraryDetailResult;
import com.digital.dl2.business.net.obj.NetBookForLibraryList;
import com.digital.dl2.business.net.obj.NetBorrowingBookList;
import com.digital.dl2.business.net.obj.NetLibraryList;
import com.digital.dl2.business.net.obj.NetLibraryUploadImageUrl;
import com.digital.dl2.business.net.obj.NetResourcesDetail;
import com.digital.dl2.business.net.obj.NetResourcesListEntity;
import com.digital.dl2.business.net.obj.NetResult;
import com.digital.dl2.business.util.Constant;
import com.google.gson.Gson;
import com.goyourfly.gdownloader.utils.Ln;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class LibraryModule extends LibraryManager {
    private Context mContext;

    public LibraryModule(Context context) {
        mContext = context;
    }

    /**
     * 获取图书馆列表 （接口52）
     *
     * @param token
     * @param page
     * @param page_size
     * @return 通过
     */
    @Override
    public Observable<List<PgLibrary>> getLibraryList(final String token, final int page, final int page_size) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=getFirstLibrarys";
                String urlStr = new StringBuffer(url)
                        .append("&login_token=").append(token)
                        .append("&page=").append(page)
                        .append("&page_size=").append(page_size)
                        .toString();
                Ln.d("LibraryModule:getLibraryList:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgLibrary>>>() {
            @Override
            public Observable<List<PgLibrary>> call(String s) {
                Ln.d("LibraryModule:getLibraryList:url:" + s);
                List<PgLibrary> list = new ArrayList<>();
                NetLibraryList net = new Gson().fromJson(s, NetLibraryList.class);

                Ln.d("LibraryModule:getLibraryList:netLibrary:" + net.toString());

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

                if (net.isStatus() && net.getData().length != 0) {
                    list = PgLibrary.getPgListByNetList(net.getData());
                }

                Ln.d("LibraryModule:getLibraryList:list:" + list);

                return Observable.just(list);
            }
        });
    }

    /**
     * 获取图书馆图书列表(一级中心)
     *
     * @param token
     * @param orgId
     * @param page
     * @param page_size
     * @return 通过
     */
    @Override
    public Observable<List<PgBookForLibraryListEntity>> getBookListToFirst(final String token, final int orgId, final int page, final int page_size) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=getOrgBooks";
                String urlStr = new StringBuffer(url)
                        .append("&login_token=").append(token)
                        .append("&org_id=").append(orgId)
                        .append("&page=").append(page)
                        .append("&page_size=").append(page_size)
                        .toString();
                Ln.d("LibraryModule:getBookListToFirst:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgBookForLibraryListEntity>>>() {
            @Override
            public Observable<List<PgBookForLibraryListEntity>> call(String s) {
                Ln.d("LibraryModule:getBookListToFirst:url:" + s);
                List<PgBookForLibraryListEntity> list = new ArrayList<>();

                NetBookForLibraryList net = new Gson().fromJson(s, NetBookForLibraryList.class);
                Ln.d("LibraryModule:getBookListToFirst:net:" + net.toString());

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

                if (net.isStatus() && net.getData().length != 0) {
                    list = PgBookForLibraryListEntity.getPgListByNetList(net.getData());
                }

                Ln.d("LibraryModule:getBookListToFirst:list:" + list);

                return Observable.just(list);
            }
        });
    }

    /**
     * 获取图书馆图书列表(二级中心)
     *
     * @param token
     * @param type      类型（1：图书，2：自有资源，3：共享资源）
     * @param page
     * @param page_size
     * @return 通过
     */
    @Override
    public Observable<List<PgBookForLibraryListEntity>> getBookListToSecond(final String token, final int type, final int page, final int page_size) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://ml.crphdm.com/?app=book&controller=forTwo&action=getOrgBooks";
//                String url = "http://192.168.4.253/?app=book&controller=forTwo&action=getOrgBooks";
                String urlStr = new StringBuffer(url)
                        .append("&login_token=").append(token)
                        .append("&type=").append(type)
                        .append("&page=").append(page)
                        .append("&page_size=").append(page_size)
                        .toString();

                Ln.d("LibraryModule:getBookListToSecond:urlStr:" + urlStr);

                return NetHelper.getData(urlStr);

            }
        }).flatMap(new Func1<String, Observable<List<PgBookForLibraryListEntity>>>() {
            @Override
            public Observable<List<PgBookForLibraryListEntity>> call(String s) {
                Ln.d("LibraryModule:getBookListToSecond:result:" + s);
                List<PgBookForLibraryListEntity> list = new ArrayList<>();

                NetBookForLibraryList net = new Gson().fromJson(s, NetBookForLibraryList.class);

                Ln.d("LibraryModule:getBookListToSecond:net:" + net.toString());

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

                if (net.isStatus() && net.getData().length != 0) {
                    list = PgBookForLibraryListEntity.getPgListByNetList(net.getData());
                    Ln.d("LibraryModule:getBookListToSecond:list:" + list);
                }

                return Observable.just(list);
            }
        });
    }

    /**
     * 获取图书馆图书详情 (一级中心)
     *
     * @param token
     * @param bookId
     * @param userId
     * @return 通过
     */
    @Override
    public Observable<PgBookForLibraryDetail> getFirstBookDetailById(final String token, final int bookId, final int userId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=getBooksDetail";
                String urlStr = new StringBuffer(url)
                        .append("&login_token=").append(token)
                        .append("&book_id=").append(bookId)
                        .append("&user_id=").append(userId)
                        .toString();
                Ln.d("LibraryModule:getFirstBookDetailById:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<PgBookForLibraryDetail>>() {
            @Override
            public Observable<PgBookForLibraryDetail> call(String s) {
                Ln.d("LibraryModule:getFirstBookDetailById:url:" + s);

                NetBookForLibraryDetailResult net = new Gson().fromJson(s, NetBookForLibraryDetailResult.class);
                Ln.d("LibraryModule:getFirstBookDetailById:net:" + net.toString());

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

                if (net.getData() != null) {
                    PgBookForLibraryDetail pg = PgBookForLibraryDetail.getPgByNet(net.getData());
                    Ln.d("LibraryModule:getFirstBookDetailById:pg:" + pg.toString());

                    return Observable.just(pg);
                } else {
                    return null;
                }
            }
        });
    }

    /**
     * 获取图书馆图书详情 (二级中心) （接口56）
     *
     * @param token
     * @param bookId
     * @param userId
     * @return 通过
     */
    @Override
    public Observable<PgBookForLibraryDetail> getSecondBookDetailById(final String token, final int bookId, final int userId, final int type) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://ml.crphdm.com/?app=book&controller=forTwo&action=getBooksDetail";
//                String url = "http://192.168.4.253/?app=book&controller=forTwo&action=getBooksDetail";
                String urlStr = new StringBuffer(url)
                        .append("&book_id=").append(bookId)
                        .append("&login_token=").append(token)
                        .append("&user_id=").append(userId)
                        .append("&type=").append(type)
                        .toString();
                Ln.d("LibraryModule:getSecondBookDetailById:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<PgBookForLibraryDetail>>() {
            @Override
            public Observable<PgBookForLibraryDetail> call(String s) {
                Ln.d("LibraryModule:getSecondBookDetailById:url:" + s);
                NetBookForLibraryDetailResult net = new Gson().fromJson(s, NetBookForLibraryDetailResult.class);
                Ln.d("LibraryModule:getSecondBookDetailById:net:" + net.toString());

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

                PgBookForLibraryDetail pg = null;

                if (net.getData() != null) {
                    pg = PgBookForLibraryDetail.getPgByNet(net.getData());
                    Ln.d("LibraryModule:getSecondBookDetailById:pg:" + pg.toString());
                }

                return Observable.just(pg);
            }
        });
    }

    /**
     * 借阅图书
     *
     * @param userId
     * @param token
     * @param bookId
     * @return 通过
     */
    @Override
    public Observable<Boolean> borrowingBookById(final int userId, final String token, final int bookId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://ml.crphdm.com/?app=book&controller=forTwo&action=borrowBook";
//                String url = "http://192.168.4.253/?app=book&controller=forTwo&action=borrowBook";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&goods_id=").append(bookId)
                        .toString();
                Ln.d("LibraryModule:borrowingBookById:urlStr:" + urlStr);
//
                Log.i("borrowing:NetHelper",NetHelper.getData(urlStr).toString());
           
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("LibraryModule:borrowingBookById:result:" + s);
                Log.i("borrowing:result:" , s);
                NetBorrowingBookList net = new Gson().fromJson(s, NetBorrowingBookList.class);
                Log.i("borrowing:net:" , net.toString());
                Ln.d("LibraryModule:borrowingBookById:net:" + net.toString());
        
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

                if (net.isStatus()) {
                    //借书成功  执行插入数据库方法
                }

                return Observable.just(net.isStatus());
            }
        });
    }

    /**
     * 预订图书
     *
     * @param userId
     * @param token
     * @param entityId
     * @param orgId
     * @return 通过
     */
    @Override
    public Observable<Boolean> reservationsBookById(final int userId, final String token, final int entityId, final int orgId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://ml.crphdm.com/?app=book&controller=forTwo&action=reserveBook";
//                String url = "http://192.168.4.253/?app=book&controller=forTwo&action=reserveBook";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&book_id=").append(entityId)
                        .append("&org_id=").append(orgId)
                        .toString();
                Ln.d("LibraryModule:reservationsBookById:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("LibraryModule:reservationsBookById:result:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("LibraryModule:reservationsBookById:net:" + net.toString());

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

                return Observable.just(net.isStatus());
            }
        });
    }

    /**
     * 获取资源列表（接口50）
     *
     * @param user_id
     * @param token
     * @param tag_id    0:全部,1:热门;2:最新,
     * @param org_id    机构ID,没有选中机构就是0,
     * @param page
     * @param page_size
     * @return
     */
    @Override
    public Observable<List<PgResourcesListEntity>> getResourcesListByLibraryId(final int user_id, final String token, final int tag_id, final int org_id, final int page, final int page_size) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=getShare";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(user_id)
                        .append("&login_token=").append(token)
                        .append("&tag_id=").append(tag_id)
                        .append("&org_id=").append(org_id)
                        .append("&page=").append(page)
                        .append("&page_size=").append(page_size).toString();

                Ln.d("LibraryModule:getResourcesListByLibraryId:urlStr:" + urlStr);

                return NetHelper.getData(urlStr);

            }
        }).flatMap(new Func1<String, Observable<List<PgResourcesListEntity>>>() {
            @Override
            public Observable<List<PgResourcesListEntity>> call(String s) {
                Ln.d("LibraryModule:getResourcesListByLibraryId:result:" + s);
                List<PgResourcesListEntity> list = new ArrayList<>();

                NetResourcesListEntity net = new Gson().fromJson(s, NetResourcesListEntity.class);

                Ln.d("LibraryModule:getResourcesListByLibraryId:net:" + net.toString());

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
//
                if (net.isStatus() && net.getData().length != 0) {
                    list = PgResourcesListEntity.getPgListByNetList(net.getData());
                }
                Ln.d("LibraryModule:getResourcesListByLibraryId:list:" + list.size());

                return Observable.just(list);
            }
        });
    }

    /**
     * 获取资源详情 （接口51）
     *
     * @param userId
     * @param token
     * @param resourceId
     * @return
     */
    @Override
    public Observable<PgResourcesDetail> getResourcesDetailById(final int userId, final String token, final int resourceId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=getShareDetail";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&goods_id=").append(resourceId)
                        .toString();

                Ln.d("LibraryModule:getResourcesDetailById:urlStr:" + urlStr);

                return NetHelper.getData(urlStr);

            }
        }).flatMap(new Func1<String, Observable<PgResourcesDetail>>() {
            @Override
            public Observable<PgResourcesDetail> call(String s) {
                Ln.d("LibraryModule:getResourcesDetailById:result:" + s);
                NetResourcesDetail net = new Gson().fromJson(s, NetResourcesDetail.class);

                Ln.d("LibraryModule:getResourcesDetailById:net:" + net.toString());

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
//
                PgResourcesDetail pg = new PgResourcesDetail();
                if (net.getData() != null) {
                    pg = PgResourcesDetail.getPgByNet(net.getData());
                }
                Ln.d("LibraryModule:getResourcesDetailById:list:" + pg.toString());

                return Observable.just(pg);
            }
        });
    }

    /**
     * 上传图片 （一级网络）
     *
     * @param userId
     * @param token
     * @param imageFile
     * @return
     */
    @Override
    public Observable<String> uploadImage(final int netType, final int userId, final String token, final File imageFile) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成

                String url;
                if (netType == 1) {
                    url = "http://app.m.crphdm.com/?app=organization&controller=org_book&action=uploadImage";
                } else {
                    url = "http://ml.crphdm.com/?app=book&controller=forTwo&action=uploadImage";
//                    url = "http://192.168.4.253/?app=book&controller=forTwo&action=uploadImage";

                }
//                String urlStr = new StringBuffer(url)
//                        .append("&user_id=").append(userId)
//                        .append("&login_token=").append(token).toString();


//               RequestBody body = RequestBody.create(MediaType.parse(""), imageFile);

                RequestBody body = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)
                        .addFormDataPart("image", imageFile.getName(), RequestBody.create(MediaType.parse("img/jpg"), imageFile))
                        .addFormDataPart("user_id", String.valueOf(userId))
                        .addFormDataPart("login_token", token)
                        .build();

//                RequestBody formBody = new FormEncodingBuilder()
//                        .add("user_id", String.valueOf(userId))
//                        .add("login_token",token)
//                        .add("image", String.valueOf(imageFile))
//                        .build();

                return NetHelper.postData(url, body);

            }
        }).flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                Ln.d("LibraryModule:uploadImage:result:" + s);
                NetLibraryUploadImageUrl net = new Gson().fromJson(s, NetLibraryUploadImageUrl.class);
                Ln.d("LibraryModule:uploadImage:net:" + net.toString());

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


                return Observable.just(net.getImage_url());

            }
        });
    }

    /**
     * 上传资源
     *
     * @param userId
     * @param token
     * @param resources
     * @return
     */
    @Override
    public Observable<Boolean> uploadResources(final int userId, final String token, final PgUploadResourceDetail resources) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
//                String url = "http://app.m.crphdm.com/?app=organization&controller=org_book&action=add_app";
                String url = "http://ml.crphdm.com/?app=book&controller=forTwo&action=uploadResource";
//                String url = "http://192.168.4.253/?app=book&controller=forTwo&action=uploadResource";

                Ln.d("LibraryModule:uploadResources:resources:" + resources.toString());

                String mimeType = getMimeType(resources.getFile().getPath());

                Ln.d("LibraryModule:uploadResources:file:mimeType:" + mimeType);

                RequestBody body = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)
                        .addFormDataPart("user_id", String.valueOf(userId))
                        .addFormDataPart("login_token", token)
                        .addFormDataPart("title", resources.getTitle())
                        .addFormDataPart("scope", String.valueOf(resources.getScope()))
                        .addFormDataPart("author", resources.getAuthor())
                        .addFormDataPart("org_id", String.valueOf(resources.getOrg_id()))
                        .addFormDataPart("description", resources.getDescription())
                        .addFormDataPart("price", String.valueOf(resources.getPrice()))
                        .addFormDataPart("thumb", resources.getThumb())
                        .addFormDataPart("resource_file", resources.getTitle(), RequestBody.create(MediaType.parse(mimeType), resources.getFile()))
                        .build();
                Ln.d("LibraryModule:uploadResources:file:body:" + body);
                return NetHelper.postData(url, body);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("LibraryModule:uploadResources:result:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("LibraryModule:uploadResources:net:" + net.toString());

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

                return Observable.just(net.isStatus());
            }
        });
    }

    private static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        Ln.d("LibraryModule:uploadResources:extension:"+extension);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            Ln.d("LibraryModule:uploadResources:type:"+type);
        }
        return type;
    }

}
