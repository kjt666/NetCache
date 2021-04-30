package wowo.kjt.lib_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * author : kjt
 * e-mail : kjt333@126.com
 * time : 2020/12/14
 * desc :
 * </pre>
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NetCache {
    //接口为请求列表时标记需缓存的页码，默认为0时不作处理
    String cachePageIndex() default "0";
    //对指定字段的不同参数都做缓存，默认为空字符串时不作处理
    String multipleCacheIdentificationParameter() default "";
//    boolean dynamicPath() default false;
    //没有网络的情况下自动使用缓存
    boolean autoLoad() default true;

    Class<?> clazz() default Object.class;
}
