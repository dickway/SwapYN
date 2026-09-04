package com.zzkj.structure.util.device.dv;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public final class Qt0 {
    public static Qt0 A04;
    public int A00 = 0;
    public int A01 = Integer.MAX_VALUE;
    public int A02;
    public int A03;

    public static final long A00(Context context) {
        MemoryInfo memoryInfo = new MemoryInfo();
        ((ActivityManager) context.getSystemService("activity")).getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem;
    }

    public static Qt0 A01() {
        Qt0 r0 = A04;
        if (r0 != null) {
            return r0;
        }
        Qt0 r02 = new Qt0();
        A04 = r02;
        return r02;
    }

    public static void A02(Qt0 r3, int i) throws Exception {
        File file = new File(String.format("/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq", i));
        if (file.exists()) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            try {
                int parseInt = Integer.parseInt(bufferedReader.readLine());
                if (parseInt > r3.A00) {
                    r3.A00 = parseInt;
                }
                if (parseInt < r3.A01) {
                    r3.A01 = parseInt;
                }
            } finally {
                bufferedReader.close();
            }
        }
    }

    public final int A03() {
        int A05 = A05();
        if (A05 == -1) {
            return A04();
        }
        return A05;
    }

    public final int A04() {
        int i = this.A03;
        if (i != 0) {
            return i;
        }
        int max = Math.max(Runtime.getRuntime().availableProcessors(), 1);
        this.A03 = max;
        return max;
    }

    public final int A05() {
        int i = this.A02;
        if (i == 0) {
            try {
                i = new File("/sys/devices/system/cpu/").listFiles(new Qu0(this)).length;
                this.A02 = i;
                if (i == 0) {
                    this.A02 = -1;
                    i = -1;
                }
            } catch (Exception e) {
                this.A02 = -1;
                return -1;
            }
        }
        return i;
    }
}