package com.zzkj.structure.util

import android.text.TextUtils
import com.blankj.utilcode.util.LogUtils
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


/**
 * @author lmk
 * @date 2022/2/21
 * @description
 */
object TimeUtil {

    private const val DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm"
    const val DEFAULT_DATE_TIME_DATA = "yyyy-MM-dd HH:mm:ss"

    /**
     * @param timestamp
     * @return 格式化后的时间 yyyy-MM-dd
     */
    fun getFormatDate(timestamp: Long, pattern: String = DEFAULT_DATE_TIME_PATTERN): String? {
        return if (timestamp == 0L) "" else getFormatDate(Date(timestamp), pattern)
    }

    fun getFormatDate(date: Date, pattern: String = DEFAULT_DATE_TIME_PATTERN): String? {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }

    /**
     * 根据不同情况返回不同格式
     * @param timestamp Long
     * @param todayPattern String 和今天是同一天
     * @param sameYearPattern String 和今年是同一年
     * @param diffYearPattern String 和今年是不同年
     * @return String?
     */
    fun getFriendlyDate(
        timestamp: Long,
        todayPattern: String = "HH:mm",
        sameYearPattern: String = "MM/dd HH:mm",
        diffYearPattern: String = "MM/dd/yyyy HH:mm"
    ): String? {
        val curr = System.currentTimeMillis()
        return when {
            isSameDay(timestamp, curr) -> getFormatDate(timestamp, todayPattern)
            isSameYear(timestamp, curr) -> getFormatDate(timestamp, sameYearPattern)
            else -> getFormatDate(timestamp, diffYearPattern)
        }
    }

    fun isSameDay(t1: Long, t2: Long) =
        getFormatDate(t1, "yyyyMMdd") == getFormatDate(t2, "yyyyMMdd")

    /**
     * 判断是不是昨天、明天
     * @param day1
     * @param day2
     * @return
     */
    fun isYestoday(day1: Long, day2: Long): Boolean {
        val instance: Calendar = Calendar.getInstance()
        instance.setTimeInMillis(day1)
        val d1: Int = instance.get(Calendar.DAY_OF_YEAR)
        instance.setTimeInMillis(day2)
        val d2: Int = instance.get(Calendar.DAY_OF_YEAR)
        return d1 - d2 == 1 || d2 - d1 == 1
    }

    fun isSameMonth(t1: Long, t2: Long) =
        getFormatDate(t1, "yyyyMM") == getFormatDate(t2, "yyyyMM")

    fun isSameYear(t1: Long, t2: Long) =
        getFormatDate(t1, "yyyy") == getFormatDate(t2, "yyyy")

    fun convertSecondsToTime(seconds: Long): String {
        var timeStr: String? = null
        var hour = 0
        var minute = 0
        var second = 0
        if (seconds <= 0) return "00:00" else {
            minute = seconds.toInt() / 60
            if (minute < 60) {
                second = seconds.toInt() % 60
                timeStr = unitFormat(minute) + ":" + unitFormat(second)
            } else {
                hour = minute / 60
                if (hour > 99) return "99:59:59"
                minute = minute % 60
                second = (seconds - hour * 3600 - minute * 60).toInt()
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second)
            }
        }
        return timeStr
    }

    fun convertSecondsToFormat(seconds: Long, format: String?): String {
        if (TextUtils.isEmpty(format)) return ""
        val date = Date(seconds)
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(date)
    }

    private fun unitFormat(i: Int): String {
        var retStr: String? = null
        retStr = if (i in 0..9) "0$i" else "" + i
        return retStr
    }

    /**
     * 把秒转换成文字描述（00:00:00）
     *
     * @param second
     * @return
     */
    fun second3Describe(second: Int): String {
        return when {
            second < 60 -> {
                "00:00:" + if (second >= 10) second else "0$second"
            }

            second / 60 < 60 -> {
                val m = second / 60
                val s = second - m * 60
                "00:" + (if (m >= 10) m.toString() else "0$m") + ":" + if (s >= 10) s else "0$s"
            }

            else -> {
                val h = second / 60 / 60
                val m = (second - h * 60 * 60) / 60
                val s = second - h * 60 * 60 - m * 60
                (if (h >= 10) h else "0$h").toString() + ":" + (if (m >= 10) m else "0$m") + ":" + if (s >= 10) s else "0$s"
            }
        }
    }

    /**
     * 把秒转换成文字描述（00:00）
     *
     * @param second
     * @return
     */
    fun second2Describe(second: Int): String {
        return when {
            second < 60 -> {
                "00:" + if (second >= 10) second else "0$second"
            }

            second / 60 < 60 -> {
                val m = second / 60
                val s = second - m * 60
                (if (m >= 10) m.toString() else "0$m") + ":" + if (s >= 10) s else "0$s"
            }

            else -> {
                val h = second / 60 / 60
                val m = (second - h * 60 * 60) / 60
                val s = second - h * 60 * 60 - m * 60
                (if (h >= 10) h else "0$h").toString() + ":" + (if (m >= 10) m else "0$m") + ":" + if (s >= 10) s else "0$s"
            }
        }
    }

    fun getTimeZone(): String {
        val timeZ = TimeZone.getDefault()
        var timezone: String = timeZ.getDisplayName(
            timeZ.inDaylightTime(Date()), TimeZone.SHORT, Locale.CHINESE
        ).run {
            replace("+", "")
        }
        if (timezone.contains(":")) {
            timezone = timezone.removeRange(timezone.lastIndexOf(":"), timezone.length)
        }
        return timezone
    }


    fun getCompleteTimeZone(): String {
        val timeZ = TimeZone.getDefault()
        val timezone: String = timeZ.getDisplayName(
            timeZ.inDaylightTime(Date()), TimeZone.SHORT, Locale.CHINESE
        )
        return timezone
    }

    /**
     * String转换时间
     * @return Long
     */
    fun getString2Time(time: String): Long {
        if (time == "") return 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(time)
        return date?.time ?: 0
    }


//    /**
//     * 转换小时
//     * @param hour Int 0-23
//     * @param to TimeZone
//     * @return Int
//     */
//    fun transformHour(hour: Int, to: TimeZone) =
//        Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, hour)
//            timeZone = to
//        }.get(Calendar.HOUR_OF_DAY)


    /**
     * 转换小时
     * @param hour Int 0-23
     * @param from TimeZone
     * @param to TimeZone
     * @return Int
     */
    fun transformHour(hour: Int, from: TimeZone, to: TimeZone) = Calendar.getInstance(from).apply {
        set(Calendar.HOUR_OF_DAY, hour)
        val oldTime = time
        timeZone = to
        time = oldTime
    }.get(Calendar.HOUR_OF_DAY)
}