package com.sun.cloud.http.apt.core;

import com.sun.cloud.http.annotation.ApiCacheTime;
import com.sun.cloud.http.annotation.ApiRepository;
import com.sun.cloud.http.annotation.HttpURL;
import com.sun.cloud.http.apt.inter.IProcessor;
import com.sun.cloud.http.apt.utils.Utils;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeSpec;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import static com.sun.cloud.http.apt.core.ClassNames.RequestUtil;

/**
 * Created on 2021/4/30
 * <p>
 * 静态代码块构造器
 * 主要负责缓存配置代码添加
 *
 * @author sunxiaoyun
 */
class StaticBlockBuilder extends BaseBuilder {
    StaticBlockBuilder(IProcessor iProcessor, TypeElement element) {
        super(iProcessor, element);
    }

    void build(TypeSpec.Builder typeSpecBuilder) {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        for (Element e : mTypeElement.getEnclosedElements()) {
            if (e instanceof ExecutableElement) {
                ExecutableElement executableElement = (ExecutableElement) e;
                CodeBlock block = createCodeBlockByApiCacheTime(executableElement);
                if (block != null) {
                    codeBlockBuilder.add(block);
                }
            }
        }
        if (!codeBlockBuilder.isEmpty()) {
            typeSpecBuilder.addStaticBlock(codeBlockBuilder.build());
        }
    }

    private CodeBlock createCodeBlockByApiCacheTime(ExecutableElement executableElement) {
        ApiCacheTime apiCacheTime = executableElement.getAnnotation(ApiCacheTime.class);
        if (apiCacheTime == null) {
            return null;
        }

        String path = getRetrofitAnnValuePath(executableElement);
        if (Utils.isEmpty(path)) {
            return null;
        }
        ApiRepository apiRepository = executableElement.getAnnotation(ApiRepository.class);
        ApiRepository annotation = apiRepository != null ? apiRepository : mTypeApiRepository;
        String baseUrl = annotation.value();
        HttpURL httpUrl = annotation.baseUrl();
        String urlKey = annotation.urlKey();
        int cacheTimeInSec = apiCacheTime.value();
        CodeBlock.Builder addCacheBuilder = CodeBlock.builder();
        if (Utils.isNotEmpty(urlKey)) {
            addCacheBuilder.addStatement(
                    "$T.addCachePathByUrlKey($S, $S, $L)",
                    RequestUtil, urlKey, path, cacheTimeInSec);

        } else if (Utils.isNotEmpty(baseUrl)) {
            addCacheBuilder.addStatement(
                    "$T.addCachePath($S, $S, $L)",
                    RequestUtil, baseUrl, path, cacheTimeInSec);

        } else {
            addCacheBuilder.addStatement(
                    "$T.addCachePath($T.$L, $S, $L)",
                    RequestUtil, ClassNames.HttpURL, httpUrl, path, cacheTimeInSec);
        }
        return addCacheBuilder.build();
    }


    /**
     * 遍历注解，找到GET，POST，PUT，PATCH，DELETE等注解value值
     */
    private static String getRetrofitAnnValuePath(ExecutableElement executableElement) {
        List<? extends AnnotationMirror> annotationMirrors = executableElement.getAnnotationMirrors();
        for (AnnotationMirror mirror : annotationMirrors) {
            if (ClassNames.isApiMethodAnnotationInScope(mirror)) {
                Map<? extends ExecutableElement, ? extends AnnotationValue> map =
                        mirror.getElementValues();
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : map.entrySet()) {
                    AnnotationValue annValue = entry.getValue();
                    return (String) annValue.getValue();
                }
            }

        }
        return null;
    }
}
