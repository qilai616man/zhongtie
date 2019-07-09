//package com.crphdm.dl2.service;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//
//import com.crphdm.dl2.activity.MainActivity;
//import com.crphdm.dl2.activity.WelcomeActivity;
//import com.crphdm.dl2.activity.login.LoginActivity;
//import com.crphdm.dl2.user.UserModule;
//import com.crphdm.dl2.user.obj.UserInfo;
//import com.crphdm.dl2.user.utils.MD5;
//import com.goyourfly.ln.Ln;
//
//import rx.android.schedulers.AndroidSchedulers;
//import rx.functions.Action1;
//import rx.schedulers.Schedulers;
//
//
//public class ServiceExhibition extends Service {
//    public static final String ACTIVITY_FROM = "ACTIVITY_FROM";
//    public static final int ACTIVITY_FROM_WELCOME_ACTIVITY = 1;
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");
////        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
////        final String username = "t200100001";
//        final String username = "t232100001";
//        String password = "123456";
//
//        final String md5Password = new MD5().md5(password);
//        UserModule.getInstance().login(username, md5Password, UserModule.USER_TYPE_EMAIL).subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<UserInfo>() {
//                    @Override
//                    public void call(UserInfo userInfo) {
//                        Intent intent = new Intent(ServiceExhibition.this, MainActivity.class);
//                        startActivity(intent);
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        String throwableString = String.valueOf(throwable);
//                        Ln.d("LoginActivity:UserInfo:throwableString:" + throwableString);
//                        if (throwableString.contains("java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $")) {
//                            UserModule.getInstance().loginOld(username, md5Password, UserModule.USER_TYPE_EMAIL)
//                                    .subscribeOn(Schedulers.newThread())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(new Action1<UserInfo>() {
//                                        @Override
//                                        public void call(UserInfo userInfo) {
//                                            Intent intent = new Intent(ServiceExhibition.this, MainActivity.class);
//                                            startActivity(intent);
//                                        }
//                                    });
//
//                        }
//                    }
//                });
//
//    }
//}
package com.crphdm.dl2.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.brainsoon.utils.BookUtils;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.UserModuleImpl;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.user.utils.MD5;
import com.crphdm.dl2.utils.Util;
import com.digital.dl2.business.core.manager.BookshelfManager;
import com.digital.dl2.business.core.manager.PublicManager;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ServiceExhibition extends Service implements DownloadHelper.DownloadListener {

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
                    Toast.makeText(ServiceExhibition.this, "文件解压成功", Toast.LENGTH_SHORT).show();

                    BookshelfManager.getInstance().updateBookByUnzipState(book_id, Constant.BOOK_UNZIP_STATE_SUCCESS);

                    break;
                case -1:
                    Ln.d("MyService:hadler:文件" + book_id + "解压失败");
                    Toast.makeText(ServiceExhibition.this, "文件解压失败", Toast.LENGTH_SHORT).show();

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

        mHandler = new Handler();

        GDownloader.getInstance().registerListener(this);

        Ln.d("MyService:onCreate");

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //90秒更新一下状态
                Ln.d("MyService:onCreate:刷新网络状态");
                refreshNetState();
            }
        }, 0, 1000 * 90);

//        final String username = "t200100001";
        final String username = "t232100001";
        String password = "123456";

        final String md5Password = new MD5().md5(password);
        UserModule.getInstance().login(username, md5Password, UserModule.USER_TYPE_EMAIL).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserInfo>() {
                    @Override
                    public void call(UserInfo userInfo) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        String throwableString = String.valueOf(throwable);
                        Ln.d("LoginActivity:UserInfo:throwableString:" + throwableString);
                        if (throwableString.contains("java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $")) {
                            UserModule.getInstance().loginOld(username, md5Password, UserModule.USER_TYPE_EMAIL)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<UserInfo>() {
                                        @Override
                                        public void call(UserInfo userInfo) {
                                        }
                                    });

                        }
                    }
                });

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
                        Toast.makeText(ServiceExhibition.this, "解压成功", Toast.LENGTH_SHORT).show();
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

                Toast.makeText(ServiceExhibition.this, "下载失败，请重新尝试", Toast.LENGTH_SHORT).show();



                GDownloader.getInstance().delete(url);
                BookshelfManager.getInstance().deleteFile(GDownloader.getInstance().getFile(url));


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
        UserModuleImpl.getInstance().getNetState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
//                        mNetState = integer;
                        UserModule.getInstance().setNetStateLocal(integer);
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

