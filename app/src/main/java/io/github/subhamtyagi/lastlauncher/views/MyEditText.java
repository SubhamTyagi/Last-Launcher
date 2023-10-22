
package io.github.subhamtyagi.lastlauncher.views.textview;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

/**
 * An EditText, which notifies when something was cut/copied/pasted inside it.
 *
 * @author Lukas Knuth
 * @version 1.0
 */
@SuppressLint("NewApi")
public class MyEditText extends EditText implements
        MenuItem.OnMenuItemClickListener {
    private static final int ID_SELECTION_MODE = android.R.id.selectTextMode;
    // Selection context mode
    private static final int ID_SELECT_ALL = android.R.id.selectAll;
    private static final int ID_SELECT_START = android.R.id.startSelectingText;
    private static final int ID_SELECT_END = android.R.id.stopSelectingText;
    private static final int ID_SELECT = android.R.id.selectedIcon;
    private static final int ID_CUT = android.R.id.cut;
    private static final int ID_COPY = android.R.id.copy;
    private static final int ID_PASTE = android.R.id.paste;

    private final Context mContext;

    /*
     * Just the constructors to create a new EditText...
     */
    public MyEditText(Context context) {
        super(context);
        this.mContext = context;
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }
    //代码效果，有弹出框选择 粘贴，复制，剪切，类似qq效果.....
    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        menu.add(0, ID_PASTE, 0, "paste").setOnMenuItemClickListener(this);
        menu.add(0, ID_CUT, 1, "cut").setOnMenuItemClickListener(this);
        menu.add(0, ID_COPY, 1, "copy").setOnMenuItemClickListener(this);
        menu.add(0, ID_SELECT_ALL, 1, "select all").setOnMenuItemClickListener(this);
        super.onCreateContextMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return onTextContextMenuItem(item.getItemId());
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        boolean consumed = super.onTextContextMenuItem(id);
        switch (id) {
            case android.R.id.cut:
                onTextCut();
                break;
            case android.R.id.paste:
                onTextPaste();
                break;
            case android.R.id.copy:
                onTextCopy();
        }
        return consumed;
    }

    /**
     * Text was cut from this EditText.
     */
    public void onTextCut() {Toast.makeText(mContext, "Cut success", Toast.LENGTH_SHORT).show();}
    /**
     * Text was copied from this EditText.
     */
    public void onTextCopy() {Toast.makeText(mContext, "Copy success", Toast.LENGTH_SHORT).show();}
    /**
     * Text was pasted into the EditText.
     */
    public void onTextPaste() {
        Toast.makeText(mContext, "Paste sucess", Toast.LENGTH_SHORT).show();
    }


}




//public static class Builder {
//    private TextView mTextView;
//    private int mCursorHandleColor = 0xFF1379D6;
//    private int mSelectedColor = 0xFFAFE1F4;
//    private float mCursorHandleSizeInDp = 24;
//
//    public Builder(TextView textView) {
//        mTextView = textView;
//    }
//
//    public Builder setCursorHandleColor(@ColorInt int cursorHandleColor) {
//        mCursorHandleColor = cursorHandleColor;
//        return this;
//    }
//
//    public Builder setCursorHandleSizeInDp(float cursorHandleSizeInDp) {
//        mCursorHandleSizeInDp = cursorHandleSizeInDp;
//        return this;
//    }
//
//    public Builder setSelectedColor(@ColorInt int selectedBgColor) {
//        mSelectedColor = selectedBgColor;
//        return this;
//    }
//
//    public SelectableTextHelper build() {
//        return new SelectableTextHelper(this);
//    }
//}
//}


