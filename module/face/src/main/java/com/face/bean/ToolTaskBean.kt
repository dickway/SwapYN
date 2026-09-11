package com.face.bean


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Ignore
import com.blankj.utilcode.util.LogUtils
import com.face.R
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.moshi.MoshiHelper
import kotlinx.parcelize.IgnoredOnParcel

@JsonClass(generateAdapter = true)
@Parcelize
data class ToolTaskBean(
    @Json(name = "create_time")
    var createTime: Long = 0,
    @Json(name = "id")
    var id: String = "",
    @Json(name = "name")
    var name: String = "",
    @Json(name = "record_url")
    var recordUrl: String = "",
    @Json(name = "type")
    var type: String = "",
    @Json(name = "user_id")
    val userId: String = "",
    @Json(name = "task_id")
    var taskId: String = "",
    @Json(name = "state")
    var state: Int = 2,
    @Json(name = "error_message")
    var errorMessage: String = "",
    @Json(name = "content")
    var txtContent: String = "",
    ) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var itemType: Int = ITEM_TYPE_LIST

    @IgnoredOnParcel
    @Ignore
    @Json(ignore = true)
    var subList: MutableList<ToolTaskBean>? = null


    //返回文生图的生成信息
    fun getinfo() = txtContent.let {
        try {
            MoshiHelper.adapter(TxTContent::class.java).fromJson(txtContent)
        } catch (e: Exception) {
            TxTContent()
        }
    }

    //返回抠图的加载链接
    fun getCutUrl() = recordUrl.let {
        try {
            val dataBean = MoshiHelper.adapter(UrlCutBean::class.java).fromJson(recordUrl)
            "${dataBean?.result?.data},${dataBean?.result?.dataWater}"
        } catch (e: Exception) {
            recordUrl
        }
    }

    //返回原始链接
    fun getSource() = recordUrl.let {
        try {
            val dataBean = MoshiHelper.adapter(UrlVoiceBean::class.java).fromJson(recordUrl)
            dataBean?.source ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    //根据设置的语言
    fun getUrl() = recordUrl.let {
        try {
            val dataBean = MoshiHelper.adapter(UrlVoiceBean::class.java).fromJson(recordUrl)
            var urlResult = dataBean?.result?.find { it.lang.contains("EN") }?.url
            dataBean?.result?.forEach {
                if (it.lang.lowercase() == SPbaseUtils.spLanguage) {
                    urlResult = it.url
                }
            }
            urlResult
        } catch (e: Exception) {
            ""
        }
    }

    //朗读获取播放录音
    fun getInputUrl() = recordUrl.let {
        try {
            val dataBean = MoshiHelper.adapter(UrlVoiceBean::class.java).fromJson(recordUrl)
            dataBean?.result?.get(0)?.url
        } catch (e: Exception) {
            ""
        }
    }

    //朗读获取时间
    fun getUrlTime() = recordUrl.let {
        try {
            val dataBean = MoshiHelper.adapter(UrlVoiceBean::class.java).fromJson(it)
            val urlTime = dataBean?.result?.get(0)?.duration ?: 0
            var minute = (urlTime / 60 % 60).toString()
            if (minute.length < 2) minute = "0$minute"
            var second = (urlTime % 60).toString()
            if (second.length < 2) second = "0$second"
            "$minute:$second"
        } catch (e: Exception) {
            ""
        }
    }

    fun getTime() = createTime.let {
        if (TimeUtil.isSameDay(it, System.currentTimeMillis())) {
            getStringX(R.string.today)
        } else if (TimeUtil.isYestoday(it, System.currentTimeMillis())) {
            getStringX(R.string.yesterday)
        } else {
            TimeUtil.getFormatDate(it, "MMMM dd,yyyy")
        }
    }

    companion object {
        const val ITEM_TYPE_HLIST = 1
        const val ITEM_TYPE_LIST = 2
        val differCallback = object : DiffUtil.ItemCallback<ToolTaskBean>() {
            override fun areItemsTheSame(
                oldItem: ToolTaskBean,
                newItem: ToolTaskBean
            ): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: ToolTaskBean,
                newItem: ToolTaskBean
            ): Boolean {
                return false
            }
        }
    }
}

@JsonClass(generateAdapter = true)
@Parcelize
data class TxTContent(
    @Json(name = "flag")
    var flag: String = "",
    @Json(name = "width")
    var width: String = "",
    @Json(name = "model")
    var model: String = "",
    @Json(name = "text")
    var text: String = "",
    @Json(name = "height")
    val height: String = "",
) : Parcelable