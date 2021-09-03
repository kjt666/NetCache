package wowo.kjt.netcachedemo;

import android.app.Application;

import wowo.kjt.lib_netcache.CacheConfig;
import wowo.kjt.lib_netcache.NetCacheProcess;


/**
 * <pre>
 * author : kjt
 * e-mail : kjt333@126.com
 * time : 2021/02/02
 * desc :
 * version: 1.0
 * </pre>
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        NetCacheProcess.init(this,"http://test.app.com/",TestApi.class);
        CacheConfig config = new CacheConfig()
                .setBaseUrl("http://test.app.com/")
                .setClazz(TestApi.class)
                .setDataHierarchy(2);
        NetCacheProcess.init(this,config);
    }
}
