package com.crphdm.dl2.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.crphdm.dl2.activity.MainActivity;
import com.crphdm.dl2.user.UserModule;
import com.digital.dl2.business.core.manager.BookshelfManager;
import com.digital.dl2.business.core.manager.CloudBookstoreManager;
import com.digital.dl2.business.core.manager.LibraryManager;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.manager.PublicManager;
import com.digital.dl2.business.core.manager.SelectManager;
import com.digital.dl2.business.core.manager.ShoppingManager;
import com.digital.dl2.business.database.DatabaseManager;
import com.digital.dl2.business.util.Constant;
import com.goyourfly.gdownloader.GDownloader;
import com.goyourfly.gdownloader.utils.Ln;
import com.jiuwei.upgrade_package_new.lib.UpgradeModule;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.acra.ACRA;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;

import java.lang.reflect.Field;


import io.paperdb.Paper;

/**
 *
 */
//@ReportsCrashes(
//        mailTo = "yuyx@brainsoon.com",
//        mode = ReportingInteractionMode.TOAST,
//        resToastText = R.string.crash_toast_text)
public class MyApplication extends ZLAndroidApplication {
    //    private static final String PATH = Environment.getExternalStorageDirectory().getAbsoluteFile() +
//            File.separator +
//            "crphdm" +
//            File.separator +
//            "download" +
//            File.separator;
    private long mBroadcastReceiverOnceTime;
     private String login_token="";
    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);

        MultiDex.install(this);

        UserModule.init(this);
        BookshelfManager.init(this);
        CloudBookstoreManager.init(this);
        LibraryManager.init(this);
        PersonalCenterManager.init(this);
        PublicManager.init(this);
        ShoppingManager.init(this);
        UpgradeModule.init(this);
        SelectManager.init(this);
        DatabaseManager.getInstance().init(this);

        GDownloader.init(this,
                Constant.FILE_DOWNLOAD_PATH,
                2, null);

        Paper.init(this);

//        JPushInterface.init(this);            // 初始化 JPush
//        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志

//        new ConfigShadow(this);
//        new ZLAndroidImageManager();
//        new ZLAndroidLibrary(this);

        displayMenuKey();
        initImageLoader(getApplicationContext());
        registerAlarmServiceReceiver();
    }
    /**
     * 显示菜单键
     * 部分机器界面上无法显示出右上角的菜单键，此段代码可将其在右上角显示出来
     */
    public void displayMenuKey() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .defaultDisplayImageOptions(options)
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    private void registerAlarmServiceReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_TOKEN_ERROR);
        intentFilter.addAction(Constant.ACTION_TOKEN_EXPIRED);
        intentFilter.addAction(Constant.ACTION_CLEAN_DATA);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Ln.d("MyApplication:broadcastReceiver");
            String action = intent.getAction();
            String error_message = intent.getStringExtra(Constant.INTENT_ERROR_MESSAGE);

            long twiceTime = System.currentTimeMillis();

            if (twiceTime - mBroadcastReceiverOnceTime > 2000) {
                if (Constant.ACTION_TOKEN_ERROR.equals(action)) {//账号已经登录
                    Toast.makeText(MyApplication.this, error_message, Toast.LENGTH_SHORT).show();
                    UserModule.getInstance().saveLoginState(false);

                    Intent intent1 = new Intent(MyApplication.this, MainActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                } else if (Constant.ACTION_TOKEN_EXPIRED.equals(action)) {//token 过期
                    Toast.makeText(MyApplication.this, error_message, Toast.LENGTH_SHORT).show();
                    UserModule.getInstance().saveLoginState(false);
                }
                mBroadcastReceiverOnceTime = twiceTime;
            }

            if (Constant.ACTION_CLEAN_DATA.equals(action)) {//清楚数据
                PublicManager.getInstance().clearData();
                BookshelfManager.getInstance().clearData();
                GDownloader.getInstance().clearAll();
            }
        }
    };

}
