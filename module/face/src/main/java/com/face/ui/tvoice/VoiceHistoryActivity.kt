package com.face.ui.tvoice

import android.media.MediaPlayer
import android.os.Bundle
import com.face.net.Repository
import com.face.BR
import com.face.R
import com.face.bean.ToolTaskBean
import com.face.databinding.ActivityHistoryVoiceBinding
import com.face.util.GVM
import com.face.view.BaseContentDialog
import com.face.view.BaseDeleteDialog
import com.face.view.BaseUpdateDialog
import com.face.view.ClearMoreDialog
import com.face.viewmodel.activity.VoiceHistoryViewModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import java.util.Date
import java.util.Timer
import java.util.TimerTask


class VoiceHistoryActivity :
    BaseBindingActivity<ActivityHistoryVoiceBinding, VoiceHistoryViewModel>(
        R.layout.activity_history_voice,
        VoiceHistoryViewModel::class.java
    ) {

    var mMediaPlayer = MediaPlayer()

    var timer = Timer()
    var timerTask: TimerTask = object : TimerTask() {
        override fun run() {
            mModel.currentTime.postValue(mMediaPlayer.currentPosition)
        }
    }

    override fun init(savedInstanceState: Bundle?) {

        mModel.getUserRecordPage()

        mModel.historyAdapter.onItemClick = { _, bean, position ->
            if (bean?.state == 1 || bean?.state == 0) {
                openActivity<VoiceDubActivity> {
                    putParcelable("dataBean", bean)
                }
            } else {
                openActivity<VoiceCompletionActivity> {
                    putParcelable("dataBean", bean)
                }
            }
        }


        mModel.historyAdapter.onSingPlayClick = { bean, position ->
            mModel.isColse.postValue(false)
            mModel.historyAdapter.selectIndex = position
            mModel.selectedToolTask.value = bean
        }

        mModel.historyAdapter.apply {
            onSingOperateClick = this@VoiceHistoryActivity.onSingOperateClick
        }

        mModel.historyAdapter.onItemLongClick = { _, bean, _ ->
            if (bean?.state == 1 || bean?.state == 0) {
                BaseContentDialog(
                    getString(R.string.are_you2),
                    getString(R.string.pro_hint2),
                    getString(R.string.cancel),
                    getString(R.string.interrupt),
                    onRightData = { onCancelData(bean) })
                    .showIgnoreState(this)
            } else if (bean?.state == 3) {
                onDeleteData(bean)
            } else {
                ClearMoreDialog(
                    onDeleteAct = { onDeleteUserData(bean) },
                    onUpdateAct = { onUpdataData(bean) })
                    .showIgnoreState(this)
            }
            true
        }

        mModel.historyList.observe(this) {
            mModel.isNoData.postValue(it.isNullOrEmpty())
            mModel.historyAdapter.submitList(it)
            colseView()
        }

        timer.schedule(timerTask, Date(), 150)

        mModel.currentTime.observe(this) {
            if (it > 0) {
                mBinding?.progress?.progress = it
                var minute = (it / 1000 / 60 % 60).toString()
                if (minute.length < 2) minute = "0$minute"
                var second = (it / 1000 % 60).toString()
                if (second.length < 2) second = "0$second"
                mBinding?.tvTime?.text = "$minute:$second"
            }
        }
        mModel.selectedToolTask.observe(this) {
            if (it != null) {
                mBinding?.tvName?.text = it.name
                mBinding?.tvTime?.text = "00:00"
                mBinding?.progress?.progress = 0
                showLoading()
                val voiceUrl = it.getInputUrl() ?: ""
                if (voiceUrl != "") {
                    try {
                        mMediaPlayer.reset()
                        mMediaPlayer.setDataSource(voiceUrl)
                        mMediaPlayer.prepareAsync()
                        // 设置监听器
                        mMediaPlayer.setOnPreparedListener {
                            mBinding?.progress?.max = mMediaPlayer.duration
                            mModel.isPlay.value = true
                            mMediaPlayer.start()
                            dismissLoading()
                        }
                        mMediaPlayer.setOnCompletionListener {
                            mModel.isPlay.value = false
                        }
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                } else {
                    dismissLoading()
                }
            } else {
                mMediaPlayer.stop()
            }
        }


    }

    fun colseView() {
        mModel.isColse.postValue(true)
        mModel.historyAdapter.selectIndex = -1
        mModel.selectedToolTask.value = null
    }

    fun playOr() {
        if (mModel.isPlay.value) {
            mMediaPlayer.pause()
        } else {
            mMediaPlayer.start()
            if (mBinding?.progress?.max == mBinding?.progress?.progress) {
                mBinding?.progress?.progress = 0
                mBinding?.tvTime?.text = "00:00"
            }
        }
        mModel.isPlay.value = !mModel.isPlay.value
    }

    fun onCancelData(bean: ToolTaskBean) {
        launchRequestWithLoadingOnIO({ Repository.cancelTask(bean.taskId) }) {
            onSuccess = {
                mModel.getUserRecordPage()
                toast(getString(R.string.success))
            }
            onFailed = { _, _, errorMsg ->
                toast(errorMsg)
            }
        }
    }

    val onSingOperateClick: (ToolTaskBean?) -> Unit = {

        if (it?.state == 0 || it?.state == 1) {
            BaseContentDialog(
                getString(R.string.are_you2),
                getString(R.string.pro_hint2),
                getString(R.string.cancel),
                getString(R.string.interrupt),
                onRightData = { onCancelData(it) })
                .showIgnoreState(this)
        } else if (it?.state == 3) {
            onDeleteData(it)
        } else {
            ClearMoreDialog(
                onDeleteAct = { onDeleteUserData(it) },
                onUpdateAct = { onUpdataData(it) })
                .showIgnoreState(this)
        }
    }

    private fun onUpdataData(bean: ToolTaskBean?) {
        BaseUpdateDialog(
            getString(R.string.modify_name),
            bean?.name?:"",
        ) { mModel.updataUserGenerateRecords(bean?.id, it) }.showIgnoreState(this)
    }

    private fun onDeleteData(bean: ToolTaskBean?) {
        BaseDeleteDialog(
            getString(R.string.remove_record),
            getString(R.string.remove_record_content)
        ) { mModel.deleteTask(bean) }.showIgnoreState(this)
    }

    private fun onDeleteUserData(bean: ToolTaskBean?) {
        BaseDeleteDialog(
            getString(R.string.remove_record),
            getString(R.string.remove_record_content)
        ) { mModel.deleteRecord(bean) }.showIgnoreState(this)
    }

    override fun onStop() {
        super.onStop()
        mMediaPlayer.pause()
        mModel.isPlay.value = false
    }

    override fun onResume() {
        super.onResume()
        mModel.getUserRecordPage()
    }

    override fun onDestroy() {
        timerTask.cancel()
        timer.cancel()
        super.onDestroy()
        mMediaPlayer.stop()
        mMediaPlayer.reset()
        mMediaPlayer.release()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}
