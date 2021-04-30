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
public class NewCacheSaveListener implements INetCacheIoListener {

    @Override
    public void onProcessing(String fileName, float progress) {

    }

    @Override
    public void onInterrupted(String fileName) {

    }

    @Override
    public void onFail(String fileName, Exception e) {
        Log.e(NetCacheUtil.NET_CACHE_TAG, fileName + "-------> 缓存失败：" + e.getMessage());
    }

    @Override
    public void onComplete(String fileName, String result) {
        Log.e(NetCacheUtil.NET_CACHE_TAG, fileName + "-------> 缓存成功");
    }
}
