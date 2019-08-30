package com.crphdm.dl2.activity.personal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.ClearEditTextNew;
import com.crphdm.dl2.views.CropImage;
import com.digital.dl2.business.core.manager.LibraryManager;
import com.goyourfly.gdownloader.utils.Ln;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
/**
 * 编辑个人资料
 */
public class UpdateUserInfoActivity extends AppCompatActivity {
    @Bind(R.id.civ_update_user_info_photo)
    CircleImageView mIcon;
    @Bind(R.id.username)
    EditText username;
    @Bind(R.id.nickname)
    ClearEditTextNew nickName;
    @Bind(R.id.truename)
    ClearEditTextNew trueName;
    @Bind(R.id.rg_update_user_info_sex)
    RadioGroup mRadioGroup;
    @Bind(R.id.boy)
    RadioButton boy;
    @Bind(R.id.girl)
    RadioButton girl;
    @Bind(R.id.save)
    Button mSave;

    public static final int REQUEST_CODE = 100;

    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private CropImage mCropImage;

    private String mIconUrl;
    private Bitmap bitmap;

    private String mTrueName;
    private String mNickName;
    private int mSex = 1;//1.男  2.女
    //头像点击事件
    /*@OnClick(R.id.civ_update_user_info_photo)
    public void onIconClick() {
        Ln.d("UpdateUserInfoActivity:onIconClick");
        if (mUserInfo != null) {

            getPhotoDialog().show();


            mCropImage.setCropListener(new CropImage.OnCropImageListener() {
                @Override
                public void onCropOk(final File file) {
                    Ln.d("UpdateUserInfoActivity:onCropOk:" + file.getPath());
//                myHandler.sendEmptyMessage(uploadStart);
                    bitmap = BitmapFactory.decodeFile(file.getPath());
                    mIcon.setImageBitmap(bitmap);

//                myHandler.sendEmptyMessage(uploadOk);

                    if (mProgressDialog == null) {
                        mProgressDialog = ProgressDialog.show(UpdateUserInfoActivity.this, null, "加载中...");
                    }

                    UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String token) {
                                    LibraryManager.getInstance().uploadImage(
                                            UserModule.NET_CENTER_FIRST,
                                            mUserInfo.getUserid(),
                                            token,
                                            file)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.newThread())
                                            .subscribe(new Action1<String>() {
                                                @Override
                                                public void call(String url) {
                                                    file.delete();
                                                    mIconUrl = url;
                                                    Ln.d("UpdateUserInfoActivity:uploadImage:mIconUrl:" + mIconUrl+"---"+url);

                                                }
                                            }, new Action1<Throwable>() {
                                                @Override
                                                public void call(Throwable throwable) {
                                                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                        mProgressDialog.dismiss();
                                                        mProgressDialog = null;
                                                    }
//                                                myHandler.sendEmptyMessage(uploadErr);
                                                    Ln.d("UpdateUserInfoActivity:uploadImage:error:" + throwable.getMessage());
                                                }
                                            }, new Action0() {
                                                @Override
                                                public void call() {
                                                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                        mProgressDialog.dismiss();
                                                        mProgressDialog = null;
                                                    }
//                                                myHandler.sendEmptyMessage(uploadOk);
                                                }
                                            });

                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                        mProgressDialog.dismiss();
                                        mProgressDialog = null;
                                    }
                                }
                            }, new Action0() {
                                @Override
                                public void call() {

                                }
                            });

                }

                @Override
                public void onCancel() {
                    Ln.d("UpdateUserInfoActivity:onCancel");
                }
            });

        }
    }*/
    //保存按钮点击事件
    @OnClick(R.id.save)
    public void onSaveClick() {
        Ln.d("UpdateUseInfoActivity:onSaveClick:mIconUrl:" + mIconUrl);
        Ln.d("UpdateUseInfoActivity:onSaveClick:mTrueName:" + mTrueName);
        Ln.d("UpdateUseInfoActivity:onSaveClick:username():" + mUserInfo.getUsername());
        Ln.d("UpdateUseInfoActivity:onSaveClick:mSex:" + mSex);

        if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_FIRST_LEVEL ||
                UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {

            UserModule.getInstance().updateUserInfo(mIconUrl,mTrueName, mNickName, mSex, UserModule.NET_CENTER_FIRST)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<UserInfo>() {
                        @Override
                        public void call(UserInfo userInfo) {
                            Ln.d("UpdateUseInfoActivity:onSaveClick:userInfo:" + userInfo.toString());
                            Ln.d("UpdateUseInfoActivity:onSaveClick:修改个人信息成功");
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Ln.d("UpdateUseInfoActivity:onSaveClick:修改个人信息出错");
                            throwable.printStackTrace();
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            Ln.d("UpdateUseInfoActivity:onSaveClick:修改个人信息完成");
                            Toast.makeText(UpdateUserInfoActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            onBackPressed();
                        }
                    });
        } else {
            //显示无网
            Ln.d("UpdateUseInfoActivity:网络异常,请检查网络!");
            Toast.makeText(UpdateUserInfoActivity.this, "亲爱的用户，看不到我？请用手机连接互联网试一试", Toast.LENGTH_SHORT).show();
        }
    }
    //创建
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initMembers();
        setListener();
    }
    //初始化成员
    private void initMembers() {
        mCropImage = new CropImage(this);

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);

        if(mUserInfo == null){
            mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND);
        }

        if (mUserInfo != null) {
            Ln.d("UpdateUseInfoActivity:mUserInfo:" + mUserInfo.toString());

            mIconUrl = mUserInfo.getPhoto();
            mNickName = mUserInfo.getNickname();
            mTrueName = mUserInfo.getTruename();

            if (mIconUrl != null) {
                mIcon.setImageResource(R.drawable.drw_1_touxiang_new);
                ImageLoader.getInstance().displayImage(mIconUrl, mIcon);
            } else {
                mIcon.setImageResource(R.drawable.drw_1_touxiang_new);
            }

            if (mUserInfo.getUsername() != null) {
                username.setText(mUserInfo.getUsername());
            }
//            else {
//                username.setText("账号:");
//            }

            if (mNickName != null) {
                nickName.setText(mUserInfo.getNickname());
            }
//            else {
//                nickName.setText("昵称:");
//            }

            if (mTrueName != null) {
                trueName.setText(mTrueName);
            }
            Log.d("ttt", "initMembers: "+mUserInfo.getSex());
            if (mUserInfo.getSex() == 0) {
                mSex = 0;
                boy.setChecked(true);
            } else {
                girl.setChecked(true);
                mSex = 1;
            }
        }
    }
    //设置监听
    private void setListener() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.boy) {//选择男
                    mSex = 1;
                    Ln.d("UpdateUseInfoActivity:sex:男");
                } else if (checkedId == R.id.girl) {//选择女
                    mSex = 0;
                    Ln.d("UpdateUseInfoActivity:sex:女");
                }
            }
        });

        nickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Ln.d("UpdateUseInfoActivity:beforeTextChanged:nickName:" + s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Ln.d("UpdateUseInfoActivity:onTextChanged:nickName:" + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Ln.d("UpdateUseInfoActivity:afterTextChanged:nickName:" + s.toString());
                mNickName = s.toString();
            }
        });

        trueName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Ln.d("UpdateUseInfoActivity:beforeTextChanged:tureName:" + s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Ln.d("UpdateUseInfoActivity:onTextChanged:tureName:" + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Ln.d("UpdateUseInfoActivity:afterTextChanged:tureName:" + s.toString());
                mTrueName = s.toString();
            }
        });
    }
    //获取头像（拍照/本地相册）
    private Dialog getPhotoDialog() {
        String[] items = new String[]{"    拍照", "    相册选择"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                UpdateUserInfoActivity.this, R.layout.dlg_1_icon_select, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateUserInfoActivity.this);

//        builder.setTitle("选择头像");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    mCropImage.takePicture(Uri.fromFile(new File("")));
                } else {
                    mCropImage.openGallery();
                }
            }
        });

        return builder.create();
    }
    //接手Activity的回调信息做处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCropImage.onActivityResult(requestCode, resultCode, data);
    }
    //处理菜单被选中运行后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            setResult(RESULT_CANCELED);
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    //恢复
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("编辑个人资料页");
        MobclickAgent.onResume(this);
    }
    //暂停
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("编辑个人资料页");
        MobclickAgent.onPause(this);
    }
}
