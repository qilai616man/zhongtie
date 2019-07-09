package com.crphdm.dl2.user.utils;

import com.goyourfly.gdownloader.utils.Ln;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Func0;

/**
 * Created by gaoyf on 15/6/10.
 */
public class NetHelper {
    private static long time;
    private static int TIME_OUT = 5;

    /**
     * 获取数据，用于也是用Observable的方式去处理
     *
     * @param url
     * @return
     */
    public static Observable<String> getData(final String url){
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    return Observable.just(getContent(url));
                } catch (Exception e) {
//                    Exceptions.propagate(e);
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    public static Observable<File> downloadFile(final String url, final File file) {
        return Observable.defer(new Func0<Observable<File>>() {
            @Override
            public Observable<File> call() {
                OkHttpClient client = new OkHttpClient();
                client.setConnectTimeout(TIME_OUT, TimeUnit.SECONDS); // connect timeout
                client.setReadTimeout(TIME_OUT, TimeUnit.SECONDS);    // socket timeout
                client.setWriteTimeout(TIME_OUT, TimeUnit.SECONDS);

                Request request = new Request.Builder().url(url)
                        .addHeader("Content-Type", "application/json").build();
                try {
                    Response response = client.newCall(request).execute();

                    InputStream in = response.body().byteStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    byte data[] = new byte[1024];
                    int count;
                    while ((count = in.read(data)) != -1) {
                        fileOutputStream.write(data, 0, count);
                    }
                    fileOutputStream.close();
                    in.close();
                    return Observable.just(file);
                } catch (Exception e) {
                    Exceptions.propagate(e);
                }
                return null;
            }
        });
    }

    public static File downloadFileSync(final String url, final File file) {

        Ln.d("DownloadFileSync:" + url + ",File:" + file.getPath());
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(TIME_OUT, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(TIME_OUT, TimeUnit.SECONDS);    // socket timeout
        client.setWriteTimeout(TIME_OUT, TimeUnit.SECONDS);

        Request request = new Request.Builder().url(url)
                .addHeader("Content-Type", "application/json").build();
        try {
            Response response = client.newCall(request).execute();

            InputStream in = response.body().byteStream();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data)) != -1) {
                fileOutputStream.write(data, 0, count);
            }
            fileOutputStream.close();
            in.close();
            return file;
        } catch (Exception e) {
            Exceptions.propagate(e);
        }
        return null;
    }

    /**
     * 获取数据，用于也是用Observable的方式去处理
     *
     * @param url
     * @return
     */
    public static Observable<String> postData(final String url) {
        return Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                try {
                    return Observable.just(postContent(url));
                } catch (IOException e) {
                    Exceptions.propagate(e);
                }
                return null;
            }
        });
    }

    /**
     * 只有这里没有使用异步，但是OkHttpClient也是非常的简单明了
     *
     * @param urlString
     * @return
     * @throws IOException
     */
    public static String getContent(String urlString) throws IOException {
        Ln.d("NetHelper:getContent:Request:" + urlString);
        OkHttpClient client = new OkHttpClient();
//        client.setConnectTimeout(TIME_OUT, TimeUnit.SECONDS); // connect timeout
//        client.setReadTimeout(TIME_OUT, TimeUnit.SECONDS);    // socket timeout
//        client.setWriteTimeout(TIME_OUT, TimeUnit.SECONDS);

        Request request = new Request.Builder()
                .url(urlString)
                .build();
        Response response = client.newCall(request).execute();
        String result = response.body().string();
        Ln.d("NetHelper:getContent:Response:" + result);
        return result;
    }

    /**
     * 只有这里没有使用异步，但是OkHttpClient也是非常的简单明了
     *
     * @param urlString
     * @return
     * @throws IOException
     */
    public static String getContent5Second(String urlString) throws IOException {
        Ln.d("GEtContent5Second:" + urlString);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setConnectTimeout(6000);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        int response = connection.getResponseCode();
        Ln.d("GEtContent5Second:response:" + response);
        InputStream inputStream ;

        if(response == 201 || response == 200){
            inputStream = connection.getInputStream();
        }else {
            inputStream = connection.getErrorStream();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        Ln.d("GEtContent5SecondResult:" + sb.toString());

        inputStream.close();
        br.close();
        return sb.toString();
    }

    /**
     * 只有这里没有使用异步，但是OkHttpClient也是非常的简单明了
     *
     * @param urlString
     * @return
     * @throws IOException
     */
    public static String postContent(String urlString) throws IOException {
        Ln.d("NetHelper:postContent:Request:" + urlString);

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(TIME_OUT, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(TIME_OUT, TimeUnit.SECONDS);    // socket timeout
        client.setWriteTimeout(TIME_OUT, TimeUnit.SECONDS);

        Request request = new Request.Builder()
                .url(urlString)
                .post(RequestBody.create(MediaType.parse("application/json"), ""))
                .build();
        Response response = client.newCall(request).execute();
        String result = response.body().string();
        Ln.d("NetHelper:postContent:Response:" + result);
        return result;
    }
}
