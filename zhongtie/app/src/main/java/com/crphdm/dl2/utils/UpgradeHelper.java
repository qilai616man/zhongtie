package com.crphdm.dl2.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.goyourfly.gdownloader.utils.Ln;
import com.jiuwei.upgrade_package_new.lib.Constant;
import com.jiuwei.upgrade_package_new.lib.UpgradeModule;
import com.jiuwei.upgrade_package_new.lib.obj.KbAppResult;

import java.text.SimpleDateFormat;

/**
 * Modify by songzixuan on 19/07/04.
 * 版本升级
 */
public class UpgradeHelper {
    private static boolean downloadFileIsSuccess = false;
    private static String mPackageName = "com.crphdm.dl2";
    private static String mUrl = "http://222.35.26.200:7090/djcp/product/upgrade.action";
            //检查升级
    public static void checkUpgrade(final Activity activity, final boolean silence, final int dialogStyle) {
        UpgradeModule.getInstance().autoInstall(true)
                .autoInstall(true)
                .packageName(mPackageName)
//                .packageName("com.huaer.mooc")
                .url(mUrl)
//                .url("http://115.28.43.225:8080/d/device/GetApplicationInfo.json")
                .checkNewVersion(new UpgradeModule.OnCheckListener() {

                   //检查开始
                    @Override
                    public void checkStart() {

                    }
                    //检查错误
                    @Override
                    public void checkError(String s) {
                        if (!silence)
                            Toast.makeText(activity, "检查版本出错", Toast.LENGTH_SHORT).show();
                    }
                    //新版本
                    @Override
                    public void newVersion(final KbAppResult kbAppObject) {
                        switch (dialogStyle) {
                            case Constant.DIALOG_STYLE_SYSTEM:
                                showUpgradeDialogToSystem(activity, kbAppObject);
                                break;
                            case Constant.DIALOG_STYLE_ELDERLY_ASSISTANT:
                                showUpgradeDialogToElderlyAssistant(activity, kbAppObject);
                                break;
                            case Constant.DIALOG_STYLE_MOMMY_ASSISTANT:
                                break;
                            case Constant.DIALOG_STYLE_TRANSLATION_STUDIES_MUSEUM:
                                break;
                            default:
                                showUpgradeDialogToSystem(activity, kbAppObject);
                                break;
                        }
                    }
                            //没有新版本
                    @Override
                    public void noNewVersion(KbAppResult kbAppObject) {
                        if (!silence)
                            Toast.makeText(activity, "没有新版本", Toast.LENGTH_SHORT).show();
                    }
                });
    }
                        //下载
    private static void download(KbAppResult kbAppObject) {
        UpgradeModule.getInstance().download(kbAppObject, true,
                new UpgradeModule.OnDownloadListener() {
                    //下载开始
                    @Override
                    public void downloadStart() {
//                        Toast.makeText(getClass(), "开始下载", Toast.LENGTH_SHORT).show();
                    }
                    //有下载
                    @Override
                    public void haveDownloading() {

                    }
                    //下载成功
                    @Override
                    public void downloadSuccess(String s) {
                        Ln.d("UpgradeHelper:downloadSuccess");
                        UpgradeModule.getInstance().installFile();
                    }
                    //下载失败
                    @Override
                    public void downloadError(String s) {

                    }
                });
    }
            //通过Wifi检查升级
    public static void checkUpgradeByWifi(final Activity activity, final boolean silence, final int dialogStyle) {
        if (getNetWorkType(activity) == Constant.NETWORKTYPE_WIFI) {
            Ln.d("UpgradeHelper:wifi");
            UpgradeModule.getInstance()
                    .autoInstall(true)
                    .packageName(mPackageName)
                    .url(mUrl)
                    .checkNewVersion(new UpgradeModule.OnCheckListener() {
                        //检查开始
                        @Override
                        public void checkStart() {

                        }
                        //检查失败
                        @Override
                        public void checkError(String s) {
                            if (!silence)
                                Toast.makeText(activity, "检查版本出错", Toast.LENGTH_SHORT).show();
                        }
                        //新版本
                        @Override
                        public void newVersion(final KbAppResult kbAppObject) {
                            Ln.d("UpgradeHelper:kbAppObject.getPackageName：" + kbAppObject.getVersionName());
                            Toast.makeText(activity, "有新版本", Toast.LENGTH_SHORT).show();
                            UpgradeModule.getInstance().download(kbAppObject, false,
                                    new UpgradeModule.OnDownloadListener() {
                                        //开始下载
                                        @Override
                                        public void downloadStart() {

                                        }
                                        //有下载
                                        @Override
                                        public void haveDownloading() {

                                        }
                                        //下载成功
                                        @Override
                                        public void downloadSuccess(String s) {
                                            Ln.d("UpgradeHelper:downloadSuccess");

                                            switch (dialogStyle) {
                                                case Constant.DIALOG_STYLE_SYSTEM:
                                                    showUpgradeDialogToSystem(activity, kbAppObject);
                                                    break;
                                                case Constant.DIALOG_STYLE_ELDERLY_ASSISTANT:
                                                    showUpgradeDialogToElderlyAssistant(activity, kbAppObject);
                                                    break;
                                                case Constant.DIALOG_STYLE_MOMMY_ASSISTANT:
                                                    break;
                                                case Constant.DIALOG_STYLE_TRANSLATION_STUDIES_MUSEUM:
                                                    break;
                                                default:
                                                    showUpgradeDialogToSystem(activity, kbAppObject);
                                                    break;
                                            }
                                        }
                                        //下载失败
                                        @Override
                                        public void downloadError(String s) {

                                        }
                                    });
                        }
                        //没有新版本
                        @Override
                        public void noNewVersion(KbAppResult kbAppObject) {
                            if (!silence)
                                Toast.makeText(activity, "没有新版本", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Ln.d("UpgradeHelper:other");
        }
    }
    //显示升级对话框到系统
    private static void showUpgradeDialogToSystem(Activity activity, KbAppResult kbAppObject) {
        final StringBuffer desc = new StringBuffer(
                Html.fromHtml(kbAppObject.getVersionDescription()))
                .append("\n版本：" + kbAppObject.getVersionName())
                .append("\n大小：" + kbAppObject.getFileSize() / 1024 + "KB")
                .append("\n发布时间：" +
                        new SimpleDateFormat("yyyy年MM月dd日 HH:mm")
                                .format(kbAppObject.getPublishData() * 1000));

        new AlertDialog.Builder(activity).setTitle("检查升级")
                .setMessage(desc)
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpgradeModule.getInstance().installFile();
                    }
                })
                .setNegativeButton("取消", null).show();

    }
                //显示升级对话
    private static void showUpgradeDialogToElderlyAssistant(Activity activity, final KbAppResult kbAppObject) {
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        Window window = dialog.getWindow();
        dialog.show();

        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        window.setContentView(com.jiuwei.upgrade_package_new.lib.R.layout.dlg_upgrade_elderly_assistant);

        TextView newVersionContext = (TextView) window.findViewById(R.id.tv_1_upgrade_version_context);
        TextView upgradeContent = (TextView) window.findViewById(R.id.tv_1_upgrade_message);
        Button ok = (Button) window.findViewById(R.id.btn_dlg_set_time_custom_ok);
        Button cancel = (Button) window.findViewById(R.id.btn_dlg_set_time_custom_cancel);

        final StringBuffer desc = new StringBuffer(
                "版本：v" + kbAppObject.getVersionName())
                .append("\n大小：" + kbAppObject.getFileSize() / 1024 + "KB")
                .append("\n发布时间：" +
                        new SimpleDateFormat("yyyy年MM月dd日 HH:mm")
                                .format(kbAppObject.getPublishData() * 1000));
        newVersionContext.setText(desc);
        upgradeContent.setText(Html.fromHtml(kbAppObject.getVersionDescription()));
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ln.d("UpgradeHelper:getVersionCode：" + kbAppObject.getVersionCode());
                if (UpgradeModule.getInstance().getFileIsAlreadyExists(kbAppObject.getVersionCode())) {
                    UpgradeModule.getInstance().installFile();
                } else {
                    download(kbAppObject);
                }
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 获取网络状态，wifi,wap,2g,3g.
     *
     * @param context 上下文
     * @return int 网络状态
     */
//    {@link #Constant.NETWORKTYPE_2G},
//    {@link #NETWORKTYPE_3G},
//    {@link #NETWORKTYPE_INVALID},
//    {@link #NETWORKTYPE_WAP}* <p>
//    {@link #Constant.NETWORKTYPE_WIFI}
    public static int getNetWorkType(Context context) {
        int mNetWorkType = 0;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = Constant.NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                mNetWorkType = TextUtils.isEmpty(proxyHost)
                        ? (isFastMobileNetwork(context) ? Constant.NETWORKTYPE_3G : Constant.NETWORKTYPE_2G)
                        : Constant.NETWORKTYPE_WAP;
            }
        } else {
            mNetWorkType = Constant.NETWORKTYPE_INVALID;
        }
        return mNetWorkType;
    }

    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }
}
