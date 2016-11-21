package com.jinlin.base.core.views.iv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

/**
 * Created by J!nl!n on 2016/5/31 14:02.
 * Copyright © 1990-2015 J!nl!n™ Inc. All rights reserved.
 *
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
public class ScaleImageView extends RatioImageView {

    private static final short DEFAULT_ANIM_TIME = 100;
    private Animator anim1;
    private Animator anim2;

    private OnClickListener listener;

    public ScaleImageView(Context context) {
        super(context);
        init();
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    private void init() {

        PropertyValuesHolder valuesHolder_1 = PropertyValuesHolder.ofFloat(
                "scaleX", 1f, 0.95f);
        PropertyValuesHolder valuesHolder_2 = PropertyValuesHolder.ofFloat(
                "scaleY", 1f, 0.92f);
        anim1 = ObjectAnimator.ofPropertyValuesHolder(this, valuesHolder_1,
                valuesHolder_2);
        anim1.setDuration(DEFAULT_ANIM_TIME);
        anim1.setInterpolator(new LinearInterpolator());

        PropertyValuesHolder valuesHolder_3 = PropertyValuesHolder.ofFloat(
                "scaleX", 0.95f, 1f);
        PropertyValuesHolder valuesHolder_4 = PropertyValuesHolder.ofFloat(
                "scaleY", 0.92f, 1f);
        anim2 = ObjectAnimator.ofPropertyValuesHolder(this, valuesHolder_3,
                valuesHolder_4);
        anim2.setDuration(DEFAULT_ANIM_TIME);
        anim2.setInterpolator(new LinearInterpolator());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                post(new Runnable() {
                    @TargetApi(VERSION_CODES.HONEYCOMB)
                    @Override
                    public void run() {
                        anim2.end();
                        anim1.start();
                    }
                });
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                post(new Runnable() {
                    @TargetApi(VERSION_CODES.HONEYCOMB)
                    @Override
                    public void run() {
                        anim1.end();
                        anim2.start();
                    }
                });
                if (event.getAction() == MotionEvent.ACTION_UP && anim2.getListeners() == null) {
                    anim2.addListener(sAnimatorListenerAdapter);
                }
                break;
        }
        return true;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        listener = l;
    }

    private AnimatorListenerAdapter sAnimatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            if (listener != null) {
                listener.onClick(ScaleImageView.this);
                anim2.removeListener(sAnimatorListenerAdapter);
            }
        }
    };

}
