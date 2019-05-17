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
 * Created by sunbaochun on 15/10/10.
 */
public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ViewHolder> {
    //修改地址可选地址adapter
    private Context mContext;
    private List<PgAddress> mAddressList;

    private OnAddressChangeListener mListener;

    public AddressListAdapter(Context context) {
        mContext = context;
    }

    public void setListener(OnAddressChangeListener listener) {
        Ln.d("MiningMenuAdapter:setListener");
        mListener = listener;
    }

    public void setData( List<PgAddress> list){
        mAddressList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_address_list_layout, parent, false);
        return new ViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return mAddressList == null ? 0 : mAddressList.size();
    }

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
