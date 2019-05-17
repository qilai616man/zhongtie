package com.digital.dl2.business.util;

/**
 * Created by qinqi on 15/12/19.
 */
public class DownloadEvent {
    public static final int EVENT_PROGRESS = 1;
    public static final int EVENT_PRE_START = 2;
    public static final int EVENT_START = 3;
    public static final int EVENT_CANCEL = 4;
    public static final int EVENT_FINISH = 5;
    public static final int EVENT_PAUSE = 6;
    public static final int EVENT_WAITING = 7;
    public static final int EVENT_ERROR = 8;

    public int event;
    public String url;
    public long totalLength;
    public long downloadedBytes;
    public int state;

}
