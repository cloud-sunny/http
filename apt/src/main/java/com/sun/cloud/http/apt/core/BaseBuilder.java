package com.sun.cloud.http.apt.core;

import com.sun.cloud.http.annotation.ApiRepository;
import com.sun.cloud.http.apt.inter.IProcessor;

import javax.lang.model.element.TypeElement;

/**
 * Created on 2021/4/29
 * <p>
 *
 * @author sunxiaoyun
 */
class BaseBuilder {

    protected IProcessor mProcessor;
    protected TypeElement mTypeElement;
    protected final ApiRepository mTypeApiRepository;

    BaseBuilder(IProcessor mProcessor, TypeElement typeElement) {
        this.mProcessor = mProcessor;
        this.mTypeElement = typeElement;
        this.mTypeApiRepository = mTypeElement.getAnnotation(ApiRepository.class);
    }
}
