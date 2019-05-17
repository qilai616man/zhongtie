package com.crphdm.dl2.adapter.library;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crphdm.dl2.R;
import com.crphdm.dl2.utils.Constant;
import com.digital.dl2.business.core.obj.PgBookCategory;
import com.digital.dl2.business.core.obj.PgMiningMenuListEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunbaochun on 15/10/9.
 */
public class MyPickBookListAdapter extends RecyclerView.Adapter<MyPickBookListAdapter.ViewHolder> {
    //我的采选里面图书列表adapter
    private Context mContext;
    private List<PgBookCategory> mList;
    private List<String> mNameList;
    private int mFlag;

    public MyPickBookListAdapter(Context context, PgMiningMenuListEntity data, int flag) {
        mContext = context;
        mList = data.getBookCategories();
        mNameList = data.getBookNameList();
        mFlag = flag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_pick_book_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mFlag == Constant.FLAG_PICK) {
            holder.mBooKName.setText((position + 1) + "." + mList.get(position).getCategoryName());
            holder.mBookCount.setVisibility(View.VISIBLE);
            holder.mBookCount.setText(mList.get(position).getCategoryNumber() + " 本");
        } else if (mFlag == Constant.FLAG_PICK_HISTORY) {
            holder.mBooKName.setText((position + 1) + "." + mNameList.get(position));
            holder.mBookCount.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (mFlag == Constant.FLAG_PICK) {
            return mList == null ? 0 : mList.size();
        } else if (mFlag == Constant.FLAG_PICK_HISTORY) {
            return mNameList == null ? 0 : mNameList.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_booK_name)
        TextView mBooKName;
        @Bind(R.id.tv_book_count)
        TextView mBookCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
