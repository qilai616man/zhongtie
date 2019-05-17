package com.crphdm.dl2.fragments.login;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.PgResult;
import com.crphdm.dl2.user.utils.MD5;
import com.crphdm.dl2.utils.DelayHelper;
import com.crphdm.dl2.utils.Util;
import com.crphdm.dl2.views.ClearEditTextNew;
import com.goyourfly.gdownloader.utils.ErrorUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PhoneRegisterFragment extends Fragment {
    @Bind(R.id.username)
    ClearEditTextNew username;
    @Bind(R.id.verify_code)
    ClearEditTextNew verifyCode;
    @Bind(R.id.get_verify_code)
    Button getVerifyCode;
    @Bind(R.id.password)
    ClearEditTextNew password;
    @Bind(R.id.cb_register_deal)
    CheckBox mCheckBox;

    private boolean dealIsOk = true;

    private String mUserName;
    private String mVerifyCode;

    @OnClick(R.id.get_verify_code)
    void onGetCodeClick() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("获取验证码中...");
        dialog.show();

        getVerifyCode.setEnabled(false);
        String username = this.username.getText().toString();
        UserModule.getInstance().getVerifyCode(username, UserModule.USER_TYPE_PHONE)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<PgResult>() {
                    @Override
                    public void call(PgResult result) {
                        dialog.dismiss();
                        if (result.isStatus()) {
                            delayBtn();
                            Toast.makeText(getActivity(), "验证码发送成功", Toast.LENGTH_LONG).show();
                        } else {
                            new DelayHelper().cancel();
                            getVerifyCode.setEnabled(true);
                            Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        dialog.dismiss();
                        new DelayHelper().cancel();
                        getVerifyCode.setEnabled(true);
                        Toast.makeText(getActivity(), "验证码发送失败", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @OnClick(R.id.tv_1_user_agreement)
    public void onUserAgreement(){
        showUserAgreementDialog(getActivity());
    }

    private static void showUserAgreementDialog(Activity activity) {
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        Window window = dialog.getWindow();
        dialog.show();

        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        window.setContentView(R.layout.dlg_user_agreement);

        Button ok = (Button) window.findViewById(R.id.btn_user_agreement_i_know);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void delayBtn() {
        new DelayHelper().delayButton(getVerifyCode);
    }

    @OnClick(R.id.register)
    void register() {
        String username = this.username.getText().toString();
        String verifyCode = this.verifyCode.getText().toString();
        String password = this.password.getText().toString();

        if (username.equals("")) {
            Toast.makeText(getActivity(), "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if(username.length() != 11){
            Toast.makeText(getActivity(), "用户名不符合规范", Toast.LENGTH_SHORT).show();
            return;
        }

        if (verifyCode.equals("")) {
            Toast.makeText(getActivity(), "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.equals("")) {
            Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6 || password.length() >20 ){
            Toast.makeText(getActivity(),"密码由6-20位英文字母、数字或者符号组成",Toast.LENGTH_SHORT).show();
            return;
        }

        if(Util.isPureDigitalPassword(password)){
            Toast.makeText(getActivity(),"密码由6-20位英文字母、数字或者符号组成",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!dealIsOk){
            Toast.makeText(getActivity(),"请阅读并同意注册用户协议",Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("注册中...");
        dialog.show();
        String md5Password = new MD5().md5(password);
        UserModule.getInstance().register(username, md5Password, UserModule.USER_TYPE_PHONE, verifyCode)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        dialog.dismiss();
                        if(aBoolean){
                            Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }else {
                            new DelayHelper().cancel();
                            getVerifyCode.setEnabled(true);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        dialog.dismiss();
                        new DelayHelper().cancel();
                        getVerifyCode.setEnabled(true);
                        Toast.makeText(getActivity(), "注册失败 " + ErrorUtils.getError(throwable), Toast.LENGTH_LONG).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {

                    }
                });
    }

    public PhoneRegisterFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_register, container, false);
        ButterKnife.bind(this, view);

        initMembers();
        setListener();
        return view;
    }

    private void initMembers() {
        username.setHint("请输入手机号");
        InputFilter[] filters = {new InputFilter.LengthFilter(11)};
        username.setFilters(filters);
        username.setInputType(InputType.TYPE_CLASS_PHONE);

        Drawable icon = getResources().getDrawable(R.drawable.selector_phone);
        if(icon != null){
            icon.setBounds(0,0,icon.getMinimumWidth(),icon.getMinimumHeight());
            username.setCompoundDrawables(icon,null,null,null);
        }

        InputFilter[] filters2 = {new InputFilter.LengthFilter(6)};
        verifyCode.setFilters(filters2);
        verifyCode.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    private void setListener(){
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dealIsOk = isChecked;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("用户注册页");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("用户注册页");
    }
}
