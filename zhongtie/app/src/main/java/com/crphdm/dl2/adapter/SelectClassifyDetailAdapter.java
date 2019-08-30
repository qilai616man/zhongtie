package com.crphdm.dl2.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.digital.dl2.business.core.manager.CloudBookstoreManager;
import com.digital.dl2.business.core.obj.PgBookForLibraryListEntity;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * //采选页分类详情recyclerView页面
 */
public class SelectClassifyDetailAdapter extends RecyclerView.Adapter<SelectClassifyDetailAdapter.ViewHolder> {
    private View mView;
    private Context mContext;
    private int mSelectType;
    private List<PgBookForLibraryListEntity> mBookList;
    private UserInfo mUserInfo;
    private OnClassDetailChangeListener mListener;
    private boolean isRecommend = false;
    private String mToken;
    private CloudBookstoreManager mCloudBookStoreManager = CloudBookstoreManager.getInstance();
    private ProgressDialog mProgressDialog;
    public SelectClassifyDetailAdapter(Context context, int selectType) {
        mContext = context;
        mSelectType = selectType;
    }
    //设置数据
    public void setData(List<PgBookForLibraryListEntity> bookList){
        mBookList = bookList;
    }
    //设置监听
    public void setListener(OnClassDetailChangeListener listener) {
        Ln.d("SelectClassifyDetailAdapter:setListener");
        mListener = listener;
    }
    //负责承载每个子项的布局。
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.item_select_classify_detail, parent, false);
        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);
        return new ViewHolder(mView);
    }
    //负责将每个子项holder绑定数据。
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(mBookList != null && !mBookList.isEmpty()){
            final PgBookForLibraryListEntity book = mBookList.get(position);

            holder.title.setText(book.getName());
            holder.author.setText(book.getAuthor());

            if (UserModule.getInstance().getRole() == Constant.USER_ROLE_CAIXUANYUAN) {//采选员,领导
                holder.mSelectLL.setVisibility(View.VISIBLE);
                holder.mRecommendLL.setVisibility(View.GONE);
            }else {
                holder.mSelectLL.setVisibility(View.GONE);
                if(mSelectType == Constant.ALREADY_SELECT_BOOKSTORE){
                    holder.mRecommendLL.setVisibility(View.GONE);
                }else{
                    holder.mRecommendLL.setVisibility(View.VISIBLE);
                }
            }

            if (mSelectType == Constant.ALREADY_SELECT_BOOKSTORE) {//已采选
                holder.type.setText("已采选");
                holder.type.setTextColor(mContext.getResources().getColor(R.color.color_blue_bg));
                holder.type.setBackgroundResource(R.drawable.frame_blue);
            } else{
                holder.type.setText("未采选");
                holder.type.setTextColor(mContext.getResources().getColor(R.color.color_text_54c25b));
                holder.type.setBackgroundResource(R.drawable.frame_green);
            }

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
                    if (book.isAddMiningList()) {
                        Ln.d("SelectClassifyDetailAdapter:cbSelect:取消选中");
                        mListener.onCancel(position, book.getEntityId(), book.getType());
                    } else {
                        Ln.d("SelectClassifyDetailAdapter:cbSelect:选中");
                        mListener.onSelect(position, book.getEntityId(), book.getType());
                    }
                }
            });
            holder.mRecommendLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(book.isAddRecommend()){
                        book.setIsAddRecommend(false);
                    }else{
                        book.setIsAddRecommend(true);
                        mListener.onRecommend(book.getEntityId(),position);

                    }
                }
            });
            if (book.isAddRecommend()){
                holder.cbRecommend.setImageDrawable(mContext.getResources().getDrawable(R.drawable.draw_cloud_book_market_select));
                holder.tvRecommend.setText("已推荐");
                holder.mRecommendLL.setEnabled(false);
            }else{
                holder.cbRecommend.setImageDrawable(mContext.getResources().getDrawable(R.drawable.draw_cloud_book_market_unselect));
                holder.tvRecommend.setText("推荐采选");
                holder.mRecommendLL.setEnabled(true);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //进入图书详情页
                    Intent intent = new Intent(mContext, CloudBookstoreBookDetailActivity.class);
                    intent.putExtra(Constant.BOOK_ID, book.getEntityId());
                    intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, mSelectType);
                    mContext.startActivity(intent);
                }
            });
        }
    }
    //得到列表项个数
    @Override
    public int getItemCount() {
        return mBookList == null ? 0 : mBookList.size();
    }
    //listview滚动的时候快速设置值，而不必每次都重新创建很多对象，从而提升性能。
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
        @Bind(R.id.ll_select)
        LinearLayout mSelectLL;
        @Bind(R.id.cb_classify_detail_select)
        ImageView cbSelect;

        @Bind(R.id.ll_recommend)
        LinearLayout mRecommendLL;
        @Bind(R.id.cb_recommend)
        ImageView cbRecommend;
        @Bind(R.id.tv_recommend)
        TextView tvRecommend;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
