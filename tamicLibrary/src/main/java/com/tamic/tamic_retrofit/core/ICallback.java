package com.tamic.tamic_retrofit.core;

/**
 * Created by LIUYONGKUI726 on 2016-07-13.
 */
public abstract class ICallback<T> {

    public abstract void success(T t);

    public abstract void failed(Throwable e);

    public void start(){

    }
    public void finish(){

    }
    public void cancel(){

    }
}
