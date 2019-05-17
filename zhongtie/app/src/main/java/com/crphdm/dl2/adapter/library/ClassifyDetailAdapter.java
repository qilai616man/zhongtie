package com.crphdm.dl2.adapter.library;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.cloud_bookstore.CloudBookstoreBookDetailActivity;
import com.crphdm.dl2.listener.OnClassDetailChangeListener;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.utils.Constant;
import com.digital.dl2.business.core.obj.PgBookForLibraryListEntity;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunbaochun on 15/10/6.
 */
public class ClassifyDetailAdapter extends RecyclerView.Adapter<ClassifyDetailAdapter.ViewHolder> {
    //分类详情recyclerView页面
    private View mView;
    private Context mContext;
    private int mCardId;
    private List<PgBookForLibraryListEntity> mBookList;

    private OnClassDetailChangeListener mListener;

    public ClassifyDetailAdapter(Context context,int cardId) {
        mContext = context;
        mCardId = cardId;
    }

    public void setData(List<PgBookForLibraryListEntity> bookList){
        mBookList = bookList;
    }

    public void setListener(OnClassDetailChangeListener listener) {
        Ln.d("ShoppingCartAdapter:setListener");
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.item_classify_detail, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(mBookList != null && !mBookList.isEmpty()){
            final PgBookForLibraryListEntity book = mBookList.get(position);

            holder.title.setText(book.getName());
            holder.author.setText(book.getAuthor());
            holder.desc.setText(book.getIntroduction());

            holder.price.setText("￥" + ( (book.getPrice() * 100)) / 100.00f);
            holder.oldPrice.setText("￥" + ( (book.getPressPrice() * 100)) / 100.00f);
            holder.oldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            holder.mSelectLL.setVisibility(View.INVISIBLE);

            if (UserModule.getInstance().getRole() == Constant.USER_ROLE_CAIXUANYUAN) {//采选员
                holder.mSelectLL.setVisibility(View.VISIBLE);
            }

            if (mCardId == Constant.CLOUD_BOOKSTORE_POD) {//电子书
                holder.type.setText("实体书");
                holder.type.setTextColor(mContext.getResources().getColor(R.color.color_blue_bg));
                holder.type.setBackgroundResource(R.drawable.frame_blue);
            } else{
                holder.type.setText("电子书");
                holder.type.setTextColor(mContext.getResources().getColor(R.color.color_text_54c25b));
                holder.type.setBackgroundResource(R.drawable.frame_green);
            }

//            if (mBookList.get(i).getType() == Constant.PAPER_BOOK){
//                holder.type.setText("实体书");
//                holder.type.setTextColor(mContext.getResources().getColor(R.color.color_blue_bg));
//                holder.type.setBackgroundResource(R.drawable.frame_blue);
//            }else if (mBookList.get(i).getType() == Constant.E_BOOK){
//                holder.type.setText("电子书");
//                holder.type.setTextColor(mContext.getResources().getColor(R.color.color_text_54c25b));
//                holder.type.setBackgroundResource(R.drawable.frame_green);
//            }else {//type = 3
//                holder.type.setText("电子书");
//                holder.type.setTextColor(mContext.getResources().getColor(R.color.color_text_54c25b));
//                holder.type.setBackgroundResource(R.drawable.frame_green);
//            }

            Glide.with(mContext)
                    .load(mBookList.get(position).getFrontCover())
                    .placeholder(R.drawable.drw_book_default)
                    .crossFade()
                    .into(holder.bookCover);

            if(book.isAddMiningList()){
                holder.cbSelect.setImageDrawable(mContext.getResources().getDrawable(R.drawable.draw_cloud_book_market_select));
            }else {
                holder.cbSelect.setImageDrawable(mContext.getResources().getDrawable(R.drawable.draw_cloud_book_market_unselect));
            }

            holder.mSelectLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(book.isAddMiningList()){
                        Ln.d("CloudMarketFragment:cbSelect:取消选中");
                        mListener.onCancel(position,book.getEntityId(),book.getType());
                    }else {
                        Ln.d("CloudMarketFragment:cbSelect:选中");
                        mListener.onSelect(position,book.getEntityId(), book.getType());
                    }
                }
            });

//            holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked) {
//                        Ln.d("CloudMarketFragment:cbSelect:选中");
//                        mListener.onSelect(book.getEntityId(), book.getType());
//                    } else {
//                        Ln.d("CloudMarketFragment:cbSelect:取消选中");
//                        mListener.onCancel(book.getEntityId(),book.getType());
//                    }
//                }
//            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //进入图书详情页
                    Intent intent = new Intent(mContext,CloudBookstoreBookDetailActivity.class);
                    intent.putExtra(Constant.BOOK_ID,book.getEntityId());
                    intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID,mCardId);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBookList == null ? 0 : mBookList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.bookCover)
        ImageView bookCover;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.type)
        TextView type;
        @Bind(R.id.rl_title)
        RelativeLayout rlTitle;
        @Bind(R.id.author)
        TextView author;
        @Bind(R.id.desc)
        TextView desc;
        @Bind(R.id.tv_classify_detail_price)
        TextView price;
        @Bind(R.id.tv_classify_detail_old_price)
        TextView oldPrice;
        @Bind(R.id.ll_select)
        LinearLayout mSelectLL;
        @Bind(R.id.cb_classify_detail_select)
        ImageView cbSelect;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
