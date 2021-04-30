package wowo.kjt.lib_netcache;

import android.util.Log;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import wowo.kjt.lib_netcache.util.NetCacheUtil;


/**
 * <pre>
 * author : kjt
 * e-mail : kjt333@126.com
 * time : 2021/01/06
 * desc :
 * </pre>
 */
public class AutoExpansionPolicy implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        Log.e(NetCacheUtil.NET_CACHE_TAG,"网络缓存线程池扩容1.5倍");
        executor.setCorePoolSize((int) (executor.getCorePoolSize()*1.5));
        executor.setMaximumPoolSize((int) (executor.getMaximumPoolSize()*1.5));
        executor.execute(r);
    }
}
