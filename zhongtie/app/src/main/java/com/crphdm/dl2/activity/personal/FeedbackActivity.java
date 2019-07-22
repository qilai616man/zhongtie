package com.crphdm.dl2.activity.personal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.digital.dl2.business.core.module.PersonalCenterModule;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
/**
 * 意见与反馈
 */
public class FeedbackActivity extends AppCompatActivity {
    //姓名
    @Bind(R.id.name)
    EditText name;
    //邮箱
    @Bind(R.id.email)
    EditText email;
    //内容
    @Bind(R.id.content)
    EditText content;

    private String mName;
    private String mEmail;
    private String mContent;

    private UserInfo mUserInfo;
    private String mToken;

    //发送反馈按钮监听
    @OnClick(R.id.sendFeedback)
    public void sendFeedback() {
        if (name.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "名字为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "联系方式为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "内容为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_FIRST_LEVEL ||
                UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {

            final ProgressDialog dialog = ProgressDialog.show(this, null, "发送中...", true, false);

            Ln.d("FeedbackActivity:mName:" + mName);
            Ln.d("FeedbackActivity:mEmail:" + mEmail);
            Ln.d("FeedbackActivity:mContent:" + mContent);

            if (mUserInfo != null) {
                PersonalCenterModule.getInstance().sendFeedback(mUserInfo.getUserid(), mToken, mName, mEmail, mContent)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                dialog.dismiss();
                                Toast.makeText(FeedbackActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Ln.d("FeedbackActivity:throwable:" + throwable);
                                Toast.makeText(FeedbackActivity.this, "发送失败,请检查邮箱填写是否正确！", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        }, new Action0() {
                            @Override
                            public void call() {

                            }
                        });
            } else {
                dialog.dismiss();
                Toast.makeText(FeedbackActivity.this, "发送失败,请您重新登录后再次尝试!", Toast.LENGTH_SHORT).show();
                Ln.d("FeedbackActivity:用户信息为空");
            }
        } else {
            //显示无网
            Ln.d("FeedbackActivity:网络异常,请检查网络!");
            Toast.makeText(FeedbackActivity.this, "网络异常,请检查网络!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);

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
                        throwable.printStackTrace();
//                        Toast.makeText(FeedbackActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {

                    }
                });

        initMembers();
    }
    //初始化成员
    private void initMembers() {


        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mName = s.toString();
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mEmail = s.toString();
            }
        });

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mContent = s.toString();
            }
        });
    }
    //处理菜单被选中运行后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    //恢复
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("意见与反馈页面");
        MobclickAgent.onResume(this);
    }
    //暂停
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("意见与反馈页面");
        MobclickAgent.onPause(this);
    }
}
