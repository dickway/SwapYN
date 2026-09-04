-keep class com.king.drawboard.** {*;}

-keep class java.util.Locale { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
