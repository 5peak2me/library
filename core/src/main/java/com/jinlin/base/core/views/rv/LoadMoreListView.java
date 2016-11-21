package com.jinlin.base.core.views.rv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;


/**
 * Created by J!nl!n on 2016/8/24 11:37.
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
 * 动态刷新和加载数据ListView
 */
public class LoadMoreListView extends ListView implements OnScrollListener {

    private static final int STATUS_NORMAL = 0x01;
    private static final int STATUS_EMPTY = 0x02;
    private static final int STATUS_LOADING = 0x03;
    private static final int STATUS_ERROR = 0x04;

    private int mStatus = STATUS_NORMAL;

    private LoadMoreFooter mLoadMoreFooter;

    public LoadMoreListView(Context context) {
        this(context, null);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.listViewStyle);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mLoadMoreFooter = new LoadMoreFooter(context);
        mLoadMoreFooter.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addFooterView(mLoadMoreFooter, null, false);
        setOnScrollListener(this);
        mLoadMoreFooter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadMoreFooter.setState(LoadMoreFooter.STATE_LOADING);
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.onLoadMore();
                }
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
            // 没有更多数据或者正在刷新状态
            if (mStatus == STATUS_EMPTY || mStatus == STATUS_LOADING || mStatus == STATUS_ERROR) {
                return;
            }
            //只有在正常状态下才会执行刷新数据
            if (mStatus == STATUS_NORMAL) {
                loadMore();
            }
        }
    }

    /**
     * 开始加载更多
     */
    private void loadMore() {
        if (this.onLoadMoreListener != null) {
            mStatus = STATUS_LOADING;
            mLoadMoreFooter.setState(LoadMoreFooter.STATE_LOADING);
            onLoadMoreListener.onLoadMore();
        }
    }

    /**
     * 加载更多完成之后调用
     */
    public void loadCompelete() {
        mStatus = STATUS_NORMAL;
        mLoadMoreFooter.setState(LoadMoreFooter.STATE_COMPLETE);
    }

    /**
     * 无更多数据
     */
    public void noMoreData() {
        this.mStatus = STATUS_EMPTY;
        mLoadMoreFooter.setState(LoadMoreFooter.STATE_NOMORE);
    }

    /**
     * 激活手动点击加载更多功能
     */
    public void retry() {
        this.mStatus = STATUS_ERROR;
        mLoadMoreFooter.setState(LoadMoreFooter.STATE_ERROR);
    }

    /**
     * 监听器
     * 监听控件的刷新或者加载更多事件
     * 所有的条目事件都会有一个偏移量，也就是position应该减1才是你适配器中的条目
     */
    public interface OnLoadMoreListener {
        /**
         * 加载更多
         */
        void onLoadMore();

    }

    private OnLoadMoreListener onLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

}