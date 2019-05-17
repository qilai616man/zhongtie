package com.crphdm.dl2.activity.library;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.brainsoon.utils.BookUtils;
import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.crphdm.dl2.service.MyService;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.utils.Util;
import com.crphdm.dl2.views.BeautifulProgressbar;
import com.digital.dl2.business.core.manager.BookshelfManager;
import com.digital.dl2.business.core.manager.LibraryManager;
import com.digital.dl2.business.core.manager.PublicManager;
import com.digital.dl2.business.core.obj.PgBookForLibraryDetail;
import com.digital.dl2.business.core.obj.PgResult;
import com.digital.dl2.business.database.DatabaseManager;
import com.digital.dl2.business.database.ProvisionalityDatabaseHelper;
import com.digital.dl2.business.database.obj.DbBookshelfEntity;
import com.digital.dl2.business.util.DownloadEvent;
import com.google.gson.Gson;
import com.goyourfly.gdownloader.GDownloader;
import com.goyourfly.gdownloader.db.DbDownloadExt;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LibraryBookDetailsSecondActivity extends AppCompatActivity {
    //图书馆二级中心详情页\
    @Bind(R.id.sv_library_book_detail)
    ScrollView mScrollView;
    @Bind(R.id.iv_book_url)
    ImageView ivBookUrl;
    @Bind(R.id.progress)
    BeautifulProgressbar progressbar;
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
    private int mBookDetailType;

    private PgBookForLibraryDetail mBookDetail;

    private boolean isBorrowing = false;
    private boolean isCanBorrowing = false;
    private boolean isCanRenew = false;
    private boolean isReservation = false;
    private boolean isDownload = false;

    private Context mContext;
    private UserInfo mUserInfo;
    private String mToken;
    private ProgressDialog mProgressDialog;

    private Handler mHandler = new Handler();

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_book_detail);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBookId = getIntent().getIntExtra(Constant.BOOK_ID, 0);
        mBookDetailType = getIntent().getIntExtra(Constant.LIBRARY_DETAIL_TYPE, Constant.LIBRARY_DETAIL_E_BOOK);

        Ln.d("LibraryBookDetailSecondActivity:onCreate:mBookId:" + mBookId);

        mContext = this;

        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(mContext, null, "加载中...");
        }

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND);
        //获取token
        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_SECOND)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mToken = s;
                        Ln.d("getToken is ok");
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
        getname();

        EventBus.getDefault().register(this);
    }

    @OnClick(R.id.btn_provisionality_book_detail_borrow)
    public void onProvisionalityBorrowClick(){//临时用户：provisionality
        if(isBorrowing){//已借阅
            if(isDownload){
                readBook();
            }else{
                //执行下载操作
                if (mBookDetail.getDownloadUrl() != null) {
                    if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_SECOND_LEVEL ||
                            UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
                        PublicManager.getInstance().downloadBook(mBookDetail.getDownloadUrl());
                    } else {
                        Toast.makeText(LibraryBookDetailsSecondActivity.this, "亲爱的用户，不对头呦！你们单位网络把我挡外面啦，领我进去嘛", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "获取下载地址失败", Toast.LENGTH_SHORT).show();
                }
            }
        }else{//借阅
            if(mUserInfo != null){
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

                            }
                        }, new Action0() {
                            @Override
                            public void call() {
                                /**
                                 *ymd  error  临时用户未做借阅限制，登陆成功即可借阅，修改！！！！！！！！！
                                 */
                                isBorrowing = true;
                                if(isBorrowing){
                                    BookshelfManager.getInstance().insertBookForLibrary(mBookDetail);
                                    initData();

                                    Intent intent = new Intent();
                                    intent.setAction(Constant.ACTION_REFRESH_DATA);
                                    sendBroadcast(intent);
                                    mProvisionalityBorrow.setText("下载");
                                    Toast.makeText(LibraryBookDetailsSecondActivity.this, "借阅成功", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(LibraryBookDetailsSecondActivity.this,"借阅失败",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        }
    }

    @OnClick(R.id.btn_provisionality_book_detail_remove)
    public void onProvisionalityRemoveClick(){//临时用户移除图书
        if (mUserInfo != null) {
            GDownloader.getInstance().delete(mBookDetail.getDownloadUrl());
            progressbar.setProgress(0);
            progressbar.setVisibility(View.GONE);

            Toast.makeText(LibraryBookDetailsSecondActivity.this, "图书移除成功", Toast.LENGTH_SHORT).show();
            BookshelfManager.getInstance().removeBookById(mBookId);
            ProvisionalityDatabaseHelper helper = new ProvisionalityDatabaseHelper(LibraryBookDetailsSecondActivity.this);
            helper.del(mBookId);
            initData();
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_REFRESH_DATA);
            sendBroadcast(intent);
        }

    }

    @OnClick(R.id.btn_library_book_detail_borrow)
    public void onBorrowClick() {
        Ln.d("LibraryBookDetailsSecondActivity:onBorrowClick");
        if (isBorrowing) {//已借阅
            if (isDownload) {
                //执行打开操作
                readBook();
            } else {
                //执行下载操作
                if (mBookDetail.getDownloadUrl() != null) {
                    if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_SECOND_LEVEL ||
                            UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
                        PublicManager.getInstance().downloadBook(mBookDetail.getDownloadUrl());
                    } else {
                        Toast.makeText(LibraryBookDetailsSecondActivity.this, "亲爱的用户，不对头呦！你们单位网络把我挡外面啦，领我进去嘛", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "获取下载地址失败", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            //执行借阅
            if (mUserInfo != null) {
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
                                //错误
                            }
                        }, new Action0() {
                            @Override
                            public void call() {
                                //完成
                                LibraryManager.getInstance().borrowingBookById(mUserInfo.getUserid(), mToken, mBookDetail.getEntityId())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.newThread())
                                        .subscribe(new Action1<Boolean>() {
                                            @Override
                                            public void call(Boolean aBoolean) {
                                                isBorrowing = aBoolean;
                                                if (isBorrowing) {
                                                    Ln.d("LibraryBookDetailSecondActivity:图书借阅成功");
                                                    Toast.makeText(LibraryBookDetailsSecondActivity.this, "图书借阅成功", Toast.LENGTH_SHORT).show();

                                                    BookshelfManager.getInstance().insertBookForLibrary(mBookDetail);

                                                    mBorrowBtn.setText("下载");
                                                    initData();

                                                    Intent intent = new Intent();
                                                    intent.setAction(Constant.ACTION_REFRESH_DATA);
                                                    sendBroadcast(intent);
                                                } else {
                                                    Toast.makeText(LibraryBookDetailsSecondActivity.this, "图书借阅失败", Toast.LENGTH_SHORT).show();
                                                    Ln.d("LibraryBookDetailSecondActivity:图书借阅失败");
                                                }
                                            }
                                        }, new Action1<Throwable>() {
                                            @Override
                                            public void call(Throwable throwable) {
                                                throwable.printStackTrace();
                                                /**
                                                 * ymd  error  添加提示信息
                                                 */
                                                Toast.makeText(LibraryBookDetailsSecondActivity.this, "图书已过期", Toast.LENGTH_SHORT).show();
                                            }
                                        }, new Action0() {
                                            @Override
                                            public void call() {

                                            }
                                        });
                            }
                        });
            }
        }
    }

    @OnClick(R.id.btn_library_book_detail_renew)
    public void onRenewClick() {//续借
        if (BookshelfManager.getInstance().getBookById(mBookDetail.getEntityId()) != null) {
            long time = System.currentTimeMillis() / 1000l;
            long borrowTime = BookshelfManager.getInstance().getBookById(mBookDetail.getEntityId()).getBorrowTime();

            Ln.d("LibraryBookDetailSecondActivity:itemView:续借:time:" + time);
            Ln.d("LibraryBookDetailSecondActivity:itemView:续借:borrowTime:" + borrowTime);
            if (time - borrowTime < 60 * 60 * 24 * 27) {
                Toast.makeText(mContext, "您只能在到期的前三天才能续借", Toast.LENGTH_SHORT).show();
            } else {
                renewBook();
            }
        } else {
            Toast.makeText(mContext, "续借失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void renewBook() {
        if (mUserInfo != null) {
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
                            //错误
                            Toast.makeText(LibraryBookDetailsSecondActivity.this, "图书续借失败", Toast.LENGTH_SHORT).show();
                            Ln.d("LibraryBookDetailSecondActivity:getTokenAsync:error:" + throwable.getMessage());
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            //完成
                            BookshelfManager.getInstance().renewBookById(
                                    mUserInfo.getUserid(),
                                    mToken,
                                    mBookDetail.getEntityId(),
                                    UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND).getOrg_id())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<Boolean>() {
                                        @Override
                                        public void call(Boolean aBoolean) {
                                            isBorrowing = aBoolean;
                                            if (isBorrowing) {
//                                                Util.showToast(LibraryBookDetailsSecondActivity.this, "图书续借成功");
                                                Toast.makeText(LibraryBookDetailsSecondActivity.this, "图书续借成功", Toast.LENGTH_SHORT).show();
                                                Ln.d("LibraryBookDetailSecondActivity:图书续借成功");

                                                if (isDownload) {
                                                    mBorrowBtn.setText("阅读");
                                                } else {
                                                    mBorrowBtn.setText("下载");
                                                }
                                                BookshelfManager.getInstance().updateBookBorrowTime(mBookId);

                                                initData();
                                            } else {
//                                                Util.showToast(LibraryBookDetailsSecondActivity.this, "图书续借失败");
                                                Toast.makeText(LibraryBookDetailsSecondActivity.this, "图书续借失败", Toast.LENGTH_SHORT).show();
                                                Ln.d("LibraryBookDetailSecondActivity:图书续借失败");
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            throwable.printStackTrace();
//                                            Util.showToast(LibraryBookDetailsSecondActivity.this, throwable.getMessage());
//                                            Toast.makeText(LibraryBookDetailsSecondActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {

                                        }
                                    });
                        }
                    });
        } else {
            Toast.makeText(LibraryBookDetailsSecondActivity.this, "图书续借失败", Toast.LENGTH_SHORT).show();
            Ln.d("LibraryBookDetailSecondActivity:用户信息为空");
        }
    }

    @OnClick(R.id.btn_library_book_detail_return)
    public void onReturnClick() {//归还
        if (mUserInfo != null) {
            GDownloader.getInstance().delete(mBookDetail.getDownloadUrl());
            progressbar.setProgress(0);
            progressbar.setVisibility(View.GONE);
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
                            //错误
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            //完成
                            BookshelfManager.getInstance().returnBookById(
                                    mUserInfo.getUserid(),
                                    mToken,
                                    mBookDetail.getEntityId(),
                                    UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND).getOrg_id())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<PgResult>() {
                                        @Override
                                        public void call(PgResult result) {
                                            if (result.isStatus()) {

//                                                Util.showToast(LibraryBookDetailsSecondActivity.this, "图书归还成功");
                                                Toast.makeText(LibraryBookDetailsSecondActivity.this, "图书归还成功", Toast.LENGTH_SHORT).show();
                                                Ln.d("LibraryBookDetailSecondActivity:图书归还成功");
                                                BookshelfManager.getInstance().removeBookById(mBookId);
                                                File file=new File(DatabaseManager.getInstance().queryBookById(mBookId).getLocalUrl());
                                                deleteFile(file);
                                                initData();

                                                Intent intent = new Intent();
                                                intent.setAction(Constant.ACTION_REFRESH_DATA);
                                                sendBroadcast(intent);

                                            } else {
//                                                Util.showToast(LibraryBookDetailsSecondActivity.this, "图书归还失败");
                                                Toast.makeText(LibraryBookDetailsSecondActivity.this, "图书归还失败:" + result.getMessage(), Toast.LENGTH_SHORT).show();
                                                Ln.d("LibraryBookDetailSecondActivity:图书归还失败");
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            throwable.printStackTrace();
//                                            Util.showToast(LibraryBookDetailsSecondActivity.this, throwable.getMessage());
//                                            Toast.makeText(LibraryBookDetailsSecondActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {

                                        }
                                    });
                        }
                        public void deleteFile(File file) {

                            if (file.exists()) { // 判断文件是否存在
                                if (file.isFile()) { // 判断是否是文件
                                    file.delete(); // delete()方法 你应该知道 是删除的意思;
                                } else if (file.isDirectory()) { // 否则如果它是一个目录
                                    File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                                        this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                                    }
                                }
                                file.delete();
                            }
                        }
                    });
        }
    }

    @OnClick(R.id.btn_library_book_detail_reservation)
    public void onReservationClick() {//预订
        if (mUserInfo != null) {
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
                            //错误
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            //完成
                            LibraryManager.getInstance().reservationsBookById(
                                    mUserInfo.getUserid(),
                                    mToken,
                                    mBookDetail.getEntityId(),
                                    UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND).getOrg_id())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<Boolean>() {
                                        @Override
                                        public void call(Boolean aBoolean) {
                                            if (aBoolean) {
                                                Ln.d("LibraryBookDetailSecondActivity:图书预订成功");
//                                                Util.showToast(LibraryBookDetailsSecondActivity.this, "图书预订成功");
                                                Toast.makeText(LibraryBookDetailsSecondActivity.this, "图书预订成功", Toast.LENGTH_SHORT).show();
                                                initData();
                                            } else {
//                                                Util.showToast(LibraryBookDetailsSecondActivity.this, "图书预订失败");
                                                Toast.makeText(LibraryBookDetailsSecondActivity.this, "图书预订失败", Toast.LENGTH_SHORT).show();
//                                                Ln.d("LibraryBookDetailSecondActivity:图书预订失败");
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            throwable.printStackTrace();
                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {

                                        }
                                    });
                        }
                    });
        }
    }
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create();
        builder.show();
    }
    private UserInfo mUserInfoFirst;
    private UserInfo mUserInfoSecond;
    private String mUserName;
    public void getname(){ mUserInfoFirst = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);
        mUserInfoSecond = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND);

        if(mUserInfoFirst != null){
            mUserName = mUserInfoFirst.getUsername();
        }else {
            mUserName = mUserInfoSecond.getUsername();
        }
    }

    SharedPreferences spp;
    int Number=0;
    @OnClick(R.id.btn_library_book_detail_download)
    public void onDownloadClick() {//下载
        Ln.d("LibraryBookDetailsSecondActivity:onDownloadClick");
        String name = String.valueOf( mBookDetail.getEntityId());
        spp=getSharedPreferences("share",MODE_PRIVATE);
//        if(Number==0){
//            spp.edit().putLong(name,0);
//            spp.edit().commit();
//            Number=1;
//        }
        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
            if(mUserName.equals("t200100001")) {//判断是否是会展用户
                long nowTime = System.currentTimeMillis() / 1000l;
                //读取SharePreference的内容
                long time = spp.getLong(name, 0);
                Log.i("CC", time + "----");
                if (time == 0) {
                    saveToSharedPreference(name, nowTime);
                    if (isDownload) {
                        //执行打开操作
                        readBook();
                    } else {
                        download();
                    }
                } else {
                    long l = 0;
                    if (nowTime - time > 60 * 15) {
                        getReturnBook(mBookDetail);
//                    spp.edit().putLong(name, 0);
//                    Log.i("aaa", spp.getLong(name, 0) + "");
//                    spp.edit().commit();
                        saveToSharedPreference(name, l);
                        Log.i("abbb", spp.getLong(name, 0) + "");
                        showDialog("您下载的资源已到期，请重新打开此页面，点击下载");
                    } else {
                        if (isDownload) {
                            //执行打开操作
                            readBook();
                        } else {
                            download();
                        }
                    }
                }
                //2017.11.08  ymd  本社t用户可下载自有资源
            }else if(mUserName.equals("t200100005")){//判断是否为本社t用户
                if (isDownload) {
                    //执行打开操作
                    readBook();
                } else {
                    download();
                }
            }else{
                showDialog("您是临时用户，请联系管理员");
            }
        }else {
            if (isDownload) {
                //执行打开操作
                readBook();
            } else {
                download();
            }
        }
    }

    private void getReturnBook(PgBookForLibraryDetail BookDetail) {
        GDownloader.getInstance().delete(BookDetail.getDownloadUrl());
        //完成
        BookshelfManager.getInstance().removeBookById(BookDetail.getEntityId());
//        initBookshelfData(mGroup);
        ProvisionalityDatabaseHelper pdHelper = new ProvisionalityDatabaseHelper(this);
        pdHelper.del(BookDetail.getEntityId());

    }

    public void saveToSharedPreference(String name , Long time){
        //得到sharedpreference的编辑器
        SharedPreferences.Editor editor = spp.edit();
        editor.putLong(name, time);
        //提交数据
        editor.commit();

    }
    public void download(){

            //执行下载操作
            if (mBookDetail.getDownloadUrl() != null) {
                Ln.d("LibraryBookDetailsSecondActivity:onDownloadClick:download:" + mBookDetail.getDownloadUrl());
                BookshelfManager.getInstance().insertBookForLibrary(mBookDetail);
                if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_SECOND_LEVEL ||
                        UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
                    PublicManager.getInstance().downloadBook(mBookDetail.getDownloadUrl());
                    Log.i("bbbb",mBookDetail.getType()+"");
                } else {
                    Toast.makeText(LibraryBookDetailsSecondActivity.this, "亲爱的用户，不对头呦！你们单位网络把我挡外面啦，领我进去嘛", Toast.LENGTH_SHORT).show();
                }
            } else {
                Ln.d("LibraryBookDetailsSecondActivity:onDownloadClick:download:error");
                Toast.makeText(mContext, "获取下载地址失败", Toast.LENGTH_SHORT).show();
            }

    }
    private void initData() {
        Ln.d("LibraryBookDetailSecondActivity:initData");
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(mContext, null, "加载中...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(true);
        }

        if (mUserInfo != null) {
            UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_SECOND)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            mToken = s;
                            Ln.d("LibraryBookDetailSecondActivity:getTokenAsync:" + mToken);

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            //错误
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                            Ln.d("LibraryBookDetailSecondActivity:getTokenAsync:error:" + throwable.getMessage());
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            //完成
                            Ln.d("LibraryBookDetailSecondActivity:getTokenAsync:ok");
                            LibraryManager.getInstance().getSecondBookDetailById(mToken, mBookId, mUserInfo.getUserid(), mBookDetailType)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<PgBookForLibraryDetail>() {
                                        @Override
                                        public void call(PgBookForLibraryDetail pgBookForLibraryDetail) {
                                            Ln.d("LibraryBookDetailSecondActivity:pgBookForLibraryDetail:" + pgBookForLibraryDetail.toString());
                                            mBookDetail = pgBookForLibraryDetail;
//                                            Toast.makeText(LibraryBookDetailsSecondActivity.this, "下载地址：" + mBookDetail.toString(), Toast.LENGTH_LONG).show();
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
                                            Ln.d("LibraryBookDetailSecondActivity:mBookDetail:" + mBookDetail.toString());
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                mProgressDialog = null;
                                            }
                                                refresh();
                                        }
                                    });
                        }
                    });
        } else {
            Ln.d("LibraryBookDetailSecondActivity:initData:mUserInfo == null");
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    private void refresh() {

        DbBookshelfEntity db = DatabaseManager.getInstance().queryBookById(mBookDetail.getEntityId());
        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
            if(db == null){
                isBorrowing = false;
            }else {
                isBorrowing = true;
            }
        }else{
            isBorrowing = mBookDetail.isBorrowing();
            isCanBorrowing = mBookDetail.isCanBorrowing();
            isCanRenew = mBookDetail.isCanRenew();
            isReservation = mBookDetail.isReservations();
        }
        isDownload = PublicManager.getInstance().isDownloadOk(mBookDetail.getDownloadUrl());

        Glide.with(LibraryBookDetailsSecondActivity.this)
                .load(mBookDetail.getFrontCover())
                .placeholder(R.drawable.drw_book_default)
                .into(ivBookUrl);

        Ln.d("LibraryBookDetailSecondActivity:Price:" + mBookDetail.getPrice());

        tvBookName.setText(mBookDetail.getName());
        tvPrice.setText(mBookDetail.getPrice() + "元");

        author.setText(mBookDetail.getAuthor());
        uploadTime.setText(mBookDetail.getPublishDate());
        bookNumber.setText(String.valueOf(mBookDetail.getBookNumber()));
        publishInstitution.setText(String.valueOf(mBookDetail.getPublishingHouse()));
        tvContentBrief.setText(mBookDetail.getIntroduction());

        mBorrowBtn.setVisibility(View.INVISIBLE);
        mReturnBtn.setVisibility(View.INVISIBLE);
        mRenewBtn.setVisibility(View.INVISIBLE);
        mReservationBtn.setVisibility(View.INVISIBLE);
        mDownloadBtn.setVisibility(View.INVISIBLE);
        mProvisionalityBorrow.setVisibility(View.INVISIBLE);
        mProvisionalityRemove.setVisibility(View.INVISIBLE);
        if (mBookDetailType == Constant.LIBRARY_DETAIL_E_BOOK) {//图书
            if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
                if(isBorrowing){//已借阅
                    mProvisionalityBorrow.setVisibility(View.VISIBLE);
                    if (isDownload){
                        mProvisionalityBorrow.setText("阅读");
                    }else{
                        mProvisionalityBorrow.setText("下载");
                    }
                    mProvisionalityRemove.setVisibility(View.VISIBLE);
                }else{
                    mProvisionalityBorrow.setText("借阅");
                    mProvisionalityBorrow.setVisibility(View.VISIBLE);
                }
            }else{
                if (isBorrowing) {//已借阅
                    mBorrowBtn.setVisibility(View.VISIBLE);
                    if (isDownload) {
                        //执行打开操作
                        mBorrowBtn.setText("阅读");
                    } else {
                        //执行下载操作
                        mBorrowBtn.setText("下载");
                    }
                } else {//未借阅
                    if (isCanBorrowing) {
                        //显示借阅
                        mBorrowBtn.setText("借阅");
                        mBorrowBtn.setVisibility(View.VISIBLE);
                    } else {
                        mReservationBtn.setVisibility(View.VISIBLE);
                        if (isReservation) {//已预订
                            //显示已预订
                            mReservationBtn.setText("已预订");
                        } else {
                            //显示预订
                            mReservationBtn.setText("预订");
                        }
                    }
                }

                if (isCanRenew) {//能归还
                    mReturnBtn.setVisibility(View.VISIBLE);
                    mRenewBtn.setVisibility(View.VISIBLE);
                    mReturnBtn.setText("归还");
                    mRenewBtn.setText("续借");
                } else {
                    mReturnBtn.setVisibility(View.INVISIBLE);
                    mRenewBtn.setVisibility(View.INVISIBLE);
                }
            }

        } else {//自有资源和共享资源
            //显示是下载还是打开
            mDownloadBtn.setVisibility(View.VISIBLE);
            if (isDownload) {
                //显示打开
                mDownloadBtn.setText("打开");
            } else {
                //执行下载操作
                mDownloadBtn.setText("下载");
            }
        }

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

    public void onEventMainThread(DownloadEvent event) {
        final int bookId = Util.getBookIdByUrl(event.url);

        if (bookId == mBookDetail.getEntityId()) {
            mBookDetail.setDownloadState(event.state);

            Ln.d("LibraryBookDetailSecondActivity:onEventMainThread:"
                    + event.state + "," + event.downloadedBytes + "/" + event.totalLength);

            switch (event.event) {
                case DownloadEvent.EVENT_PROGRESS:
                    mBookDetail.setDownloadProgress((float) event.downloadedBytes / (float) event.totalLength);
                    refreshDownloadState();
                    break;
                case DownloadEvent.EVENT_CANCEL:
                    progressbar.setProgress(0);
                    progressbar.setVisibility(View.GONE);
                    break;
                case DownloadEvent.EVENT_FINISH:
                    refresh();
                    break;
                default:
                    refreshDownloadState();
                    break;
            }
        }
    }

    private void refreshDownloadState() {
        Ln.d("LibraryBookDetailSecondActivity:refreshDownloadState");
        if (mBookDetail.getDownloadProgress() != 0) {
            if (mBookDetail.getDownloadProgress() < 0.01) {
                progressbar.to(0.01F);
            } else {
                progressbar.to(mBookDetail.getDownloadProgress());
            }
        }

        switch (mBookDetail.getDownloadState()) {
            case DbDownloadExt.DOWNLOAD_STATE_NOT_DOWNLOAD:
                progressbar.setVisibility(View.GONE);
                break;
            case DbDownloadExt.DOWNLOAD_STATE_PREPARING:
                progressbar.setVisibility(View.VISIBLE);
                break;
            case DbDownloadExt.DOWNLOAD_STATE_DOWNLOADING:
                progressbar.setVisibility(View.VISIBLE);
                break;
            case DbDownloadExt.DOWNLOAD_STATE_PAUSE:
                progressbar.setVisibility(View.VISIBLE);
                break;
            case DbDownloadExt.DOWNLOAD_STATE_WAITING:
                progressbar.setVisibility(View.VISIBLE);
                break;
            case DbDownloadExt.DOWNLOAD_STATE_DOWNLOADED:
                progressbar.setVisibility(View.GONE);
                mBorrowBtn.setText("阅读");
                mDownloadBtn.setText("打开");
                break;
            case DbDownloadExt.DOWNLOAD_STATE_ERROR:
                progressbar.setVisibility(View.VISIBLE);
                break;
            default:
                progressbar.setVisibility(View.VISIBLE);
                break;
        }
    }

    //阅读图书
    private void readBook() {
        if (BookshelfManager.getInstance().getBookById(mBookDetail.getEntityId()) == null) {
            BookshelfManager.getInstance().insertBookForLibrary(mBookDetail);
        }

        if (mBookDetail.getDownloadUrl() != null && !mBookDetail.getDownloadUrl().equals("")) {
            if (isDownload) {//已经下载
                final String fileUrl = PublicManager.getInstance().getFilePath(mBookDetail.getDownloadUrl());
                final String readUrl = com.digital.dl2.business.util.Constant.FILE_READ_PATH + String.valueOf(mBookDetail.getEntityId());
                String[] uuids = UserModule.getInstance().getUUIDs();

                Ln.d("LibraryBookDetailsSecondActivity:readBook:阅读:download:" + fileUrl);
                Ln.d("LibraryBookDetailsSecondActivity:readBook:阅读:read:" + readUrl);
                Ln.d("LibraryBookDetailsSecondActivity:readBook:阅读:uuids:" + new Gson().toJson(uuids));
//                Toast.makeText(mContext, "借阅时间:"+BookshelfManager.getInstance().getBookById(mBookDetail.getEntityId()).getBorrowTime(), Toast.LENGTH_LONG).show();
                if (fileUrl != null && readUrl != null) {
                    int unzipState = BookshelfManager.getInstance().getBookById(mBookDetail.getEntityId()).getUnzipState();
                    Ln.d("LibraryBookDetailsSecondActivity:readBook:阅读:unzipState:" + unzipState);
                    if (unzipState == com.digital.dl2.business.util.Constant.BOOK_UNZIP_STATE_SUCCESS) {
                        String succeed = null;
                        if (fileUrl.endsWith("tdp")) {
                            if (uuids.length != 0) {
                                succeed = BookUtils.openTdpBook(
                                        fileUrl,
                                        readUrl,
                                        uuids,
                                        mContext);
                                Ln.d("LibraryBookDetailsSecondActivity:readBook:阅读:succeed:" + succeed);
                            } else {
                                Toast.makeText(mContext, "用户信息出错,请重新登录", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            BookUtils.openFile(
                                    fileUrl,
                                    mContext);
                            Ln.d("LibraryBookDetailsSecondActivity:readBook:阅读:打开自有资源");
                        }

                    } else if (unzipState == com.digital.dl2.business.util.Constant.BOOK_UNZIP_STATE_ING) {
                        Toast.makeText(mContext, "文件正在解压", Toast.LENGTH_SHORT).show();
                    } else {
                        Ln.d("LibraryBookDetailsSecondActivity:readBook:文件未解压");
                        Toast.makeText(mContext, "文件解压失败，正在重新尝试", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(mContext, MyService.class);
                        intent.putExtra(MyService.INTENT_CMD, MyService.CMD_UN_ZIP_FILE);
                        intent.putExtra(Constant.BOOK_ID, mBookDetail.getEntityId());
                        mContext.startService(intent);
                    }
                } else {
                    Ln.d("LibraryBookDetailsSecondActivity:readBook:文件路径为空");

                    Toast.makeText(mContext, "获取路径失败,请重新尝试", Toast.LENGTH_SHORT).show();
                }

            } else {
                Ln.d("LibraryBookDetailsSecondActivity:readBook:未register下载:downloadUrl:" + mBookDetail.getDownloadUrl());
                if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_SECOND_LEVEL ||
                        UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
                    PublicManager.getInstance().downloadBook(mBookDetail.getDownloadUrl());
                } else {
                    Toast.makeText(LibraryBookDetailsSecondActivity.this, "亲爱的用户，不对头呦！你们单位网络把我挡外面啦，领我进去嘛", Toast.LENGTH_SHORT).show();
                }
//                             itemView.setEnabled(false);
            }
        } else {
            Ln.d("LibraryBookDetailsSecondActivity:readBook:下载路径出错:" + mBookDetail.getDownloadUrl());
            Toast.makeText(mContext, "获取下载路径失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("二级图书馆图书详情页");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("二级图书馆图书详情页");
        MobclickAgent.onPause(this);
    }
}
