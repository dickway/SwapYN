package com.zzkj.structure.util.device.dv;

import android.os.Process;

import java.util.UUID;

public class Fvl {
    public static UUID getUUID() {
        try {
            return UUID.randomUUID();
        } catch (Throwable e) {
            return UUID.nameUUIDFromBytes(String.format("%s-%s-%s", new Object[]{Long.valueOf(System.currentTimeMillis()), Long.valueOf(System.nanoTime()), Integer.valueOf(Process.myTid())}).getBytes());
        }
    }

}
