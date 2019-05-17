package com.goyourfly.gdownloader;

import android.content.Context;
import android.support.annotation.Nullable;

import com.goyourfly.gdownloader.db.DbDownloadExt;
import com.goyourfly.gdownloader.helper.DownloadFileHelper;
import com.goyourfly.gdownloader.helper.DownloadHelper;
import com.goyourfly.gdownloader.name_generator.NameGenerator;
import com.goyourfly.gdownloader.utils.ByteUtils;
import com.goyourfly.gdownloader.utils.Ln;

import java.io.File;
import java.util.HashMap;
import java.util.List;


/**
 * Created by gaoyf on 15/6/18.
 */
public class GDownloaderImpl extends GDownloader {
    private Context mContext;
    private DownloadHelper.DownloadListener mDownloadListener;
    private HashMap<String, Integer> transformerMap = new HashMap<>();
    private DbDownloadExt.Helper mDownloadHelper;

    public GDownloaderImpl(final Context context, final String path, int maxTask, @Nullable NameGenerator nameGenerator) {
        mContext = context;
        mDownloadHelper = DbDownloadExt.Helper.getInstance(mContext);
        if (nameGenerator == null) {
            nameGenerator = new NameGenerator() {
                @Override
                public String getName(String url) {
                    DbDownloadExt dbDownloadExt = mDownloadHelper.queryDownloadExt(url);
                    if (dbDownloadExt == null || dbDownloadExt.getDownloadState() != DbDownloadExt.DOWNLOAD_STATE_DOWNLOADED) {
                        return String.valueOf(url.hashCode());
                    } else {
                        if (dbDownloadExt.getFilename() == null) {
                            return String.valueOf(url.hashCode());
                        }
                        File file = new File(path, dbDownloadExt.getFilename());
                        if (file.exists() && file.length() > 0) {
                            return dbDownloadExt.getFilename();
                        } else {
                            return String.valueOf(url.hashCode());
                        }
                    }
                }
            };
        }
        DownloadFileHelper.init(path, nameGenerator);
        DownloadHelper.init(context, maxTask);
    }

    @Override
    public void download(String url) {
        DownloadHelper.DownloadListener listener = new DownloadHelper.DownloadListener() {
            @Override
            public void onPreStart(String url) {
                Ln.d("GDownloaderImpl:onPreStart:" + url);
                if (DbDownloadExt.Helper
                        .getInstance(mContext).queryDownloadExt(url) == null) {
                    DbDownloadExt dbDownloadExt1 = new DbDownloadExt();
                    dbDownloadExt1.setUrl(url);
                    dbDownloadExt1.setDownloadProgress(0);
                    dbDownloadExt1.setDownloadState(DbDownloadExt.DOWNLOAD_STATE_PREPARING);
                    dbDownloadExt1.setLastUpdateTime(System.currentTimeMillis());
                    Ln.d("GDownloaderImpl:onPreStart:insert:" + url);
                    mDownloadHelper.insertOrUpdate(dbDownloadExt1);
                } else {
                    mDownloadHelper.updateState(url, DbDownloadExt.DOWNLOAD_STATE_PREPARING);
                }
                if (mDownloadListener != null)
                    mDownloadListener.onPreStart(url);
            }

            @Override
            public void onStart(String url, long fileLength, long localFileSize) {
                Ln.d("GDownloaderImpl:onStart-fileLength:" + ByteUtils.getMb(fileLength) + ",localFileSize:" + ByteUtils.getMb(localFileSize));
                mDownloadHelper.updateFileLength(url, fileLength);
                mDownloadHelper.updateState(url, DbDownloadExt.DOWNLOAD_STATE_DOWNLOADING);
                if (mDownloadListener != null)
                    mDownloadListener.onStart(url, fileLength, localFileSize);
            }

            @Override
            public void onProgress(String url, long totalLength, long downloadedBytes) {
                int transfer = (int) (downloadedBytes * 100 * 5 / totalLength);
                if (!transformerMap.containsKey(url) ||
                        (transformerMap.containsKey(url) && transformerMap.get(url) != transfer)) {
                    mDownloadHelper.updateProgress(url, downloadedBytes);
                    if (mDownloadListener != null)
                        mDownloadListener.onProgress(url, totalLength, downloadedBytes);
                    transformerMap.put(url, transfer);
                }
            }

            @Override
            public void onPause(String url) {
                Ln.d("GDownloaderImpl:onPause:" + url);
                transformerMap.remove(url);
                mDownloadHelper.updateState(url, DbDownloadExt.DOWNLOAD_STATE_PAUSE);
                if (mDownloadListener != null)
                    mDownloadListener.onPause(url);
            }

            @Override
            public void onWaiting(String url) {
                Ln.d("GDownloaderImpl:onWaiting:" + url);
                mDownloadHelper.updateState(url, DbDownloadExt.DOWNLOAD_STATE_WAITING);
                if (mDownloadListener != null)
                    mDownloadListener.onWaiting(url);
            }

            @Override
            public void onCancel(String url) {
                Ln.d("GDownloaderImpl:onCancel:" + url);
                transformerMap.remove(url);
                mDownloadHelper.delete(url);
                if (mDownloadListener != null)
                    mDownloadListener.onCancel(url);
            }

            @Override
            public void onFinish(String url) {
                Ln.d("GDownloaderImpl:onFinish:" + url);
                transformerMap.remove(url);
                File file = DownloadFileHelper.getInstance().get(url);
                if (mDownloadHelper.queryDownloadExt(url) == null) {
                    if (mDownloadListener != null)
                        mDownloadListener.onError(url, "数据库不存在该记录");
                    return;
                }
                String realName = mDownloadHelper.queryDownloadExt(url).getFilename();
                DownloadFileHelper.getInstance().rename(file, realName);
                mDownloadHelper.updateState(url, DbDownloadExt.DOWNLOAD_STATE_DOWNLOADED);
                if (mDownloadListener != null)
                    mDownloadListener.onFinish(url);
            }

            @Override
            public void onError(String url, String err) {
                Ln.e("GDownloaderImpl:onError:url:" + url + ",err:" + err);
                transformerMap.remove(url);
                mDownloadHelper.updateState(url, DbDownloadExt.DOWNLOAD_STATE_ERROR);
                if (mDownloadListener != null)
                    mDownloadListener.onError(url, err);
            }
        };
        listener.onPreStart(url);

        DownloadHelper.getInstance().start(url,
                DownloadFileHelper.getInstance().get(url),
                listener
        );
    }

    @Override
    public void pause(String url) {
        Ln.d("GDownloaderImpl:pause:url:" + url);
        mDownloadHelper.updateState(url, DbDownloadExt.DOWNLOAD_STATE_PAUSE);
        boolean result = DownloadHelper.getInstance().pause(url);
        if (!result)
            mDownloadListener.onPause(url);
    }

    @Override
    public void delete(String url) {
        Ln.d("GDownloaderImpl:delete:url:" + url);
        DownloadFileHelper.getInstance().delete(url);
        mDownloadHelper.delete(url);
        boolean result = DownloadHelper.getInstance().cancel(url);
        if (!result)
            mDownloadListener.onCancel(url);
    }

    @Override
    public int getRunningTaskCount() {
        return DownloadHelper.getInstance().getRunningTaskCount();
    }

    @Override
    public void registerListener(DownloadHelper.DownloadListener listener) {
        mDownloadListener = listener;
    }

    @Override
    public void unRegisterListener() {
        mDownloadListener = null;
    }

    @Override
    public List<DbDownloadExt> getDownloading() {
        return mDownloadHelper.queryAllNotDownloadedExt();
    }

    @Override
    public List<DbDownloadExt> getDownloaded() {
        return mDownloadHelper.queryAllDownloadedExt();
    }

    @Override
    public DbDownloadExt getDownloadState(String url) {
        return mDownloadHelper.queryDownloadExt(url);
    }

    @Override
    public File getFile(String url) {
        return DownloadFileHelper.getInstance().get(url);
    }

    @Override
    public void startAllWithoutPause() {
        List<DbDownloadExt> list = mDownloadHelper.queryAllDownloadExt();
        Ln.d("GDownloaderImpl:startAllWithoutPause:list:" + list.toString());
        for (DbDownloadExt dbDownloadExt : list) {
            if (dbDownloadExt.getDownloadState() == DbDownloadExt.DOWNLOAD_STATE_DOWNLOADING
                    || dbDownloadExt.getDownloadState() == DbDownloadExt.DOWNLOAD_STATE_WAITING
                    || dbDownloadExt.getDownloadState() == DbDownloadExt.DOWNLOAD_STATE_PREPARING) {
                Ln.d("GDownloaderImpl:startAllWithoutPause:url:" + dbDownloadExt.getUrl());
                download(dbDownloadExt.getUrl());
            }
        }
    }

    @Override
    public void forceStartAll() {
        List<DbDownloadExt> list = mDownloadHelper.queryAllDownloadExt();
        Ln.d("GDownloaderImpl:startAllWithoutPause:list:" + list.toString());
        for (DbDownloadExt dbDownloadExt : list) {
            if (dbDownloadExt.getDownloadState() == DbDownloadExt.DOWNLOAD_STATE_PAUSE
                    || dbDownloadExt.getDownloadState() == DbDownloadExt.DOWNLOAD_STATE_ERROR) {
                Ln.d("GDownloaderImpl:startAllWithoutPause:url:" + dbDownloadExt.getUrl());
                download(dbDownloadExt.getUrl());
            }
        }
    }

    @Override
    public void stopAll() {
        List<DbDownloadExt> list = mDownloadHelper.queryAllDownloadExt();
        Ln.d("GDownloaderImpl:stopAll:list:" + list.toString());
        for (DbDownloadExt dbDownloadExt : list) {
            if (dbDownloadExt.getDownloadState() == DbDownloadExt.DOWNLOAD_STATE_DOWNLOADING ||
                    dbDownloadExt.getDownloadState() == DbDownloadExt.DOWNLOAD_STATE_WAITING) {
                Ln.d("GDownloaderImpl:stopAll:url:" + dbDownloadExt.getUrl());
                pause(dbDownloadExt.getUrl());
            }
        }
    }

    @Override
    public void deleteAllDownloaded() {
        List<DbDownloadExt> list = mDownloadHelper.queryAllDownloadedExt();
        Ln.d("GDownloaderImpl:deleteAllDownloaded:list:" + list.toString());
        for (DbDownloadExt dbDownloadExt : list) {
            DownloadFileHelper.getInstance().delete(dbDownloadExt.getUrl());
            mDownloadHelper.delete(dbDownloadExt.getUrl());
            Ln.d("GDownloaderImpl:deleteAllDownloaded:url:" + dbDownloadExt.getUrl());
            mDownloadListener.onCancel(dbDownloadExt.getUrl());
        }
    }

    @Override
    public void shutdown() {
        List<DbDownloadExt> list = mDownloadHelper.queryAllNotDownloadedExt();
        Ln.d("GDownloaderImpl:shutdown");
        DownloadHelper.getInstance().shutdown();
        for (DbDownloadExt dbDownloadExt : list) {
            Ln.d("GDownloaderImpl:shutdown:" + dbDownloadExt.getUrl());
            mDownloadHelper.updateState(dbDownloadExt.getUrl(), DbDownloadExt.DOWNLOAD_STATE_PAUSE);
            mDownloadListener.onPause(dbDownloadExt.getUrl());
        }
    }

    @Override
    public void clearAll() {
        DownloadHelper.getInstance().shutdown();
        List<DbDownloadExt> list = mDownloadHelper.queryAllDownloadExt();
        Ln.d("GDownloaderImpl:clearAll:list:" + list.toString());
        for (DbDownloadExt dbDownloadExt : list) {
            mDownloadHelper.delete(dbDownloadExt.getUrl());
            if (dbDownloadExt.getDownloadState() != DbDownloadExt.DOWNLOAD_STATE_NOT_DOWNLOAD
                    && mDownloadListener != null) {
                mDownloadListener.onCancel(dbDownloadExt.getUrl());
                Ln.d("GDownloaderImpl:clearAll:url:" + dbDownloadExt.getUrl());
            }
        }
        DownloadFileHelper.getInstance().clear();
    }


}
