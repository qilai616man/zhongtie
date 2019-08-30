package com.crphdm.dl2.fragments.video;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;

import java.util.List;


/**
 * Created by szx1 on 2019/8/8.
 * 视频列表展示适配器
 */

public class VideoAdapter extends BaseAdapter{
    List<GTVideoListBean.DataBean> list;
    Context context;
    VideoListFragment fragment;

    /*@Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_video_item, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(context).load(list.get(position).getThumbnail()).into(holder.imageView);
        holder.time.setText(list.get(position).getDuration().substring(3,8));
        holder.name.setText(list.get(position).getTitle().replace("_batch.mp4",""));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.shipin(list.get(position).getUrl(),list.get(position).getTitle());
            }
        });
    }*/



    /*@Override
    public int getItemCount() {
        if (list != null){
            return list.size();
        }
        return 0;
    }*/


    public VideoAdapter(VideoListFragment videoListFragment, List<GTVideoListBean.DataBean> list, Context context) {
        this.list = list;
        this.context = context;
        this.fragment = videoListFragment;
    }

    @Override
    public int getCount() {
        if (list != null){
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        TextView name,time;
        convertView = LayoutInflater.from(context).inflate(R.layout.adapter_video_item,null);
        imageView = (ImageView) convertView.findViewById(R.id.video_image_item_id);
        name = (TextView) convertView.findViewById(R.id.video_name_item_id);
        time = (TextView) convertView.findViewById(R.id.video_time_id);
        Glide.with(context).load(list.get(position).getThumbnail()).into(imageView);
        time.setText(list.get(position).getDuration().substring(3,8));
        name.setText(list.get(position).getTitle().replace("_batch.mp4",""));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.shipin(list.get(position).getUrl(),list.get(position).getTitle());
            }
        });
        return convertView;
    }

    /*public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name,time;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.video_image_item_id);
            name = (TextView) itemView.findViewById(R.id.video_name_item_id);
            time = (TextView) itemView.findViewById(R.id.video_time_id);
        }
    }*/
}