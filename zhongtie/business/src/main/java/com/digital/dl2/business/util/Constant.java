package com.digital.dl2.business.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by samsung on 14-11-10.
 */
public class Constant {
    public static final String FILE_DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsoluteFile() +
            File.separator +
            "crphdm" +
            File.separator +
            "download" +
            File.separator;

    public static final String FILE_READ_PATH = Environment.getExternalStorageDirectory().getAbsoluteFile() +
            File.separator +
            "crphdm" +
            File.separator +
            "read" +
            File.separator;

    public static final String CODE = "utf-8";

    public static final String DATABASE_NAME = "digital_library.db";
    public static final String TABLE_DIGITAL_LIBRARY = "digital_library";
    public static final String TABLE_GROUP = "TABLE_GROUP";
    public static final String TABLE_TYPE = "TABLE_TYPE";
    //成都不过期用户
    public static final String TABLE_USERJSON = "TABLE_USERJSON";
    //用户可借阅图书时间限制
    public static final String TABLE_EXCEEDTIME = "TABLE_EXCEEDTIME";

    public static final String PROVISIONALITY_DATABASE_NAME = "privisionality_digital_library.db";
    public static final String PROVISIONALITY_TABLE_DIGITAL_LIBRARY = "privisionality_digital_library";

    public static final int DB_STATUS_INSERT = 1;
    public static final int DB_STATUS_UPDATE = 2;
    public static final int DB_STATUS_DELETE = 3;
    public static final int DB_STATUS_NORMAL = 4;

    public static final int BOOKSHELF_TYPE_ALL = 0;
    public static final int BOOKSHELF_TYPE_BUY = 1;
    public static final int BOOKSHELF_TYPE_BORROWED = 2;
    public static final int BOOKSHELF_TYPE_RESOURCES = 3;

    public static final int BOOK_SOURCE_BUY = 1;
    public static final int BOOK_SOURCE_BORROWED = 2;

    public static final int NETWORK_STATUS_ONE_CENTRE = 0;
    public static final int NETWORK_STATUS_TWO_CENTRE = 1;
    public static final int NETWORK_STATUS_ONE_AND_TWO_CENTRE = 2;
    public static final int NETWORK_STATUS_NO_NETWORK = 3;

    public static final int BOOK_UNZIP_STATE_SUCCESS = 0;
    public static final int BOOK_UNZIP_STATE_FAIL = 1;
    public static final int BOOK_UNZIP_STATE_ING = 2;

    public static final int TOKEN_ERROR = 401;
    public static final int TOKEN_EXPIRED = 402;
    public static final String TOKEN_ERROR_MESSAGE = "您的账户已在其他设备登录";

    public static final String ACTION_TOKEN_ERROR = "com.digital.dl2.action.token_error";
    public static final String ACTION_TOKEN_EXPIRED = "com.digital.dl2.action.token_expired";
    public static final String ACTION_CLEAN_DATA = "com.digital.dl2.action.clean_data";

    public static final String INTENT_ERROR_MESSAGE = "INTENT_ERROR_MESSAGE";

}
