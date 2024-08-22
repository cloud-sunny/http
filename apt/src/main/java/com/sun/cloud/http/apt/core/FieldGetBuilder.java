package com.sun.cloud.http.apt.core;

import com.sun.cloud.http.annotation.ApiRepository;
import com.sun.cloud.http.apt.inter.IProcessor;
import com.sun.cloud.http.apt.utils.Utils;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Locale;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created on 2021/4/29
 * <p>
 * 变量暴露方法构造
 *
 * @author sunxiaoyun
 */
class FieldGetBuilder extends BaseBuilder {
    private final Map<String, ApiRepository> mFieldMap;

    FieldGetBuilder(IProcessor iProcessor,
                    TypeElement element,
                    Map<String, ApiRepository> mFieldMap) {
        super(iProcessor, element);
        this.mFieldMap = mFieldMap;
    }

    void build(TypeSpec.Builder typeSpecBuilder) {
        boolean hasMultiUrl = mFieldMap.size() > 1;
        for (Map.Entry<String, ApiRepository> entry : mFieldMap.entrySet()) {
            ApiRepository annotation = entry.getValue();
            String baseUrl = annotation.value();
            com.sun.cloud.http.annotation.HttpURL httpUrl = annotation.baseUrl();
            String urlKey = annotation.urlKey();
            String key = annotation.key();
            String serviceName = entry.getKey();
            String annoDoc;
            CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
            // 构造方法中创建成员变量
            if (Utils.isNotEmpty(urlKey)) {
                annoDoc = String.format(Locale.CHINA, "@ApiRepository(urlKey = \"%s\",key= \"%s\")", urlKey, key);
            } else if (Utils.isNotEmpty(baseUrl)) {
                annoDoc = String.format(Locale.CHINA, "@ApiRepository(baseUrl = \"%s\",key= \"%s\")", baseUrl, key);
            } else {
                annoDoc = String.format(Locale.CHINA, "@ApiRepository(baseUrl = HttpURL.%s,key= \"%s\")", httpUrl.name(), key);
            }

            FieldSpec fieldSpec = FieldSpec.builder(TypeName.get(mTypeElement.asType()), serviceName, Modifier.PRIVATE)
                    .addJavadoc(String.format("generate by %s \n", annoDoc))
                    .build();
            typeSpecBuilder.addField(fieldSpec);

            // service变量get方法
            String getMethodName = hasMultiUrl ?
                    (Utils.isNotEmpty(baseUrl)
                            ? String.format(Locale.CHINA, "get%1$s%2$s", mTypeElement.getSimpleName(), Utils.urlToMethodName(baseUrl))
                            : Utils.isNotEmpty(urlKey)
                            ? String.format(Locale.CHINA, "get%1$s%2$s", mTypeElement.getSimpleName(), Utils.urlToMethodName(urlKey))
                            : String.format(Locale.CHINA, "get%1$s%2$s", mTypeElement.getSimpleName(), httpUrl.name())
                    )
                    : String.format(Locale.CHINA, "get%s", mTypeElement.getSimpleName());

            String docHttpUrlName = Utils.isEmpty(baseUrl)
                    ? String.format(Locale.CHINA, "HttpURL.%s", httpUrl.name())
                    : String.format(Locale.CHINA, "%s", baseUrl);
            MethodSpec getMethodSpec =
                    MethodSpec.methodBuilder(getMethodName)
                            .addJavadoc("generate get method for \n{@link $L} by {@link $L} \n",
                                    TypeName.get(mTypeElement.asType()), docHttpUrlName)
                            .addStatement("return $N", serviceName)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.get(mTypeElement.asType()))
                            .build();
            typeSpecBuilder.addMethod(getMethodSpec);
        }
    }
}
