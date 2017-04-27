package com.lvmama.base.core.mvp.delegate;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lvmama.base.core.mvp.IPresenter;
import com.lvmama.base.core.mvp.IView;

/**
 * Created by J!nl!n on 2017/3/14.
 * Copyright © 1990-2017 J!nl!n™ Inc. All rights reserved.
 * <p>
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
public class ActivityMvpDelegateImpl<V extends IView, P extends IPresenter<V>> implements ActivityMvpDelegate {

    public static boolean DEBUG = false;
    private static final String DEBUG_TAG = "ActivityMvpDelegateImpl";

    private MvpDelegateCallback<V, P> delegateCallback;
    protected Activity activity;

    /**
     * @param activity         The Activity
     * @param delegateCallback The callback
     *                         orientation changes. Otherwise false.
     */
    public ActivityMvpDelegateImpl(@NonNull Activity activity,
                                   @NonNull MvpDelegateCallback<V, P> delegateCallback) {

        this.delegateCallback = delegateCallback;
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle bundle) {

        P presenter = delegateCallback.initPresenter();
        delegateCallback.setPresenter(presenter);
        getPresenter().attachView(getMvpView());

        if (DEBUG) {
            Log.d(DEBUG_TAG, "View" + getMvpView() + " attached to Presenter " + presenter);
        }
    }

    private P getPresenter() {
        P presenter = delegateCallback.getPresenter();
        if (presenter == null) {
            throw new NullPointerException("Presenter returned from getPresenter() is null");
        }
        return presenter;
    }

    private V getMvpView() {
        V view = delegateCallback.getMvpView();
        if (view == null) {
            throw new NullPointerException("View returned from getMvpView() is null");
        }
        return view;
    }

    @Override
    public void onDestroy() {
        getPresenter().detachView();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onContentChanged() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
    }
}
