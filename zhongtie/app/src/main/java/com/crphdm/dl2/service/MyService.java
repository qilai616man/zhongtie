package com.crphdm.dl2.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.brainsoon.utils.BookUtils;
import com.crphdm.dl2.api.Api;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.UserModuleImpl;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Util;
import com.digital.dl2.business.bean.ExceedTime;
import com.digital.dl2.business.core.manager.BookshelfManager;
import com.digital.dl2.business.core.manager.PublicManager;
import com.digital.dl2.business.database.DatabaseManager;
import com.digital.dl2.business.util.Constant;
import com.digital.dl2.business.util.DownloadEvent;
import com.goyourfly.gdownloader.GDownloader;
import com.goyourfly.gdownloader.db.DbDownloadExt;
import com.goyourfly.gdownloader.helper.DownloadHelper;
import com.goyourfly.gdownloader.utils.Ln;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static io.paperdb.Paper.book;
import static org.acra.ACRA.log;

public class MyService extends Service implements DownloadHelper.DownloadListener {
    public static final String INTENT_CMD = "INTENT_CMD";
    public static final String CMD_UN_ZIP_FILE = "CMD_UN_ZIP_FILE";
    private Handler mHandler;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int book_id = msg.arg1;
            switch (msg.what) {
                case 0:
                    Ln.d("MyService:hadler:文件" + book_id + "解压成功");
                    Toast.makeText(MyService.this, "文件解压成功", Toast.LENGTH_SHORT).show();

                    BookshelfManager.getInstance().updateBookByUnzipState(book_id, Constant.BOOK_UNZIP_STATE_SUCCESS);

                    break;
                case -1:
                    Ln.d("MyService:hadler:文件" + book_id + "解压失败");
                    Toast.makeText(MyService.this, "文件解压失败", Toast.LENGTH_SHORT).show();

                    if (BookshelfManager.getInstance().getBookById(book_id) != null) {

                        File downloadFile = new File(BookshelfManager.getInstance().getBookById(book_id).getDownloadUrl());
                        File readFile = new File(BookshelfManager.getInstance().getBookById(book_id).getDownloadUrl());

                        Ln.d("MyService:hadler:downloadFile" + downloadFile);
                        Ln.d("MyService:hadler:readFile" + readFile);
                        BookshelfManager.getInstance().deleteFile(downloadFile);
                        BookshelfManager.getInstance().deleteFile(readFile);
                        BookshelfManager.getInstance().updateBookByUnzipState(book_id, Constant.BOOK_UNZIP_STATE_FAIL);
                    }

                    break;
            }
        }
    };
    //当另一个组件通过调用bindService()与服务绑定时，系统将调用此方法
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
//        return null;
    }
    //首次创建服务时，系统将调用此方法
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("rere","rerere");
        mHandler = new Handler();

        GDownloader.getInstance().registerListener(this);

        Ln.d("MyService:onCreate");

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //90秒更新一下状态
                Ln.d("MyService:onCreae:刷新网络状态");
                log.i("rere","runrunr");
                refreshNetState();
            }
        }, 0, 1000 * 90);
    }
    //开始服务
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Ln.d("MyService:onStartCommand");
        if (intent != null) {
            String cmd = intent.getStringExtra(INTENT_CMD);

            if (CMD_UN_ZIP_FILE.equals(cmd)) {
                final int bookId = intent.getIntExtra(com.crphdm.dl2.utils.Constant.BOOK_ID, 0);

                final String fileStr = BookshelfManager.getInstance().getBookById(bookId).getDownloadUrl();
                final String path = BookshelfManager.getInstance().getBookById(bookId).getLocalUrl();

                Ln.d("MyService:onStartCommand:downloadUrl:" + fileStr);
                Ln.d("MyService:onStartCommand:readUrl:" + path);
                Ln.d("MyService:onStartCommand:bookId:" + bookId);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BookshelfManager.getInstance().updateBookByUnzipState(bookId, Constant.BOOK_UNZIP_STATE_ING);
                        BookUtils.unZipFile(fileStr, path, bookId, handler);
                    }
                });
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }
    //准备开始
    @Override
    public void onPreStart(String url) {
        Ln.d("MyService:onPreStart:url:" + url);
        DownloadEvent event = new DownloadEvent();
        event.event = DownloadEvent.EVENT_PRE_START;
        event.url = url;
        event.state = DbDownloadExt.DOWNLOAD_STATE_PREPARING;
        EventBus.getDefault().post(event);
    }
    //界面被显示出来的时候执行
    @Override
    public void onStart(String url, long totalLength, long localLength) {
        Ln.d("MyService:onStart:url:" + localLength + "/" + totalLength);
        DownloadEvent event = new DownloadEvent();
        event.event = DownloadEvent.EVENT_START;
        event.url = url;
        event.state = DbDownloadExt.DOWNLOAD_STATE_DOWNLOADING;
        EventBus.getDefault().post(event);
    }
    //下载时触发
    @Override
    public void onProgress(String url, long totalLength, long downloadedBytes) {
        Ln.d("MyService:onProgress:" + downloadedBytes + "/" + totalLength);

        DownloadEvent event = new DownloadEvent();
        event.event = DownloadEvent.EVENT_PROGRESS;
        event.url = url;
        event.state = DbDownloadExt.DOWNLOAD_STATE_DOWNLOADING;
        event.downloadedBytes = downloadedBytes;
        event.totalLength = totalLength;
        EventBus.getDefault().post(event);
    }
    //暂停
    @Override
    public void onPause(String url) {
        Ln.d("MyService:onPause:url:" + url);

        DownloadEvent event = new DownloadEvent();
        event.event = DownloadEvent.EVENT_PAUSE;
        event.url = url;
        EventBus.getDefault().post(event);

    }
    //视频由于要播放下一帧而需要缓冲时触发
    @Override
    public void onWaiting(String url) {
        Ln.d("MyService:onWaiting:url:" + url);
        DownloadEvent event = new DownloadEvent();
        event.event = DownloadEvent.EVENT_WAITING;
        event.state = DbDownloadExt.DOWNLOAD_STATE_PAUSE;
        event.url = url;
        EventBus.getDefault().post(event);
    }
    //关闭对话框
    @Override
    public void onCancel(String url) {
        Ln.d("MyService:onCancel:url:" + url);

        DownloadEvent event = new DownloadEvent();
        event.event = DownloadEvent.EVENT_CANCEL;
        event.state = DbDownloadExt.DOWNLOAD_STATE_DOWNLOADING;
        event.url = url;
        EventBus.getDefault().post(event);
    }
    //关闭当前界面
    @Override
    public void onFinish(final String url) {
        Ln.d("MyService:onFinish:url:" + url);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                final int bookId = Util.getBookIdByUrl(url);

                Ln.d("MyService:onFinish:mDownloadBookId:" + bookId);

                final String fileStr = PublicManager.getInstance().getFilePath(url);
                if (BookshelfManager.getInstance().getBookById(bookId) != null) {
                    final String path = BookshelfManager.getInstance().getBookById(bookId).getLocalUrl();

                    Ln.d("MyService:onFinish:downloadUrl:" + fileStr);
                    Ln.d("MyService:onFinish:readUrl:" + path);

                    if (PublicManager.getInstance().getFilePath(url).endsWith("tdp")) {
                        BookshelfManager.getInstance().updateBookByUnzipState(bookId, Constant.BOOK_UNZIP_STATE_ING);
                        BookUtils.unZipFile(fileStr, path, bookId, handler);
                    } else {
                        Toast.makeText(MyService.this, "解压成功", Toast.LENGTH_SHORT).show();
                        BookshelfManager.getInstance().updateBookByUnzipState(bookId, Constant.BOOK_UNZIP_STATE_SUCCESS);
                    }
                } else {

                }

            }
        });

        DownloadEvent event = new DownloadEvent();
        event.event = DownloadEvent.EVENT_FINISH;
        event.state = DbDownloadExt.DOWNLOAD_STATE_DOWNLOADED;
        event.url = url;
        EventBus.getDefault().post(event);
    }
    //错误
    @Override
    public void onError(final String url, String err) {
        Ln.d("MyService:onError:url:" + url);
        Ln.d("MyService:onError:err:" + err);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                final int bookId = Util.getBookIdByUrl(url);

                Toast.makeText(MyService.this, "下载失败，请重新尝试", Toast.LENGTH_SHORT).show();

                Ln.d("MyService:onError:bookId:" + bookId);
                Ln.d("MyService:onError:book:" + BookshelfManager.getInstance().getBookById(bookId));

//                if(bookId != 0){
//                    BookshelfManager.getInstance().removeBookById(bookId);
//                }

                GDownloader.getInstance().delete(url);
                BookshelfManager.getInstance().deleteFile(GDownloader.getInstance().getFile(url));

//                if (BookshelfManager.getInstance().getBookById(bookId) != null) {
//                    File downloadFile = new File(BookshelfManager.getInstance().getBookById(bookId).getDownloadUrl());
//                    BookshelfManager.getInstance().deleteFile(downloadFile);
//                }
            }
        });

        DownloadEvent event = new DownloadEvent();
        event.event = DownloadEvent.EVENT_ERROR;
        event.state = DbDownloadExt.DOWNLOAD_STATE_ERROR;
        event.url = url;
        EventBus.getDefault().post(event);

    }
    //刷新网络状态
    private void refreshNetState() {
        Log.i("rere","rerere");
        UserModuleImpl.getInstance().getNetState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
//                        mNetState = integer;
                        Log.i("rere","rere"+integer);
                        UserModule.getInstance().setNetStateLocal(integer);
                        //2017 11 29 添加获取login_token
                        String login_token="";
                        UserInfo userInfo=null;
                        if(integer==UserModule.NET_STATE_FIRST_LEVEL){
                            login_token= book().read("TOKEN"+UserModule.NET_CENTER_FIRST);
                            userInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);
//                            Toast.makeText(getApplicationContext(),"integer=="+integer,Toast.LENGTH_SHORT).show();
                        }
                        if(integer==UserModule.NET_STATE_SECOND_LEVEL||integer==UserModule.NET_STATE_ALL){
                            login_token= book().read("TOKEN"+UserModule.NET_CENTER_SECOND);
                            userInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND);
                        }
//                        Toast.makeText(getApplicationContext(),"==user_id"+userInfo.getUserid(),Toast.LENGTH_SHORT).show();
                        updateExceedTime(login_token,userInfo.getUserid());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Ln.d("MyService:getNetState:error");

                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Ln.d("MyService:getNetState:ok");
                    }
                });
    }

    /**
     * 2017.11.30  ymd  传参为String时，需添加"'"+String+"'"
     */
    public void updateExceedTime(String login_token,int user_id){
        Retrofit retrofit=new Retrofit.Builder().baseUrl("http://192.168.4.253/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api=retrofit.create(Api.class);
        //2017.11.29 ymd  用户可借阅时间限制
        //app=book&controller=forTwo&action=getMemberBorrowPermissions    参数{login_token:(string)user_id:(int)}
        Call<ExceedTime> call=api.getTime("book","forTwo","getMemberBorrowPermissions",login_token,user_id);

        call.enqueue(new Callback<ExceedTime>() {
            @Override
            public void onResponse(Call<ExceedTime> call, Response<ExceedTime> response) {
                ExceedTime exceedTime=response.body();
//                Toast.makeText(getApplicationContext(),"kejieyue====:==="+exceedTime.getTime()+"+="+exceedTime.getUsername()+"=="+exceedTime.getUser_id(),Toast.LENGTH_SHORT).show();

                DatabaseManager.getInstance().updateExceedTime(exceedTime.getUser_id(),"'"+exceedTime.getUsername()+"'",exceedTime.getTime());

            }

            @Override
            public void onFailure(Call<ExceedTime> call, Throwable t) {
//                Toast.makeText(getApplicationContext(),"failure",Toast.LENGTH_SHORT).show();
            }
        });
    }
//    public void test() {
//        //uri = data.getData();
//        ContentResolver resolver = getContentResolver();
//
//        //ContentResolver对象的getType方法可返回形如content://的Uri的类型
//
//        //如果是一张图片，返回结果为image/jpeg或image/png等
//        String fileType = resolver.getType(uri);
//        if (fileType.startsWith("image"))//判断用户选择的是否为图片{
//        //根据返回的uri获取图片路径
//            Cursor cursor = resolver.query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
//        cursor.moveToFirst();
//        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//
////do  anything you want
//    }

}
