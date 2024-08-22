package com.sun.httpsample.bean;

import androidx.core.util.ObjectsCompat;

/**
 * Created on 2021/4/15
 * <p>
 *
 * @author sunxiaoyun
 */
public class WorkType {
    public int id;
    public String code;
    public String type;
    public String content;
    public boolean delFlag;
    public Object sorting;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkType)) {
            return false;
        }
        WorkType workType = (WorkType) o;
        return id == workType.id;
    }

    @Override
    public int hashCode() {
        return ObjectsCompat.hash(id);
    }
}
