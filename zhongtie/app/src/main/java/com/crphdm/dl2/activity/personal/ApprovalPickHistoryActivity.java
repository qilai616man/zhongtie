package com.crphdm.dl2.activity.personal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.obj.PgReviewMiningListData;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ApprovalPickHistoryActivity extends AppCompatActivity {
    //采选历史页面
    @Bind(R.id.rcl_pick_history_list)
    RecyclerView mPickHistoryList;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;
    @Bind(R.id.load)
    FrameLayout load;
    private int index = 2;

    private ApprovalListAdapter mAdapter;

    private ProgressDialog mProgressDialog;
    private UserInfo mUserInfo;
    private String mToken;

    private List<PgReviewMiningListData> mListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_pick_history);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);
        initMembers();
    }

    @OnClick(R.id.lLError)
    public void onErrorClick() {
        initMembers();
    }

    private void initMembers() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mPickHistoryList.setLayoutManager(linearLayoutManager);

        //Constant.FLAG_PICK_HISTORY  从历史页面进入
        mAdapter = new ApprovalListAdapter();
        mPickHistoryList.setAdapter(mAdapter);

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);

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
                            if (lLError != null) {
                                lLError.setVisibility(View.VISIBLE);
                            }
                            Ln.d("PickHistoryActivity:initMembers:getToken:error:" + throwable.getMessage());
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            Ln.d("PickHistoryActivity:initMembers:getToken:OK");
                            initData();
                        }
                    });
        }else {
            if (lLError != null) {
                lLError.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initData() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(ApprovalPickHistoryActivity.this, null, "加载中");
        }
        PersonalCenterManager.getInstance().getReviewMiningList(
                mUserInfo.getUserid(),
                mToken,
                mUserInfo.getOrg_id(),
                Constant.APPROVAL_STATUS_YES,
                1, 50)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgReviewMiningListData>>() {
                    @Override
                    public void call(List<PgReviewMiningListData> pgReviewMiningListDatas) {
                        mListData = pgReviewMiningListDatas;
                        Ln.d("ApprovalListActivity:initData:getReviewMiningList:mListData:" + mListData);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (lLError != null) {
                            lLError.setVisibility(View.VISIBLE);
                        }
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        Ln.d("ApprovalListActivity:initData:getReviewMiningList:error:" + throwable.getMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {

                        if (lLError != null) {
                            if (mListData.size() == 0) {
                                lLError.setVisibility(View.VISIBLE);
                                tvError.setText("暂无数据");
                            } else {
                                lLError.setVisibility(View.GONE);
                            }
                        }

                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        Ln.d("ApprovalListActivity:initData:getReviewMiningList:ok");
                        mAdapter.notifyDataSetChanged();
                    }
                });

        mPickHistoryList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean loading = false;
            boolean end = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (end)
                    return;
                if (mAdapter != null && newState == RecyclerView.SCROLL_STATE_IDLE && !loading
                        && mAdapter.getItemCount() > 0 && (((LinearLayoutManager) mPickHistoryList.getLayoutManager()).findLastVisibleItemPosition() + 1) == mAdapter.getItemCount()) {
                    loading = true;
                    load.setVisibility(View.VISIBLE);

                    PersonalCenterManager.getInstance().getReviewMiningList(
                            mUserInfo.getUserid(),
                            mToken,
                            mUserInfo.getOrg_id(),
                            Constant.APPROVAL_STATUS_YES,
                            index++, 30)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(new Action1<List<PgReviewMiningListData>>() {
                                @Override
                                public void call(List<PgReviewMiningListData> pgReviewMiningListDatas) {
                                    if (pgReviewMiningListDatas == null || pgReviewMiningListDatas.isEmpty()) {
                                        end = true;
                                        return;
                                    }
                                    mListData.addAll(pgReviewMiningListDatas);
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    loading = false;
                                    load.setVisibility(View.GONE);
                                }
                            }, new Action0() {
                                @Override
                                public void call() {
                                    loading = false;
                                    load.setVisibility(View.GONE);
                                    mAdapter.notifyDataSetChanged();

                                }
                            });
                }
            }
        });

    }

    class ApprovalListAdapter extends RecyclerView.Adapter<CustomViewHolder> {
        private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CustomViewHolder customViewHolder = new CustomViewHolder(getLayoutInflater().inflate(R.layout.item_approval_history_select, parent, false));

            return customViewHolder;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            final PgReviewMiningListData reviewMining = mListData.get(position);
            Ln.d("ApprovalListAdapter:PgReviewMiningListData:" + reviewMining.toString());
            holder.selectNumber.setText(String.valueOf(reviewMining.getDemandId()));
            holder.time.setText(format.format(reviewMining.getSubmitTime() * 1000));
            holder.submitPeople.setText(reviewMining.getSubmitPeople());
            holder.priceAll.setText(String.valueOf(reviewMining.getAllPrice()));
            holder.pricePrivilege.setText(String.valueOf(reviewMining.getPreferentialPrice()));
            Ln.d("ApprovalListAdapter:getReject:" + reviewMining.getReject());
            if(reviewMining.getReject().equals("") || reviewMining.getReject().equals("null")){
                holder.comment.setText("批注");
            }else{
                holder.comment.setText(reviewMining.getReject());
            }

            if (reviewMining.getStatus() == 0) {//待审核
                holder.state.setText("待审核");
            } else if (reviewMining.getStatus() == 1) {//已审核
                holder.state.setText("已审核");
            } else if (reviewMining.getStatus() == 2) {//打回
                holder.state.setText("打回");
            }

            holder.itemLinerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ApprovalPickDetailActivity.class);
                    intent.putExtra(Constant.PICK_MINING_MENU_ID, reviewMining.getDemandId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mListData == null ? 0 : mListData.size();
        }
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.select_number)
        TextView selectNumber;
        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.submit_people)
        TextView submitPeople;
        @Bind(R.id.price_all)
        TextView priceAll;
        @Bind(R.id.state)
        TextView state;
        @Bind(R.id.price_privilege)
        TextView pricePrivilege;
        @Bind(R.id.edit_text_write_comment)
        TextView comment;
        @Bind(R.id.item_liner_layout)
        LinearLayout itemLinerLayout;

        CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("采选历史页面");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("采选历史页面");
        MobclickAgent.onPause(this);
    }
}
