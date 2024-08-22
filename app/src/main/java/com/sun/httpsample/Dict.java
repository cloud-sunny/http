package com.sun.httpsample;

/**
 * @className: Dict
 * @description: Dict 类描述
 * @author: Dict
 * @date: 2023/6/8 12:53 下午 星期四
 **/
public class Dict {

    public int id;
    public String code;
    public String type;
    public String content;
    public boolean delFlag;

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

    public boolean getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(boolean delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return "Dict{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
