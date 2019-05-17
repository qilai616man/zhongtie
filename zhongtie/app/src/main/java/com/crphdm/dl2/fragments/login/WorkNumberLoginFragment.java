package com.crphdm.dl2.fragments.login;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class WorkNumberLoginFragment extends Fragment {
    //工号

    @Bind(R.id.username)
    ClearEditTextNew username;
    @Bind(R.id.password)
    ClearEditTextNew password;
    @Bind(R.id.bind)
    Button register;
    @Bind(R.id.forget_password)
    View forgetPassword;

    private boolean loginSecondCenter = false;
    private OnWorkNumberLoginFragmentListener mListener;

    public static WorkNumberLoginFragment loginSecondInstance() {
        WorkNumberLoginFragment workNumberLoginFragment = new WorkNumberLoginFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("secondCenter", true);
        workNumberLoginFragment.setArguments(bundle);
        return workNumberLoginFragment;
    }

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

        Ln.d("WorkNumberLoginFragment:loginSecondCenter:" + loginSecondCenter);

        Observable<UserInfo> observable = null;
        if (loginSecondCenter) {
            observable = UserModule.getInstance().loginSecond(username, password, UserModule.USER_TYPE_JOB_NUMBER);
        } else {
            observable = UserModule.getInstance().login(username, password, UserModule.USER_TYPE_JOB_NUMBER);
        }

        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserInfo>() {
                    @Override
                    public void call(UserInfo userInfo) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_LONG).show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "登录失败 " + ErrorUtils.getError(throwable), Toast.LENGTH_LONG).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (mListener != null) {
                            mListener.onWorkNumberLoginOk();
                        }
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginSecondCenter = getArguments() == null ? false : getArguments().getBoolean("secondCenter");
    }

    public WorkNumberLoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_email_login, container, false);
        ButterKnife.bind(this, view);

        username.setHint("请输入工号");
        Drawable icon = getResources().getDrawable(R.drawable.selector_work_number);
        if(icon != null){
            icon.setBounds(0,0,icon.getMinimumWidth(),icon.getMinimumHeight());
            username.setCompoundDrawables(icon,null,null,null);
        }

        if (loginSecondCenter) {
            register.setVisibility(View.GONE);
            forgetPassword.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnWorkNumberLoginFragmentListener) activity;
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

    public interface OnWorkNumberLoginFragmentListener{
        void onWorkNumberLoginOk();
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
