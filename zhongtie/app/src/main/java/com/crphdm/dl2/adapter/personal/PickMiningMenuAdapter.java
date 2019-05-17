package com.crphdm.dl2.adapter.personal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crphdm.dl2.R;
import com.crphdm.dl2.listener.OnChangeCountListener;
import com.digital.dl2.business.core.obj.PgMiningMenuListEntity;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunbaochun on 15/10/7.
 */
public class PickMiningMenuAdapter extends RecyclerView.Adapter<PickMiningMenuAdapter.ViewHolder> {
    private View mParentView;
    private Context mContext;
    private List<PgMiningMenuListEntity> mList;
    private int mCount = 1;
    private int mType;
    private OnChangeCountListener mListener;

    public PickMiningMenuAdapter() {

    }

    public void setData(Context context, int type, List<PgMiningMenuListEntity> list) {
        mContext = context;
        mType = type;
        mList = list;
    }

    public void setListener(OnChangeCountListener listener) {
        Ln.d("ShoppingCartAdapter:setListener");
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mParentView = LayoutInflater.from(mContext).inflate(R.layout.item_shopping_cart, parent, false);
        return new ViewHolder(mParentView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PgMiningMenuListEntity item = mList.get(position);
//        holder.title.setText(item.getName());
//        holder.author.setText(item.getAuthor());
//        holder.price.setText("￥" + item.getBookType().getPrice());
//        holder.oldPrice.setText("￥" + item.getBookType().getPressPrice() + "");
//        holder.oldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//        holder.tvSelectCount.setText(item.getNumber() + "");
//
//        Glide.with(mContext)
//                .load(item.getFrontCover())
//                .placeholder(R.drawable.drw_book_default)
//                .crossFade()
//                .into(holder.bookCover);

//        if (mType == Constant.E_BOOK) {
//            holder.tvAdd.setEnabled(false);
//            holder.tvReduce.setEnabled(false);
//        }
//
//        holder.llDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCount = Integer.parseInt(holder.tvSelectCount.getText().toString());
//                mListener.onDelete(item.getEntityId(), mType, mCount);
//            }
//        });
//
//        holder.tvAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCount = Integer.parseInt(holder.tvSelectCount.getText().toString());
//                Ln.d("ShoppingCartAdapter:mCount:" + mCount);
//                if (mCount <= 999) {
//                    mCount++;
//                    holder.tvSelectCount.setText(String.valueOf(mCount));
//                    holder.tvAdd.setEnabled(true);
//                    holder.tvReduce.setEnabled(true);
//                    mListener.onAdd(item.getEntityId(), mType);
//                } else if (Integer.parseInt(holder.tvSelectCount.getText().toString()) >= 1000) {
//                    holder.tvAdd.setEnabled(false);
//                    holder.tvReduce.setEnabled(true);
//                }
//            }
//        });
//
//        holder.tvReduce.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCount = Integer.parseInt(holder.tvSelectCount.getText().toString());
//                if (mCount > 1) {
//                    mCount--;
//                    holder.tvSelectCount.setText(String.valueOf(mCount));
//                    holder.tvReduce.setEnabled(true);
//                    holder.tvAdd.setEnabled(true);
//                    mListener.onReduce(item.getEntityId(), mType);
//                } else if (Integer.parseInt(holder.tvSelectCount.getText().toString()) <= 1) {
//                    holder.tvReduce.setEnabled(false);
//                    holder.tvAdd.setEnabled(true);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.bookCover)
        ImageView bookCover;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.type)
        TextView type;
        @Bind(R.id.ll_delete)
        LinearLayout llDelete;
        @Bind(R.id.rl_title)
        RelativeLayout rlTitle;
        @Bind(R.id.author)
        TextView author;
        @Bind(R.id.price)
        TextView price;
        @Bind(R.id.old_price)
        TextView oldPrice;
        @Bind(R.id.tv_reduce)
        TextView tvReduce;
        @Bind(R.id.tv_select_count)
        TextView tvSelectCount;
        @Bind(R.id.tv_add)
        TextView tvAdd;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
