package com.face.bean

import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class UserRewardBean(
    val adUnitId: String = "",
    val networkName: String = "",
    val creativeId: String? = "",
    val dspId: String? = "",
    val dspName: String? = "",
    val revenue: Double,
    val amount: Int,
    val revenuePrecision: String? = "",
    val placement: String? = "",
    val networkPlacement: String? = "",
    val label: String? = "",
) : Parcelable