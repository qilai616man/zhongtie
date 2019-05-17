package com.crphdm.dl2.activity.personal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.obj.PgPublishAgency;
import com.goyourfly.gdownloader.utils.ErrorUtils;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class BindOrganizationActivity extends AppCompatActivity {
    //绑定机构
    public static final int REQUEST_CODE = 321;
    public static final String INTENT_ORG = "phone";

    private List<String> list = new ArrayList<>();
    @Bind(R.id.spinner)
    Spinner spinner;

    private ArrayAdapter mAdapter;
    private Map<Integer, Integer> mTagMap = new HashMap<>();
    private List<String> mListData;

    private int mOrgId;
    private String mToken;
    private UserInfo mUserInfo;

    private ProgressDialog mProgressDialog;

    @OnClick(R.id.bind)
    public void bind() {
        if (mUserInfo != null) {
            Ln.d("Start bind org");
            UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            Ln.d("Start bind get token");
                            mToken = s;
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            //错误
                            Toast.makeText(BindOrganizationActivity.this, "绑定失败 " + ErrorUtils.getError(throwable), Toast.LENGTH_SHORT).show();
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            Ln.d("Start apply for bind agency");
                            //完成
                            PersonalCenterManager.getInstance().applyForBindingAgency(mUserInfo.getUserid(), mToken, mOrgId)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<Boolean>() {
                                        @Override
                                        public void call(Boolean aBoolean) {
                                            Ln.d("Bind result:" + aBoolean);
                                            if (aBoolean) {
                                                UserModule.getInstance().updateUserInfoByState(1,"",spinner.getSelectedItem().toString());
                                                bindSuccess();
                                            } else {
                                                Toast.makeText(BindOrganizationActivity.this, "绑定失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            throwable.printStackTrace();
                                            Toast.makeText(BindOrganizationActivity.this, "绑定失败 " + ErrorUtils.getError(throwable), Toast.LENGTH_SHORT).show();
                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {

                                        }
                                    });
                        }
                    });

        } else {
            Toast.makeText(BindOrganizationActivity.this, "绑定失败 ：用户信息为空", Toast.LENGTH_SHORT).show();
        }
    }

    private void bindSuccess() {
        Intent intent = new Intent();
        intent.putExtra(INTENT_ORG, spinner.getSelectedItem().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_organization);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);


        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(BindOrganizationActivity.this, null, "绑定中...");
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
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
//                        Toast.makeText(BindOrganizationActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        initMembers();
                    }
                });
    }

    private void initMembers() {

        PersonalCenterManager.getInstance().getPublishAgencys(mToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgPublishAgency>>() {
                    @Override
                    public void call(List<PgPublishAgency> pgPublishAgencies) {
                        mListData = getList(pgPublishAgencies);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
//                        Toast.makeText(BindOrganizationActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        mAdapter.addAll(mListData);
                    }
                });

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mOrgId = mTagMap.get(position);
//                Toast.makeText(BindOrganizationActivity.this, mTagMap.get(position) + "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private List<String> getList(List<PgPublishAgency> pgPublishAgencies) {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < pgPublishAgencies.size(); i++) {
            list.add(pgPublishAgencies.get(i).getName());
            mTagMap.put(i, pgPublishAgencies.get(i).getEntityId());

            Ln.d("ClassifyDetailActivity:getData:index:" + pgPublishAgencies.get(i).getEntityId());
        }
        return list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("绑定机构页");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("绑定机构页");
        MobclickAgent.onPause(this);
    }
}
