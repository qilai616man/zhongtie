package com.jiuwei.upgrade_package_new.lib;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.goyourfly.gdownloader.utils.Ln;
import com.jiuwei.upgrade_package_new.lib.obj.KbAppResult;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by gaoyf on 15/6/13.
 */
public class UpgradeModule {

    public interface OnCheckListener {
        void checkStart();

        void checkError(String err);

        void newVersion(KbAppResult kbAppObject);

        void noNewVersion(KbAppResult kbAppObject);
    }

    public interface OnDownloadListener {
        void downloadStart();

        void haveDownloading();

        void downloadSuccess(String path);

        void downloadError(String err);
    }

    private static UpgradeModule mModule;
    protected Context mContext;
    protected String mUrl;
    protected String mPackageName;
    protected long mDownloadId;
    protected int mVersionCode;
    protected String mPath = "/Download/";
    protected DownloadManager mDownloadManager;
    protected boolean mAutoInstall = true;
    protected OnDownloadListener mDownloadListener;
    protected Handler mHandler;
    protected File mFile;

    private UpgradeModule(Context context) {
        mContext = context;
        mHandler = new Handler(context.getMainLooper());
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static UpgradeModule init(Context context) {
        if (mModule == null)
            mModule = new UpgradeModule(context);
        return mModule;
    }

    public static UpgradeModule getInstance() {
        if (mModule == null)
            throw new NullPointerException("Call init before getInstance");
        return mModule;
    }

    public UpgradeModule url(String url) {
        mUrl = url;
        return this;
    }

    public UpgradeModule packageName(String packageName) {
        mPackageName = packageName;
        return this;
    }

    public UpgradeModule downloadPath(String path) {
        mPath = path;
        return this;
    }

    public UpgradeModule autoInstall(boolean autoInstall) {
        mAutoInstall = autoInstall;
        return this;
    }

    /**
     * 检查新版本接口
     *
     * @param listener
     */
    public void checkNewVersion(final OnCheckListener listener) {
        listener.checkStart();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(mUrl);
//        stringBuffer.append("?");
//        stringBuffer.append("packageName").append("=").append(mPackageName);
        Ln.d("UpgradeModule:RequestUrl:" + stringBuffer.toString());
        AsyncHttpGet get = null;
        try {
            get = new AsyncHttpGet(URLDecoder.decode(URLEncoder.encode(stringBuffer.toString(), "UTF-8"), "UTF-8"));
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.checkError(e.getMessage());
                }
            });
        }

//        Observable.just(stringBuffer.toString())
//                .flatMap(new Func1<String, Observable<String>>() {
//                    @Override
//                    public Observable<String> call(String s) {
//                        return NetHelper.getData(s);
//                    }
//                }).flatMap(new Func1<String, Observable<Boolean>>() {
//                    @Override
//                    public Observable<Boolean> call(String s) {
//                        Ln.d("UpgradeModule:CheckResult:result:" + s);
//
//                        try {
//                            final KbAppResult kbObject = new Gson().fromJson(s, KbAppResult.class);
//                            if (kbObject != null) {
//                                int currentCode = getVersionCode();
//
//                                Ln.d("UpgradeModule:checkNewVersion:currentCode:" + currentCode);
//                                Ln.d("UpgradeModule:checkNewVersion:kbAppObject.version:" + kbObject.getVersionCode());
//                                //当前版本号小于网上版本号
//                                if (currentCode < kbObject.getVersionCode()) {
//                                    mHandler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            listener.newVersion(kbObject);
//                                        }
//                                    });
//                                } else {
//                                    mHandler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            listener.noNewVersion(kbObject);
//                                        }
//                                    });
//                                }
//                            } else {
//                                mHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        listener.checkError("出错");
//                                    }
//                                });
//                            }
//                        } catch (final Exception e1) {
//                            e1.printStackTrace();
//                            mHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    listener.checkError(e1.getMessage());
//                                }
//                            });
//                        }
//
//                        return null;
//                    }
//                });

        AsyncHttpClient.getDefaultInstance().executeString(get, new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception e, AsyncHttpResponse source, String result) {
                Ln.d("UpgradeModule:CheckResult:" + result);
                try {
                    final KbAppResult kbObject = new Gson().fromJson(result, KbAppResult.class);
                    Ln.d("UpgradeModule:KbAppResult:" + kbObject);
                    if (kbObject != null) {
                        int currentCode = getVersionCode();

                        Ln.d("UpgradeModule:checkNewVersion:currentCode:" + currentCode);
                        Ln.d("UpgradeModule:checkNewVersion:kbAppObject.version:" + kbObject.getVersionCode());
                        //当前版本号小于网上版本号
                        if (currentCode < kbObject.getVersionCode()) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.newVersion(kbObject);
                                }
                            });
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.noNewVersion(kbObject);
                                }
                            });
                        }
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.checkError("出错");
//                                listener.noNewVersion(kbObject);
                            }
                        });
                    }
                } catch (final Exception e1) {
                    e1.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.checkError(e1.getMessage());
                        }
                    });
                }
            }
        });
    }

    protected int getVersionCode() throws PackageManager.NameNotFoundException {
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(mPackageName, 0);
        Ln.d("UpgradeModule:getVersionCode:packageInfo:" + packInfo.toString());
        return packInfo.versionCode;
    }

    /**
     * 如果服务器不支持中文路径的情况下需要转换url的编码。
     * @param string
     * @return
     */
    private String encodeGB(String string) {
        //转换中文编码
        String split[] = string.split("/");
        for (int i = 1; i < split.length; i++) {
            try {
                split[i] = URLEncoder.encode(split[i], "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            split[0] = split[0] + "/" + split[i];
        }
        split[0] = split[0].replaceAll("\\+", "%20");//处理空格
        return split[0];
    }

    protected void downloadFile(String url, String name, boolean isNotificationVisibility) {
//        String mName = name + ".apk";
        String mName = name;
        int status = downloadState();

        Ln.d("UpgradeModule:downloadFile:status:" + status);
        if (status == DownloadManager.STATUS_RUNNING || status == DownloadManager.STATUS_PENDING) {
            if (mDownloadListener != null)
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDownloadListener.haveDownloading();
                    }
                });
            return;
        }

        if (status == DownloadManager.STATUS_SUCCESSFUL) {
//            openFile(mDownloadManager.getUriForDownloadedFile(mDownloadId));
            if (mDownloadListener != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mDownloadManager == null){
                            Ln.d("UpgradeModule:downloadFile:mDownloadManager == null");
                        }else {
                            Ln.d("UpgradeModule:downloadFile:mDownloadManager != null");
                        }

                        Ln.d("UpgradeModule:downloadFile:mDownloadId:" + mDownloadId);
//                        mDownloadManager.getUriForDownloadedFile(mDownloadId).getPath()
                        mDownloadListener.downloadSuccess("下载成功");
                    }
                });
            }
            return;
        }

        if (mDownloadListener != null)
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDownloadListener.downloadStart();
                }
            });

        //开始下载
        Uri resource = Uri.parse(encodeGB(url));
        DownloadManager.Request request = new DownloadManager.Request(resource);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        //设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        //在通知栏中显示
        if (isNotificationVisibility) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        } else {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        }
        request.setVisibleInDownloadsUi(false);
        //sdcard的目录下的download文件夹
        request.setDestinationInExternalPublicDir(mPath, mName);
        request.setTitle("正在下载：" + mName);
        Ln.d("UpgradeModule:downloadFile:正在下载Filename:" + mName);
        mDownloadId = mDownloadManager.enqueue(request);
        Ln.d("UpgradeModule:downloadFile:mDownloadId:" + mDownloadId);
        mContext.registerReceiver(mDownloadFileReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public boolean getFileIsAlreadyExists(int newFileVersionCode) {
        Ln.d("UpgradeModule:getFileIsDownloadSuccess:newFileVersionCode:" + newFileVersionCode);
        String sdCardPath =
                Environment.getExternalStorageDirectory().toString();

        File mfile = new File(sdCardPath + "/" + mPath);
        File[] files = mfile.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            Ln.d("UpgradeModule:getFileIsDownloadSuccess:File:" + file.getName());
            Ln.d("UpgradeModule:getFileIsDownloadSuccess:mPackageName:" + mPackageName);

            PackageInfo info = getFileVersionCode(file.getPath());
            if(info != null){
                Ln.d("UpgradeModule:getFileIsDownloadSuccess:PackageInfo:" + info.toString());
                if (mVersionCode == info.versionCode && mPackageName.equals(info.packageName) ) {
                    mFile = file;
                    return true;
                }
            }
        }
        return false;
    }

    private PackageInfo getFileVersionCode(String archiveFilePath) {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
           return info;
//            ApplicationInfo appInfo = info.applicationInfo;
//            String appName = pm.getApplicationLabel(appInfo).toString();
//            String packageName = appInfo.packageName;  //得到安装包名称
//            String version = info.versionName;       //得到版本信息
//            Drawable icon = pm.getApplicationIcon(appInfo);//得到图标信息
//            TextView tv = (TextView)findViewById(R.id.tv); //显示图标
//            tv.setBackgroundDrawable(icon);

//            Ln.d("UpgradeModule:getFileVersionCode:appName:" + appName);
//            Ln.d("UpgradeModule:getFileVersionCode:packageName:" + packageName);
//            Ln.d("UpgradeModule:getFileVersionCode:version:" + version);
//            Ln.d("UpgradeModule:getFileVersionCode:versionCode:" + versionCode);
//            Ln.d("UpgradeModule:getFileVersionCode:-------------------------------------");
        } else {
            Ln.d("UpgradeModule:getFileVersionCode:info == null");
        }

        return null;
    }

    /**
     * 下载接口
     * @param kbAppObject
     * @param isNotificationVisibility
     * @param downloadListener
     */
    public void download(KbAppResult kbAppObject, boolean isNotificationVisibility, OnDownloadListener downloadListener) {
        mDownloadListener = downloadListener;
        mVersionCode = kbAppObject.getVersionCode();

        String name = kbAppObject.getName() + ".apk";
        downloadFile(kbAppObject.getPackageUrl(), name, isNotificationVisibility);
    }

    public int downloadState() {
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor c = mDownloadManager.query(query);
        while (c.moveToNext()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            Ln.d("UpgradeModule:downloadFile:status:" + status);
            long id = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID));

            Ln.d("UpgradeModule:downloadFile:mDownloadId:" + mDownloadId);
            Ln.d("UpgradeModule:downloadFile:id:" + id);
            Ln.d("UpgradeModule:downloadFile:是否已存在:" + getFileIsAlreadyExists(mVersionCode));

            if (getFileIsAlreadyExists(mVersionCode)) {
                return DownloadManager.STATUS_SUCCESSFUL;
            }
        }
        return DownloadManager.STATUS_FAILED;
    }

    protected BroadcastReceiver mDownloadFileReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //这里可以取得下载的id，这样就可以知道哪个文件下载完成了。适用与多个下载任务的监听
            Ln.d("DownloadFileReceiver,DownloadId:" + intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0) + ",MyId:" + mDownloadId);
            if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0) == mDownloadId) {
                queryDownloadStatus();
            }
        }
    };

    protected void queryDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor c = mDownloadManager.query(query);
        while (c.moveToNext()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            long id = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID));
            if (id == mDownloadId) {
                switch (status) {
                    case DownloadManager.STATUS_SUCCESSFUL:
                        if (mDownloadListener != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mDownloadListener.downloadSuccess(mDownloadManager.getUriForDownloadedFile(mDownloadId).getPath());
                                }
                            });
                        }
                        //完成
//                        if (mAutoInstall) {
//                            Uri downloadFileUri = mDownloadManager.getUriForDownloadedFile(mDownloadId);
//                            openFile(downloadFileUri);
//                        }

                        break;
                    case DownloadManager.STATUS_FAILED:
                        if (mDownloadListener != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mDownloadListener.downloadError("下载出错");
                                }
                            });
                        }
                        //清除已下载的内容，重新下载
                        mDownloadManager.remove(mDownloadId);
                        break;
                }
                break;
            }
        }
    }

    public void installFile() {
//        Uri downloadFileUri = mDownloadManager.getUriForDownloadedFile(mDownloadId);
        Uri downloadFileUri1 = Uri.fromFile(mFile);
        openFile(downloadFileUri1);
    }

    protected void openFile(Uri uri) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(install);
    }
}
