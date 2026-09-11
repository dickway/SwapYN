package com.key

import android.content.Context
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.singular.sdk.Singular
import com.singular.sdk.SingularConfig


object DiffKey {
    const val API_URL = "https://www.vantasyx.com"
    const val BASE_ACT_AI = "alviora?a=ai&m="
    const val BASE_ACT_USER = "alviora?a=user&m="
    const val BASE_ACT_API = "alviora?a=api&m="
    const val BASE_ACT_LOGIN = "alviora?a=login&m="
    const val BASE_ACT_PAY = "alviora?a=pay&m="
    const val BASE_ACT_STATISTICS = "alviora?a=statistics&m="

    // TODO: Google Login
    const val GOOGLE_WEB_CLIENT_ID =
        "840936533819-chpjk7gprdu98dk1pjf24t2bhql3bbv5.apps.googleusercontent.com"

    // TODO: Singular
    const val SINGULAR_KEY =
        "6b857c971de018f634ec12e30ef712f61fec13f90ae923fb02e815e30ee123f60ab518af0fb24b_sl"
    const val SINGULAR_SECRET =
        "15d4667371e3051677b2524476e3034277b2534520ec511022ec04402db2034526ed5440_sl"


    fun initSingular(
        mContext: Context,
        config: SingularConfig,
        singularListener: SingularListener
    ) {
        config.withSingularDeviceAttribution { attribution ->
            singularListener.withSingularDeviceAttribution(true, attribution)
        }
        Singular.init(mContext, config)
        Singular.trackingOptIn()
    }


    fun initAdjust(context: Context, ajKey: String) {
        val config = AdjustConfig(context, ajKey, AdjustConfig.ENVIRONMENT_PRODUCTION).apply {
            setLogLevel(LogLevel.WARN)
        }
        Adjust.initSdk(config)
    }
}