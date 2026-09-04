package com.zzkj.structure.util.device.dv;

import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;

import java.lang.reflect.InvocationTargetException;

public final class PO0 {
    public final NotificationManager A00;
    public final Context A01;

    public PO0(Context context) {
        this.A01 = context;
        this.A00 = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static PO0 A00(Context context) {
        return new PO0(context);
    }

    public final boolean A03() {
        if (VERSION.SDK_INT >= 24) {
            return this.A00.areNotificationsEnabled();
        }
        boolean z = true;
        Context context = this.A01;
        Object systemService = context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        String packageName = context.getApplicationContext().getPackageName();
        int i = applicationInfo.uid;
        try {
            Class cls = Class.forName(AppOpsManager.class.getName());
            Class cls2 = Integer.TYPE;
            if (((Integer) cls.getMethod("checkOpNoThrow", new Class[]{cls2, cls2, String.class}).invoke(systemService, new Object[]{Integer.valueOf(((Integer) cls.getDeclaredField("OP_POST_NOTIFICATION").get(Integer.class)).intValue()), Integer.valueOf(i), packageName})).intValue() != 0) {
                z = false;
            }
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException | RuntimeException | InvocationTargetException unused) {
        }
        return z;
    }
}