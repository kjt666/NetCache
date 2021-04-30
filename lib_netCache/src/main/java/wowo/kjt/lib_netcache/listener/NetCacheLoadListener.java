package wowo.kjt.lib_netcache.listener;

import android.util.Log;

import wowo.kjt.lib_netcache.util.NetCacheUtil;


/**
 * <pre>
 * author : kjt
 * e-mail : kjt333@126.com
 * time : 2020/12/16
 * desc :
 * </pre>
 */
public class NetCacheLoadListener implements INetCacheIoListener {


    @Override
    public void onProcessing(String fileName, float progress) {
//        Log.e(NetCacheUtil.NET_CACHE_TAG, fileName + "-------> 读取缓存" + progress + "%");
    }

    @Override
    public void onInterrupted(String fileName) {
        Log.e(NetCacheUtil.NET_CACHE_TAG, fileName + "-------> 读取缓存中断");
    }

    @Override
    public void onFail(String fileName, Exception e) {
        Log.e(NetCacheUtil.NET_CACHE_TAG, fileName + "-------> 读取缓存失败\n" + e.getMessage());
    }

    @Override
    public void onComplete(String fileName, String result) {
        Log.e(NetCacheUtil.NET_CACHE_TAG, fileName + "-------> 读取缓存成功");
    }
}
