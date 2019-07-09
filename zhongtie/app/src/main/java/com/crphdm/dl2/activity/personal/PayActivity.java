package com.crphdm.dl2.activity.personal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.utils.NetHelper;
import com.crphdm.dl2.utils.PayResult;
import com.crphdm.dl2.utils.SignUtils;
import com.google.gson.Gson;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 支付页面
 */

public class PayActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_PAY = 12;

    public static final String PARAMS_INT_ORDER_ID = "order_id";
    public static final String PARAMS_INT_ORDER_SN = "order_sn";
    public static final String PARAMS_STRING_NAME = "name";
    public static final String PARAMS_STRING_DESC = "desc";
    public static final String PARAMS_FLOAT_PRICE = "price";

    // 商户PID
    public static final String PARTNER = "2088611231743631";
    // 商户收款账号
    public static final String SELLER = "lijun@crphdm.com";

    public static final String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKOI03qEySgrn5PL//6M1oVWSNg76NwPGepQWpWneWD1SEV2Ssuf+6hvTTx2Ii8nEifmFDEbOHTs3erCedgOQgNsyIwkXtu4L5W/kvJak3RdNrPdYQxsgI+YTlMtOmaRgXzkSF+x9IonZSPaOCWIyb3RBwgDqDUYKdNXY4yCzHutAgMBAAECgYBQxyFO/5AtW37zjmzg28iLDFfnF5CqQntWlmJoUdVO41nTld7UPi4cwP2xw9iW2gHJ24hVyVBV0Tq831s4MbLBG+rGxTBX86AS72O4XOLvt5Ieh63FNGtWkfFFq8lAzTiQDFzy6bNLjmUwaWHpAyd3K1g+FCUnH0YUgJW5TDDisQJBANfJo93G/cCnabUDLF9qGN2VTagxI4uEoJvH8/Zdlnol4L15yds/5yZShiU8E3FdUXPbwNIKj5q/Vo+IjEUUOiMCQQDCAmWysA6F0E4U8pg0U50FU1iHRUElr2Q0nDOFknjsvQb/gwWC5O3lY3pCLDpJptyCf/JKKyS2J55aY+P6c8fvAkAWUun8+6Us1ST+cQGaLWO9KMEL8Wl3+ZSUCS+YAT6cDGcfswXLBOVo9YijOX/ZbooKrmVYVXddzwWpe7rnOpx5AkB6V+ip9jwyRln2+8TfupqacSSyqQmcKi6Wvqn+4Y9AOzPejVE8agFbVpASirF3ILKN2QGBpBNSi0lg1v09QyYhAkATJVuuEzyfC3pYJBZf37322tQ+oSnRS28pa+maLLCzQAwRxHUbSwEQQDOlyPMT3IYqfzSyn54w9n1xAaeNkLgM";

    // 异步通知页面路径
    public static final String NOTIFY_URL = "http://app.m.crphdm.com/alipayCallback";

    // 支付宝公钥
    public static final String RSA_PUBLIC = "";

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    private int mOrderId;
    private String mOrderSn;

    private PayTask alipay;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        final ProgressDialog dialog = ProgressDialog.show(PayActivity.this, null, "支付结果确认中...");
                        checkPayResult().observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(new Action1<Boolean>() {
                                    @Override
                                    public void call(Boolean aBoolean) {
                                        dialog.dismiss();
                                        if (aBoolean)
                                            setResult(RESULT_OK);
                                        else
                                            setResult(RESULT_CANCELED);
                                        finish();
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        dialog.dismiss();
                                        setResult(RESULT_CANCELED);
                                        finish();
                                    }
                                });
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            final ProgressDialog dialog = ProgressDialog.show(PayActivity.this, null, "支付结果确认中...");
                            checkPayResult().observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<Boolean>() {
                                        @Override
                                        public void call(Boolean aBoolean) {
                                            dialog.dismiss();
                                            if (aBoolean)
                                                setResult(RESULT_OK);
                                            else
                                                setResult(RESULT_CANCELED);
                                            finish();
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            dialog.dismiss();
                                            setResult(RESULT_CANCELED);
                                            finish();
                                        }
                                    });
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(PayActivity.this, "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }
    };

    private Observable<Boolean> checkPayResult() {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                StringBuilder stringBuilder = new StringBuilder("http://app.m.crphdm.com/?app=pay&controller=AlipayWAP&action=payStatus");
                stringBuilder.append("&login_token=").append(UserModule.getInstance().getToken(UserModule.NET_CENTER_FIRST));
                stringBuilder.append("&order_id=").append(mOrderId);

                Ln.d("PayActivity:checkPayResult:url:" + stringBuilder.toString());
                return Observable.just(stringBuilder.toString());
            }
        }).flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                return NetHelper.getData(s);
            }
        }).flatMap(new Func1<String, Observable<PayCheckResultObject>>() {
            @Override
            public Observable<PayCheckResultObject> call(String s) {
                PayCheckResultObject payCheckResultObject = new Gson().fromJson(s, PayCheckResultObject.class);
                return Observable.just(payCheckResultObject);
            }
        }).flatMap(new Func1<PayCheckResultObject, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(PayCheckResultObject payCheckResultObject) {
                if (payCheckResultObject.status) {
                    if (payCheckResultObject.pay_status == 1) {
                        return Observable.just(true);
                    }
                }
                return Observable.just(false);
            }
        });
    }

    public boolean isInstallZhiFuBao() {
        PackageManager manager = getPackageManager();
        List<PackageInfo> pkgList = manager.getInstalledPackages(0);
        for (int i = 0; i < pkgList.size(); i++) {
            PackageInfo pI = pkgList.get(i);
            if (pI.packageName.equalsIgnoreCase("com.eg.android.AlipayGphone"))
                return true;
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        String name = getIntent().getStringExtra(PARAMS_STRING_NAME);
        String desc = getIntent().getStringExtra(PARAMS_STRING_DESC);
        float price = getIntent().getFloatExtra(PARAMS_FLOAT_PRICE, -1);
        mOrderId = getIntent().getIntExtra(PARAMS_INT_ORDER_ID, -1);
        mOrderSn = getIntent().getStringExtra(PARAMS_INT_ORDER_SN);


        // 构造PayTask 对象
        alipay = new PayTask(PayActivity.this);

        if (!isInstallZhiFuBao()) {
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setMessage("请安装支付宝")
                    .setCancelable(false)
                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        } else

        {
            if (price <= 0) {
                Toast.makeText(this, "价格错误", Toast.LENGTH_SHORT).show();
                finish();
            }
            pay(name, desc, price);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    //支付
    public void pay(String orderName, String orderDesc, float price) {
        if (TextUtils.isEmpty(PARTNER)
                || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("需要配置PARTNER | SELLER")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface, int i) {
                                    //
                                    finish();
                                }
                            }).show();
            return;
        }
        // 订单
        String orderInfo = getOrderInfo(orderName, orderDesc, String.valueOf(price));

        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        if (sign == null) {
            Toast.makeText(PayActivity.this, "sign = null", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {

                // 调用支付接口，获取支付结果
                if (alipay != null) {
                    String result = alipay.pay(payInfo);

                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }else {

                }

            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     */
    public void check(View v) {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(PayActivity.this);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();

    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion(View v) {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * create the order info. 创建订单信息
     */
    public String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + NOTIFY_URL
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    public String getOutTradeNo() {
        return mOrderSn;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, PRIVATE_KEY);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }

    public class PayCheckResultObject {
        boolean status;//: (boolean)true/false,
        int error_code;//: (int)0:没有错误; 1:登陆超时或者未登录; 2:查询失败.
        String message;//: (string)消息，可能为空
        int pay_status;//: (int)0:未支付; 1:已支付; 2:支付失败，支付遇到问题。
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("商品支付页");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("商品支付页");
        MobclickAgent.onPause(this);
    }
}
