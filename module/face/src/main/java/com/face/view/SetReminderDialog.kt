package com.face.view

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.blankj.utilcode.util.LogUtils
import com.face.net.Repository
import com.face.BR
import com.face.R
import com.face.bean.ConfigBean
import com.face.bean.ConfigBean.Companion.toMap
import com.face.databinding.DialogSetReminderBinding
import com.face.util.FirebaseMessageUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.ui.dialog.BaseBindingDF
import com.zzkj.structure.util.NotNullMutableLiveData
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.toIntOrZero
import com.zzkj.structure.util.toast
import java.util.TimeZone


class SetReminderDialog : BaseBindingDF<DialogSetReminderBinding>(
    width = WindowManager.LayoutParams.MATCH_PARENT,
    gravity = Gravity.BOTTOM,
    isBottomAnimation = true,
    cancelable = false
) {

    val checked = NotNullMutableLiveData(SPUtils.enableCheckInNotification != 2)
    var index = SPUtils.checkInNotificationTime
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        mBinding?.wheel3d?.onWheelChangedListener =
            (com.face.view.wheel.OnWheelChangedListener { wheel, oldIndex, newIndex ->
                index = newIndex
            })
        launchRequestOnIO({ Repository.getUserSetting("check_in") }) {
            onSuccess = { list ->
                list?.toMap()?.let { map ->
                    map["notification_topic"]?.let {
                        SPUtils.checkInNotificationTopic = it
                    }
                    index = if (map["reminder_time"].isNullOrBlank()) {
                        TimeUtil.getFormatDate(System.currentTimeMillis(), "H")
                    } else {
                        map["reminder_time"]
                    }.toIntOrZero()
                    SPUtils.checkInNotificationTime = index
                    mBinding?.wheel3d?.setCurrentIndex(index, true)
                }
            }
        }
    }

    fun onConfirmClick() {
        showLoading()
        SPUtils.enableCheckInNotification = if (checked.value) 1 else 2
        if (!checked.value) {
            val oldTopic = SPUtils.checkInNotificationTopic
            if (oldTopic != "") {
                FirebaseMessageUtil.unsubscribeTopic(oldTopic) {
                    dismissLoading()
                    if (it) {
                        SPUtils.checkInNotificationTopic = ""
                        dismissAllowingStateLoss()
                    } else {
                        toast(getString(R.string.fail))
                    }
                }
            } else {
                dismissLoading()
                dismissAllowingStateLoss()
            }
            return
        }
        val transformHour = TimeUtil.transformHour(
            index,
            TimeZone.getDefault(),
            TimeZone.getTimeZone("UTC")
        ).let { "${if (it < 10) "0$it" else it.toString()}00" }
        val topic = "checkIn_$transformHour"
        launchRequestOnIO({
            Repository.saveUserSetting(
                ConfigBean(
                    type = "check_in",
                    key = "notification_topic",
                    value = topic
                ),
                ConfigBean(
                    type = "check_in",
                    key = "reminder_time",
                    value = index.toString()
                ),
                ConfigBean(
                    type = "check_in",
                    key = "enable_check",
                    value = SPUtils.enableCheckInNotification.toString()
                )
            )
        }) {
            onSuccess = {
                val oldTopic = SPUtils.checkInNotificationTopic
                if (oldTopic != "") {
                    FirebaseMessageUtil.unsubscribeTopic(oldTopic) { fail ->
                        if (fail) {
                            SPUtils.checkInNotificationTopic = ""
                            FirebaseMessageUtil.subscribeTopic(topic) { success ->
                                dismissLoading()
                                if (success) {
                                    SPUtils.checkInNotificationTopic = topic
                                    dismissAllowingStateLoss()
                                } else {
                                    toast(getString(R.string.fail))
                                }
                            }
                        } else {
                            dismissLoading()
                            toast(getString(R.string.fail))
                        }
                    }
                } else {
                    FirebaseMessageUtil.subscribeTopic(topic) {
                        dismissLoading()
                        if (it) {
                            SPUtils.checkInNotificationTopic = topic
                            dismissAllowingStateLoss()
                        } else {
                            toast(getString(R.string.fail))
                        }
                    }
                }
            }
            onFailed = { _, _, msg ->
                dismissLoading()
                toast(msg)
            }
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}