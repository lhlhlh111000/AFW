package com.lease.fw.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.lease.fw.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动滚动TextView
 * Created by zlh on 2017/1/10.
 */
public class AutoScrollTextView extends View implements View.OnClickListener {

    private final static int MAX_STEP = 10;

    private List<String> list = new ArrayList<>();

    private String currentStr;
    private String nextStr;

    private int currentPosition;

    private int speed;

    private Paint paint = new Paint();

    private OnTextClickListener listener;

    private boolean isAutoScroll;

    private Handler handler;

    public AutoScrollTextView(Context context) {
        this(context, null);
    }

    public AutoScrollTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if(null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoScrollTextView);
            int textSize = a.getDimensionPixelSize(R.styleable.AutoScrollTextView_auto_textSize, 14);
            int textColor = a.getColor(R.styleable.AutoScrollTextView_auto_textColor, Color.BLACK);
            paint.setTextSize(textSize);
            paint.setColor(textColor);
            a.recycle();
        }

        setClickable(true);
        setOnClickListener(this);

        paint.setAntiAlias(true);
        handler = new Handler();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(0 == list.size()) {
            return;
        }
        super.onMeasure(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(0 == list.size()) {
            return;
        }

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        int textHeight = getFontHeight();
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int marginMid = (height - textHeight)/2;
        int startY = marginMid + textHeight - getPaddingTop()/2;

        // 文本绘制
        int offset = -(int) ((((float)(speed%MAX_STEP)/MAX_STEP) * startY));
        canvas.drawText(fitRealDrawText(currentStr, width - paddingLeft - paddingRight),
                paddingLeft, offset + startY, paint);
        canvas.drawText(fitRealDrawText(nextStr, width - paddingLeft - paddingRight),
                paddingLeft, offset + startY + marginMid + textHeight, paint);

        // 文本更新及选择位置
        if(MAX_STEP - 1 == speed % MAX_STEP) {
            currentPosition ++;
            initText();
        }

        // 滚动处理
        if(list.size() > 1) {
            if(!isAutoScroll) {
                return;
            }
            speed++;
            handler.removeCallbacks(runnable);
            if(1  == speed%MAX_STEP) {
                handler.postDelayed(runnable, 2000);
            }else {
                handler.postDelayed(runnable, 100);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(null == listener) {
            return;
        }

        this.listener.onClick(currentPosition, currentStr, nextStr);
    }

    /**
     * 计算宽度
     * @param widthMeasureSpec
     * @return
     */
    private int measureWidth (int widthMeasureSpec) {
        if(MeasureSpec.EXACTLY == MeasureSpec.getMode(widthMeasureSpec)) {
            return widthMeasureSpec;
        }

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        String maxLengthStr = "";
        for(String str : list) {
            if(maxLengthStr.length() < str.length()) {
                maxLengthStr = str;
            }
        }
        int maxTextWidth = px2dip(getContext(), paint.measureText(maxLengthStr));

        return MeasureSpec.makeMeasureSpec(
                Math.max(width, paddingLeft + maxTextWidth + paddingRight), MeasureSpec.EXACTLY);
    }

    /**
     * 计算高度
     * @param heightMeasureSpec
     * @return
     */
    private int measureHeight(int heightMeasureSpec) {
        if(MeasureSpec.EXACTLY == MeasureSpec.getMode(heightMeasureSpec)) {
            return heightMeasureSpec;
        }

        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int textHeight = getFontHeight();

        if(height == 0) {
            return MeasureSpec.makeMeasureSpec(textHeight + paddingTop + paddingBottom, MeasureSpec.EXACTLY);
        }

        return MeasureSpec.makeMeasureSpec(
                Math.min(height, textHeight + paddingTop + paddingBottom), MeasureSpec.EXACTLY);
    }

    private void initText() {
        currentStr = list.get(currentPosition%list.size());
        nextStr = list.get((currentPosition + 1)%list.size());
    }

    public int getFontHeight() {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    private int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取绘制文本
     * @param text
     * @return
     */
    private String fitRealDrawText(String text, int containerWidth) {
        float textWidth = paint.measureText(text);
        int position = (int) ((containerWidth/textWidth)*text.length()  - 1);
        if(position <= 0 || position >= text.length() - 1) {
            return text;
        }
        return text.substring(0, position).concat("…");
    }

    /**
     * 绑定数据
     * @param list 滚动文本
     */
    public void bindData(List<String> list) {
        this.bindData(list, 0);
    }

    /**
     * 数据绑定
     * @param list 滚动文本
     * @param position 初始位置
     */
    public void bindData(List<String> list, int position) {
        if(null == list || 0 == list.size()) {
            return;
        }

        this.list.clear();
        this.list.addAll(list);
        this.currentPosition = position;
        initText();
    }

    public synchronized void pause() {
        if(!isAutoScroll) {
            return;
        }

        isAutoScroll = !isAutoScroll;
        handler.removeCallbacks(runnable);
        handler.post(runnable);
    }

    public synchronized  void start() {
        if(isAutoScroll) {
            return;
        }

        isAutoScroll = !isAutoScroll;
        handler.removeCallbacks(runnable);
        handler.post(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    /**
     * 设置点击事件
     * @param listener
     */
    public void setTextClickListener(OnTextClickListener listener) {
        this.listener = listener;
    }

    public interface OnTextClickListener {

        void onClick(int position, String currentStr, String nextStr);
    }
}
