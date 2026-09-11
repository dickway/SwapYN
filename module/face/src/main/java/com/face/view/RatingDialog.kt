package com.face.view

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.face.BR
import com.face.R
import com.face.bean.RateInfoBean
import com.face.bean.RatingBean
import com.face.databinding.DialogRatingBinding
import com.face.net.Repository
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.toString

class RatingDialog @JvmOverloads constructor(
    val taskId: String = "",
    val mediaId: String = "",
    val isRate: Boolean = false,
    val rateScore: Float = 0f,
    private val onRefresh: ((Float) -> Unit)? = null
) : BaseBindingDF<DialogRatingBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true,
    cancelable = false
) {
    val isRtl = MutableLiveData(SPbaseUtils.spLanguage in listOf("ar", "fa", "iw"))
    var rateInfoBean: RateInfoBean? = MoshiHelper.adapter(RateInfoBean::class.java).fromJson(
        SPUtils.rateInfo
    )

    val isRating = MutableLiveData<Boolean>()

    val mLoading = NotNullMutableLiveData(false)
    val loadFailed = NotNullMutableLiveData(false)

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

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        isRating.value = isRate

        mBinding?.tvScore?.text = rateScore.toString()
        mBinding?.starRating?.setRating(rateScore)
        getRatingRewardInfo()

        ratingBean.observe(this) {
            it?.apply {
                rewards.forEachIndexed { index, bean ->
                    if (dayRating >= bean.num) {//评价数量满足可领取
                        when (bean.id) {//获取对应的id评价任务
                            rateInfoBean?.daily1 -> {
                                taskDay1.value = if (bean.isReward == 1) 1 else 2
                            }

                            rateInfoBean?.daily2 -> {
                                taskDay2.value = if (bean.isReward == 1) 1 else 2
                            }

                            rateInfoBean?.daily3 -> {
                                taskDay3.value = if (bean.isReward == 1) 1 else 2
                            }
                        }
                    }
                    if (weekRating >= bean.num) {//评价数量满足可领取
                        when (bean.id) {//获取对应的id评价任务
                            rateInfoBean?.weekly1 -> {
                                taskWeek1.value = if (bean.isReward == 1) 1 else 2
                            }

                            rateInfoBean?.weekly2 -> {
                                taskWeek2.value = if (bean.isReward == 1) 1 else 2
                            }

                            rateInfoBean?.weekly3 -> {
                                taskWeek3.value = if (bean.isReward == 1) 1 else 2
                            }
                        }
                    }
                }

                //对应获取week进度显示第几格
                val numDay1 = rateInfoBean?.rateDailyNum1 ?: 0
                val numDay2 = rateInfoBean?.rateDailyNum2 ?: 0
                val numDay3 = rateInfoBean?.rateDailyNum3 ?: 0
                taskDayNum.value = when {
                    dayRating == 0 -> 0
                    dayRating < numDay1 -> 1
                    dayRating == numDay1 -> 2
                    dayRating < numDay2 -> 3
                    dayRating == numDay2 -> 4
                    dayRating < numDay3 -> 5
//                    dayRating <= numDay3 -> 6
                    else -> 6
                }
                //对应获取week进度显示第几格
                val numWeek1 = rateInfoBean?.rateWeeklyNum1 ?: 0
                val numWeek2 = rateInfoBean?.rateWeeklyNum2 ?: 0
                val numWeek3 = rateInfoBean?.rateWeeklyNum3 ?: 0
                taskWeekNum.value = when {
                    weekRating == 0 -> 0
                    weekRating < numWeek1 -> 1
                    weekRating == numWeek1 -> 2
                    weekRating < numWeek2 -> 3
                    weekRating == numWeek2 -> 4
                    weekRating < numWeek3 -> 5
//                    weekRating <= numWeek3 -> 6
                    else ->  6
                }
            }
        }
    }

    /**
     * 获取评价任务
     * */
    fun getRatingRewardInfo() {
        loadFailed.value = false
        launchRequestOnIO({ Repository.getRatingRewardInfo() }) {
            onSuccess = {
                ratingBean.value = it
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
                loadFailed.postValue(true)
            }
            onComplete = {
                mLoading.value = false
            }
        }
    }

    /**
     * 领取评价任务
     * */
    fun onRating(oneWhich: Int) {
        val configId = ratingBean.value?.rewards?.getOrNull(oneWhich)?.id ?: 0
        launchRequestOnIO({ Repository.ratingReward(configId) }) {
            onSuccess = { data ->
                val msg=getString(R.string.receive_msg,data.toString())
                toast(msg)
                getRatingRewardInfo()
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    fun postRate() {
        isRating.value = true
        mLoading.value = true
        val score = mBinding?.starView?.rating ?: 0.5f
        launchRequestOnIO({ Repository.ratingShare(taskId, mediaId, score) }) {
            onSuccess = { it ->
                isRating.value = true
                val ratingF = BigDecimal(it.toString())
                    .setScale(1, RoundingMode.DOWN)
                    .toFloat()
                mBinding?.starRating?.setRating(ratingF)
                mBinding?.tvScore?.text = ratingF.toString()
                onRefresh?.invoke(ratingF)
                getRatingRewardInfo()
            }
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.vm, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}