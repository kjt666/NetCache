package wowo.kjt.lib_complie;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import retrofit2.http.GET;
import retrofit2.http.POST;
import wowo.kjt.lib_annotation.NetCache;

/**
 * <pre>
 * author : kjt
 * e-mail : kjt333@126.com
 * time : 2021/03/05
 * desc :
 * version: 1.0
 * </pre>
 */
public class NetCacheMethodClass {

    private ExecutableElement mExecutableElement;

    private Messager mMessager;

    private String mMethodName;

    private String mFilterParameterName;

    private String mUrl;

    private TypeName mReturnType;


    public NetCacheMethodClass(ExecutableElement element, Messager messager) {
        mExecutableElement = element;
        mMessager = messager;
        mMethodName = element.getSimpleName().toString();
        NetCache netCache = element.getAnnotation(NetCache.class);
        mFilterParameterName = netCache.multipleCacheIdentificationParameter();
//        Class.forName(returnType.toString());
        /*if (mReturnType.getKind() != TypeKind.VOID){
            String s = mReturnType.toString();
            mMessager.printMessage(Diagnostic.Kind.OTHER,s);
            if (s.contains("HttpResponse<")) {
                String[] strings = s.split("[<>]");
                for (String split : strings) {
                    mMessager.printMessage(Diagnostic.Kind.OTHER, split);
                }
                String s1 = strings[strings.length - 1];
                String pakageName = s1.substring(0, s1.lastIndexOf("."));
                String beanName = s1.substring(s1.lastIndexOf(".") + 1);
                mReturnTypeName = ClassName.get(pakageName, beanName);
            }
        }*/
        messager.printMessage(Diagnostic.Kind.WARNING, "><><><><><><><>");
        try {
            netCache.clazz();
        } catch (MirroredTypeException e) {
            messager.printMessage(Diagnostic.Kind.WARNING, e.getTypeMirror().toString());
            mReturnType = ClassName.get(e.getTypeMirror());
        }
        messager.printMessage(Diagnostic.Kind.WARNING, mReturnType.toString());

        if (element.getAnnotation(GET.class) != null) {
            mUrl = element.getAnnotation(GET.class).value();
        }
        if (element.getAnnotation(POST.class) != null) {
            mUrl = element.getAnnotation(POST.class).value();
        }
    }

    public ExecutableElement getExecutableElement() {
        return mExecutableElement;
    }

    public String getMethodName() {
        return mMethodName;
    }

    public String getFilterParameterName() {
        return mFilterParameterName;
    }

    public String getUrl() {
        return mUrl;
    }

    public TypeName getReturnType() {
        return mReturnType;
    }

}
