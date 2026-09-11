package com.face.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxReward
import com.blankj.utilcode.util.LogUtils
import com.face.BR
import com.face.R
import com.face.ad.AdUtil
import com.face.ad.DefaultMaxRewardedAdListener
import com.face.bean.AdsCountBean
import com.face.bean.PointBean
import com.face.bean.UserRewardBean
import com.face.databinding.ActivitySigninBinding
import com.face.ui.gift.GiftListActivity
import com.face.ui.share.PointsHistActivity
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.view.BaseContentDialog
import com.face.view.PointDialog
import com.face.view.SetReminderDialog
import com.face.viewmodel.activity.SigninViewModel
import com.google.gson.Gson
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.setIfNot
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs


class SigninActivity : BaseBindingActivity<ActivitySigninBinding, SigninViewModel>(
    R.layout.activity_signin,
    SigninViewModel::class.java
) {
    private val titleBarBackgroundDrawable =
        ColorDrawable(Color.parseColor("#0F0F0F")).apply {
            alpha = 0
        }
    val showTitle = MutableLiveData(false)
    private val titleBarBackground = MutableLiveData<Drawable>(titleBarBackgroundDrawable)

    override fun init(savedInstanceState: Bundle?) {
        EventUtil.inPage("SignIn")
        mModel.refreshDays()


        if (SPUtils.hashPoints.isNotEmpty()) {
            mModel.pointBean =
                MoshiHelper.adapter(PointBean::class.java).fromJson(SPUtils.hashPoints)
                    ?: PointBean()
        }
        val nowTime = TimeUtil.getFormatDate(System.currentTimeMillis(), "yyyy-MM-dd-HH").toString()
        mModel.adsCountBean =
            SPUtils.adsUserCount.find { it.id == GVM.INSTANT.userInfo.value.userId }
                ?: AdsCountBean()
        //缓存时间如果有就取广告加载次数显示
        if (nowTime == mModel.adsCountBean.time) {
            mModel.numShowAds.postValue(mModel.adsCountBean.count)
        }

        GVM.INSTANT.userInfo.observe(this) {
            if (it.tflops > mModel.oldPoint && mModel.isDialog) {//积分有变化就弹窗提示
                PointDialog(
                    titleStr = mModel.titleStr,
                    cardNum = mModel.visaCard,
                    pointNum = it.tflops - mModel.oldPoint
                ).showIgnoreState(this)
                mModel.oldPoint = it.tflops
                mModel.isDialog = false
            }
        }

        mModel.ratingBean.observe(this) {
            it?.apply {
                rewards.forEachIndexed { index, bean ->
                    if (dayRating >= bean.num) {//评价数量满足可领取
                        when (bean.id) {//获取对应的id评价任务
                            mModel.rateInfoBean?.daily1 -> {
                                mModel.taskDay1.value = if (bean.isReward == 1) 1 else 2
                            }

                            mModel.rateInfoBean?.daily2 -> {
                                mModel.taskDay2.value = if (bean.isReward == 1) 1 else 2
                            }

                            mModel.rateInfoBean?.daily3 -> {
                                mModel.taskDay3.value = if (bean.isReward == 1) 1 else 2
                            }
                        }
                    }
                    if (weekRating >= bean.num) {//评价数量满足可领取
                        when (bean.id) {//获取对应的id评价任务
                            mModel.rateInfoBean?.weekly1 -> {
                                mModel.taskWeek1.value = if (bean.isReward == 1) 1 else 2
                            }

                            mModel.rateInfoBean?.weekly2 -> {
                                mModel.taskWeek2.value = if (bean.isReward == 1) 1 else 2
                            }

                            mModel.rateInfoBean?.weekly3 -> {
                                mModel.taskWeek3.value = if (bean.isReward == 1) 1 else 2
                            }
                        }
                    }
                }
                LogUtils.e(">>>>>>>Day:${mModel.taskDay1.value} ${mModel.taskDay2.value}  ${mModel.taskDay3.value} ")
                LogUtils.e(">>>>>>>Week:${mModel.taskWeek1.value} ${mModel.taskWeek2.value}  ${mModel.taskWeek3.value} ")

                //对应获取week进度显示第几格
                val numDay1 = mModel.rateInfoBean?.rateDailyNum1 ?: 0
                val numDay2 = mModel.rateInfoBean?.rateDailyNum2 ?: 0
                val numDay3 = mModel.rateInfoBean?.rateDailyNum3 ?: 0
                LogUtils.e(">>>>>$dayRating $numDay2 ")
                mModel.taskDayNum.value = when {
                    dayRating == 0       -> 0
                    dayRating < numDay1  -> 1
                    dayRating == numDay1 -> 2
                    dayRating < numDay2  -> 3
                    dayRating == numDay2 -> 4
                    dayRating < numDay3  -> 5
//                    dayRating <= numDay3 -> 6
                    else                 -> 6
                }
                //对应获取week进度显示第几格
                val numWeek1 = mModel.rateInfoBean?.rateWeeklyNum1 ?: 0
                val numWeek2 = mModel.rateInfoBean?.rateWeeklyNum2 ?: 0
                val numWeek3 = mModel.rateInfoBean?.rateWeeklyNum3 ?: 0
                mModel.taskWeekNum.value = when {
                    weekRating == 0        -> 0
                    weekRating < numWeek1  -> 1
                    weekRating == numWeek1 -> 2
                    weekRating < numWeek2  -> 3
                    weekRating == numWeek2 -> 4
                    weekRating < numWeek3  -> 5
//                    weekRating <= numWeek3 -> 6
                    else                   -> 6
                }

            }
        }

        mModel.isSelected3.observe(this) {
            mBinding?.tvCheck3?.text =
                if (it) getString(R.string.receive) else getString(R.string.undone)
        }
        mModel.isSelected7.observe(this) {
            mBinding?.tvCheck7?.text =
                if (it) getString(R.string.receive) else getString(R.string.undone)
        }
        mModel.isSelected14.observe(this) {
            mBinding?.tvCheck14?.text =
                if (it) getString(R.string.receive) else getString(R.string.undone)
        }
        mModel.isSelected30.observe(this) {
            mBinding?.tvCheck30?.text =
                if (it) getString(R.string.receive) else getString(R.string.undone)
        }
        mModel.numShowAds.observe(this) {
            val fullText = String.format(
                resources.getString(R.string.points_video),
                it.toString(), mModel.pointBean.adsNum.toString()
            )
            // 找到需要添加点击事件的文字的开始和结束位置
            val startIndex = fullText.indexOf("(") + 1
            val endIndex = startIndex + 1
            val spannableString = SpannableString(fullText)
            // 设置文字颜色
            spannableString.setSpan(
                ForegroundColorSpan(getColorX(R.color.colorF4A42A)),
                startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            //设置TextView的文本为可点击
            mBinding?.tvHintVideo?.text = spannableString
        }
        mModel.infoList.observe(this) {
            mModel.checkedDayCurrentMouth.value = mModel.checkedDayCurrentMouth.value
        }
        mModel.checkedDayCurrentMouth.observe(this) {

            if (it >= (mModel.infoList.value.getOrNull(0)?.day ?: 3)) {
                mModel.isSelected3.postValue(true)
                mBinding?.tvCheck3?.text = getString(R.string.receive)
            }
            if (it >= (mModel.infoList.value.getOrNull(1)?.day ?: 7)) {
                mModel.isSelected7.postValue(true)
                mBinding?.tvCheck7?.text = getString(R.string.receive)
            }
            if (it >= (mModel.infoList.value.getOrNull(2)?.day ?: 14)) {
                mModel.isSelected14.postValue(true)
                mBinding?.tvCheck14?.text = getString(R.string.receive)
            }
            if (it >= (mModel.infoList.value.getOrNull(3)?.day ?: 27)) {
                mModel.isSelected30.postValue(true)
                mBinding?.tvCheck30?.text = getString(R.string.receive)
            }
            mBinding?.txtBeen?.text = getResources().getQuantityString(
                R.plurals.points_been, it, it, it
            )
        }
        titleBarBackground.observe(this) {
            mBinding?.clTitle?.background = it
        }
        mBinding?.apply {

            if (SPUtils.nowAdsNum == "0") {//每日广告次数为0可以屏蔽签到获取算力点
                clayVideo.visibility = View.GONE
            }

            mModel.titleStr = getString(R.string.receive_success)
            tvAddPoint.text = "+${mModel.pointBean.pointsAds}"
            tvAddCheckPoint.text = "+${mModel.pointBean.checkIn}"
            tvAddCheckPoint3.text = "+${mModel.pointBean.check3}"
            tvAddCheckPoint7.text = "+${mModel.pointBean.check7}"
            tvAddCheckPoint14.text = "+${mModel.pointBean.check14}"
            tvAddCheckPoint30.text = "+${mModel.pointBean.check30}"
            appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
                (abs(verticalOffset) * 1F / appBarLayout.totalScrollRange).let {
                    showTitle.setIfNot(it > 0.5)
                    titleBarBackground.value = titleBarBackgroundDrawable.apply {
                        alpha = (255 * it).toInt()
                    }
                }
            }
            when (GVM.INSTANT.userInfo.value.vipLv) {
                2    -> {//周
                    txtRank.text = getString(R.string.viplv7)
                    tvAddVipPoint.text = "+${mModel.pointBean.vip7}"
                }

                3    -> {//月
                    txtRank.text = getString(R.string.viplv30)
                    tvAddVipPoint.text = "+${mModel.pointBean.vip30}"
                }

                4, 5 -> {//年
                    txtRank.text = getString(R.string.viplv365)
                    tvAddVipPoint.text = "+${mModel.pointBean.vip365}"
                }

                else -> {
                    txtRank.text = getString(R.string.viplv)
                    tvBuy.text = getString(R.string.become)
                }
            }
        }
    }

    //每日签到
    fun onCheck() {
        mModel.titleStr = getString(R.string.checkin_success)
        mModel.signLog(0, 1) {
            if (SPUtils.enableCheckInNotification == 0) {
                SetReminderDialog().showIgnoreState(this)
            }
        }
    }

    //签到达标领取
    fun onSignTarget(num: Int) {
        mModel.titleStr = getString(R.string.receive_success)
        mModel.signReward(mModel.infoList.value.getOrNull(num)?.day ?: 0)
    }

    //会员每日领取算力
    fun onReceive() {
        mModel.titleStr = getString(R.string.receive_success)
        if (GVM.INSTANT.userInfo.value.vipLv > 1) {
            mModel.signLog(0, 2)
        } else {
            if (GVM.INSTANT.isShowTool.value == 0) {
                GVM.INSTANT.payPage.value = "Home_point0"
            } else {
                GVM.INSTANT.payPage.value = "Home_point"
            }
            VipActivity.jump(this@SigninActivity)
        }
    }

    fun onBuyAct() {
//        AppLovinSdk.getInstance( App.INSTANCE ).showMediationDebugger()
        GVM.INSTANT.payPage.value = "Buy_AIPoints_Page"
        openActivity<BuyPointActivity>()
    }

    fun onCashAct() {
        openActivity<GiftListActivity>()
    }

    fun onHist() {
        openActivity<PointsHistActivity>()
    }

    fun onSetReminderClick() {
        SetReminderDialog().showIgnoreState(this)
    }

    //补签
    fun onUseMcClick() {
        if (mModel.cardNum.value > 0)
            BaseContentDialog(
                getString(R.string.dialog_usmu_title),
                getString(R.string.dialog_usmu_con),
                getString(R.string.confirm),
                getString(R.string.cancel),
                true,
                onLeftData = {
                    mModel.signLog(1, 1)
                })
                .showIgnoreState(this)
        else
            toast(getString(R.string.card_error))
    }

    //看激励广告领取算力
    fun onAdsPoint() {
        mModel.titleStr = getString(R.string.watch_success)
        BaseContentDialog(
            getString(R.string.points_ads_title),
            getString(R.string.points_ads_context),
            getString(R.string.dialog_ads_watch),
            getString(R.string.cancel),
            true,
            onLeftData = {
                if (!AdUtil.showRewardedAdForRewarded(onRewardedAdListener)) {
                    showLoading(false)
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(6 * 1000) // 延迟6秒
                        AdUtil.cancelAds = true
                        dismissLoading()
                        if (!AdUtil.showAds) toast(getString(R.string.ad_load_fail))
                    }
                }
            })
            .showIgnoreState(this)
    }


    private val onRewardedAdListener = object : DefaultMaxRewardedAdListener {

        override fun onAdDisplayed(ad: MaxAd) {
            dismissLoading()
        }

        override fun onAdHidden(ad: MaxAd) {
            dismissLoading()
        }

        override fun onAdReward(ad: MaxAd, reward: MaxReward?) {
            LogUtils.e("onUserRewarded")
            if (mModel.numShowAds.value < mModel.pointBean.adsNum) {
                val userRewardBean = UserRewardBean(
                    adUnitId = ad.adUnitId,
                    networkName = ad.networkName,
                    creativeId = ad.creativeId,
                    dspId = ad.dspId,
                    dspName = ad.dspName,
                    revenue = ad.revenue,
                    amount = reward?.amount ?: 0,
                    revenuePrecision = ad.revenuePrecision,
                    placement = ad.placement,
                    networkPlacement = ad.networkPlacement,
                    label = reward?.label
                )
                mModel.addUserTFLOPS(Gson().toJson(userRewardBean))
            } else {
                toast(getString(R.string.ads_num))
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        AdUtil.onRewardedAdListener = null
    }

    override fun onResume() {
        super.onResume()
        mModel.oldPoint = GVM.INSTANT.userInfo.value.tflops
        mBinding?.apply {
            tvBuy.text = getString(R.string.receive)
            when (GVM.INSTANT.userInfo.value.vipLv) {
                2    -> {//周
                    txtRank.text = getString(R.string.viplv7)
                    tvAddVipPoint.text = "+${mModel.pointBean.vip7}"
                }

                3    -> {//月
                    txtRank.text = getString(R.string.viplv30)
                    tvAddVipPoint.text = "+${mModel.pointBean.vip30}"
                }

                4, 5 -> {//年
                    txtRank.text = getString(R.string.viplv365)
                    tvAddVipPoint.text = "+${mModel.pointBean.vip365}"
                }

                else -> {
                    txtRank.text = getString(R.string.viplv)
                    tvBuy.text = getString(R.string.become)
                }
            }

        }
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}