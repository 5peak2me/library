package com.jinlin.base.core.views.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jinlin.base.core.R;
import com.jinlin.base.core.views.iv.ResizableImageView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Jinlin on 2015年10月16日14:37:48 修改支持自定义比例,由于首页与其他页面高度显示比例不同,则需要更加具体情况自定义
 */
public class BannerView extends RelativeLayout implements OnPageChangeListener, View.OnClickListener {

    /**
     * 默认自动切换广告的间隔时间秒数
     */
    private static final int DEFAULT_INTERVAL = 5 * 1000;

    /**
     * 默认的广告牌长宽比（高度除以宽度）
     */
    private static final float DEFAULT_ASPECT_RATIO = 0.5f;

    private int mScrollInterval = DEFAULT_INTERVAL;

    private static final int MSG_AUTO_SCROLL = 0;

    /**
     * 显示广告的ViewPager
     */
    private CustomViewPager mPager;

    private float mAspectRatio = DEFAULT_ASPECT_RATIO;

    private boolean mScrollable = true;

    /**
     * 广告页面位置指示器
     */
    private DotAdIndicator mIndicator;

    private OnPageChangeListener mOnPageChangeListener;

    /**
     * 广告被点击时的回调函数
     */
    private OnItemClickListener mOnItemClickListener;

    private boolean mRunning = false;

    /**
     * 内部Handler类引起内存泄露
     */
    private Handler mHandler = new MyHandler(this);
    private int mResourceId;
    private ResizableImageView mImageView;

    private static class MyHandler extends Handler {
        private final WeakReference<BannerView> mViewGroup;

        public MyHandler(BannerView view) {
            mViewGroup = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            BannerView adView = mViewGroup.get();
            if (msg.what == MSG_AUTO_SCROLL && adView != null && adView.mRunning) {
                adView.showNext();
                adView.startScrolling();
            }
        }
    }

    /**
     * 设置颜色，（选中和未选中颜色相同，默认当前点为空心）
     *
     * @param color 颜色值
     */
    public void setColor(int color) {
        mIndicator.setColor(color);
    }

    /**
     * 设置选中的颜色
     *
     * @param color 选中颜色值
     */
    @SuppressWarnings("unused")
    public void setSelectedDotColor(int color) {
        mIndicator.setSelectedColor(color);
    }

    /**
     * 设置未选中的颜色
     *
     * @param color 未选中颜色值
     */
    @SuppressWarnings("unused")
    public void setUnselectedDotColor(int color) {
        mIndicator.setUnselectedColor(color);
    }

    @SuppressWarnings("unused")
    public void setSelectedPaintStyle(Paint.Style style) {
        mIndicator.setSelectedPaintStyle(style);
    }

    @SuppressWarnings("unused")
    public boolean isScrollable() {
        return mScrollable;
    }

    @SuppressWarnings("unused")
    public void setScrollable(boolean scrollable) {
        this.mScrollable = scrollable;
    }

    /**
     * 显示的广告列表
     */
    private List<AdEntity> mAdList;

    public BannerView(Context context) {
        super(context);
        init(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context 上下文对象
     * @param attrs   属性数组
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BannerView, 0, 0);
        try {
            mScrollInterval = ta.getInt(R.styleable.BannerView_interval, DEFAULT_INTERVAL);
            mResourceId = ta.getResourceId(R.styleable.BannerView_defaultResId, 0);
            mAspectRatio = ta.getFloat(R.styleable.BannerView_ratio, DEFAULT_ASPECT_RATIO);
        } finally {
            ta.recycle();
        }
        inflate(context, R.layout.banner_layout, this);
        mPager = (CustomViewPager) findViewById(R.id.pager);
        mIndicator = (DotAdIndicator) findViewById(R.id.indicator);
        mPager.addOnPageChangeListener(this);

        mImageView = new ResizableImageView(context);
        mImageView.setScaleType(ScaleType.FIT_XY);
        mImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mImageView.setImageResource(mResourceId);
        addView(mImageView);
    }

    @SuppressWarnings("unused")
    public void setVerticalInterferenceView(ViewParent vertical_interference_view) {
        mPager.setVerticalInterferenceView(vertical_interference_view);
    }

    /**
     * 设置广告列表，并从列表中第一张开始显示。
     *
     * @param list 广告列表
     */
    public void setAdList(List<AdEntity> list) {
        mAdList = list;
        int size = mAdList == null ? 0 : mAdList.size();
        mPager.setAdapter(new BillboardPagerAdapter());
        if (size > 0) {
            setVisibility(View.VISIBLE);
            removeView(mImageView);
            mPager.setCurrentItem(BillboardPagerAdapter.MAX_COUNT / 2 + size - (BillboardPagerAdapter.MAX_COUNT / 2) % size);
            mIndicator.notifyPageCountChanged(mPager.getCurrentItem(), size);
            startScrolling();
        } else {
            stopScrolling();
            setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 播放下一张广告，如果已经到了列表末尾，则回到头部重新播放。
     */
    public void showNext() {
        if (hasAdapter() && mPager.getAdapter().getCount() > 0) {
            final int item = (mPager.getCurrentItem() + 1) % mPager.getAdapter().getCount();
            mPager.setCurrentItem(item);
        }
    }

    /**
     * 播放上一张广告，如果已经到了列表开头，则切换到列表末尾。
     */
    @SuppressWarnings("unused")
    public void showPrevious() {
        if (hasAdapter() && mPager.getAdapter().getCount() > 0) {
            int item = (mPager.getCurrentItem() - 1 + mPager.getAdapter().getCount()) % mPager.getAdapter().getCount();
            mPager.setCurrentItem(item);
        }
    }

    /**
     * mPager是否有设定Adapter
     *
     * @return 有真无假
     */
    private boolean hasAdapter() {
        return mPager.getAdapter() != null;
    }

    /**
     * 得到当前正在显示的广告的坐标位置
     *
     * @return 当前广告位置
     */
    public int getCurrentItem() {
        return mPager.getCurrentItem();
    }

    /**
     * 设置页面状态变化时的回调。请参见{@link OnPageChangeListener}。
     *
     * @param listener 设定的回调
     */
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    /**
     * 自动切换到下一张广告所要等待的时间间隔（毫秒）
     *
     * @param milliseconds 时间间隔
     */
    public void setInterval(int milliseconds) {
        mScrollInterval = milliseconds;
    }

    public void startScrolling() {
        if (mScrollable) {
            mRunning = canRunning();
            if (mHandler != null) {
                mHandler.removeMessages(MSG_AUTO_SCROLL);
                if (mRunning) {
                    mHandler.sendEmptyMessageDelayed(MSG_AUTO_SCROLL, mScrollInterval);
                }
            }
        }
    }

    public boolean canRunning() {
        return getVisibility() == View.VISIBLE && mPager != null
                && mPager.getAdapter() != null
                && mPager.getAdapter().getCount() > 1;
    }

    public void stopScrolling() {
        mRunning = false;
        if (mHandler != null) {
            mHandler.removeMessages(MSG_AUTO_SCROLL);
        }
    }

    public void onResume() {
        startScrolling();
    }

    public void onPause() {
        stopScrolling();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * mAspectRatio);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressWarnings("unused")
    public void setAspectRatio(float ratio) {
        mAspectRatio = ratio;
        requestLayout();
    }

    /**
     * 设置当广告被点击时的回调
     *
     * @param listener 回调
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * 当子页面被点击时回调的接口定义。
     */
    public interface OnItemClickListener {
        /**
         * 当子页面被点击时调用
         *
         * @param position 被点击的页面的索引值
         */
        void onItemClick(int position);
    }

    @Override
    public void onClick(View v) {
        int index = mPager.getCurrentItem() % mAdList.size();
        AdEntity adEntity = mAdList.get(index);
        adEntity.responseClick();
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(index);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mIndicator.onPageScrollStateChanged(state);
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
        switch (state) {
            case ViewPager.SCROLL_STATE_DRAGGING:
                stopScrolling();
                break;
            default:
                if (!mRunning) {
                    startScrolling();
                }
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mIndicator.onPageSelected(position);
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
    }

    private class BillboardPagerAdapter extends PagerAdapter {

        final static int MAX_COUNT = 2000;

        @Override
        public int getCount() {
            return mAdList == null ? 0 : (mAdList.size() > 1 ? MAX_COUNT : mAdList.size());
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public ImageView instantiateItem(ViewGroup container, int position) {
            position = position % mAdList.size();
            AdEntity e = mAdList.get(position);
            ResizableImageView img = new ResizableImageView(getContext());
            img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            if (!TextUtils.isEmpty(e.getUrl())) {
                Glide.with(getContext()).load(e.getUrl()).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).dontAnimate()
                        .placeholder(mResourceId).error(mResourceId).into(img);
            }
            img.setOnClickListener(BannerView.this);

            container.addView(img);

            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startScrolling();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopScrolling();
    }

}
