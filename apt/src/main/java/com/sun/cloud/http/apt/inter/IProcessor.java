package com.sun.cloud.http.apt.inter;

import javax.annotation.processing.Filer;
import javax.lang.model.util.Elements;

public interface IProcessor {
    /**
     * 日志打印
     *
     * @param message
     */
    void log(String message);

    /**
     * 元素辅助类
     *
     * @return 元素
     */
    Elements getElements();

    /**
     * 文件操作
     *
     * @return 文件操作者
     */
    Filer getFiler();
}
