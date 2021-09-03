package wowo.kjt.lib_netcache;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;


import androidx.annotation.NonNull;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

import retrofit2.http.GET;
import retrofit2.http.POST;
import wowo.kjt.lib_annotation.NetCache;
import wowo.kjt.lib_netcache.model.NetCacheModel;
import wowo.kjt.lib_netcache.util.MD5Util;
import wowo.kjt.lib_netcache.util.NetCacheUtil;

/**
 * <pre>
 * author : kjt
 * e-mail : kjt333@126.com
 * time : 2020/12/14
 * desc :
 * </pre>
 */
public class NetCacheProcess {

    /**
     * 已Http请求url的md5值为key
     */
    public static Application application;
    public static HashMap<String, NetCacheModel> cacheModels = new HashMap<>();
    private static String DEFAULT_CACHE_FILE_FOLDER = "/netCache/";
    public static String cacheFolderPath;
    public static String BASE_URL;


    public static void init(@NonNull Application context, @NonNull CacheConfig config) {

        if (TextUtils.isEmpty(config.getBaseUrl())) {
            throw new IllegalArgumentException("baseUrl is can't be null or empty");
        }
        if (config.getService() == null) {
            throw new IllegalArgumentException("service is can't be null");
        }
        if (!config.getService().isInterface()) {
            throw new IllegalArgumentException("service can only be interfaces");
        }

        try {
            application = context;
            cacheModels.clear();
            BASE_URL = config.getBaseUrl();
            Method[] methods = config.getService().getMethods();
            for (Method method : methods) {
                NetCache netCache = method.getAnnotation(NetCache.class);
                if (netCache != null) {
                    POST post = method.getAnnotation(POST.class);
                    if (post != null) {
                        Log.e(NetCacheUtil.NET_CACHE_TAG, "POST：" + post.value());
                        if (!TextUtils.isEmpty(post.value())) {
                            addNetCacheModel(config.getBaseUrl() + post.value(), netCache);
                        }
                    } else {
                        GET get = method.getAnnotation(GET.class);
                        if (get != null) {
                            Log.e(NetCacheUtil.NET_CACHE_TAG, "GET：" + get.value());
                            if (!TextUtils.isEmpty(get.value())) {
                                addNetCacheModel(config.getBaseUrl() + get.value(), netCache);
                            }
                        }
                    }
                }
            }

            //设置默认的缓存路径
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                cacheFolderPath = externalCacheDir.getAbsolutePath() + (TextUtils.isEmpty(config.getCacheFolderName()) ? DEFAULT_CACHE_FILE_FOLDER : config.getCacheFolderName());
                File cacheFile = new File(cacheFolderPath);
                if (!cacheFile.exists()) {
                    cacheFile.mkdir();
                }
//                setCacheFolder(cacheFolderPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public static void setCacheFolder(String cacheFolderPath) {
        NetCacheUtil.setCacheFolderPath(cacheFolderPath);
    }*/

    private static void addNetCacheModel(String key, NetCache netCache) {
        NetCacheModel model = new NetCacheModel();
        model.cacheUrl = key;
        model.cachePageIndex = netCache.cachePageIndex();
        model.identificationParameter = netCache.multipleCacheIdentificationParameter();
//        model.dynamicPath = netCache.dynamicPath();
        model.autoLoad = netCache.autoLoad();

        cacheModels.put(MD5Util.encodeBy32BitMD5(key), model);
    }
}
