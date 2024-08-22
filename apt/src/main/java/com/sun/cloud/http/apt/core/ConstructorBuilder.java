package com.sun.cloud.http.apt.core;

import static com.sun.cloud.http.apt.core.ClassNames.ApiCenter;
import static com.sun.cloud.http.apt.core.ClassNames.HttpURL;

import com.sun.cloud.http.annotation.ApiRepository;
import com.sun.cloud.http.apt.inter.IProcessor;
import com.sun.cloud.http.apt.utils.Utils;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created on 2021/4/29
 * <p>
 *
 * @author sunxiaoyun
 */
class ConstructorBuilder extends BaseBuilder {

    public static final String KEY_DEFAULT = "default";

    private final Map<String, ApiRepository> mFieldMap;

    ConstructorBuilder(IProcessor iProcessor,
                       TypeElement element,
                       Map<String, ApiRepository> fieldMap) {
        super(iProcessor, element);
        this.mFieldMap = fieldMap;
    }

    void build(TypeSpec.Builder typeSpecBuilder) {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("default constructor \n");
        for (Map.Entry<String, ApiRepository> entry : mFieldMap.entrySet()) {
            ApiRepository annotation = entry.getValue();
            String baseUrl = annotation.value();
            com.sun.cloud.http.annotation.HttpURL httpUrl = annotation.baseUrl();
            String urlKey = annotation.urlKey();
            String serviceName = entry.getKey();
            String key = annotation.key();
            String keyParam;
            if (KEY_DEFAULT.equals(key)) {
                keyParam = ")";
            } else {
                keyParam = ", $S)";
            }
            // 构造方法中创建成员变量
            if (Utils.isNotEmpty(urlKey)) {
                Object[] args;
                if (KEY_DEFAULT.equals(key)) {
                    args = new Object[]{serviceName, ApiCenter, TypeName.get(mTypeElement.asType()), urlKey};
                } else {
                    args = new Object[]{serviceName, ApiCenter, TypeName.get(mTypeElement.asType()), urlKey, key};
                }
                constructorBuilder.addStatement("$N = $T.getInstance().getServiceByKey($T.class, $S" + keyParam, args);

            } else if (Utils.isNotEmpty(baseUrl)) {
                Object[] args;
                if (KEY_DEFAULT.equals(key)) {
                    args = new Object[]{serviceName, ApiCenter, TypeName.get(mTypeElement.asType()), baseUrl};
                } else {
                    args = new Object[]{serviceName, ApiCenter, TypeName.get(mTypeElement.asType()), baseUrl, key};
                }
                constructorBuilder.addStatement("$N = $T.getInstance().getService($T.class, $S" + keyParam, args);

            } else {
                Object[] args;
                if (KEY_DEFAULT.equals(key)) {
                    args = new Object[]{serviceName, ApiCenter, TypeName.get(mTypeElement.asType()), HttpURL, httpUrl};
                } else {
                    args = new Object[]{serviceName, ApiCenter, TypeName.get(mTypeElement.asType()), HttpURL, httpUrl, key};
                }
                constructorBuilder.addStatement("$N = $T.getInstance().getService($T.class, $T.$L" + keyParam, args);
            }
        }
        typeSpecBuilder.addMethod(constructorBuilder.build());
    }
}
