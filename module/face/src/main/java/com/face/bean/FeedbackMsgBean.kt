package com.face.bean

import android.os.Parcelable
import com.zzkj.structure.util.ktx.size
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class FeedbackMsgBean(
    @Json(name = "content")
    var content: String? = null,
    @Json(name = "imgUrls")
    var imgUrls: List<String>? = null,
    @Json(name = "videoUrls")
    var videoUrls: List<String>? = null,
) : Parcelable {
    fun hasVideo() = videoUrls.size() > 0
    fun hasImage() = imgUrls.size() > 0
    fun hasMedia() = hasVideo() || hasImage()
}