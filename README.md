# NetCache

### 一个搭配Retrofit使用的网络缓存库

#### 博客：https://blog.csdn.net/Ever69/article/details/113037161?spm=1001.2014.3001.5501


#### 实现功能

* 在没有网络的情况下返回缓存数据。
* 自动生成获取缓存Json、缓存对象的工具类。

*当前并没有对缓存做时长限制，后续版本可能会加。

#### 使用说明

在Application中

```java
CacheConfig config = new CacheConfig()
                .setBaseUrl("http://test.app.com/")
                .setService(TestApi.class);
        NetCacheProcess.init(this,config);
```

将NetCacheInterceptor设置给Okhttp

```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new NetCacheInterceptor())
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .connectTimeout(30000, TimeUnit.MILLISECONDS)
                .writeTimeout(30000, TimeUnit.MILLISECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        api = retrofit.create(TestApi.class);
```

在接口的类上添加GenericContainer注解，这个注解全局只需添加一次。
在需要进行缓存的api上添加NetCache注解，不需要缓存的接口不需要添加。
```java
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
```

如果在某些情况下，你需要手动拿到缓存，进行一些处理，那么你只需在开发的时候，编译一次项目，编译后，自动会对需要缓存的api产生对应的CacheUtil和JsonCacheUtil

* CacheUtil：可直接获取缓存对象的工具类

* JsonCacheUtil：获取缓存Json的工具类

##### CacheUtil

```java
public class CacheUtil {
  private static Gson mGson = new Gson();

  public static Object testA() {
    try {
      String url = NetCacheProcess.sCacheConfig.getBaseUrl() + "mobile/user/aaa";
      String cache = NetCacheUtil.loadCache(url,new NetCacheLoadListener());
      HttpResponse<Object> response = mGson.fromJson(cache,new TypeToken<HttpResponse<Object>>(){}.getType());
      return response.data;
    } catch(Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static TestBean testB() {
    try {
      String url = NetCacheProcess.sCacheConfig.getBaseUrl() + "mobile/list/bbb";
      String cache = NetCacheUtil.loadCache(url,new NetCacheLoadListener());
      HttpResponse<TestBean> response = mGson.fromJson(cache,new TypeToken<HttpResponse<TestBean>>(){}.getType());
      return response.data;
    } catch(Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static TestBean2 testC(String filterParameter) {
    try {
      String url = NetCacheProcess.sCacheConfig.getBaseUrl() + "mobile/content/ccc" + filterParameter;
      String cache = NetCacheUtil.loadCache(url,new NetCacheLoadListener());
      HttpResponse<TestBean2> response = mGson.fromJson(cache,new TypeToken<HttpResponse<TestBean2>>(){}.getType());
      return response.data;
    } catch(Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}

```

##### JsonCacheUtil

```java
public class JsonCacheUtil {
  public static String testA() {
    String json = null;
    try {
      String url = NetCacheProcess.sCacheConfig.getBaseUrl() + "mobile/user/aaa";
      json = NetCacheUtil.loadCache(url,new NetCacheLoadListener());
    } catch(Exception e) {
      e.printStackTrace();
    }
    return json;
  }

  public static String testB() {
    String json = null;
    try {
      String url = NetCacheProcess.sCacheConfig.getBaseUrl() + "mobile/list/bbb";
      json = NetCacheUtil.loadCache(url,new NetCacheLoadListener());
    } catch(Exception e) {
      e.printStackTrace();
    }
    return json;
  }

  public static String testC(String filterParameter) {
    String json = null;
    try {
      String url = NetCacheProcess.sCacheConfig.getBaseUrl() + "mobile/content/ccc" + filterParameter;
      json = NetCacheUtil.loadCache(url,new NetCacheLoadListener());
    } catch(Exception e) {
      e.printStackTrace();
    }
    return json;
  }
}
```
### 注解详解


* @GenericContainer

|  属性   |   类型    |   说明   |
|  ----  | ----  |  ----  |
| container  | Class | 泛型的容器类 |
| field  | String |泛型数据对应Json中的字段 |
      
一般，后台返回的接口数据会按固定格式输出，比如，

```
{
"code":200,
"message":"seccuess",
"data":{
    /.../
  }
}
```
以上面的数据为例，我们会为不同接口‘data’中的数据建立对应的实体类， 并将其通过泛型的方式放在一个基类中，声明在接口方法的返回值上，container对应的就是这个基类，field对应的就是json中的‘data’字段。
如果没有基类包裹，则不需要设置此注解
      
* @NetCache

|  属性   |   类型    |   说明   |
|  ----  | ----  |  ----  |
| cachePageIndex  | String | 需要缓存第几页的数据（只针对列表接口） |
| multipleCacheIdentificationParameter  | String | 同一接口不同类型数据的缓存标识(比如新闻信息类接口，同一接口，但是新闻类型参数不同)|
| autoLoad | bool |  无网的情况下，是否自动返回缓存（某些情况，我们需要用到该接口缓存，但是又不希望没网的时候，自动加载该接口缓存） |
| clazz | Class|  要缓存的类  |
      
      
      
