package com.jinlin.base.core.views.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;

/**
 * Created by J!nl!n on 15/12/3.
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
 * 由于要加入到ListView的FootView里去,则最好不要使用ListView重写onMeasure方法.
 * 因为非常繁重,直接使用自定义的竖直线性布局代替LinearLayout
 * 同时整理其适用方法完全类似于ListView
 */
public class NotScrollableListView extends LinearLayoutCompat {

    private final DataSetObserver dataSetObserver;
    private BaseAdapter adapter;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private Drawable divider;
    private int dividerHeight;

    /**
     * xml初始化调用
     *
     * @param context 上下文对象
     * @param attrs   定义属性集
     * @author J!nl!n
     */
    public NotScrollableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayoutCompat.VERTICAL);
        setAttributes(attrs);
        this.dataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                syncDataFromAdapter();
                super.onChanged();
            }

            @Override
            public void onInvalidated() {
                syncDataFromAdapter();
                super.onInvalidated();
            }
        };
    }

    /**
     * 设置适配器
     *
     * @param adapter 数据源适配器
     * @author J!nl!n
     */
    public void setAdapter(BaseAdapter adapter) {
        ensureDataSetObserverIsUnregistered();

        this.adapter = adapter;
        if (this.adapter != null) {
            this.adapter.registerDataSetObserver(dataSetObserver);
        }
        syncDataFromAdapter();
    }

    /**
     * 反注册数据观察者
     *
     * @author J!nl!n
     */
    protected void ensureDataSetObserverIsUnregistered() {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(dataSetObserver);
        }
    }

    /**
     * 获取对于位置的对象
     *
     * @param position Item位置
     * @return
     * @author J!nl!n
     */
    public Object getItemAtPosition(int position) {
        return adapter != null ? adapter.getItem(position) : null;
    }

    /**
     * 设置选中
     *
     * @param i Item位置
     * @author J!nl!n
     */
    public void setSelection(int i) {
        getChildAt(i).setSelected(true);
    }

    /**
     * 设置点击事件
     *
     * @param onItemClickListener 点击监听
     * @author J!nl!n
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置长按监听
     *
     * @param onItemLongClickListener 长按监听
     * @author J!nl!n
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    /**
     * 获取数据源适配器
     *
     * @return
     * @author J!nl!n
     */
    public BaseAdapter getAdapter() {
        return adapter;
    }

    /**
     * 获取数据个数
     *
     * @return
     * @author J!nl!n
     */
    public int getCount() {
        return adapter != null ? adapter.getCount() : 0;
    }

    /**
     * 初始化数据源适配器
     *
     * @author J!nl!n
     */
    private void syncDataFromAdapter() {
        removeAllViews();
        if (adapter != null) {
            int count = adapter.getCount();

            // calculating of divider properties
            ViewGroup.LayoutParams dividerLayoutParams = null;
            if (divider != null && dividerHeight > 0) {
                dividerLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight);
            }
            // adding items
            for (int i = 0; i < count; i++) {
                // adding item
                View view = adapter.getView(i, null, this);
                boolean enabled = adapter.isEnabled(i);
                if (enabled) {
                    final int position = i;
                    final long id = adapter.getItemId(position);
                    view.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick(null, v, position, id);
                            }
                        }
                    });
                    view.setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (onItemLongClickListener != null) {
                                onItemLongClickListener.onItemLongClick(null, v, position, id);
                            }
                            return false;
                        }
                    });
                }
                addView(view);

                // adding divider
                if (divider != null && dividerHeight > 0) {
                    if (i < count - 1) {
                        View dividerView = new View(getContext());
                        dividerView.setBackgroundDrawable(divider);
                        dividerView.setLayoutParams(dividerLayoutParams);
                        addView(dividerView);
                    }
                }
            }
        }
    }

    /**
     * 设置分割线属性集
     *
     * @param attributes
     * @author J!nl!n
     */
    private void setAttributes(AttributeSet attributes) {
        int[] dividerAttrs = new int[]{android.R.attr.divider, android.R.attr.dividerHeight};

        TypedArray a = getContext().obtainStyledAttributes(attributes, dividerAttrs);
        try {
            divider = a.getDrawable(0);
            dividerHeight = a.getDimensionPixelSize(1, 0);
        } finally {
            a.recycle();
        }
    }
}