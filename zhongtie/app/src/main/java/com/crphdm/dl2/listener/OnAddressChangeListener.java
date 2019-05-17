package com.crphdm.dl2.listener;

import com.digital.dl2.business.core.obj.PgAddress;

/**
 * Created by sunbaochun on 15/10/8.
 */
public interface OnAddressChangeListener {
    void onSelect(PgAddress address);
    void onDelete(int id);
}
