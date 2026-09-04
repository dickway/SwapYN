package com.face.bean

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class VideoAipointBean(
    @Json(name = "type2")
    val type2: Int = 0,
    @Json(name = "type2_original")
    val type2Original: Int = 0,
    @Json(name = "type3")
    val type3: Int = 19,
    @Json(name = "type3_original")
    val type3Original: Int = 100,
    @Json(name = "type5_original")
    val type5Original: Int = 39,
    @Json(name = "type5")
    val type5: Int = 200,
    @Json(name = "type_pic")
    val typePic: Int = 0,
    @Json(name = "type_pic_original")
    val typePicOriginal: Int = 0,
) : Parcelable

