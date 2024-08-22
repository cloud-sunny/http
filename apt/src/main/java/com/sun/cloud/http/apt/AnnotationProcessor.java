package com.sun.cloud.http.apt;

import com.google.auto.service.AutoService;
import com.sun.cloud.http.annotation.ApiRepository;
import com.sun.cloud.http.apt.core.TypeBuilder;
import com.sun.cloud.http.apt.inter.IProcessor;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created on 2019/10/31
 *
 * @author sunxiaoyun
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
        "com.sun.cloud.http.annotation.ApiRepository"
})
public class AnnotationProcessor extends AbstractProcessor implements IProcessor {
    /**
     * 文件相关的辅助类
     */
    public Filer mFiler;

    /**
     * 元素相关的辅助类
     */
    public Elements mElements;

    /**
     * 日志相关的辅助类
     */
    public Messager mMessager;

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();

        try {
            for (TypeElement element
                    : ElementFilter.typesIn(roundEnvironment.getElementsAnnotatedWith(ApiRepository.class))) {
                new TypeBuilder(this, element).build();
            }
        } catch (FilerException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
        return true;
    }

    @Override
    public void log(String message) {
        mMessager.printMessage(Diagnostic.Kind.NOTE,
                String.format("ApiRepository APT : %s", message));
    }

    @Override
    public Elements getElements() {
        return mElements;
    }

    @Override
    public Filer getFiler() {
        return mFiler;
    }
}
