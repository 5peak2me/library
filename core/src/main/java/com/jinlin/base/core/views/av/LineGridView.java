package com.jinlin.base.core.views.av;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.jinlin.base.core.R;


/**
 * Created by J!nl!n on 2016/10/11 11:15.
 * Copyright © 1990-2016 J!nl!n™ Inc. All rights reserved.
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
public class LineGridView extends GridView {

    private boolean mIsInScrollView;
    private int mLineColor;

    public LineGridView(Context context) {
        this(context, null);
    }

    public LineGridView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.gridViewStyle);
    }

    public LineGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LineGridView);
        try {
            mIsInScrollView = ta.getBoolean(R.styleable.LineGridView_isInScrollView, false);
            mLineColor = ta.getColor(R.styleable.LineGridView_lineColor, Color.LTGRAY);
        } finally {
            ta.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mIsInScrollView)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 注意这里的getLeft(), getTop(), getRight, getBottom()均是相对于父控件的位置
     * @param canvas 画布
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (getChildAt(0) != null) {
            int column = getNumColumns(); // 每一列的宽度
            int childCount = getChildCount(); // 获取子元素个数
            int row;
            if (childCount % column == 0) { // 如果子元素被列整除，则不新增行
                row = childCount / column;
            } else {
                row = childCount / column + 1;
            }
            int endAllcolumn = (row - 1) * column;
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            paint.setColor(mLineColor);

            for (int i = 0; i < childCount; i++) {
                View cellView = getChildAt(i);
                if ((i + 1) % column != 0) {
                    canvas.drawLine(cellView.getRight(), cellView.getTop(), cellView.getRight(), cellView.getBottom(), paint); // Vertical Line
                }
                if ((i + 1) <= endAllcolumn) {
                    canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), paint); // horizontal Line
                }
            }
            if (childCount % column != 0) {
                for (int j = 0; j < (column - childCount % column); j++) {
                    View lastView = getChildAt(childCount - 1);
                    canvas.drawLine(lastView.getRight() + lastView.getWidth() * j, lastView.getTop(),
                            lastView.getRight() + lastView.getWidth() * j, lastView.getBottom(), paint);
                }
            }
        }
    }
}
