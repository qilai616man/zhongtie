package com.crphdm.dl2.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.crphdm.dl2.R;
import com.umeng.analytics.MobclickAgent;
/**
 * 广告页
 */
public class WebActivity extends AppCompatActivity {

    public static final String INTENT_POST_URL = "INTENT_POST_URL";
    private WebView mWebView;
    private LinearLayout mError;
    private Context context;
    private LinearLayout mLoading;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        url = getIntent().getStringExtra(INTENT_POST_URL);

        mWebView = (WebView) findViewById(R.id.webView);
        mError = (LinearLayout) findViewById(R.id.lLError);
        mLoading = (LinearLayout) findViewById(R.id.loading);

        init();

        mError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mWebView.setVisibility(View.VISIBLE);
                mError.setVisibility(View.GONE);
                mLoading.setVisibility(View.VISIBLE);

                init();
            }
        });
    }

    public void init() {

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new MyWebViewClient());

        if (testConnectivityManager()) {
            mWebView.setVisibility(View.VISIBLE);
            mError.setVisibility(View.GONE);
            mWebView.loadUrl(url);
        } else {
            mWebView.setVisibility(View.GONE);
            mError.setVisibility(View.VISIBLE);
            mLoading.setVisibility(View.GONE);
        }
    }
    //测试连接管理器
    public boolean testConnectivityManager() {
        ConnectivityManager connectivity = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.i("NetWorkState", "Unavailabel");
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        Log.i("NetWorkState", "Availabel");
                        return true;
                    }
                }
            }
        }
        return false;
    }
    //处理菜单被选中运行后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //我的web页面客户端
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("www.example.com")) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            mLoading.setVisibility(View.GONE);
            mError.setVisibility(View.VISIBLE);

        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mLoading.setVisibility(View.GONE);
        }
    }
    //按下屏幕时触发
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    //获取后退按钮事件
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    //恢复
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("广告页");
        MobclickAgent.onResume(this);
    }
    //暂停
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("广告页");
        MobclickAgent.onPause(this);
    }

}
