package com.zzkj.structure.util.device;

import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.content.Context.WIFI_SERVICE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Debug;
import android.os.Process;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import androidx.annotation.RequiresPermission;

import com.blankj.utilcode.util.LogUtils;
import com.zzkj.structure.base.BaseApp;
import com.zzkj.structure.util.StringUtils;
import com.zzkj.structure.util.device.dv.Do0;
import com.zzkj.structure.util.device.dv.Fqz;
import com.zzkj.structure.util.device.dv.MgC;
import com.zzkj.structure.util.device.dv.SS2;
import com.zzkj.structure.util.device.dv.Utils;
import com.zzkj.structure.util.device.dv.XpC;
import com.zzkj.structure.util.device.dv.Y71;

import java.io.File;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public final class DeviceUtils {
    private static String ANDROID_ID;
    private static String SERIAL_NO;

    private DeviceUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Return the version name of device's system.
     *
     * @return the version name of device's system
     */
    public static String getSDKVersionName() {
        return Build.VERSION.RELEASE;
    }

    /**
     * Return version code of device's system.
     *
     * @return version code of device's system
     */
    public static int getSDKVersionCode() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * @return 手机商家
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * @return 手机型号
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * Return the android id of device.
     *
     * @return the android id of device
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidID() {
        if (StringUtils.isNotEmpty(ANDROID_ID)) {
            return ANDROID_ID;
        }
        String id = Settings.Secure.getString(
                BaseApp.Companion.getINSTANCE().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        if ("9774d56d682e549c".equals(id)) return "";
        return id == null ? "" : (ANDROID_ID = id);
    }

    public static String getSerialNo() {
        if (StringUtils.isNotEmpty(SERIAL_NO)) {
            return SERIAL_NO;
        }
        ShellUtils.CommandResult result = ShellUtils.execCmd("getprop", false);
        if (result.result == 0 && StringUtils.isNotEmpty(result.successMsg)) {
            String[] split = result.successMsg.split("\n");
            String serialNo = "";
            for (String s : split) {
                if (s.contains("ro.serialno")) {
                    LogUtils.i(s);
                    serialNo = s.substring(s.lastIndexOf('['), s.length() - 1);
                    LogUtils.i(serialNo);
                }
            }
            return SERIAL_NO = serialNo;
        } else {
            return "";
        }
    }


    /**
     * Return the MAC address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />},
     * {@code <uses-permission android:name="android.permission.INTERNET" />},
     * {@code <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />}</p>
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, CHANGE_WIFI_STATE})
    public static String getMacAddress() {
        String macAddress = getMacAddress((String[]) null);
        if (!TextUtils.isEmpty(macAddress) || getWifiEnabled()) return macAddress;
        setWifiEnabled(true);
        setWifiEnabled(false);
        return getMacAddress((String[]) null);
    }

    public static boolean getWifiEnabled() {
        @SuppressLint("WifiManagerLeak")
        WifiManager manager = (WifiManager) BaseApp.Companion.getINSTANCE().getSystemService(WIFI_SERVICE);
        if (manager == null) return false;
        return manager.isWifiEnabled();
    }

    /**
     * Enable or disable wifi.
     * <p>Must hold {@code <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />}</p>
     *
     * @param enabled True to enabled, false otherwise.
     */
    @RequiresPermission(CHANGE_WIFI_STATE)
    public static void setWifiEnabled(final boolean enabled) {
        try {
            @SuppressLint("WifiManagerLeak")
            WifiManager manager = (WifiManager) BaseApp.Companion.getINSTANCE().getSystemService(WIFI_SERVICE);
            if (manager == null) return;
            if (enabled == manager.isWifiEnabled()) return;
            manager.setWifiEnabled(enabled);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * Return the MAC address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />},
     * {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @return the MAC address
     */
    @RequiresPermission(allOf = {ACCESS_WIFI_STATE})
    public static String getMacAddress(final String... excepts) {
        String macAddress = getMacAddressByNetworkInterface();
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        macAddress = getMacAddressByInetAddress();
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        macAddress = getMacAddressByWifiInfo();
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        macAddress = getMacAddressByFile();
        if (isAddressNotInExcepts(macAddress, excepts)) {
            return macAddress;
        }
        return "";
    }

    private static boolean isAddressNotInExcepts(final String address, final String... excepts) {
        if (TextUtils.isEmpty(address)) {
            return false;
        }
        if ("02:00:00:00:00:00".equals(address)) {
            return false;
        }
        if (excepts == null || excepts.length == 0) {
            return true;
        }
        for (String filter : excepts) {
            if (filter != null && filter.equals(address)) {
                return false;
            }
        }
        return true;
    }

    @RequiresPermission(ACCESS_WIFI_STATE)
    private static String getMacAddressByWifiInfo() {
        try {
            final WifiManager wifi = (WifiManager) BaseApp.Companion.getINSTANCE()
                    .getApplicationContext().getSystemService(WIFI_SERVICE);
            if (wifi != null) {
                final WifiInfo info = wifi.getConnectionInfo();
                if (info != null) {
                    @SuppressLint("HardwareIds")
                    String macAddress = info.getMacAddress();
                    if (!TextUtils.isEmpty(macAddress)) {
                        return macAddress;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static String getMacAddressByNetworkInterface() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (ni == null || !ni.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = ni.getHardwareAddress();
                if (macBytes != null && macBytes.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : macBytes) {
                        sb.append(String.format("%02x:", b));
                    }
                    return sb.substring(0, sb.length() - 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static String getMacAddressByInetAddress() {
        try {
            InetAddress inetAddress = getInetAddress();
            if (inetAddress != null) {
                NetworkInterface ni = NetworkInterface.getByInetAddress(inetAddress);
                if (ni != null) {
                    byte[] macBytes = ni.getHardwareAddress();
                    if (macBytes != null && macBytes.length > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (byte b : macBytes) {
                            sb.append(String.format("%02x:", b));
                        }
                        return sb.substring(0, sb.length() - 1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    private static InetAddress getInetAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String hostAddress = inetAddress.getHostAddress();
                        if (hostAddress.indexOf(':') < 0) return inetAddress;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getMacAddressByFile() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("getprop wifi.interface", false);
        if (result.result == 0) {
            String name = result.successMsg;
            if (name != null) {
                result = ShellUtils.execCmd("cat /sys/class/net/" + name + "/address", false);
                if (result.result == 0) {
                    String address = result.successMsg;
                    if (address != null && address.length() > 0) {
                        return address;
                    }
                }
            }
        }
        return "02:00:00:00:00:00";
    }


    @RequiresPermission(allOf = {ACCESS_WIFI_STATE, CHANGE_WIFI_STATE})
    public static HashMap<String, Object> getAllInfo(Context context) {
        HashMap<String, Object> map;
        map = new HashMap<>();
        map.put("collectVersion", "1.1");

        map.put("osType", "Android");
        map.put("timeInfo", getTimeInfo());
        map.put("androidId", getAndroidID());
        map.put("mac", getMacAddress());
        map.put("deviceVPN", isVPN());
        map.put("deviceProxySet", isProxy());
        map.put("storageSpace", getStorageSpace(context));
        map.put("displayInfo", getDisplayInfo(context));

        map.put("isRoot", isRoot());
        map.put("hasXP", hasXP());
        map.put("hasMG", MgC.hasMG());
        map.put("isJailBroken", isDeviceJailBroken());
        map.put("canCallPhone", context.getPackageManager().hasSystemFeature("android.hardware.telephony"));
        map.put("isEmulator", isEmulator());
        map.put("buildField", getBuildField());
        map.put("languageInfo", getLanguageInfo());

        map.put("accessibilityInfo", getAccessibilityInfo(context));
        map.put("runtimeInfo", getRuntimeInfo(context));
        map.put("appInstallInfo", getAppInstallInfo(context));
//        map.put("appStorePackageName", getStorePackageName(context));
        map.put("hardwareKeyboardInfo", getHardwareKeyboard());
        return map;
    }

    /**
     * @return cpu信息
     */
    public static Map<String, Object> getCpuInfo() {
        Map<String, Object> map = new HashMap<>();
        SS2 r0 = new SS2();
        map.put("socInfo", r0.A00 + ":" + r0.A01);
        String cpuPath = "/proc/cpuinfo";
        String deviceCpuFile = Utils.File2String(new File(cpuPath));
        map.put("deviceCpuFile", TextUtils.isEmpty(deviceCpuFile) ? "" : deviceCpuFile);
        String DeviceCpuCat = Utils.runtimeExec("cat " + cpuPath);
        map.put("deviceCpuCat", TextUtils.isEmpty(DeviceCpuCat) ? "" : DeviceCpuCat);
        return map;
    }

    /**
     * @return 应用安装信息
     */
    public static Map<String, Object> getAppInstallInfo(Context context) {
        Map<String, Object> map = new HashMap<>();
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            map.put("appFirstInstallTime", packageInfo.firstInstallTime);
            map.put("appLastUpdateTime", packageInfo.lastUpdateTime);

            String installerPackageName = context.getPackageManager().getInstallerPackageName(context.getPackageName());
            map.put("installerPackageName", installerPackageName == null ? "" : installerPackageName);

            int flags = context.getApplicationInfo().flags;
            String installType;
            if ((flags & 1) == 0) {
                installType = "system_app";
            } else if ((flags & 128) != 0) {
                installType = "updated_system_app";
            } else {
                installType = "user_installed_app";
            }
            map.put("installType", installType);
        } catch (Throwable ignored) {
        }
        return map;
    }

//    /**
//     * @return 启动器信息
//     */
//    public static Map<String, Object> getLauncherInfo(Context context) {
//        Map<String, Object> map = new HashMap<>();
//        String packageName = T40.A02(context);
//        int versionCode;
//        try {
//            versionCode = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
//        } catch (PackageManager.NameNotFoundException unused2) {
//            versionCode = -1;
//        }
//        map.put("packageName", packageName);
//        map.put("versionCode", versionCode);
//        return map;
//    }

    /**
     * @return 屏幕信息
     */
    public static Map<String, Object> getDisplayInfo(Context context) {
        Map<String, Object> map = new HashMap<>();
        DisplayMetrics displayMetrics2 = context.getResources().getDisplayMetrics();
        map.put("density", displayMetrics2.density);
        map.put("densityDpi", displayMetrics2.densityDpi);
        map.put("screenWidth", displayMetrics2.widthPixels);
        map.put("screenHeight", displayMetrics2.heightPixels);
        map.put("fontScale", Settings.System.getFloat(context.getContentResolver(), "font_scale", 1.0f));
        return map;
    }

    /**
     * @return 时间信息
     */
    public static Map<String, Object> getTimeInfo() {
        Map<String, Object> map = new HashMap<>();
        TimeZone timeZone = TimeZone.getDefault();
        Date date = new Date();
        map.put("deviceLocalTimezone", timeZone.getDisplayName(timeZone.inDaylightTime(date), TimeZone.LONG, Locale.ENGLISH));
        map.put("requestTimestamp", System.currentTimeMillis());
        map.put("timezoneOffsetMs", timeZone.getOffset(date.getTime()));
        map.put("deviceUptime", SystemClock.elapsedRealtime());
        map.put("deviceBootTime", System.currentTimeMillis() - SystemClock.elapsedRealtime());
        return map;
    }

    /**
     * @return 语言信息
     */
    public static Map<String, Object> getLanguageInfo() {
        Map<String, Object> map = new HashMap<>();
        map.put("language", Locale.getDefault().getLanguage());
        map.put("country", Locale.getDefault().getCountry());
        return map;
    }

    /**
     * @return 硬键盘
     */
    public static String getHardwareKeyboard() {
        int i4 = Resources.getSystem().getConfiguration().keyboard;
        String hardwareKeyboard = "";
        if (Resources.getSystem().getConfiguration().hardKeyboardHidden == 1 && (i4 == 2 || i4 == 3)) {
            if (Resources.getSystem().getConfiguration().keyboard == 2) {
                hardwareKeyboard = "qwerty";
            } else {
                hardwareKeyboard = "12key";
            }
        }
        return hardwareKeyboard;
    }

    /**
     * @return 是否开启vpn
     */
    public static boolean isVPN() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isUp() && networkInterface.getName().startsWith("tun")) {
                    return true;
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    /**
     * @return 是否使用代理
     */
    public static boolean isProxy() {
        return System.getProperty("http.proxyHost") != null;
    }

    /**
     * @return 存储信息
     */
    public static Map<String, String> getStorageSpace(Context context) {
        return Do0.A01(context);
    }

    /**
     * @return
     */
    public static Map<String, Object> getRuntimeInfo(Context context) {
        Map<String, Object> map = new HashMap<>();
        try {
            Runtime runtime = Runtime.getRuntime();
            map.put("javaTotal", runtime.totalMemory() >> 20);
            map.put("javaFree", runtime.freeMemory() >> 20);
            map.put("javaMax", runtime.maxMemory() >> 20);
            map.put("javaUsed", (runtime.totalMemory() - runtime.freeMemory()) >> 20);
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[]{Process.myPid()})[0];
            map.put("totalPss", memoryInfo.getTotalPss() >> 10);
            map.put("totalPrivateDirty", memoryInfo.getTotalPrivateDirty() >> 10);
            map.put("totalSharedDirty", memoryInfo.getTotalSharedDirty() >> 10);
            map.put("dalvikPrivateDirty", memoryInfo.dalvikPrivateDirty >> 10);
            map.put("dalvikPss", memoryInfo.dalvikPss >> 10);
            map.put("dalvikSharedDirty", memoryInfo.dalvikSharedDirty >> 10);
            map.put("nativePrivateDirty", memoryInfo.nativePrivateDirty >> 10);
            map.put("nativePss", memoryInfo.nativePss >> 10);
            map.put("nativeSharedDirty", memoryInfo.nativeSharedDirty >> 10);
            map.put("otherPrivateDirty", memoryInfo.otherPrivateDirty >> 10);
            map.put("otherPss", memoryInfo.otherPss >> 10);
            map.put("otherSharedDirty", memoryInfo.otherSharedDirty >> 10);
            ActivityManager.MemoryInfo activityMemoryInfo = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(activityMemoryInfo);
            map.put("totalMem", activityMemoryInfo.totalMem >> 20);
            map.put("availMem", activityMemoryInfo.availMem >> 20);
            map.put("lowMemory", activityMemoryInfo.lowMemory);
        } catch (Throwable ignore) {
        }
        return map;
    }

    /**
     * @return 无障碍信息
     */
    public static Map<String, Object> getAccessibilityInfo(Context context) {
        Map<String, Object> accessibilityMap = new HashMap<>();
        accessibilityMap.put("displayInversion", Fqz.A00(context));
        accessibilityMap.put("accessibilityEnabled", Y71.A00(context));
        accessibilityMap.put("touchExplorationEnabled", Y71.A01(context));
        accessibilityMap.put("talkbackEnabled", Fqz.A01(context, "TalkBackService"));
        accessibilityMap.put("switchAccessEnabled", Fqz.A01(context, "TalkBackService"));
        accessibilityMap.put("selectToSpeakEnabled", Fqz.A01(context, "TalkBackService"));
        return accessibilityMap;
    }

    /**
     * @return 是否有Xposed
     */
    public static boolean hasXP() {
        boolean deviceXP = false;
        if (XpC.hasXPClass() || XpC.hasXPFile() || XpC.hasXPStack()) {
            deviceXP = true;
        }
        return deviceXP;
    }

    /**
     * @return 是否有root
     */
    public static boolean isRoot() {
        String[] Paths = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
        boolean deviceRoot = false;
        for (String s : Paths) {
            if (new File(s + "su").isFile()) {
                deviceRoot = true;
                break;
            }
        }
        return deviceRoot;
    }

    public static Map<String, Object> getBuildField() {
        Map<String, Object> buildMap = new HashMap<>();
        for (Field field : Build.class.getFields()) {
            try {
                buildMap.put(field.getName(), field.get(null));
            } catch (Exception ignored) {
            }
        }
        for (Field field : Build.VERSION.class.getFields()) {
            try {
                buildMap.put(field.getName(), field.get(null));
            } catch (Exception ignored) {
            }
        }
        return buildMap;
    }

    /**
     * @return 是否修改系统
     */
    public static boolean isDeviceJailBroken() {
        boolean deviceJailBroken;
        if (!new File("/system/app/Superuser.apk").exists()) {
            String tags = Build.TAGS;
            if (tags == null || !tags.contains("test-keys")) {
                deviceJailBroken = false;
                String env = System.getenv("PATH");
                if (env != null) {
                    String[] split = env.split(":");
                    for (int i = 0; i < split.length; i++) {
                        if (deviceJailBroken) {
                            break;
                        }
                        File file = new File(split[i]);
                        if (file.exists() && file.isDirectory()) {
                            File[] listFiles = file.listFiles();
                            if (listFiles != null) {
                                for (File name : listFiles) {
                                    if (name.getName().equals("su")) {
                                        deviceJailBroken = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                deviceJailBroken = true;
            }
        } else {
            deviceJailBroken = true;
        }
        return deviceJailBroken;
    }

    /**
     * @return 是否是模拟器
     */
    public static boolean isEmulator() {
        String fingerprint = Build.FINGERPRINT;
        String str = "generic";
        boolean isEmulator = true;
        if (!fingerprint.startsWith(str) && !fingerprint.startsWith("unknown")) {
            String str1 = "google_sdk";
            String str2 = Build.MODEL;
            if (!str2.contains(str1)) {
                if (!str2.contains("Emulator") && !str2.contains("Android SDK built for x86")
                        && !Build.MANUFACTURER.contains("Genymotion")
                        && (!Build.BRAND.startsWith(str) || !Build.DEVICE.startsWith(str))) {
                    isEmulator = false;
                }
            }
        }
        return isEmulator;
    }

//    /**
//     * @param context
//     * @return 应用市场包名
//     */
//    public static String getStorePackageName(Context context) {
//        PackageManager packageManager = context.getPackageManager();
//        ResolveInfo resolveActivity = packageManager.resolveActivity(
//                new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + context.getPackageName())),
//                PackageManager.GET_INTENT_FILTERS);
//        String storePackageName = "null";
//        if (resolveActivity != null) {
//            ActivityInfo activityInfo = resolveActivity.activityInfo;
//            if (activityInfo != null) {
//                String str9 = activityInfo.name;
//                if (str9 != null) {
//                    if (!str9.contains("ResolverActivity")) {
//                        storePackageName = resolveActivity.activityInfo.packageName;
//                    }
//                }
//            }
//            if (TextUtils.isEmpty(storePackageName)) {
//                String[] A01 = {"com.android.vending", "com.google.android.gms", "com.google.market"};
//                int length = A01.length;
//                int i5 = 0;
//                while (true) {
//                    if (i5 >= length) {
//                        storePackageName = "unknown";
//                        break;
//                    }
//                    storePackageName = A01[i5];
//                    try {
//                        packageManager.getPackageInfo(storePackageName, PackageManager.GET_ACTIVITIES);
//                        break;
//                    } catch (PackageManager.NameNotFoundException unused) {
//                        i5++;
//                    }
//                }
//            }
//        }
//        return storePackageName;
//    }
}