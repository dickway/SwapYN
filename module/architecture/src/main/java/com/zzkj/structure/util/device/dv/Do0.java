package com.zzkj.structure.util.device.dv;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class Do0 {
    public static Do0 A07;
    public static final long A08 = TimeUnit.MINUTES.toMillis(2);
    public long A00;
    public final Lock A01 = new ReentrantLock();
    public volatile StatFs A02 = null;
    public volatile StatFs A03 = null;
    public volatile File A04;
    public volatile File A05;
    public volatile boolean A06 = false;

    public static StatFs A00(StatFs statFs, File file) throws Throwable {
        if (file != null && file.exists()) {
            if (statFs == null) {
                try {
                    return new StatFs(file.getAbsolutePath());
                } catch (IllegalArgumentException unused) {
                } catch (Throwable th) {
                    Class<Error> cls = Error.class;
                    if (!cls.isInstance(th)) {
                        Class<RuntimeException> cls2 = RuntimeException.class;
                        if (!cls2.isInstance(th)) {
                            throw new RuntimeException(th);
                        }
                        throw ((Throwable) cls2.cast(th));
                    }
                    throw ((Throwable) cls.cast(th));
                }
            } else {
                statFs.restat(file.getAbsolutePath());
                return statFs;
            }
        }
        return null;
    }

    public static synchronized Do0 A01() {
        Do0 r0;
        synchronized (Do0.class) {
            r0 = A07;
            if (r0 == null) {
                r0 = new Do0();
                A07 = r0;
            }
        }
        return r0;
    }

    private void A02() throws Throwable {
        Lock lock = this.A01;
        if (lock.tryLock()) {
            try {
                if (SystemClock.uptimeMillis() - this.A00 > A08) {
                    A04(this);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public static void A03(Do0 r2) throws Throwable {
        if (!r2.A06) {
            Lock lock = r2.A01;
            lock.lock();
            try {
                if (!r2.A06) {
                    r2.A05 = Environment.getDataDirectory();
                    r2.A04 = Environment.getExternalStorageDirectory();
                    A04(r2);
                    r2.A06 = true;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public static void A04(Do0 r2) throws Throwable {
        r2.A03 = A00(r2.A03, r2.A05);
        r2.A02 = A00(r2.A02, r2.A04);
        r2.A00 = SystemClock.uptimeMillis();
    }

    public final long A05() {
        try {
            A03(this);
            A02();
        } catch (Throwable e) {
        }
        StatFs statFs = this.A02;
        if (statFs != null) {
            return statFs.getBlockSizeLong() * statFs.getBlockCountLong();
        }
        return -1;
    }

    public final long A06(Integer num) throws Throwable {
        StatFs statFs;
        A03(this);
        A02();
        if (num == 0) {
            statFs = this.A03;
        } else {
            statFs = this.A02;
        }
        if (statFs != null) {
            return statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
        }
        return 0;
    }

    public final long A07(Integer num) throws Throwable {
        StatFs statFs;
        A03(this);
        A02();
        if (num == 0) {
            statFs = this.A03;
        } else {
            statFs = this.A02;
        }
        if (statFs != null) {
            return statFs.getBlockSizeLong() * statFs.getFreeBlocksLong();
        }
        return -1;
    }

    public static final long MIN_DISK_FREE_FOR_MIXED_MODE = 419430400;

    public final boolean A08() throws Throwable {
        if (A06(0) < MIN_DISK_FREE_FOR_MIXED_MODE) {
            return true;
        }
        return false;
    }

    public final boolean A09() throws Throwable {
        if (A06(0) < 104857600) {
            return true;
        }
        return false;
    }

    public static long A00(File file, int i) {
        long j = 0;
        if (file == null || !file.exists()) {
            return 0;
        }
        if (file.isFile()) {
            long j2 = (long) i;
            return (((file.length() + j2) - 1) / j2) * j2;
        } else if (!file.isDirectory()) {
            return 0;
        } else {
            File[] listFiles = file.listFiles();
            if (listFiles == null) {
                return 0;
            }
            int length = listFiles.length;
            while (true) {
                length--;
                if (length < 0) {
                    return j;
                }
                j += A00(listFiles[length], i);
            }
        }
    }

    public static final Map<String,String> A01(Context context) {
        HashMap hashMap = new HashMap();
        try {
            File filesDir = context.getFilesDir();
            if (filesDir != null) {
                int blockSize = new StatFs(filesDir.getPath()).getBlockSize();
                hashMap.put("internalTotalSpaceInMb", Long.toString(filesDir.getTotalSpace() / 1048576));
                hashMap.put("internalUsableSpaceInMb", Long.toString(filesDir.getUsableSpace() / 1048576));
                hashMap.put("internalUsedInMb", Long.toString(A00(filesDir, blockSize) / 1048576));
                hashMap.put("internalCacheUsedInMb", Long.toString(A00(context.getCacheDir(), blockSize) / 1048576));
                hashMap.put("appUsedInMb", Long.toString(A00(new File(context.getApplicationInfo().publicSourceDir), blockSize) / 1048576));
            }
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                int blockSize2 = new StatFs(externalCacheDir.getPath()).getBlockSize();
                hashMap.put("externalTotalSpaceInMb", Long.toString(externalCacheDir.getTotalSpace() / 1048576));
                hashMap.put("externalUsableSpaceInMb", Long.toString(externalCacheDir.getUsableSpace() / 1048576));
                hashMap.put("externalFileUsedInMb", Long.toString(A00(externalCacheDir, blockSize2) / 1048576));
                return hashMap;
            }
        } catch (IllegalArgumentException e) {
        }
        return hashMap;
    }

}