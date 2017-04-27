package com.lvmama.base.core.mvp;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lvmama.base.core.mvp.delegate.ActivityMvpDelegate;
import com.lvmama.base.core.mvp.delegate.ActivityMvpDelegateImpl;
import com.lvmama.base.core.mvp.delegate.MvpDelegateCallback;
import com.lvmama.base.core.ui.BaseActivity;


/**
 * Created by J!nl!n on 2016/9/14 13:29.
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
public abstract class BaseMvpActivity<V extends IView, P extends BasePresenter> extends BaseActivity
        implements IView, MvpDelegateCallback<V, P> {

    protected ActivityMvpDelegate mDelegate;
    protected P mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMvpDelegate().onCreate(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getMvpDelegate().onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMvpDelegate().onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMvpDelegate().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getMvpDelegate().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getMvpDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getMvpDelegate().onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getMvpDelegate().onSaveInstanceState(outState);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        getMvpDelegate().onContentChanged();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getMvpDelegate().onPostCreate(savedInstanceState);
    }

    protected ActivityMvpDelegate getMvpDelegate() {
        if (mDelegate == null) {
            mDelegate = new ActivityMvpDelegateImpl<>(this, this);
        }
        return mDelegate;
    }

    @NonNull
    public P getPresenter() {
        return mPresenter;
    }

    @Override
    public void setPresenter(@NonNull P presenter) {
        this.mPresenter = presenter;
    }

    @NonNull
    @Override
    public V getMvpView() {
        return (V) this;
    }

}
