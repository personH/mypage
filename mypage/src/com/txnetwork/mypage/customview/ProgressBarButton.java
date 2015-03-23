package com.txnetwork.mypage.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import com.txnetwork.mypage.R;

import static com.txnetwork.mypage.R.styleable.ProgressBarButton_text;


/**
 * Created by daimajia on 14-4-30.
 */
public class ProgressBarButton extends View {

    private Context mContext;

    /**
     * The max progress, default is 100
     */
    private int mMax = 100;

    /**
     * current progress, can not exceed the max progress.
     */
    private int mProgress = 0;

    /**
     * the progress area bar color
     */
    private int mReachedBarColor;


    /**
     * the progress text color.
     */
    private int mTextColor;

    /**
     * the progress text size
     */
    private float mTextSize;

    /**
     * the height of the reached area
     */
    private float mReachedBarHeight;


    /**
     * the suffix of the number.
     */
    private String mSuffix = "%";

    /**
     * the prefix.
     */
    private String mPrefix = "";


    private final int default_text_color = Color.rgb(66, 145, 241);
    private final int default_reached_color = Color.rgb(66, 145, 241);
    private final float default_text_size;
    private final float default_reached_bar_height;

    private final String default_text = "progressbar";

    /**
     * for save and restore instance of progressbar.
     */
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_REACHED_BAR_HEIGHT = "reached_bar_height";
    private static final String INSTANCE_REACHED_BAR_COLOR = "reached_bar_color";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_SUFFIX = "suffix";
    private static final String INSTANCE_PREFIX = "prefix";
    private static final String INSTANCE_TEXT_VISBILITY = "text_visibility";

    private static final int PROGRESS_TEXT_VISIBLE = 0;
    private static final int PROGRESS_TEXT_INVISIBLE = 1;


    /**
     * the width of the text that to be drawn
     */
    private float mDrawTextWidth;

    /**
     * the drawn text start
     */
    private float mDrawTextStart;

    /**
     * the drawn text end
     */
    private float mDrawTextEnd;

    /**
     * the text that to be drawn in onDraw()
     */
    private String mCurrentDrawText;

    /**
     * the Paint of the reached area.
     */
    private Paint mReachedBarPaint;
    /**
     * the Painter of the progress text.
     */
    private Paint mTextPaint;
    /**
     * reached bar area rect.
     */
    private RectF mReachedRectF = new RectF(0, 0, 0, 0);


    private boolean mIfDrawText = false;

    private String text;

    public enum ProgressTextVisibility {
        Visible, Invisible
    }

    ;


    public ProgressBarButton(Context context) {
        this(context, null);
    }

    public ProgressBarButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.numberProgressBarStyle);//
    }

    public ProgressBarButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        default_reached_bar_height = dp2px(1.5f);
        default_text_size = sp2px(10);

        //load styled attributes.
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressBarButton,
                defStyleAttr, 0);

        mReachedBarColor = attributes.getColor(R.styleable.ProgressBarButton_progress_reached_color, default_reached_color);
        mTextColor = attributes.getColor(R.styleable.ProgressBarButton_progress_text_color, default_text_color);
        mTextSize = attributes.getDimension(R.styleable.ProgressBarButton_progress_text_size, default_text_size);

        mReachedBarHeight = attributes.getDimension(R.styleable.ProgressBarButton_progress_reached_bar_height, default_reached_bar_height);

        text = attributes.getString(ProgressBarButton_text);

        int textVisible = attributes.getInt(R.styleable.ProgressBarButton_progress_text_visibility, PROGRESS_TEXT_VISIBLE);
        if (textVisible != PROGRESS_TEXT_VISIBLE) {
            mIfDrawText = false;
        }

        setProgress(attributes.getInt(R.styleable.ProgressBarButton_progress, 0));
        setMax(attributes.getInt(R.styleable.ProgressBarButton_max, 100));
        //
        attributes.recycle();//循环

        initializePainters();

    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) mTextSize;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return Math.max((int) mTextSize, (int) mReachedBarHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        calculateDrawRectFWithoutProgressText();

        canvas.drawRect(mReachedRectF, mReachedBarPaint);

        float textWidth = mTextPaint.measureText(text);
        float start = getPaddingLeft() + (getWidth() - getPaddingLeft() - getPaddingRight()) / 2.0f - textWidth / 2.0f;
        float end = (getHeight() / 2.0f) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2.0f);
        canvas.drawText(text, start, end, mTextPaint);
    }

    private void initializePainters() {
        mReachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mReachedBarPaint.setColor(mReachedBarColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }


    private void calculateDrawRectFWithoutProgressText() {
        mReachedRectF.left = getPaddingLeft();
        mReachedRectF.top = getHeight() / 2.0f - mReachedBarHeight / 2.0f;
        mReachedRectF.right = (getWidth() - getPaddingLeft() - getPaddingRight()) / (getMax() * 1.0f) * getProgress() + getPaddingLeft();
        mReachedRectF.bottom = getHeight() / 2.0f + mReachedBarHeight / 2.0f;
    }


    /**
     * get progress text color
     *
     * @return progress text color
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * get progress text size
     *
     * @return progress text size
     */
    public float getProgressTextSize() {
        return mTextSize;
    }


    public int getReachedBarColor() {
        return mReachedBarColor;
    }

    public int getProgress() {
        return mProgress;
    }

    public int getMax() {
        return mMax;
    }

    public float getReachedBarHeight() {
        return mReachedBarHeight;
    }


    public void setProgressTextSize(float TextSize) {
        this.mTextSize = TextSize;
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public void setProgressTextColor(int TextColor) {
        this.mTextColor = TextColor;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }


    public void setReachedBarColor(int ProgressColor) {
        this.mReachedBarColor = ProgressColor;
        mReachedBarPaint.setColor(mReachedBarColor);
        invalidate();
    }

    public void setReachedBarHeight(float height) {
        mReachedBarHeight = height;
    }


    public void setMax(int Max) {
        if (Max > 0) {
            this.mMax = Max;
            invalidate();
        }
    }

    public void setSuffix(String suffix) {
        if (suffix == null) {
            mSuffix = "";
        } else {
            mSuffix = suffix;
        }
    }

    public String getSuffix() {
        return mSuffix;
    }

    public void setPrefix(String prefix) {
        if (prefix == null)
            mPrefix = "";
        else {
            mPrefix = prefix;
        }
    }

    public String getPrefix() {
        return mPrefix;
    }

    public void incrementProgressBy(int by) {
        if (by > 0) {
            setProgress(getProgress() + by);
        }
    }

    public void setProgress(int Progress) {
        if (Progress <= getMax() && Progress >= 0) {
            this.mProgress = Progress;
            invalidate();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getProgressTextSize());
        bundle.putFloat(INSTANCE_REACHED_BAR_HEIGHT, getReachedBarHeight());
        bundle.putInt(INSTANCE_REACHED_BAR_COLOR, getReachedBarColor());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        bundle.putString(INSTANCE_SUFFIX, getSuffix());
        bundle.putString(INSTANCE_PREFIX, getPrefix());
        bundle.putBoolean(INSTANCE_TEXT_VISBILITY, getProgressTextVisibility());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mTextColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            mTextSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            mReachedBarHeight = bundle.getFloat(INSTANCE_REACHED_BAR_HEIGHT);
            mReachedBarColor = bundle.getInt(INSTANCE_REACHED_BAR_COLOR);
            initializePainters();
            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            setPrefix(bundle.getString(INSTANCE_PREFIX));
            setSuffix(bundle.getString(INSTANCE_SUFFIX));
            setProgressTextVisibility(bundle.getBoolean(INSTANCE_TEXT_VISBILITY) ? ProgressTextVisibility.Visible : ProgressTextVisibility.Invisible);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public float dp2px(float dp) {//dip
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public void setProgressTextVisibility(ProgressTextVisibility visibility) {
        mIfDrawText = visibility == ProgressTextVisibility.Visible;
        invalidate();
    }

    public boolean getProgressTextVisibility() {
        return mIfDrawText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }
}
