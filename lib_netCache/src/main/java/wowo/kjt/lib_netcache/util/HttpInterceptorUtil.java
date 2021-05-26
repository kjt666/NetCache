package wowo.kjt.lib_netcache.util;

import android.text.TextUtils;
import android.util.Log;


import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import wowo.kjt.lib_netcache.NetCacheProcess;
import wowo.kjt.lib_netcache.model.*;

/**
 * <pre>
 * author : kjt
 * e-mail : kjt333@126.com
 * time : 2020/12/16
 * desc :
 * </pre>
 */
public class HttpInterceptorUtil {

    /**
     * 获取response数据
     *
     * @param response 返回的对象
     */
    public static String getResponseInfo(Response response) {
        String str = "";
        try {
            if (response == null || !response.isSuccessful()) {
                return str;
            }
            ResponseBody responseBody = response.body();
            long contentLength = responseBody.contentLength();
            BufferedSource source = responseBody.source();
            try {
                source.request(Long.MAX_VALUE); // Buffer the entire body.
            } catch (IOException e) {
                e.printStackTrace();
            }
            Buffer buffer = source.buffer();
            Charset charset = Charset.forName("utf-8");
            if (contentLength != 0) {
                str = buffer.clone().readString(charset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * @param request http请求对象
     * @return 是否可以缓存
     */
    public static NetCacheModel checkCacheEnable(Request request) {
        NetCacheModel model = null;
        try {
            String url = request.url().toString();
            ;
            if ("GET".equalsIgnoreCase(request.method())) {
                url = url.contains("?") ? url.substring(0, url.indexOf("?")) : url;
            }
            String key = MD5Util.encodeBy32BitMD5(url);
            if (NetCacheProcess.cacheModels.containsKey(key)) {
                model = getNewModel(NetCacheProcess.cacheModels.get(key));
//                //接口url为动态路径时，使用发起请求的url作为缓存url标识
//                if (model.dynamicPath) {
//                    model.cacheUrl = request.url().toString();
//                }
                //需要接口标识请求参数时，直接将需要区别的参数加到缓存url标识后面
                if (!TextUtils.isEmpty(model.identificationParameter)) {
                    String value = getRequestParameter(request, model.identificationParameter);
                    model.cacheUrl = model.cacheUrl + value;
                }
                //针对列表接口过滤不需要缓存的页码请求
                if (!model.cachePageIndex.equals("0")) {
                    String value = getRequestParameter(request, "page");
                    if (!model.cachePageIndex.equals(value)) {
                        Log.e(NetCacheUtil.NET_CACHE_TAG, model.cacheUrl + "------->非第" + model.cachePageIndex + "页数据，不缓存");
                        model = null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    public static String getRequestParameter(Request request, String parameterName) {
        String parameterValue = null;
        if ("POST".equalsIgnoreCase(request.method())) {
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int i = 0; i < body.size(); i++) {
                    if (parameterName.equals(body.encodedName(i))) {
                        parameterValue = body.encodedValue(i);
                    }
                }
            }
        }
        if ("GET".equalsIgnoreCase(request.method())) {
            HttpUrl.Builder httpBuilder = request.url().newBuilder();
            HttpUrl httpUrl = httpBuilder.build();
            parameterValue = httpUrl.queryParameter(parameterName);
        }
        return parameterValue == null ? "" : parameterValue;
    }

    private static NetCacheModel getNewModel(NetCacheModel oldModel) {
        if (oldModel == null)
            return null;
        NetCacheModel newModel = new NetCacheModel();
        newModel.cacheUrl = oldModel.cacheUrl;
        newModel.cachePageIndex = oldModel.cachePageIndex;
        newModel.identificationParameter = oldModel.identificationParameter;
//        newModel.dynamicPath = oldModel.dynamicPath;
        newModel.autoLoad = oldModel.autoLoad;
        return newModel;
    }

}
