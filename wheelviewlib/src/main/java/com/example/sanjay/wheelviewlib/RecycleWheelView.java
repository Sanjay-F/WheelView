package com.example.sanjay.wheelviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class RecycleWheelView extends RecyclerView {
    //子view可以被缩放到最小的view的大小
    private static final float MIN_SCALE_VALUE = 0.1f;
    private static final float MIN_ALPHA_VALUE = 0.1f;
    private static final float ALPHA_WEIGHT = 1.6f;
    private static final String TAG = RecycleWheelView.class.getSimpleName();

    //设置一开始的padding
    private int mOldPaddingLeft, mOldPaddingTop, mOldPaddingRight, mOldPaddingBottom;

    //分割线粗细
    private int mLineThickness = 0;

    //分割线颜色
    private int mLineColor = 0;
    //中间提示颜色线
    private ColorDrawable mLineDrawable;
    //中间提示自定线
    private Drawable mCustomLineDrawable;
    //drawable padding
    private int mDrawablePadding = 0;
    //是否需要重排
    private boolean mNeedAdjust = false;
    //当前激活的view
    private View mCurView;
    //方向 0 垂直 1 水平
    private int mDirection = 0;
    private OnSelectItemListener mSelectListener;
    //上一次选中的位置
    private int mLastSelectPosition = -1;
    private int selectedPos = 0;
    private boolean isCurve = false;
    private boolean gradient = true;
    private int visibleItemSize = 5;
    private String lable = "label";
    private Paint labelTextPaint;
    private static final int MINI_VISIBLE_ITEM = 3;
    private static final int MAX_VISIBLE_ITEM = 11;

    public RecycleWheelView(Context context) {
        this(context, null);

    }

    public RecycleWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecycleWheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttr(attrs, defStyle);
    }


    void initAttr(AttributeSet attrs, int defStyle) {

        labelTextPaint = new Paint();
        labelTextPaint.setColor(Color.BLACK);
        labelTextPaint.setTextSize(ViewUtils.dp2px(getContext(), 17));

        mOldPaddingLeft = getPaddingLeft();
        mOldPaddingBottom = getPaddingBottom();
        mOldPaddingRight = getPaddingRight();
        mOldPaddingTop = getPaddingTop();
        setClipToPadding(false);

        final TypedArray typedArray = getContext().obtainStyledAttributes(
                attrs, R.styleable.RecycleWheelView, defStyle, 0);

        mLineThickness = typedArray.getDimensionPixelSize(R.styleable.RecycleWheelView_recycleWheelLineThickness, 0);
        mLineColor = typedArray.getColor(R.styleable.RecycleWheelView_recycleWheelLineColor, 0);
        mCustomLineDrawable = typedArray.getDrawable(R.styleable.RecycleWheelView_recycleWheelLineDrawable);
        mDrawablePadding = typedArray.getDimensionPixelSize(R.styleable.RecycleWheelView_recycleWheelLinePadding, 0);

        if (mLineColor != 0 && mLineThickness != 0 && mCustomLineDrawable == null) {
            mLineDrawable = new ColorDrawable(mLineColor);
        }

        mDirection = typedArray.getInt(R.styleable.RecycleWheelView_recycleWheelDirection, 0);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        if (mDirection == 0) {
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        } else {
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        }
        setLayoutManager(layoutManager);

        typedArray.recycle();
    }

    public int getSelectPosition() {
        int curPosition;
        if (getLayoutManager().canScrollHorizontally()) {
            curPosition = ViewUtils.getCenterXChildPosition(this);
        } else {
            curPosition = ViewUtils.getCenterYChildPosition(this);
        }
        return curPosition;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mNeedAdjust = true;
                break;
            }
        }
        return super.onTouchEvent(e);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        switch (state) {
            case SCROLL_STATE_IDLE: {
                Log.e(TAG, " on state idle");
                if (getLayoutManager() != null
                        && getLayoutManager().canScrollHorizontally()) {
                    adjustPositionX();
                } else {
                    adjustPositionY();
                }
                break;
            }
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (getChildCount() > 0) {
            View view = getChildAt(0);
            int paddingH = (getWidth() - view.getWidth()) >> 1;
            int paddingV = (getHeight() - view.getHeight()) >> 1;
            if (getLayoutManager().canScrollHorizontally()) {
                if (getPaddingLeft() != paddingH || getPaddingRight() != paddingH) {
                    setPadding(paddingH, mOldPaddingTop, paddingH, mOldPaddingBottom);
                    scrollToPosition(selectedPos);
                }
            } else {
                if (getPaddingTop() != paddingV || getPaddingBottom() != paddingV) {
                    setPadding(mOldPaddingLeft, paddingV, mOldPaddingRight, paddingV);
                    scrollToPosition(selectedPos);
                }
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制中间分割线
        drawCenterLine(canvas);
        //绘制旁边的标签 eg:年，体重
        drawLabel(canvas);
        //设置childView的效果
        updateChildView();
    }

    private void drawLabel(Canvas canvas) {

        if (getLayoutManager() != null && getLayoutManager().canScrollHorizontally()) {
            //水平时候的标枪
        } else {
            int height = 0;
            if (getChildCount() > 0) {
                getChildAt(0).getHeight();
                height = getChildAt(0).getWidth();
            }
            int startLeft = getWidth() * 5 / 8;
            int paddingV = (getHeight() - height) >> 1;
            int centerY = (int) (paddingV + height / 2 + labelTextPaint.getTextSize() / 2);
            canvas.drawText(lable, startLeft, centerY, labelTextPaint);
        }
    }

    private void drawCenterLine(Canvas canvas) {
        if ((mLineDrawable != null || mCustomLineDrawable != null)) {

            Drawable drawable = mCustomLineDrawable == null ? mLineDrawable
                    : mCustomLineDrawable;
            if (getLayoutManager() != null && getLayoutManager().canScrollHorizontally()) {
                int width = 0;
                if (getChildCount() > 0) {
                    width = getChildAt(0).getWidth();
                }
                int startLeft = ((getWidth() - width) >> 1) - mLineThickness;
                drawable.setBounds(startLeft, mDrawablePadding, startLeft + mLineThickness, getHeight() - mDrawablePadding);
                drawable.draw(canvas);
                startLeft = ((getWidth() - width) >> 1) + width;
                drawable.setBounds(startLeft, mDrawablePadding, startLeft + mLineThickness, getHeight() - mDrawablePadding);
                drawable.draw(canvas);
            } else {
                int height = 0;
                if (getChildCount() > 0) {
                    height = getChildAt(0).getHeight();
                }
                int startTop = ((getHeight() - height) >> 1) - mLineThickness;
                drawable.setBounds(mDrawablePadding, startTop, getWidth() - mDrawablePadding, startTop + mLineThickness);
                drawable.draw(canvas);
                startTop = ((getHeight() - height) >> 1) + height;
                drawable.setBounds(mDrawablePadding, startTop, getWidth() - mDrawablePadding, startTop + mLineThickness);
                drawable.draw(canvas);
            }
        }
    }

    /**
     * 更新子view,包括渐显，缩小等
     */
    protected void updateChildView() {

        if (getLayoutManager().canScrollHorizontally()) {
            mCurView = ViewUtils.getCenterXChild(this);
        } else {
            mCurView = ViewUtils.getCenterYChild(this);
        }

        int centerIndex = indexOfChild(mCurView);
        int limitViewIndex = visibleItemSize / 2;

        for (int i = getChildCount() - 1; i >= 0; --i) {
            View view = getChildAt(i);

            //设置在可见区间的view
            if (centerIndex - limitViewIndex <= i && centerIndex + limitViewIndex >= i) {

                view.setVisibility(VISIBLE);
                float value;
                if (getLayoutManager().canScrollHorizontally()) {//水平情况
                    float midX = view.getLeft() + view.getWidth() / 2.0f;
                    float size = getWidth() / 2.0f;
                    value = (size - midX) / size;
                    if (ViewUtils.isChildInCenterX(this, view)) {
                        mCurView = view;
                    }
                } else {//垂直情况
                    float midY = view.getTop() + view.getHeight() / 2.0f;
                    float size = getHeight() / 2.0f;
                    value = (size - midY) / size;
                    if (ViewUtils.isChildInCenterY(this, view)) {
                        mCurView = view;
                    }
                }

                float valueWeight = ((Math.abs(centerIndex - i)) * (1.0f / visibleItemSize)) * ALPHA_WEIGHT;
                float viewAlpha = Math.abs(1 - valueWeight);


                value = (float) Math.sqrt(1.0f - value * value);
                value = Math.min(1.0f, Math.abs(value));
                if (view != mCurView) {
                    value -= MIN_SCALE_VALUE;
                    if (value < 0.0f) {
                        value = 0.0f;
                    }
                }

                if (isCurve()) {
                    view.setScaleX(value);
                    view.setScaleY(value);
                }
                if (isGradient()) {
                    view.setAlpha(viewAlpha);
                }

            } else {
                view.setVisibility(GONE);
            }
        }
    }

    /***
     * adjust position before Touch event complete and fling action start.
     */
    protected void adjustPositionY() {
        if (!mNeedAdjust) {
            return;
        }
        mNeedAdjust = true;

        int curPosition = getSelectPosition();
        if (curPosition != mLastSelectPosition) {
            mLastSelectPosition = curPosition;
            if (mSelectListener != null) {
                mSelectListener.onSelectChanged(mLastSelectPosition);
            }
        }
        smoothScrollToPosition(curPosition);
    }

    /***
     * adjust position before Touch event complete and fling action start.
     */
    protected void adjustPositionX() {
        if (!mNeedAdjust) {
            return;
        }
        mNeedAdjust = true;

        int curPosition = getSelectPosition();
        if (curPosition != mLastSelectPosition) {
            mLastSelectPosition = curPosition;
            if (mSelectListener != null) {
                mSelectListener.onSelectChanged(mLastSelectPosition);
            }
        }
        smoothScrollToPosition(curPosition);
    }

    public interface OnSelectItemListener {
        void onSelectChanged(int position);
    }
    //--------------------------------------------------------
    //---------------------对外的接口----------------------------

    public void setOnSelectListener(OnSelectItemListener listener) {
        this.mSelectListener = listener;
    }

    /**
     * 设置中间的分割线的颜色
     *
     * @param color 颜色
     */
    public void setLineColor(@ColorInt int color) {
        if (mLineDrawable != null) {
            mLineDrawable.setColor(color);
        } else {
            mLineDrawable = new ColorDrawable(color);
        }
        requestLayout();
    }

    /**
     * 设置中间的分割线的粗细
     *
     * @param thickness 线大小
     */
    public void setLineThickness(int thickness) {
        mLineThickness = thickness;
        requestLayout();
    }

    /**
     * 设置我们的线的样子,默认为一条黑线
     *
     * @param drawable
     */
    public void setLineDrawable(Drawable drawable) {
        mCustomLineDrawable = drawable;
        requestLayout();
    }

    /**
     * 选中特定的数据项 ，快速的滚动到目标项
     * <p/>
     * 这个序号是于我们的数据在数组中的序号
     *
     * @param index 序号
     */
    public void setSelectedItem(int index) {

        if (mSelectListener != null) {
            mSelectListener.onSelectChanged(index);
        }
        if (index <= 0) {
            index = 0;
        }
        this.selectedPos = index;
        smoothScrollToPosition(index);
    }

    /**
     * 设置是否不可见的数据显示相对中间的数据小一点。
     * 默认为false
     *
     * @param isCurve 是否为curve效果
     * @deprecated 目前还有bug，暂时不要用
     */
    public void setCurve(boolean isCurve) {
        this.isCurve = isCurve;
    }

    /**
     * 设置可见数据项是否为渐显的效果,默认为true
     *
     * @param gradient 是否渐显
     */
    public void setIsGradient(boolean gradient) {
        this.gradient = gradient;
    }

    public boolean isGradient() {
        return gradient;
    }

    public boolean isCurve() {
        return isCurve;
    }

    /**
     * 设置显示的数据项数，最少3个 {@link RecycleWheelView#MINI_VISIBLE_ITEM} ，最多显示11个 {@link RecycleWheelView#MAX_VISIBLE_ITEM}
     * 默认为5
     * 但如果显示的空间不够大，不会调整childView的大小
     *
     * @param visibleItemSize 显示的可见项目数
     */
    public void setVisibleItem(int visibleItemSize) {

        if (visibleItemSize <= 0) {
            visibleItemSize = MINI_VISIBLE_ITEM;
        } else if (visibleItemSize >= MAX_VISIBLE_ITEM) {
            visibleItemSize = MAX_VISIBLE_ITEM;
        }

        this.visibleItemSize = visibleItemSize;
    }

    /**
     * 设置显示在中间的数据边的文字，EG: 身高，年等
     *
     * @param lable 显示标枪内容
     */
    public void setLable(String lable) {
        this.lable = lable;
    }

    /**
     * 设置标签字的大小
     *
     * @param size 单位为DP
     */
    public void setLabelTextSize(int size) {
        labelTextPaint.setTextSize(ViewUtils.dp2px(getContext(), size));
    }

    /**
     * 设置标签的颜色
     *
     * @param color 形式：0xFF000000,不要扔一个ID->R.color.black
     */
    public void setLableTextColor(@ColorInt int color) {
        labelTextPaint.setColor(color);
    }

}
