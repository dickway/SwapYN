package com.face.bean

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.math.RoundingMode
import java.text.DecimalFormat

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class VipSchemeBean(
    //{"id":101,"name":"aipoints10","price":4.00,"old_price":4.00,"day":10,"type":1,"remark":null,"description":null,"package_name":null,"productId":"aipoints10","subscription":0,"create_time":1727143524000}
//{"id":12,"name":"1 Yearly","price":35.99,"old_price":99.99,"day":365,"type":1,"remark":"1460","description":"Cancel anytime in 'Google Play -> Payments & Subscriptions'.","package_name":null,"productId":"1_year_discount","subscription":1,"create_time":1727076848000}
    @Json(name = "id")
    var id: Int = 0,
    @Json(name = "day")
    val day: Int = 0,
    @Json(name = "zkday")
    val zkday: Int = 0,
    @Json(name = "name")
    val name: String = "",
    @Json(name = "old_price")
    val oldPrice: Float = 0f,
    @Json(name = "price")
    val price: Float = 0f,
    @Json(name = "productId")
    var productId: String = "",//forever 表示商品，其他表示订阅
    @Json(name = "type")
    val type: Int = 1,
    @Json(name = "remark")
    val remark: String = "",
    @Json(name = "description")
    val description: String = "",
    // 显示的Google折扣价价格
    @Json(name = "showPrice")
    var showPrice: String = "",
    // 显示的Google原价格
    @Json(name = "oldGooglePrice")
    var oldGooglePrice: String? = null,
    //商品原价显示
    @Json(name = "oldShowPrice")
    var oldShowPrice: String? = "--",
    @Json(name = "formattedPrice")
    var formattedPrice: String? = null,
    @Json(name = "subscription")
    var subscription: Int = 1,
    // 显示的Google折扣时间
    @Json(name = "trialPriceTitle")
    var trialPriceTitle: String = "",

    // 显示的Google货币单位
    @Json(name = "showUnit")
    var showUnit: String = "",
    // 显示的Google货币代码
    @Json(name = "showCode")
    var showCode: String = "",

    ) : Parcelable {

    fun getBeanPrice() =
        if (oldGooglePrice != null && oldGooglePrice != "") oldGooglePrice else "--"


    // 商品类型
    fun getProductId() = subscription == 0//0 表示商品，其他表示订阅

    companion object {
        val differCallback = object : DiffUtil.ItemCallback<VipSchemeBean>() {
            override fun areItemsTheSame(oldItem: VipSchemeBean, newItem: VipSchemeBean): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: VipSchemeBean,
                newItem: VipSchemeBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
