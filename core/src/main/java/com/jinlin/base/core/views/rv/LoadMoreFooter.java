package com.jinlin.base.core.views.rv;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


public class LoadMoreFooter extends LinearLayout {

    public final static int STATE_LOADING = 0;
    public final static int STATE_COMPLETE = 1;
    public final static int STATE_NOMORE = 2;
    public final static int STATE_ERROR = 3;
    private TextView mText;
    private ProgressBar progressView;

    public LoadMoreFooter(Context context) {
        super(context);
        initView();
    }

    public LoadMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void initView() {
        setGravity(Gravity.CENTER);
        setPadding(0, 10, 0, 10);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        progressView = new ProgressBar(this.getContext());
        int size = dp2px(12);
        progressView.setLayoutParams(new ViewGroup.LayoutParams(size, size));

        addView(progressView);
        mText = new TextView(getContext());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(size, 0, 0, 0);
        mText.setLayoutParams(layoutParams);
        addView(mText);
    }

    public void setState(int state) {
        switch (state) {
            case STATE_LOADING:
                progressView.setVisibility(View.VISIBLE);
                mText.setText("加载中...");
                this.setVisibility(View.VISIBLE);
                break;
            case STATE_COMPLETE:
                mText.setText("加载完成");
                this.setVisibility(View.VISIBLE);
                break;
            case STATE_NOMORE:
                mText.setText("木有了");
                progressView.setVisibility(View.GONE);
                this.setVisibility(View.VISIBLE);
                break;
            case STATE_ERROR:

                break;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public void setStateText(String text) {
        if (!TextUtils.isEmpty(text)) {
            mText.setText(text);
        }
    }
}
