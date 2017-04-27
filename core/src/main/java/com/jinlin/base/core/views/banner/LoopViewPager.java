package com.jinlin.base.core.views.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.Glide;
import com.jinlin.base.core.R;
import com.jinlin.base.core.views.iv.ResizableImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 需要实现循环滚动的时候，不需要改动代码，只需要把xml的节点换成LoopViewPager就可以了
 * 如果滑动到边界的时候，出现了闪现的情况，调用setBoundaryCaching( true ), 或者 DEFAULT_BOUNDARY_CASHING 变成 true
 * 如果使用 FragmentPagerAdapter 或者 FragmentStatePagerAdapter, 必须改变Adapter，使其多创建两个条目，比如
 * original adapter position    [0,1,2,3]
 * modified adapter position  [0,1,2,3,4,5]
 * modified     realPosition  [3,0,1,2,3,0]
 * modified     InnerPosition [4,1,2,3,4,1]
 */
public class LoopViewPager extends ViewPager {

    private PagerAdapter mAdapter;   //原始的Adapter
    private LoopAdapterWrapper mLoopAdapter;    //实现了循环滚动的Adapter

    private OnPageChangeListener loopPageChangeListener;  //内部定义的监听器
    private OnPageChangeListener mOnPageChangeListener;   //外部通过set传进来的
    private ArrayList<OnPageChangeListener> mOnPageChangeListeners;   //外部通过add传进来的

    private Handler mHandler;  //处理轮播的Handler
    private boolean mIsAutoLoop = true;  //是否自动轮播
    private int mDelayTime = 4000; //轮播的延时时间
    private boolean isDetached;    //是否被回收过
    private int currentPosition;   //当前的条目位置
    private static final int MSG_AUTO_SCROLL = 0;

    public LoopViewPager(Context context) {
        this(context, null);
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        loopPageChangeListener = new MyOnPageChangeListener();
        super.addOnPageChangeListener(loopPageChangeListener);

        TypedArray a = getResources().obtainAttributes(attrs, R.styleable.LoopViewPager);
        mIsAutoLoop = a.getBoolean(R.styleable.LoopViewPager_lvp_isAutoLoop, mIsAutoLoop);
        mDelayTime = a.getInteger(R.styleable.LoopViewPager_lvp_delayTime, mDelayTime);
        a.recycle();

        setAutoLoop(mIsAutoLoop, mDelayTime);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isDetached) {
            if (loopPageChangeListener != null) {
                super.addOnPageChangeListener(loopPageChangeListener);
            }
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler.sendEmptyMessageDelayed(0, mDelayTime);
            }
            isDetached = false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (loopPageChangeListener != null) {
            super.removeOnPageChangeListener(loopPageChangeListener);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        isDetached = true;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("currentPosition", currentPosition);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable("superState"));
            currentPosition = bundle.getInt("currentPosition");
        } else {
            super.onRestoreInstanceState(state);
        }
        setCurrentItem(currentPosition);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        mAdapter = adapter;
        mLoopAdapter = new LoopAdapterWrapper(adapter);
        super.setAdapter(mLoopAdapter);
        setCurrentItem(0, false);
    }

    /**
     * 默认返回的是传进来的Adapter
     */
    @Override
    public PagerAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        int realItem = mLoopAdapter == null ? 0 : mLoopAdapter.getInnerPosition(item);
        super.setCurrentItem(realItem, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        if (getCurrentItem() != item) {
            setCurrentItem(item, true);
        }
    }

    @Override
    public int getCurrentItem() {
        return mLoopAdapter == null ? 0 : mLoopAdapter.toRealPosition(super.getCurrentItem());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        if (mOnPageChangeListeners == null) {
            mOnPageChangeListeners = new ArrayList<>();
        }
        mOnPageChangeListeners.add(listener);
    }

    @Override
    public void removeOnPageChangeListener(OnPageChangeListener listener) {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners.remove(listener);
        }
    }

    public void setData(final List<String> data) {
        setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ResizableImageView imageView = new ResizableImageView(getContext());
                imageView.setScaleType(ScaleType.CENTER_CROP);
                Glide.with(getContext()).load(data.get(position)).into(imageView);
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
    }

    private class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            int realPosition = mLoopAdapter == null ? 0 : mLoopAdapter.toRealPosition(position);
            currentPosition = realPosition;

            //分发事件给外部传进来的监听
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(realPosition);
            }
            if (mOnPageChangeListeners != null) {
                for (int i = 0, z = mOnPageChangeListeners.size(); i < z; i++) {
                    OnPageChangeListener listener = mOnPageChangeListeners.get(i);
                    if (listener != null) {
                        listener.onPageSelected(realPosition);
                    }
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int realPosition = mLoopAdapter == null ? 0 : mLoopAdapter.toRealPosition(position);

            //分发事件给外部传进来的监听
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
            }
            if (mOnPageChangeListeners != null) {
                for (int i = 0, z = mOnPageChangeListeners.size(); i < z; i++) {
                    OnPageChangeListener listener = mOnPageChangeListeners.get(i);
                    if (listener != null) {
                        listener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    stopScrolling();
                    break;
                default:
                    if (!mIsAutoLoop) {
                        startScrolling();
                    }
                    break;
            }
            //当滑动到了第一页 或者 最后一页的时候，跳转到指定的对应页
            //不能在onPageSelected中写这段逻辑，因为onPageSelected当松手的时候，就调用了
            //不是在滑动结束后再调用
            int position = LoopViewPager.super.getCurrentItem();
            int realPosition = mLoopAdapter == null ? 0 : mLoopAdapter.toRealPosition(position);
            int count = mLoopAdapter == null ? 0 : mLoopAdapter.getCount();
            if (state == ViewPager.SCROLL_STATE_IDLE && (position == 0 || position == count - 1)) {
                setCurrentItem(realPosition, false);
            }

            //分发事件给外部传进来的监听
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
            if (mOnPageChangeListeners != null) {
                for (int i = 0, z = mOnPageChangeListeners.size(); i < z; i++) {
                    OnPageChangeListener listener = mOnPageChangeListeners.get(i);
                    if (listener != null) {
                        listener.onPageScrollStateChanged(state);
                    }
                }
            }
        }
    }

    /**
     * 设置是否自动轮播  delayTime延时的毫秒
     */
    public void setAutoLoop(boolean isAutoLoop, int delayTime) {
        mIsAutoLoop = isAutoLoop;
        mDelayTime = delayTime;
        if (mIsAutoLoop) {
            if (mHandler == null) {
                mHandler = new InnerHandler(this);
                mHandler.sendEmptyMessageDelayed(0, mDelayTime);
            } else {
                mHandler.removeCallbacksAndMessages(null);
                mHandler.sendEmptyMessageDelayed(0, mDelayTime);
            }
        } else {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
        }
    }

    public void startScrolling() {
        mIsAutoLoop = canRunning();
        if (mHandler != null) {
            mHandler.removeMessages(MSG_AUTO_SCROLL);
            if (mIsAutoLoop) {
                mHandler.sendEmptyMessageDelayed(MSG_AUTO_SCROLL, mDelayTime);
            }
        }
    }

    public boolean canRunning() {
        return getVisibility() == View.VISIBLE
                && getAdapter() != null && getAdapter().getCount() > 1;
    }

    public void stopScrolling() {
        mIsAutoLoop = false;
        if (mHandler != null) {
            mHandler.removeMessages(MSG_AUTO_SCROLL);
        }
    }

    /**
     * 自动轮播的Handler
     */
    private static class InnerHandler extends Handler {
        private WeakReference<LoopViewPager> mViewGroup;

        public InnerHandler(LoopViewPager viewGroup) {
            mViewGroup = new WeakReference<>(viewGroup);
        }

        @Override
        public void handleMessage(Message msg) {
            LoopViewPager viewPager = mViewGroup.get();
            if (viewPager != null) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                sendEmptyMessageDelayed(0, viewPager.mDelayTime);
            }
        }
    }

    /**
     * 用该类包装一个需要实现循环滚动的Adapter
     */
    private class LoopAdapterWrapper extends PagerAdapter {

        private PagerAdapter mAdapter;
        private int realFirst;
        private int realLast;

        public LoopAdapterWrapper(PagerAdapter adapter) {
            this.mAdapter = adapter;
            realFirst = 1;
            realLast = realFirst + getRealCount() - 1;
        }

        @Override
        public int getCount() {
            return getRealCount() > 1 ? getRealCount() + 2 : getRealCount();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int realPosition = toRealPosition(position);
            return mAdapter.instantiateItem(container, realPosition);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            boolean flag = mAdapter instanceof FragmentPagerAdapter || mAdapter instanceof FragmentStatePagerAdapter;
            int realPosition = toRealPosition(position);

            //头尾的两个一直不销毁
            if (flag && (position <= realFirst || position >= realLast)) {
                return;
            }
            mAdapter.destroyItem(container, realPosition, object);
        }

        /**
         * original adapter position    [0,1,2,3]
         * modified adapter position  [0,1,2,3,4,5]
         * modified     realPosition  [3,0,1,2,3,0]
         * modified     InnerPosition [4,1,2,3,4,1]
         */
        protected int toRealPosition(int position) {
            int realCount = getRealCount();
            if (realCount == 0)
                return 0;
            int realPosition = (position - 1) % realCount;
            if (realPosition < 0)
                realPosition = realPosition + realCount;
            return realPosition;
        }

        /**
         * 根据传进来的真实位置，得到该 loopAdapter 的真实条目位置
         */
        public int getInnerPosition(int realPosition) {
            return realPosition + 1;
        }

        public int getRealCount() {
            return mAdapter.getCount();
        }

        //重写对Adapter的操作
        @Override
        public void finishUpdate(ViewGroup container) {
            mAdapter.finishUpdate(container);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return mAdapter.isViewFromObject(view, object);
        }

        @Override
        public void restoreState(Parcelable bundle, ClassLoader classLoader) {
            mAdapter.restoreState(bundle, classLoader);
        }

        @Override
        public Parcelable saveState() {
            return mAdapter.saveState();
        }

        @Override
        public void startUpdate(ViewGroup container) {
            mAdapter.startUpdate(container);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            mAdapter.setPrimaryItem(container, position, object);
        }
    }

}
