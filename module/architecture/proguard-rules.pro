# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


############### LogUtils #################
-keep class com.apkfuns.**{*;}
-keep class me.pqpo.librarylog4a.**{*;}
-keepattributes InnerClasses

-keep class android.support.v8.renderscript.** { *; }
-keep class androidx.renderscript.** { *; }
-keep class com.hjq.toast.** {*;}


-keep public class com.android.installreferrer.** { *; }

-keepclassmembers class *{
     public<init>(org.json.JSONObject);
}
-keepclassmembers enum *{
      publicstatic**[] values();
      publicstatic** valueOf(java.lang.String);
}

-keep class java.util.Locale { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}


-keep class org.repackage.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# bannerviewpager
-keep class androidx.recyclerview.widget.**{*;}
-keep class androidx.viewpager2.widget.**{*;}


# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response

 # With R8 full mode generic signatures are stripped for classes that are not
 # kept. Suspend functions are wrapped in continuations where the type argument
 # is used.
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-keep class com.umeng.** {*;}

-keep class com.uc.** { *; }

-keep class com.efs.** { *; }

############ Firebase Crashlytics  #################
 # Keep file names and line numbers.
-keepattributes SourceFile,LineNumberTable
# Optional: Keep custom exceptions.
-keep public class * extends java.lang.Exception



-dontwarn java.awt.**
-dontwarn javax.**
-dontwarn org.**
-dontwarn springfox.**


-optimizationpasses 5

# R文件下面的资源
-keep class **.R$* {*;}
# 本地的native方法（JNI）
-keepclasseswithmembernames class * {
      native <methods>;
}
# For using GSON @Expose annotation
-keepattributes *Annotation*
# For using GSON @Expose annotation
-keepattributes Signature

-keepattributes InnerClasses

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# Parcelable序列化和Creator静态成员变量
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
# WebView
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}


-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, java.lang.Boolean);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}


-keep class xyz.doikki.videoplayer.** { *; }
-dontwarn xyz.doikki.videoplayer.**

# IjkPlayer
-keep class tv.danmaku.ijk.** { *; }
-dontwarn tv.danmaku.ijk.**

# ExoPlayer
-keep class com.google.android.exoplayer2.** { *; }
-dontwarn com.google.android.exoplayer2.**

-keep class android.databinding.** { *; }
-dontwarn android.databinding.**

-keep public class * extends androidx.databinding.ViewDataBinding{
    public static <methods>;
}
