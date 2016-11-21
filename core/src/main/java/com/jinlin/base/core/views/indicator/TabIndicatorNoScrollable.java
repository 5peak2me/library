package com.jinlin.base.core.views.indicator;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jinlin.base.core.R;


/**
 * Created by J!nl!n on 2016/7/12 17:08.
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
public class TabIndicatorNoScrollable extends LinearLayout implements ViewPager.OnPageChangeListener, OnClickListener {

    private ViewPager mViewPager;

    private int mMode;
    private int mTabPadding;
    private int mTextAppearance;
    private int tabBackground;

    private int mIndicatorOffset;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private int mIndicatorMode;

    private int mUnderLineHeight;

    private Paint mPaint;
    private Paint mUnderLinePaint;

    public static final int MODE_SCROLL = 0;
    public static final int MODE_FIXED = 1;

    private int mSelectedPosition;
    private boolean mScrolling = false;

    private Runnable mTabAnimSelector;

    private ViewPager.OnPageChangeListener mListener;

    private static final int MATCH_PARENT = -1;
    private static final int WRAP_CONTENT = -2;

    private DataSetObserver mObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            notifyDataSetInvalidated();
        }

    };

    public TabIndicatorNoScrollable(Context context) {
        super(context);

        init(context, null, 0, 0);
    }

    public TabIndicatorNoScrollable(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, 0, 0);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    public TabIndicatorNoScrollable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public TabIndicatorNoScrollable(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setGravity(Gravity.CENTER_VERTICAL);
        setHorizontalScrollBarEnabled(false);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.FILL);

        mUnderLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnderLinePaint.setStyle(Style.FILL);

        applyStyle(context, attrs, defStyleAttr, defStyleRes);

        if (isInEditMode())
            addTemporaryTab();
    }

    @SuppressWarnings("unused")
    public void applyStyle(int resId) {
        applyStyle(getContext(), null, 0, resId);
    }

    private void applyStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabIndicator, defStyleAttr, defStyleRes);
        int indicatorColor;
        int underLineColor;
        try {
            mTabPadding = a.getDimensionPixelSize(R.styleable.TabIndicator_tpi_tabPadding, 12);
            indicatorColor = a.getColor(R.styleable.TabIndicator_tpi_indicatorColor, Color.WHITE);
            mIndicatorMode = a.getInt(R.styleable.TabIndicator_tpi_indicatorMode, MATCH_PARENT); /* MATCH_PARENT = -1  &&  WRAP_CONTENT = -2 */
            mIndicatorHeight = a.getDimensionPixelSize(R.styleable.TabIndicator_tpi_indicatorHeight, 2);
            underLineColor = a.getColor(R.styleable.TabIndicator_tpi_underLineColor, Color.LTGRAY);
            mUnderLineHeight = a.getDimensionPixelSize(R.styleable.TabIndicator_tpi_underLineHeight, 1);
            mTextAppearance = a.getResourceId(R.styleable.TabIndicator_android_textAppearance, 0);
            tabBackground = a.getResourceId(R.styleable.TabIndicator_tpi_tabBackground, 0);
            mMode = a.getInteger(R.styleable.TabIndicator_tpi_mode, MODE_SCROLL);
        } finally {
            a.recycle();
        }
        removeAllViews();

        mPaint.setColor(indicatorColor);
        mUnderLinePaint.setColor(underLineColor);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Re-post the selector we saved
        if (mTabAnimSelector != null)
            post(mTabAnimSelector);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabAnimSelector != null)
            removeCallbacks(mTabAnimSelector);
    }

    private TabView getTabView(int position) {
        return (TabView) getChildAt(position);
    }

    private void animateToTab(final int position) {
        final TabView tv = getTabView(position);
        if (tv == null)
            return;

        if (mTabAnimSelector != null)
            removeCallbacks(mTabAnimSelector);

        mTabAnimSelector = new Runnable() {
            public void run() {
                if (!mScrolling)
                    switch (mIndicatorMode) {
                        case MATCH_PARENT:
                            updateIndicator(tv.getLeft(), tv.getWidth());
                            break;
                        case WRAP_CONTENT:
                            int textWidth = getTextWidth(tv);
                            updateIndicator(tv.getLeft() + tv.getWidth() / 2 - textWidth / 2, textWidth);
                            break;
                    }
//                scrollTo(tv.getLeft() - (getWidth() - tv.getWidth()) / 2 + getPaddingLeft(), 0);
                mTabAnimSelector = null;
            }
        };

        post(mTabAnimSelector);
    }

    /**
     * Set a listener will be called when the current page is changed.
     *
     * @param listener The {@link ViewPager.OnPageChangeListener} will be called.
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    /**
     * Set the ViewPager associate with this indicator view.
     *
     * @param view The ViewPager view.
     */
    @SuppressWarnings("deprecation")
    public void setViewPager(ViewPager view) {
        if (mViewPager == view)
            return;

        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
            PagerAdapter adapter = view.getAdapter();
            if (adapter != null)
                adapter.unregisterDataSetObserver(mObserver);
        }

        PagerAdapter adapter = view.getAdapter();
        if (adapter == null)
            throw new IllegalStateException("ViewPager does not have adapter instance.");

        adapter.registerDataSetObserver(mObserver);

        mViewPager = view;
        view.setOnPageChangeListener(this);

        notifyDataSetChanged();
    }

    /**
     * Set the ViewPager associate with this indicator view and the current position;
     *
     * @param view            The ViewPager view.
     * @param initialPosition The current position.
     */
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    private void updateIndicator(int offset, int width) {
        mIndicatorOffset = offset;
        mIndicatorWidth = width;
        invalidate();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);

        // draw underline
        canvas.drawRect(0, getHeight() - mUnderLineHeight, getWidth(), getHeight(), mUnderLinePaint); // must do it first

        int x = mIndicatorOffset + getPaddingLeft();
        canvas.drawRect(x, getHeight() - mIndicatorHeight, x + mIndicatorWidth, getHeight(), mPaint);

        if (isInEditMode())
            canvas.drawRect(getPaddingLeft(), getHeight() - mIndicatorHeight, getPaddingLeft() + getChildAt(0).getWidth(), getHeight(), mPaint);
    }


    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            mScrolling = false;
            TextView tv = getTabView(mSelectedPosition);
            if (tv != null)
                switch (mIndicatorMode) {
                    case MATCH_PARENT:
                        updateIndicator(tv.getLeft(), tv.getMeasuredWidth());
                        break;
                    case WRAP_CONTENT:
                        int textWidth = getTextWidth(tv);
                        updateIndicator(tv.getLeft() + tv.getWidth() / 2 - textWidth / 2, textWidth);
                        break;
                }
        } else
            mScrolling = true;

        if (mListener != null)
            mListener.onPageScrollStateChanged(state);
    }

    private int getTextWidth(TextView tv) {
        return (int) tv.getPaint().measureText(tv.getText().toString());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mListener != null)
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);

        TabView tv_scroll = getTabView(position);
        TabView tv_next = getTabView(position + 1);

        if (tv_scroll != null && tv_next != null) {
            int width_scroll = mIndicatorMode == MATCH_PARENT ? tv_scroll.getWidth() : getTextWidth(tv_scroll);
            int width_next = mIndicatorMode == MATCH_PARENT ? tv_next.getWidth() : getTextWidth(tv_next);
            float distance = mIndicatorMode == MATCH_PARENT ? (width_scroll + width_next) / 2f : width_scroll / 2 + tv_scroll.getWidth() / 2 + tv_next.getWidth() / 2 - width_next / 2;

            int width = (int) (width_scroll + (width_next - width_scroll) * positionOffset + 0.5f);
            int offset = mIndicatorMode == MATCH_PARENT ?
                    (int) (tv_scroll.getLeft() + width_scroll / 2f + distance * positionOffset - width / 2f + 0.5f)
                    : (int) (tv_scroll.getLeft() + tv_scroll.getWidth() / 2 - width_scroll / 2 + distance * positionOffset + 0.5f);
            updateIndicator(offset, width);
        }
    }

    @Override
    public void onPageSelected(int position) {
        setCurrentItem(position);
        if (mListener != null)
            mListener.onPageSelected(position);
    }

    @Override
    public void onClick(android.view.View v) {
        int position = (Integer) v.getTag();
        if (position == mSelectedPosition && mListener != null)
            mListener.onPageSelected(position);

        mViewPager.setCurrentItem(position, true);
    }

    /**
     * Set the current page of this TabPageIndicator.
     *
     * @param position The position of current page.
     */
    public void setCurrentItem(int position) {
        if (mSelectedPosition != position) {
            TabView tv = getTabView(mSelectedPosition);
            if (tv != null)
                tv.setChecked(false);
        }

        mSelectedPosition = position;
        TabView tv = getTabView(mSelectedPosition);
        if (tv != null)
            tv.setChecked(true);

        animateToTab(position);
    }

    /**
     *
     */
    private void notifyDataSetChanged() {
        removeAllViews();

        PagerAdapter adapter = mViewPager.getAdapter();
        final int count = adapter.getCount();

        if (mSelectedPosition > count)
            mSelectedPosition = count - 1;

        for (int i = 0; i < count; i++) {
            CharSequence title = adapter.getPageTitle(i);
            if (title == null)
                title = "NULL";

            TabView tv = new TabView(getContext());
//            tv.setCheckMarkDrawable(null);
            tv.setText(title);
//            tv.setGravity(Gravity.CENTER);
//            tv.setTextAppearance(getContext(), mTextAppearance);
//            tv.setSingleLine(true);
//            tv.setEllipsize(TextUtils.TruncateAt.END);
//            tv.setOnClickListener(this);
            tv.setTag(i);

            if (mMode == MODE_SCROLL) {
//                tv.setPadding(mTabPadding, 0, mTabPadding, 0);
                LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.leftMargin = mTabPadding;
                lp.rightMargin = mTabPadding;
                addView(tv, lp);
            } else if (mMode == MODE_FIXED) {
                LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
                params.weight = 1f;
                addView(tv, params);
            }

        }

        setCurrentItem(mSelectedPosition);
        requestLayout();
    }

    private void notifyDataSetInvalidated() {
        PagerAdapter adapter = mViewPager.getAdapter();
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            TextView tv = getTabView(i);
            CharSequence title = adapter.getPageTitle(i);
            if (title == null)
                title = "NULL";

            tv.setText(title);
        }

        requestLayout();
    }

    private void addTemporaryTab() {
        for (int i = 0; i < 3; i++) {
            CharSequence title = null;
            if (i == 0)
                title = "流行新品";
            else if (i == 1)
                title = "最近上新";
            else if (i == 2)
                title = "人气热销";

            TabView tv = new TabView(getContext());
//            tv.setCheckMarkDrawable(null);
            tv.setText(title);
//            tv.setGravity(Gravity.CENTER);
//            tv.setTextAppearance(getContext(), mTextAppearance);
//            tv.setSingleLine(true);
            tv.setTag(i);
            tv.setChecked(i == 0);
            if (mMode == MODE_SCROLL) {
                tv.setPadding(mTabPadding, 0, mTabPadding, 0);
                addView(tv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else if (mMode == MODE_FIXED) {
                LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
                params.weight = 1f;
                addView(tv, params);
            }
        }
    }

    class TabView extends RadioButton {
        public TabView(Context context) {
            super(context, null, mTextAppearance);
            init();
        }

        private void init(){
            setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            setGravity(Gravity.CENTER);
            setTextAppearance(getContext(), mTextAppearance);
            if(0 != tabBackground){
                setBackgroundResource(tabBackground);
            }else{
                setBackground(new ColorDrawable(Color.TRANSPARENT));
            }
            setSingleLine(true);
            setEllipsize(TextUtils.TruncateAt.END);
            setOnClickListener(TabIndicatorNoScrollable.this);
        }
    }
}