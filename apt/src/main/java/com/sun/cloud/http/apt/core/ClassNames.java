package com.sun.cloud.http.apt.core;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.AnnotationMirror;

/**
 * Created on 2021/4/29
 * <p>
 *
 * @author sunxiaoyun
 */
class ClassNames {
    static final ClassName Call
            = ClassName.get("retrofit2", "Call");

    static final ClassName Observable
            = ClassName.get("io.reactivex", "Observable");

    static final ClassName Flowable
            = ClassName.get("io.reactivex", "Flowable");

    static final ClassName Single
            = ClassName.get("io.reactivex", "Single");

    static final ClassName Maybe
            = ClassName.get("io.reactivex", "Maybe");

    static final ClassName LiveData
            = ClassName.get("androidx.lifecycle", "LiveData");

    static final ClassName ApiCenter
            = ClassName.get("com.sun.cloud.http", "ApiCenter");

    static final ClassName RequestUtil
            = ClassName.get("com.sun.cloud.http.utils", "RequestUtil");

    static final ClassName Resource
            = ClassName.get("com.sun.cloud.http", "Resource");

    static final ClassName HttpURL
            = ClassName.get("com.sun.cloud.http.annotation", "HttpURL");


    static boolean isApiReturnTypeClassNameInScope(ClassName className) {
        return LiveData.equals(className)
                || Call.equals(className)
                || Observable.equals(className)
                || Flowable.equals(className)
                || Single.equals(className)
                || Maybe.equals(className);
    }

    static final ClassName GET
            = ClassName.get("retrofit2.http", "GET");

    static final ClassName POST
            = ClassName.get("retrofit2.http", "POST");

    static final ClassName PUT
            = ClassName.get("retrofit2.http", "PUT");

    static final ClassName DELETE
            = ClassName.get("retrofit2.http", "DELETE");

    static final ClassName PATCH
            = ClassName.get("retrofit2.http", "PATCH");

    static boolean isApiMethodAnnotationInScope(AnnotationMirror mirror) {
        TypeName typeName = TypeName.get(mirror.getAnnotationType());
        return GET.equals(typeName)
                || POST.equals(typeName)
                || PUT.equals(typeName)
                || DELETE.equals(typeName)
                || PATCH.equals(typeName);
    }
}
