package com.crphdm.dl2.activity.resource;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainsoon.utils.BookUtils;
import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.personal.PayManagerDetailActivity;
import com.crphdm.dl2.activity.personal.ShoppingCartActivity;
import com.crphdm.dl2.service.MyService;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.utils.Util;
import com.crphdm.dl2.views.BeautifulProgressbar;
import com.digital.dl2.business.core.manager.BookshelfManager;
import com.digital.dl2.business.core.manager.LibraryManager;
import com.digital.dl2.business.core.manager.PublicManager;
import com.digital.dl2.business.core.manager.ShoppingManager;
import com.digital.dl2.business.core.module.BookshelfModule;
import com.digital.dl2.business.core.obj.PgResourcesDetail;
import com.digital.dl2.business.core.obj.PgShoppingCart;
import com.digital.dl2.business.util.DownloadEvent;
import com.goyourfly.gdownloader.db.DbDownloadExt;
import com.goyourfly.gdownloader.utils.ErrorUtils;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
/**
 * 资源fragment 图书详情页
 */

public class ResourceBookDetailsActivity extends AppCompatActivity {
    @Bind(R.id.iv_book_url)
    ImageView ivBookUrl;
    @Bind(R.id.tv_book_name)
    TextView tvBookName;
    @Bind(R.id.tv_price)
    TextView tvPrice;
    @Bind(R.id.btn_resource_detail_add_shopping_cart)
    Button btnAddShoppingCart;
    @Bind(R.id.btn_resource_book_detail_download)
    Button btnDownload;
    @Bind(R.id.tv_upload_author_name)
    TextView tvAuthorName;
    @Bind(R.id.tv_upload_time)
    TextView tvUploadTime;
    @Bind(R.id.btn_publish_institution)
    TextView btnPublishInstitution;
    @Bind(R.id.tv_content_brief)
    TextView tvContentBrief;
    @Bind(R.id.progress)
    BeautifulProgressbar progressbar;
//    @Bind(R.id.list_view)
//    ListView listView;

    private int mResourceId;
    private int mType;

    private PgResourcesDetail mResourceDetail;

    private boolean isDownload;
    private boolean isBuy;

    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;

    private int mShopCartCount;

    private TextView mMenuChopcastCountTextView;

    private Context mContext;

     //加入购物车按钮点击事件
    @OnClick(R.id.btn_resource_detail_add_shopping_cart)
    public void onAddShoppingCartClick() {
        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
            Toast.makeText(this,"此用户为临时用户",Toast.LENGTH_SHORT).show();
        }else{
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(ResourceBookDetailsActivity.this, null, "处理中...");
            }

            if (mUserInfo != null) {
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
                                //错误
                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                    mProgressDialog.dismiss();
                                    mProgressDialog = null;
                                }
                                Toast.makeText(mContext, "加入购物车失败", Toast.LENGTH_SHORT).show();
                            }
                        }, new Action0() {
                            @Override
                            public void call() {
                                //完成
                                ShoppingManager.getInstance().addBookToShoppingCart(mUserInfo.getUserid(), mToken, mResourceId, 2, 1)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.newThread())
                                        .subscribe(new Action1<List<PgShoppingCart>>() {
                                            @Override
                                            public void call(List<PgShoppingCart> pgShoppingCarts) {
                                                Toast.makeText(mContext, "加入购物车成功", Toast.LENGTH_SHORT).show();
                                            }
                                        }, new Action1<Throwable>() {
                                            @Override
                                            public void call(Throwable throwable) {
                                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                    mProgressDialog.dismiss();
                                                    mProgressDialog = null;
                                                }
                                                Toast.makeText(mContext, "加入购物车失败 " + ErrorUtils.getError(throwable), Toast.LENGTH_SHORT).show();
                                            }
                                        }, new Action0() {
                                            @Override
                                            public void call() {
                                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                    mProgressDialog.dismiss();
                                                    mProgressDialog = null;
                                                }
                                                setupActionbar();
                                            }
                                        });
                            }
                        });
            }else {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

            }
        }

    }
    //购买按钮点击事件
    @OnClick(R.id.btn_resource_book_detail_download)
    public void onDownloadClick() {
        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){

            Toast.makeText(mContext, "此用户为临时用户", Toast.LENGTH_SHORT).show();
        }else{
            final String fileUrl = PublicManager.getInstance().getFilePath(mResourceDetail.getDownloadUrl());
            if (isBuy) {//已经购买  执行下载操作
                if (isDownload) {//已经下载  执行打开
                    //打开
                    readBook();
                } else {
                    BookshelfModule.getInstance().insertResource(mResourceDetail);
                    PublicManager.getInstance().downloadBook(mResourceDetail.getDownloadUrl());
                }
            } else {//购买资源
//            Toast.makeText(ResourceBookDetailsActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ResourceBookDetailsActivity.this, PayManagerDetailActivity.class);
                intent.putExtra(Constant.SHOPPING_CART_ORDER_TYPE, Constant.E_BOOK);
                intent.putExtra(Constant.BOOK_ID, mResourceId);
                startActivityForResult(intent, PayManagerDetailActivity.REQUEST_CODE);
            }
        }

    }
    //出版社
    @OnClick(R.id.btn_publish_institution)
    public void onPublishInstitutionClick() {
//        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
//            Toast.makeText(mContext, "此用户为临时用户", Toast.LENGTH_SHORT).show();
//        }else{

            Intent intent = new Intent(ResourceBookDetailsActivity.this, ResourceOrgListActivity.class);
            intent.putExtra(Constant.INSTITUTION_ID, mResourceDetail.getAgencyId());
            intent.putExtra(Constant.RESOURCE_TYPE, mType);
            startActivity(intent);
//        }

    }

    //阅读图书
    private void readBook() {
        if (BookshelfManager.getInstance().getBookById(mResourceDetail.getEntityId()) == null) {
            BookshelfManager.getInstance().insertResource(mResourceDetail);
        }

        if (mResourceDetail.getDownloadUrl() != null && !mResourceDetail.getDownloadUrl().equals("")) {
            if (isDownload) {//已经下载
                final String fileUrl = PublicManager.getInstance().getFilePath(mResourceDetail.getDownloadUrl());
                final String readUrl = com.digital.dl2.business.util.Constant.FILE_READ_PATH + String.valueOf(mResourceDetail.getEntityId());
                String[] uuids = UserModule.getInstance().getUUIDs();

                Ln.d("LibraryBookDetailsSecondActivity:readBook:阅读:download:" + fileUrl);
                Ln.d("LibraryBookDetailsSecondActivity:readBook:阅读:read:" + readUrl);
                Ln.d("LibraryBookDetailsSecondActivity:readBook:阅读:uuids:" + uuids);

                if (fileUrl != null && readUrl != null) {
                    int unzipState = BookshelfManager.getInstance().getBookById(mResourceDetail.getEntityId()).getUnzipState();
                    Ln.d("LibraryBookDetailsSecondActivity:readBook:阅读:unzipState:" + unzipState);
                    if (unzipState == com.digital.dl2.business.util.Constant.BOOK_UNZIP_STATE_SUCCESS) {
                        String succeed = null;
                        if (fileUrl.endsWith("tdp")) {
                            succeed = BookUtils.openTdpBook(
                                    fileUrl,
                                    readUrl,
                                    uuids,
                                    mContext);
                            Ln.d("LibraryBookDetailsSecondActivity:readBook:阅读:succeed:" + succeed);
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
                        intent.putExtra(Constant.BOOK_ID, mResourceDetail.getEntityId());
                        mContext.startService(intent);
                    }
                } else {
                    Ln.d("LibraryBookDetailsSecondActivity:readBook:文件路径为空");

                    Toast.makeText(mContext, "获取路径失败,请重新尝试", Toast.LENGTH_SHORT).show();
                }

            } else {
                Ln.d("LibraryBookDetailsSecondActivity:readBook:未下载下载:downloadUrl:" + mResourceDetail.getDownloadUrl());
                PublicManager.getInstance().downloadBook(mResourceDetail.getDownloadUrl());
//                             itemView.setEnabled(false);
            }
        } else {
            Ln.d("LibraryBookDetailsSecondActivity:readBook:下载路径出错:" + mResourceDetail.getDownloadUrl());
            Toast.makeText(mContext, "获取下载路径失败", Toast.LENGTH_SHORT).show();
        }
    }
    //销毁
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    //创建
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_book_detail);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mResourceId = getIntent().getIntExtra(Constant.BOOK_ID, 0);
        mType = getIntent().getIntExtra(Constant.RESOURCE_TYPE, 0);
        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
            btnAddShoppingCart.setVisibility(View.GONE);
            btnDownload.setVisibility(View.GONE);
        }else{
            btnAddShoppingCart.setVisibility(View.VISIBLE);
            btnDownload.setVisibility(View.VISIBLE);
        }
        Ln.d("ResourceBookDetailActivity:mType:" + mType);

        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(ResourceBookDetailsActivity.this, null, "加载中...");
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
//                        Toast.makeText(ResourceBookDetailsActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {

                        initMembers(mResourceId);
                    }
                });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mResourceId = intent.getIntExtra(Constant.BOOK_ID, 0);
        mType = intent.getIntExtra(Constant.RESOURCE_TYPE, 0);

        initMembers(mResourceId);

    }

    private void initMembers(final int id) {
        Ln.d("ResourceBookDetailActivity:mResourceId:" + id);
        mContext = this;

        if (mUserInfo != null) {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(ResourceBookDetailsActivity.this, null, "加载中...");
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
                            //错误
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            //完成
                            LibraryManager.getInstance().getResourcesDetailById(mUserInfo.getUserid(), mToken, id)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<PgResourcesDetail>() {
                                        @Override
                                        public void call(PgResourcesDetail pgResourcesDetail) {
                                            mResourceDetail = pgResourcesDetail;
                                            Ln.d("ResourceBookDetailActivity:mResourceDetail:" + mResourceDetail.toString());
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

                                            refreshData(mResourceDetail);
                                        }
                                    });
                        }
                    });
        }
    }

    //刷新数据
    private void refreshData(PgResourcesDetail resource) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (resource != null) {
            isDownload = PublicManager.getInstance().isDownloadOk(resource.getDownloadUrl());
            isBuy = resource.isBuy();
            Glide.with(ResourceBookDetailsActivity.this)
                    .load(resource.getFrontCover())
                    .placeholder(R.drawable.drw_book_default)
                    .into(ivBookUrl);

            tvBookName.setText(resource.getName());
            tvPrice.setText(resource.getPrice() + "元");
            tvAuthorName.setText(resource.getAuthor());
            tvUploadTime.setText(sdf.format(resource.getUploadTime() * 1000));
            if (resource.getAgencyName() != null && resource.getAgencyName().equals("")) {
                btnPublishInstitution.setText("中国铁道社");
            } else {
                btnPublishInstitution.setText(resource.getAgencyName());
            }
            tvContentBrief.setText(resource.getIntroduction());

            if (isBuy) {//已经购买
                if (isDownload) {//已经下载
                    btnDownload.setText("打开");
                } else {
                    btnDownload.setText("下载");
                }
            } else {
                btnDownload.setText("购买");
            }
        }
    }

    //设置actionbar购物车和采选单数量
    private void setupActionbar() {
        if (mUserInfo != null) {
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
                            //错误
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            //完成
                            ShoppingManager.getInstance().getShoppingCartBookNumber(mUserInfo.getUserid(), mToken)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread())
                                    .subscribe(new Action1<Integer>() {
                                        @Override
                                        public void call(Integer integer) {
                                            mShopCartCount = integer;
                                            Ln.d("CloudBookstoreBookDetailActivity:mShopCartCount:" + mShopCartCount);
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {

                                        }
                                    }, new Action0() {
                                        @Override
                                        public void call() {
                                            if (mShopCartCount > 0) {
                                                mMenuChopcastCountTextView.setVisibility(View.VISIBLE);
                                                mMenuChopcastCountTextView.setText(mShopCartCount + "");
                                            } else {
                                                mMenuChopcastCountTextView.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                        }
                    });


        } else {
//            Toast.makeText(CloudBookstoreBookDetailActivity.this, "mUserInfo=null", Toast.LENGTH_SHORT).show();
        }
    }

    public void onEventMainThread(DownloadEvent event) {
        final int bookId = Util.getBookIdByUrl(event.url);

        if (bookId == mResourceDetail.getEntityId()) {
            mResourceDetail.setDownloadState(event.state);

            Ln.d("LibraryBookDetailSecondActivity:onEventMainThread:"
                    + event.state + "," + event.downloadedBytes + "/" + event.totalLength);

            switch (event.event) {
                case DownloadEvent.EVENT_PROGRESS:
//                int progress = (int) (event.downloadedBytes * 100 / event.totalLength);
                    mResourceDetail.setDownloadProgress((float) event.downloadedBytes / (float) event.totalLength);
//                mBookDetail.setDownloadProgress(progress);

                    refreshDownloadState();
                    break;
                case DownloadEvent.EVENT_FINISH:
                    mResourceDetail.setDownloadState(DbDownloadExt.DOWNLOAD_STATE_DOWNLOADED);
                    refreshDownloadState();
                    refreshData(mResourceDetail);
                    break;
                default:
                    refreshDownloadState();
                    break;
            }
        }
    }
    //刷新下载状态
    private void refreshDownloadState() {
        Ln.d("LibraryBookDetailSecondActivity:refreshDownloadState");
        if (mResourceDetail.getDownloadProgress() != 0) {
            if (mResourceDetail.getDownloadProgress() < 0.01) {
                progressbar.to(0.01F);
            } else {
                progressbar.to(mResourceDetail.getDownloadProgress());
            }
        }

        switch (mResourceDetail.getDownloadState()) {
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
                break;
            case DbDownloadExt.DOWNLOAD_STATE_ERROR:
                progressbar.setVisibility(View.VISIBLE);
                break;
            default:
                progressbar.setVisibility(View.VISIBLE);
                break;
        }
    }
    //创建Menu菜单的项目
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        mMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_cloud_bookstore_book_detail, menu);
        RelativeLayout shopcartLayout = (RelativeLayout) menu.findItem(R.id.action_shopcart).getActionView();
        RelativeLayout pickDetailLayout = (RelativeLayout) menu.findItem(R.id.action_pick_detail).getActionView();
        mMenuChopcastCountTextView = (TextView) shopcartLayout.findViewById(R.id.menu_shopcart_count);
//        mMenuPickDetailCountTextView = (TextView) pickDetailLayout.findViewById(R.id.menu_pick_count);

        shopcartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 15/10/26
                Ln.d("CloudBookstoreBookDetailActivity:点击购物车");
                if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
                    Toast.makeText(ResourceBookDetailsActivity.this,"此用户为临时用户",Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(new Intent(ResourceBookDetailsActivity.this, ShoppingCartActivity.class));
                }

            }
        });

//        pickDetailLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO: 15/10/26
//                Ln.d("CloudBookstoreBookDetailActivity:点击采选单");
//                Intent intent = new Intent(CloudBookstoreBookDetailActivity.this, PickActivity.class);
//                intent.putExtra(Constant.PICK_ACTIVITY_BY_WHICH, Constant.FROM_CLOUD_BOOK_MARKET);
//                startActivity(intent);
//            }
//        });

        return true;
    }
    //每次显示菜单前都会被调用
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem pickDetailLayout = menu.findItem(R.id.action_pick_detail);
            pickDetailLayout.setVisible(false);
        MenuItem shopDetailLayout = menu.findItem(R.id.action_shopcart);
        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
            shopDetailLayout.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 重新计算ListView的高度，解决ScrollView和ListView两个View都有滚动的效果，在嵌套使用时起冲突的问题
     *
     * @param listView
     */
    public void setListViewHeight(ListView listView) {

        // 获取ListView对应的Adapter

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private class MyAdapter extends BaseAdapter {

        private List<String> catalogs;

        public void setData(List catalogs) {
            this.catalogs = catalogs;
        }

        @Override
        public int getCount() {
            return catalogs.size();
        }

        @Override
        public Object getItem(int i) {
            return catalogs.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            LinearLayout linearLayout = new LinearLayout(ResourceBookDetailsActivity.this);

            TextView textView = new TextView(ResourceBookDetailsActivity.this);
            textView.setText(catalogs.get(i));
            textView.setPadding(10, 20, 10, 20);

            linearLayout.addView(textView);

            return linearLayout;
        }
    }

    //处理菜单被选中运行后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    //接收Activity回调并处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            initMembers(mResourceId);
        }
    }
    //恢复
    public void onResume() {
        super.onResume();
        setupActionbar();
        MobclickAgent.onPageStart("资源详情页");
        MobclickAgent.onResume(this);
    }
    //暂停
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("资源详情页");
        MobclickAgent.onPause(this);
    }
}
