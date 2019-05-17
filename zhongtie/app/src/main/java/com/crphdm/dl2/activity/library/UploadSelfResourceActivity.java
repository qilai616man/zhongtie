package com.crphdm.dl2.activity.library;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.views.ClearEditText;
import com.digital.dl2.business.core.manager.LibraryManager;
import com.digital.dl2.business.core.obj.PgUploadResourceDetail;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class UploadSelfResourceActivity extends AppCompatActivity implements View.OnClickListener {
    //上传文件
    private static final String TAG = UploadSelfResourceActivity.class.getSimpleName();

    @Bind(R.id.tv_local_institution)
    TextView tvLocalInstitution;
    @Bind(R.id.tv_all_institution)
    TextView tvAllInstitution;
    @Bind(R.id.cet_title)
    ClearEditText cetTitle;
    @Bind(R.id.cet_author)
    ClearEditText cetAuthor;
    @Bind(R.id.cet_institution)
    ClearEditText cetInstitution;
    @Bind(R.id.cet_introduction)
    ClearEditText cetIntroduction;
    @Bind(R.id.cet_make_price)
    ClearEditText cetMakePrice;
    @Bind(R.id.tv_upload_percent)
    TextView tvUploadPercent;
    @Bind(R.id.iv_add_image)
    ImageView ivAddImage;
    @Bind(R.id.btn_upload_file)
    Button btnUploadFile;
    @Bind(R.id.tv_upload_file_percent)
    TextView tvUploadFilePercent;
    @Bind(R.id.iv_add_file_image)
    ImageView ivAddFileImage;
    //@Bind(R.id.iv_icon)
    //ImageView ivIcon;

    private String mImageUrl;
    private String mImagePath;
    private String mResourcePath;
    private PgUploadResourceDetail mPgResourcesDetail;

    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;

    private int mModify;

    private static final int REQUEST_IMAGE = 1;
    private int FILE_SELECT_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_self_resourse);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initData();
        initClickListener();
    }

    private void initClickListener() {
        tvLocalInstitution.setOnClickListener(this);
        tvAllInstitution.setOnClickListener(this);
        ivAddImage.setOnClickListener(this);
        ivAddFileImage.setOnClickListener(this);
        btnUploadFile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tv_local_institution:
                mPgResourcesDetail.setOrg_id(1);
                tvLocalInstitution.setBackgroundResource(R.drawable.drw_1_upload_data_institution_bg);
                tvAllInstitution.setBackgroundResource(R.drawable.drawable_ffffff);

                break;

            case R.id.tv_all_institution :
                mPgResourcesDetail.setOrg_id(2);
                tvAllInstitution.setBackgroundResource(R.drawable.drw_1_upload_data_institution_bg);
                tvLocalInstitution.setBackgroundResource(R.drawable.drawable_ffffff);

                break;
            case R.id.iv_add_image://上传图片

              //11
              //  Intent intent = new Intent(this, MultiImageSelectorActivity.class);

                // whether show camera  11
             //   intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);

                // max select image amount
                // intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 4);

                // select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI) 11
               // intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
             //111
                //startActivityForResult(intent, REQUEST_IMAGE);

                break;

            case R.id.iv_add_file_image://上传文件

                showFileChooser();

                break;

            case R.id.btn_upload_file:
                Ln.d("UploadSelfResource:提交:cetTitle:" + cetTitle.getText().toString());
                Ln.d("UploadSelfResource:提交:cetAuthor:" + cetAuthor.getText().toString());
                Ln.d("UploadSelfResource:提交:cetInstitution:" + cetInstitution.getText().toString());
                Ln.d("UploadSelfResource:提交:cetIntroduction:" + cetIntroduction.getText().toString());
                Ln.d("UploadSelfResource:提交:cetMakePrice:" + cetMakePrice.getText().toString());


                if (!cetTitle.getText().toString().equals("") && !cetAuthor.getText().toString().equals("")
                        && !cetInstitution.getText().toString().equals("") && !cetIntroduction.getText().toString().equals("")
                        && !cetMakePrice.getText().toString().equals("")) {

                    mPgResourcesDetail.setTitle(cetTitle.getText().toString().trim());
                    mPgResourcesDetail.setAuthor(cetAuthor.getText().toString().trim());
                    mPgResourcesDetail.setDescription(cetIntroduction.getText().toString().trim());

                    Ln.d("UploadSelfResource:提交:one");

                    try {
                        float price = Float.parseFloat(cetMakePrice.getText().toString().trim());
                        mPgResourcesDetail.setPrice(price);
                    } catch (Exception e) {
                        Toast.makeText(UploadSelfResourceActivity.this, "你所输入的数据格式不正确，请重新输入", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Ln.d("UploadSelfResource:提交:two");

                    if (mImageUrl != null && !mImageUrl.equals("")) {
                        mPgResourcesDetail.setThumb(mImageUrl);
                    } else {
                        Toast.makeText(UploadSelfResourceActivity.this, "请上传图片", Toast.LENGTH_SHORT).show();
                    }

                    Ln.d("UploadSelfResource:提交:three");

                    if (mResourcePath != null && !mResourcePath.equals("")) {
                        File file = new File(mResourcePath);
                        if (file.exists()) {
                            mPgResourcesDetail.setFile(file);
                        } else {
                            Ln.d("UploadSelfResource:提交:文件不存在");
                        }
                    } else {
                        Toast.makeText(UploadSelfResourceActivity.this, "请上传文件", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Ln.d("UploadSelfResource:提交:four");

                    if (mUserInfo != null) {
                        Ln.d("UploadSelfResource:提交:five");
                        if (mProgressDialog == null) {
                            mProgressDialog = ProgressDialog.show(UploadSelfResourceActivity.this, null, "提交中...");
                        }

                        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_SECOND)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(new Action1<String>() {
                                    @Override
                                    public void call(String s) {
                                        mToken = s;
                                        Ln.d("UploadSelfResource:提交:getToken:" + mToken);
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
                                        Ln.d("UploadSelfResource:提交:getToken:ok:" + mToken);
                                        LibraryManager.getInstance().uploadResources(
                                                mUserInfo.getUserid(),
                                                mToken,
                                                mPgResourcesDetail)
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribeOn(Schedulers.newThread())
                                                .subscribe(new Action1<Boolean>() {
                                                    @Override
                                                    public void call(Boolean aBoolean) {
                                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                            mProgressDialog.dismiss();
                                                            mProgressDialog = null;
                                                        }
                                                        mModify = 0;
                                                        Ln.d("UploadSelfResource:提交:uploadResources:" + aBoolean);
                                                    }
                                                }, new Action1<Throwable>() {
                                                    @Override
                                                    public void call(Throwable throwable) {
                                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                            mProgressDialog.dismiss();
                                                            mProgressDialog = null;
                                                        }
                                                        Ln.d("UploadSelfResource:提交:uploadResources:error");
                                                    }
                                                }, new Action0() {
                                                    @Override
                                                    public void call() {
                                                        Ln.d("UploadSelfResource:提交:uploadResources:ok");
                                                        btnUploadFile.setText("提交成功");
                                                        onBackPressed();
                                                        Toast.makeText(
                                                                UploadSelfResourceActivity.this,
                                                                "上传成功",
                                                                Toast.LENGTH_SHORT)
                                                                .show();
                                                    }
                                                });
                                    }
                                });

                    } else {
                        Ln.d("UploadSelfResource:提交:zero_one");
//                        Toast.makeText(UploadSelfResourceActivity.this, "用户信息为空", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Ln.d("UploadSelfResource:提交:zero_two");
                    Toast.makeText(this, "带*的选项不能为空", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                // Get the result list of select image paths  11111
//                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
//                if (path != null && path.size() > 0) {
//                    mImagePath = path.get(0);
//                    //Glide.with(this).load(path.get(0)).into(ivIcon);
//
//                    Ln.d("UploadSelfResource:image:path" + path);
//                }

                final File image = new File(mImagePath);

                if (mUserInfo != null) {
                    Ln.d("UploadSelfResource:mUserInfo：" + mUserInfo.toString());
                    if (mProgressDialog == null) {
                        mProgressDialog = ProgressDialog.show(UploadSelfResourceActivity.this, null, "提交中...");
                    }

                    UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_SECOND)
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
                                }
                            }, new Action0() {
                                @Override
                                public void call() {
                                    LibraryManager.getInstance().uploadImage(
                                            UserModule.NET_CENTER_SECOND,
                                            mUserInfo.getUserid(),
                                            mToken,
                                            image)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.newThread())
                                            .subscribe(new Action1<String>() {
                                                @Override
                                                public void call(String s) {
                                                    mImageUrl = s;
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
                                                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                        mProgressDialog.dismiss();
                                                        mProgressDialog = null;
                                                    }
                                                    mModify++;
                                                    tvUploadPercent.setText("已上传");
                                                    Toast.makeText(UploadSelfResourceActivity.this, "上传图片成功", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                } else {
                    Ln.d("UploadSelfResource:mUserInfo == null：");

//                    Toast.makeText(this, "带*的选项不能为空", Toast.LENGTH_SHORT).show();
                }

                Ln.d("UploadSelfResource:path" + mImagePath);
                // do your logic ....
            }
        } else if (requestCode == FILE_SELECT_CODE) {
            if (data != null && data.getData() != null) {

                Uri uri = data.getData();
                String path = null;
                try {
                    path = getPath(UploadSelfResourceActivity.this, uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                Ln.d("UploadSelfResource:file:path" + path);
                mResourcePath = path;

                tvUploadFilePercent.setText("已选择");
            }
        }
    }

    public String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件上传"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    public void hideSoftInputFromWindow(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void initData() {
        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND);

        mPgResourcesDetail = new PgUploadResourceDetail();
        tvLocalInstitution.setBackgroundResource(R.drawable.drw_1_upload_data_institution_bg);
        tvAllInstitution.setBackgroundResource(R.drawable.drawable_ffffff);

        tvUploadPercent.setText("未上传");
        tvUploadFilePercent.setText("未上传");
    }

    private AlertDialog mExitConfirmDialog;

    @Override
    public void onBackPressed() {
        if (mModify != 0) {
            if (mExitConfirmDialog == null) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("是否离开当前页面？");
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
                mExitConfirmDialog = builder.create();
            }

            if (mExitConfirmDialog.isShowing()) {
                mExitConfirmDialog.dismiss();
            } else {
                mExitConfirmDialog.show();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);

    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("图书馆上传文件页");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("图书馆上传文件页");
        MobclickAgent.onPause(this);
    }
}
