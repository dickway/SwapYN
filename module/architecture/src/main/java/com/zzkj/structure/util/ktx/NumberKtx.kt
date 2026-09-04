package com.zzkj.structure.util.ktx

import androidx.annotation.IntRange
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * @author 再战科技
 * @date 2022/9/26
 * @description
 */
fun Number.saveDecimals(
    @IntRange(from = 1) digit: Int,
    mode: RoundingMode = RoundingMode.HALF_UP
): String {
    return DecimalFormat("0.${"#".repeat(digit)}").apply {
        roundingMode = mode
    }.format(this)
}