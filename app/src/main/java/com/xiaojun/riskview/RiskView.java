package com.xiaojun.riskview;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.xiaojun.bulter.yyUtils.YyScreenUtils;

/**
 * 风险View
 * created by xiaojun at 2020/8/31
 */
public class RiskView extends View {

    private int mWidth, mHeight;
    private float mBallOffsetX;
    private int mBallColor, mLineColor;
    private ValueAnimator mAnimator;
    private Handler mHandler = new Handler();

    private int[] mColors = new int[]{0xFFFF0000, 0xFFFFFF00, 0xFF008000};
    private float[] mPositions = new float[]{0, 0.5f, 1};

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintBall = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Ball mBall = new Ball();
    private Line mLine1 = new Line();
    private Line mLine2 = new Line();

    public RiskView(Context context) {
        this(context, null);
    }

    public RiskView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RiskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        mPaintLine.setStyle(Paint.Style.STROKE);

        mPaintLine.setStrokeWidth(YyScreenUtils.dp2px(getContext(), 1));

        getCustomAttrs(context, attrs);
    }

    /**
     * 获取自定义属性
     *
     * @param context
     * @param attrs
     */
    private void getCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RiskView);
        mBallColor = ta.getColor(R.styleable.RiskView__ballColor, Color.WHITE);
        mLineColor = ta.getColor(R.styleable.RiskView__lineColor, Color.WHITE);
        ta.recycle();
        mPaintBall.setColor(mBallColor);
        mPaintLine.setColor(mLineColor);
    }

    /**
     * 设置轨迹球颜色
     *
     * @param color
     */
    public void setBallColor(int color) {
        mPaintBall.setColor(color);
        invalidate();
    }

    /**
     * 设置线段颜色
     *
     * @param color
     */
    public void setLineColor(int color) {
        mPaintLine.setColor(color);
    }

    /**
     * 设置值
     *
     * @param value 0~1
     */
    public void setValue(float value) {
        if (mAnimator != null) {
            if (mAnimator.isRunning()) {
                mAnimator.cancel();
            }
        } else {
            mAnimator = new ValueAnimator();
            mAnimator.setDuration(250);
            mAnimator.setInterpolator(new DecelerateInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mBallOffsetX = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
        }
        int widthBar = mWidth - mHeight;
        mAnimator.setFloatValues(mBallOffsetX, widthBar * value);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAnimator.start();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int defaultSizeWidth = getMeasuredWidth();
        mWidth = getProperSize(defaultSizeWidth, widthMeasureSpec,true);
        defaultSizeWidth = getMeasuredHeight();
        mHeight = getProperSize(defaultSizeWidth,heightMeasureSpec,false);
        setMeasuredDimension(mWidth,mHeight);
    }

    private int getProperSize(int defaultSize, int measureSpec, boolean isWidth) {
        int properSize = defaultSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY:
                properSize = size;
                break;
            case MeasureSpec.AT_MOST:
                if (!isWidth)
                    properSize = YyScreenUtils.dp2px(getContext(), 12);
                else
                    properSize = Math.min(size,YyScreenUtils.dp2px(getContext(),72));
                break;
        }
        return properSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mPaint.setShader(new LinearGradient(0, mHeight / 2.f, mWidth, mHeight / 2.f, mColors, mPositions, Shader.TileMode.REPEAT));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mHeight);
        //初始化轨迹球
        mBall.x = 0;
        mBall.y = mHeight / 2.f;
        mBall.radius = mHeight / 5.f * 2;
        //初始化线段
        mLine1.startX = (mWidth - mHeight) / 3.f + mHeight / 2.f;
        mLine1.startY = 0;
        mLine1.endX = (mWidth - mHeight) / 3.f + mHeight / 2.f;
        mLine1.endY = mHeight;

        mLine2.startX = (mWidth - mHeight) / 3.f * 2 + mHeight / 2.f;
        mLine2.startY = 0;
        mLine2.endX = (mWidth - mHeight) / 3.f * 2 + mHeight / 2.f;
        mLine2.endY = mHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(mHeight / 2.f, mHeight / 2.f, mWidth - mHeight / 2.f, mHeight / 2.f, mPaint);
        drawLine(canvas);
        drawBall(canvas);
    }

    private void drawLine(Canvas canvas) {
        canvas.drawLine(mLine1.startX, mLine1.startY, mLine1.endX, mLine1.endY, mPaintLine);
        canvas.drawLine(mLine2.startX, mLine2.startY, mLine2.endX, mLine2.endY, mPaintLine);
    }

    private void drawBall(Canvas canvas) {
        canvas.drawCircle(mHeight / 2.f + mBallOffsetX + mBall.x, mBall.y, mBall.radius, mPaintBall);
    }
}
