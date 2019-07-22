package com.crphdm.dl2.activity.personal;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.obj.PgLogisticTrace;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/6/21.
 */
public class LogisticTraceActivity extends AppCompatActivity {

//    @Bind(R.id.recycler)
    RecyclerView recycler;
    //错误提示内容
//    @Bind(R.id.tvError)
    //错误提示布局
    TextView tvError;
//    @Bind(R.id.lLError)
    LinearLayout lLError;
    //加载页面
//    @Bind(R.id.load)
    FrameLayout load;

    private String mOrderSn;
    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;
    private List<PgLogisticTrace> mList;
    private MyAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistic_trace);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mOrderSn = getIntent().getStringExtra("getOrderSn");
        Ln.d("LogisticTraceActivity:onCreate:mOrderSn:" + mOrderSn);
        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);
        recycler = (RecyclerView)findViewById(R.id.recyler);
        tvError = (TextView)findViewById(R.id.tvError);
        lLError = (LinearLayout)findViewById(R.id.lLError);
        load = (FrameLayout)findViewById(R.id.load);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new MyAdapter();
        recycler.setAdapter(mAdapter);
        initData();

    }
    //初始化数据
    private void initData() {
        if(mUserInfo != null){
            if(mProgressDialog == null){
                mProgressDialog = ProgressDialog.show(this ,null ,"加载中...");
            }
            UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            Ln.d("LogisticTraceActivity:initData:s:" + s);
                            mToken = s;
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Ln.d("LogisticTraceActivity:initData:throwable:" + throwable);
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            refreshData();
                        }

                    });

        }

    }
    //刷新数据
    private void refreshData(){
        PersonalCenterManager.getInstance().getLogisticTrace(mUserInfo.getUserid() ,"2016050667352" ,mToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgLogisticTrace>>() {
                    @Override
                    public void call(List<PgLogisticTrace> pgLogisticTraces) {
                        Ln.d("LogisticTraceActivity:refreshData:pgLogisticTraces:" + pgLogisticTraces);
                        mList = pgLogisticTraces;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Ln.d("LogisticTraceActivity:refreshData:throwable:" + throwable);
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        if (lLError != null) {
                            lLError.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        if (mList.size() == 0) {
                            if (lLError != null) {
                                lLError.setVisibility(View.VISIBLE);
                                tvError.setText("暂无数据");
                            }
                        } else {
                            if (lLError != null) {
                                lLError.setVisibility(View.GONE);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });

    }

    //适配器
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_logistic_trace ,viewGroup ,false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            PgLogisticTrace pgLogisticTrace = mList.get(position);
            holder.tvAddress.setText(pgLogisticTrace.getRemark());
            holder.tvTime.setText(pgLogisticTrace.getDatetime());
            if(position == 0){
                holder.line1.setVisibility(View.INVISIBLE);
                holder.ivStatus.setImageResource(R.drawable.ordertrace);
            }else {
                holder.line1.setVisibility(View.VISIBLE);
                holder.ivStatus.setImageResource(R.drawable.ordertrace_status);
            }
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            @Bind(R.id.View_logistic_tracking_line1)
            View line1;
            @Bind(R.id.View_logistic_tracking_line2)
            View line2;
            @Bind(R.id.mgView_logistic_tracking_status)
            ImageView ivStatus;
            @Bind(R.id.tv_logistic_tracking_address)
            TextView tvAddress;
            @Bind(R.id.tv_logistic_tracking_time)
            TextView tvTime;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    //处理菜单被选中运行后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //恢复
    public void onResume() {
        super.onResume();

        MobclickAgent.onPageStart("物流跟踪");
        MobclickAgent.onResume(this);
    }
    //暂停
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("物流跟踪");
        MobclickAgent.onPause(this);
    }
}
