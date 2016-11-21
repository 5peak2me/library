package com.jinlin.base.core.views.rv;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class LoadMoreRecyclerView extends RecyclerView {

    private boolean isLoadingData = false;
    private boolean isNoMore = false;
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFootViews = new ArrayList<>();
    private Adapter mWrapAdapter;
    private LoadingListener mLoadingListener;
    private boolean loadingMoreEnabled = true;
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_HEADER = -1;
    private static final int TYPE_FOOTER = -2;
    // adapter没有数据的时候显示,类似于listView的emptyView
    private View mEmptyView;
    private final AdapterDataObserver mDataObserver = new DataObserver();

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LoadMoreFooter footView = new LoadMoreFooter(getContext());
        addFootView(footView);
        mFootViews.get(0).setVisibility(GONE);
    }

    public void addHeaderView(View view) {
        mHeaderViews.add(view);
    }

    public void removeHeadView(View view) {
        mHeaderViews.remove(view);
    }

    public boolean hasHeadView(View view) {
        return mHeaderViews.contains(view);
    }

    public void addFootView(final View view) {
        mFootViews.clear();
        mFootViews.add(view);
    }

    public void loadMoreComplete() {
        isLoadingData = false;
        View footView = mFootViews.get(0);
        if (footView instanceof LoadMoreFooter) {
            ((LoadMoreFooter) footView).setState(LoadMoreFooter.STATE_COMPLETE);
        } else {
            footView.setVisibility(View.GONE);
        }
    }

    public void setNoMore(boolean noMore) {
        this.isNoMore = noMore;
        View footView = mFootViews.get(0);
        ((LoadMoreFooter) footView).setState(this.isNoMore ? LoadMoreFooter.STATE_NOMORE : LoadMoreFooter.STATE_COMPLETE);
    }

    public void reset() {
        setNoMore(false);
        loadMoreComplete();
    }

    public void noMoreLoading() {
        isLoadingData = false;
        View footView = mFootViews.get(0);
        isNoMore = true;
        if (footView instanceof LoadMoreFooter) {
            ((LoadMoreFooter) footView).setState(LoadMoreFooter.STATE_NOMORE);
        } else {
            footView.setVisibility(View.GONE);
        }
    }

    public void setLoadingMoreEnabled(boolean enabled) {
        loadingMoreEnabled = enabled;
        if (!enabled && mFootViews.size() > 0) {
            mFootViews.get(0).setVisibility(GONE);
        }
    }

    public void setStateText(String text){
        View footView = mFootViews.get(0);
        ((LoadMoreFooter) footView).setStateText(text);
    }

    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        mDataObserver.onChanged();
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);

        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadingListener != null && !isLoadingData && loadingMoreEnabled) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1 && layoutManager.getItemCount() > layoutManager.getChildCount() && !isNoMore) {

                View footView = mFootViews.get(0);
                isLoadingData = true;
                if (footView instanceof LoadMoreFooter) {
                    ((LoadMoreFooter) footView).setState(LoadMoreFooter.STATE_LOADING);
                } else {
                    footView.setVisibility(View.VISIBLE);
                }
                mLoadingListener.onLoadMore();
            }
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private int findMin(int[] firstPositions) {
        int min = firstPositions[0];
        for (int value : firstPositions) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    private boolean isOnTop() {
        return !(mHeaderViews == null || mHeaderViews.isEmpty()) && mHeaderViews.get(0).getParent() != null;
    }

    private class DataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();
            if (adapter != null && mEmptyView != null) {
                int emptyCount = 0;
                if (loadingMoreEnabled) {
                    emptyCount++;
                }
                if (adapter.getItemCount() == emptyCount) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    LoadMoreRecyclerView.this.setVisibility(View.GONE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                    LoadMoreRecyclerView.this.setVisibility(View.VISIBLE);
                }
            }
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    public class WrapAdapter extends Adapter<ViewHolder> {

        private Adapter adapter;

        private int currentPosition = 0;

        public WrapAdapter(Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isFooter(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }

        public boolean isHeader(int position) {
            return position >= 0 && position < mHeaderViews.size();
        }

        public boolean isFooter(int position) {
            return position < getItemCount() && position >= getItemCount() - mFootViews.size();
        }

        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        public int getFootersCount() {
            return mFootViews.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                ViewHolder viewHolder = new SimpleViewHolder(mHeaderViews.get(currentPosition));
                // fix multiple headerView disorder
                if (mHeaderViews.size() > 0) {
                    viewHolder.setIsRecyclable(false);
                }
                return viewHolder;
            } else if (viewType == TYPE_FOOTER) {
                return new SimpleViewHolder(mFootViews.get(0));
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (isHeader(position)) {
                return;
            }
            int adjPosition = position - getHeadersCount();
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    adapter.onBindViewHolder(holder, adjPosition);
                    return;
                }
            }
        }

        @Override
        public int getItemCount() {
            if (adapter != null) {
                return getHeadersCount() + getFootersCount() + adapter.getItemCount();
            } else {
                return getHeadersCount() + getFootersCount();
            }
        }

        @Override
        public int getItemViewType(int position) {
            currentPosition = position;
            if (isHeader(position)) {
                return TYPE_HEADER;
            }
            if (isFooter(position)) {
                return TYPE_FOOTER;
            }
            int adjPosition = position - getHeadersCount();
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemViewType(adjPosition);
                }
            }
            return TYPE_NORMAL;
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= getHeadersCount()) {
                int adjPosition = position - getHeadersCount();
                int adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            if (adapter != null) {
                adapter.unregisterAdapterDataObserver(observer);
            }
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            if (adapter != null) {
                adapter.registerAdapterDataObserver(observer);
            }
        }

        private class SimpleViewHolder extends ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }

    public interface LoadingListener {
        void onLoadMore();

        void onScroll(int dx, int dy);

        void onScrollUp();

        void onScrollDown();
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView parent, View view, int position);
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    public interface OnItemLongClickListener {
        boolean onItemLongClick(RecyclerView parent, View view, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        mOnItemLongClickListener = listener;
    }

    /**
     * The listener that receives notifications when an item is clicked.
     */
    OnItemClickListener mOnItemClickListener;

    /**
     * The listener that receives notifications when an item is long clicked.
     */
    OnItemLongClickListener mOnItemLongClickListener;

    private RecyclerViewClickLister mRecyclerViewClickLister = new RecyclerViewClickLister();

    private class RecyclerViewClickLister implements OnItemTouchListener {
        private GestureDetector mGestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childView = findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mOnItemClickListener != null) {
                    if (!mHeaderViews.contains(childView)) {
                        mOnItemClickListener.onItemClick(LoadMoreRecyclerView.this, childView, getChildLayoutPosition(childView) - mHeaderViews.size());
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childView = findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mOnItemLongClickListener != null) {
                    if (!mHeaderViews.contains(childView)) {
                        mOnItemLongClickListener.onItemLongClick(LoadMoreRecyclerView.this, childView, getChildLayoutPosition(childView) - mHeaderViews.size());
                    }
                }
            }
        });

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            return mGestureDetector.onTouchEvent(e);
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeOnItemTouchListener(mRecyclerViewClickLister);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addOnItemTouchListener(mRecyclerViewClickLister);
    }

    private int scrollY;
    private boolean scrollUp = false;

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (getChildCount() > 0) {
            scrollY += dy;
            if (mLoadingListener != null) {
                mLoadingListener.onScroll(dx, dy);
                if (dy < -3) {
                    if (!scrollUp) {
                        mLoadingListener.onScrollUp();
                        scrollUp = true;
                    }
                } else if (dy > 3) {
                    if (scrollUp) {
                        mLoadingListener.onScrollDown();
                        scrollUp = false;
                    }
                }
            }
        }
    }

    public int scrollY() {
        return scrollY;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        return super.fling(velocityX, (int) (velocityY * 0.8f));
    }
}