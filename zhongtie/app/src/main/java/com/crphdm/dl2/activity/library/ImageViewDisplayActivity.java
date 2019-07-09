package com.crphdm.dl2.activity.library;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.crphdm.dl2.R;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.umeng.analytics.MobclickAgent;


import java.io.File;

public class ImageViewDisplayActivity extends ActionBarActivity {
    //试读列表页
    public static final String INTENT_IMAGE_PATH = "INTENT_IMAGE_PATH";
    private SubsamplingScaleImageView mImageView;
    private ProgressDialog mProgressDialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_display);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        mImageView = (SubsamplingScaleImageView) findViewById(R.id.image);
        mImageView.setOrientation(SubsamplingScaleImageView.ORIENTATION_0);
        String path = getIntent().getStringExtra(INTENT_IMAGE_PATH);
        mProgressDialog = ProgressDialog.show(ImageViewDisplayActivity.this,"","加载中",true,false,null);
        if(path.startsWith("http")){
            Glide.with(this).load(path).downloadOnly(new SimpleTarget<File>() {
                  //资源就绪
                @Override
                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                    mImageView.setImage(ImageSource.uri(Uri.fromFile(resource)).tilingEnabled());
                    mProgressDialog.dismiss();
                }
                //加载失败
                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    mProgressDialog.dismiss();
                }
            });
        }else {
            mImageView.setImage(ImageSource.uri(Uri.fromFile(new File(path))).tilingEnabled());
            mImageView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
                        //准备就绪
                @Override
                public void onReady() {
                    mProgressDialog.dismiss();
                }
                        //在图像加载
                @Override
                public void onImageLoaded() {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onPreviewLoadError(Exception e) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onImageLoadError(Exception e) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onTileLoadError(Exception e) {
                    mProgressDialog.dismiss();
                }
            });
        }

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("试读列表页");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("试读列表页");
        MobclickAgent.onPause(this);
    }
}
