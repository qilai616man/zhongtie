package com.digital.dl2.business.core.module;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDiskIOException;
import android.os.Environment;

import com.digital.dl2.business.core.manager.BookshelfManager;
import com.digital.dl2.business.core.obj.PgBookForBookstoreDetail;
import com.digital.dl2.business.core.obj.PgBookForLibraryDetail;
import com.digital.dl2.business.core.obj.PgBookshelfItem;
import com.digital.dl2.business.core.obj.PgGroup;
import com.digital.dl2.business.core.obj.PgResourcesDetail;
import com.digital.dl2.business.core.obj.PgResult;
import com.digital.dl2.business.database.DatabaseManager;
import com.digital.dl2.business.database.obj.DbBookshelfEntity;
import com.digital.dl2.business.database.obj.DbGroup;
import com.digital.dl2.business.net.NetHelper;
import com.digital.dl2.business.net.obj.NetBookshelfResult;
import com.digital.dl2.business.net.obj.NetResult;
import com.digital.dl2.business.util.Constant;
import com.google.gson.Gson;
import com.goyourfly.gdownloader.GDownloader;
import com.goyourfly.gdownloader.db.DbDownloadExt;
import com.goyourfly.gdownloader.utils.Ln;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class BookshelfModule extends BookshelfManager {
    private Context mContext;
    private boolean a = false;

    public BookshelfModule(Context context) {
        mContext = context;
    }


    /**
     * 获取图书列表
     *
     * @param type
     * @param offset
     * @param count
     * @return
     */
    @Override
    public List<PgBookshelfItem> getBookShelfItemList(int type, int offset, int count) {
        List<DbBookshelfEntity> list = new ArrayList<>();

        Ln.d("BookshelfModule:getBookShelfItemList:type:" + type);

        if (type == Constant.BOOKSHELF_TYPE_ALL) {//全部
            list = DatabaseManager.getInstance().queryAllBook();
            Ln.d("BookshelfModule:getBookShelfItemList:queryAllBook:" + list);
        } else if (type == Constant.BOOKSHELF_TYPE_BUY) {//购买
            list = DatabaseManager.getInstance().queryBookListBySource(type);
        } else if (type == Constant.BOOKSHELF_TYPE_BORROWED) {//借阅
            list = DatabaseManager.getInstance().queryBookListBySource(type);
            Ln.d("BookshelfModule:getBookShelfItemList:queryBookListBySource:" + list);
        } else if (type == Constant.BOOKSHELF_TYPE_RESOURCES) {//资源
            list = DatabaseManager.getInstance().queryBookByType(type);
        }

        List<PgBookshelfItem> pgList = PgBookshelfItem.getPgListByDbList(list);
        for (PgBookshelfItem book : pgList) {
            DbDownloadExt downloadExt = GDownloader.getInstance().getDownloadState(book.getDownloadUrl());
            if (downloadExt == null) {
                book.setDownloadState(DbDownloadExt.DOWNLOAD_STATE_NOT_DOWNLOAD);
            } else {
                book.setDownloadState(downloadExt.getDownloadState());
                float progress = downloadExt.getFileSize() == 0 ? 0 : (float) downloadExt.getDownloadProgress() / (float) downloadExt.getFileSize();
                book.setDownloadProgress(progress);
            }
        }

        Ln.d("BookshelfModule:getBookShelfItemList:list:" + list);
        return pgList;
    }

    /**
     * 书架 插入数据库
     *
     * @param pgList
     */
    @Override
    public void insertBookList(List<PgBookshelfItem> pgList) {
        Ln.d("BookshelfModule:insertBookList:pgList:" + pgList);

//        List<DbBookshelfEntity> dbList = new ArrayList<>();

        for (PgBookshelfItem pg : pgList) {
            DbBookshelfEntity db = DatabaseManager.getInstance().queryBookById(pg.getEntityId());

            if (db == null) {

                db = new DbBookshelfEntity();
                db.setEntityId(pg.getEntityId());
                db.setFrontCover(pg.getFrontCover());
                db.setName(pg.getName());
                db.setAuthor(pg.getAuthor());
                db.setIntroduction(pg.getIntroduction());
                db.setLastReadTime(pg.getLastReadTime());
                db.setBorrowedTime(pg.getBorrowTime());
                db.setDownloadUrl(pg.getDownloadUrl());
                db.setFileSize(pg.getFileSize());
                db.setLocalUrl(Constant.FILE_READ_PATH + String.valueOf(pg.getEntityId()));
                db.setUnzipState(Constant.BOOK_UNZIP_STATE_FAIL);
                db.setSource(pg.getSource());
//            db.setType();//属性未知
                db.setGroup(pg.getGroup());
                db.setStatus(Constant.DB_STATUS_INSERT);
                Ln.d("BookshelfModule:insertBookList:db:" + db);

                int id = DatabaseManager.getInstance().insertBook(db);
                Ln.d("BookshelfModule:insertBookList:id:" + id);

//                dbList.add(db);
            }
        }
//        DatabaseManager.getInstance().insertBookList(dbList);
    }

    /**
     * 书架插入数据库
     *
     * @param book
     */
    @Override
    public void insertBook(PgBookshelfItem book) {
        DbBookshelfEntity db = DatabaseManager.getInstance().queryBookById(book.getEntityId());

        if (db == null) {

            db = new DbBookshelfEntity();
            db.setEntityId(book.getEntityId());
            db.setFrontCover(book.getFrontCover());
            db.setName(book.getName());
            db.setAuthor(book.getAuthor());
            db.setIntroduction(book.getIntroduction());
            db.setLastReadTime(book.getLastReadTime());
            db.setBorrowedTime(book.getBorrowTime());
            db.setDownloadUrl(book.getDownloadUrl());
            db.setFileSize(book.getFileSize());
            db.setLocalUrl(Constant.FILE_READ_PATH + String.valueOf(book.getEntityId()));
            db.setUnzipState(Constant.BOOK_UNZIP_STATE_FAIL);
            db.setSource(book.getSource());
            db.setGroup(book.getGroup());
            db.setStatus(Constant.DB_STATUS_INSERT);
            Ln.d("BookshelfModule:insertBookList:db:" + db);

            int id = DatabaseManager.getInstance().insertBook(db);
            Ln.d("BookshelfModule:insertBookList:id:" + id);

        }
    }

    /**
     * 书架插入数据库
     *
     * @param book
     */
    @Override
    public void insertBookstoreBook(PgBookForBookstoreDetail book) {
        DbBookshelfEntity db = DatabaseManager.getInstance().queryBookById(book.getEntityId());

        if (db == null) {

            db = new DbBookshelfEntity();
            db.setEntityId(book.getEntityId());
            db.setFrontCover(book.getFrontCover());
            db.setName(book.getName());
            db.setAuthor(book.getAuthor());
            db.setIntroduction(book.getIntroduction());
            db.setDownloadUrl(book.getDownloadUrl());
            db.setLocalUrl(Constant.FILE_READ_PATH + String.valueOf(book.getEntityId()));
            db.setUnzipState(Constant.BOOK_UNZIP_STATE_FAIL);
            db.setStatus(Constant.DB_STATUS_INSERT);
            Ln.d("BookshelfModule:insertBookList:db:" + db);

            int id = DatabaseManager.getInstance().insertBook(db);
            Ln.d("BookshelfModule:insertBookList:id:" + id);

        }
    }

    /**
     * 资源 插入数据库
     *
     * @param book
     */
    @Override
    public void insertResource(PgResourcesDetail book) {
        DbBookshelfEntity db = DatabaseManager.getInstance().queryBookById(book.getEntityId());
        if (db == null) {

            db = new DbBookshelfEntity();
            db.setEntityId(book.getEntityId());
            db.setFrontCover(book.getFrontCover());
            db.setName(book.getName());
            db.setAuthor(book.getAuthor());
            db.setIntroduction(book.getIntroduction());
            db.setDownloadUrl(book.getDownloadUrl());
            db.setLocalUrl(Constant.FILE_READ_PATH + String.valueOf(book.getEntityId()));
            db.setUnzipState(Constant.BOOK_UNZIP_STATE_FAIL);
            db.setSource(Constant.BOOK_SOURCE_BUY);
            db.setStatus(Constant.DB_STATUS_INSERT);
            Ln.d("BookshelfModule:insertBookList:db:" + db);

            int id = DatabaseManager.getInstance().insertBook(db);
            Ln.d("BookshelfModule:insertBookList:id:" + id);
        }
    }

    /**
     * 临时用户 图书馆  插入数据库
     * @param book
     */
    @Override
    public void insertProvisionalityBookForLibrary(PgBookForLibraryDetail book){
        DbBookshelfEntity db = DatabaseManager.getInstance().queryBookById(book.getEntityId());
        long time = System.currentTimeMillis() / 1000l;

        if (db == null) {
            db = new DbBookshelfEntity();
            db.setEntityId(book.getEntityId());
            db.setFrontCover(book.getFrontCover());
            db.setName(book.getName());
            db.setAuthor(book.getAuthor());
            db.setIntroduction(book.getIntroduction());
            db.setBorrowedTime(time);
            db.setDownloadUrl(book.getDownloadUrl());
            db.setLocalUrl(Constant.FILE_READ_PATH + String.valueOf(book.getEntityId()));
            db.setUnzipState(Constant.BOOK_UNZIP_STATE_FAIL);
            db.setSource(Constant.BOOK_SOURCE_BORROWED);
            db.setStatus(Constant.DB_STATUS_INSERT);
            Ln.d("BookshelfModule:insertBookList:db:" + db);

            int id = DatabaseManager.getInstance().insertBook(db);
            Ln.d("BookshelfModule:insertBookList:id:" + id);
        }
    }

    /**
     * 图书馆  插入数据库
     *
     * @param book
     */
    @Override
    public void insertBookForLibrary(PgBookForLibraryDetail book) {
        DbBookshelfEntity db = DatabaseManager.getInstance().queryBookById(book.getEntityId());
        long time = System.currentTimeMillis() / 1000l;

        if (db == null) {
            db = new DbBookshelfEntity();
            db.setEntityId(book.getEntityId());
            db.setFrontCover(book.getFrontCover());
            db.setName(book.getName());
            db.setAuthor(book.getAuthor());
            db.setIntroduction(book.getIntroduction());
            db.setBorrowedTime(time);
            db.setDownloadUrl(book.getDownloadUrl());
            db.setLocalUrl(Constant.FILE_READ_PATH + String.valueOf(book.getEntityId()));
            db.setUnzipState(Constant.BOOK_UNZIP_STATE_FAIL);
            db.setSource(Constant.BOOK_SOURCE_BORROWED);
            db.setType(book.getType());
            db.setStatus(Constant.DB_STATUS_INSERT);
            Ln.d("BookshelfModule:insertBookList:db:" + db);

            int id = DatabaseManager.getInstance().insertBook(db);
            Ln.d("BookshelfModule:insertBookList:id:" + id);
        }
    }

    /**
     * 云书城  插入数据库
     *
     * @param book
     */
    @Override
    public void insertBookForCloudBookstore(PgBookForBookstoreDetail book) {
        DbBookshelfEntity db = DatabaseManager.getInstance().queryBookById(book.getEntityId());

        if (db == null) {
            db = new DbBookshelfEntity();
            db.setEntityId(book.getEntityId());
            db.setFrontCover(book.getFrontCover());
            db.setName(book.getName());
            db.setAuthor(book.getAuthor());
            db.setIntroduction(book.getIntroduction());
            db.setDownloadUrl(book.getDownloadUrl());
            db.setLocalUrl(Constant.FILE_READ_PATH + String.valueOf(book.getEntityId()));
            db.setUnzipState(Constant.BOOK_UNZIP_STATE_FAIL);
            db.setSource(Constant.BOOK_SOURCE_BUY);
            db.setStatus(Constant.DB_STATUS_INSERT);
            Ln.d("BookshelfModule:insertBookForCloudBookstore:db:" + db);

            int id = DatabaseManager.getInstance().insertBook(db);
            Ln.d("BookshelfModule:insertBookForCloudBookstore:id:" + id);
        }
    }

    /**
     * 更新图书借阅时间
     *
     * @param bookId
     */
    @Override
    public void updateBookBorrowTime(int bookId) {
        DbBookshelfEntity db = DatabaseManager.getInstance().queryBookById(bookId);
        long time = System.currentTimeMillis() / 1000l;
        //+(60*60*24*30)    -(60*60*24*31)
        if (db != null) {
            db.setBorrowedTime(time);
        }

        DatabaseManager.getInstance().updateBook(db);
    }

    /**
     * 根据分组信息查询所有图书列表
     *
     * @param group
     * @param offset
     * @param count
     * @return
     */
    @Override
    public List<PgBookshelfItem> getBookShelfItemListByGroup(String group, int offset, int count) {
        List<DbBookshelfEntity> list = DatabaseManager.getInstance().queryBookByGroup(group);

        return PgBookshelfItem.getPgListByDbList(list);
    }

    /**
     * 移除图书
     *
     * @param entityId
     * @return
     */
    @Override
    public void removeBookById(int entityId) {
        try {
            DbBookshelfEntity db = DatabaseManager.getInstance().queryBookById(entityId);
            if (db != null) {
                //删除文件
                File file1 = new File(db.getDownloadUrl());
                File file2 = new File(db.getLocalUrl());
                if(file1.exists()){

                    deleteFile(file1);
                }
//                deleteFile(file2);

                Ln.d("BookshelfModule:removeBookById:db:" + db.toString());
                GDownloader.getInstance().delete(db.getDownloadUrl());
                DatabaseManager.getInstance().deleteBook(entityId);
            }
        } catch (SQLiteDiskIOException e) {
            e.printStackTrace();
        }
    }
    //ymd 10.22 删除解压文件（阅读）
    public void deleteLocalFile(File file) {

        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        }
    }
    //ymd 删除下载文件
    public void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                return;
            }

            for(File file1 : childFiles){
                file1.delete();
            }

//            for (int i = 0; i < childFiles.length; i++) {
//                deleteFile(childFiles[i]);
//                childFiles[i].delete();
//            }

        }
    }

    /**
     * 归还图书
     *
     * @param userId
     * @param token
     * @param entityId
     * @param orgId
     * @return
     */
    @Override
    public Observable<PgResult> returnBookById(final int userId, final String token, final int entityId, final int orgId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://ml.crphdm.com/?app=book&controller=forTwo&action=returnBook";
//                String url = "http://192.168.4.253/?app=book&controller=forTwo&action=returnBook";

                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&goods_id=").append(entityId)
                        .append("&org_id=").append(orgId)
                        .toString();

                Ln.d("BookshelfModule:returnBookById:urlStr:" + urlStr);

                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<PgResult>>() {
            @Override
            public Observable<PgResult> call(String s) {
                Ln.d("BookshelfModule:returnBookById:result:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("BookshelfModule:returnBookById:net:" + net.toString());

                if (!net.isStatus()) {
                    if (net.getError_code() == Constant.TOKEN_ERROR) {
//                        Exceptions.propagate(new NetworkErrorException(Constant.TOKEN_ERROR_MESSAGE));
                        Intent intent = new Intent();
                        intent.setAction(Constant.ACTION_TOKEN_ERROR);
                        intent.putExtra(Constant.INTENT_ERROR_MESSAGE, net.getMessage());
                        mContext.sendBroadcast(intent);
                    }

                    if (net.getError_code() == Constant.TOKEN_EXPIRED) {
//                        Exceptions.propagate(new NetworkErrorException(Constant.TOKEN_ERROR_MESSAGE));
                        Intent intent = new Intent();
                        intent.setAction(Constant.ACTION_TOKEN_EXPIRED);
                        intent.putExtra(Constant.INTENT_ERROR_MESSAGE, net.getMessage());
                        mContext.sendBroadcast(intent);
                    }

                    Exceptions.propagate(new NetworkErrorException(net.getMessage()));
                }

                PgResult pgResult = PgResult.getPgByNet(net);

                return Observable.just(pgResult);
            }
        });
    }

    /**
     * 续借图书
     *
     * @param entityId
     * @return
     */
    @Override
    public Observable<Boolean> renewBookById(final int userId, final String token, final int entityId, final int orgId) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://ml.crphdm.com/?app=book&controller=forTwo&action=reBorrowBook";
//                String url = "http://192.168.4.253/?app=book&controller=forTwo&action=reBorrowBook";

                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .append("&goods_id=").append(entityId)
                        .append("&org_id=").append(orgId)
                        .toString();

                Ln.d("BookshelfModule:renewBookById:urlStr:" + urlStr);

                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                Ln.d("BookshelfModule:renewBookById:result:" + s);
                NetResult net = new Gson().fromJson(s, NetResult.class);
                Ln.d("BookshelfModule:renewBookById:net:" + net.toString());

                if (!net.isStatus()) {
                    if (net.getError_code() == Constant.TOKEN_ERROR) {
//                        Exceptions.propagate(new NetworkErrorException(Constant.TOKEN_ERROR_MESSAGE));
                        Intent intent = new Intent();
                        intent.setAction(Constant.ACTION_TOKEN_ERROR);
                        intent.putExtra(Constant.INTENT_ERROR_MESSAGE, net.getMessage());
                        mContext.sendBroadcast(intent);
                    }

                    if (net.getError_code() == Constant.TOKEN_EXPIRED) {
//                        Exceptions.propagate(new NetworkErrorException(Constant.TOKEN_ERROR_MESSAGE));
                        Intent intent = new Intent();
                        intent.setAction(Constant.ACTION_TOKEN_EXPIRED);
                        intent.putExtra(Constant.INTENT_ERROR_MESSAGE, net.getMessage());
                        mContext.sendBroadcast(intent);
                    }

                    Exceptions.propagate(new NetworkErrorException(net.getMessage()));
                }

                return Observable.just(net.isStatus());
            }
        });
    }


    /**
     * 获取借书列表
     * ****************************************************
     * 修改为不需要返回值  登录成功后   业务直接调用  存入数据库
     *
     * @return
     */
    @Override
    public Observable<List<PgBookshelfItem>> getBorrowingBookList(final int userId, final String token) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                //URL生成
                String url = "http://ml.crphdm.com/?app=book&controller=forTwo&action=getMyBorrow";
//                String url = "http://192.168.4.253/?app=book&controller=forTwo&action=getMyBorrow";

                String urlStr = new StringBuffer(url)
                        .append("&user_id=").append(userId)
                        .append("&login_token=").append(token)
                        .toString();

                Ln.d("BookshelfModule:getBorrowingBookList:urlStr:" + urlStr);

                return NetHelper.getData(urlStr);
            }
        }).flatMap(new Func1<String, Observable<List<PgBookshelfItem>>>() {
            @Override
            public Observable<List<PgBookshelfItem>> call(String s) {
                Ln.d("BookshelfModule:getBorrowingBookList:result:" + s);
                List<PgBookshelfItem> list = new ArrayList<PgBookshelfItem>();
                NetBookshelfResult net = new Gson().fromJson(s, NetBookshelfResult.class);
                Ln.d("BookshelfModule:getBorrowingBookList:net:" + net.toString());

//                if (!net.isStatus()) {
//                    if (net.getError_code() == Constant.TOKEN_ERROR) {
////                        Exceptions.propagate(new NetworkErrorException(Constant.TOKEN_ERROR_MESSAGE));
//                        Intent intent = new Intent();
//                        intent.setAction(Constant.ACTION_TOKEN_ERROR);
//                        intent.putExtra(Constant.INTENT_ERROR_MESSAGE, net.getMessage());
//                        mContext.sendBroadcast(intent);
//                    }
//
//                    if (net.getError_code() == Constant.TOKEN_EXPIRED) {
////                        Exceptions.propagate(new NetworkErrorException(Constant.TOKEN_ERROR_MESSAGE));
//                        Intent intent = new Intent();
//                        intent.setAction(Constant.ACTION_TOKEN_EXPIRED);
//                        intent.putExtra(Constant.INTENT_ERROR_MESSAGE, net.getMessage());
//                        mContext.sendBroadcast(intent);
//                    }
//
//                    Exceptions.propagate(new NetworkErrorException(net.getMessage()));
//                }
                if (net.isStatus() && net.getData() != null) {
                    list = PgBookshelfItem.getPgListByNetList(net.getData());
                    Ln.d("BookshelfModule:getBorrowingBookList:list:" + list);
                }

                return Observable.just(list);
            }
        });
    }

    /**
     * 根据ID查询图书
     *
     * @param bookId
     * @return
     */
    @Override
    public PgBookshelfItem getBookById(int bookId) {
        DbBookshelfEntity db = DatabaseManager.getInstance().queryBookById(bookId);
        PgBookshelfItem pg = PgBookshelfItem.getPgByDb(db);
        return pg;
    }

    /**
     * 修改图书所属分组
     *
     * @param id
     * @param group
     */
    @Override
    public void updateBookByGroup(int id, String group) {
        DbBookshelfEntity db = DatabaseManager.getInstance().queryBookById(id);

        db.setGroup(group);

        DatabaseManager.getInstance().updateBook(db);
    }

    /**
     * 修改图书解压状态
     *
     * @param id
     * @param unzipState
     */
    @Override
    public void updateBookByUnzipState(int id, int unzipState) {
        DbBookshelfEntity db = DatabaseManager.getInstance().queryBookById(id);
        if (db == null)
            return;

        db.setUnzipState(unzipState);

        Ln.d("BookshelfModule:updateBookByUnzipState:db:" + db.toString());

        DatabaseManager.getInstance().updateBook(db);
    }


    /**
     * 修改图书本地解压路径
     *
     * @param id
     * @param localUrl
     */
    @Override
    public void updateBookByLocalUrl(int id, String localUrl) {
        DbBookshelfEntity db = DatabaseManager.getInstance().queryBookById(id);
        db.setLocalUrl(localUrl);

        DatabaseManager.getInstance().updateBook(db);
    }


    /**
     * 查询所有分组名称
     *
     * @return
     */
    @Override
    public List<PgGroup> queryAllGroup() {
        List<DbGroup> dbList = DatabaseManager.getInstance().queryAllGroup();

        List<PgGroup> list = PgGroup.getPgListByDbList(dbList);

        return list;
    }

    /**
     * 插入分组名称
     *
     * @param name
     */
    @Override
    public void insertGroup(String name) {
        int id = DatabaseManager.getInstance().insertGroup(name);
        Ln.d("BookshelfModule:insertGroup:id" + id);
    }

    /**
     * 修改分组名称
     *
     * @param id
     * @param name
     */
    @Override
    public void updateGroup(int id, String name) {
        DatabaseManager.getInstance().updateGroup(id, name);
    }

    /**
     * 删除分组名称
     *
     * @param id
     */
    @Override
    public void deleteGroup(int id) {
        DatabaseManager.getInstance().deleteGroup(id);
    }

    @Override
    public void clearData() {
        String path = Environment.getExternalStorageDirectory().getAbsoluteFile()
                + File.separator
                + "crphdm";
        File file = new File(path);
        deleteFile(file);

    }


}
