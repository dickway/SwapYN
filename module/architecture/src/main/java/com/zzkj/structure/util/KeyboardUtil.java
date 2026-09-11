package com.zzkj.structure.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.blankj.utilcode.util.LogUtils;
import com.zzkj.structure.base.BaseApp;

public class KeyboardUtil {

    @SuppressLint("ClickableViewAccessibility")
    public static View.OnTouchListener onClickBlankHideKeyboardListener = new View.OnTouchListener() {
        private boolean isNeedHide = true;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isNeedHide = true;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    isNeedHide = false;
                    break;
                case MotionEvent.ACTION_UP:
                    if (isNeedHide && isShouldHideKeyboard(view)) {
                        hideKeyboard(view.getWindowToken());
                    }
                    break;
            }
            return false;
        }
    };

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @return
     */
    private static boolean isShouldHideKeyboard(View v) {
        if (v != null && !(v instanceof EditText)) {  //判断得到的焦点控件是否包含EditText
            return true;
        }
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    public static void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) BaseApp.Companion.getINSTANCE().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            LogUtils.w("HideKeyboard token is null");
        }
    }

    /**
     * 显示软键盘
     *
     * @param view
     */
    public static void showKeyboard(View view) {
        showKeyboard(view, 0);
    }

    /**
     * 显示软键盘
     *
     * @param view
     * @param flag {@link InputMethodManager#SHOW_FORCED}
     */
    public static void showKeyboard(View view, int flag) {
        if (view != null) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.postDelayed(() -> {
                InputMethodManager im = (InputMethodManager) BaseApp.Companion.getINSTANCE().getSystemService(Context.INPUT_METHOD_SERVICE);
                im.showSoftInput(view, flag);
            }, 200);
        } else {
            LogUtils.w("ShowKeyboard view is null");
        }
    }

//    public static boolean isShowing(Activity activity) {
//        //获取当屏幕内容的高度
//        int screenHeight = activity.getWindow().getDecorView().getHeight();
//        //获取View可见区域的bottom
//        Rect rect = new Rect();
//        //DecorView即为activity的顶级view
//        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//        //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
////        int visibleHeight = rect.bottom + BarKtxKt.getNavBarHeight();
//        int visibleHeight = rect.bottom;
//        return screenHeight - visibleHeight > 200;
//    }
}
