package com.digital.dl2.business.core.manager;

import android.content.Context;

import com.digital.dl2.business.core.module.ShoppingModule;
import com.digital.dl2.business.core.obj.PgAddress;
import com.digital.dl2.business.core.obj.PgPayOrderInfo;
import com.digital.dl2.business.core.obj.PgResult;
import com.digital.dl2.business.core.obj.PgShoppingCart;

import java.util.List;

import rx.Observable;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public abstract class ShoppingManager {

    private static ShoppingManager mShoppingManager;

    public static ShoppingManager getInstance() {
        if (mShoppingManager == null)
            throw new NullPointerException("ShoppingManager is not init");
        return mShoppingManager;
    }

    //初始化1
    public static ShoppingManager init(Context context) {
        if (mShoppingManager == null)
            mShoppingManager = new ShoppingModule(context);
        return mShoppingManager;
    }

    //25.获取购物车列表
    public abstract Observable<List<PgShoppingCart>> getShoppingCartList(int userId, String token, int page, int page_size);

    //获取购物车内商品数量
    public abstract Observable<Integer> getShoppingCartBookNumber(int userId, String token);

    //添加图书到购物车
    public abstract Observable<List<PgShoppingCart>> addBookToShoppingCart(int userId, String token, int bookId, int type, int number);

    //移除图书从购物车
    public abstract Observable<List<PgShoppingCart>> deleteBookFromShoppingCart(int userId, String token, int bookId, int type, int number);

    //结账（购物车提交订单）
    public abstract Observable<PgPayOrderInfo> submitOrderByShoppingCart(int userId, String token, int type, int addr_id);

    //结账（图书直接提交订单）
    public abstract Observable<PgPayOrderInfo> submitOrderByBook(int userId,String token,int is_st,int goodsId,int addressId);

    //添加地址
    public abstract Observable<Boolean> addAddress(int userId,String token,String name,String phone,String address);

    //删除地址
    public abstract Observable<Boolean> deleteAddress(int userId,String token,int addressId);

    //修改地址
    public abstract Observable<Boolean> changeAddress(int userId,String token,int addressId,String name,String phone,String address);

    //获取常用地址
    public abstract Observable<List<PgAddress>> getCommonAddress(int userId,String token);

    //电子书0元时 跳过支付宝直接购买
    public abstract Observable<PgResult> freePurchase(int userId,int bookId);

}
