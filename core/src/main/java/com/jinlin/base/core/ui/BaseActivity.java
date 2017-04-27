package com.lvmama.base.core.ui;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lvmama.base.core.ui.interfaces.UiDelegate;

/**
 * Created by J!nl!n on 2016/12/8.
 * Copyright © 1990-2016 J!nl!n™ Inc. All rights reserved.
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
public abstract class BaseActivity extends AppCompatActivity implements UiDelegate {

    @CallSuper
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getLayoutId());
        this.init(savedInstanceState);
        this.initView(null);
    }

    @CallSuper
    @Override
    protected void onStart() {
        super.onStart();
        this.setListener();
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    @Override
    public void visible(@NonNull View view, int visibility) {
        view.setVisibility(visibility);
    }

    protected final <V extends View> V $(@NonNull View view, int resId) {
        return (V) view.findViewById(resId);
    }

    protected final <V extends View> V $(int resId) {
        return (V) findViewById(resId);
    }

}
