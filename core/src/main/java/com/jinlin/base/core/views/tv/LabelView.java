package com.jinlin.base.core.views.tv;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.jinlin.base.core.R;

/**
 * Created by J!nl!n on 2016/6/12 17:08.
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
public class LabelView extends TextView {

    private CharSequence mLeftText, mTopText, mRightText, mBottomText;
    private int mLeftTextAppearance, mTopTextAppearance, mRightTextAppearance, mBottomTextAppearance;
    private CharSequence mText;

    public LabelView(Context context) {
        this(context, null);
    }

    public LabelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LabelView);
        try {
            mLeftText = a.getText(R.styleable.LabelView_leftText);
            mTopText = a.getText(R.styleable.LabelView_topText);
            mRightText = a.getText(R.styleable.LabelView_rightText);
            mBottomText = a.getText(R.styleable.LabelView_bottomText);
            mLeftTextAppearance = a.getResourceId(R.styleable.LabelView_leftTextAppearance, 0);
            mTopTextAppearance = a.getResourceId(R.styleable.LabelView_topTextAppearance, 0);
            mRightTextAppearance = a.getResourceId(R.styleable.LabelView_rightTextAppearance, 0);
            mBottomTextAppearance = a.getResourceId(R.styleable.LabelView_bottomTextAppearance, 0);
            int gravity = a.getInt(R.styleable.LabelView_android_gravity, Gravity.CENTER);
            setGravity(gravity);
            setText(super.getText());
        } finally {
            a.recycle();
        }
    }

    @Override
    public void setText(CharSequence mainText, BufferType type) {
        super.setText(mainText, type);
        mText = mainText;
        CharSequence text = mainText;
        if (!TextUtils.isEmpty(mLeftText)) {
            text = buildTextLeft(mLeftText.toString(), text, mLeftTextAppearance);
        }
        if (!TextUtils.isEmpty(mLeftText)) {
            text = buildTextRight(text, mRightText.toString(), mRightTextAppearance);
        }
        if (!TextUtils.isEmpty(mTopText)) {
            text = new SpannableStringBuilder("\n").append(text);
            text = buildTextLeft(mTopText.toString(), text, mTopTextAppearance);
        }
        if (!TextUtils.isEmpty(mBottomText)) {
            text = new SpannableStringBuilder(text).append("\n");
            text = buildTextRight(text, mBottomText.toString(), mBottomTextAppearance);
        }
        if (!TextUtils.isEmpty(text)) {
            super.setText(text, type);
        }
    }

    private CharSequence buildTextLeft(CharSequence head, CharSequence foot, int style) {
        SpannableString leftText = format(getContext(), head, style);
        SpannableStringBuilder builder = new SpannableStringBuilder(leftText).append(foot);
        return builder.subSequence(0, builder.length());
    }

    private CharSequence buildTextRight(CharSequence head, CharSequence foot, int style) {
        SpannableString rightText = format(getContext(), foot, style);
        SpannableStringBuilder builder = new SpannableStringBuilder(head).append(rightText);
        return builder.subSequence(0, builder.length());
    }

    public SpannableString format(Context context, CharSequence text, int style) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new TextAppearanceSpan(context, style), 0, text.length(), 0);
        return spannableString;
    }

    @Override
    public CharSequence getText() {
        return mText;
    }

}
