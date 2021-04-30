package wowo.kjt.lib_netcache;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;


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
    public static HashMap<String, NetCacheModel> cacheModels = new HashMap<>();
    public static String DEFAULT_CACHE_FILE_FOLDER = "/netCache/";
    public static Application application;
    public static String BASE_URL;

    public static <T> void init(Application context, String baseUrl, final Class<T> service) {
        if (context == null || baseUrl == null || service == null) {
            throw new IllegalArgumentException("请传入必要参数");
        }
        try {
            application = context;
            cacheModels.clear();
            BASE_URL = baseUrl;
            Method[] methods = service.getMethods();
            for (Method method : methods) {
                NetCache netCache = method.getAnnotation(NetCache.class);
                if (netCache != null) {
                    POST post = method.getAnnotation(POST.class);
                    if (post != null) {
                        Log.e(NetCacheUtil.NET_CACHE_TAG, "POST：" + post.value());
                        if (!TextUtils.isEmpty(post.value())) {
                            addNetCacheModel(baseUrl + post.value(), netCache);
                        }
                    } else {
                        GET get = method.getAnnotation(GET.class);
                        if (get != null) {
                            Log.e(NetCacheUtil.NET_CACHE_TAG, "GET：" + get.value());
                            if (!TextUtils.isEmpty(get.value())) {
                                addNetCacheModel(baseUrl + get.value(), netCache);
                            }
                        }
                    }
                }
            }
            //设置默认的缓存路径
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                String path = externalCacheDir.getAbsolutePath() + DEFAULT_CACHE_FILE_FOLDER;
                File cacheFile = new File(path);
                if (!cacheFile.exists()) {
                    cacheFile.mkdir();
                }
                setCacheFolder(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setCacheFolder(String cacheFolderPath) {
        NetCacheUtil.setCacheFolderPath(cacheFolderPath);
    }

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
