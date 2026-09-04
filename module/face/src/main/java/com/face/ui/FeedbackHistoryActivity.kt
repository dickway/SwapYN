package com.face.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.face.net.Repository
import com.face.adapter.feedback.FeedbackAdapter
import com.face.bean.FeedbackBean
import com.face.BR
import com.face.R
import com.face.databinding.ActivityFeedbackHistoryBinding
import com.face.util.EventUtil
import com.face.util.GVM
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity

class FeedbackHistoryActivity :
    BaseBindingActivity<ActivityFeedbackHistoryBinding, BaseViewModel>(
        R.layout.activity_feedback_history,
        BaseViewModel::class.java
    ) {
    val isList by intentExtras("isList", false)
    val data = MutableLiveData<List<FeedbackBean>>()
    val refreshing = MutableLiveData(true)
    val adapter = FeedbackAdapter().apply {
        onItemClick = { _, bean, _ ->
            if (bean != null) {
                FeedbackMsgActivity.jump(
                    this@FeedbackHistoryActivity,
                    bean.id, bean.state
                )
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        EventUtil.inPage("feedback_history")
        if (isList)
            mBinding?.imgAdd?.visibility = View.VISIBLE
    }

    fun onCarteMsg() {
        openActivity<FeedbackActivity>{
            putBoolean("isList", true)
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    fun refresh() {
        launchRequestOnIO({ Repository.getFeedback() }) {
            onSuccess = { beans ->
                data.value = beans?.toMutableList()
                mBinding?.noDataView?.isVisible = beans?.size == 0
                var hasNewMsg = false
                beans?.forEach {
                    if (it.state == 1) {
                        hasNewMsg = true
                        return@forEach
                    }
                }
                GVM.INSTANT.hasNewFeedbackMsg.value = hasNewMsg
            }
            onComplete = { refreshing.value = false }
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
    }
}