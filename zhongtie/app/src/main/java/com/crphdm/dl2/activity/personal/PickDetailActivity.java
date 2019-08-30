package com.crphdm.dl2.activity.personal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.crphdm.dl2.R;
import com.crphdm.dl2.adapter.personal.PickDetailAdapter;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.obj.PgMiningMenuDetail;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 采选详情页面
 */
public class PickDetailActivity extends AppCompatActivity {
    //采选单号
    @Bind(R.id.tv_id)
    TextView mId;
    //价格
    @Bind(R.id.tv_press_price)
    TextView mPressPrice;
    //时间
    @Bind(R.id.tv_time)
    TextView mTime;
    //实付价格
    @Bind(R.id.tv_press)
    TextView mPrice;
    //批注
    @Bind(R.id.tv_comments)
    TextView mComments;
    //采选总数
    @Bind(R.id.tv_book_count)
    TextView mBookCount;
    @Bind(R.id.rcl_pick_detail_list)
    RecyclerView mPickDetailList;

    private int mMiningMenuId;

    private  PickDetailAdapter mAdapter;

    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;

    private PgMiningMenuDetail mMiningMenuDetail;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_detail);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);

        mMiningMenuId = getIntent().getIntExtra(Constant.PICK_MINING_MENU_ID,0);

        initMembers();
    }
    //初始化成员
    private void initMembers() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPickDetailList.setLayoutManager(linearLayoutManager);
        mPickDetailList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new PickDetailAdapter(PickDetailActivity.this);
        mPickDetailList.setAdapter(mAdapter);


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
                        Ln.d("PickDetailActivity:initData:getToken:" + mToken);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Ln.d("PickDetailActivity:initData:getToken:error" + throwable.getMessage());
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        throwable.printStackTrace();
//                        Toast.makeText(PickDetailActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Ln.d("PickDetailActivity:initData:getToken:ok");
                        initData();
                    }
                });
    }
    //初始化数据
    private void initData() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, null, "加载中...");
        }

        Ln.d("PickDetailActivity:initData:getMiningMenuDetail");

        PersonalCenterManager.getInstance().getMiningMenuDetail(mUserInfo.getUserid(), mToken, mUserInfo.getOrg_id(), mMiningMenuId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<PgMiningMenuDetail>() {
                    @Override
                    public void call(PgMiningMenuDetail pgMiningMenuDetail) {
                        mMiningMenuDetail = pgMiningMenuDetail;
                        Ln.d("PickDetailActivity:initData:mMiningMenuDetail:" + mMiningMenuDetail.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Ln.d("PickDetailActivity:initData:getMiningMenuDetail:error:" + throwable.getMessage());
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Ln.d("PickDetailActivity:initData:getMiningMenuDetail:完成");
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        refreshData();
                    }
                });
    }
    //刷新数据
    private void refreshData(){
        if(mMiningMenuDetail != null){
            Ln.d("PickDetailActivity:refreshData:mMiningMenuDetail:" + mMiningMenuDetail.toString());
            mId.setText(String.valueOf(mMiningMenuDetail.getEntityId()));
            mTime.setText(format.format(mMiningMenuDetail.getMiningDate() * 1000));
            mPressPrice.setText("￥" + mMiningMenuDetail.getPreferentialPrice());
            mPrice.setText("￥" + mMiningMenuDetail.getAllPrice());
            if(mMiningMenuDetail.getAnnotation().equals("null")|| mMiningMenuDetail.getAnnotation().equals("")){
                mComments.setText("批注");
            }else{
                mComments.setText(mMiningMenuDetail.getAnnotation());
            }
            mBookCount.setText(String.valueOf(mMiningMenuDetail.getNumber()));

            mAdapter.setData(mMiningMenuDetail.getShoppingCartItemList());
            mAdapter.notifyDataSetChanged();
        }else {
            Ln.d("PickDetailActivity:refreshData:mMiningMenuDetail == null");
        }
    }
    ////处理菜单被选中运行后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
    //恢复
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("采选详情页面");
        MobclickAgent.onResume(this);
    }
    //暂停
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("采选详情页面");
        MobclickAgent.onPause(this);
    }
}
