package com.zcc.news.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import org.xutils.common.util.LogUtil;
import org.xutils.common.util.MD5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017-03-07.
 */
public class LocalCacheUtil {
    public Bitmap getBitmapFromLocal(String imageUrl) {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            try {
                String fileName = MD5.md5(imageUrl);
                File file = new File(Environment.getExternalStorageDirectory()+"/beijingnews",fileName);

                if (file.exists()){
                    FileInputStream fis = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    return  bitmap;
                }

            } catch (IOException e) {
                LogUtil.e("本地加载图片失败");
                e.printStackTrace();
            }
        }
        return null;
    }

    public void putBitmap(String imageUrl, Bitmap bitmap) {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
           try {
               String fileName = MD5.md5(imageUrl);
               File file = new File(Environment.getExternalStorageDirectory()+"/beijingnews",fileName);
               File parentFile = file.getParentFile();
               if (!parentFile.exists()){
                   parentFile.mkdir();
               }
               if (!file.exists()){
                   file.createNewFile();
               }
               bitmap.compress(Bitmap.CompressFormat.PNG,100, new FileOutputStream(file));
               LogUtil.e("本地缓存图片成功");
           } catch (IOException e) {
               LogUtil.e("本地缓存图片失败");
               e.printStackTrace();
           }
        }

    }
}
