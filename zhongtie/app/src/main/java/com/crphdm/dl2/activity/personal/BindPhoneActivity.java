package com.crphdm.dl2.activity.personal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.DelayHelper;
import com.crphdm.dl2.views.ClearEditTextNew;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.obj.PgResult;
import com.goyourfly.gdownloader.utils.ErrorUtils;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class BindPhoneActivity extends AppCompatActivity {
    //绑定手机号
    public static final int REQUEST_CODE = 213;
    public static final String INTENT_PHONE = "phone";
    @Bind(R.id.username)
    ClearEditTextNew username;
    @Bind(R.id.verify_code)
    ClearEditTextNew verifyCode;
    @Bind(R.id.get_verify_code)
    Button getVerifyCode;

    private String mPhone;
    private String mVerifyCode;
    private String mToken;
    private UserInfo mUserInfo;

    private ProgressDialog mProgressDialog;

    @OnClick(R.id.bind)
    public void bind() {
        if(mPhone == null || mPhone.equals("")){
            showDialog("手机号不能为空");
//            Toast.makeText(BindPhoneActivity.this,"手机号不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mVerifyCode == null ||mVerifyCode.equals("")){
            showDialog("验证码不能为空");
//            Toast.makeText(BindPhoneActivity.this,"验证码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(mProgressDialog == null){
            mProgressDialog = ProgressDialog.show(BindPhoneActivity.this,null,"绑定中...");
        }

        Ln.d("BindPhoneActivity:bind");
        PersonalCenterManager.getInstance().bindingMobileNumber(mUserInfo.getUserid(), mToken, 1, mPhone, mVerifyCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<PgResult>() {
                    @Override
                    public void call(PgResult result) {
                        Ln.d("BindPhoneActivity:bindingMobileNumber:result:" + result.toString());
                        if (result.isStatus()) {
                            Toast.makeText(BindPhoneActivity.this, "申请成功", Toast.LENGTH_SHORT).show();
                            UserModule.getInstance().updateUserInfoByState(1,mPhone,"");

                            bindSuccess();
                        }else {
//                            showDialog(result.getMessage());
                            Toast.makeText(BindPhoneActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if(mProgressDialog != null && mProgressDialog.isShowing()){
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }

                        Ln.d("BindPhoneActivity:bindingMobileNumber:error:" + throwable.getMessage());
                        throwable.printStackTrace();
//                        showDialog(throwable.getMessage());
                        Toast.makeText(BindPhoneActivity.this, ErrorUtils.getError(throwable.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Ln.d("BindPhoneActivity:bindingMobileNumber:ok");
                        if(mProgressDialog != null && mProgressDialog.isShowing()){
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }

                    }
                });
    }

    @OnClick(R.id.get_verify_code)
    void onGetVerifyCodeClick() {
        if(mPhone == null || mPhone.equals("")){
            showDialog("手机号不能为空");
//            Toast.makeText(BindPhoneActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        new DelayHelper().delayButton(getVerifyCode);

        UserModule.getInstance().getVerifyCode(mPhone, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<com.crphdm.dl2.user.obj.PgResult>() {
                    @Override
                    public void call(com.crphdm.dl2.user.obj.PgResult result) {
                        if (result.isStatus()) {
                            Toast.makeText(BindPhoneActivity.this, "正在发送验证码。。。", Toast.LENGTH_SHORT).show();
                        } else {
                            showDialog("发送验证码失败");
//                            Toast.makeText(BindPhoneActivity.this, "发送验证码失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    private void showDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

//        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
        builder.create();
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initMembers();
    }

    private void initMembers(){
        Drawable icon = getResources().getDrawable(R.drawable.selector_phone);
        if(icon != null){
            icon.setBounds(0,0,icon.getMinimumWidth(),icon.getMinimumHeight());
            username.setCompoundDrawables(icon,null,null,null);
        }

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);

        if(mProgressDialog == null){
            mProgressDialog = ProgressDialog.show(BindPhoneActivity.this,null,"加载中...");
        }

        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mToken = s;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if(mProgressDialog != null && mProgressDialog.isShowing()){
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if(mProgressDialog != null && mProgressDialog.isShowing()){
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                    }
                });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPhone = s.toString();
            }
        });

        verifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mVerifyCode = s.toString();
            }
        });
    }

    private void bindSuccess(){
        Intent intent = new Intent();
        intent.putExtra(INTENT_PHONE, username.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("绑定手机号页");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("绑定手机号页");
        MobclickAgent.onPause(this);
    }
}
