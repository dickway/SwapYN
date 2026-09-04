package com.zzkj.structure.util.device.dv;

import android.content.Context;
import android.view.accessibility.AccessibilityManager;

public final class Y71 {
    public static boolean A00(Context context) {
        if (Boolean.getBoolean("is_accessibility_enabled")) {
            return true;
        }
        if (context != null) {
            AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
            if (accessibilityManager != null) {
                return accessibilityManager.isEnabled();
            }
        }
        return false;
    }

    public static boolean A01(Context context) {
        if (Boolean.getBoolean("is_accessibility_enabled")) {
            return true;
        }
        if (context != null) {
            AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
            if (accessibilityManager != null) {
                return accessibilityManager.isTouchExplorationEnabled();
            }
        }
        return false;
    }
}
