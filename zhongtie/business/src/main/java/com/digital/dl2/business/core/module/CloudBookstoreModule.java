package com.digital.dl2.business.core.module;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;

import com.digital.dl2.business.core.manager.CloudBookstoreManager;
import com.digital.dl2.business.core.obj.PgBookCategory;
import com.digital.dl2.business.core.obj.PgBookForBookstoreDetail;
import com.digital.dl2.business.core.obj.PgBookForCloudMarket;
import com.digital.dl2.business.core.obj.PgBookForLibraryListEntity;
import com.digital.dl2.business.core.obj.PgResult;
import com.digital.dl2.business.net.NetHelper;
import com.digital.dl2.business.net.obj.NetBookCategory;
import com.digital.dl2.business.net.obj.NetBookForBookstoreDetail;
import com.digital.dl2.business.net.obj.NetBookForCloudMarket;
import com.digital.dl2.business.net.obj.NetBookForLibraryList;
import com.digital.dl2.business.net.obj.NetResult;
import com.digital.dl2.business.util.Constant;
import com.google.gson.Gson;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class CloudBookstoreModule extends CloudBookstoreManager {
    private Context mContext;

    public CloudBookstoreModule(Context context) {
        mContext = context;
    }

    /**
     * 获取推广图书列表（接口35）
     *
     * @param type
     * @return
     */
    @Override
    public Observable<List<PgBookForCloudMarket>> getBookForLibraryListEntityByPromotionTagId(final int type, final String token) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=getBooksLists";

                String urlStr = new StringBuffer(url)
                        .append("&login_token=").append(token)
                        .append("&type=").append(type)
                        .toString();

//                RequestBody formBody = new FormEncodingBuilder()
//                        .add("user_id", "" + user_id)
//                        .add("login_token", "" + token)
//                        .add("tag_id", "" + tag_id)
//                        .add("org_id", "" + org_id)
//                        .add("page", "" + page)
//                        .add("page_size", "" + page_size)
//                        .build();
                Ln.d("CloudBookstoreModule:getBookForLibraryListEntityByPromotionTagId:url:" + urlStr);
                return NetHelper.getData(urlStr);

            }
        }).flatMap(new Func1<String, Observable<List<PgBookForCloudMarket>>>() {
            @Override
            public Observable<List<PgBookForCloudMarket>> call(String s) {
                Ln.d("CloudBookstoreModule:getBookForLibraryListEntityByPromotionTagId:result:" + s);
                List<PgBookForCloudMarket> list = new ArrayList<>();

                NetBookForCloudMarket net = new Gson().fromJson(s, NetBookForCloudMarket.class);

                Ln.d("CloudBookstoreModule:getBookForLibraryListEntityByPromotionTagId:net:" + net.toString());

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
                    list = PgBookForCloudMarket.getPgListByNetList(net.getData());
                }
                Ln.d("CloudBookstoreModule:getBookForLibraryListEntityByPromotionTagId:list:" + list);
                return Observable.just(list);
            }
        });
    }

    /**
     * 获取分类列表(接口34)
     *
     * @param userId   用户ID
     * @param token    token
     * @param type     1.图书 2.POD
     * @param catId    1.中图分类  2. 专业分类
     * @param parentId 父类Id
     * @return 通过
     */
    @Override
    public Observable<List<PgBookCategory>> getBookCategoryById(final int userId, final String token, final int type, final int catId, final int parentId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=getCategory";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&type=").append(type)
                        .append("&cat_id=").append(catId)
                        .append("&parent_id=").append(parentId)
                        .toString();
                Ln.d("CloudBookstoreModule:getBookCategoryById:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgBookCategory>>>() {
            @Override
            public Observable<List<PgBookCategory>> call(String s) {
                Ln.d("CloudBookstoreModule:getBookCategoryById:result:" + s);
                List<PgBookCategory> list = new ArrayList<PgBookCategory>();
                NetBookCategory net = new Gson().fromJson(s, NetBookCategory.class);

                Ln.d("CloudBookstoreModule:getBookCategoryById:netLibrary:" + net.toString());

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
                    list = PgBookCategory.getPgListByNetList(net.getData());
                }
                Ln.d("CloudBookstoreModule:getBookCategoryById:list:" + list);
                return Observable.just(list);
            }
        });
    }


    /**
     * 获取分类下图书列表（接口74）
     *
     * @param userId
     * @param token
     * @param type
     * @param catId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Observable<List<PgBookForLibraryListEntity>> getBookSubCategoryByType(final int userId, final String token, final int isBook, final int type, final int catId, final int page, final int pageSize) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=catBooks";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&isbook=").append(isBook)
                        .append("&type=").append(type)
                        .append("&cat_id=").append(catId)
                        .append("&page=").append(page)
                        .append("&page_size=").append(pageSize)
                        .toString();
                Ln.d("CloudBookstoreModule:getBookSubCategoryByType:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgBookForLibraryListEntity>>>() {
            @Override
            public Observable<List<PgBookForLibraryListEntity>> call(String s) {
                Ln.d("CloudBookstoreModule:getBookSubCategoryByType:result:" + s);
                List<PgBookForLibraryListEntity> list = new ArrayList<>();

                NetBookForLibraryList net = new Gson().fromJson(s, NetBookForLibraryList.class);
                Ln.d("CloudBookstoreModule:getBookSubCategoryByType:net:" + net.toString());

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
                    Ln.d("CloudBookstoreModule:getBookSubCategoryByType:list:" + list);
                }

                return Observable.just(list);
            }
        });
    }

    /**
     * 获取分类图书列表   更多按钮(接口36)
     *
     * @param userId
     * @param token
     * @param tagId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Observable<List<PgBookForLibraryListEntity>> getBookForLibraryListEntityByCategoryId(final int userId, final String token, final int tagId, final int type, final int page, final int pageSize) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=tagDetail";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&tag_id=").append(tagId)
                        .append("&type=").append(type)
                        .append("&page=").append(page)
                        .append("&page_size=").append(pageSize)
                        .toString();
                Ln.d("CloudBookstoreModule:getBookForLibraryListEntityByCategoryId:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgBookForLibraryListEntity>>>() {
            @Override
            public Observable<List<PgBookForLibraryListEntity>> call(String s) {
                Ln.d("CloudBookstoreModule:getBookForLibraryListEntityByCategoryId:result:" + s);
                List<PgBookForLibraryListEntity> list = new ArrayList<>();

                NetBookForLibraryList net = new Gson().fromJson(s, NetBookForLibraryList.class);
                Ln.d("CloudBookstoreModule:getBookForLibraryListEntityByCategoryId:net:" + net.toString());

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
                    Ln.d("CloudBookstoreModule:getBookForLibraryListEntityByCategoryId:list:" + list);
                }

                return Observable.just(list);
            }
        });
    }

    /**
     * 获取图书详情 （接口37）
     *
     * @param userId
     * @param token
     * @param type
     * @param bookId
     * @return
     */
    @Override
    public Observable<List<PgBookForBookstoreDetail>> getBookDetail(final int userId, final String token, final int type, final int bookId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=bookDetail";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&book_type=").append(type)
                        .append("&goods_id=").append(bookId)
                        .toString();

                Ln.d("CloudBookstoreModule:getBookDetail:url:" + urlStr);

                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgBookForBookstoreDetail>>>() {
            @Override
            public Observable<List<PgBookForBookstoreDetail>> call(String s) {
                Ln.d("CloudBookstoreModule:getBookDetail:result:" + s);
                List<PgBookForBookstoreDetail> list = new ArrayList<>();

                NetBookForBookstoreDetail net = new Gson().fromJson(s, NetBookForBookstoreDetail.class);
                Ln.d("CloudBookstoreModule:getBookDetail:net:" + net.toString());

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
                    list = PgBookForBookstoreDetail.getPgListByNetList(net.getData());
                    Ln.d("CloudBookstoreModule:getBookDetail:list:" + list);
                }

                return Observable.just(list);
            }
        });
    }

    /**
     * 收藏图书
     *
     * @param bookId
     * @return
     */
    @Override
    public Observable<Boolean> collectBookById(final int userId, final String token, final int bookId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=addCollect";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&goods_id=").append(bookId)
                        .toString();
                Ln.d("CloudBookstoreModule:collectBookById:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("CloudBookstoreModule:collectBookById:result:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("CloudBookstoreModule:collectBookById:result:" + net.toString());

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
     * 取消收藏
     *
     * @param userId
     * @param token
     * @param bookId
     * @return
     */
    @Override
    public Observable<Boolean> cancelCollectBookById(final int userId, final String token, final int bookId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=delCollect";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&book_id=").append(bookId)
                        .toString();
                Ln.d("CloudBookstoreModule:cancelCollectBookById:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("CloudBookstoreModule:cancelCollectBookById:result:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);

                Ln.d("CloudBookstoreModule:cancelCollectBookById:net:" + net.toString());

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
     * 购买图书
     *
     * @param userId
     * @param token
     * @param is_st
     * @param goods_id
     * @param addr_id
     * @return
     */
    @Override
    public Observable<Boolean> buyBookById(final int userId, final String token, final int is_st, final int goods_id, final int addr_id) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=buyBook";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&is_st=").append(is_st)
                        .append("&goods_id=").append(goods_id)
                        .append("&addr_id=").append(addr_id)
                        .toString();

                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                //使用ShoppingModule  的 submitOrderByBook 方法
                NetResult net = new Gson().fromJson(s, NetResult.class);

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
     * 加入采选单
     *
     * @param orgId
     * @param userId
     * @param token
     * @param goodsId
     * @param type
     * @param num
     * @return
     */
    @Override
    public Observable<PgResult> addMiningListById(final int orgId, final int userId, final String token, final int goodsId, final int type, final int num) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=mirror&controller=MiningMenu&action=addGoodsInMining";
                String urlStr = new StringBuffer(url)
                        .append("&org_id=").append(orgId)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&goods_id=").append(goodsId)
                        .append("&type=").append(type)
                        .append("&num=").append(num)
                        .toString();

                Ln.d("CloudBookstoreModule:addMiningListById:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<PgResult>>() {
            @Override
            public Observable<PgResult> call(String s) {
                Ln.d("CloudBookstoreModule:addMiningListById:s:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("CloudBookstoreModule:addMiningListById:net:" + net);
                PgResult pg = null;

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

                pg = PgResult.getPgByNet(net);

                return Observable.just(pg);
            }
        });
    }

    /**
     * 推荐采选
     *
     * @param userId
     * @param token
     * @param orgId
     * @param goodsId
     * @return
     */
    @Override
    public Observable<Boolean> recommendMiningById(final int userId, final String token, final int orgId, final int goodsId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=addRecommend";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&org_id=").append(orgId)
                        .append("&goods_id=").append(goodsId)
                        .toString();

                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("CloudBookstoreModule:recommendMiningById:s:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);

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


}
