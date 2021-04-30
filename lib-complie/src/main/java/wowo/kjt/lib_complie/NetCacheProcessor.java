package wowo.kjt.lib_complie;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
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
@AutoService(Processor.class)
public class NetCacheProcessor extends AbstractProcessor {


    private final String mUrlClassName = "NetCacheUrlUtil";
    private final String mReturnTypeClassName = "NetCacheReturnTypeUtil";
    private final String mCacheClassName = "CacheUtil";
    private final String mJsonCacheClassName = "JsonCacheUtil";

    private Elements mElementUtils;
    private Filer mFiler;
    private Messager mMessager;
    private Types mTypeUtils;

    private List<NetCacheMethodClass> mMethodClassList = new ArrayList<>();
    private PackageElement mPackageElement;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(NetCache.class.getCanonicalName());
        return annotations;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElementUtils = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
        mTypeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(NetCache.class);
        for (Element element : elements) {
            if (mPackageElement == null) {
                mPackageElement = mElementUtils.getPackageOf(element);
                mMessager.printMessage(Diagnostic.Kind.OTHER, mPackageElement.getQualifiedName().toString());
            }
            checkValidMethod(element);
            NetCacheMethodClass methodClass = new NetCacheMethodClass((ExecutableElement) element, mMessager);
            mMethodClassList.add(methodClass);
        }
        try {
//            generateUrlCode();
//            generateReturnTypeCode();
            generateGetJsonCacheCode();
            generateGetCacheCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    private void checkValidMethod(Element element) {
        //NetCache注解只用用在方法上
        if (element.getKind() != ElementKind.METHOD) {
            throw new IllegalStateException("Only methods can be annotated with " + NetCache.class.getSimpleName());
        }
        ExecutableElement methodElement = (ExecutableElement) element;
        GET get = methodElement.getAnnotation(GET.class);
        POST post = methodElement.getAnnotation(POST.class);
        if (get == null && post == null) {
            throw new IllegalStateException(String.format("the method %s must annotated with %s or %s", methodElement.getSimpleName(), POST.class.getSimpleName(), GET.class.getSimpleName()));
        }
        if (get != null && post != null) {
            throw new IllegalStateException(String.format("the method %s only annotate one of %s or %s", methodElement.getSimpleName(), POST.class.getSimpleName(), GET.class.getSimpleName()));
        }
        if (get != null && get.value().isEmpty()) {
            throw new IllegalStateException(String.format("the get annotation of %s method must have a value", methodElement.getSimpleName()));
        }
        if (post != null && post.value().isEmpty()) {
            throw new IllegalStateException(String.format("the post annotation of %s method must have a value", methodElement.getSimpleName()));
        }
    }

    /**
     * 生成获取缓存对象的方法
     *
     * @throws IOException
     */
    private void generateGetCacheCode() throws IOException {

        //导入需要的工具类
        TypeName gson = ClassName.get("com.google.gson", "Gson");
        TypeName netCacheUtil = ClassName.get("wowo.kjt.lib_netcache.util", "NetCacheUtil");
        TypeName listener = ClassName.get("wowo.kjt.lib_netcache.listener", "NetCacheLoadListener");
        TypeName netCacheProcess = ClassName.get("wowo.kjt.lib_netcache", "NetCacheProcess");

        //全局变量Gson
        FieldSpec fieldGson = FieldSpec
                .builder(gson, "mGson", Modifier.PRIVATE, Modifier.STATIC)
                .initializer("new $T()", gson)
                .build();

        TypeSpec.Builder clazz = TypeSpec
                .classBuilder(mCacheClassName)
                .addModifiers(Modifier.PUBLIC)
                .addField(fieldGson);

        //生成截取json字符串的方法
        MethodSpec subStringMethod = MethodSpec
                .methodBuilder("subJsonString")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .returns(String.class)
                .addParameter(String.class, "json")
                .beginControlFlow("if(json != null && json.contains($S))", "\"data\":")
                .addStatement("int startIndex = json.indexOf($S) + 7", "\"data\":")
                .addStatement("int endIndex = json.lastIndexOf($S)", "}")
                .addStatement("json = json.substring(startIndex,endIndex)")
                .endControlFlow()
                .addStatement("return json")
                .build();
        clazz.addMethod(subStringMethod);

        //生成获取与接口对应的缓存的方法
        for (NetCacheMethodClass methodClass : mMethodClassList) {

            if (methodClass.getReturnType().getKind() == TypeKind.VOID) {
                break;
            }

            TypeName realReturnType = methodClass.getReturnTypeName() == null ? ClassName.get(methodClass.getReturnType()) : methodClass.getReturnTypeName();

            String filterParameter = methodClass.getFilterParameterName();
            boolean hasFilterParameter = (filterParameter != null && !filterParameter.isEmpty());

            MethodSpec.Builder method = MethodSpec
                    .methodBuilder(methodClass.getMethodName())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(realReturnType)
                    .beginControlFlow("try");

            if (hasFilterParameter) {
                method.addParameter(String.class, "filterParameter")
                        .addStatement("$T url = $T.BASE_URL + $S + filterParameter", String.class, netCacheProcess, methodClass.getUrl());
            } else {
                method.addStatement("$T url = $T.BASE_URL + $S", String.class, netCacheProcess, methodClass.getUrl());
            }

            method.addStatement("$T cache = $T.loadCache(url,new $T())", String.class, netCacheUtil, listener)
                    .addStatement("cache = $N(cache)", subStringMethod)
                    .addStatement("return ($T)mGson.fromJson(cache,$T.forName($S))", realReturnType, Class.class, realReturnType.toString())
                    .nextControlFlow("catch($T e)", Exception.class)
                    .addStatement("e.printStackTrace()")
                    .endControlFlow()
                    .addStatement("return null");
            clazz.addMethod(method.build());
        }

        JavaFile.builder(mPackageElement.getQualifiedName().toString(), clazz.build()).build().writeTo(mFiler);
    }

    /**
     * 生成获取缓存Json字符串的方法
     *
     * @throws IOException
     */
    private void generateGetJsonCacheCode() throws IOException {

        //导入需要的工具类
        TypeName netCacheUtil = ClassName.get("wowo.kjt.lib_netcache.util", "NetCacheUtil");
        TypeName listener = ClassName.get("wowo.kjt.lib_netcache.listener", "NetCacheLoadListener");
        TypeName netCacheProcess = ClassName.get("wowo.kjt.lib_netcache", "NetCacheProcess");


        TypeSpec.Builder clazz = TypeSpec
                .classBuilder(mJsonCacheClassName)
                .addModifiers(Modifier.PUBLIC);

        //生成获取与接口对应的Json缓存的方法
        for (NetCacheMethodClass methodClass : mMethodClassList) {

            if (methodClass.getReturnType().getKind() == TypeKind.VOID) {
                break;
            }

            String filterParameter = methodClass.getFilterParameterName();
            boolean hasFilterParameter = (filterParameter != null && !filterParameter.isEmpty());

            MethodSpec.Builder method = MethodSpec
                    .methodBuilder(methodClass.getMethodName())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(String.class)
                    .addStatement("$T json = null", String.class)
                    .beginControlFlow("try");
            if (hasFilterParameter) {
                method.addParameter(String.class, "filterParameter")
                        .addStatement("$T url = $T.BASE_URL + $S + filterParameter", String.class, netCacheProcess, methodClass.getUrl());
            } else {
                method.addStatement("$T url = $T.BASE_URL + $S", String.class, netCacheProcess, methodClass.getUrl());
            }
            method.addStatement("json = $T.loadCache(url,new $T())", netCacheUtil, listener)
                    .nextControlFlow("catch($T e)", Exception.class)
                    .addStatement("e.printStackTrace()")
                    .endControlFlow()
                    .addStatement("return json");
            clazz.addMethod(method.build());
        }

        JavaFile.builder(mPackageElement.getQualifiedName().toString(), clazz.build()).build().writeTo(mFiler);
    }

    private void generateUrlCode() throws IOException {
        TypeSpec.Builder clazz = TypeSpec
                .classBuilder(mUrlClassName)
                .addModifiers(Modifier.PUBLIC)
                .addField(String.class, "mBaseUrl", Modifier.PRIVATE, Modifier.STATIC);
        //生成设置域名的方法
        MethodSpec setBaseUrlMethod = MethodSpec
                .methodBuilder("setBaseUrl")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "baseUrl")
                .addStatement("mBaseUrl = baseUrl")
                .build();
        clazz.addMethod(setBaseUrlMethod);
        //生成获取url的方法
        for (NetCacheMethodClass methodClass : mMethodClassList) {
            MethodSpec.Builder method = MethodSpec
                    .methodBuilder(methodClass.getMethodName())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(String.class);
            if (!methodClass.getFilterParameterName().isEmpty()) {
                method.addParameter(String.class, "filterParameter")
                        .addStatement("return mBaseUrl + $S + filterParameter", methodClass.getUrl());
            } else {
                method.addStatement("return mBaseUrl + $S", methodClass.getUrl());
            }
            clazz.addMethod(method.build());
        }
        JavaFile.builder(mPackageElement.getQualifiedName().toString(), clazz.build()).build().writeTo(mFiler);
    }

    private void generateReturnTypeCode() throws IOException {
        TypeSpec.Builder clazz = TypeSpec
                .classBuilder(mReturnTypeClassName)
                .addModifiers(Modifier.PUBLIC);
        ClassName cla = ClassName.get(Class.class);
        //生成获取返回值类型的方法
        for (NetCacheMethodClass methodClass : mMethodClassList) {
            MethodSpec.Builder method = MethodSpec
                    .methodBuilder(methodClass.getMethodName())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(Class.class);
            if (methodClass.getReturnType().getKind() != TypeKind.VOID) {
                method.beginControlFlow("try")
                        .addStatement("return $T.forName($S)", cla, methodClass.getReturnType().toString())
                        .nextControlFlow("catch($T e)", ClassNotFoundException.class)
                        .addStatement("e.printStackTrace()")
                        .endControlFlow()
                        .addStatement("return null");
            } else {
                method.addStatement("return null");
            }
            clazz.addMethod(method.build());
        }
        JavaFile.builder(mPackageElement.getQualifiedName().toString(), clazz.build()).build().writeTo(mFiler);
    }
}
