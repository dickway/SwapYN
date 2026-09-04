package com.zzkj.structure.util

import com.zzkj.structure.util.ktx.BaseSharedPreferences

object SPbaseUtils : BaseSharedPreferences("AI_FACE") {
    // 清理视频缓存
    var spCache by preference(true, apply = false)

    // 语言切换
    var spCacheLanguage by preference("", apply = true)

    // 语言切换
    var spLanguage by preference("", apply = true)

    // 主题切换
    var spTheme by preference(true, apply = false)

    // 是否开启录屏
    var screenshot by preference("0")

    // 加载的ab:0是a
    var loadAB by preference(0)

    // 是否是测试版本，不禁止录屏
    var versionRestriction by preference(true)
}
