package com.crphdm.dl2.adapter.personal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crphdm.dl2.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunbaochun on 15/10/10.
 */
public class PickItemHistoryAdapter extends RecyclerView.Adapter<PickItemHistoryAdapter.ViewHolder> {


    private Context mContext;
    private List<String> mNameList;

    public PickItemHistoryAdapter(Context context, List<String> list) {
        mContext = context;
        mNameList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pick_history_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mIndex.setText((position+1)+".");
        holder.mName.setText(mNameList.get(position));
    }

    @Override
    public int getItemCount() {
        return mNameList == null ? 0 : mNameList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_name)
        TextView mName;
        @Bind(R.id.tv_index)
        TextView mIndex;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
