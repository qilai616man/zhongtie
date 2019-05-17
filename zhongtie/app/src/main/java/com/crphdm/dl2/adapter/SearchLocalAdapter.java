package com.crphdm.dl2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brainsoon.utils.BookUtils;
import com.crphdm.dl2.R;
import com.crphdm.dl2.entity.SearchLocalKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/21.
 */

public class SearchLocalAdapter extends RecyclerView.Adapter<SearchLocalAdapter.MyViewHolder> {
    private  List<SearchLocalKey> data=new ArrayList<>();
    private Context context;
    public SearchLocalAdapter(Context context){
        this.context=context;
    }

    public void setData(List<SearchLocalKey> data){
        this.data=data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=View.inflate(context, R.layout.search_local_item,null);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final SearchLocalKey searchLocalKey=data.get(position);
        holder.search_text_name.setText(searchLocalKey.getName());
        holder.search_text_count.setText("共"+searchLocalKey.getCount()+"个");
        holder.search_text_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[]  asskey=searchLocalKey.getAssKey();
                String localUrl=searchLocalKey.getLocalUrl();
                String fileUrl=searchLocalKey.getFileUrl();

                String succeed = BookUtils.openTdpBook(
                        fileUrl,
                        localUrl,
                        asskey,
                        context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView search_text_name;
        TextView search_text_count;

        public MyViewHolder(View itemView) {
            super(itemView);
            search_text_name= (TextView) itemView.findViewById(R.id.search_local_bookName);
            search_text_count= (TextView) itemView.findViewById(R.id.search_local_count);
        }
    }
}
