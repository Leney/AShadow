package com.mosect.ashadow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * 阴影相对布局，其子视图可以设置阴影
 */
public class ShadowRelativeLayout extends RelativeLayout {

    private ShadowHelper shadowHelper = new ShadowHelper();

    public ShadowRelativeLayout(Context context) {
        super(context);
    }

    public ShadowRelativeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShadowRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            clearChildShadow(child);
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child.getVisibility() == VISIBLE) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (null != lp.shadow) {
                shadowHelper.drawChildShadow(canvas, child, lp.shadow, lp.shadowX, lp.shadowY);
            }
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            Object key = lp.getShadowKey(isInEditMode());
            if (null == lp.shadow) {
                // lp.shadow = ShadowHelper.createShadow(lp.shadowKey);
                lp.shadow = ShadowHelper.getShadow(key);
            } else if (!key.equals(lp.shadow.getKey())) {
                // ShadowManager.getDefault().unbind(lp.shadow);
                // lp.shadow = ShadowHelper.createShadow(lp.shadowKey);
                lp.shadow = ShadowHelper.getShadow(key);
            }
        }
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        clearChildShadow(child);
    }

    private void clearChildShadow(View child) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (null != lp && null != lp.shadow) {
            // ShadowManager.getDefault().unbind(lp.shadow);
            lp.shadow = null;
        }
    }

    public static class LayoutParams extends RelativeLayout.LayoutParams {

        /**
         * 阴影信息
         */
        private final RoundShadow.Key shadowKey = new RoundShadow.Key();
        UnsupportedRoundShadow.Key editModeShadowKey;
        /**
         * 阴影X轴偏移量
         */
        public float shadowX;
        /**
         * 阴影Y轴偏移量
         */
        public float shadowY;
        Shadow shadow;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray ta = c.obtainStyledAttributes(attrs, R.styleable.ShadowRelativeLayout_Layout);
            shadowX = ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_shadowX, 0f);
            shadowY = ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_shadowY, 0f);
            shadowKey.shadowRadius = ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_shadowRadius, 0f);
            shadowKey.shadowColor = ta.getColor(R.styleable.ShadowRelativeLayout_Layout_layout_shadowColor, Color.BLACK);
            float round = ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_roundRadius, 0f);
            float roundLT = ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_roundRadiusLT, round);
            float roundRT = ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_roundRadiusRT, round);
            float roundRB = ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_roundRadiusRB, round);
            float roundLB = ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_roundRadiusLB, round);
            if (roundLT > 0 || roundRT > 0 || roundRB > 0 || roundLB > 0) {
                shadowKey.radii = new float[]{
                        roundLT, roundLT,
                        roundRT, roundRT,
                        roundRB, roundRB,
                        roundLB, roundLB,
                };
            } else {
                shadowKey.radii = null;
            }
            shadowKey.solidColor = ta.getColor(R.styleable.ShadowRelativeLayout_Layout_layout_solidColor, Color.BLACK);
            shadowKey.noSolid = ta.getBoolean(R.styleable.ShadowRelativeLayout_Layout_layout_noSolid, false);
            ta.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(RelativeLayout.LayoutParams source) {
            super(source);
        }

        @NonNull
        public RoundShadow.Key getShadowKey() {
            return shadowKey;
        }

        Object getShadowKey(boolean editMode) {
            if (editMode) {
                if (null == editModeShadowKey)
                    editModeShadowKey = new UnsupportedRoundShadow.Key();
                editModeShadowKey.set(shadowKey);
                return editModeShadowKey;
            }
            return shadowKey;
        }
    }
}
