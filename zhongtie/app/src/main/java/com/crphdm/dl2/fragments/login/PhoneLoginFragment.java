package com.crphdm.dl2.fragments.login;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.login.FindPasswordActivity;
import com.crphdm.dl2.activity.login.RegisterActivity;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.views.ClearEditTextNew;
import com.goyourfly.gdownloader.utils.ErrorUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PhoneLoginFragment extends Fragment {
    //手机号 （已废弃）
    @Bind(R.id.username)
    ClearEditTextNew username;
    @Bind(R.id.password)
    ClearEditTextNew password;
    @Bind(R.id.bind)
    Button register;

    private OnPhoneLoginFragmentListener mListener;

    @OnClick(R.id.login)
    void onLoginClick() {
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();

        if (username.equals("")) {
            Toast.makeText(getActivity(), "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.equals("")) {
            Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("登录中...");
        dialog.show();

        UserModule.getInstance().login(username, password, UserModule.USER_TYPE_PHONE).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserInfo>() {
                    @Override
                    public void call(UserInfo userInfo) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_LONG).show();

                        if (mListener != null) {
                            mListener.onPhoneLoginOk();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "登录失败 " + ErrorUtils.getError(throwable), Toast.LENGTH_LONG).show();
                    }
                });

    }

    @OnClick(R.id.bind)
    void onRegisterClick() {
        startActivity(new Intent(getActivity(), RegisterActivity.class));
    }

    @OnClick(R.id.forget_password)
    void onFindPasswordClick() {
        startActivity(new Intent(getActivity(), FindPasswordActivity.class));
    }

    public PhoneLoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_login, container, false);
        ButterKnife.bind(this, view);

        initMembers();
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
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPhoneLoginFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPhoneLoginFragmentListener {
        void onPhoneLoginOk();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("登录页");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("登录页");
    }
}
