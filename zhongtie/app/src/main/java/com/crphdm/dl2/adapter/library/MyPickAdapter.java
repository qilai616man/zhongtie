package com.crphdm.dl2.adapter.library;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.personal.PickDetailActivity;
import com.crphdm.dl2.utils.Constant;
import com.digital.dl2.business.core.obj.PgMiningMenuListEntity;
import com.goyourfly.gdownloader.utils.Ln;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Modify by songzixuan on 19/07/04.
 * 我的采选adapter
 */
public class MyPickAdapter extends RecyclerView.Adapter<MyPickAdapter.ViewHolder> {
    //我的采选adapter
    private Context mContext;
    private List<PgMiningMenuListEntity> mListData;
    private int mFlag;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private final int ZHENG_ZAI_SHEN_HE = 1;
    private final int CAI_XUAN_WAN_CHENG = 2;
    private final int BU_TONG_GUO = 3;
    private final int WEI_SHEN_HE = 4;

    public MyPickAdapter(Context context, int flag) {
        mContext = context;
        mFlag = flag;
    }

    public void setData(List<PgMiningMenuListEntity> infoList) {
        mListData = infoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_pick_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PgMiningMenuListEntity item = mListData.get(position);
        Ln.d("MyPickAdapter:book:" + item.toString());

        holder.mId.setText(item.getEntityId() + "");
        holder.mTime.setText(format.format(item.getMiningDate() * 1000));
        holder.mPrice.setText("￥" + item.getAllPrice());
        holder.mDiscount.setText("￥" + item.getPreferentialPrice());

        if (item.getState() == 0) {//待审核
            holder.mState.setText("待审核");
        } else if (item.getState() == 1) {//已审核
            holder.mState.setText("已审核");
        } else if (item.getState() == 2) {//打回
            holder.mState.setText("打回");
        } else if (item.getState() == 3) {//未提交
            holder.mState.setText("未提交");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PickDetailActivity.class);
//                Intent intent =new Intent(mContext, PickActivity.class);
                intent.putExtra(Constant.PICK_MINING_MENU_ID, item.getEntityId());
                mContext.startActivity(intent);
            }
        });

//        if (mFlag == Constant.FLAG_PICK) {
//            holder.mStateView.setVisibility(View.VISIBLE);
//            switch (mPickInfoList.get(position).getState()) {
//                case ZHENG_ZAI_SHEN_HE:
//                    holder.mState.setText("正在审核");
//                    break;
//                case CAI_XUAN_WAN_CHENG:
//                    holder.mState.setText("采选完成");
//                    break;
//                case BU_TONG_GUO:
//                    holder.mState.setText("审核不通过");
//                    holder.mState.setTextColor(Color.RED);
//                    break;
//                case WEI_SHEN_HE:
//                    holder.mState.setText("未审核");
//                    break;
//                default:
//                    break;
//            }
//
//            MyLinearLayoutManager myLinearLayoutManager = new MyLinearLayoutManager(mContext);
//            holder.mPickDetailBookList.setLayoutManager(myLinearLayoutManager);
//            MyPickBookListAdapter myPickBookListAdapter
//                    = new MyPickBookListAdapter(mContext, mPickInfoList.get(position), mFlag);
//            holder.mPickDetailBookList.setAdapter(myPickBookListAdapter);
//
//        } else if (mFlag == Constant.FLAG_PICK_HISTORY) {
//            holder.mStateView.setVisibility(View.INVISIBLE);
//            MyLinearLayoutManager myLinearLayoutManager = new MyLinearLayoutManager(mContext);
//            holder.mPickDetailBookList.setLayoutManager(myLinearLayoutManager);
//            MyPickBookListAdapter myPickBookListAdapter1
//                    = new MyPickBookListAdapter(mContext, mPickInfoList.get(position), mFlag);
//            holder.mPickDetailBookList.setAdapter(myPickBookListAdapter1);
//        }

    }

    @Override
    public int getItemCount() {
        return mListData == null ? 0 : mListData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_id)
        TextView mId;
        @Bind(R.id.tv_time)
        TextView mTime;
        @Bind(R.id.tv_state)
        TextView mState;
        @Bind(R.id.tv_price)
        TextView mPrice;
        @Bind(R.id.tv_discount)
        TextView mDiscount;
        @Bind(R.id.rcl_pick_detail_book_list)
        RecyclerView mPickDetailBookList;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
