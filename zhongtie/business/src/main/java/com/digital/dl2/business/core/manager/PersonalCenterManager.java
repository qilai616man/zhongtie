package com.digital.dl2.business.core.manager;

import android.content.Context;

import com.digital.dl2.business.core.module.PersonalCenterModule;
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

import java.util.List;

import rx.Observable;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public abstract class PersonalCenterManager {

    private static PersonalCenterManager mPersonalCenterManager;

    public static PersonalCenterManager getInstance() {
        if (mPersonalCenterManager == null)
            throw new NullPointerException("PersonalCenterManager is not init");
        return mPersonalCenterManager;
    }

    //初始化1
    public static PersonalCenterManager init(Context context) {
        if (mPersonalCenterManager == null)
            mPersonalCenterManager = new PersonalCenterModule(context);
        return mPersonalCenterManager;
    }

    //获取验证码
    public abstract Observable<Boolean> getSecurityCode(String accountNumber, int accountType);

    //获取订单
    public abstract Observable<List<PgOrderList>> getOrders(int userId, String token, int type, int page, int page_size);

    //获取订单详情
    public abstract Observable<PgPayOrderInfo> getOrderDetail(String token, int OrderId);

    //取消订单
    public abstract Observable<Boolean> cancelOrder(String token,int orderId);

    //获取采选单列表
    public abstract Observable<List<PgMiningMenuListEntity>> getMiningMenuList(int userId, String token, int orgId, int demandId);

    //获取采选单
    public abstract Observable<PgMiningMenuDetail> getMiningMenuDetail(int userId, String token, int orgId,int demandId);

    //提交采选单
    public abstract Observable<Boolean> submitMiningMenu( int userId,  String token,  int orgId);

    //获取历史采选单
    public abstract Observable<List<PgMiningMenuListEntity>> getHistoryMiningMenuList(int userId,String token,int orgId,int page,int pageSize);

    //获取用户推荐采选列表
    public abstract Observable<List<PgRecommendMiningMenuEntity>> getUserRecommendMining(int orgId, int userId, String token, int page, int pageSize);

    //清空推荐采选单列表
    public abstract Observable<Boolean> deleteAdviceMiningList(int userId, String token, int orgId);

    //采选单减少、删除商品
    public abstract Observable<Boolean> deleteMiningList(int userId,int orgId,String token,int goodsId,int type,int num);

    //获取审核采选单列表
    public abstract Observable<List<PgReviewMiningListData>> getReviewMiningList(int userId,String token,int orgId,int status,int page,int pageSize);

    //领导 修改（审核）采选单
    public abstract Observable<Boolean> modifyReviewMining(int userId,String token,int orgId,int id,String comment,int status);

    //绑定手机号
    public abstract Observable<PgResult> bindingMobileNumber(int userId, String token, int type, String account, String verifyCode);

    //获取机构列表
    public abstract Observable<List<PgPublishAgency>> getPublishAgencys(String token);

    //申请绑定机构
    public abstract Observable<Boolean> applyForBindingAgency(int userId, String token, int orgId);

    //发送意见反馈
    public abstract Observable<Boolean> sendFeedback(int userId, String token, String name, String email, String content);

    //获取我的收藏列表 (一级中心)
    public abstract Observable<List<PgBookForLibraryListEntity>> getMyCollectList(int userId, String token, int page, int pageSize);

    //获取我的预订列表：（二级中心）
    public abstract Observable<List<PgBookForLibraryListEntity>> getMyDestineList(int userId, String token, int page, int pageSize);

    public abstract Observable<List<PgUploadList>> getMyUploadList(int userId,String token,int page,int pageSize);

    //取消预订
    public abstract Observable<Boolean> cancelDestineById(String id);

    //获取我的购买列表： （一级中心）
    public abstract Observable<List<PgBookForLibraryListEntity>> getMyBuyList(int userId, String token, int page, int pageSize);

    //获取物流信息
    public abstract Observable<List<PgLogisticTrace>> getLogisticTrace(int userId, String orderSn ,String mToken);

}
