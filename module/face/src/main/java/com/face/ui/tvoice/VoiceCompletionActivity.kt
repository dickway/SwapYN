package com.face.ui.tvoice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.face.BR
import com.face.R
import com.face.util.GVM
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.intentExtras
import com.face.bean.ToolTaskBean
import com.face.databinding.ActivityCompletionVoiceBinding
import com.face.key.Constants
import com.face.ui.FeedbackActivity
import com.face.ui.VipActivity
import com.face.util.FileUtil
import com.face.util.GoogleToPlay
import com.face.util.SPUtils
import com.face.view.BaseUpdateDialog
import com.face.view.CommentDialog
import com.face.viewmodel.activity.VoiceCompletionViewModel
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import java.io.File


class VoiceCompletionActivity :
    BaseBindingActivity<ActivityCompletionVoiceBinding, VoiceCompletionViewModel>(
        R.layout.activity_completion_voice,
        VoiceCompletionViewModel::class.java
    ) {
    private lateinit var launcherPermission: ActivityResultLauncher<String>

    private val dataBean by intentExtras("dataBean", ToolTaskBean())
    var mMediaPlayer = MediaPlayer()
    override fun init(savedInstanceState: Bundle?) {

        if (dataBean.state == 3) {
            mModel.loadingView.postValue(3)
            mBinding?.tvHint?.text = dataBean.errorMessage
        } else {
            mModel.getUserGenerate(dataBean.id)
            mModel.loadingView.postValue(2)
        }


        mModel.dataBean.observe(this) {
            if (it.id != "") {
                mModel.downloadUrl.value = it.getInputUrl().toString()
                mBinding?.tvName?.text = it.name
                mBinding?.tvTime?.text = it.getUrlTime()
            }
        }


        mModel.isDownload.observe(this) {//下载完成处理存储路径
            if (it) {
                val downloadFile = File(Constants.savePath, mModel.downName.value)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    FileUtil.copyFileToDownloadDir(downloadFile.path, mModel.pathName.value)
                    reviewApp()
                } else {
                    checkPermission()
                }

            }
        }

        //单个权限申请
        launcherPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {//同意
                    val downloadFile = File(Constants.savePath, mModel.downName.value)
                    FileUtil.fileSaveToPublic(downloadFile.path, mModel.pathName.value)
                } else {//拒绝
                    val intent = Intent()
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.setData(Uri.parse("package:" + this.packageName))
                    startActivity(intent)
                    toast(getString(R.string.permissions_storage))
                    finish()
                }
            }
    }

    /**
     * 检查是否拥有权限
     */
    private fun checkPermission() {
        val writePermission: Boolean = (PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(
            this@VoiceCompletionActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ))
        if (writePermission) {
            val downloadFile = File(Constants.savePath, mModel.downName.value)
            FileUtil.fileSaveToPublic(downloadFile.path, mModel.pathName.value)
            reviewApp()
        } else {
            launcherPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    fun onUpdataData() {
        BaseUpdateDialog(
            getString(R.string.modify_name),
            mModel.dataBean.value?.name?:"",
        ) { updateData(mModel.dataBean.value, it) }.showIgnoreState(this)
    }

    fun updateData(bean: ToolTaskBean?, name: String) {
        mModel.updataUserGenerateRecords(bean?.id, name)
    }

    fun playVoice() {

        if (mModel.isPrepareAsync) {//是否初始完成
            if(mMediaPlayer.isPlaying){
                mModel.isPlay.postValue(false)
                mMediaPlayer.pause()
            }else{
                mModel.isPlay.postValue(true)
                mMediaPlayer.start()
            }
        } else {
            showLoading()
            val voiceUrl = mModel.dataBean.value?.getInputUrl() ?: ""
            if (voiceUrl != "") {
                try {
                    mMediaPlayer.reset()
                    mMediaPlayer.setDataSource(voiceUrl)
                    mMediaPlayer.prepareAsync()

                    mMediaPlayer.setOnCompletionListener {
                        mModel.isPlay.postValue(false)
                    }
                    // 设置监听器
                    mMediaPlayer.setOnPreparedListener {
                        mModel.isPrepareAsync = true
                        mModel.isPlay.postValue(true)
                        mMediaPlayer.start()
                        dismissLoading()
                    }
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            } else {
                dismissLoading()
            }
        }
    }


    fun download() {
        GVM.INSTANT.payPage.value="Download_voice"
        if (!mModel.isDownloadSate.value) {
            if (GVM.INSTANT.userInfo.value.isVip()) {
                SPUtils.downloadNum += 1
                mModel.download()
            } else {
                VipActivity.jump(this@VoiceCompletionActivity)
            }
        }
    }

    fun reviewApp() {
        if (SPUtils.commentNum > 0 &&
            SPUtils.commentNum < SPUtils.downloadNum &&
            !SPUtils.notComment
        ) {
            CommentDialog(
                onOpenCode = { onOpenCode() },
                onFeedback = { onFeedback() })
                .showIgnoreState(this)
        }
    }

    fun onFeedback() {
        openActivity<FeedbackActivity>()
    }

    fun onOpenCode() {
        GoogleToPlay.launchGooglePlay(this)
    }

    override fun onStop() {
        super.onStop()
        mModel.isPlay.postValue(false)
        mMediaPlayer.pause()
    }

    override fun onDestroy() {
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