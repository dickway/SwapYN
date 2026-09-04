package com.zzkj.structure.util.device.dv;

import android.os.SystemProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class SS2 {
    public static final List A02;
    public String A00 = "N/A";
    public String A01 = "others";

    static {
        A02 = new ArrayList();
        A02.add("msmnile");
        A02.add("trinket");
        A02.add("kona");
        A02.add("atoll");
        A02.add("lito");
        A02.add("bengal");
        A02.add("lahaina");
    }

    public SS2() {
        String str = null;
        String str2 = SystemProperties.get("ro.board.platform");
        if (str2 == null || str2.isEmpty()) {
            str2 = SystemProperties.get("ro.mediatek.platform");
            if (str2 == null || str2.isEmpty()) {
                str2 = SystemProperties.get("ro.mediatek.hardware");
                if (str2 == null || str2.isEmpty()) {
                    return;
                }
            }
        }
        String lowerCase = str2.toLowerCase(Locale.ENGLISH);
        if (lowerCase.startsWith("msm") || lowerCase.startsWith("apq") || lowerCase.startsWith("sdm") || lowerCase.startsWith("sm") || A02.contains(lowerCase)) {
            str = "qualcomm";
        } else if (lowerCase.startsWith("exynos") || lowerCase.startsWith("universal") || lowerCase.startsWith("erd")) {
            this.A01 = "samsung";
            String str3 = SystemProperties.get("ro.chipname");
            if (str3 == null || str3.isEmpty()) {
                str3 = SystemProperties.get("ro.hardware.chipname");
            }
            if (!str3.isEmpty()) {
                lowerCase = str3;
            }
            this.A00 = lowerCase;
        } else if (lowerCase.startsWith("mt")) {
            str = "mediatek";
        } else if (lowerCase.startsWith("sc") || lowerCase.startsWith("sp9") || lowerCase.startsWith("sp7")) {
            str = "spreadtrum";
        } else if (lowerCase.startsWith("hi") || lowerCase.startsWith("kirin")) {
            str = "hisilicon";
        } else if (lowerCase.startsWith("rk")) {
            str = "rockchip";
        } else {
            if (lowerCase.startsWith("bcm")) {
                str = "broadcom";
            }
            this.A00 = lowerCase;
        }
        this.A01 = str;
        this.A00 = lowerCase;
    }
}