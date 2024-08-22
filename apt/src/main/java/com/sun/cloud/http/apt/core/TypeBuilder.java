package com.sun.cloud.http.apt.core;

import com.sun.cloud.http.annotation.ApiRepository;
import com.sun.cloud.http.apt.inter.IProcessor;
import com.sun.cloud.http.apt.utils.Utils;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

/**
 * Created on 2021/4/29
 * <p>
 * 类构造器
 *
 * @author sunxiaoyun
 */
public class TypeBuilder extends BaseBuilder {

    private static final String SERVICE = "Service";

    final Map<String, ApiRepository> mFieldMap = new HashMap<>();

    public TypeBuilder(IProcessor iProcessor, TypeElement element) {
        super(iProcessor, element);
    }

    public void build() throws IOException {
        long startTime = System.currentTimeMillis();
        Name name = mTypeElement.getSimpleName();
        String simpleName = name.toString();
        String nameStr = name.toString();

        // 类创建器
        if (nameStr.endsWith(SERVICE) && !SERVICE.equalsIgnoreCase(nameStr)) {
            nameStr = nameStr.substring(0, nameStr.length() - SERVICE.length());
        }
        String className = nameStr + "Repository";
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addJavadoc("Do not modify !!! \nauto generate by {@link $L} \n",
                        TypeName.get(mTypeElement.asType()));

        // 遍历方法，收集变量，创建方法
        new MethodBuilder(mProcessor,
                mTypeElement,
                mFieldMap)
                .build(typeSpecBuilder);

        // 构造变量访问方法
        new FieldGetBuilder(mProcessor,
                mTypeElement,
                mFieldMap)
                .build(typeSpecBuilder);

        // 构造函数, 创建初始化变量
        new ConstructorBuilder(mProcessor,
                mTypeElement,
                mFieldMap)
                .build(typeSpecBuilder);

        // 构造静态代码块
        new StaticBlockBuilder(mProcessor,
                mTypeElement)
                .build(typeSpecBuilder);

        String packageName = Utils.getPackageName(mProcessor.getElements(), mTypeElement);
        if (packageName != null) {
            JavaFile
                    .builder(packageName, typeSpecBuilder.build())
                    .build()
                    .writeTo(mProcessor.getFiler());

            mProcessor.log(String.format(Locale.CHINA, "处理：%1$s -> %2$s, 耗时：%3$3.5f 秒",
                    simpleName, className, (System.currentTimeMillis() - startTime) / 1000.f));
        }
    }
}
