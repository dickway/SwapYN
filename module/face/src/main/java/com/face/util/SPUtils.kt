package com.face.util

import com.face.bean.AdsCountBean
import com.face.bean.AiFaceBean
import com.face.bean.BuySlotBean
import com.face.bean.CheckInBean
import com.face.bean.NotificationBean
import com.face.bean.RecommendBean
import com.face.bean.StyleBean
import com.face.bean.TagConfigBean
import com.face.bean.TopSearchBean
import com.face.bean.UseTypeBean
import com.face.bean.UserBean
import com.face.bean.VipSchemeBean
import com.face.bean.VipUserCountBean
import com.zzkj.structure.util.ktx.BaseSharedPreferences

object SPUtils : BaseSharedPreferences("AI_FACE") {

    // 缓存界面路径
    var aLoginName by preference("com.a.activity.ALoginActivity")
    var aVipName by preference("com.a.activity.AVipActivity")
    var aCompletionName by preference("com.a.activity.ACompletionActivity")
    var aMainName by preference("com.a.activity.AMainActivity")
    var mainAct by preference("com.face.main.WelcomeActivity")

    //首页列表工具列表数据
    var exploreListTools1 by preference("{}")
    var exploreListTools2 by preference("{}")
    //工具玩法列表数据
    var funzoneList by preference("{}")
    //ab首次提示窗
    var noticeRecord by preference("0")
    var noticeLook by preference(false)
    var noticeList by preferenceList(mutableListOf<TagConfigBean>())
    //动态玩法数据
    var listDynamicTools by preference("{}")

    var useAd by preference(1)
    var showPage by preference("")
    //
    var hugPinot by preference("30")
    var kissPinot by preference("30")
    var outfitPinot by preference("10")
    var movesPinot by preference("20")
    var playAiPinot by preference("196")
    //gift剩余数量
    var giftNum by preference("0")

    //别人token
    var tokenUser by preference("")

    //是否显示分享
    var isShare by preference(true)

    //是否开启高清模式
    var isHdFace by preference(false)

    //是否开play玩法
    var isPlayAI by preference(true)

    // 高清生成消耗
    var hdPoints by preference(10)

    //缓存看过的完成素材
    var fishTask by preference("")

    //记录收到的完成素材通知
    var notificationList by preferenceList(mutableListOf<NotificationBean>())

    // 是否打开了签到提醒，0 未设置，1 打开，2 关闭
    var enableCheckInNotification by preference(0)

    // 签到提醒推送时间
    var checkInNotificationTime by preference(0)

    // 签到提醒推送主题
    var checkInNotificationTopic by preference("")

    //  订阅的时区
    var lastTimeZone by preference("")

    //  订阅的版本
    var lastVersion by preference("")

    //  订阅的语言
    var lastLanguage by preference("")

    //  获取时区
//    var gmtTime by preference("GMT08")

    // 跳转返回后是否关闭选择图片视频界面
    var isMaterial by preference(false)

    // 自定义步骤说明
    var toolVideoHint by preference(true)
    var toolPicHint by preference(true)

    var privacyHint by preference(true)

    var faceWarning by preference(false)

    var faceHD by preference(true)

    var saveTime by preference(0L)

    // 第一次打开
    var openScrolleApp by preference(true)

    // 第一次退出
    var exitApp by preference(true)

    // 第一次进长视频hint提示
    var openLongView by preference(true)

    // 第一次进长视频悬浮提示
    var levitateView by preference(true)

    // 第一次打开头像上传
    var openFace by preference(false)
    // 缓存默认选择的会员
//    var vipSelectIndex by preference(0, apply = false)

    // 任务是否有新消息
    var notificationMsg by preference(false, apply = false)

    var gaid by preference("", apply = false)

    var deviceId by preference("", apply = false)

    // 记录开启隐藏分享后不显示的素材
    var materialId by preference("")

    // 缓存登录视频链接
    var startMp4 by preference("")


    // 评分配置
    var videoHeader by preference("yourdomain.com")

    // 评分配置
    var rateInfo by preference("{}")

    // 隐私政策
    var privacyPolicy by preference("")//https://sites.google.com/view/privacy-policy-of-swapai

    // 可生成次数
//    var experiences by preference("0")

    // 签到
    var checkInLogs by preferenceList(mutableListOf<CheckInBean>())

    // 当前签到日期
    var checkInTime by preference("")

    // 分享获得vip天数
    var addDay by preference("0")

    // 可免费生成次数
    var freeNum by preference("1")

    // 购买上传次数
    var buySlots by preference("")

    // a通知公告标题
    var noticeTitle by preference("")
    // a通知公告内容
    var noticeContent by preference("")
    // a通知ok按钮
    var noticeBtnOk by preference("")
    // a通知取消按钮
    var noticeBtnDisagree by preference("")
    // a通知公告下次是否显示
    var noticeShow by preference(false)

    // 朗读最大字符数
    var readNumSize by preference("10000")

    // 视频介绍(防止视频链接失效做了一个预动态参数)
    var introductionVideo by preference("")

    // 首页打开第几个
    var openHomeShow by preference(-1)

    // 今日证件照生成次数
    var poperworkNew by preference(0)

    // 今日age生成次数
    var ageNew by preference(0)

    // 今日live生成次数
    var liveNew by preference(0)

    // 今日txtimg生成次数
    var txtimgNew by preference(0)

    //广告弹窗
    var adsPaperworkShow by preference(true)

    //广告弹窗
    var adsAgeShow by preference(true)

    //广告弹窗
    var adsLiveShow by preference(true)

    // 年零不看广可生成次数
    var ageNum by preference(1)

    // 转动图不看广可生成次数
    var liveNum by preference(1)

    // 证件照不看广可生成次数
    var poperworkNum by preference(1)

    // 生成多少次后展示广告
    var interstitialNum by preference(2)

    // 生成多少次后展示广告
    var interstitialNumA by preference(100)

    // 设备打开多少次后展示广告
    var openNum by preference(2)

    // 设备打开多少次
    var openAppNum by preference(0)

    // telegramGroup
    var telegramGroup by preference("")

    // VIP群
    var txtTelegram by preference("")
    var txtWhatapp by preference("")
    var txtDiscord by preference("")

    var vipDiscount by preference("")

    //缓存时间首页，视频，图片
//    var cacheHomeTime by preference(0L)
//    var cachePhotoTime by preference(0L)

    //首页缓存
//    var bannerCacheData by preferenceList(mutableListOf<AiFaceBean>())
//    var likeCacheData by preferenceList(mutableListOf<AiFaceBean>())
//    var hotsCacheData by preferenceList(mutableListOf<AiFaceBean>())
//    var recommendCacheData by preferenceList(mutableListOf<RecommendBean>())
//    var aifaceCacheData by preferenceList(mutableListOf<AiFaceBean>())

    //图片缓存
//    var photoCacheData by preferenceList(mutableListOf<AiFaceBean>())

    //视频缓存
//    var videoCacheData by preferenceList(mutableListOf<AiFaceBean>())

//    var cacheTimeDuration by preference(0)

    // 算力点的相关配置
    var hashPoints by preference("")

    // 消耗算力点的相关配置
    var usePoints by preference("")

    // 文生图默认关键字
    var txtimgZH by preference("")
    var txtimgZHTW by preference("")
    var txtimgEN by preference("")
    var txtimgES by preference("")
    var txtimgDE by preference("")
    var txtimgFR by preference("")
    var txtimgSV by preference("")
    var txtimgAR by preference("")
    var txtimgKO by preference("")
    var txtimgJA by preference("")
    var txtimgKU by preference("")
    var txtimgTR by preference("")
    var txtimgFA by preference("")
    var txtimgIW by preference("")
    var txtimgPTBR by preference("")
    var txtimgPT by preference("")
    var txtimgIT by preference("")


    //普通用户每日看广告生成图片的次数
    var txtimgDayNum by preference(0)

    //VIP用户每日生成图片的次数
    var txtimgVipdayNum by preference(5)


    // 剪切视频的大小限制
    var voideSize by preference(200)

    // 本地提取人脸配置
    var faceRect by preference("")

    // 剪切视频每秒大小限制
    var videoScale by preference(0.35f)


    // 长视频消耗算力点
    var videoAipoints by preference("")


    // 消耗算力点失败缓存
    var useNum by preference(0)

    var useType by preferenceObject(UseTypeBean())

    // 当前一个小时内获得算力点的次数
//    var pointsNum by preference(0)

    // 算力点获取看广告的时间
//    var hashTime by preference("")

    // 是否开启分享功能
    var openShare by preference(true)

    // 是否启用Banner广告功能(允许显示广告)
    var enableAd by preference(true)

    // VIP是否启用Banner广告功能
    var enableVipBannerAd by preference(true)

    // 不再触发评论
    var notComment by preference(false)

    // 下载了几次
    var downloadNum by preference(0)

    // 下载几次触发评论
    var commentNum by preference(-1)

    // 设备当日能生成几次
    var nowAdsNum by preference("0")

    // a设备当日能生成几次
    var nowAAdsNum by preference("20")

    // 记录设备当日生成几次
    var nowNum by preference(0)

    // 点几次会弹广告（1.1.0废弃）
//    var showAdsNum by preference("-1")

    // 每日VIP免费生成次数
    var vipUsageCount by preference("-1")

    // 永久会员
    var vipLifetime by preference("")

    // 下载按钮
    var versionDownloadShow by preference("")

    // 工具使用不限制
    var toolUse by preference(true)

    // a显示自定义素材入口
    var customShowUse by preference(false)

    // 缓存朗读英语语言
    var readEnglish by preferenceList(mutableListOf<String>())

    // 缓存朗读中文语言
    var readChinese by preferenceList(mutableListOf<String>())

    // 缓存朗读西班牙语言
    var readSpain by preferenceList(mutableListOf<String>())

    // 缓存每日VIP已生成次数
    var vipUserCount by preferenceList(mutableListOf<VipUserCountBean>())

    // 缓存账号观看广告次数
    var adsUserCount by preferenceList(mutableListOf<AdsCountBean>())

    // 自动订阅协议
    var autoSub by preference("")//https://sites.google.com/view/privacy-policy-of-swapai

    // 是否显示工具栏，0不显示，1走渠道显示逻辑
    var showTool by preference("0")

    // 缓存免责申明
    var txtDisclaimer by preference("")

    // 缓存会员列表
    var vipSchemes by preferenceList(listOf<VipSchemeBean>())

    // 缓存discount
    var discountSchemes by preferenceObject(VipSchemeBean())

    // 缓存商品列表
    var buySchemes by preferenceList(listOf<VipSchemeBean>())

    //搜索热门标签
    var searchTop by preferenceObject(TopSearchBean())

    // 后台推送的地址
    var userInfo by preferenceObject(UserBean())

    // 缓存全部模型标签
    var typeTags by preferenceList(listOf(""))

    // 缓存a类型
    var commonAuditTags by preferenceList(mutableListOf<String>())

    // 视频模型标签
    var typeVideoTags by preferenceList(listOf(""))

    // 图片模型标签
    var typeImgTags by preferenceList(listOf(""))

    // 缓存漫画类型
    var styleList by preferenceList(mutableListOf<StyleBean>())

    // 是否启用广告功能(允许显示广告)
//    var enableAd by preference(true)

    // 是否启用插屏广告
//    var enableAdvi by preference(true)

    // 第一次打开需要重置UnifiedId后再push
//    var needResetInmobiUnifiedId by preference(true)


    // 缓存上传图片标签
    var tabTitles by preferenceList(
        listOf(
            "Me",
            "Friend",
            "Lover",
            "Workmate",
            "Classmate",
            "Relative"
        )
    )

    /**
     * 安装归因信息
     */
    // 是否已经获取到信息 0:未获取到 1:成功获取到了 2:获取失败
    var installAwarded by preference(0)
    var installReferrer by preference("")
    var installVersion by preference("")
    var installTime by preference(0L)
    var installClickTime by preference(0L)

    //是否上报过归因信息
    var installPost by preference(false)

    // 在线时长 eventId,liveTime
    var liveTime by preference("")


    /**
     *  share模块
     */
    // 是否首次点击share
    var isFirstClickShare by preference(false)

    // 是否首次进入share模块
    var isFirstShare by preference(false)

    // 是否首次进入share
    var isEnterFirstShare by preference(false)

    //自定义视频的算力点消耗
    //pic
    var videoPic by preference("")

    //20s
    var videoPoints20s by preference("")

    //3分钟
    var videoPoints3m by preference("")

    //5分钟
    var videoPoints5m by preference("")

    //获取奖励的算力点
    var shareGetPoints20s by preference("")
    var shareGetPoints3m by preference("")
    var shareGetPoints5m by preference("")
}
