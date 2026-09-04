package com.zzkj.structure.util.device.dv;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public final class T40 {
    public static int A00;
    public static String A01;

    public static int A00(Context context) {
        if (A00 == 0) {
            try {
                A00 = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            } catch (NameNotFoundException unused) {
            }
        }
        return A00;
    }

    public static String A01(Context context) {
        String str = A01;
        if (str != null) {
            return str;
        }
        String valueOf = String.valueOf(context.getApplicationInfo().loadLabel(context.getPackageManager()));
        A01 = valueOf;
        return valueOf;
    }

    public static final int LOAD_RESULT_PGO_ATTEMPTED = 65536;

//    public static String A02(Context context) {
//        Intent intent = new Intent("android.intent.action.MAIN");
//        intent.addCategory("android.intent.category.HOME");
//        try {
//            ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(intent, LOAD_RESULT_PGO_ATTEMPTED);
//            if (resolveActivity != null) {
//                ActivityInfo activityInfo = resolveActivity.activityInfo;
//                if (activityInfo != null) {
//                    return activityInfo.packageName;
//                }
//            }
//        } catch (RuntimeException unused) {
//        }
//        return "";
//    }
}