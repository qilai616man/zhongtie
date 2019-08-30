package com.crphdm.dl2.fragments.login;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
/**
 * 邮箱登陆
 */

public class EmailLoginFragment extends Fragment {
    //用户名
    @Bind(R.id.username)
    ClearEditTextNew username;
    //密码
    @Bind(R.id.password)
    ClearEditTextNew password;
    //注册
    @Bind(R.id.bind)
    Button register;

    private OnEmailLoginFragmentListener mListener;
    //立即登录按钮监听
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

        UserModule.getInstance().login(username, password, UserModule.USER_TYPE_EMAIL).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserInfo>() {
                    @Override
                    public void call(UserInfo userInfo) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_LONG).show();

                        if(mListener != null){
                            mListener.onEmailLoginOk();
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

    //注册
    @OnClick(R.id.bind)
    void onRegisterClick() {
        startActivity(new Intent(getActivity(), RegisterActivity.class));
    }
    //忘记密码
    @OnClick(R.id.forget_password)
    void onFindPasswordClick() {
        startActivity(new Intent(getActivity(), FindPasswordActivity.class));
    }

    public EmailLoginFragment() {

    }


    //初始化Fragment的布局。
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_login, container, false);
        ButterKnife.bind(this, view);

        initMembers();
        return view;
    }
    //初始化成员
    private void initMembers(){
        username.setHint("请输入邮箱地址");
        username.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnEmailLoginFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    //销毁与Fragment有关的视图，但未与Activity解除绑定
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    //解除与Activity的绑定。在onDestroy方法之后调用。
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnEmailLoginFragmentListener{
        void onEmailLoginOk();
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
