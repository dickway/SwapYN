package com.luck.picture.lib.interfaces;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

/**
 * @author：luck
 * @date：2020-01-14 17:08
 * @describe：onResult Callback Listener
 */
public interface OnResultCallbackListener<T> {
    /**
     * return LocalMedia result
     *
     * @param result
     */
    void onResult(ArrayList<T> result, Fragment context);

    /**
     * Cancel
     */
    void onCancel();

    void onHint(FragmentActivity activity);

    void onCamera();
}
