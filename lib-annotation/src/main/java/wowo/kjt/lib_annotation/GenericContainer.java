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
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GenericContainer {

    Class<?> container();

    String field() default "data";
}
