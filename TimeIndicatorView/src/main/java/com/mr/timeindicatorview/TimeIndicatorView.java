package com.mr.timeindicatorview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * @auther: pengwang
 * @date: 2022/5/13
 * @description: 时间指示器
 */
public class TimeIndicatorView extends View {

    private static final long TIME_OF_DAY = 24 * 60 * 60 * 1000;
    private static final int TIME_OF_HOURS = 60 * 60 * 1000;
    private static final int TIME_OF_MINUTE = 60 * 1000;

    public static final String YEARS = "yyyy";
    public static final String MONTH = "MM";
    public static final String DAY = "dd";
    public static final String HOURS = "HH";
    public static final String MINUTE = "mm";
    public static final String SECONDS = "ss";

    public static final String SUFFIX1 = "-";
    public static final String SUFFIX2 = ":";
    public static final String SUFFIX3 = "/";
    public static final String SUFFIX4 = ".";
    public static final String SUFFIX5 = "年";
    public static final String SUFFIX6 = "月";
    public static final String SUFFIX7 = "日";
    public static final String SUFFIX8 = "时";
    public static final String SUFFIX9 = "分";
    public static final String SUFFIX10 = "秒";
    public static final String SUFFIX11 = " ";

    public static final long DEF_MILLIS_IN_FUTURE = 1000;

    /**
     * 默认时间格式
     */
    private static final String DEF_DATA_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 模式指针背景颜色
     */
    private static final int DEF_POINTER_BACKGROUND_COLOR = Color.WHITE;

    /**
     * 默认指针字体颜色
     */
    private static final int DEF_TEXT_COLOR = Color.GRAY;

    /**
     * 默认字体大小
     */
    private static final int DEF_TEXT_SIZE = 12;

    /**
     * 默认字体样式
     */
    private static final int DEF_TEXT_STYLE = Typeface.NORMAL;

    /**
     * 默认指针圆角
     */
    private static final int DEF_POINTER_RADIUS = 0;

    /**
     * 默认指针重力控制
     */
    private static final int DEF_POINTER_GRAVITY = 1;

    /**
     * 是否为倒计时
     */
    private boolean isCountdown;

    /**
     * 时间格式
     */
    private String dataFormat;

    /**
     * 指针背景颜色
     */
    private int pointerBackgroundColor;

    /**
     * 指针文字颜色
     */
    private int pointerTextColor;

    /**
     * 指针文字大小
     */
    private int pointerTextSize;

    /**
     * 指针文字样式
     */
    private int pointerTextStyle;

    /**
     * 指针圆角大小
     */
    private int pointerRadius;

    /**
     * 指针宽
     */
    private int pointerWidth;

    /**
     * 指针高
     */
    private int pointerHeight;

    /**
     * 后缀文字颜色
     */
    private int suffixTextColor;

    /**
     * 后缀文字大小
     */
    private int suffixTextSize;

    /**
     * 后缀文字样式
     */
    private int suffixTextStyle;

    /**
     * 后缀距离左边距离
     */
    private int suffixMarginLeft;

    /**
     * 后缀距离右边距
     */
    private int suffixMarginRight;

    private long millisInFuture = DEF_MILLIS_IN_FUTURE;

    /**
     * 视图宽
     */
    private int mViewWidth;

    /**
     * 视图高
     */
    private int mViewHeight;

    private long startTime = 0;

    private List<FormatNode> formatNodes;

    private Paint mTimeTextPaint;

    private Paint mSuffixTextPaint;

    private Paint mTimePointerPaint;

    private RectF mPointerRectF;

    private Rect mMeasureRect;

    private List<NodeRect> nodeRectList;

    private CountDownTimer mCountDownTimer;

    public TimeIndicatorView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public TimeIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public TimeIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.TimeIndicator, defStyleAttr, 0);

        dataFormat = ta.getString(R.styleable.TimeIndicator_tiDataFormat);
        dataFormat = TextUtils.isEmpty(dataFormat) ? DEF_DATA_FORMAT : dataFormat;

        pointerBackgroundColor = ta.getColor(R.styleable.TimeIndicator_tiPointerBackgroundColor
                , DEF_POINTER_BACKGROUND_COLOR);
        pointerTextColor = ta.getColor(R.styleable.TimeIndicator_tiPointerTextColor, DEF_TEXT_COLOR);
        pointerTextSize = ta.getDimensionPixelSize(R.styleable.TimeIndicator_tiPointerTextSize
                , DEF_TEXT_SIZE);
        pointerTextStyle = ta.getInt(R.styleable.TimeIndicator_tiPointerTextStyle,
                DEF_TEXT_STYLE);
        pointerRadius = ta.getDimensionPixelSize(R.styleable.TimeIndicator_tiPointerRadius,
                DEF_POINTER_RADIUS);
        pointerWidth = ta.getDimensionPixelSize(R.styleable.TimeIndicator_tiPointerWidth
                , 28);
        pointerHeight = ta.getDimensionPixelSize(R.styleable.TimeIndicator_tiPointerHeight
                , 28);
        suffixTextColor = ta.getColor(R.styleable.TimeIndicator_tiSuffixTextColor, DEF_TEXT_COLOR);
        suffixTextSize = ta.getDimensionPixelSize(R.styleable.TimeIndicator_tiSuffixTextSize
                , DEF_TEXT_SIZE);
        suffixTextStyle = ta.getInt(R.styleable.TimeIndicator_tiSuffixTextStyle, DEF_TEXT_STYLE);
        suffixMarginLeft = ta.getDimensionPixelSize(R.styleable.TimeIndicator_tiSuffixMarginLeft
                , 0);
        suffixMarginRight = ta.getDimensionPixelSize(R.styleable.TimeIndicator_tiSuffixMarginRight
                , 0);

        nodeRectList = new ArrayList<>();

        mTimeTextPaint = new Paint();
        mSuffixTextPaint = new Paint();
        mTimePointerPaint = new Paint();

        mTimeTextPaint.setAntiAlias(true);
        mTimeTextPaint.setColor(pointerTextColor);
        mTimeTextPaint.setTextSize(pointerTextSize);
        setTypeface(mTimeTextPaint, pointerTextStyle);

        mSuffixTextPaint.setAntiAlias(true);
        mSuffixTextPaint.setColor(suffixTextColor);
        mSuffixTextPaint.setTextSize(suffixTextSize);

        mTimePointerPaint.setAntiAlias(true);
        mTimePointerPaint.setColor(pointerBackgroundColor);

        parsingDataFormatNode();

        setTimeDataToNodeRect(startTime);

        postInvalidate();
    }

    public void setTypeface(Paint textPaint, int style) {
        if (style > 0) {
            textPaint.setFakeBoldText(true);
            textPaint.setTextSkewX(0);
        } else {
            textPaint.setFakeBoldText(false);
            textPaint.setTextSkewX(0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int twSpec = MeasureSpec.getSize(widthMeasureSpec);
        int thSpec = MeasureSpec.getSize(heightMeasureSpec);

        int ctw = 0;
        int cth = 0;

        //先算高度
        if (heightMode == MeasureSpec.AT_MOST) {
            int th;
            int sh;

            th = getPaddingTop() + getPaddingBottom() + pointerHeight;

            Rect bounds = getMeasureRect(mMeasureRect);
            mSuffixTextPaint.getTextBounds("0", 0, "0".length(), bounds);
            sh = getPaddingTop() + getPaddingBottom() + bounds.height();

            cth = Math.max(th, sh);

            mViewHeight = cth;
        } else {
            mViewHeight = thSpec;
        }

        ctw = getPaddingLeft();
        nodeRectList.clear();
        for (int i = 0; i < formatNodes.size(); i++) {
            FormatNode node = formatNodes.get(i);

            NodeRect nodeRect = new NodeRect();
            nodeRect.isPointer = node.isPointer;
            nodeRect.format = node.format;

            Rect bounds = getMeasureRect(mMeasureRect);
            if (node.isPointer) {
                mTimeTextPaint.getTextBounds("00", 0, "00".length(), bounds);

                int textW = bounds.width();
                int textH = bounds.height();

                nodeRect.bl = ctw;
                nodeRect.bt = getPaddingTop();
                nodeRect.br = ctw + pointerWidth;
                nodeRect.bb = getPaddingTop() + pointerHeight;

                nodeRect.tx = (int) (ctw + ((float) (pointerWidth - textW) / 2));
                nodeRect.ty = (int) (((float) pointerHeight) / 2 + ((float) textH) / 2);

                ctw = ctw + pointerWidth;
            } else {
                mSuffixTextPaint.getTextBounds(nodeRect.format, 0, nodeRect.format.length(), bounds);

                int textW = bounds.width();
                int textH = bounds.height();

                nodeRect.tx = ctw + suffixMarginLeft;
                nodeRect.ty = (int) (((float) pointerHeight) / 2 + ((float) textH) / 2);

                ctw = ctw + suffixMarginLeft + textW + suffixMarginRight;
            }

            nodeRectList.add(nodeRect);
        }

        ctw = ctw + getPaddingRight();

        if (widthMode == MeasureSpec.AT_MOST) {
            mViewWidth = ctw;
        } else {
            mViewWidth = twSpec;
        }

        // 设置控件的宽高，这里就是给文字设置宽高
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (canvas != null)

            if (nodeRectList != null) {
                for (NodeRect rect : nodeRectList) {
                    if (rect.isPointer) {
                        mPointerRectF = getPointerRectF(rect.bl, rect.bt, rect.br, rect.bb);
                        canvas.drawRoundRect(mPointerRectF, pointerRadius, pointerRadius,
                                mTimePointerPaint);

                        if (!TextUtils.isEmpty(rect.text))
                            canvas.drawText(rect.text, rect.tx, rect.ty, mTimeTextPaint);
                    } else {
                        if (!TextUtils.isEmpty(rect.format))
                            canvas.drawText(rect.format, rect.tx, rect.ty, mSuffixTextPaint);
                    }
                }
            }
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;

        parsingDataFormatNode();

        setTimeDataToNodeRect(startTime);

        postInvalidate();
    }

    public void setStartTime(long time) {
        startTime = time;
        if (startTime <= 0) return;

        if (null != mCountDownTimer) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }

        mCountDownTimer = new CountDownTimer(startTime, millisInFuture) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimeView(millisUntilFinished);
            }

            @Override
            public void onFinish() {
//                allShowZero();
            }
        };

        updateTimeView(startTime);
    }

    public void start() {
        if (mCountDownTimer != null)
            mCountDownTimer.start();
    }

    public void stop() {
        if (mCountDownTimer != null)
            mCountDownTimer.onFinish();
    }

    private void parsingDataFormatNode() {
        nodeRectList.clear();
        FormatNodeParser parser = new FormatNodeParser(dataFormat);
        parser.parsing();
        formatNodes = parser.getFormatNodes();
    }

    private RectF getPointerRectF(int l, int t, int r, int b) {
        RectF rectF = getPointerRectF(mPointerRectF);
        rectF.left = l;
        rectF.top = t;
        rectF.right = r;
        rectF.bottom = b;

        return rectF;
    }

    private RectF getPointerRectF(RectF rectF) {
        if (rectF == null) return new RectF();
        return rectF;
    }

    private Rect getMeasureRect(Rect rect) {
        if (rect == null)
            mMeasureRect = new Rect();
        return mMeasureRect;
    }

    private void updateTimeView(long time) {
        setTimeDataToNodeRect(time);

        postInvalidate();
    }

    private void setTimeDataToNodeRect(long time) {
        int day = (int) (time / TIME_OF_DAY);
        int hours = (int) ((time % TIME_OF_DAY) / TIME_OF_HOURS);
        int minute = (int) (time % TIME_OF_HOURS) / TIME_OF_MINUTE;
        int seconds = (int) (time % TIME_OF_MINUTE) / 1000;

        String dayStr = formatPriceToPointAll(day, "00");
        String hoursStr = formatPriceToPointAll(hours, "00");
        String minuteStr = formatPriceToPointAll(minute, "00");
        String secondsStr = formatPriceToPointAll(seconds, "00");

        if (formatNodes != null) {
            for (NodeRect rect : nodeRectList) {
                if (!TextUtils.isEmpty(rect.format)) {
                    if (rect.format.equals(YEARS)) {
                        rect.text = "00";
                    } else if (rect.format.equals(MONTH)) {
                        rect.text = "00";
                    } else if (rect.format.equals(DAY)) {
                        rect.text = dayStr;
                    } else if (rect.format.equals(HOURS)) {
                        rect.text = hoursStr;
                    } else if (rect.format.equals(MINUTE)) {
                        rect.text = minuteStr;
                    } else if (rect.format.equals(SECONDS)) {
                        rect.text = secondsStr;
                    }
                }
            }
        }
    }

    public static String formatPriceToPointAll(float value, String format) {
        DecimalFormat df = new DecimalFormat(format);
        return df.format(value);
    }
}
