package com.digital.dl2.business.core.module;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;

import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.obj.PgBookForLibraryListEntity;
import com.digital.dl2.business.core.obj.PgLogisticTrace;
import com.digital.dl2.business.core.obj.PgMiningMenuDetail;
import com.digital.dl2.business.core.obj.PgMiningMenuListEntity;
import com.digital.dl2.business.core.obj.PgOrderList;
import com.digital.dl2.business.core.obj.PgPayOrderInfo;
import com.digital.dl2.business.core.obj.PgPublishAgency;
import com.digital.dl2.business.core.obj.PgRecommendMiningMenuEntity;
import com.digital.dl2.business.core.obj.PgResult;
import com.digital.dl2.business.core.obj.PgReviewMiningListData;
import com.digital.dl2.business.core.obj.PgUploadList;
import com.digital.dl2.business.net.NetHelper;
import com.digital.dl2.business.net.obj.NetBookForLibraryList;
import com.digital.dl2.business.net.obj.NetLogisticTrace;
import com.digital.dl2.business.net.obj.NetMiningMenuDetail;
import com.digital.dl2.business.net.obj.NetMiningMenuList;
import com.digital.dl2.business.net.obj.NetOrderListResult;
import com.digital.dl2.business.net.obj.NetPayOrderResult;
import com.digital.dl2.business.net.obj.NetPublishAgency;
import com.digital.dl2.business.net.obj.NetRecommendMiningMenu;
import com.digital.dl2.business.net.obj.NetResult;
import com.digital.dl2.business.net.obj.NetReviewMiningList;
import com.digital.dl2.business.net.obj.NetUploadList;
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
public class PersonalCenterModule extends PersonalCenterManager {
    private Context mContext;

    public PersonalCenterModule(Context context) {
        mContext = context;
    }

    /**
     * 获取验证码
     *
     * @param accountNumber
     * @param accountType
     * @return
     */
    @Override
    public Observable<Boolean> getSecurityCode(String accountNumber, int accountType) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                StringBuffer stringBuffer = new StringBuffer("url");
                stringBuffer.append("?token=").append("token")
                        .append("&username=").append("username");
                return Observable.just(stringBuffer.toString());

            }
        }).flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                //网络请求
//                return NetHelper.postData(s);
                return Observable.just(s);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                NetResult result = new Gson().fromJson(s, NetResult.class);

                return Observable.just(result.isStatus());
            }
        });
    }

    /**
     * 支付管理  获取订单列表
     *
     * @param userId
     * @param token
     * @param type      0.全部 1.已支付 2.未支付
     * @param page
     * @param page_size
     * @return
     */
    @Override
    public Observable<List<PgOrderList>> getOrders(final int userId, final String token, final int type, final int page, final int page_size) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=getIndent";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&order_type=").append(type)
                        .append("&page=").append(page)
                        .append("&page_size=").append(page_size)
                        .toString();

                Ln.d("PersonalCenterModule:getOrders:urlStr:" + urlStr);

                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgOrderList>>>() {
            @Override
            public Observable<List<PgOrderList>> call(String s) {
                Ln.d("PersonalCenterModule:getOrders:result:" + s);
                NetOrderListResult net = new Gson().fromJson(s, NetOrderListResult.class);
                Ln.d("PersonalCenterModule:getOrders:net:" + net.toString());

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
                    List<PgOrderList> list = PgOrderList.getPgListByNetList(net.getData());
                    Ln.d("PersonalCenterModule:getOrders:list:" + list);
                    return Observable.just(list);
                }else {
                    Ln.d("PersonalCenterModule:getOrders:list == null");
                    return null;
                }
            }
        });
    }

    /**
     * 获取订单详情
     *
     * @param token
     * @param orderId
     * @return
     */
    @Override
    public Observable<PgPayOrderInfo> getOrderDetail(final String token, final int orderId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=getIndentDetail";
                String urlStr = new StringBuffer(url)
                        .append("&login_token=").append(token)
                        .append("&order_id=").append(orderId)
                        .toString();

                Ln.d("PersonalCenterModule:getOrderDetail:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<PgPayOrderInfo>>() {
            @Override
            public Observable<PgPayOrderInfo> call(String s) {
                Ln.d("PersonalCenterModule:getOrderDetail:result:" + s);

                NetPayOrderResult net = new Gson().fromJson(s, NetPayOrderResult.class);
                Ln.d("PersonalCenterModule:getOrderDetail:net:" + net.toString());

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
                    PgPayOrderInfo pg = PgPayOrderInfo.getPgByNet(net.getData());
                    Ln.d("PersonalCenterModule:getOrderDetail:pg:" + pg.toString());
                    return Observable.just(pg);
                }

                return null;
            }
        });
    }

    /**
     * 取消订单
     *
     * @param token
     * @param orderId
     * @return
     */
    @Override
    public Observable<Boolean> cancelOrder(final String token, final int orderId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://member.m.crphdm.com/?app=ecshop&controller=order&action=cancelOrder";
                String urlStr = new StringBuffer(url)
                        .append("&login_token=").append(token)
                        .append("&order_id=").append(orderId)
                        .toString();

                Ln.d("PersonalCenterModule:cancelOrder:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("PersonalCenterModule:cancelOrder:s:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("PersonalCenterModule:cancelOrder:net:" + net);

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
     * 获取采选单列表
     *
     * @param userId
     * @param token
     * @param orgId
     * @param demandId
     * @return
     */
    @Override
    public Observable<List<PgMiningMenuListEntity>> getMiningMenuList(final int userId, final String token, final int orgId, final int demandId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=mirror&controller=MiningMenu&action=getMiningMenuDetail";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&org_id=").append(orgId)
                        .append("&demandid=").append(demandId)
                        .toString();

                Ln.d("PersonalCenterModule:getMiningMenuList:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgMiningMenuListEntity>>>() {
            @Override
            public Observable<List<PgMiningMenuListEntity>> call(String s) {
                Ln.d("PersonalCenterModule:getMiningMenuList:result:" + s);
                NetMiningMenuList net = new Gson().fromJson(s, NetMiningMenuList.class);
                Ln.d("PersonalCenterModule:getMiningMenuList:net:" + net.toString());

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
                    List<PgMiningMenuListEntity> list = PgMiningMenuListEntity.getPgListByNetList(net.getData());
                    Ln.d("PersonalCenterModule:getMiningMenuList:list:" + list);
                    return Observable.just(list);
                }

                return null;
            }
        });
    }

    /**
     * 获取采选单  个人中心 采选单按钮(接口64)
     *
     * @param userId
     * @param token
     * @param orgId
     * @return
     */
    @Override
    public Observable<PgMiningMenuDetail> getMiningMenuDetail(final int userId, final String token, final int orgId, final int demandId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=mirror&controller=MiningMenu&action=getMiningMenuDetail";

                String urlStr;

                if (demandId != 0) {//根据ID查询采选单
                    urlStr = new StringBuffer(url)
                            .append("&user_id=").append(userId)
                            .append("&login_token=").append(token)
                            .append("&org_id=").append(orgId)
                            .append("&demandid=").append(demandId)
                            .toString();
                } else {//查询当前采选单
                    urlStr = new StringBuffer(url)
                            .append("&user_id=").append(userId)
                            .append("&login_token=").append(token)
                            .append("&org_id=").append(orgId)
                            .toString();
                }

                Ln.d("PersonalCenterModule:getMiningMenuDetail:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<PgMiningMenuDetail>>() {
            @Override
            public Observable<PgMiningMenuDetail> call(String s) {
                Ln.d("PersonalCenterModule:getMiningMenuDetail:result:" + s);
                NetMiningMenuDetail net = new Gson().fromJson(s, NetMiningMenuDetail.class);
                Ln.d("PersonalCenterModule:getMiningMenuDetail:net:" + net.toString());

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
                    PgMiningMenuDetail pg = PgMiningMenuDetail.getPgByNet(net.getData());
                    Ln.d("PersonalCenterModule:getMiningMenuDetail:pg:" + pg.toString());
                    return Observable.just(pg);
                }
                return null;
            }
        });
    }

    /**
     * 提交采选单
     *
     * @param userId
     * @param token
     * @param orgId
     * @return
     */
    @Override
    public Observable<Boolean> submitMiningMenu(final int userId, final String token, final int orgId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=mirror&controller=MiningMenu&action=submitMiningMenu";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&org_id=").append(orgId)
                        .toString();

                Ln.d("PersonalCenterModule:submitMiningMenu:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("PersonalCenterModule:submitMiningMenu:s:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("PersonalCenterModule:submitMiningMenu:net:" + net);

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
     * 获取历史采选单
     *
     * @param userId
     * @param token
     * @param orgId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Observable<List<PgMiningMenuListEntity>> getHistoryMiningMenuList(final int userId, final String token, final int orgId, final int page, final int pageSize) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=mirror&controller=MiningMenu&action=getMiningMenuList";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&org_id=").append(orgId)
                        .append("&page=").append(page)
                        .append("&page_size=").append(pageSize)
                        .toString();

                Ln.d("PersonalCenterModule:getHistoryMiningMenuList:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgMiningMenuListEntity>>>() {
            @Override
            public Observable<List<PgMiningMenuListEntity>> call(String s) {
                Ln.d("PersonalCenterModule:getHistoryMiningMenuList:result:" + s);
                NetMiningMenuList net = new Gson().fromJson(s, NetMiningMenuList.class);
                Ln.d("PersonalCenterModule:getHistoryMiningMenuList:net:" + net.toString());

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
                    List<PgMiningMenuListEntity> list = PgMiningMenuListEntity.getPgListByNetList(net.getData());
                    Ln.d("PersonalCenterModule:getHistoryMiningMenuList:list:" + list);

                    return Observable.just(list);
                }
                return null;
            }
        });
    }

    /**
     * 获取用户推荐采选列表
     *
     * @param orgId
     * @param userId
     * @param token
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Observable<List<PgRecommendMiningMenuEntity>> getUserRecommendMining(final int orgId, final int userId, final String token, final int page, final int pageSize) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=mirror&controller=MiningMenu&action=getRecommendResource";
                String urlStr = new StringBuffer(url)
                        .append("&org_id=").append(orgId)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&page=").append(page)
                        .append("&page_size=").append(pageSize)
                        .toString();

                Ln.d("PersonalCenterModule:getUserRecommendMining:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgRecommendMiningMenuEntity>>>() {
            @Override
            public Observable<List<PgRecommendMiningMenuEntity>> call(String s) {
                Ln.d("PersonalCenterModule:getUserRecommendMining:result:" + s);
                List<PgRecommendMiningMenuEntity> list = new ArrayList<>();
                NetRecommendMiningMenu net = new Gson().fromJson(s, NetRecommendMiningMenu.class);
                Ln.d("PersonalCenterModule:getUserRecommendMining:net:" + net.toString());

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
                    list = PgRecommendMiningMenuEntity.getPgListByNetList(net.getData());
                    Ln.d("PersonalCenterModule:getUserRecommendMining:list:" + list);
                }
                return Observable.just(list);
            }
        });
    }

    /**
     * 清空推荐采选单列表
     *
     * @param userId
     * @param token
     * @param orgId
     * @return
     */
    @Override
    public Observable<Boolean> deleteAdviceMiningList(final int userId, final String token, final int orgId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=cleartj";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&org_id=").append(orgId)
                        .toString();

                Ln.d("PersonalCenterModule:deleteAdviceMiningList:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("PersonalCenterModule:deleteAdviceMiningList:s:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("PersonalCenterModule:deleteAdviceMiningList:net:" + net);

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
     * 从采选单删除商品
     *
     * @param userId
     * @param orgId
     * @param token
     * @param goodsId
     * @param type
     * @param num
     * @return
     */
    @Override
    public Observable<Boolean> deleteMiningList(final int userId, final int orgId, final String token, final int goodsId, final int type, final int num) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=clearRecommend";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&org_id=").append(orgId)
                        .append("&login_token=").append(token)
                        .append("&goods_id=").append(goodsId)
                        .append("&type=").append(type)
                        .append("&num=").append(num)
                        .toString();

                Ln.d("PersonalCenterModule:deleteMiningList:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("PersonalCenterModule:deleteMiningList:s:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("PersonalCenterModule:deleteMiningList:net:" + net);

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
     * 领导 获取审核采选单列表
     *
     * @param userId
     * @param token
     * @param orgId
     * @param status   1.已审核 2.未审核
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Observable<List<PgReviewMiningListData>> getReviewMiningList(final int userId, final String token, final int orgId, final int status, final int page, final int pageSize) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=mirror&controller=MiningMenu&action=getOrgMiningMenuList";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&org_id=").append(orgId)
                        .append("&status=").append(status)
                        .append("&page=").append(page)
                        .append("&page_size=").append(pageSize)
                        .toString();

                Ln.d("PersonalCenterModule:getReviewMiningList:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgReviewMiningListData>>>() {
            @Override
            public Observable<List<PgReviewMiningListData>> call(String s) {
                Ln.d("PersonalCenterModule:getReviewMiningList:result:" + s);
                List<PgReviewMiningListData> list = new ArrayList<>();

                NetReviewMiningList net = new Gson().fromJson(s, NetReviewMiningList.class);
                Ln.d("PersonalCenterModule:getReviewMiningList:net:" + net.toString());

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
                    list = PgReviewMiningListData.getPgListByNetList(net.getData());
                    Ln.d("PersonalCenterModule:getReviewMiningList:list:" + list);
                }
                return Observable.just(list);
            }
        });
    }

    /**
     * 领导 修改审核采选单
     *
     * @param userId
     * @param token
     * @param orgId
     * @param miningId
     * @param comment  批注内容
     * @param status   1.同意 2.拒绝
     * @return
     */
    @Override
    public Observable<Boolean> modifyReviewMining(final int userId, final String token, final int orgId, final int miningId, final String comment, final int status) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=mirror&controller=MiningMenu&action=updateMiningMenu";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&org_id=").append(orgId)
                        .append("&login_token=").append(token)
                        .append("&id=").append(miningId)
                        .append("&comment=").append(comment)
                        .append("&status=").append(status)
                        .toString();

                Ln.d("PersonalCenterModule:modifyReviewMining:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("PersonalCenterModule:modifyReviewMining:s:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("PersonalCenterModule:modifyReviewMining:net:" + net);

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
     * 绑定手机号
     *
     * @param userId
     * @param token
     * @param type       1.邮箱 2.手机号
     * @param account
     * @param verifyCode
     * @return
     */
    @Override
    public Observable<PgResult> bindingMobileNumber(final int userId, final String token, final int type, final String account, final String verifyCode) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=bindTelorEmail";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&type=").append(type)
                        .append("&account=").append(account)
                        .append("&verify_code=").append(verifyCode)
                        .toString();

                Ln.d("PersonalCenterModule:bindingMobileNumber:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<PgResult>>() {
            @Override
            public Observable<PgResult> call(String s) {
                Ln.d("PersonalCenterModule:bindingMobileNumber:result:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("PersonalCenterModule:bindingMobileNumber:net:" + net.toString());

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

//                    Exceptions.propagate(new NetworkErrorException(net.getMessage()));
                }

                PgResult pg = PgResult.getPgByNet(net);

                return Observable.just(pg);
            }
        });
    }

    /**
     * 获取机构列表
     *
     * @param token
     * @return
     */
    @Override
    public Observable<List<PgPublishAgency>> getPublishAgencys(final String token) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=getOrgList";
                String urlStr = new StringBuffer(url)
                        .append("&login_token=").append(token)
                        .toString();

                Ln.d("PersonalCenterModule:getPublishAgencys:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgPublishAgency>>>() {
            @Override
            public Observable<List<PgPublishAgency>> call(String s) {
                Ln.d("PersonalCenterModule:getPublishAgencys:result:" + s);
                NetPublishAgency net = new Gson().fromJson(s, NetPublishAgency.class);
                Ln.d("PersonalCenterModule:getPublishAgencys:net:" + net.toString());
                List<PgPublishAgency> list = new ArrayList<PgPublishAgency>();

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
                    list = PgPublishAgency.getPgListByNetList(net.getData());
                    Ln.d("PersonalCenterModule:getPublishAgencys:list:" + list);
                }

                return Observable.just(list);
            }
        });
    }

    /**
     * 申请绑定机构
     *
     * @param userId
     * @param token
     * @param orgId
     * @return
     */
    @Override
    public Observable<Boolean> applyForBindingAgency(final int userId, final String token, final int orgId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=bindOrg";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&org_id=").append(orgId)
                        .toString();

                Ln.d("PersonalCenterModule:applyForBindingAgency:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("PersonalCenterModule:applyForBindingAgency:result:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("PersonalCenterModule:applyForBindingAgency:net:" + net.toString());

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
     * 发送意见反馈
     *
     * @param userId
     * @param token
     * @param name
     * @param email
     * @param content
     * @return
     */
    @Override
    public Observable<Boolean> sendFeedback(final int userId, final String token, final String name, final String email, final String content) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=khfw&controller=feedback&action=feedback_app";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&real_name=").append(name)
                        .append("&email=").append(email)
                        .append("&content=").append(content)
                        .toString();

                Ln.d("PersonalCenterModule:sendFeedback:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("PersonalCenterModule:sendFeedback:result:" + s);
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
     * 获取我的收藏列表 (一级中心)
     *
     * @param userId
     * @param token
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Observable<List<PgBookForLibraryListEntity>> getMyCollectList(final int userId, final String token, final int page, final int pageSize) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=getMyFavorite";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&page=").append(page)
                        .append("&page_size=").append(pageSize)
                        .toString();

                Ln.d("PersonalCenterModule:getMyCollectList:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgBookForLibraryListEntity>>>() {
            @Override
            public Observable<List<PgBookForLibraryListEntity>> call(String s) {
                Ln.d("PersonalCenterModule:getMyCollectList:result:" + s);
                List<PgBookForLibraryListEntity> list = new ArrayList<>();

                NetBookForLibraryList net = new Gson().fromJson(s, NetBookForLibraryList.class);
                Ln.d("PersonalCenterModule:getMyCollectList:net:" + net.toString());

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
                    Ln.d("PersonalCenterModule:getMyCollectList:list:" + list);
                }

                return Observable.just(list);
            }
        });
    }

    /**
     * 获取我的预订列表：（二级中心）
     *
     * @param userId
     * @param token
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Observable<List<PgBookForLibraryListEntity>> getMyDestineList(final int userId, final String token, final int page, final int pageSize) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://ml.crphdm.com/?app=book&controller=forTwo&action=getMyReserve";
//                String url = "http://192.168.4.253/?app=book&controller=forTwo&action=getMyReserve";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&page=").append(page)
                        .append("&page_size=").append(pageSize)
                        .toString();

                Ln.d("PersonalCenterModule:getMyDestineList:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgBookForLibraryListEntity>>>() {
            @Override
            public Observable<List<PgBookForLibraryListEntity>> call(String s) {
                Ln.d("PersonalCenterModule:getMyDestineList:result:" + s);
                List<PgBookForLibraryListEntity> list = new ArrayList<>();

                NetBookForLibraryList net = new Gson().fromJson(s, NetBookForLibraryList.class);
                Ln.d("PersonalCenterModule:getMyDestineList:net:" + net.toString());

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
                    Ln.d("PersonalCenterModule:getMyDestineList:list:" + list);
                }

                return Observable.just(list);
            }
        });
    }

    /**
     * 获取我的上传列表
     *
     * @param userId
     * @param token
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Observable<List<PgUploadList>> getMyUploadList(final int userId, final String token, final int page, final int pageSize) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                String url = "http://ml.crphdm.com/?app=book&controller=forTwo&action=getUploadList";
//                String url = "http://192.168.4.253/?app=book&controller=forTwo&action=getUploadList";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&page=").append(page)
                        .append("&page_size=").append(pageSize)
                        .toString();

                Ln.d("PersonalCenterModule:getMyUploadList:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgUploadList>>>() {
            @Override
            public Observable<List<PgUploadList>> call(String s) {
                Ln.d("PersonalCenterModule:getMyUploadList:result:" + s);
                List<PgUploadList> list = new ArrayList<>();

                NetUploadList net = new Gson().fromJson(s, NetUploadList.class);
                Ln.d("PersonalCenterModule:getMyUploadList:net:" + net.toString());

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
                    list = PgUploadList.getPgListByNetList(net.getData());
                    Ln.d("PersonalCenterModule:getMyUploadList:list:" + list);
                }
                return Observable.just(list);
            }
        });
    }

    /**
     * 取消预订
     *
     * @param id
     * @return
     */
    @Override
    public Observable<Boolean> cancelDestineById(String id) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                StringBuffer stringBuffer = new StringBuffer("url");
                stringBuffer.append("?token=").append("token")
                        .append("&username=").append("username");
                return Observable.just(stringBuffer.toString());

            }
        }).flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                //网络请求
//                return NetHelper.postData(s);
                return Observable.just(s);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                NetResult result = new Gson().fromJson(s, NetResult.class);

                return Observable.just(result.isStatus());
            }
        });
    }

    /**
     * 获取我的购买列表： （一级中心）
     *
     * @param userId
     * @param token
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Observable<List<PgBookForLibraryListEntity>> getMyBuyList(final int userId, final String token, final int page, final int pageSize) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=getMyBook";
                StringBuffer urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&page=").append(page)
                        .append("&page_size=").append(pageSize);

                Ln.d("PersonalCenterModule:getMyBuyList:urlStr:" + urlStr);
                return NetHelper.getData(urlStr.toString());
            }
        }).flatMap(new Func1<String, Observable<List<PgBookForLibraryListEntity>>>() {
            @Override
            public Observable<List<PgBookForLibraryListEntity>> call(String s) {
                Ln.d("PersonalCenterModule:getMyBuyList:result:" + s);
                List<PgBookForLibraryListEntity> list = new ArrayList<>();

                NetBookForLibraryList net = new Gson().fromJson(s, NetBookForLibraryList.class);
                Ln.d("PersonalCenterModule:getMyBuyList:net:" + net.toString());

                if (net.isStatus() && net.getData().length != 0) {
                    list = PgBookForLibraryListEntity.getPgListByNetList(net.getData());
                    Ln.d("PersonalCenterModule:getMyBuyList:list:" + list);
                }

                return Observable.just(list);
            }
        });
    }


    //获取物流信息
    @Override
    public Observable<List<PgLogisticTrace>> getLogisticTrace(final int userId, final String orderSn ,final String mToken) {

        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=getLogistics";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&order_sn=").append(orderSn)
                        .append("&login_token=").append(mToken)
                        .toString();
                Ln.d("PersonalCenterModule:getLogisticTrace:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgLogisticTrace>>>() {
            @Override
            public Observable<List<PgLogisticTrace>> call(String s) {
                Ln.d("PersonalCenterModule:getLogisticTrace:s:" + s);
//                s = "{\"company\":\"\u987a\u4e30\",\"com\":\"sf\",\"no\":\"912481037468\",\"status\":\"1\",\"list\":[{\"datetime\":\"2016-06-20 11:42:31\",\"remark\":\"\u5df2\u7b7e\u6536,\u611f\u8c22\u4f7f\u7528\u987a\u4e30,\u671f\u5f85\u518d\u6b21\u4e3a\u60a8\u670d\u52a1\",\"zone\":\"\"},{\"datetime\":\"2016-06-20 07:38:46\",\"remark\":\"\u6b63\u5728\u6d3e\u9001\u9014\u4e2d,\u8bf7\u60a8\u51c6\u5907\u7b7e\u6536 (\u6d3e\u4ef6\u4eba:\u59dc\u53cc\u5174,\u7535\u8bdd:13521547618)\",\"zone\":\"\"},{\"datetime\":\"2016-06-19 19:02:03\",\"remark\":\"\u56e0\u4f11\u606f\u65e5\u6216\u5047\u671f\u5ba2\u6237\u4e0d\u4fbf\u6536\u4ef6,\u5f85\u5de5\u4f5c\u65e5\u6d3e\u9001\",\"zone\":\"\"},{\"datetime\":\"2016-06-19 19:01:08\",\"remark\":\"\u5feb\u4ef6\u6d3e\u9001\u4e0d\u6210\u529f(\u56e0\u4f11\u606f\u65e5\u6216\u5047\u671f\u5ba2\u6237\u4e0d\u4fbf\u6536\u4ef6),\u5f85\u5de5\u4f5c\u65e5\u518d\u6b21\u6d3e\u9001\",\"zone\":\"\"},{\"datetime\":\"2016-06-19 16:26:48\",\"remark\":\"\u6b63\u5728\u6d3e\u9001\u9014\u4e2d,\u8bf7\u60a8\u51c6\u5907\u7b7e\u6536 (\u6d3e\u4ef6\u4eba:\u9648\u6653\u4f1f,\u7535\u8bdd:13911605451)\",\"zone\":\"\"},{\"datetime\":\"2016-06-19 16:09:10\",\"remark\":\"\u5feb\u4ef6\u5230\u8fbe \u3010\u5317\u4eac\u901a\u5dde\u91d1\u6865\u8425\u4e1a\u70b9\u3011\",\"zone\":\"\"},{\"datetime\":\"2016-06-19 13:19:12\",\"remark\":\"\u5feb\u4ef6\u79bb\u5f00\u3010\u5317\u4eac\u987a\u4e49\u96c6\u6563\u4e2d\u5fc3\u3011,\u6b63\u53d1\u5f80 \u3010\u5317\u4eac\u901a\u5dde\u91d1\u6865\u8425\u4e1a\u70b9\u3011\",\"zone\":\"\"},{\"datetime\":\"2016-06-19 12:29:35\",\"remark\":\"\u5feb\u4ef6\u5230\u8fbe \u3010\u5317\u4eac\u987a\u4e49\u96c6\u6563\u4e2d\u5fc3\u3011\",\"zone\":\"\"},{\"datetime\":\"2016-06-18 23:05:54\",\"remark\":\"\u5feb\u4ef6\u79bb\u5f00\u3010\u90d1\u5dde\u65b0\u90d1\u96c6\u6563\u4e2d\u5fc3\u3011,\u6b63\u53d1\u5f80\u4e0b\u4e00\u7ad9\",\"zone\":\"\"},{\"datetime\":\"2016-06-18 22:27:57\",\"remark\":\"\u5feb\u4ef6\u5230\u8fbe \u3010\u90d1\u5dde\u65b0\u90d1\u96c6\u6563\u4e2d\u5fc3\u3011\",\"zone\":\"\"},{\"datetime\":\"2016-06-18 21:24:34\",\"remark\":\"\u5feb\u4ef6\u79bb\u5f00\u3010\u90d1\u5dde\u5e02\u91d1\u6c34\u533a\u6570\u7801\u516c\u5bd3\u8425\u4e1a\u90e8\u3011,\u6b63\u53d1\u5f80 \u3010\u90d1\u5dde\u65b0\u90d1\u96c6\u6563\u4e2d\u5fc3\u3011\",\"zone\":\"\"},{\"datetime\":\"2016-06-18 20:06:28\",\"remark\":\"\u987a\u4e30\u901f\u8fd0 \u5df2\u6536\u53d6\u5feb\u4ef6\",\"zone\":\"\"}]}";
                List<PgLogisticTrace> list = new ArrayList<PgLogisticTrace>();
                Ln.d("PersonalCenterModule:getLogisticTrace:s1:" + s);
                NetLogisticTrace net = new Gson().fromJson(s, NetLogisticTrace.class);
                Ln.d("PersonalCenterModule:getLogisticTrace:net:" + net.toString());
                if (net.getStatus() == 1 && net.getList().length != 0) {
                    list = PgLogisticTrace.getPgListByNet(net.getList());
                }
                Ln.d("PersonalCenterModule:getLogisticTrace:list:" + list);
                return Observable.just(list);
            }
        });
    }

}
