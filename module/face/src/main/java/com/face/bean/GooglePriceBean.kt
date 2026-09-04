package com.face.bean

import android.os.Parcelable
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.apkfuns.logutils.LogUtils
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * @author 再战科技
 * @date 2023/12/18
 * @description
 */
@JsonClass(generateAdapter = true)
@Parcelize
data class GooglePriceBean(
    @Json(name = "productId")
    val productId: String = "",
    @Json(name = "type")
    val type: String = "",
    @Json(name = "unit")
    val unit: String = "",
    @Json(name = "currencyCode")
    val currencyCode: String = "",
    @Json(name = "price")
    val price: String = "",
    @Json(name = "formattedPrice")
    val formattedPrice: String = "",
    @Json(name = "oldGooglePrice")
    val oldGooglePrice: String = "",
    @Json(name = "trialPriceTitle")
    var trialTitle: String = "",
) : Parcelable {

    companion object {
        val regex = "[\\d\\.*\\,]+".toRegex()

        fun toBean(bean: ProductDetails): GooglePriceBean {
            // e.g. "HK$38.00"
            val formattedPrice = if (bean.productType == BillingClient.ProductType.SUBS) {
                bean.subscriptionOfferDetails?.getOrNull(0)?.pricingPhases
                    ?.pricingPhaseList?.getOrNull(0)?.formattedPrice
            } else {
                bean.oneTimePurchaseOfferDetails?.formattedPrice
            } ?: ""

            val pricingPrice = if (bean.productType == BillingClient.ProductType.SUBS) {
                bean.subscriptionOfferDetails?.getOrNull(0)?.pricingPhases
                    ?.pricingPhaseList?.getOrNull(1)?.formattedPrice
            } else {
                ""
            } ?: ""

            val priceCode = if (bean.productType == BillingClient.ProductType.SUBS) {
                bean.subscriptionOfferDetails?.getOrNull(0)?.pricingPhases
                    ?.pricingPhaseList?.getOrNull(0)?.priceCurrencyCode
            } else {
                bean.oneTimePurchaseOfferDetails?.priceCurrencyCode
            } ?: ""

            var tt = ""
            if (bean.productType == BillingClient.ProductType.SUBS &&
                bean.subscriptionOfferDetails?.getOrNull(0)?.offerId != null
            ) {
                tt = bean.subscriptionOfferDetails?.getOrNull(0)?.pricingPhases
                    ?.pricingPhaseList?.getOrNull(0)?.billingPeriod ?: ""
            }

            val p = regex.find(formattedPrice)?.value ?: ""
            val u = formattedPrice.replace(p, "")

            val op = regex.find(pricingPrice)?.value ?: p
            return GooglePriceBean(
                productId = bean.productId,
                type = bean.productType,
                unit = u,
                currencyCode = priceCode,
                price = p,
                formattedPrice = formattedPrice,
                oldGooglePrice = op,
                trialTitle = tt,
            )
        }

        fun toBeans(beans: List<ProductDetails>?): List<GooglePriceBean>? =
            if (beans == null) null else List(beans.size) { toBean(beans[it]) }
    }
}
