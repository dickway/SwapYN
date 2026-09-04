package com.zzkj.structure.util.device.dv;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.provider.Settings.Secure;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

public final class Fqz {
    public static boolean A00(Context context) {
        return Secure.getInt(context.getContentResolver(), "accessibility_display_inversion_enabled", 0) != 0;
    }

    public static boolean A01(Context context, String str) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getApplicationContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager != null) {
            List<AccessibilityServiceInfo> enabledAccessibilityServiceList = accessibilityManager.getEnabledAccessibilityServiceList(-1);
            if (enabledAccessibilityServiceList != null) {
                for (AccessibilityServiceInfo id : enabledAccessibilityServiceList) {
                    if (id.getId().contains(str)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}