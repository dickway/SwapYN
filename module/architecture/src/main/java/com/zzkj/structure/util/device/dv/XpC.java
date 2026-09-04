package com.zzkj.structure.util.device.dv;

import android.os.Process;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class XpC {
    private static final String[] classNames = {"de.robv.android.xposed.XposedHelpers", "andhook.lib.xposed.XposedHelpers", "com.whale.xposed.XposedHelpers"};

    public static boolean hasXPStack() {
        try {
            throw new Exception("");
        } catch (Exception localException) {
            StackTraceElement[] arrayOfStackTraceElement = localException.getStackTrace();
            for (StackTraceElement ste : arrayOfStackTraceElement) {
                for (String classname : classNames) {
                    if (ste.getClassName().contains(classname)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasXPFile() {
        try {
            Object localObject = new HashSet();
            BufferedReader localBufferedReader =
                    new BufferedReader(new FileReader("/proc/" + Process.myPid() + "/maps"));
            while (true) {
                String str = localBufferedReader.readLine();
                if (str == null) {
                    break;
                }
                if ((str.endsWith(".so")) || (str.endsWith(".jar"))) {
                    ((Set) localObject).add(str.substring(str.lastIndexOf(" ") + 1));
                }
            }
            localBufferedReader.close();
            localObject = ((Set) localObject).iterator();
            while (((Iterator) localObject).hasNext()) {
                boolean bool = ((String) ((Iterator) localObject).next()).toLowerCase().contains("xposedbridge");
                if (bool) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean hasXPClass() {
        Object localObject = null;
        for (String clssname : classNames) {
            try {
                localObject = ClassLoader.getSystemClassLoader()
                        .loadClass(clssname).newInstance();
            } catch (Throwable throwable) {
            }
            if (localObject != null) {
                return true;
            }
            try {
                localObject = Class.forName(clssname).newInstance();
            } catch (Throwable throwable) {
            }
            if (localObject != null) {
                return true;
            }
        }
        return false;
    }
}
