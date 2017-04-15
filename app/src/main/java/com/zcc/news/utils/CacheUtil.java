package com.zcc.news.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import org.xutils.common.util.MD5;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;


/**
 * 缓存参数和数据
 * Created by 朱超超 on 2017-01-25.
 */
public class CacheUtil {
    /**
     * 得到缓存值
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("zcc", context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    /**
     * 保存软件参数
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences("zcc", Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 缓存文本数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putString(Context context, String key, String value) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                String fileName = MD5.md5(key);
                File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews/cacheString", fileName);
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdir();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(value.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SharedPreferences sp = context.getSharedPreferences("zcc", Context.MODE_PRIVATE);
            sp.edit().putString(key, value).commit();
        }
    }

    /**
     * 得到缓存的文本数据
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        String result = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                String fileName = MD5.md5(key);
                File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews/cacheString", fileName);
                FileInputStream fis = new FileInputStream(file);
                byte[] b = new byte[1024];
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int length;
                while ((length = fis.read(b)) != -1) {
                    byteArrayOutputStream.write(b, 0, length);
                }
                fis.close();
                byteArrayOutputStream.close();
                result = byteArrayOutputStream.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SharedPreferences sp = context.getSharedPreferences("zcc", context.MODE_PRIVATE);
            result =  sp.getString(key, "");
        }
        return result;
    }
}
