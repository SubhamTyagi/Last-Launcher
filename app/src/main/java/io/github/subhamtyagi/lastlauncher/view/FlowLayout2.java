package io.github.subhamtyagi.lastlauncher.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import io.github.subhamtyagi.lastlauncher.R;

public class FlowLayout2 extends ViewGroup {

    private int paddingHorizontal;
    private int paddingVertical;

    public FlowLayout2(Context context) {
        super(context);
        init();
    }

    public FlowLayout2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paddingHorizontal = getResources().getDimensionPixelSize(R.dimen.flowlayout_horizontal_padding);
        paddingVertical = getResources().getDimensionPixelSize(R.dimen.flowlayout_vertical_padding);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int lineHeight = 0;

        // 100 is a dummy number, widthMeasureSpec should always be EXACTLY for FlowLayout
        int myWidth = resolveSize(100, widthMeasureSpec);
        int wantedHeight = 0;

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            // let the child measure itself
            child.measure(
                    getChildMeasureSpec(widthMeasureSpec, 0, child.getLayoutParams().width),
                    getChildMeasureSpec(heightMeasureSpec, 0, child.getLayoutParams().height));
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            // lineheight is the height of current line, should be the height of the heightest view
            lineHeight = Math.max(childHeight, lineHeight);
            if (childWidth + childLeft + getPaddingRight() > myWidth) {
                // wrap this line
                childLeft = getPaddingLeft();
                childTop += paddingVertical + lineHeight;
                lineHeight = childHeight;
            }
            childLeft += childWidth + paddingHorizontal;
        }
        wantedHeight += childTop + lineHeight + getPaddingBottom();
        setMeasuredDimension(myWidth, resolveSize(wantedHeight, heightMeasureSpec));
    }

   @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childLeft = getPaddingLeft();//x
        int childTop = getPaddingTop();//y
        int lineHeight = 0;

        int myWidth = right - left;//d

        ///in
        int lastHorizontalSpacing = 0;
        int rowStartIdx = 0;
        ///

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            lineHeight = Math.max(childHeight, lineHeight);
            if (childWidth + childLeft + getPaddingRight() > myWidth) {
                childLeft = getPaddingLeft();
                childTop += paddingVertical + lineHeight;
                lineHeight = childHeight;
            }
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            childLeft += childWidth + paddingHorizontal;
        }
    }


    /*@Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        int lineHeight = 0;
        int lastHorizontalSpacing = 0;
        int rowStartIdx = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                lineHeight = Math.max(childh, lineHeight);
                if (xpos + childw > width) {
                    final int freeSpace = width - xpos + lastHorizontalSpacing;
                    xpos = getPaddingLeft() + freeSpace / 2;

                    for (int j = rowStartIdx; j < i; ++j) {
                        final View drawChild = getChildAt(j);
                        drawChild.layout(xpos, ypos, xpos + drawChild.getMeasuredWidth(), ypos + drawChild.getMeasuredHeight());
                        xpos += drawChild.getMeasuredWidth() +paddingHorizontal;
                    }

                    lastHorizontalSpacing = 0;

                    xpos = getPaddingLeft();
                    ypos += lineHeight;

                    rowStartIdx = i;
                }
                // lineHeight = child.getMeasuredHeight() + lp.vertical_spacing;
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + paddingHorizontal;
                lastHorizontalSpacing = paddingHorizontal;

            }
        }
        if (rowStartIdx < count) {
            final int freeSpace = width - xpos + lastHorizontalSpacing;
            xpos = getPaddingLeft() + freeSpace / 2;

            for (int j = rowStartIdx; j < count; ++j) {
                final View drawChild = getChildAt(j);
                drawChild.layout(xpos, ypos, xpos + drawChild.getMeasuredWidth(), ypos + drawChild.getMeasuredHeight());
                xpos += drawChild.getMeasuredWidth() + paddingHorizontal;
            }
        }
    }*/
}