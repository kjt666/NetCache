package wowo.kjt.lib_netcache;

import android.app.Application;

/**
 * <pre>
 * author : kjt
 * e-mail : kjt333@126.com
 * time : 2021/09/03
 * desc :
 * version: 1.0
 * </pre>
 */
public class CacheConfig {

    private String baseUrl;

    private Class<?> service;

    private String cacheFolderName;

    public CacheConfig() {
    }

    public CacheConfig setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public CacheConfig setCacheFolderName(String cacheFolderName) {
        this.cacheFolderName = cacheFolderName;
        return this;
    }

    public CacheConfig setService(Class<?> clazz){
        this.service = clazz;
        return this;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Class<?> getService() {
        return service;
    }

    public String getCacheFolderName() {
        return cacheFolderName;
    }

}
