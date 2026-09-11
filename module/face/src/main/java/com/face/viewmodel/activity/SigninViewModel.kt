package com.face.viewmodel.activity

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.face.net.Repository
import com.face.adapter.other.PointDateAdapter
import com.face.bean.AdsCountBean
import com.face.bean.CheckInBean
import com.face.bean.PointBean
import com.face.bean.PointInfoBean
import com.face.bean.RateInfoBean
import com.face.bean.RatingBean
import com.face.bean.ToolTypeBean
import com.face.bean.UrlVoiceBean
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import java.util.Calendar

class SigninViewModel : BaseViewModel() {

    val isRtl = MutableLiveData(SPbaseUtils.spLanguage in listOf("ar", "fa", "iw"))
    var rateInfoBean: RateInfoBean? = MoshiHelper.adapter(RateInfoBean::class.java).fromJson(
        SPUtils.rateInfo
    )
    val loadFinish = MutableLiveData(false)
    val loadFailed = NotNullMutableLiveData(false)
    val isShow = MutableLiveData(false)

    //是否已经签到
    val isCheckShow = MutableLiveData(GVM.INSTANT.isTodayChecked.value)

    //是否已经领取3天  isCheck控制领取还是已领取，isSelected控制 可以领取还是不能领取
    val isCheck3 = MutableLiveData(false)
    val isSelected3 = MutableLiveData(false)

    //是否已经领取7天
    val isCheck7 = MutableLiveData(false)
    val isSelected7 = MutableLiveData(false)

    //是否已经领取14天
    val isCheck14 = MutableLiveData(false)
    val isSelected14 = MutableLiveData(false)

    //是否已经领取30天
    val isCheck30 = MutableLiveData(false)
    val isSelected30 = MutableLiveData(false)

    //是否已经领取会员权益
    val isVipTalk = MutableLiveData(false)

    //获取配置
    var pointBean = PointBean()

    //看广告次数，账号，时间
    var adsCountBean = AdsCountBean()

    //界面显示已看次数
    val numShowAds = NotNullMutableLiveData(0)

    //历史积分
    var oldPoint = GVM.INSTANT.userInfo.value.tflops
    var isDialog = false

    //标题
    var titleStr = ""

    //补签达标获得的补签卡
    var visaCard = 0


    private val calendar = Calendar.getInstance()
    private val _month = TimeUtil.getFormatDate(System.currentTimeMillis(), "yyyyMM")?.toInt() ?: 0

    // 补签卡数量
    val cardNum = NotNullMutableLiveData(0)

    //任务数量达到状态
    val taskDayNum = NotNullMutableLiveData(0)
    val taskWeekNum = NotNullMutableLiveData(0)

    //是否领取 0不可领取，1已领取，2可以领取
    val taskDay1 = NotNullMutableLiveData(0)
    val taskDay2 = NotNullMutableLiveData(0)
    val taskDay3 = NotNullMutableLiveData(0)
    val taskWeek1 = NotNullMutableLiveData(0)
    val taskWeek2 = NotNullMutableLiveData(0)
    val taskWeek3 = NotNullMutableLiveData(0)

    val ratingBean = MutableLiveData<RatingBean>()

    // 本月签到天数
    val checkedDayCurrentMouth = NotNullMutableLiveData(0)

    val showDate = TimeUtil.getFormatDate(System.currentTimeMillis(), "MMM yyyy")

    // 日历
    val days = MutableLiveData(SPUtils.checkInLogs)

    //临时缓存奖品详情
    var infoList = NotNullMutableLiveData<List<PointInfoBean>>(mutableListOf())

    // 本月最大天数
    val maxDayCurrentMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    var pointDateAdapter = PointDateAdapter(
        calendar.get(Calendar.DAY_OF_MONTH),
        maxDayCurrentMonth
    )

    /**
     * 获取评价任务
     * */
    fun getRatingRewardInfo() {
        launchRequestOnIO({ Repository.getRatingRewardInfo() }) {
            onSuccess = {
                ratingBean.value = it
            }
            onFailed = { _, _, errorMsg ->
                dismissLoading()
                loadFailed.postValue(true)
            }
        }
    }

    /**
     * 领取评价任务
     * */
    fun onRating(oneWhich: Int) {
        isDialog = true
        val configId = ratingBean.value?.rewards?.getOrNull(oneWhich)?.id ?: 0
        launchRequestWithLoadingOnIO({ Repository.ratingReward(configId) }) {
            onSuccess = {
                getRatingRewardInfo()
                refreshUserInfo()
            }
            onFailed = { _, _, errorMsg ->
                dismissLoading()
                toast(errorMsg)
            }
        }
    }

    /**
     * 看广告增加算力
     * */
    fun addUserTFLOPS(content: String) {
        isDialog = true
        visaCard = 0
        showLoading()
        launchRequestOnIO({ Repository.addUserTFLOPS(content) }) {
            onSuccess = {
                refreshUserInfo(1)
            }
            onFailed = { _, _, errorMsg ->
                dismissLoading()
                toast(errorMsg)
            }
        }
    }

    /**
     * 领取奖品
     * */
    fun signReward(num: Int) {
        isDialog = true
        showLoading()
        val infoBean = infoList.value.firstOrNull { it.day == num }
        visaCard = infoBean?.info?.find { it.type == 1 }?.num ?: 0
        infoBean?.id?.apply {
            launchRequestOnIO({ Repository.signReward(this) }) {
                onSuccess = {
                    refreshUserInfo()
                    getUserSignRewards()
                    getSignRewardConfig()
                }
                onFailed = { _, _, errorMsg ->
                    dismissLoading()
                    toast(errorMsg)
                }
            }
        }
    }


    /**
     * 获取奖品列表
     * */
    fun getSignRewardConfig(isOpen: Boolean = false) {
        launchRequestOnIO({ Repository.getSignRewardConfig() }) {
            onSuccess = {
                infoList.value = it?.toMutableList() ?: mutableListOf()
                infoList.value.forEach { bean ->
                    when (bean.day) {
                        3 -> {
                            isCheck3.postValue(bean.isGet == 1)
                        }

                        7 -> {
                            isCheck7.postValue(bean.isGet == 1)
                        }

                        14 -> {
                            isCheck14.postValue(bean.isGet == 1)
                        }

                        else -> {
                            isCheck30.postValue(bean.isGet == 1)
                        }
                    }
                }
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                toast(errorMsg)
            }
        }
    }


    fun refreshUserInfo(type: Int = 0, isOpen: Boolean = false) {
        launchRequestOnIO({ Repository.getUserInfo() }) {
            onSuccess = { bean ->
                if (isOpen)
                    loadFinish.postValue(true)
                bean?.apply {
                    if (type == 1) {
                        val hashTime =
                            TimeUtil.getFormatDate(System.currentTimeMillis(), "yyyy-MM-dd-HH")
                                .toString()
                        adsCountBean = AdsCountBean(
                            GVM.INSTANT.userInfo.value.userId,
                            numShowAds.value + 1,
                            hashTime
                        )

                        //看广告做账号缓存
                        if (SPUtils.adsUserCount.any { it.id == adsCountBean.id }) {
                            SPUtils.adsUserCount = SPUtils.adsUserCount.toMutableList().apply {
                                removeIf { it.id == adsCountBean.id }
                                add(adsCountBean)
                            }
                        } else {
                            SPUtils.adsUserCount = SPUtils.adsUserCount.toMutableList().apply {
                                add(adsCountBean)
                            }
                        }
                        numShowAds.postValue(adsCountBean.count)
                    }
                    GVM.INSTANT.userInfo.postValue(this)
                }
            }
            onComplete = {
                dismissLoading()
            }
        }
    }


    /**
     * 获取多少补签卡
     */
    fun getUserSignRewards(isOpen: Boolean = false) {
        launchRequestOnIO({ Repository.getUserSignRewards() }) {
            onSuccess = { bean ->
                cardNum.postValue(bean?.cardNum ?: 0)
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
            }
        }
    }

    /**
     * type 0正常，1补签
     * from_type  1 普通，2 VIP
     */
    fun signLog(
        type: Int,
        fromType: Int,
        successCallback: (() -> Unit)? = null
    ) {
        visaCard = 0
        isDialog = true
        val date = if (type == 0) {
            TimeUtil.getFormatDate(System.currentTimeMillis(), "yyyyMMdd")
        } else {
            days.value?.find { it.type == null && it.fromType == 1 }?.day ?: return
        }
        showLoading()
        launchRequestOnIO({ Repository.signLog(type, fromType, date?.toInt() ?: 0) }) {
            onSuccess = { bean ->
                successCallback?.invoke()
                getUserSignInLog()
                if (type != 0) getUserSignRewards()
            }
            onFailed = { _, _, errorMsg ->
                dismissLoading()
                toast(errorMsg)
            }
        }
    }

    fun refreshDays() {
        loadFailed.postValue(false)
        val old = days.value?.firstOrNull()
        if (old == null
            || old.getYear() != calendar.get(Calendar.YEAR)
            || old.getMonth() != calendar.get(Calendar.MONTH) + 1
        ) {
            val newDays = mutableListOf<CheckInBean>().apply {
                repeat(35) {
                    add(
                        CheckInBean(
                            fromType = 1,
                            dayOfMonth = it + 1,
                            day = _month.toString() + if (it < 9) "0${it + 1}" else it + 1
                        )
                    )
                }
            }
            SPUtils.checkInLogs = newDays
            days.value = newDays
        }
        getRatingRewardInfo()
        getSignRewardConfig(true)
        getUserSignRewards(true)
        getUserSignInLog(true)

    }

    /**
     * 获取签到数据
     */
    fun getUserSignInLog(isOpen: Boolean = false) {
        showLoading()
        launchRequestOnIO({ Repository.getUserSignInLog(_month) }) {
            onSuccess = { it ->
                days.value = CheckInBean.merge(days.value, it)
                SPUtils.checkInLogs = days.value ?: mutableListOf()
                checkedDayCurrentMouth.value = it?.filter { it.fromType == 1 }?.size ?: 0

                val date = TimeUtil.getFormatDate(System.currentTimeMillis(), "yyyyMMdd")
                if (it?.any { it.fromType == 2 && it.day == date } == true) {
                    isVipTalk.postValue(true)
                }
                if (it?.any { it.fromType == 1 && it.day == date } == true) {
                    isCheckShow.postValue(true)
                    SPUtils.checkInTime = date.toString()
                    GVM.INSTANT.isTodayChecked.setIfNot(SPUtils.checkInTime == date)
                }
                refreshUserInfo(isOpen = isOpen)
            }
            onFailed = { _, _, errorMsg ->
                loadFailed.postValue(true)
                dismissLoading()
                toast(errorMsg)
            }
        }
    }
}