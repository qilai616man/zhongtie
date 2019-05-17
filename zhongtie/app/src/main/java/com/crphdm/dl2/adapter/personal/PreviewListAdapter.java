package com.crphdm.dl2.adapter.personal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.library.ImageViewDisplayNewActivity;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunbaochun on 15/10/12.
 */
public class PreviewListAdapter extends RecyclerView.Adapter<PreviewListAdapter.ViewHolder> {
    public static final String INTENT_IMAGE_PATH = "INTENT_IMAGE_PATH";
    private Context mContext;
    private List<String> mImageList;
    private List<String> mImageUrlList;

    public void setData(List<String> imageList){
        mImageList = imageList;
    }

    public PreviewListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_preview_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String url = mImageList.get(position);
        Ln.d("PreviewListAdapter:url:" + url);

        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.drw_book_default)
                .crossFade()
                .into(holder.bookCover);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 15/10/12
                Intent intent = new Intent(mContext, ImageViewDisplayNewActivity.class);
                intent.putExtra("position",position);
                intent.putStringArrayListExtra("ImageList", (ArrayList<String>) mImageList);
//                intent.putExtra("INTENT_IMAGE_PATH",mImageList.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageList == null?0:mImageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.bookCover)
        ImageView bookCover;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
