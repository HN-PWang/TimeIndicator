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

    private static final int TIME_OF_MILLISECOND = 1000;
    private static final int TIME_OF_MINUTE = 60 * TIME_OF_MILLISECOND;
    private static final int TIME_OF_HOURS = 60 * TIME_OF_MINUTE;
    private static final int TIME_OF_DAY = 24 * TIME_OF_HOURS;
    private static final long TIME_OF_MONTH = 30L * TIME_OF_DAY;
    private static final long TIME_OF_YEAR = 365L * TIME_OF_DAY;

    public static final String YEARS = "yyyy";
    public static final String MONTH = "MM";
    public static final String DAY = "dd";
    public static final String HOURS = "HH";
    public static final String MINUTE = "mm";
    public static final String SECONDS = "ss";
    public static final String MILLISECOND = "SSS";

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

    public static final long DEF_MILLIS_IN_FUTURE = 20;

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
     * 默认倒计时状态
     */
    private static final boolean DEF_IS_COUNTDOWN = false;

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
    private float pointerTextSize;

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
    private float suffixTextSize;

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

    /**
     * 计时间隔
     */
    private long millisInFuture = DEF_MILLIS_IN_FUTURE;

    /**
     * 计时器初始化时间
     */
    private long mTimerInitializationTime;

    /**
     * 视图宽
     */
    private int mViewWidth;

    /**
     * 视图高
     */
    private int mViewHeight;

    /**
     * 开始时间
     */
    private long startTime = 0;

    private List<FormatNode> formatNodes;

    private Paint mTimeTextPaint;

    private Paint mSuffixTextPaint;

    private Paint mTimePointerPaint;

    private RectF mPointerRectF;

    private Rect mMeasureRect;

    private List<NodeRect> nodeRectList;

    private CountDownTimer mCountDownTimer;

    private final DecimalFormat df1 = new DecimalFormat("00");

    private final DecimalFormat df2 = new DecimalFormat("000");

    private final DecimalFormat df3 = new DecimalFormat("0000");

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

        isCountdown = ta.getBoolean(R.styleable.TimeIndicator_tiIsCountdown, DEF_IS_COUNTDOWN);
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
        setTypeface(mSuffixTextPaint, suffixTextStyle);

        mTimePointerPaint.setAntiAlias(true);
        mTimePointerPaint.setColor(pointerBackgroundColor);

        parsingDataFormatNode();

        invalidate();
    }

    /**
     * 设置时间格式  例如 HH:mm:ss
     *
     * @param dataFormat
     */
    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;

        parsingDataFormatNode();

        setTimeDataToNodeRect(startTime);

        invalidate();
    }

    /**
     * 设置是否为倒计时
     */
    public void setIsCountdown(boolean isCountdown) {
        this.isCountdown = isCountdown;

        buildTimer();
    }

    /**
     * 设置时间文字样式
     */
    public void setTimeTextColor(int color) {
        this.pointerTextColor = color;

        if (mTimeTextPaint != null) {
            mTimeTextPaint.setColor(pointerTextColor);

            invalidate();
        }
    }

    /**
     * 设置时间文字字体大小
     */
    public void setTimeTextSize(float size) {
        this.pointerTextSize = size;

        if (mTimeTextPaint != null) {
            mTimeTextPaint.setTextSize(pointerTextSize);

            requestLayout();
        }
    }

    public float getTimeTextSize() {
        return pointerTextSize;
    }

    /**
     * 设置后缀单位文字颜色
     */
    public void setSuffixTextColor(int color) {
        this.suffixTextColor = color;

        if (mSuffixTextPaint != null) {
            mSuffixTextPaint.setColor(suffixTextColor);

            invalidate();
        }
    }

    /**
     * 设置后缀单位文字字体大小
     *
     * @param size
     */
    public void setSuffixTextSize(float size) {
        this.suffixTextSize = size;

        if (mSuffixTextPaint != null) {
            mSuffixTextPaint.setTextSize(suffixTextSize);

            requestLayout();
        }
    }

    public float getSuffixTextSize() {
        return suffixTextSize;
    }

    private void setTypeface(Paint textPaint, int style) {
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

        for (NodeRect nodeRect : nodeRectList) {
            Rect bounds = getMeasureRect(mMeasureRect);
            if (nodeRect.isPointer) {
                mTimeTextPaint.getTextBounds(nodeRect.text, 0, nodeRect.text.length(), bounds);

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

    public void setStartTime(long time) {
        startTime = time;

        buildTimer();

        setTimeDataToNodeRect(startTime);
    }

    public void start() {
        if (mCountDownTimer != null)
            mCountDownTimer.start();
    }

    public void stop() {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
    }

    /**
     * 构建计时器
     */
    private void buildTimer() {
        if (isCountdown) {
            //倒计时
            mTimerInitializationTime = startTime;
        } else {
            //顺时针
            mTimerInitializationTime = Long.MAX_VALUE;
        }

        if (null != mCountDownTimer) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }

        mCountDownTimer = new CountDownTimer(mTimerInitializationTime, millisInFuture) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isCountdown) {
                    setTimeDataToNodeRect(millisUntilFinished);
                } else {
                    setTimeDataToNodeRect(mTimerInitializationTime - millisUntilFinished + startTime);
                }
            }

            @Override
            public void onFinish() {

            }
        };

        setTimeDataToNodeRect(startTime);
    }

    /**
     * 解析时间格式节点
     */
    private void parsingDataFormatNode() {
        FormatNodeParser parser = new FormatNodeParser(dataFormat);
        parser.parsing();
        formatNodes = parser.getFormatNodes();

        fillNodeRect();
    }

    /**
     * 填充节点绘制工具
     */
    private void fillNodeRect() {
        nodeRectList.clear();
        if (formatNodes != null && formatNodes.size() != 0) {
            for (FormatNode node : formatNodes) {
                NodeRect nodeRect = new NodeRect();
                nodeRect.isPointer = node.isPointer;
                nodeRect.format = node.format;

                nodeRectList.add(nodeRect);
            }
        }

        setTimeDataToNodeRect(startTime);
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

    /**
     * 填充节点绘制工具写入数据
     */
    private void setTimeDataToNodeRect(long time) {
        int year = (int) (time / TIME_OF_YEAR);
        int month = (int) ((time % TIME_OF_YEAR) / TIME_OF_MONTH);
        int day = (int) ((time % TIME_OF_MONTH) / TIME_OF_DAY);
        int hours = (int) ((time % TIME_OF_DAY) / TIME_OF_HOURS);
        int minute = (int) ((time % TIME_OF_HOURS) / TIME_OF_MINUTE);
        int seconds = (int) (time % TIME_OF_MINUTE) / TIME_OF_MILLISECOND;
        int millisecond = (int) (time % TIME_OF_MILLISECOND);

        String yearStr = df3.format(year);
        String monthStr = df1.format(month);
        String dayStr = df1.format(day);
        String hoursStr = df1.format(hours);
        String minuteStr = df1.format(minute);
        String secondsStr = df1.format(seconds);
        String millisecondStr = df2.format(millisecond);

        if (nodeRectList != null) {
            for (NodeRect rect : nodeRectList) {
                if (YEARS.equals(rect.format)) {
                    rect.text = yearStr;
                } else if (MONTH.equals(rect.format)) {
                    rect.text = monthStr;
                } else if (DAY.equals(rect.format)) {
                    rect.text = dayStr;
                } else if (HOURS.equals(rect.format)) {
                    rect.text = hoursStr;
                } else if (MINUTE.equals(rect.format)) {
                    rect.text = minuteStr;
                } else if (SECONDS.equals(rect.format)) {
                    rect.text = secondsStr;
                } else if (MILLISECOND.equals(rect.format)) {
                    rect.text = millisecondStr;
                } else {
                    rect.text = "00";
                }
            }
        }
        invalidate();
    }

}
