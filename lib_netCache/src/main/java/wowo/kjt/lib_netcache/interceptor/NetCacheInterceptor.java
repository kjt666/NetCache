package wowo.kjt.lib_netcache.interceptor;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import wowo.kjt.lib_netcache.NetCacheProcess;
import wowo.kjt.lib_netcache.listener.NetCacheLoadListener;
import wowo.kjt.lib_netcache.listener.NewCacheSaveListener;
import wowo.kjt.lib_netcache.util.*;
import wowo.kjt.lib_netcache.model.*;


/**
 * <pre>
 * author : kjt
 * e-mail : kjt333@126.com
 * time : 2020/12/25
 * desc :
 * </pre>
 */
public class NetCacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        NetCacheModel cacheModel = HttpInterceptorUtil.checkCacheEnable(request);
        //无网络时并且此接口可缓存时尝试获取缓存返回
        if (!NetworkUtil.isNetworkAvailable(NetCacheProcess.application) && cacheModel != null && cacheModel.autoLoad) {
            String cache = NetCacheUtil.loadCache(cacheModel.cacheUrl, new NetCacheLoadListener());
            //缓存不为空时创建response返回
            if (!TextUtils.isEmpty(cache)) {
                Response.Builder builder = new Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .message("use net cache")
                        .code(200)
                        .body(ResponseBody.create(MediaType.parse("application/json"), cache));
                response = builder.build();
                return response;
            }
        }
        response = chain.proceed(request);
        //此接口可缓存并且请求成功时尝试将数据缓存本地
        if (cacheModel != null && response != null && response.code() == 200) {
            String key = cacheModel.cacheUrl;
            String responseInfo = HttpInterceptorUtil.getResponseInfo(response);
            if (TextUtils.isEmpty(responseInfo)) {
                Log.e(NetCacheUtil.NET_CACHE_TAG, "缓存失败，responseInfo为null");
            } else {
                NetCacheUtil.saveCacheAsync(key, responseInfo, new NewCacheSaveListener());
            }
        }
        return response;
    }
}
