package com.crphdm.dl2.adapter.personal;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.digital.dl2.business.core.obj.PgMiningMenuDetailItem;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 采选详情adapter
 */
public class PickDetailAdapter extends RecyclerView.Adapter<PickDetailAdapter.ViewHolder> {
    private Context mContext;
    private List<PgMiningMenuDetailItem> mList;

    public PickDetailAdapter(Context context) {
        mContext = context;
    }
    //设置数据
    public void setData(List<PgMiningMenuDetailItem> list) {
        mList = list;
    }
    //负责承载每个子项的布局。
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pick_detail, parent, false);
        return new ViewHolder(view);
    }
    //负责将每个子项holder绑定数据。
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Ln.d("PickDetailAdapter:position:" + position);
        if (mList != null) {

            PgMiningMenuDetailItem item = mList.get(position);
            Ln.d("PickDetailAdapter:item:" + item);
            holder.title.setText(item.getTitle());
            holder.author.setText(item.getAuthor());
            holder.price.setText("￥" + item.getPrice());
            holder.oldPrice.setText("￥" + item.getOldPrice());
            holder.oldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.number.setText(String.valueOf(item.getNumber())+"本");
            Glide.with(mContext)
                    .load(item.getThumb())
                    .placeholder(R.drawable.drw_book_default)
                    .crossFade()
                    .into(holder.bookCover);
        }
    }
    //得到列表项个数
    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }
    //listview滚动的时候快速设置值，而不必每次都重新创建很多对象，从而提升性能。
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.bookCover)
        ImageView bookCover;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.rl_title)
        RelativeLayout Title;
        @Bind(R.id.author)
        TextView author;
        @Bind(R.id.price)
        TextView price;
        @Bind(R.id.old_price)
        TextView oldPrice;
        @Bind(R.id.goods_number)
        TextView number;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
