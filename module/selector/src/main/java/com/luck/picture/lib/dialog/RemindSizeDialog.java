package com.luck.picture.lib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.luck.picture.lib.R;

public class RemindSizeDialog extends Dialog implements View.OnClickListener {
    private final TextView btnOk;
    private final TextView tvContent;

    public RemindSizeDialog(Context context, String tips) {
        super(context, R.style.Picture_Theme_Dialog);
        setContentView(R.layout.ps_remind_size_dialog);
        btnOk = findViewById(R.id.btnOk);
        tvContent = findViewById(R.id.tv_content);
        tvContent.setText(tips);
        btnOk.setOnClickListener(this);
        setDialogSize();
    }

    @Deprecated
    public static Dialog showTipsDialog(Context context, String tips) {
        return new RemindSizeDialog(context, tips);
    }

    public static RemindSizeDialog buildDialog(Context context, String tips) {
        return new RemindSizeDialog(context, tips);
    }

    public void setButtonText(String text) {
        btnOk.setText(text);
    }

    public void setButtonTextColor(int color) {
        btnOk.setTextColor(color);
    }

    public void setContent(String text) {
        tvContent.setText(text);
    }

    public void setContentTextColor(int color) {
        tvContent.setTextColor(color);
    }

    private void setDialogSize() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        getWindow().setWindowAnimations(R.style.PictureThemeDialogWindowStyle);
        getWindow().setAttributes(params);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnOk) {
            if (listener != null) {
                listener.onClick(view);
            } else {
                dismiss();
            }
        }
    }

    private OnDialogClickListener listener;

    public void setOnDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    public interface OnDialogClickListener {
        void onClick(View view);
    }
}
