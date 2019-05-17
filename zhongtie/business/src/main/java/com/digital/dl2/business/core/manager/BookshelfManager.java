package com.digital.dl2.business.core.manager;

import android.content.Context;

import com.digital.dl2.business.core.module.BookshelfModule;
import com.digital.dl2.business.core.obj.PgBookForBookstoreDetail;
import com.digital.dl2.business.core.obj.PgBookForLibraryDetail;
import com.digital.dl2.business.core.obj.PgBookshelfItem;
import com.digital.dl2.business.core.obj.PgGroup;
import com.digital.dl2.business.core.obj.PgResourcesDetail;
import com.digital.dl2.business.core.obj.PgResult;

import java.io.File;
import java.util.List;

import rx.Observable;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public abstract class BookshelfManager {

    private static BookshelfManager mBookshelfManager;

    public static BookshelfManager getInstance() {
        if (mBookshelfManager == null)
            throw new NullPointerException("BookshelfManager is not init");
        return mBookshelfManager;
    }

    //初始化1
    public static BookshelfManager init(Context context) {
        if (mBookshelfManager == null)
            mBookshelfManager = new BookshelfModule(context);
        return mBookshelfManager;
    }

    //书架 插入数据库
    public abstract void insertBookList(List<PgBookshelfItem> pgList);

    //书架 插入数据库
    public abstract void insertBook(PgBookshelfItem book);

    //云书城
    public abstract void insertBookstoreBook(PgBookForBookstoreDetail book);

    //资源 插入数据库
    public abstract void insertResource(PgResourcesDetail resourcesDetail);

    //图书馆 插入数据库
    public abstract void insertBookForLibrary(PgBookForLibraryDetail book);
    //临时用户图书馆插入数据库
    public abstract void insertProvisionalityBookForLibrary(PgBookForLibraryDetail book);

    public abstract void insertBookForCloudBookstore(PgBookForBookstoreDetail book);

    //更新借阅时间
    public abstract void updateBookBorrowTime(int bookId);

    //获取图书列表
    public abstract List<PgBookshelfItem> getBookShelfItemList(int type, int offset, int count);

    //根据分组信息查询所有图书列表
    public abstract List<PgBookshelfItem> getBookShelfItemListByGroup(String group, int offset, int count);

    //移除图书
    public abstract void removeBookById(int entityId);

    //归还图书
    public abstract Observable<PgResult> returnBookById(int userId, String token, int entityId, int orgId);

    //续借图书
    public abstract Observable<Boolean> renewBookById(int userId, String token, int entityId, int orgId);

    //获取借书列表
    public abstract Observable<List<PgBookshelfItem>> getBorrowingBookList(int userId, String token);

    //根据ID查询图书
    public abstract PgBookshelfItem getBookById(int bookId);

    //更新图书所属分组
    public abstract void updateBookByGroup(int id, String group);

    public abstract void updateBookByUnzipState(int id, int unzipState);

    //更新图书本地路径
    public abstract void updateBookByLocalUrl(int id, String localUrl);

    //查询 所有分组
    public abstract List<PgGroup> queryAllGroup();

    //添加 分组
    public abstract void insertGroup(String name);

    //修改 分组
    public abstract void updateGroup(int id, String name);

    //删除 分组
    public abstract void deleteGroup(int id);

    //删除文件
    public abstract void deleteFile(File file);

    public abstract void clearData();

}
