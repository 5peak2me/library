package com.jinlin.base.core.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.util.SparseArrayCompat;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jinlin.base.core.glide.transform.RoundedCornersTransformation;

/**
 * Created by J!nl!n on 15/10/19.
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
public class ViewHolder {
    private final SparseArrayCompat<View> mViews;
    private final View view;
    private final Context mContext;
    private int mPosition;
    private int mLayoutId;
    /**
     * Package private field to retain the associated user object and detect a change
     */
    private Object mAssociatedObject;

    private ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mContext = context;
        this.mPosition = position;
        this.mLayoutId = layoutId;
        this.mViews = new SparseArrayCompat<>();
        view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        // setTag
        view.setTag(this);
    }

    /**
     * 根据id获取控件
     *
     * @param viewId 资源ID
     * @param <T>    泛型
     * @return 控件
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View childView = mViews.get(viewId);
        if (childView == null) {
            childView = view.findViewById(viewId);
            mViews.put(viewId, childView);
        }
        return (T) childView;
    }

    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId) {
        return get(context, convertView, parent, layoutId, -1);
    }

    /**
     * 拿到一个ViewHolder对象
     *
     * @param context     上下文对象
     * @param convertView 缓存的View
     * @param parent      父控件
     * @param layoutId    视图ID
     * @param position    当前位置
     * @return
     */
    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        }

        // Retrieve the existing helper and update its position
        ViewHolder existingHelper = (ViewHolder) convertView.getTag();

        if (existingHelper.mLayoutId != layoutId) {
            return new ViewHolder(context, parent, layoutId, position);
        }

        existingHelper.mPosition = position;
        return existingHelper;
    }

    public View getConvertView() {
        return view;
    }

    public void setMaxLines(int viewId, int line) {
        TextView view = getView(viewId);
        view.setMaxLines(line);
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param url
     * @return
     */
    public ViewHolder setImageByUrl(int viewId, String url) {
        ImageView iv = getView(viewId);
        Glide.with(mContext).load(url).dontAnimate().into(iv);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param url
     * @return
     */
    public ViewHolder setImageByUrl(int viewId, int defaultResId, String url) {
        ImageView iv = getView(viewId);
        Glide.with(mContext).load(url)
                .placeholder(defaultResId).error(defaultResId).dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESULT).into(iv);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param url
     * @return
     */
    public ViewHolder setRoundedImageByUrl(int viewId, int defaultResId, String url) {
        final ImageView iv = getView(viewId);
        Glide.with(mContext).load(url).placeholder(defaultResId).error(defaultResId).diskCacheStrategy(DiskCacheStrategy.RESULT)
                .bitmapTransform(new RoundedCornersTransformation(mContext, 20, 0, RoundedCornersTransformation.CornerType.ALL)).into(iv);
        return this;
    }

    /**
     * 为View设置监听
     *
     * @param viewId
     * @param listener
     * @return
     */
    public ViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View v = getView(viewId);
        v.setOnClickListener(listener);
        return this;
    }

    public int getPosition() {
        if (mPosition == -1)
            throw new IllegalStateException("Use ViewHolder constructor " +
                    "with mPosition if you need to retrieve the mPosition.");
        return mPosition;
    }

    /**
     * Set a view visibility to VISIBLE (true) or GONE (false).
     *
     * @param viewId  The view id.
     * @param visible True for VISIBLE, false for GONE.
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * Will set background color of a view.
     *
     * @param viewId The view id.
     * @param color  A color, not a resource id.
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    /**
     * Will set background of a view.
     *
     * @param viewId        The view id.
     * @param backgroundRes A resource to use as a background.
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setBackgroundRes(int viewId, int backgroundRes) {
        View view = getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    /**
     * Will set text color of a TextView.
     *
     * @param viewId    The view id.
     * @param textColor The text color (not a resource id).
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setTextColor(int viewId, int textColor) {
        TextView view = getView(viewId);
        view.setTextColor(textColor);
        return this;
    }

    /**
     * Will set text color of a TextView.
     *
     * @param viewId       The view id.
     * @param textColorRes The text color resource id.
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setTextColorRes(int viewId, int textColorRes) {
        TextView view = getView(viewId);
        view.setTextColor(mContext.getResources().getColor(textColorRes));
        return this;
    }

    /**
     * Will set the image of an ImageView from a drawable.
     *
     * @param viewId   The view id.
     * @param drawable The image drawable.
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
        if (null != drawable) {
            ImageView view = getView(viewId);
            view.setImageDrawable(drawable);
        }
        return this;
    }

    /**
     * Add an action to set the image of an image view. Can be called multiple times.
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        if (null != bitmap) {
            ImageView view = getView(viewId);
            view.setImageBitmap(bitmap);
        }
        return this;
    }

    /**
     * Add an action to set the alpha of a view. Can be called multiple times.
     * Alpha between 0-1.
     */
    public ViewHolder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView(viewId).setAlpha(value);
        } else {
            // Pre-honeycomb hack to set Alpha value
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            getView(viewId).startAnimation(alpha);
        }
        return this;
    }

    /**
     * Add links into a TextView.
     *
     * @param viewId The id of the TextView to linkify.
     * @return The BaseAdapterHelper for chaining.
     */
    public ViewHolder linkify(int viewId) {
        TextView view = getView(viewId);
        Linkify.addLinks(view, Linkify.ALL);
        return this;
    }

    /**
     * Add links into a TextView.
     *
     * @param viewId The id of the TextView to linkify.
     * @param mask
     * @return The ViewHolder for chaining.
     * @see Linkify#addLinks(TextView text, int mask)
     */
    public ViewHolder addLinks(int viewId, int mask) {
        TextView view = getView(viewId);
        Linkify.addLinks(view, mask);
        return this;
    }

    /**
     * Apply the typeface to the given viewId, and enable subpixel rendering.
     */
    public ViewHolder setTypeface(int viewId, Typeface typeface) {
        TextView view = getView(viewId);
        view.setTypeface(typeface);
        view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        return this;
    }

    /**
     * Apply the typeface to all the given viewIds, and enable subpixel rendering.
     */
    public ViewHolder setTypeface(Typeface typeface, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = getView(viewId);
            view.setTypeface(typeface);
            view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
        return this;
    }

    /**
     * Sets the progress of a ProgressBar.
     *
     * @param viewId   The view id.
     * @param progress The progress.
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setProgress(int viewId, int progress) {
        ProgressBar view = getView(viewId);
        view.setProgress(progress);
        return this;
    }

    /**
     * Sets the progress and max of a ProgressBar.
     *
     * @param viewId   The view id.
     * @param progress The progress.
     * @param max      The max value of a ProgressBar.
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setProgress(int viewId, int progress, int max) {
        ProgressBar view = getView(viewId);
        view.setMax(max);
        view.setProgress(progress);
        return this;
    }

    /**
     * Sets the range of a ProgressBar to 0...max.
     *
     * @param viewId The view id.
     * @param max    The max value of a ProgressBar.
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setMax(int viewId, int max) {
        ProgressBar view = getView(viewId);
        view.setMax(max);
        return this;
    }

    /**
     * Sets the rating (the number of stars filled) of a RatingBar.
     *
     * @param viewId The view id.
     * @param rating The rating.
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setRating(int viewId, float rating) {
        RatingBar view = getView(viewId);
        view.setRating(rating);
        return this;
    }

    /**
     * Sets the rating (the number of stars filled) and max of a RatingBar.
     *
     * @param viewId The view id.
     * @param rating The rating.
     * @param max    The range of the RatingBar to 0...max.
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setRating(int viewId, float rating, int max) {
        RatingBar view = getView(viewId);
        view.setMax(max);
        view.setRating(rating);
        return this;
    }

    /**
     * Sets the on touch listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The on touch listener;
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
        View view = getView(viewId);
        view.setOnTouchListener(listener);
        return this;
    }

    /**
     * Sets the on long click listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The on long click listener;
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        View view = getView(viewId);
        view.setOnLongClickListener(listener);
        return this;
    }

    /**
     * Sets the listview or gridview's item click listener of the view
     *
     * @param viewId   The view id.
     * @param listener The item on click listener;
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setOnItemClickListener(int viewId, AdapterView.OnItemClickListener listener) {
        AdapterView view = getView(viewId);
        view.setOnItemClickListener(listener);
        return this;
    }

    /**
     * Sets the listview or gridview's item long click listener of the view
     *
     * @param viewId   The view id.
     * @param listener The item long click listener;
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setOnItemLongClickListener(int viewId, AdapterView.OnItemLongClickListener listener) {
        AdapterView view = getView(viewId);
        view.setOnItemLongClickListener(listener);
        return this;
    }

    /**
     * Sets the listview or gridview's item selected click listener of the view
     *
     * @param viewId   The view id.
     * @param listener The item selected click listener;
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setOnItemSelectedClickListener(int viewId, AdapterView.OnItemSelectedListener listener) {
        AdapterView view = getView(viewId);
        view.setOnItemSelectedListener(listener);
        return this;
    }

    /**
     * Sets the tag of the view.
     *
     * @param viewId The view id.
     * @param tag    The tag;
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setTag(int viewId, Object tag) {
        View view = getView(viewId);
        view.setTag(tag);
        return this;
    }

    /**
     * Sets the tag of the view.
     *
     * @param viewId The view id.
     * @param key    The key of tag;
     * @param tag    The tag;
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setTag(int viewId, int key, Object tag) {
        if (null != tag) {
            View view = getView(viewId);
            view.setTag(key, tag);
        }
        return this;
    }

    /**
     * Sets the checked status of a checkable.
     *
     * @param viewId  The view id.
     * @param checked The checked status;
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setChecked(int viewId, boolean checked) {
        Checkable view = getView(viewId);
        view.setChecked(checked);
        return this;
    }

    /**
     * Sets the adapter of a adapter view.
     *
     * @param viewId  The view id.
     * @param adapter The adapter;
     * @return The ViewHolder for chaining.
     */
    public ViewHolder setAdapter(int viewId, Adapter adapter) {
        if (null != adapter) {
            AdapterView view = getView(viewId);
            view.setAdapter(adapter);
        }
        return this;
    }


    /**
     * Retrieves the last converted object on this view.
     */
    public Object getAssociatedObject() {
        return mAssociatedObject;
    }

    /**
     * Should be called during convert
     */
    public void setAssociatedObject(Object associatedObject) {
        this.mAssociatedObject = associatedObject;
    }

    private String replaceEmpty(String params) {
        return TextUtils.isEmpty(params) ? "" : params;
    }
}
