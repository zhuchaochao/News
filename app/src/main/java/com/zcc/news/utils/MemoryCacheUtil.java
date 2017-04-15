package com.zcc.news.utils;

import android.graphics.Bitmap;

import org.xutils.cache.LruCache;
import org.xutils.common.util.LogUtil;

/**
 * Created by Administrator on 2017-03-07.
 */
public class MemoryCacheUtil {
    private LruCache<String,Bitmap> lruCache;
    public MemoryCacheUtil(){
        int maxSize = (int) (Runtime.getRuntime().maxMemory()/1024/8);
        lruCache = new LruCache<String,Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return (value.getRowBytes()*value.getHeight())/1024;
            }
        };
    }
    public void putBitmap(String imageUrl, Bitmap bitmap) {
        LogUtil.e("内存缓存图片成功");
        lruCache.put(imageUrl,bitmap);
    }

    public Bitmap getBitmap(String imageUrl) {
        return lruCache.get(imageUrl);
    }
}
