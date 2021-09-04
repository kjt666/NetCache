package wowo.kjt.netcachedemo;


import java.util.ArrayList;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import wowo.kjt.lib_annotation.GenericContainer;
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
@GenericContainer(container = HttpResponse.class)
public interface TestApi {

    @NetCache()
    @FormUrlEncoded
    @POST("mobile/user/aaa")
    HttpResponse<Object> testA();

    @NetCache(clazz = TestBean.class, autoLoad = false, cachePageIndex = "3")
    @GET("mobile/list/bbb")
    HttpResponse<TestBean> testB();

    @NetCache(clazz = TestBean2.class, multipleCacheIdentificationParameter = "type")
    @GET("mobile/content/ccc")
    HttpResponse<TestBean2> testC();
}
