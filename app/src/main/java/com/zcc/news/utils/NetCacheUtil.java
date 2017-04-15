package com.zcc.news.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;

import com.zcc.news.activity.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017-03-06.
 */
public class NetCacheUtil {
    public static final int SUCCESS = 1;
    public static final int FAIL = 2;
    private Handler handler;
    private ExecutorService service;
    private LocalCacheUtil localCacheUtil;
    private MemoryCacheUtil memoryCacheUtil;
    public NetCacheUtil(Handler handler, LocalCacheUtil localCacheUtil, MemoryCacheUtil memoryCacheUtil) {
        this.handler = handler;
        this.memoryCacheUtil = memoryCacheUtil;
        this.localCacheUtil = localCacheUtil;
        service = Executors.newFixedThreadPool(10);
    }

    public void getBitmapFromNet(String imageUrl, int position)  {
        service.execute(new MyRunnable(imageUrl,position));

    }
    class MyRunnable implements Runnable{
        private final String imageUrl;
        private final int position;

        public MyRunnable(String imageUrl, int position){
            this.imageUrl = imageUrl;
            this.position = position;
        }
        @Override
        public void run() {

            try {
                URL url = new URL(imageUrl);
                HttpURLConnection  connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(4000);
                connection.setReadTimeout(4000);
                connection.connect();
                int code = connection.getResponseCode();
                if(code == 200){
                    InputStream is = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    Message msg = Message.obtain();
                    msg.what =SUCCESS;
                    msg.arg1 = position;
                    msg.obj =bitmap;
                    handler.sendMessage(msg);

                    //向内存中缓存一份
                    memoryCacheUtil.putBitmap(imageUrl,bitmap);
                    //向本地中缓存一份
                    localCacheUtil.putBitmap(imageUrl,bitmap);

                }
            } catch (IOException e) {
                e.printStackTrace();
                Message msg = Message.obtain();
                msg.what =FAIL;
                msg.arg1 = position;
                handler.sendMessage(msg);
            }
        }
    }
}
