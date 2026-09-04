package com.face.bean

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@JsonClass(generateAdapter = true)
@Parcelize
data class NotificationBean(
    @Json(name = "notification_id") var notificatioId: Int = 0,
    @Json(name = "task_id") var taskId: String? = "",
) : Parcelable

