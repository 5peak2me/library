package com.lvmama.base.core.mvp.delegate;

import android.app.Activity;
import android.os.Bundle;


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
public interface ActivityMvpDelegate {
    /**
     * This method must be called from {@link Activity#onCreate(Bundle)}.
     * This method internally creates the presenter and attaches the view to it.
     */
    void onCreate(Bundle bundle);

    /**
     * This method must be called from {@link Activity#onDestroy()}}.
     * This method internally detaches the view from presenter
     */
    void onDestroy();

    /**
     * This method must be called from {@link Activity#onPause()}
     */
    void onPause();

    /**
     * This method must be called from {@link Activity#onResume()}
     */
    void onResume();

    /**
     * This method must be called from {@link Activity#onStart()}
     */
    void onStart();

    /**
     * This method must be called from {@link Activity#onStop()}
     */
    void onStop();

    /**
     * This method must be called from {@link Activity#onRestart()}
     */
    void onRestart();

    /**
     * This method must be called from {@link Activity#onContentChanged()}
     */
    void onContentChanged();

    /**
     * This method must be called from {@link Activity#onSaveInstanceState(Bundle)}
     */
    void onSaveInstanceState(Bundle outState);

    /**
     * This method must be called from {@link Activity#onRestoreInstanceState(Bundle)} (Bundle)}
     */
    void onRestoreInstanceState(Bundle savedInstanceState);

    /**
     * This method must be called from {@link Activity#onPostCreate(Bundle)}
     */
    void onPostCreate(Bundle savedInstanceState);
}
