package com.jinlin.base.core.views.et;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.jinlin.base.core.R;


//带删除的ET update by Jinlin 修改支持焦点指示图
public class ClearEditText extends EditText implements
        OnFocusChangeListener, TextWatcher {

    private Drawable mClearDrawable;

    private boolean hasFoucs;

    public void setIsshowClearIcon(boolean isshowClearIcon) {
        this.isshowClearIcon = isshowClearIcon;
    }

    private boolean isshowClearIcon = true;

    private Drawable mFocusDrawable;
    private Drawable mUnFocusDrawable;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText);
        try {
            mFocusDrawable = ta.getDrawable(R.styleable.ClearEditText_focusImage);
            mUnFocusDrawable = ta.getDrawable(R.styleable.ClearEditText_unfocusImage);
        } finally {
            ta.recycle();
        }

        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
        	throw new NullPointerException("You can add drawableRight attribute in XML");
        }
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        setClearIconVisible(false);
        setCheckedResourse(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {

                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));

                if (touchable) {
                    this.setText("");
                }
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFoucs = hasFocus;
        if (hasFocus && isshowClearIcon) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }

    public void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    private void setCheckedResourse(boolean checked) {
        Drawable left = checked ? mFocusDrawable : mUnFocusDrawable;
        setCompoundDrawablesWithIntrinsicBounds(left,
                getCompoundDrawables()[1], getCompoundDrawables()[2], getCompoundDrawables()[3]);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if (hasFoucs) {
            setClearIconVisible(s.length() > 0);
        }
        setCheckedResourse(s.length() > 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public void setShakeAnimation() {
        this.setAnimation(shakeAnimation(5));
    }

    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

}
