package com.crphdm.dl2.adapter.personal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.personal.PickDetailActivity;
import com.crphdm.dl2.utils.MyLinearLayoutManager;
import com.digital.dl2.business.core.obj.PgMiningMenuListEntity;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 采选历史adapter
 */
public class PickHistoryAdapter extends RecyclerView.Adapter<PickHistoryAdapter.ViewHolder> {
    private Context mContext;
    private List<PgMiningMenuListEntity> mPickInfoList;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public PickHistoryAdapter(Context context,List<PgMiningMenuListEntity> pickInfoList){
        mContext = context;
        mPickInfoList  = pickInfoList;
    }
    //负责承载每个子项的布局。
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pick_history_layout, parent, false);
        return new ViewHolder(view);
    }
    //负责将每个子项holder绑定数据。
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mId.setText(mPickInfoList.get(position).getEntityId()+"");
        holder.mTime.setText(format.format(mPickInfoList.get(position).getMiningDate()));
        holder.mPrice.setText(mPickInfoList.get(position).getAllPrice()+"");
        holder.mDiscount.setText(mPickInfoList.get(position).getPreferentialPrice()+"");

        MyLinearLayoutManager linearLayoutManager = new MyLinearLayoutManager(mContext);
        holder.mPickHistoryItemList.setLayoutManager(linearLayoutManager);
        PickItemHistoryAdapter pickItemHistoryAdapter = new PickItemHistoryAdapter(mContext,mPickInfoList.get(position).getBookNameList());
        holder.mPickHistoryItemList.setAdapter(pickItemHistoryAdapter);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, PickDetailActivity.class));
            }
        });

    }
    //得到列表项个数
    @Override
    public int getItemCount() {
        return mPickInfoList == null ? 0 : mPickInfoList.size();
    }
    //listview滚动的时候快速设置值，而不必每次都重新创建很多对象，从而提升性能。
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_id)
        TextView mId;
        @Bind(R.id.tv_time)
        TextView mTime;
        @Bind(R.id.rcl_pick_history_item_list)
        RecyclerView mPickHistoryItemList;
        @Bind(R.id.tv_price)
        TextView mPrice;
        @Bind(R.id.tv_discount)
        TextView mDiscount;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
