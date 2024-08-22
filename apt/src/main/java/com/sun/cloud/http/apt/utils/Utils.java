package com.sun.cloud.http.apt.utils;

import com.squareup.javapoet.ClassName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Messager;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;

public class Utils {
    public static final String ANNOTATION = "@";

    public static boolean isPublic(TypeElement element) {
        return element.getModifiers().contains(PUBLIC);
    }

    public static boolean isAbstract(TypeElement element) {
        return element.getModifiers().contains(ABSTRACT);
    }

    public static boolean isValidClass(Messager messager, TypeElement element) {
        if (element.getKind() != ElementKind.CLASS) {
            return false;
        }

        if (!isPublic(element)) {
            String message = String.format("Classes annotated with %s must be public.", ANNOTATION);
            messager.printMessage(Diagnostic.Kind.ERROR, message, element);
            return false;
        }

        if (isAbstract(element)) {
            String message = String.format("Classes annotated with %s must not be abstract.", ANNOTATION);
            messager.printMessage(Diagnostic.Kind.ERROR, message, element);
            return false;
        }

        return true;
    }

    public static String getPackageName(Elements elements, TypeElement typeElement) {
        PackageElement pkg = elements.getPackageOf(typeElement);
        if (pkg.isUnnamed()) {
            return null;
        }
        return pkg.getQualifiedName().toString();
    }


    public static String getClassName(TypeElement typeElement) throws ClassNotFoundException {
        return ClassName.get(typeElement).simpleName();
    }

    public static ClassName getType(String className) {
        return ClassName.get(className.substring(0, className.lastIndexOf(".")),
                className.substring(className.lastIndexOf(".") + 1, className.length()));
    }

    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(@Nullable CharSequence str) {
        return !isEmpty(str);
    }

    public static String urlToMethodName(@Nonnull String url) {
        return url.replace("://", "_")
                .replace("/", "_")
                .replace("?", "_")
                .replace("=", "_")
                .replace("%", "_")
                .replace("&", "_")
                .replace(".", "_")
                .replace("-", "_");
    }
}
