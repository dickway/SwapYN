package com.zzkj.structure.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import com.apkfuns.logutils.LogUtils
import com.zzkj.structure.base.BaseApp
import java.util.Locale


object LanguageUtil {

    /**
     * 修改语言
     * @param activity 上下文
     */
    fun checkLanguage(context: Context) {
        var language = SPbaseUtils.spLanguage
        if (language.isEmpty()) {
            val locale = Locale.getDefault()
            val lang = locale.language.lowercase()
            val country = locale.country.lowercase()

            language = when {
                lang == "zh" && (country.equals("TW", true) || country.equals(
                    "HK",
                    true
                ) || country.equals("MO", true)) -> "zh-tw"
                lang.contains("zh") -> "zh"
                lang.contains("es") -> "es"
                lang.contains("de") -> "de"
                lang.contains("fr") -> "fr"
                lang.contains("sv") -> "sv"
                lang.contains("ar") -> "ar"
                lang.contains("ko") -> "ko"
                lang.contains("ja") -> "ja"
                lang.contains("ku") -> "ku"
                lang.contains("iw") -> "iw"
                lang.contains("fa") -> "fa"
                lang.contains("tr") -> "tr"
                lang.contains("it") -> "it"
                lang == "pt" && (country.equals("BR", true)) -> "pt-br"
                lang.contains("pt") -> "pt-pt"
                else -> "en"
            }
        }

        SPbaseUtils.spLanguage = language

        val locale = when (language) {
            "zh-tw" -> Locale("zh", "TW")
            "pt-br" -> Locale("pt", "BR")
            "pt-pt" -> Locale("pt", "PT")
            else -> Locale(language)
        }

        val config = context.resources.configuration
        config.setLocales(LocaleList(locale))
        context.createConfigurationContext(config)
        BaseApp.INSTANCE.createConfigurationContext(config)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        BaseApp.INSTANCE.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

}