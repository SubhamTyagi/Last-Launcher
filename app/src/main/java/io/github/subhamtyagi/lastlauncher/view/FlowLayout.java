package io.github.subhamtyagi.lastlauncher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import io.github.subhamtyagi.lastlauncher.R;

public class FlowLayout extends ViewGroup {

    private int line_height;

    private int paddingHorizontal;
    private int paddingVertical;

    private void init() {
        paddingHorizontal = getResources().getDimensionPixelSize(R.dimen.flowlayout_horizontal_padding);
        paddingVertical = getResources().getDimensionPixelSize(R.dimen.flowlayout_vertical_padding);
    }


    public static class LayoutParams extends ViewGroup.LayoutParams {

        public final int horizontal_spacing;
        public final int vertical_spacing;

        /**
         * @param horizontal_spacing Pixels between items, horizontally
         * @param vertical_spacing   Pixels between items, vertically
         */
        public LayoutParams(int horizontal_spacing, int vertical_spacing) {
            super(0, 0);
            this.horizontal_spacing = horizontal_spacing;
            this.vertical_spacing = vertical_spacing;
        }
    }

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }
    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();
        int line_height = 0;
        final int width = resolveSize(100, widthMeasureSpec);
        //final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
         int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
       // int height=0;

        final int count = getChildCount();

        int childHeightMeasureSpec;
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        } else {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }


        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), childHeightMeasureSpec);
                final int childw = child.getMeasuredWidth();
                line_height = Math.max(line_height, child.getMeasuredHeight() + lp.vertical_spacing);

                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }

                xpos += childw + lp.horizontal_spacing;
                 line_height = child.getMeasuredHeight() + lp.vertical_spacing;
            }
        }
        this.line_height = line_height;


        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = ypos + line_height;

        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if (ypos + line_height < height) {
                height = ypos + line_height;
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(1, 1); // default of 1px spacing
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateLayoutParams(
            android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(1, 1);

    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return true;
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        //int lineHeight = 0;
        int lastHorizontalSpacing = 0;
        int rowStartIdx = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (xpos + childw > width) {
                    final int freeSpace = width - xpos + lastHorizontalSpacing;
                    xpos = getPaddingLeft() + freeSpace / 2;

                    for (int j = rowStartIdx; j < i; ++j) {
                        final View drawChild = getChildAt(j);
                        drawChild.layout(xpos, ypos, xpos + drawChild.getMeasuredWidth(), ypos + drawChild.getMeasuredHeight());
                        xpos += drawChild.getMeasuredWidth() + ((LayoutParams) drawChild.getLayoutParams()).horizontal_spacing;
                    }

                    lastHorizontalSpacing = 0;

                    xpos = getPaddingLeft();
                    ypos += line_height;

                    rowStartIdx = i;
                }
                // lineHeight = child.getMeasuredHeight() + lp.vertical_spacing;
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + lp.horizontal_spacing;
                lastHorizontalSpacing = lp.horizontal_spacing;

            }
        }
        if (rowStartIdx < count) {
            final int freeSpace = width - xpos + lastHorizontalSpacing;
            xpos = getPaddingLeft() + freeSpace / 2;

            for (int j = rowStartIdx; j < count; ++j) {
                final View drawChild = getChildAt(j);
                drawChild.layout(xpos, ypos, xpos + drawChild.getMeasuredWidth(), ypos + drawChild.getMeasuredHeight());
                xpos += drawChild.getMeasuredWidth() + ((LayoutParams) drawChild.getLayoutParams()).horizontal_spacing;
            }
        }
    }
}