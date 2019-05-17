package com.crphdm.dl2.activity.library;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.crphdm.dl2.R;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class ImageViewDisplayNewActivity extends AppCompatActivity{
    //试读列表页
    private ViewPager mViewPager;
    public int mPosition;
    private ArrayList<String> mImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏通知栏 全屏
        setContentView(R.layout.activity_image_view_display_new);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
    }

    private void initView() {
        mPosition = getIntent().getIntExtra("position",0);
        mImageList = getIntent().getStringArrayListExtra("ImageList");
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        //试读图片
//        mViewPager.setAdapter(new PreviewAdapter(Arrays.asList(getResources().getStringArray(R.array.test_list))));
        mViewPager.setAdapter(new PreviewAdapter(mImageList));
        mViewPager.setCurrentItem(mPosition);

    }

    @Override
    protected void onDestroy() {
        mImageList.clear();
        super.onDestroy();
    }

    class PreviewAdapter extends PagerAdapter {
        private List<String> mPhotos;

        public PreviewAdapter(List<String> photos) {
            mPhotos = photos;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final ImageView photoView = new ImageView(container.getContext());
//            final ImageView photoView = new ImageView(container.getContext());

            Ln.d("ImageViewDisplayNewActivity:ImageView:one:" + mPhotos.get((position)));

            Glide.with(ImageViewDisplayNewActivity.this)
                    .load(mPhotos.get(position))
                    .placeholder(R.drawable.drw_book_default)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            Ln.d("PreviewActivity:ImageView:two:" + mPhotos.get((position)));
                            photoView.setImageDrawable(resource.getCurrent());
                        }
                    });

            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPosition = position;

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mPhotos.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
