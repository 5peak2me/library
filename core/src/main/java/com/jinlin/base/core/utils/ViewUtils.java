package com.jinlin.base.core.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.jinlin.base.core.R;

import java.lang.reflect.Field;

/**
 * Created by J!nl!n on 2016/5/13 12:49.
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
public final class ViewUtils {

    public static <V extends View> V findViewById(Activity activity, int resId) {
        return (V) activity.findViewById(resId);
    }

    public static <V extends View> V findViewById(View view, int resId) {
        return (V) view.findViewById(resId);
    }

    public static <V extends View> V getChildAt(View rootView, int index) {
        return (V) ((ViewGroup) rootView).getChildAt(index);
    }

    public static <V extends ViewGroup.LayoutParams> V getLayoutParams(View view) {
        return (V) view.getLayoutParams();
    }

    /**
     * activity自动findview
     */
    public static void autoFind(Activity activity) {
        try {
            Class<?> clazz = activity.getClass();
            Field[] fields = clazz.getDeclaredFields();// 获得Activity中声明的字段
            for (Field field : fields) {
                if (field.getGenericType().toString().contains("widget")
                        || field.getGenericType().toString().contains("view")
                        || field.getGenericType().toString()
                        .contains("WebView")) {// 找到所有的view和widget,WebView
                    try {
                        String name = field.getName();
                        Field idfield = R.id.class.getField(name);
                        int id = idfield.getInt(new R.id());// 获得view的id
                        field.setAccessible(true);
                        field.set(activity, activity.findViewById(id));// 给我们要找的字段设置值
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fragment以及ViewHolder等自动findview
     */
    public static void autoFind(Object obj, View view) {
        try {
            Class<?> clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();// 获得Activity中声明的字段
            for (Field field : fields) {
                if (field.getGenericType().toString().contains("widget")
                        || field.getGenericType().toString().contains("view")
                        || field.getGenericType().toString()
                        .contains("WebView")) {// 找到所有的view和widget
                    try {
                        String name = field.getName();
                        Field idfield = R.id.class.getField(name);
                        int id = idfield.getInt(new R.id());
                        field.setAccessible(true);
                        field.set(obj, view.findViewById(id));// 给我们要找的字段设置值
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public boolean isViewCovered(final View view) {
        View currentView = view;

        Rect currentViewRect = new Rect();
        boolean partVisible = currentView.getGlobalVisibleRect(currentViewRect);
        boolean totalHeightVisible = (currentViewRect.bottom - currentViewRect.top) >= view.getMeasuredHeight();
        boolean totalWidthVisible = (currentViewRect.right - currentViewRect.left) >= view.getMeasuredWidth();
        boolean totalViewVisible = partVisible && totalHeightVisible && totalWidthVisible;
        if (!totalViewVisible)//if any part of the view is clipped by any of its parents,return true
            return true;

        while (currentView.getParent() instanceof ViewGroup) {
            ViewGroup currentParent = (ViewGroup) currentView.getParent();
            if (currentParent.getVisibility() != View.VISIBLE)//if the parent of view is not visible,return true
                return true;

            int start = indexOfViewInParent(currentView, currentParent);
            for (int i = start + 1; i < currentParent.getChildCount(); i++) {
                Rect viewRect = new Rect();
                view.getGlobalVisibleRect(viewRect);
                View otherView = currentParent.getChildAt(i);
                Rect otherViewRect = new Rect();
                otherView.getGlobalVisibleRect(otherViewRect);
                if (Rect.intersects(viewRect, otherViewRect))//if view intersects its older brother(covered),return true
                    return true;
            }
            currentView = currentParent;
        }
        return false;
    }


    private int indexOfViewInParent(View view, ViewGroup parent) {
        int index;
        for (index = 0; index < parent.getChildCount(); index++) {
            if (parent.getChildAt(index) == view)
                break;
        }
        return index;
    }

    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
