package com.crphdm.dl2.listener;

import com.digital.dl2.business.core.obj.PgAddress;

/**
 * Modify by songzixuan on 19/07/04.
 * 地址改变监听器
 */
public interface OnAddressChangeListener {
    void onSelect(PgAddress address);
    void onDelete(int id);
}
