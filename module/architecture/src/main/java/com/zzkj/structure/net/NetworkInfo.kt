package com.zzkj.structure.net

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.zzkj.structure.util.moshi.MoshiHelper
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class NetworkInfo(
    @Json(name = "hasNetwork")
    val hasNetwork: Boolean = false,
    @Json(name = "hasWifi")
    val hasWifi: Boolean = false,
    @Json(name = "hasCellular")
    val hasCellular: Boolean = false,
    @Json(name = "hasEthernet")
    val hasEthernet: Boolean = false,
    @Json(name = "hasVpn")
    val hasVpn: Boolean = false,
//    @Json(name = "wifiName")
//    val wifiName: String = "",
    @Json(name = "wifiIp")
    val wifiIp: String = "",
) : Parcelable {

    @IgnoredOnParcel
    @Json(ignore = true)
    val toJson: String = MoshiHelper.adapter(NetworkInfo::class.java).toJson(this)
}
