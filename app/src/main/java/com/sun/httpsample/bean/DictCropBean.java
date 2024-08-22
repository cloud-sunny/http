package com.sun.httpsample.bean;

import androidx.annotation.DrawableRes;

import java.util.Objects;

/**
 * Created on 2021/4/15
 * <p>
 *
 * @author sunxiaoyun
 */
public class DictCropBean {

    /**
     * id : 1
     * code : 01
     * type : crop
     * content : 春玉米
     * delFlag : false
     * sorting : 1
     */

    private int id;
    private String code;
    private String type;
    private String content;
    private boolean delFlag;
    private int sorting;
    /**
     * 以下两个属性不是后台返回，而是App根据自己需要set的
     */
    @DrawableRes
    private int cropIcon;
    private boolean selected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDelFlag() {
        return delFlag;
    }

    public void setDelFlag(boolean delFlag) {
        this.delFlag = delFlag;
    }

    public int getSorting() {
        return sorting;
    }

    public void setSorting(int sorting) {
        this.sorting = sorting;
    }

    public int getCropIcon() {
        return cropIcon;
    }

    public void setCropIcon(@DrawableRes int cropIcon) {
        this.cropIcon = cropIcon;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictCropBean that = (DictCropBean) o;
        return id == that.id &&
                delFlag == that.delFlag &&
                sorting == that.sorting &&
                cropIcon == that.cropIcon &&
                selected == that.selected &&
                Objects.equals(code, that.code) &&
                Objects.equals(type, that.type) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, type, content, delFlag, sorting, cropIcon, selected);
    }

    @Override
    public String toString() {
        return "DictCropBean{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", delFlag=" + delFlag +
                ", sorting=" + sorting +
                ", cropIcon=" + cropIcon +
                ", selected=" + selected +
                '}';
    }
}

