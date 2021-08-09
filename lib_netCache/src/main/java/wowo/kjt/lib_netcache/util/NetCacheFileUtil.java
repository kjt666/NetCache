package wowo.kjt.lib_netcache.util;

import android.text.TextUtils;
import android.util.Log;


import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.concurrent.ConcurrentHashMap;

import wowo.kjt.lib_netcache.listener.INetCacheIoListener;


/**
 * <pre>
 * author : kjt
 * e-mail : kjt333@126.com
 * time : 2020/12/14
 * desc :
 * </pre>
 */
class NetCacheFileUtil {

    private static ConcurrentHashMap<String, File> mFiles = new ConcurrentHashMap<>();

    public static boolean writeJson2File(String filePath, String json, boolean append, @NonNull INetCacheIoListener listener) {
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(json)) {
            return false;
        }
        File file = getWritableFile(filePath);
        if (file == null) {
            return false;
        }
        synchronized (file) {
            long startTimeMills = System.currentTimeMillis();
            FileOutputStream fos = null;
            OutputStreamWriter osw = null;
            BufferedWriter writer = null;
            try {
                fos = new FileOutputStream(file, append);
                osw = new OutputStreamWriter(fos, "utf-8");
                writer = new BufferedWriter(osw);
                json = DeflaterUtils.zipString(json);
                writer.write(json);
                writer.flush();
                Log.e(NetCacheUtil.NET_CACHE_TAG, filePath + " ========> 缓存本地用时" + (System.currentTimeMillis() - startTimeMills) + "毫秒");
                listener.onComplete(filePath, "");
            } catch (Exception e) {
                listener.onFail(filePath, e);
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                    if (osw != null) {
                        osw.close();
                    }
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static String readJson2File(@NonNull String filePath, @NonNull INetCacheIoListener listener) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = getReadableFile(filePath);
        if (file == null) {
            return null;
        }
        synchronized (file) {
            StringBuffer sb = new StringBuffer();
            String tempStr;
            FileInputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader reader = null;
            FileChannel fileChannel;
            try {
                long startTimeMills = System.currentTimeMillis();
                fis = new FileInputStream(file);
                fileChannel = fis.getChannel();
                isr = new InputStreamReader(fis, "utf-8");
                reader = new BufferedReader(isr);
                while ((tempStr = reader.readLine()) != null) {
                    sb.append(tempStr);
                    listener.onProcessing(filePath, (sb.toString().length() * 100f / fileChannel.size()));
                }
                String json = null;
                if (tempStr != null) {
                    listener.onInterrupted(filePath);
                } else {
                    listener.onProcessing(filePath, 100f);
                    json = DeflaterUtils.unzipString(sb.toString());
                    listener.onComplete(filePath, json);
                }
                Log.e(NetCacheUtil.NET_CACHE_TAG, filePath + " ========> 读取用时" + (System.currentTimeMillis() - startTimeMills) + "毫秒");
                return json;
            } catch (Exception e) {
                listener.onFail(filePath, e);
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                    if (isr != null) {
                        isr.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static File getWritableFile(@NonNull String filePath) {
        String key = MD5Util.encodeBy32BitMD5(filePath);
        File file = null;
        if (mFiles.containsKey(key)) {
            file = mFiles.get(key);
        } else {
            file = new File(filePath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    mFiles.put(key, file);
                } catch (IOException e) {
                    Log.e(NetCacheUtil.NET_CACHE_TAG, filePath + "创建失败");
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    private static File getReadableFile(@NonNull String filePath) {
        String key = MD5Util.encodeBy32BitMD5(filePath);
        File file = null;
        if (mFiles.containsKey(key)) {
            file = mFiles.get(key);
        } else {
            file = new File(filePath);
            if (!file.exists()) {
                Log.e(NetCacheUtil.NET_CACHE_TAG, "文件不存在：" + filePath);
            }
        }
        return file;
    }
}
