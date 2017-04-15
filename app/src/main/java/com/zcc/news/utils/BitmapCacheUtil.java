package com.zcc.news.utils;

import android.graphics.Bitmap;
import android.os.Handler;

import org.xutils.common.util.LogUtil;

/**
 * 图片三级缓存工具类
 * Created by Administrator on 2017-03-06.
 */
public class BitmapCacheUtil {
    private String imageUrl;
    /**
     * 网络缓存工具类
     */
    private NetCacheUtil netCacheUtil;
    private LocalCacheUtil localCacheUtil;
    private MemoryCacheUtil memoryCacheUtil;
    public BitmapCacheUtil(Handler handler) {
        this.memoryCacheUtil = new MemoryCacheUtil();
        this.localCacheUtil = new LocalCacheUtil();
        this.netCacheUtil = new NetCacheUtil(handler,localCacheUtil,memoryCacheUtil);
    }

    /**
     * 图片的三级缓存
     * @param imageUrl
     * @param position
     */
    public Bitmap getBitmap(String imageUrl, int position) {
        this.imageUrl = imageUrl;
        //从内存中获取
        Bitmap bitmapFromMemory = memoryCacheUtil.getBitmap(imageUrl);
        if(bitmapFromMemory != null){
            LogUtil.e("内存加载图片成功");
            return  bitmapFromMemory;
        }
        //内存中没有，从本地获取，并在内存中缓存一张
        Bitmap bitmapFromLocal = localCacheUtil.getBitmapFromLocal(imageUrl);
        if(bitmapFromLocal != null){
            LogUtil.e("本地加载图片成功");
            return  bitmapFromLocal;
        }
        //本地和内存都没有，从网络获取，并在本地和内存中获取
        netCacheUtil.getBitmapFromNet(imageUrl,position);
        return null;
    }
}
