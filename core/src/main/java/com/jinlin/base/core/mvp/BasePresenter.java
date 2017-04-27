package com.lvmama.base.core.mvp;

import android.support.annotation.UiThread;

import java.lang.ref.WeakReference;

/**
 * Created by J!nl!n on 2016/9/14 13:38.
 * Copyright © 1990-2016 J!nl!n™ Inc. All rights reserved.
 * <p/>
 * ━━━━━━神兽出没━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛Code is far away from bug with the animal protecting
 * 　　　　┃　　　┃    神兽保佑,代码无bug
 * 　　　　┃　　　┃
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━感觉萌萌哒━━━━━━
 */
public abstract class BasePresenter<M extends IModel, V extends IView> implements IPresenter<V> {

//    private V mView;
    private WeakReference<V> viewRef;
    private M mModel;

    public BasePresenter(M model) {
        mModel = model;
    }

    @UiThread
    @Override
    public void attachView(V view) {
//        mView = view;
        viewRef = new WeakReference<>(view);
    }

    @Override
    public void detachView() {
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }

    public boolean isViewAttached() {
        return viewRef != null && viewRef.get() != null;
    }

    public V getView() {
        return viewRef == null ? null : viewRef.get();
    }

    public M getModel() {
        return mModel;
    }

}
