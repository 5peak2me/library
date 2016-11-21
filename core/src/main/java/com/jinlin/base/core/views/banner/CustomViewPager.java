package com.jinlin.base.core.views.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.jinlin.base.core.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 防滑动干扰的ViewPager
 * Updated by J!nl!n 修改支持编辑预览布局
 */
public class CustomViewPager extends ViewPager {

    private int lastX;

    private ViewParent vertical_interference_view;
    private VelocityTracker vTracker = null;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode())
            preview(context, attrs);
    }

    public CustomViewPager(Context context) {
        super(context);
        if (isInEditMode())
            preview(context, null);
    }

    public void setVerticalInterferenceView(
            ViewParent vertical_interference_view) {
        this.vertical_interference_view = vertical_interference_view;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (vertical_interference_view != null) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                lastX = x;
                int lastY = y;
                vertical_interference_view
                        .requestDisallowInterceptTouchEvent(true);
                if (vTracker == null) {
                    vTracker = VelocityTracker.obtain();
                } else {
                    vTracker.clear();
                }
                vTracker.addMovement(event);
            }
            if (event.getAction() == MotionEvent.ACTION_CANCEL
                    | event.getAction() == MotionEvent.ACTION_UP) {
                vertical_interference_view
                        .requestDisallowInterceptTouchEvent(false);
                if (vTracker != null) {
                    vTracker.clear();
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                int deltaX = x - lastX;
                vTracker.addMovement(event);
                vTracker.computeCurrentVelocity(1000);
                int minMove = 5;
                if (Math.abs(vTracker.getYVelocity()) > Math.abs(vTracker
                        .getXVelocity())) {
                    vertical_interference_view
                            .requestDisallowInterceptTouchEvent(false);
                } else if (getCurrentItem() == 0 && deltaX > minMove) {
                    vertical_interference_view
                            .requestDisallowInterceptTouchEvent(false);

                } else if (getAdapter() != null
                        && getCurrentItem() == (getAdapter().getCount() - 1)
                        && deltaX < -minMove) {
                    vertical_interference_view
                            .requestDisallowInterceptTouchEvent(false);

                }
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    private void preview(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomViewPager);
        List<View> viewList = new ArrayList<>();

        int layoutResId;
        if ((layoutResId = a.getResourceId(R.styleable.CustomViewPager_tools_layout0, 0)) != 0) {
            viewList.add(inflate(context, layoutResId, null));
        }
        a.recycle();

        setAdapter(new PreviewPagerAdapter(viewList));
    }

    /**
     * 这里传入一个list数组，从每个list中可以剥离一个view并显示出来
     */
    public static class PreviewPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public PreviewPagerAdapter(List<View> viewList) {
            mViewList = viewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (mViewList.get(position) != null) {
                container.removeView(mViewList.get(position));
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position), 0);
            return mViewList.get(position);
        }
    }
}
