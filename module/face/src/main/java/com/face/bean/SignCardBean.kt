package com.face.bean

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class SignCardBean(
    @Json(name = "card")
    val card: Int = 0,
    @Json(name = "TFLOPS_num")
    val tFLOPSNum: Int = 0,
    @Json(name = "tflops")
    val tflops: Int = 0,
    @Json(name = "card_num")
    val cardNum: Int = 0,
) : Parcelable


