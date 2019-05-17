package com.crphdm.dl2.utils;

/**
 * Created by sunbaochun on 15/9/26.
 */
public class Constant {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int PAPER_BOOK = 1;
    public static final int E_BOOK = 2;
    public static final int FLAG_PICK = 1;
    public static final int FLAG_PICK_HISTORY = 2;

    public static final int BOOKSHELF_TYPE_ALL = 0;
    public static final int BOOKSHELF_TYPE_BUY = 1;
    public static final int BOOKSHELF_TYPE_BORROWED = 2;
    public static final int BOOKSHELF_TYPE_RESOURCES = 3;

    public static final int BOOK_TYPE_REAL_BOOK = 1;
    public static final int BOOK_TYPE_E_BOOK = 2;
    public static final int BOOK_TYPE_POD = 3;

    public static final int BOOK_SOURCE_BUY = 1;
    public static final int BOOK_SOURCE_BORROWING = 2;

    public static final String GROUP_ALL = "全部";
    public static final String GROUP_BUY = "购买";
    public static final String GROUP_BORROWING = "借阅";
    public static final String GROUP_SOURCE = "资源";

    public static final String LIBRARY_ID = "LIBRARY_ID";
    public static final String LIBRARY_NAME = "LIBRARY_NAME";
    public static final String LIBRARY_LOGO = "LIBRARY_LOGO";
    public static final String LIBRARY_DESCRIPTION = "LIBRARY_DESCRIPTION";

    public static final int LIBRARY_BOOK_TYPE_BOOK = 1;
    public static final int LIBRARY_BOOK_TYPE_PRIVATE = 2;
    public static final int LIBRARY_BOOK_TYPE_PUBLIC = 3;

    public static final String BOOK_ID = "BOOK_ID";

    public static final String RESOURCE_TYPE = "RESOURCE_TYPE";

    public static final String INSTITUTION_ID = "INSTITUTION_ID";

    public static final String LIBRARY_DETAIL_TYPE = "LIBRARY_DETAIL_TYPE";
    public static final int LIBRARY_DETAIL_E_BOOK = 1;
    public static final int LIBRARY_DETAIL_RESOURCE = 2;

    public static final String CLOUD_BOOKSTORE_CARD_ID = "CLOUD_BOOKSTORE_CARD_ID";
    public static final int CLOUD_BOOKSTORE_BOOK = 1;
    public static final int CLOUD_BOOKSTORE_POD = 2;

    public static final String CLOUD_BOOKSTORE_TYPE_ID = "CLOUD_BOOKSTORE_TYPE_ID";
    public static final int CLOUD_BOOKSTORE_FIGURE = 1;
    public static final int CLOUD_BOOKSTORE_PROFESSIONAL = 2;

    public static final String CLOUD_BOOKSTORE_PARENT_ID = "CLOUD_BOOKSTORE_PARENT_ID";
    public static final String CLOUD_BOOKSTORE_PARENT_NAME = "CLOUD_BOOKSTORE_PARENT_NAME";
    public static final String SHOPPING_CART_ORDER_TYPE = "SHOPPING_CART_ORDER_TYPE";
    public static final String PAY_MANAGEMENT_ORDER_ID = "PAY_MANAGEMENT_ORDER_ID";

    public static final String SELECT_BOOKSTORE_TYPE_ID = "SELECT_BOOKSTORE_TYPE_ID";
    public static final int NOT_SELECT_BOOKSTORE = 1;
    public static final int ALREADY_SELECT_BOOKSTORE = 2;

    public static final String ADDRESS_ID = "ADDRESS_ID";

    public static final int SCANNING_QR_CODE = 1;

    public static final int NET_STATE_UNKNOWN = 0x00;
    public static final int NET_STATE_FIRST_LEVEL = 0x01;
    public static final int NET_STATE_SECOND_LEVEL = 0x02;
    public static final int NET_STATE_ALL = 0x03;

    public static final int USER_TYPE_PHONE = 0x1;
    public static final int USER_TYPE_EMAIL = 0x2;
    public static final int USER_TYPE_JOB_NUMBER = 0x3;

    public static final int NET_CENTER_FIRST = 0x01;
    public static final int NET_CENTER_SECOND = 0x02;

    public static final int ORG_BIND_STATE_NO_BIND = 1;
    public static final int ORG_BIND_STATE_BINDING = 2;
    public static final int ORG_BIND_STATE_BIND_SUCCESS = 3;
    public static final int ORG_BIND_STATE_BIND_FAIL = 4;

    public static final int USER_ROLE_PUTONG = 6;
    public static final int USER_ROLE_PROVISIONALITY = 7;//临时用户
    public static final int USER_ROLE_JIGOU = 8;
    public static final int USER_ROLE_CAIXUANYUAN = 9;
    public static final int USER_ROLE_LINGDAO = 10;

    public static final String PICK_ACTIVITY_BY_WHICH = "PICK_ACTIVITY_BY_WHICH";
    public static final int FROM_CLOUD_BOOK_MARKET = 1;
    public static final int FROM_USER_CENTER = 2;

    public static final String PICK_MINING_MENU_ID = "PICK_MINING_MENU_ID";

    public static final int MINING_MENU_STATE_SHEN_HE_ZHONG= 1;
    public static final int MINING_MENU_STATE_TONG_GUO = 2;
    public static final int MINING_MENU_STATE_WEI_TONG_GUO= 3;

    public static final String CLOUD_MARKET_TAG = "CLOUD_MARKET_TAG";
    public static final int CLOUD_MARKET_TAG_YES = 1;
    public static final int CLOUD_MARKET_TAG_NO = 2;
    public static final String NOT_SHOW_WELCOME = "NOT_SHOW_WELCOME";

    public static final int APPROVAL_STATUS_YES = 1;
    public static final int APPROVAL_STATUS_NO = 2;

    public static final String ACTION_REFRESH_DATA = "com.crphdm.dl2.action.finishActivity";

    public static final int PAGE_SIZE = 20;


}
