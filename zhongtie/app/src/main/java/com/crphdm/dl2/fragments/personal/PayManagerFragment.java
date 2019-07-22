package com.crphdm.dl2.fragments.personal;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.personal.PayManagerDetailActivity;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.obj.PgOrderList;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
/**
 * 支付管理 下面的列表
 */
public class PayManagerFragment extends Fragment {
    private int page = 1;
    public static final int COUNT = 50;
    public static final String ARG_TYPE = "TYPE";
    public static final int TYPE_ALL = 0x00;
    public static final int TYPE_PAYED = 0x01;
    public static final int TYPE_UNPAYED = 0x02;
    @Bind(R.id.recycler)
    RecyclerView mRecycler;
    @Bind(R.id.load)
    FrameLayout load;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;

    private int mType;
    private List<PgOrderList> list = new ArrayList<>();
    private CustomAdapter mAdapter;

    private UserInfo mUserInfo;
    private String mToken;
    private ProgressDialog mProgressDialog;

    public static PayManagerFragment newInstance(int type) {
        PayManagerFragment payManagerFragment = new PayManagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TYPE, type);
        payManagerFragment.setArguments(bundle);
        return payManagerFragment;
    }

    public PayManagerFragment() {
        // Required empty public constructor
    }
    //初始化Fragment。
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt(ARG_TYPE);
    }
    //初始化Fragment的布局。
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pay_manager, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
    // onViewCreated在onCreateView执行完后立即执行
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMembers();
    }
    //错误页面显示
    @OnClick(R.id.lLError)
    public void onErrorClick() {
        initData();
    }
    //初始化成员
    private void initMembers() {
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new CustomAdapter();
        mRecycler.setAdapter(mAdapter);

    }
    //初始化数据
    private void initData() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getActivity(), null, "加载中...");
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

                        if (lLError != null) {
                            lLError.setVisibility(View.VISIBLE);
                        }
                        throwable.printStackTrace();
//                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        refreshData();
                    }
                });
    }
    //刷新数据
    private void refreshData() {
        PersonalCenterManager.getInstance().getOrders(mUserInfo.getUserid(), mToken, mType, 1, COUNT)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgOrderList>>() {
                    @Override
                    public void call(List<PgOrderList> orderLists) {
                        list = orderLists;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        throwable.printStackTrace();
//                        Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        if (list.size() == 0) {
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

        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean loading = false;
            boolean end = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (end)
                    return;
                if (mAdapter != null && newState == RecyclerView.SCROLL_STATE_IDLE && !loading
                        && mAdapter.getItemCount() > 0 && (((LinearLayoutManager) mRecycler.getLayoutManager()).findLastVisibleItemPosition() + 1) == mAdapter.getItemCount()) {
                    loading = true;
                    load.setVisibility(View.VISIBLE);

                    PersonalCenterManager.getInstance().getOrders(mUserInfo.getUserid(), mToken, mType, ++page, 50)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<List<PgOrderList>>() {
                                @Override
                                public void call(List<PgOrderList> orderList) {
                                    if (orderList == null || orderList.isEmpty()) {
                                        end = true;
                                        return;
                                    }
                                    list.addAll(orderList);
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
    //适配器
    public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {
        private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_personal_pay_manager, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (list != null && !list.isEmpty()) {
                final PgOrderList orderList = list.get(position);
                Ln.d("PayManagerFragment:orderList:" + orderList.toString());
                holder.buyTime.setText(format.format(orderList.getConfirmTime() * 1000));
                holder.digitalBook.setVisibility(orderList.getType() == 2 ? View.VISIBLE : View.GONE);
                holder.paperBook.setVisibility(orderList.getType() == 1 ? View.VISIBLE : View.GONE);
                holder.price.setText(String.valueOf(orderList.getPreferentialPrice()));

                Ln.d("PayManagerFragment:status:" + orderList.getStatus());
                if (orderList.getStatus() == 0) {//未支付
                    holder.buyState.setText("未付款");
                } else if (orderList.getStatus() == 1) {//已支付
                    holder.buyState.setText("已支付");
                } else if (orderList.getStatus() == 2) {//支付中遇到问题
                    holder.buyState.setText("支付中遇到问题");
                } else if (orderList.getStatus() == 3) {//取消或者删除
                    holder.buyState.setText("已取消");
                }

                holder.orderNumber.setText(orderList.getOrderSn());
                holder.goodsName.setText("点击查看详情");

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), PayManagerDetailActivity.class);
                        intent.putExtra(PayManagerDetailActivity.ORDER_ID, orderList.getOrderId());
                        startActivity(intent);
                    }
                });
            } else {
//                Toast.makeText(getActivity(),"获取数据为空",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
    }
    //销毁与Fragment有关的视图，但未与Activity解除绑定
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    //listview滚动的时候快速设置值，而不必每次都重新创建很多对象，从而提升性能。
    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.order_number)
        TextView orderNumber;
        @Bind(R.id.paper_book)
        TextView paperBook;
        @Bind(R.id.digital_book)
        TextView digitalBook;
        @Bind(R.id.price)
        TextView price;
        @Bind(R.id.buy_time)
        TextView buyTime;
        @Bind(R.id.buy_state)
        TextView buyState;
        @Bind(R.id.goods_name)
        TextView goodsName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    //执行该方法时，Fragment处于活动状态，用户可与之交互。
    @Override
    public void onResume() {
        super.onResume();
        initData();
        MobclickAgent.onPageStart("支付管理页");
    }
    //执行该方法时，Fragment处于暂停状态，但依然可见，用户不能与之交互。
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("支付管理页");
    }

}
