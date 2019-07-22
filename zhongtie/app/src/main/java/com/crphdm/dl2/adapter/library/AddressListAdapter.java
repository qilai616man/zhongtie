package com.crphdm.dl2.adapter.library;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crphdm.dl2.R;
import com.crphdm.dl2.listener.OnAddressChangeListener;
import com.digital.dl2.business.core.obj.PgAddress;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Modify by songzixuan on 19/07/04.
 * 修改地址可选地址adapter
 */
public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ViewHolder> {
    //修改地址可选地址adapter
    private Context mContext;
    private List<PgAddress> mAddressList;

    private OnAddressChangeListener mListener;

    public AddressListAdapter(Context context) {
        mContext = context;
    }
    //设置监听
    public void setListener(OnAddressChangeListener listener) {
        Ln.d("MiningMenuAdapter:setListener");
        mListener = listener;
    }
    //设置数据
    public void setData( List<PgAddress> list){
        mAddressList = list;
    }
    //承载每个子项的布局
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_address_list_layout, parent, false);
        return new ViewHolder(view);
    }
    //将每个子项holder绑定数据
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final PgAddress address = mAddressList.get(position);

        holder.mName.setText(address.getName());
        holder.mPhone.setText(address.getPhone());
        holder.mAddress.setText(address.getAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSelect(address);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onDelete(address.getAddressId());
                return false;
            }
        });
    }
    //返回数组
    @Override
    public int getItemCount() {
        return mAddressList == null ? 0 : mAddressList.size();
    }
    //listview滚动的时候快速设置值，而不必每次都重新创建很多对象，从而提升性能。
    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_name)
        TextView mName;
        @Bind(R.id.tv_phone)
        TextView mPhone;
        @Bind(R.id.tv_address)
        TextView mAddress;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


}
