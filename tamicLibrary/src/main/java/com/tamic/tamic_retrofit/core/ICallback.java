package com.tamic.tamic_retrofit.core;

/**
 * Created by LIUYONGKUI726 on 2016-07-13.
 */
public interface ICallback<T> {

    void success(T t);
    void failed(Throwable e);
}
