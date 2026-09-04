package com.face.util

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.apkfuns.logutils.LogUtils
import com.face.net.Repository
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.key.DiffKey
import com.singular.sdk.Singular
import com.face.ui.App
import com.face.bean.Face
import com.face.bean.MyFaceImgBean
import com.face.bean.TargetBean
import com.face.bean.UserBean
import com.face.bean.VipUserCountBean
import com.face.bean.VipUserCountBean.Companion.toMap
import com.face.ui.LoginActivity
import com.face.view.LoginDialog
import com.facebook.appevents.AppEventsLogger
import com.zzkj.structure.base.BaseApp
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.LoadingState
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.AppManager
import com.zzkj.structure.util.NetworkLiveData
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.device.DeviceUtils
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.postIfNot
import com.zzkj.structure.util.ktx.setIfNot
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

/**
 * @author 再战科技
 * @date 2022/10/12
 * @description
 */
class GVM : BaseViewModel() {

    //支付成功是跳转的界面
    var payPage = MutableLiveData("")

    //视频素材界面当前framant列表大小
    var showListSize = MutableLiveData(1)

    //视频素材界面是否显示删除按钮
    var showDelete = NotNullMutableLiveData(false)

    //首页是否显示tool  == 0 自然量
    var isShowTool = MutableLiveData<Int>()

    //首页是否显示tool  == 0 自然量
    var aIsAB = MutableLiveData(0)

    //是否刷新我的界面数据
    var aIsMeRefresh = MutableLiveData(false)

    //切换回首页
    var aSelectIndex = MutableLiveData(-1)

    //是否分享上传自定义素材
    var isShareActivity = NotNullMutableLiveData(false)

    //这次是否已经上报过一次
    var isPostAB = MutableLiveData(-1)

    //isShowTool连续两次结果不一致就重启app
    var isEventA = MutableLiveData(-1)

    //是否开启分享
    var isOpenShare = MutableLiveData(SPUtils.openShare)

    var viewShowBtn = MutableLiveData(true)

    var swapNeedMute = NotNullMutableLiveData(false)

    //最近一次进入的素材，反馈用
    var ofLateSwap = MutableLiveData("")//记录当前界面选中下标


    var swapOpenTime = NotNullMutableLiveData(0L)//记录素材界面打开时的时间
    var videoOpenTime = NotNullMutableLiveData(0L)//记录素材视频链接获取的时间

    //素材界面弹窗选择的列表
    var isSwapR = NotNullMutableLiveData(true)//是否需要刷新头像，处理多次加载头像闪动问题
    var swapSelectList = MutableLiveData<List<MyFaceImgBean>>(mutableListOf())
    var swapTabList = MutableLiveData<List<Face>>(mutableListOf())
    var selectBean = MutableLiveData(MyFaceImgBean())//记录当前界面选中的头像
    var selectNum = NotNullMutableLiveData(0)//记录当前界面素材头像选中下标

    var isForeground = MutableLiveData<Boolean>()

    //视频素材头像
    var videoFaceList = MutableLiveData(mutableListOf(TargetBean(isAdd = true)))

    // grid 列表列数，根据屏幕宽度来
    val faceListSpan by lazy { max(min(getScreenWidth() / 170.dp, 3), 2) }

    // 网络状态
    var networkLiveData = NetworkLiveData(BaseApp.INSTANCE)

    // 滑动后滚动到最左边
    var isRefresh = NotNullMutableLiveData(false)

    // 用户信息
    var userInfo = NotNullMutableLiveData(SPUtils.userInfo)

    // 是否是会员
    val isVip = NotNullMutableLiveData(false)

    // 用户数据加载状态
    private var userInfoLoadingState: LoadingState = LoadingState.None

    // 添加头像后是否要刷新
    var needRefresh = NotNullMutableLiveData(false)

    // 广告是否看完，有效1次
    var hasAD = NotNullMutableLiveData(false)

    // 广告是否显示
    var adShow = NotNullMutableLiveData(false)

    // 今天是否签到
    var isTodayChecked = NotNullMutableLiveData(false)

    var isVisibleShare = MutableLiveData(SPUtils.isShare)

    var showMineRedDot = MutableLiveData(false)

    // 是否启用banner广告功能(允许vip显示广告)
    val enableAdAndNotVip = NotNullMutableLiveData(true)

    // 反馈是否有新消息
    var hasNewFeedbackMsg = NotNullMutableLiveData(false)


    companion object {
        @JvmStatic
        val INSTANT = BaseApp.INSTANCE.getApplicationScopeViewModel(GVM::class.java)
    }

    init {
        userInfo.observeForever { updateVip() }
        hasNewFeedbackMsg.observeForever { updateMineRedDot() }
        isTodayChecked.observeForever { updateMineRedDot() }
    }

    private fun updateVip() {
        isVip.setIfNot(userInfo.value.isVip())
//        enableAdAndNotVip.setIfNot(!userInfo.value.isVip() && SPUtils.enableAd)
//        val num1 = userInfo.value.mediaNum//用户生成次数
//        val num2 = SPUtils.experiences.toInt()// 可生成次数3
//        enableAdvi.setIfNot(!userInfo.value.isVip() && SPUtils.enableAdvi && num1 >= num2)
    }


    fun updateMineRedDot() {
        showMineRedDot.setIfNot(!isTodayChecked.value || hasNewFeedbackMsg.value)
    }


    /**
     * @param from  1 绑定google
     *              2 刷新用户
     *              4 注销
     *              5 google 登录
     */
    fun updateUserInfo(bean: UserBean, from: Int = 0) {
        userInfo.postValue(bean)
        SPUtils.userInfo = bean
        if (from != 0) {
            bean.userId.toString().let {
                if (from != 4) {
                    val userId = if (it != "0") {
                        it
                    } else {
                        DeviceUtils.getAndroidID()
                    }
                    setThirdUserId(userId)
                } else {
                    setThirdUserId(DeviceUtils.getAndroidID())
                }
                if (from == 5 || from == 1) {
                    FirebaseMessageUtil.uploadPushToken()
                }
                if (from==1){//绑定邮箱需要再次刷新信息
                    launch {
                        delay(500)
                        refreshUserInfo()
                    }
                }
            }
            if (from == 4) {
                EventUtil.logout()
                if (AppManager.topActivity !is LoginActivity && isShowTool.value != 0) {
                    AppManager.topActivity?.openActivity<LoginActivity>()
                }
                if (isShowTool.value == 0) {
                    val intent = Intent().setClassName(
                        App.INSTANCE.packageName,
                        SPUtils.aLoginName
                    )
                    AppManager.topActivity?.startActivity(intent)
                }
                FirebaseMessageUtil.logout()
                SPUtils.checkInLogs = mutableListOf()
                SPUtils.checkInNotificationTopic = ""
                SPUtils.enableCheckInNotification = 0
            }
        }
    }

    fun refreshUserInfo(from: Int = 2, onFinish: ((Boolean) -> Unit)? = null) {
        userInfoLoadingState = LoadingState.Refresh()
        launchRequestOnIO({ Repository.getUserInfo() }) {
            onSuccess = { bean ->
                bean?.apply {
                    SPUtils.vipUserCount.toMap().let {//本地获取和保存vip生成次数
                        val numCount = it[userId] ?: -1
                        if (numCount == -1) {
                            SPUtils.vipUserCount = SPUtils.vipUserCount.toMutableList().apply {
                                add(VipUserCountBean(userId, 0))
                            }
                        }
                    }
                    //开启会员不显示banner广告
                    if (!SPUtils.enableVipBannerAd){
                        enableAdAndNotVip.postIfNot( !isVip())
                    }

                    val t1 = System.currentTimeMillis()
                    val t2 = SPUtils.saveTime
                    if (!TimeUtil.isSameDay(t1, t2)) {//本地生成次数0点重置
                        SPUtils.nowNum = 0
                        SPUtils.poperworkNew = 0
                        SPUtils.txtimgNew = 0
                        SPUtils.ageNew = 0
                        SPUtils.liveNew = 0

                        SPUtils.vipUserCount = SPUtils.vipUserCount.onEach {
                            it.countUse = 0
                        }
                        SPUtils.saveTime = t1
                    }
                    updateUserInfo(this, from)
                }
                userInfoLoadingState = LoadingState.Success()
                onFinish?.invoke(true)
            }
            onFailed = { _, _, _ ->
                userInfoLoadingState = LoadingState.Fail()
                onFinish?.invoke(false)
            }
        }
    }

    fun isLogin() = userInfo.value.isLogin()

    fun checkingLogin(mActivity: FragmentActivity): Boolean {
        if (!isLogin()) {
            LoginDialog().showIgnoreState(mActivity)
        }
        return isLogin()
    }


    fun setThirdUserId(it: String) {
        LogUtils.i("setThirdUserId:$it")
        Singular.setCustomUserId(it)
        Firebase.analytics.setUserId(it)
        Firebase.crashlytics.setUserId(it)
        AppEventsLogger.setUserID(it)
    }


    // only b
    fun onNetworkChange(available: Boolean) {
        if (!available) {
            return
        }
//        getNoticeIfNeed()
    }

    //    private fun getNoticeIfNeed() {
//        if (!noticeLoadingState.needRefresh()) {
//            return
//        }
//        noticeLoadingState = LoadingState.Refresh()
//        launchRequestOnIO({ Repository.getConfigs("notice") }) {
//            onSuccess = { list ->
//                noticeData.value = list?.toMap()
//                noticeLoadingState = LoadingState.Success()
//            }
//            onFailed = { _, _, _ ->
//                noticeLoadingState = LoadingState.Fail()
//            }
//        }
//    }

}