package com.threess.summership.treasurehunt.model.network;

public interface GenericCallback<T> {
    void onResponseReady(T generic);
}
