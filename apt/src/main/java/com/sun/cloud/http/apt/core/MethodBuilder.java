package com.sun.cloud.http.apt.core;

import com.sun.cloud.http.annotation.ApiDataBase;
import com.sun.cloud.http.annotation.ApiRepository;
import com.sun.cloud.http.annotation.HttpURL;
import com.sun.cloud.http.apt.inter.IProcessor;
import com.sun.cloud.http.apt.utils.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import static com.sun.cloud.http.apt.core.ClassNames.LiveData;
import static com.sun.cloud.http.apt.core.ClassNames.RequestUtil;
import static com.sun.cloud.http.apt.core.ClassNames.Resource;

/**
 * Created on 2021/4/29
 * <p>
 *
 * @author sunxiaoyun
 */
class MethodBuilder extends BaseBuilder {
    private static final String HASH_NAME_FORMAT = "mService%s";

    private final Map<String, ApiRepository> mFieldMap;

    MethodBuilder(IProcessor iProcessor,
                  TypeElement element,
                  Map<String, ApiRepository> fieldMap) {
        super(iProcessor, element);
        this.mFieldMap = fieldMap;
    }

    void build(TypeSpec.Builder typeSpecBuilder) {
        String defFieldName = annoHashName(mTypeApiRepository);
        mFieldMap.put(defFieldName, mTypeApiRepository);
        for (Element e : mTypeElement.getEnclosedElements()) {
            if (e instanceof ExecutableElement) {
                ExecutableElement executableElement = (ExecutableElement) e;
                MethodSpec.Builder methodBuilder =
                        MethodSpec.methodBuilder(e.getSimpleName().toString())
                                .addJavadoc("generate method from \n{@link $T#$L}\n",
                                        TypeName.get(mTypeElement.asType()), executableElement.toString())
                                .addModifiers(Modifier.PUBLIC);
                ApiRepository apiRepository = executableElement.getAnnotation(ApiRepository.class);
                String serviceName = apiRepository == null ? defFieldName : annoHashName(apiRepository);
                if (makeMethodCore(methodBuilder, executableElement, serviceName)) {
                    // 将服务变量存到表里面
                    if (!mFieldMap.containsKey(serviceName)) {
                        mFieldMap.put(serviceName, apiRepository);
                    }
                    typeSpecBuilder.addMethod(methodBuilder.build());
                }
            }
        }
    }

    private boolean makeMethodCore(MethodSpec.Builder methodBuilder,
                                   ExecutableElement executableElement,
                                   String serviceName) {
        TypeMirror typeMirror = executableElement.getReturnType();

        ParameterizedTypeName parameterizedTypeName
                = (ParameterizedTypeName) ParameterizedTypeName.get(typeMirror);

        TypeName targetType = null;
        ClassName returnClassName = parameterizedTypeName.rawType;
        boolean isTypeInScope = ClassNames.isApiReturnTypeClassNameInScope(returnClassName);

        ParameterizedTypeName iResponseType = null;
        if (isTypeInScope) {
            List<TypeName> pt = parameterizedTypeName.typeArguments;
            if (pt != null && pt.size() >= 1) {
                iResponseType = (ParameterizedTypeName) pt.get(0);
                List<TypeName> pt2 = iResponseType.typeArguments;
                if (pt2 != null && pt2.size() >= 1) {
                    targetType = pt2.get(0);
                }
            }
        }

        if (targetType == null) {
            String errorMsg = String.format("Error -> not support for return Type:\n  %1$s %2$s ",
                    parameterizedTypeName.rawType.simpleName(), executableElement.toString());
            mProcessor.log(errorMsg);
            return false;
        }


        StringBuilder args = new StringBuilder();
        Iterator<? extends VariableElement> iterable = executableElement.getParameters().iterator();
        while (iterable.hasNext()) {
            VariableElement variableElement = iterable.next();
            methodBuilder.addParameter(TypeName.get(variableElement.asType()),
                    variableElement.getSimpleName().toString(),
                    Modifier.FINAL);
            args.append(variableElement.getSimpleName().toString());
            if (iterable.hasNext()) {
                args.append(", ");
            }
        }
        ApiDataBase apiDataBase = executableElement.getAnnotation(ApiDataBase.class);
        if (LiveData.equals(returnClassName)) {
            if (apiDataBase != null) {
                String dbLiveDataName = "dbLiveData";
                methodBuilder.addJavadoc("add db strategy by {@link $L} \n",
                        apiDataBase);
                methodBuilder.addParameter(ParameterizedTypeName.get(LiveData, targetType),
                        dbLiveDataName,
                        Modifier.FINAL);

                methodBuilder.addStatement("return $T.getResourceLiveData(\n" +
                                "        () -> $N.$N($N), \n" +
                                "        $L, \n" +
                                "        $T.$L\n" +
                                ")",
                        RequestUtil, serviceName, executableElement.getSimpleName(), args,
                        dbLiveDataName, ClassName.get(ApiDataBase.Strategy.class), apiDataBase.value().name());
            } else {
                methodBuilder.addStatement("return $T.getResourceLiveData(() -> $N.$N($N))",
                        RequestUtil, serviceName, executableElement.getSimpleName(), args);
            }
            methodBuilder.returns(ParameterizedTypeName.get(returnClassName, ParameterizedTypeName.get(Resource, targetType)));
        } else {
            if (apiDataBase != null) {
                String errorMsg = String.format("Error -> @ApiDataBase only support return type is LiveData,\n  %1$s#%2$s",
                        mTypeElement.getSimpleName(), executableElement.toString());
                mProcessor.log(errorMsg);
            }
            methodBuilder.addStatement("return $N.$N($N)",
                    serviceName, executableElement.getSimpleName(), args);
            methodBuilder.returns(ParameterizedTypeName.get(returnClassName, ParameterizedTypeName.get(iResponseType.rawType, targetType)));
        }
        return true;
    }

    private static String annoHashName(@Nonnull ApiRepository annotation) {
        String baseUrl = annotation.value();
        String urlKey = annotation.urlKey();
        HttpURL httpUrl = annotation.baseUrl();

        if (Utils.isNotEmpty(baseUrl)) {
            String hashName = String.valueOf(baseUrl.hashCode()).replace("-", "_");
            return String.format(Locale.CHINA, HASH_NAME_FORMAT, hashName);
        } else if (Utils.isNotEmpty(urlKey)) {
            String hashName = String.valueOf(urlKey.hashCode()).replace("-", "_");
            return String.format(Locale.CHINA, HASH_NAME_FORMAT, hashName);
        } else {
            String hashName = String.valueOf(httpUrl.name().hashCode()).replace("-", "_");
            return String.format(Locale.CHINA, HASH_NAME_FORMAT, hashName);
        }
    }

}