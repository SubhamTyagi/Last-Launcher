/*
 * created by https://github.com/rtugeek/ColorSeekBar
 */

package io.github.subhamtyagi.lastlauncher.views.colorseekbar;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.github.subhamtyagi.lastlauncher.R;


//https://github.com/rtugeek/ColorSeekBar

// NOT in priority
public class ColorSeekBar extends View {

    private final List<Integer> mCachedColors = new ArrayList<>();
    private final Paint colorPaint = new Paint();
    private final Paint alphaThumbGradientPaint = new Paint();
    private final Paint alphaBarPaint = new Paint();
    private final Paint mDisabledPaint = new Paint();
    private final Paint thumbGradientPaint = new Paint();
    private int[] mColorSeeds = new int[]{
            0xFFFFFFFF,
            0xFF9900FF,
            0xFF0000FF,
            0xFF00FF00,
            0xFF00FFFF,
            0xFFFF0000,
            0xFFFF00FF,
            0xFFFF6600,
            0xFFFFFF00,
            0xFF8F8C8C,
            0xFF000000
            // 0xFFEAFEAF
    };
    private int mAlpha;
    private OnColorChangeListener mOnColorChangeLister;
    private Context mContext;
    private boolean mIsShowAlphaBar = false;
    private boolean mIsVertical;
    private boolean mMovingColorBar;
    private boolean mMovingAlphaBar;
    private Bitmap mTransparentBitmap;
    private RectF mColorRect;
    private int mThumbHeight = 20;
    private float mThumbRadius;
    private int mBarHeight = 2;
    private Paint mColorRectPaint;
    private int realLeft;
    private int realRight;
    private int mBarWidth;
    private int mMaxPosition;
    private RectF mAlphaRect;
    private int mColorBarPosition;
    private int mAlphaBarPosition;
    private int mDisabledColor;
    private int mBarMargin = 5;
    private int mAlphaMinPosition = 0;
    private int mAlphaMaxPosition = 255;
    private int mBarRadius;
    private int mColorsToInvoke = -1;
    private boolean mInit = false;
    private boolean mFirstDraw = true;
    private boolean mShowThumb = true;
    private OnInitDoneListener mOnInitDoneListener;

    public ColorSeekBar(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ColorSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ColorSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        applyStyle(context, attrs, defStyleAttr, defStyleRes);
    }

    public void applyStyle(int resId) {
        applyStyle(getContext(), null, 0, resId);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int mViewWidth = widthMeasureSpec;
        int mViewHeight = heightMeasureSpec;

        int widthSpeMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpeMode = MeasureSpec.getMode(heightMeasureSpec);

        int barHeight = mIsShowAlphaBar ? mBarHeight * 2 : mBarHeight;
        int thumbHeight = mIsShowAlphaBar ? mThumbHeight * 2 : mThumbHeight;


        if (isVertical()) {
            if (widthSpeMode == MeasureSpec.AT_MOST || widthSpeMode == MeasureSpec.UNSPECIFIED) {
                mViewWidth = thumbHeight + barHeight + mBarMargin;
                setMeasuredDimension(mViewWidth, mViewHeight);
            }

        } else {
            if (heightSpeMode == MeasureSpec.AT_MOST || heightSpeMode == MeasureSpec.UNSPECIFIED) {
                mViewHeight = thumbHeight + barHeight + mBarMargin;
                setMeasuredDimension(mViewWidth, mViewHeight);
            }
        }
    }


    protected void applyStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mContext = context;
        //get attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorSeekBar, defStyleAttr, defStyleRes);
        int colorsId = a.getResourceId(R.styleable.ColorSeekBar_colorSeeds, 0);
        mMaxPosition = a.getInteger(R.styleable.ColorSeekBar_maxPosition, 100);
        mColorBarPosition = a.getInteger(R.styleable.ColorSeekBar_colorBarPosition, 0);
        mAlphaBarPosition = a.getInteger(R.styleable.ColorSeekBar_alphaBarPosition, mAlphaMinPosition);
        mDisabledColor = a.getInteger(R.styleable.ColorSeekBar_disabledColor, Color.GRAY);
        mIsVertical = a.getBoolean(R.styleable.ColorSeekBar_isVertical, false);
        mIsShowAlphaBar = a.getBoolean(R.styleable.ColorSeekBar_showAlphaBar, false);
        mShowThumb = a.getBoolean(R.styleable.ColorSeekBar_showAlphaBar, true);

        int backgroundColor = a.getColor(R.styleable.ColorSeekBar_bgColor, Color.TRANSPARENT);

        mBarHeight = (int) a.getDimension(R.styleable.ColorSeekBar_barHeight, (float) dp2px(2));
        mBarRadius = (int) a.getDimension(R.styleable.ColorSeekBar_barRadius, 0);
        mThumbHeight = (int) a.getDimension(R.styleable.ColorSeekBar_thumbHeight, (float) dp2px(30));
        mBarMargin = (int) a.getDimension(R.styleable.ColorSeekBar_barMargin, (float) dp2px(5));
        a.recycle();

        mDisabledPaint.setAntiAlias(true);
        mDisabledPaint.setColor(mDisabledColor);

        if (colorsId != 0) {
            mColorSeeds = getColorsById(colorsId);
        }

        setBackgroundColor(backgroundColor);
    }

    /**
     * @param id color array resource
     * @return
     */
    private int[] getColorsById(int id) {
        if (isInEditMode()) {
            String[] s = mContext.getResources().getStringArray(id);
            int[] colors = new int[s.length];
            for (int j = 0; j < s.length; j++) {
                colors[j] = Color.parseColor(s[j]);
            }
            return colors;
        } else {
            TypedArray typedArray = mContext.getResources().obtainTypedArray(id);
            int[] colors = new int[typedArray.length()];
            for (int j = 0; j < typedArray.length(); j++) {
                colors[j] = typedArray.getColor(j, Color.BLACK);
            }
            typedArray.recycle();
            return colors;
        }
    }

    private void init() {

        //init size
        mThumbRadius = mThumbHeight / 2;
        int mPaddingSize = (int) mThumbRadius;
        int viewBottom = getHeight() - getPaddingBottom() - mPaddingSize;
        int viewRight = getWidth() - getPaddingRight() - mPaddingSize;
        //init left right top bottom
        realLeft = getPaddingLeft() + mPaddingSize;
        realRight = mIsVertical ? viewBottom : viewRight;
        int realTop = getPaddingTop() + mPaddingSize;

        mBarWidth = realRight - realLeft;

        //init rect
        mColorRect = new RectF(realLeft, realTop, realRight, realTop + mBarHeight);

        //init paint
        LinearGradient mColorGradient = new LinearGradient(0, 0, mColorRect.width(), 0, mColorSeeds, null, Shader.TileMode.CLAMP);
        mColorRectPaint = new Paint();
        mColorRectPaint.setShader(mColorGradient);
        mColorRectPaint.setAntiAlias(true);
        cacheColors();
        setAlphaValue();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mIsVertical) {
            mTransparentBitmap = Bitmap.createBitmap(h, w, Bitmap.Config.ARGB_4444);
        } else {
            mTransparentBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        }
        mTransparentBitmap.eraseColor(Color.TRANSPARENT);
        init();
        mInit = true;
        if (mColorsToInvoke != -1) {
            setColor(mColorsToInvoke);
        }
    }


    private void cacheColors() {
        //if the view's size hasn't been initialized. do not cache.
        if (mBarWidth < 1) {
            return;
        }
        mCachedColors.clear();
        for (int i = 0; i <= mMaxPosition; i++) {
            mCachedColors.add(pickColor(i));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {


        if (mIsVertical) {
            canvas.rotate(-90);
            canvas.translate(-getHeight(), 0);
            canvas.scale(-1, 1, getHeight() / 2, getWidth() / 2);
        }

        float colorPosition = (float) mColorBarPosition / mMaxPosition * mBarWidth;

        colorPaint.setAntiAlias(true);

        int color = isEnabled() ? getColor(false) : mDisabledColor;

        int colorStartTransparent = Color.argb(mAlphaMaxPosition, Color.red(color), Color.green(color), Color.blue(color));
        int colorEndTransparent = Color.argb(mAlphaMinPosition, Color.red(color), Color.green(color), Color.blue(color));
        colorPaint.setColor(color);
        int[] toAlpha = new int[]{colorStartTransparent, colorEndTransparent};
        //clear
        canvas.drawBitmap(mTransparentBitmap, 0, 0, null);

        //draw color bar
        canvas.drawRoundRect(mColorRect, mBarRadius, mBarRadius, isEnabled() ? mColorRectPaint : mDisabledPaint);
        //draw color bar thumb
        if (mShowThumb) {
            float thumbX = colorPosition + realLeft;
            float thumbY = mColorRect.top + mColorRect.height() / 2;
            canvas.drawCircle(thumbX, thumbY, mBarHeight / 2 + 5, colorPaint);

            //draw color bar thumb radial gradient shader
            RadialGradient thumbShader = new RadialGradient(thumbX, thumbY, mThumbRadius, toAlpha, null, Shader.TileMode.MIRROR);
            thumbGradientPaint.setAntiAlias(true);
            thumbGradientPaint.setShader(thumbShader);
            canvas.drawCircle(thumbX, thumbY, mThumbHeight / 2, thumbGradientPaint);
        }


        if (mIsShowAlphaBar) {
            //init rect
            int top = (int) (mThumbHeight + mThumbRadius + mBarHeight + mBarMargin);
            mAlphaRect = new RectF(realLeft, top, realRight, top + mBarHeight);
            //draw alpha bar
            alphaBarPaint.setAntiAlias(true);
            LinearGradient alphaBarShader = new LinearGradient(0, 0, mAlphaRect.width(), 0, toAlpha, null, Shader.TileMode.CLAMP);
            alphaBarPaint.setShader(alphaBarShader);
            canvas.drawRect(mAlphaRect, alphaBarPaint);

            //draw alpha bar thumb
            if (mShowThumb) {
                float alphaPosition = (float) (mAlphaBarPosition - mAlphaMinPosition) / (mAlphaMaxPosition - mAlphaMinPosition) * mBarWidth;
                float alphaThumbX = alphaPosition + realLeft;
                float alphaThumbY = mAlphaRect.top + mAlphaRect.height() / 2;
                canvas.drawCircle(alphaThumbX, alphaThumbY, mBarHeight / 2 + 5, colorPaint);

                //draw alpha bar thumb radial gradient shader
                RadialGradient alphaThumbShader = new RadialGradient(alphaThumbX, alphaThumbY, mThumbRadius, toAlpha, null, Shader.TileMode.MIRROR);

                alphaThumbGradientPaint.setAntiAlias(true);
                alphaThumbGradientPaint.setShader(alphaThumbShader);
                canvas.drawCircle(alphaThumbX, alphaThumbY, mThumbHeight / 2, alphaThumbGradientPaint);
            }
        }

        if (mFirstDraw) {
            if (mOnColorChangeLister != null) {
                mOnColorChangeLister.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, getColor());
            }
            mFirstDraw = false;

            if (mOnInitDoneListener != null) {
                mOnInitDoneListener.done();
            }
        }
        super.onDraw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return true;
        }
        float x = mIsVertical ? event.getY() : event.getX();
        float y = mIsVertical ? event.getX() : event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isOnBar(mColorRect, x, y)) {
                    mMovingColorBar = true;
                    float value = (x - realLeft) / mBarWidth * mMaxPosition;
                    setColorBarPosition((int) value);
                } else if (mIsShowAlphaBar) {
                    if (isOnBar(mAlphaRect, x, y)) {
                        mMovingAlphaBar = true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                if (mMovingColorBar) {
                    float value = (x - realLeft) / mBarWidth * mMaxPosition;
                    setColorBarPosition((int) value);
                } else if (mIsShowAlphaBar) {
                    if (mMovingAlphaBar) {
                        float value = (x - realLeft) / (float) mBarWidth * (mAlphaMaxPosition - mAlphaMinPosition) + mAlphaMinPosition;
                        mAlphaBarPosition = (int) value;
                        if (mAlphaBarPosition < mAlphaMinPosition) {
                            mAlphaBarPosition = mAlphaMinPosition;
                        } else if (mAlphaBarPosition > mAlphaMaxPosition) {
                            mAlphaBarPosition = mAlphaMaxPosition;
                        }
                        setAlphaValue();
                    }
                }
                if (mOnColorChangeLister != null && (mMovingAlphaBar || mMovingColorBar)) {
                    mOnColorChangeLister.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, getColor());
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mMovingColorBar = false;
                mMovingAlphaBar = false;
                break;
            default:
        }
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    public int getAlphaMaxPosition() {
        return mAlphaMaxPosition;
    }

    /***
     *
     * @param alphaMaxPosition <= 255 && > alphaMinPosition
     */
    public void setAlphaMaxPosition(int alphaMaxPosition) {
        mAlphaMaxPosition = alphaMaxPosition;
        if (mAlphaMaxPosition > 255) {
            mAlphaMaxPosition = 255;
        } else if (mAlphaMaxPosition <= mAlphaMinPosition) {
            mAlphaMaxPosition = mAlphaMinPosition + 1;
        }

        if (mAlphaBarPosition > mAlphaMinPosition) {
            mAlphaBarPosition = mAlphaMaxPosition;
        }
        invalidate();
    }

    public int getAlphaMinPosition() {
        return mAlphaMinPosition;
    }

    /***
     *
     * @param alphaMinPosition >=0 && < alphaMaxPosition
     */
    public void setAlphaMinPosition(int alphaMinPosition) {
        this.mAlphaMinPosition = alphaMinPosition;
        if (mAlphaMinPosition >= mAlphaMaxPosition) {
            mAlphaMinPosition = mAlphaMaxPosition - 1;
        } else if (mAlphaMinPosition < 0) {
            mAlphaMinPosition = 0;
        }

        if (mAlphaBarPosition < mAlphaMinPosition) {
            mAlphaBarPosition = mAlphaMinPosition;
        }
        invalidate();
    }

    /**
     * @param r
     * @param x
     * @param y
     * @return whether MotionEvent is performing on bar or not
     */
    private boolean isOnBar(RectF r, float x, float y) {
        return r.left - mThumbRadius < x && x < r.right + mThumbRadius && r.top - mThumbRadius < y && y < r.bottom + mThumbRadius;
    }

    /**
     * @return
     * @deprecated use {@link #setOnInitDoneListener(OnInitDoneListener)} instead.
     */
    public boolean isFirstDraw() {
        return mFirstDraw;
    }


    /**
     * @param value
     * @return color
     */
    private int pickColor(int value) {
        return pickColor((float) value / mMaxPosition * mBarWidth);
    }

    /**
     * @param position
     * @return color
     */
    private int pickColor(float position) {
        float unit = position / mBarWidth;
        if (unit <= 0.0) {
            return mColorSeeds[0];
        }


        if (unit >= 1) {
            return mColorSeeds[mColorSeeds.length - 1];
        }

        float colorPosition = unit * (mColorSeeds.length - 1);
        int i = (int) colorPosition;
        colorPosition -= i;
        int c0 = mColorSeeds[i];
        int c1 = mColorSeeds[i + 1];

        // mAlpha = mix(Color.alpha(c0), Color.alpha(c1), colorPosition);

        int mRed = mix(Color.red(c0), Color.red(c1), colorPosition);
        int mGreen = mix(Color.green(c0), Color.green(c1), colorPosition);
        int mBlue = mix(Color.blue(c0), Color.blue(c1), colorPosition);

        return Color.rgb(mRed, mGreen, mBlue);
    }

    /**
     * @param start
     * @param end
     * @param position
     * @return
     */
    private int mix(int start, int end, float position) {
        return start + Math.round(position * (end - start));
    }

    public int getColor() {
        return getColor(mIsShowAlphaBar);
    }

    /**
     * Set color,the mCachedColors must contains the specified color, if not take the nearest
     *
     * @param color
     */
    public void setColor(int color) {
        int withoutAlphaColor = Color.rgb(Color.red(color), Color.green(color), Color.blue(color));
        if (mInit) {
            int value = mCachedColors.indexOf(withoutAlphaColor);

            if (value == -1) {
                float[] hsv = new float[3];
                Color.colorToHSV(color, hsv);

                float[] cachedHsv = new float[3];

                for (int i = 7; i < mCachedColors.size(); i++) {
                    Color.colorToHSV(mCachedColors.get(i), cachedHsv);

                    if (cachedHsv[0] < hsv[0]) {
                        value = i;
                        break;
                    }
                }
            }

            setAlphaValue(Color.alpha(color));
            setColorBarPosition(value);

        } else {
            mColorsToInvoke = color;
        }

    }

    /**
     * @param withAlpha
     * @return
     */
    public int getColor(boolean withAlpha) {
        //pick mode
        if (mColorBarPosition >= mCachedColors.size()) {
            int color = pickColor(mColorBarPosition);
            if (withAlpha) {
                return color;
            } else {
                return Color.argb(getAlphaValue(), Color.red(color), Color.green(color), Color.blue(color));
            }
        }

        //cache mode
        int color = mCachedColors.get(mColorBarPosition);

        if (withAlpha) {
            return Color.argb(getAlphaValue(), Color.red(color), Color.green(color), Color.blue(color));
        }
        return color;
    }

    public int getAlphaBarPosition() {
        return mAlphaBarPosition;
    }

    public void setAlphaBarPosition(int value) {
        this.mAlphaBarPosition = value;
        setAlphaValue();
        invalidate();
    }

    public int getAlphaValue() {
        return mAlpha;
    }

    private void setAlphaValue(int value) {
        mAlpha = value;
        mAlphaBarPosition = 255 - mAlpha;
        // invalidate();
    }

    /**
     * @param onColorChangeListener
     */
    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.mOnColorChangeLister = onColorChangeListener;
    }

    public int dp2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Set colors by resource id. The resource's type must be ArrayRes
     *
     * @param resId
     */
    public void setColorSeeds(int resId) {
        setColorSeeds(getColorsById(resId));
    }

    public void setColorSeeds(int[] colors) {
        mColorSeeds = colors;
        init();
        invalidate();
        if (mOnColorChangeLister != null) {
            mOnColorChangeLister.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, getColor());
        }
    }

    /**
     * @param color
     * @return the color's position in the bar, if not in the bar ,return -1;
     */
    public int getColorIndexPosition(int color) {
        return mCachedColors.indexOf(Color.argb(255, Color.red(color), Color.green(color), Color.blue(color)));
    }

    public List<Integer> getColors() {
        return mCachedColors;
    }

//    public void setVertical(boolean vertical) {
//        mIsVertical = vertical;
//        refreshLayoutParams();
//        invalidate();
//    }

    public boolean isShowAlphaBar() {
        return mIsShowAlphaBar;
    }

    public void setShowAlphaBar(boolean show) {
        mIsShowAlphaBar = show;
        refreshLayoutParams();
        invalidate();
        if (mOnColorChangeLister != null) {
            mOnColorChangeLister.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, getColor());
        }
    }

    private void refreshLayoutParams() {
        setLayoutParams(getLayoutParams());
    }

    public boolean isVertical() {
        return mIsVertical;
    }

    /**
     * @param px
     */
    public void setBarHeightPx(int px) {
        mBarHeight = px;
        refreshLayoutParams();
        invalidate();
    }

    private void setAlphaValue() {
        mAlpha = 255 - mAlphaBarPosition;
    }

    public int getMaxValue() {
        return mMaxPosition;
    }

    public void setMaxPosition(int value) {
        this.mMaxPosition = value;
        invalidate();
        cacheColors();
    }

    /**
     * set margin between bars
     *
     * @param mBarMargin
     */
    public void setBarMarginPx(int mBarMargin) {
        this.mBarMargin = mBarMargin;
        refreshLayoutParams();
        invalidate();
    }

    public void setPosition(int colorBarPosition, int alphaBarPosition) {
        this.mColorBarPosition = colorBarPosition;
        mColorBarPosition = Math.min(mColorBarPosition, mMaxPosition);
        mColorBarPosition = Math.max(mColorBarPosition, 0);
        this.mAlphaBarPosition = alphaBarPosition;
        setAlphaValue();
        invalidate();
        if (mOnColorChangeLister != null) {
            mOnColorChangeLister.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, getColor());
        }
    }

    public void setOnInitDoneListener(OnInitDoneListener listener) {
        this.mOnInitDoneListener = listener;
    }

    /**
     * set thumb's height by pixels
     *
     * @param px
     */
    public void setThumbHeightPx(int px) {
        this.mThumbHeight = px;
        mThumbRadius = mThumbHeight / 2;
        refreshLayoutParams();
        invalidate();
    }

    public int getBarHeight() {
        return mBarHeight;
    }

    /**
     * @param dp
     */
    public void setBarHeight(float dp) {
        mBarHeight = dp2px(dp);
        refreshLayoutParams();
        invalidate();
    }

    public int getThumbHeight() {
        return mThumbHeight;
    }

    /**
     * set thumb's height by dpi
     *
     * @param dp
     */
    public void setThumbHeight(float dp) {
        this.mThumbHeight = dp2px(dp);
        mThumbRadius = mThumbHeight / 2;
        refreshLayoutParams();
        invalidate();
    }

    public int getBarMargin() {
        return mBarMargin;
    }

    /**
     * set margin between bars
     *
     * @param mBarMargin
     */
    public void setBarMargin(float mBarMargin) {
        this.mBarMargin = dp2px(mBarMargin);
        refreshLayoutParams();
        invalidate();
    }

    public float getColorBarValue() {
        return mColorBarPosition;
    }

    public int getColorBarPosition() {
        return mColorBarPosition;
    }

    /**
     * Set the value of color bar, if out of bounds , it will be 0 or maxValue;
     *
     * @param value
     */
    public void setColorBarPosition(int value) {
        this.mColorBarPosition = value;
        mColorBarPosition = Math.min(mColorBarPosition, mMaxPosition);
        mColorBarPosition = Math.max(mColorBarPosition, 0);
        invalidate();

        if (mOnColorChangeLister != null) {
            mOnColorChangeLister.onColorChangeListener(mColorBarPosition, mAlphaBarPosition, getColor());
        }
    }

    public int getDisabledColor() {
        return mDisabledColor;
    }

    public void setDisabledColor(int disabledColor) {
        this.mDisabledColor = disabledColor;
        mDisabledPaint.setColor(disabledColor);
    }

    public boolean isShowThumb() {
        return mShowThumb;
    }

    public void setShowThumb(boolean showThumb) {
        this.mShowThumb = showThumb;
        invalidate();
    }

    public int getBarRadius() {
        return mBarRadius;
    }

    /**
     * Set bar radius with px unit
     *
     * @param barRadiusInPx
     */
    public void setBarRadius(int barRadiusInPx) {
        this.mBarRadius = barRadiusInPx;
        invalidate();
    }

    public interface OnColorChangeListener {
        /**
         * @param colorBarPosition between 0-maxValue
         * @param alphaBarPosition between 0-255
         * @param color            return the color contains alpha value whether showAlphaBar is true or without alpha value
         */
        void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color);
    }

    public interface OnInitDoneListener {
        void done();
    }

}
