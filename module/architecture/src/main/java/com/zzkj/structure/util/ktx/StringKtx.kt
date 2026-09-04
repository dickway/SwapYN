package com.zzkj.structure.util.ktx

import java.security.MessageDigest
import java.util.regex.Pattern

/**
 * @author lmk
 * @date 2022/2/18
 * @description
 */
fun CharSequence?.realLength(trim: Boolean = true) =
    this?.let { if (trim) trim().length else length } ?: 0

// 转整型或0
fun String?.toIntOrZero(): Int = this?.toDoubleOrNull()?.toInt() ?: 0
fun String?.toIntOrNullX(): Int? = this?.toDoubleOrNull()?.toInt()

fun String?.md5(): String {
    if (isNullOrEmpty()) return ""
    val hash = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    val hex = StringBuilder(hash.size * 2)
    for (b in hash) {
        var str = Integer.toHexString(b.toInt())
        if (b < 0x10) {
            str = "0$str"
        }
        hex.append(str.substring(str.length - 2))
    }
    return hex.toString().lowercase()
}

inline fun <T : CharSequence> T?.ifNullOrBlank(defaultValue: () -> T): T =
    if (isNullOrBlank()) defaultValue() else this

private const val REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
fun CharSequence?.isEmail(): Boolean {
    return !isNullOrEmpty() && Pattern.matches(REGEX_EMAIL, this)
}
