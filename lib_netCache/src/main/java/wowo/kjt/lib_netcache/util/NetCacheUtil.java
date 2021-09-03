package wowo.kjt.lib_netcache.util;

import android.text.TextUtils;
import android.util.Log;


import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import wowo.kjt.lib_netcache.AutoExpansionPolicy;
import wowo.kjt.lib_netcache.NetCacheProcess;
import wowo.kjt.lib_netcache.listener.INetCacheIoListener;


/**
 * <pre
 * author : kjt
 * e-mail : kjt333@126.com
 * time : 2020/12/14
 * desc :
 * </pre>
 */
public class NetCacheUtil {

    public static final String NET_CACHE_TAG = "net_cache";
    private static final ThreadPoolExecutor mThreadPoolExecutor = new ThreadPoolExecutor(
            15, 30, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1), new AutoExpansionPolicy());
//    public static String mCacheFolderPath;

    /**
     * 异步保存网络缓存
     *
     * @param key
     * @param json
     * @param listener
     */
    public static void saveCacheAsync(String key, String json, INetCacheIoListener listener) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(json)) {
            return;
        }
        mThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String md5key = MD5Util.encodeBy32BitMD5(key);
                Log.e(NetCacheUtil.NET_CACHE_TAG, key + " + md5 = " + md5key);
                NetCacheFileUtil.writeJson2File(NetCacheProcess.cacheFolderPath + md5key, json, false, listener);
            }
        });
    }

    /**
     * 异步读取网络缓存
     *
     * @param key      url
     * @param listener 读取缓存的回调
     */
    public static void loadCacheAsync(String key, INetCacheIoListener listener) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String md5key = MD5Util.encodeBy32BitMD5(key);
                Log.e(NetCacheUtil.NET_CACHE_TAG, key + " + md5 = " + md5key);
                NetCacheFileUtil.readJson2File(NetCacheProcess.cacheFolderPath + md5key, listener);
            }
        });
    }

    /**
     * 同步读取网络缓存
     *
     * @param key url
     */
    public static String loadCache(String key, INetCacheIoListener listener) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        String md5key = MD5Util.encodeBy32BitMD5(key);
        Log.e(NetCacheUtil.NET_CACHE_TAG, key + " + md5 = " + md5key);
        return NetCacheFileUtil.readJson2File(NetCacheProcess.cacheFolderPath + md5key, listener);
    }

    /*public static void setCacheFolderPath(String path) {
        if (TextUtils.isEmpty(path)) {
            Log.e(NetCacheUtil.NET_CACHE_TAG, "netCache folderPath is null");
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            Log.e(NetCacheUtil.NET_CACHE_TAG, path + "文件夹不存在");
            return;
        }
        if (!file.isDirectory()) {
            Log.e(NetCacheUtil.NET_CACHE_TAG, path + "不是文件夹");
            return;
        }
        mCacheFolderPath = path;
    }*/

}
