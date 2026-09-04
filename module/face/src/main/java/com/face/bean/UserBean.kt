package com.face.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.realLength
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * @author 再战科技
 * @date 2022/4/1
 * @description
 */
@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class UserBean(
    @Json(name = "avatar")
    val avatar: String = "",
    @Json(name = "id")
    val userId: Int = 0,
    @Json(name = "newTask_num")
    val newTaskNum: Int = 0,
    @Json(name = "login_type")
    val loginType: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "token")
    val token: String = "",
    @Json(name = "username")
    val username: String = "",
    @Json(name = "email")
    val email: String = "",
    @Json(name = "vip_end_time")
    var expireMillis: Long = 0,
    @Json(name = "exp_vip")
    var expVip: Long = 0,
    @Json(name = "media_num")
    var mediaNum: Int = 0,
    @Json(name = "vipLv")
    var vipLv: Int = 0,
    @Json(name = "tflops")
    var tflops: Int = 0,


) : Parcelable {
    //头像
    fun avatarImg() = avatar.takeIf { it.isNotBlank() }.toString()

    // 昵称首字母
    fun firstNickname() = name.takeIf { it.isNotBlank() }?.first()?.toString() ?: ""

    // 游客登录昵称
    fun guestName() = name.takeIf { it.length > 10 }?.substring(0, 10) ?: ""

    // 是否登录
    fun isLogin() = token.realLength() > 0

    // 是否是VIP
    fun isVip() = expireMillis > System.currentTimeMillis()

    // VIP到期时间
    fun vipTime() = TimeUtil.getFormatDate(expireMillis, "yyyy/MM/dd") ?: ""

    // VIP到期时间
    fun vipTime2() = TimeUtil.getFormatDate(expireMillis, "dd/MM/yyyy") ?: ""
}

