package com.crphdm.dl2.fragments.video;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;

import java.util.List;

import static com.github.why168.utils.L.TAG;


/**
 * Created by Administrator on 2019/8/28.
 */

public class VideoRecyclerCoverFlowAdapter extends RecyclerView.Adapter<VideoRecyclerCoverFlowAdapter.ViewHolder>{
    List<GTVideoListBean.DataBean> list;
    Context context;
    VideoListFragment videoFragment;

    public VideoRecyclerCoverFlowAdapter(VideoListFragment videoFragment, List<GTVideoListBean.DataBean> list, Context context) {
        this.list = list;
        this.context = context;
        this.videoFragment = videoFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_cover_flow_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Log.e(TAG, "onActivityCreated: "+list.size());
        Glide.with(context).load(list.get(position).getThumbnail()).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoFragment.shipin(list.get(position).getUrl(),list.get(position).getTitle());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null){
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.cover_flow_image_id);
        }
    }
}
