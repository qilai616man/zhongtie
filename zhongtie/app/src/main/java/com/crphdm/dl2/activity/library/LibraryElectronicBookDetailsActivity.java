package com.crphdm.dl2.activity.library;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.digital.dl2.business.core.manager.LibraryManager;
import com.digital.dl2.business.core.obj.PgBookForLibraryDetail;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LibraryElectronicBookDetailsActivity extends AppCompatActivity {
    //图书馆和资源共用详情页
    @Bind(R.id.sv_library_book_detail)
    ScrollView mScrollView;
    @Bind(R.id.iv_book_url)
    ImageView ivBookUrl;
    @Bind(R.id.tv_book_name)
    TextView tvBookName;
    @Bind(R.id.tv_price)
    TextView tvPrice;

    @Bind(R.id.btn_library_book_detail_borrow)//借阅
            Button mBorrowBtn;
    @Bind(R.id.btn_library_book_detail_renew)//续借
            Button mRenewBtn;
    @Bind(R.id.btn_library_book_detail_return)//归还
            Button mReturnBtn;
    @Bind(R.id.btn_library_book_detail_reservation)//预订
            Button mReservationBtn;
    @Bind(R.id.btn_library_book_detail_download)//下载
            Button mDownloadBtn;

    @Bind(R.id.btn_provisionality_book_detail_borrow)//临时借阅
            Button mProvisionalityBorrow;
    @Bind(R.id.btn_provisionality_book_detail_remove)//移除
            Button mProvisionalityRemove;

    @Bind(R.id.tv_upload_author_name)
    TextView author;
    @Bind(R.id.tv_upload_time)
    TextView uploadTime;
    @Bind(R.id.tv_library_book_detail_book_number)
    TextView bookNumber;
    @Bind(R.id.btn_publish_institution)
    TextView publishInstitution;
    @Bind(R.id.tv_content_brief)
    TextView tvContentBrief;
    @Bind(R.id.tv_contents)
    TextView mContents;

    private int mBookId;

    private PgBookForLibraryDetail mBookDetail;

    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_book_detail);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBookId = getIntent().getIntExtra(Constant.BOOK_ID, 0);


        Ln.d("LibraryBookDetailFirstActivity:onCreate:mBookId:" + mBookId);

        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, null, "加载中...");
        }

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
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        throwable.printStackTrace();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        initData();
                    }
                });
    }
    //临时用户借阅
    @OnClick(R.id.btn_provisionality_book_detail_borrow)
    public void onProvisionalityBorrowClick(){//临时用户：provisionality

        Toast.makeText(LibraryElectronicBookDetailsActivity.this, "亲爱的用户，请先连接自助阅读机的wifi后再进行借阅图书！", Toast.LENGTH_SHORT).show();

    }

    //借阅
    @OnClick(R.id.btn_library_book_detail_borrow)
    public void onBorrowClick() {

        Toast.makeText(LibraryElectronicBookDetailsActivity.this, "亲爱的用户，请先连接自助阅读机的wifi后再进行借阅图书！", Toast.LENGTH_SHORT).show();

    }
    //续借
    @OnClick(R.id.btn_library_book_detail_renew)
    public void onRenewClick() {
        Toast.makeText(LibraryElectronicBookDetailsActivity.this, "亲爱的用户，请先连接自助阅读机的wifi后再进行续借图书！", Toast.LENGTH_SHORT).show();

    }

    //归还
    @OnClick(R.id.btn_library_book_detail_return)
    public void onReturnClick() {
        Toast.makeText(LibraryElectronicBookDetailsActivity.this, "亲爱的用户，请先连接自助阅读机的wifi后再进行归还图书！", Toast.LENGTH_SHORT).show();

    }
    //预订
    @OnClick(R.id.btn_library_book_detail_reservation)
    public void onReservationClick() {
        Toast.makeText(LibraryElectronicBookDetailsActivity.this, "亲爱的用户，请先连接自助阅读机的wifi后再进行预订图书！", Toast.LENGTH_SHORT).show();

    }
    //初始化数据
    private void initData() {
        if (mUserInfo != null) {

            LibraryManager.getInstance().getFirstBookDetailById(mToken, mBookId, mUserInfo.getUserid())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<PgBookForLibraryDetail>() {
                        @Override
                        public void call(PgBookForLibraryDetail pgBookForLibraryDetail) {
                            mBookDetail = pgBookForLibraryDetail;
                            Ln.d("LibraryBookDetailFirstActivity:onCreate:mBookDetail:" + mBookDetail);
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
                            refresh();
                        }
                    });

        }else {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }
    //刷新
    private void refresh() {
        Ln.d("LibraryBookDetailFirstActivity:refresh");
        Glide.with(LibraryElectronicBookDetailsActivity.this)
                .load(mBookDetail.getFrontCover())
                .placeholder(R.drawable.drw_book_default)
                .into(ivBookUrl);

        tvBookName.setText(mBookDetail.getName());
        tvPrice.setText(mBookDetail.getPrice() + "元");

        author.setText(mBookDetail.getAuthor());
        uploadTime.setText(mBookDetail.getPublishDate());
        bookNumber.setText(mBookDetail.getBookNumber());
        publishInstitution.setText(mBookDetail.getPublishingHouse());
        tvContentBrief.setText(mBookDetail.getIntroduction());

        mBorrowBtn.setVisibility(View.INVISIBLE);
        mReturnBtn.setVisibility(View.INVISIBLE);
        mRenewBtn.setVisibility(View.INVISIBLE);
        mReservationBtn.setVisibility(View.INVISIBLE);
        mDownloadBtn.setVisibility(View.INVISIBLE);
        mProvisionalityBorrow.setVisibility(View.INVISIBLE);
        mProvisionalityRemove.setVisibility(View.INVISIBLE);
        mProvisionalityBorrow.setText("借阅");
        mProvisionalityBorrow.setVisibility(View.VISIBLE);
        mBorrowBtn.setText("借阅");
        mBorrowBtn.setVisibility(View.VISIBLE);

        setupContents(mBookDetail.getCatalog());

        mScrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    //目录
    private void setupContents(String[] contents) {
        StringBuffer buffer = new StringBuffer();
        for (String string : contents) {
            buffer.append(string + "\n");
        }
        mContents.setText(buffer.toString());
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
        MobclickAgent.onPageStart("一级图书馆图书详情页");
        MobclickAgent.onResume(this);
    }
    //暂停
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("一级图书馆图书详情页");
        MobclickAgent.onPause(this);
    }
}


