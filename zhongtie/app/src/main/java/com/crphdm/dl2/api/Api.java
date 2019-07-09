package com.crphdm.dl2.api;

import com.digital.dl2.business.bean.ExceedTime;
import com.digital.dl2.business.bean.Jsob;
import com.digital.dl2.business.bean.Userjson;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/10/31.
 */

public interface Api {
    //得到清单
    @GET("/")
    Call<Userjson> getList(@Query("app")String book, @Query("controller")String controller, @Query("action")String action);
//?app=book&controller=forTwo&action=getReadTime
    //得到结果
    @GET("/")
     Call<Jsob> getResult(@Query("app")String app, @Query("controller")String forTwo, @Query("action")String getReadTime);
  //得到时间
    @GET("/")//http://192.168.4.253/?app=book&controller=forTwo&action=getMemberBorrowPermissions  login_token:(string) user_id:(int)
    Call<ExceedTime> getTime(@Query("app")String book, @Query("controller")String controller, @Query("action")String action,@Query("login_token")String login_token, @Query("user_id")int user_id);
}
