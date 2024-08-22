package com.sun.cloud.http;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.ObjectsCompat;

/**
 * A generic class that holds a value with its loading status.
 *
 * @param <T>
 */
@SuppressWarnings("WeakerAccess")
public class Resource<T> {

    @NonNull
    public final Status status;

    @NonNull
    public int code = -1;

    @Nullable
    public final String message;

    @Nullable
    public final T data;

    public Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> success(@Nullable T data, @Nullable String message) {
        return new Resource<>(Status.SUCCESS, data, message);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg);
    }
    public static <T> Resource<T> error(String msg, @Nullable T data, @Nullable int code) {
        Resource<T> tResource = new Resource<>(Status.ERROR, data, msg);
        tResource.code = code;
        return tResource;
    }
    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Resource<?> resource = (Resource<?>) o;

        if (status != resource.status) {
            return false;
        }
        if (!ObjectsCompat.equals(message, resource.message)) {
            return false;
        }
        return ObjectsCompat.equals(data, resource.data);
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    @NonNull
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
