package com.common.testhistorysearch.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.AttributeSet;

import com.common.testhistorysearch.R;

/**
 * Created by zhouzhenhua on 2018/5/24.
 */

public class FakeBoldTextView extends AppCompatTextView {

    private static final int BOLD_STYLE_NORMAL = 1;
    private static final int BOLD_STYLE_MEDIUM = 2;
    private static final int BOLD_STYLE_BOLD = 3;

    private static final float BOLD_SIZE_NORMAL = 1.0f;
    private static final float BOLD_SIZE_MEDIUM = 1.3f;
    private static final float BOLD_SIZE_BOLD = 1.5f;

    private CharSequence fullText;        // 原始文本
    private boolean programmaticChange;
    private boolean isStale;
    private float boldSize = BOLD_SIZE_NORMAL;

    public FakeBoldTextView(Context context) {
        this(context, null);
    }

    public FakeBoldTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FakeBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FakeBoldTextView);
            int boldStyle = array.getInteger(R.styleable.FakeBoldTextView_boldStyle, BOLD_STYLE_NORMAL);
            boldSize = (boldStyle == BOLD_STYLE_MEDIUM ? BOLD_SIZE_MEDIUM
                    : (boldStyle == BOLD_STYLE_BOLD ? BOLD_SIZE_BOLD
                    : BOLD_SIZE_NORMAL));
            array.recycle();
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (!programmaticChange) {
            fullText = text;    // 拿到原始文本
            isStale = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isStale) {
            super.setEllipsize(null);
            resetText();
        }
        super.onDraw(canvas);
    }

    private void resetText() {
        if (TextUtils.isEmpty(fullText)) {
            return;
        }
        CharSequence workingText = fullText;
        if (!workingText.equals(getText())) {
            programmaticChange = true;
            try {
                setTextStyle(workingText);
            } finally {
                programmaticChange = false;
            }
        } else {
            setTextStyle(workingText);
        }
        isStale = false;
    }

    private void setTextStyle(CharSequence text) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);  // 用于可变字符串
        spannable.setSpan(new CharSpannable(), 0, getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.setText(spannable);
    }

    private class CharSpannable extends CharacterStyle {
        @Override
        public void updateDrawState(TextPaint tp) {
            tp.setStyle(Paint.Style.FILL_AND_STROKE);
            tp.setStrokeWidth(boldSize);
        }
    }
}
