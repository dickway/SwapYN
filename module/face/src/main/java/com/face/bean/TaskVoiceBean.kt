package com.face.bean


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.zzkj.structure.util.moshi.MoshiHelper
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class TaskVoiceBean(
    @Json(name = "content")
    val content: String = "",
    @Json(name = "createTime")
    val createTime: Long = 0,
    @Json(name = "ecount")
    val ecount: Int = 0,
    @Json(name = "ee")
    val ee: String = "",
    @Json(name = "estimatedTime")
    val estimatedTime: Long = 0,
    @Json(name = "exec_time")
    val execTime: Long = 0,
    @Json(name = "id")
    val id: String = "",
    @Json(name = "idate")
    val idate: String = "",
    @Json(name = "node")
    val node: String = "",
    @Json(name = "package_name")
    val packageName: String = "",
    @Json(name = "params")
    val params: String = "",
    @Json(name = "reportTime")
    val reportTime: Long = 0,
    @Json(name = "result")
    val result: String = "",
    @Json(name = "sendTime")
    val sendTime: String = "",
    @Json(name = "state")
    val state: Int = 0,
    @Json(name = "task_type")
    val taskType: String = "",
    @Json(name = "uid")
    val uid: String = ""
) : Parcelable{

    //返回原始链接
    fun getUrl() = content.let {
        val dataBean = MoshiHelper.adapter(conSource::class.java).fromJson(content)
        "${dataBean?.source}"
    }
}
@JsonClass(generateAdapter = true)
@Parcelize
data class conSource(
    @Json(name = "background")
    val background: String = "",
    @Json(name = "height")
    val height: String = "",
    @Json(name = "source")
    val source: String = "",
    @Json(name = "style")
    val style: String = "",
    @Json(name = "watermarker")
    val watermarker: String = "",
    @Json(name = "width")
    val width: String = ""
) : Parcelable
