package com.crphdm.dl2.utils;

import android.net.Uri;
import android.util.Log;

import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.BookReadingException;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;

import java.io.File;

/**
 * Created by Administrator on 2017/9/20.
 * gy  关键字搜索书架中图书
 */

public class MyFbreaderClass {
    static FBReaderApp myFBReaderApp;
    public static String  makeFbreader(String asskey, String uri,BookCollectionShadow bookCollectionShadow,String query){
//         myFBReaderApp = (FBReaderApp) FBReaderApp.Instance();
        Log.i("ccccc","i==1");
        if (myFBReaderApp == null) {
            Log.i("cccccccc","myFBReaderApp=="+myFBReaderApp);
            myFBReaderApp = new FBReaderApp(bookCollectionShadow);
        }

         Uri data = Uri.fromFile(new File(uri));
        Log.i("ccccc","i==5");
         Book myBook = createBookForFile(ZLFile.createFileByPath(
//                 readUrl
                 data.getPath()
         ));

        myBook.setAesKey(asskey);
        Log.i("ccccc","my book==="+myBook);
        try {
            BookModel Model = BookModel.createModel(myBook);
            int length = Model.getTextModel().getParagraphsNumber();
            Log.i("ccccc","mynodel===="+Model+"length=="+length);
            int i=Model.getTextModel().search(query,0,length+1,false);
            return i+"";
        } catch (BookReadingException e) {
            e.printStackTrace();
        }
        myFBReaderApp=null;
        return 0+"";
    }
   //在文件路径下创建图书
    private static Book createBookForFile(ZLFile file) {
        Log.i("ccccc","createBookFile"+file);
        if (file == null) {
            return null;
        }
        Book book = myFBReaderApp.Collection.getBookByFile(file);

        Log.i("ccccc","createbook"+book);
        if (book != null) {
            return book;
        }
        Log.i("cccccc","collection=="+myFBReaderApp.Collection);
        book = myFBReaderApp.Collection.getBookByFile(file);

        return book;
    }

}
