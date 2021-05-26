package wowo.kjt.netcachedemo;


import java.util.ArrayList;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import wowo.kjt.lib_annotation.NetCache;

/**
 * <pre>
 * author : kjt
 * e-mail : kjt333@126.com
 * time : 2021/02/02
 * desc :
 * version: 1.0
 * </pre>
 */
public interface TestApi {

    @NetCache
    @FormUrlEncoded
    @POST("mobile/user/aaa")
    String testA();

    @NetCache
    @FormUrlEncoded
    @POST("mobile/config/bbb")
    ArrayList<String> testB();

    @NetCache(clazz=TestBean.class,autoLoad = false,cachePageIndex = "3")
    @GET("mobile/list/ccc")
    HttpResponse<TestBean> testC();

    @NetCache(multipleCacheIdentificationParameter = "type")
    @GET("mobile/content/ddd")
    void testD();
}
