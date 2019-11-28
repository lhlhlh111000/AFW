package com.lease.fw.ui.view;
/**
 *
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.lease.framework.core.StringUtils;
import com.lease.fw.ui.R;


/**
 */
public class TipBubble extends AppCompatTextView {

    private CharSequence actualText;
    private CharSequence shownText;

    private int maxLength;
    /**
     * 如果超过指定maxLength长度，则使用掩码替换，必须指定maxLength才能使用此功能
     */
    private String maxLengthMask;
    // private ImageView mImgBg;
    private Animation mStartAnimation;
    private Animation mEndAnimation;

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public TipBubble(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.tip_bubble, defStyle, 0);
        maxLength = ta.getInt(R.styleable.tip_bubble_maxLength, 0);
        maxLengthMask = ta.getString(R.styleable.tip_bubble_maxLengthMask);
        ta.recycle();

        if (StringUtils.isNotBlank(maxLengthMask) && maxLength <= 0) {
            throw new IllegalArgumentException(
                    "使用maxLengthMask属性时，必须指定maxLength属性为大于0的值");
        }

        setSingleLine(true);
        setEllipsize(TextUtils.TruncateAt.END);
        setGravity(Gravity.CENTER);

        setBackgroundResource(R.mipmap.apk_all_newsbg);
    }

    /**
     * @param context
     * @param attrs
     */
    public TipBubble(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * @param context
     */
    public TipBubble(Context context) {
        this(context, null);
    }

    @Override
    public CharSequence getText() {
        return actualText;
    }

    public CharSequence getShownText() {
        return shownText;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        this.actualText = text == null ? "" : StringUtils.trimToEmpty(text.toString());
        this.shownText = actualText;
        if (maxLength > 0 && this.actualText.length() > maxLength) {
            if (StringUtils.isBlank(maxLengthMask)) {
                // InputFilter[] inputFilters = {new
                // InputFilter.LengthFilter(maxLength)};
                // mTxtMsg.setFilters(inputFilters);
                this.shownText = this.actualText.subSequence(0, maxLength);
            } else {
                this.shownText = maxLengthMask;
            }
        }
        super.setText(shownText, type);
    }


    public void startShowAnimation() {
        if (mStartAnimation == null) {
            mStartAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.tip_bubble_show);
        }
        startAnimation(mStartAnimation);
    }

    public void startHideAnimation() {
        if (mEndAnimation == null) {
            mEndAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.tip_bubble_hide);
        }
        startAnimation(mEndAnimation);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }


    /**
     * 设置未读数字
     * @param unread
     */
    public void setUnread(int unread){
        if(unread <=0){
            this.hide();
        }else {
            super.setText(unread + "");
            int resId = R.mipmap.apk_all_newsbg;
            if(unread >10){
                resId=R.mipmap.apk_all_newsbigbg;
            }
            setBackgroundResource(resId);
            this.show();
        }
    }
    /**
     * 仅仅设置小红点 无精确数字
     */
    public void showWithoutCount(int unread){
        if(unread <=0){
            this.hide();
        }else {
            this.setBackgroundResource(R.mipmap.ic_new_msg);
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, 1.0f);
            this.show();
        }
    }

    /**
     * 显示New的样式
     */
    public void showNew(int unread){
        if(unread <=0){
            this.hide();
        }else {
            this.setBackgroundResource(R.mipmap.ic_new_bg);
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, 1.0f);
            this.show();
        }
    }
}
