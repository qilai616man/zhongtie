package com.crphdm.dl2.activity.login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.WelcomeActivity;
import com.crphdm.dl2.api.Api;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.user.utils.MD5;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.ClearEditTextNew;
import com.digital.dl2.business.bean.Jsob;
import com.digital.dl2.business.bean.Userjson;
import com.digital.dl2.business.database.DatabaseManager;
import com.goyourfly.gdownloader.utils.ErrorUtils;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    public static final String ACTIVITY_FROM = "ACTIVITY_FROM";
    public static final int ACTIVITY_FROM_WELCOME_ACTIVITY = 1;
    public static final int ACTIVITY_FROM_MAIN_ACTIVITY = 2;

    private static final int ACCESS_COARSE_LOCATION_REQUEST_CODE = 1;
    private static final int READ_PHONE_STATE_REQUEST_CODE = 2;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 3;

    private boolean isAccessCoarseLocation = false;
    private boolean isReadPhoneState = false;
    private boolean isReadExternalStorage = false;

    @Bind(R.id.username)
    ClearEditTextNew username;
    @Bind(R.id.password)
    ClearEditTextNew password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        initPermissions();
    }

    private void initPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    READ_PHONE_STATE_REQUEST_CODE);
        } else {
            Ln.d("已有读取手机状态权限");
            isReadPhoneState = true;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_COARSE_LOCATION_REQUEST_CODE);
        } else {
            Ln.d("已有位置信息权限");
            isAccessCoarseLocation = true;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            Ln.d("已有存储空间权限");
            isReadExternalStorage = true;
        }
    }

    @OnClick(R.id.login)
    void onLoginClick() {
        getOnLogin();
    }

    @OnClick(R.id.bind)
    void onRegisterClick() {
        if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_FIRST_LEVEL ||
                UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        } else {
            Toast.makeText(LoginActivity.this, "亲爱的用户，看不到我？请用手机连接互联网试一试？", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.forget_password)
    void onFindPasswordClick() {
        if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_FIRST_LEVEL ||
                UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
            startActivity(new Intent(LoginActivity.this, FindPasswordActivity.class));
        } else {
            Toast.makeText(LoginActivity.this, "亲爱的用户，看不到我？请用手机连接互联网试一试？", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) {
            return;
        }

        if (requestCode == ACCESS_COARSE_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Ln.d("允许位置信息权限");
                isAccessCoarseLocation = true;
            } else {
                Ln.d("拒绝位置信息权限");
                isAccessCoarseLocation = false;
            }
        } else if (requestCode == READ_PHONE_STATE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Ln.d("允许读取手机状态权限");
                isReadPhoneState = true;
            } else {
                Ln.d("拒绝读取手机状态权限");
                isReadPhoneState = false;
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Ln.d("允许存储空间权限");
                isReadExternalStorage = true;
            } else {
                Ln.d("拒绝存储空间权限");
                isReadExternalStorage = false;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            moveTaskToBack(true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
//        super.onBackPressed();
    }

    public void onResume() {
        super.onResume();

//        Toast.makeText(this,"onresume",Toast.LENGTH_SHORT).show();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("登录页");

    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("登录页");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
    /**
     * ymd 2017.08.08
     * 上海发行分部部分图书t用户阅读时间限制为30分钟。
     * ymd 2017.11.01
     * 成都部分用户无借阅时限
     * */
    private void getOnLogin(){
        if (isAccessCoarseLocation && isReadPhoneState && isReadExternalStorage) {

            final String username = this.username.getText().toString();
            String password = this.password.getText().toString();

            if (username.equals("")) {
                Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.equals("")) {
                Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("登录中...");
            dialog.show();

            final String md5Password = new MD5().md5(password);
            UserModule.getInstance().login(username, md5Password, UserModule.USER_TYPE_EMAIL).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<UserInfo>() {
                        @Override
                        public void call(UserInfo userInfo) {
                            dialog.dismiss();
                            Ln.d("LoginActivity:UserInfo:userInfo:" + userInfo);

                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);

                            setResult(RESULT_OK, intent);

                            Retrofit retrofit=new Retrofit.Builder().baseUrl("http://192.168.4.253/")
                                    .addConverterFactory(ScalarsConverterFactory.create())
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            Api api=retrofit.create(Api.class);
                            //成都无借阅期限用户接口
                            Call<Userjson> call=api.getList("book","forTwo","getInfiniteBorrow");

                            call.enqueue(new Callback<Userjson>() {
                                @Override
                                public void onResponse(Call<Userjson> call, Response<Userjson> response) {
                                    Userjson userjson=response.body();
                                    DatabaseManager.getInstance().insertUserJson(userjson.getData());
                                }

                                @Override
                                public void onFailure(Call<Userjson> call, Throwable t) {
                                }
                            });
                            //上海发行分部部分xml图书30分钟到期接口
                            if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY) {
                                Call<Jsob> resultCall = api.getResult("book", "forTwo", "getReadTime");
                                resultCall.enqueue(new Callback<Jsob>() {
                                    @Override
                                    public void onResponse(Call<Jsob> call, Response<Jsob> response) {
                                        Jsob jsob = response.body();
                                        DatabaseManager.getInstance().deletejsob();
                                        DatabaseManager.getInstance().insertBookType(jsob.getTime(),jsob.getData());
                                    }

                                    @Override
                                    public void onFailure(Call<Jsob> call, Throwable t) {
                                    }
                                });
                            }
                            finish();

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            String throwableString = String.valueOf(throwable);
                            Ln.d("LoginActivity:UserInfo:throwableString:" + throwableString);
                            if (throwableString.contains("java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $")){
                                UserModule.getInstance().loginOld(username, md5Password, UserModule.USER_TYPE_EMAIL)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<UserInfo>() {
                                            @Override
                                            public void call(UserInfo userInfo) {
                                                dialog.dismiss();
                                                Ln.d("LoginActivity:UserInfo:userInfo1:" + userInfo);

                                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);

                                                setResult(RESULT_OK, intent);
                                                Retrofit retrofit=new Retrofit.Builder().baseUrl("http://192.168.4.253/")
                                                        .addConverterFactory(ScalarsConverterFactory.create())
                                                        .addConverterFactory(GsonConverterFactory.create())
                                                        .build();
                                                Api api=retrofit.create(Api.class);
                                                //成都无借阅期限用户接口
                                                Call<Userjson> call=api.getList("book","forTwo","getInfiniteBorrow");
                                                call.enqueue(new Callback<Userjson>() {
                                                    @Override
                                                    public void onResponse(Call<Userjson> call, Response<Userjson> response) {
                                                        Userjson userjson=response.body();
                                                        DatabaseManager.getInstance().insertUserJson(userjson.getData());
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Userjson> call, Throwable t) {
                                                    }
                                                });
                                                //上海发行分部部分xml图书30分钟到期接口
                                                if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY) {
                                                    Call<Jsob> resultCall = api.getResult("book", "forTwo", "getReadTime");
                                                    resultCall.enqueue(new Callback<Jsob>() {
                                                        @Override
                                                        public void onResponse(Call<Jsob> call, Response<Jsob> response) {
                                                            Jsob jsob = response.body();
                                                            DatabaseManager.getInstance().deletejsob();
                                                            DatabaseManager.getInstance().insertBookType(jsob.getTime(),jsob.getData());
                                                        }

                                                        @Override
                                                        public void onFailure(Call<Jsob> call, Throwable t) {
                                                        }
                                                    });
                                                }
                                                finish();

                                            }
                                        });

                            }else{
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, "登录失败 " + ErrorUtils.getError(throwable), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            initPermissions();
        }

    }

}
