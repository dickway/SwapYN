package com.zzkj.structure.util.device.dv;

import android.text.TextUtils;

public class MgC {
    public static boolean hasMG() {
        try {
            String str = Utils.runtimeExec("which magisk");
            if (str != null && !TextUtils.isEmpty(str.trim())) {
                return true;
            }
            str = Utils.runtimeExec("ls /sbin/magisk");
            if (str != null
                    && str.toLowerCase().contains("magisk")
                    && !str.toLowerCase().contains("permission")
                    && !str.toLowerCase().contains("no such")) {
                return true;
            }
        } catch (Throwable e) {
        }
        return false;
    }
}
