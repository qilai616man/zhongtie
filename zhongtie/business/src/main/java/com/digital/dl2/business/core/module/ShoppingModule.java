package com.digital.dl2.business.core.module;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;

import com.digital.dl2.business.core.manager.ShoppingManager;
import com.digital.dl2.business.core.obj.PgAddress;
import com.digital.dl2.business.core.obj.PgPayOrderInfo;
import com.digital.dl2.business.core.obj.PgResult;
import com.digital.dl2.business.core.obj.PgShoppingCart;
import com.digital.dl2.business.net.NetHelper;
import com.digital.dl2.business.net.obj.NetAddress;
import com.digital.dl2.business.net.obj.NetPayOrderResult;
import com.digital.dl2.business.net.obj.NetResult;
import com.digital.dl2.business.net.obj.NetShoppingCart;
import com.digital.dl2.business.net.obj.NetShoppingCartBookNumber;
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
public class ShoppingModule extends ShoppingManager {
    private Context mContext;

    public ShoppingModule(Context context) {
        mContext = context;
    }

    /**
     * 25.获取购物车列表
     *
     * @return
     */
    @Override
    public Observable<List<PgShoppingCart>> getShoppingCartList(final int userId, final String token, final int page, final int page_size) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=getMyCart";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&page=").append(page)
                        .append("&page_size=").append(page_size)
                        .toString();

                Ln.d("ShoppingModule:getShoppingCartList:urlStr:" + urlStr);

                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgShoppingCart>>>() {
            @Override
            public Observable<List<PgShoppingCart>> call(String s) {
                Ln.d("ShoppingModule:getShoppingCartList:result:" + s);
                NetShoppingCart net = new Gson().fromJson(s, NetShoppingCart.class);
                Ln.d("ShoppingModule:getShoppingCartList:net:" + net.toString());

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
                if (net.isStatus()) {
                    List<PgShoppingCart> list = PgShoppingCart.getPgListByNet(net);
                    Ln.d("ShoppingModule:getShoppingCartList:list:" + list);
                    return Observable.just(list);
                } else {
                    return null;
                }

            }
        });
    }

    /**
     * 获取购物车内商品数量
     *
     * @return
     */
    @Override
    public Observable<Integer> getShoppingCartBookNumber(final int userId, final String token) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=getCartNum";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .toString();
                Ln.d("ShoppingModule:getShoppingCartList:urlStr:" + urlStr);

                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(String s) {
                NetShoppingCartBookNumber net = new Gson().fromJson(s, NetShoppingCartBookNumber.class);

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

                return Observable.just(net.getNum());
            }
        });
    }

    /**
     * 添加图书到购物车
     *
     * @param userId
     * @param token
     * @param bookId
     * @param type   1.实体书  2.电子书
     * @param number
     * @return
     */
    @Override
    public Observable<List<PgShoppingCart>> addBookToShoppingCart(final int userId, final String token, final int bookId, final int type, final int number) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=addToCart";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&goods_id=").append(bookId)
                        .append("&type=").append(type)
                        .append("&num=").append(number)
                        .toString();

                Ln.d("ShoppingModule:addBookToShoppingCart:urlStr:" + urlStr);

                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgShoppingCart>>>() {
            @Override
            public Observable<List<PgShoppingCart>> call(String s) {
                List<PgShoppingCart> list = new ArrayList<PgShoppingCart>();
                Ln.d("ShoppingModule:addBookToShoppingCart:result:" + s);
                NetShoppingCart net = new Gson().fromJson(s, NetShoppingCart.class);
                Ln.d("ShoppingModule:addBookToShoppingCart:net:" + s);

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

                list = PgShoppingCart.getPgListByNet(net);
                Ln.d("ShoppingModule:addBookToShoppingCart:list:" + list);

                return Observable.just(list);
            }
        });
    }

    /**
     * 从购物车里移除图书(接口27)
     *
     * @param userId
     * @param token
     * @param bookId
     * @param type
     * @param number
     * @return
     */
    @Override
    public Observable<List<PgShoppingCart>> deleteBookFromShoppingCart(final int userId, final String token, final int bookId, final int type, final int number) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=delFromCart";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&goods_id=").append(bookId)
                        .append("&type=").append(type)
                        .append("&num=").append(number)
                        .toString();

                Ln.d("LibraryModule:deleteBookFromShoppingCart:urlStr:" + urlStr);

                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgShoppingCart>>>() {
            @Override
            public Observable<List<PgShoppingCart>> call(String s) {
                Ln.d("LibraryModule:deleteBookFromShoppingCart:result:" + s);
                NetShoppingCart net = new Gson().fromJson(s, NetShoppingCart.class);
                Ln.d("LibraryModule:deleteBookFromShoppingCart:netShoppingCart:" + net.toString());

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

                List<PgShoppingCart> list = PgShoppingCart.getPgListByNet(net);
                Ln.d("LibraryModule:deleteBookFromShoppingCart:list:" + list);
                return Observable.just(list);
            }
        });
    }

    /**
     * 结账（购物车提交订单）（接口28）
     *
     * @param userId
     * @param token
     * @param type    1.实体书  2.电子书
     * @param addr_id
     * @return
     */
    @Override
    public Observable<PgPayOrderInfo> submitOrderByShoppingCart(final int userId, final String token, final int type, final int addr_id) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=organization&controller=orginterface&action=createIndent";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&type=").append(type)
                        .append("&addr_id=").append(addr_id)
                        .toString();

                Ln.d("ShoppingModule:submitOrderByShoppingCart:urlStr:" + urlStr);

                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<PgPayOrderInfo>>() {
            @Override
            public Observable<PgPayOrderInfo> call(String s) {
                Ln.d("ShoppingModule:submitOrderByShoppingCart:result:" + s);
                NetPayOrderResult net = new Gson().fromJson(s, NetPayOrderResult.class);
                Ln.d("ShoppingModule:submitOrderByShoppingCart:net:" + net.toString());

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
                    Ln.d("ShoppingModule:submitOrderByShoppingCart:pg:" + pg.toString());
                    return Observable.just(pg);
                }

                return null;
            }
        });
    }

    /**
     * 结账（图书直接提交订单）(接口41)
     *
     * @param userId
     * @param token
     * @param is_st
     * @param goodsId
     * @param addressId
     * @return
     */
    @Override
    public Observable<PgPayOrderInfo> submitOrderByBook(final int userId, final String token, final int is_st, final int goodsId, final int addressId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=buyBook";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&is_st=").append(is_st)
                        .append("&goods_id=").append(goodsId)
                        .append("&addr_id=").append(addressId)
                        .toString();

                Ln.d("ShoppingModule:submitOrderByBook:urlStr:" + urlStr);

                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<PgPayOrderInfo>>() {
            @Override
            public Observable<PgPayOrderInfo> call(String s) {
                Ln.d("ShoppingModule:submitOrderByBook:result:" + s);
                NetPayOrderResult net = new Gson().fromJson(s, NetPayOrderResult.class);
                Ln.d("ShoppingModule:submitOrderByBook:net:" + net.toString());

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
                    Ln.d("ShoppingModule:submitOrderByBook:pg:" + pg.toString());
                    return Observable.just(pg);
                }else {
                    return null;

                }
            }
        });
    }

    /**
     * 添加地址
     *
     * @param userId
     * @param token
     * @param name
     * @param phone
     * @param address
     * @return 通过
     */
    @Override
    public Observable<Boolean> addAddress(final int userId, final String token, final String name, final String phone, final String address) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=shippingaddress&controller=shippingaddress&action=add_app";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&name=").append(name)
                        .append("&phone=").append(phone)
                        .append("&address=").append(address)
                        .toString();

                Ln.d("ShoppingModule:addAddress:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("ShoppingModule:addAddress:result:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("ShoppingModule:addAddress:result:" + net.toString());

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
     * 删除地址
     *
     * @param userId
     * @param token
     * @param addressId
     * @return 通过
     */
    @Override
    public Observable<Boolean> deleteAddress(final int userId, final String token, final int addressId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=shippingaddress&controller=shippingaddress&action=delete_app";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&addr_id=").append(addressId)
                        .toString();

                Ln.d("ShoppingModule:deleteAddress:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("ShoppingModule:deleteAddress:result:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("ShoppingModule:deleteAddress:result:" + net.toString());

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
     * 修改地址
     *
     * @param userId
     * @param token
     * @param addressId
     * @param name
     * @param phone
     * @param address
     * @return 通过
     */
    @Override
    public Observable<Boolean> changeAddress(final int userId, final String token, final int addressId, final String name, final String phone, final String address) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=shippingaddress&controller=shippingaddress&action=update_app";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&addr_id=").append(addressId)
                        .append("&name=").append(name)
                        .append("&phone=").append(phone)
                        .append("&address=").append(address)
                        .toString();

                Ln.d("ShoppingModule:changeAddress:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("ShoppingModule:changeAddress:result:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("ShoppingModule:changeAddress:result:" + net.toString());

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
     * 获取常用地址列表
     *
     * @param userId
     * @param token
     * @return
     */
    @Override
    public Observable<List<PgAddress>> getCommonAddress(final int userId, final String token) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=shippingaddress&controller=shippingaddress&action=get_list_app";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .toString();

                Ln.d("ShoppingModule:getCommonAddress:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }

        }).flatMap(new Func1<String, Observable<List<PgAddress>>>() {
            @Override
            public Observable<List<PgAddress>> call(String s) {
                Ln.d("ShoppingModule:getCommonAddress:result:" + s);

                List<PgAddress> list = new ArrayList<>();
                NetAddress net = new Gson().fromJson(s, NetAddress.class);
                Ln.d("ShoppingModule:getCommonAddress:net:" + net.toString());

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
                    list = PgAddress.getPgListByNetList(net.getData());
                    Ln.d("ShoppingModule:getCommonAddress:list:" + list);
                }

                return Observable.just(list);
            }
        });
    }

    /**
     * 0元免费购买
     *
     * @param userId
     * @param bookId
     * @return
     */
    @Override
    public Observable<PgResult> freePurchase(final int userId, final int bookId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://app.m.crphdm.com/?app=book&controller=bookinterface&action=addBuyBook";
                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&book_id=").append(bookId)
                        .toString();

                Ln.d("ShoppingModule:freePurchase:urlStr:" + urlStr);
                return NetHelper.getData(urlStr);
            }

        }).flatMap(new Func1<String, Observable<PgResult>>() {
            @Override
            public Observable<PgResult> call(String s) {
                Ln.d("ShoppingModule:freePurchase:result:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("ShoppingModule:freePurchase:net:" + net);

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

                return Observable.just(pg);
            }
        });
    }
}
