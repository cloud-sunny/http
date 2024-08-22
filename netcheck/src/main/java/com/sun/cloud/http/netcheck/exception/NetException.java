package com.sun.cloud.http.netcheck.exception;

/**
 * Created on 2019/10/22
 *
 * @author sunxiaoyun
 */
public class NetException extends Exception {

    private ExceptionType type;
    private String displayMessage;
    private long time;
    private String url;

    public NetException(Throwable throwable, ExceptionType code) {
        super(throwable);
        this.type = code;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ExceptionType getType() {
        return type;
    }

    public void setType(ExceptionType type) {
        this.type = type;
    }

    @Override
    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n 异常请求: ");
        builder.append(url);
        builder.append("\n 异常类型: ");
        builder.append(displayMessage);
        builder.append("\n");
        builder.append(super.getMessage());
        return builder.toString();
    }

    @Override
    public String toString() {
        return "NetException{" +
                "type=" + type +
                ", displayMessage='" + displayMessage + '\'' +
                ", time=" + time +
                ", url='" + url + '\'' +
                '}';
    }
}
