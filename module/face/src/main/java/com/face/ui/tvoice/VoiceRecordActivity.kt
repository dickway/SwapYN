package com.face.ui.tvoice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.face.BR
import com.face.R
import com.face.databinding.ActivityRecordVoiceBinding
import com.face.key.Constants.voicePath
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.util.recoder.RecorderManager
import com.face.util.recoder.RecordingUtil
import com.face.viewmodel.activity.VoiceRecordViewModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import java.io.File


class VoiceRecordActivity :
    BaseBindingActivity<ActivityRecordVoiceBinding, VoiceRecordViewModel>(
        R.layout.activity_record_voice,
        VoiceRecordViewModel::class.java
    ) {
    private lateinit var launcherPermission: ActivityResultLauncher<String>
    private var filePath = voicePath + "recordVoice.mp3"

    var recorderManager: RecorderManager = RecordingUtil(filePath)
    private var mediaPlayer: MediaPlayer? = null
    override fun init(savedInstanceState: Bundle?) {

        val dir = File(voicePath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        //单个权限申请
        launcherPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {//同意
                    recorderAudio()
                } else {//拒绝
                    val intent = Intent()
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.setData(Uri.parse("package:" + this.packageName))
                    startActivity(intent)
                    toast(getString(R.string.permissions_voice))
                    finish()
                }
            }
        val readTxt = when (SPbaseUtils.spLanguage) {
            "zh" -> {
                val index = (0 until SPUtils.readChinese.size).random()
                SPUtils.readChinese[index].trim()
            }
            "es" -> {
                val index = (0 until SPUtils.readSpain.size).random()
                SPUtils.readSpain[index].trim()
            }
            else -> {
                val index = (0 until SPUtils.readEnglish.size).random()
                SPUtils.readEnglish[index].trim()
            }
        }
        mBinding?.tvHint?.text = readTxt


    }

    /**
     * 检查是否拥有权限
     */
    private fun checkPermission() {
        val audioPermission: Boolean = (PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(
            this@VoiceRecordActivity,
            Manifest.permission.RECORD_AUDIO
        ))
        if (audioPermission) {
            recorderAudio()
        } else {
            launcherPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    var playTimer = object : CountDownTimer(1000 * 11, 1000) {
        //1000ms运行一次onTick里面的方法
        override fun onFinish() {
            mBinding?.tvTime?.text = "10''"
            mBinding?.tvThan2?.visibility=View.VISIBLE
        }

        override fun onTick(millisUntilFinished: Long) {
            mBinding?.tvTime?.text = "${(millisUntilFinished) / 1000}''"
        }
    }

    var countDownTimer = object : CountDownTimer(1000 * 11, 1000) {
        //1000ms运行一次onTick里面的方法
        override fun onFinish() {
            recorderManager.stop()
            mModel.typeView.value = 3
        }

        override fun onTick(millisUntilFinished: Long) {
            mBinding?.tvThan1?.text = String.format(
                resources.getString(R.string.countdown_10s),
                ((millisUntilFinished) / 1000).toString()
            )
        }
    }

    fun bottomView() {
        checkPermission()
    }

    fun bottomView1() {
        recorderManager.stop()
        countDownTimer.cancel()
        mModel.typeView.value = 1
    }

    fun bottomView2() {
        mModel.typeView.value = 1
        playTimer.cancel()
        //销毁音频资源
        if (mediaPlayer != null) {
            mediaPlayer?.pause()
        }
    }

    fun nextVoice() {
        if (mediaPlayer != null) {
            mediaPlayer?.pause()
        }
        openActivity<VoiceCutActivity> {
            putString("audio_url", filePath)
            putBoolean("audio_voide", true)
        }
        finish()
    }


    fun linsterVoice() {
        mBinding?.tvThan2?.visibility=View.INVISIBLE
        mediaPlayer = MediaPlayer.create(this, Uri.parse(filePath))
        mediaPlayer?.start()
        playTimer.start()
    }


    fun recorderAudio() {
        recorderManager.start()
        countDownTimer.start()
        mModel.typeView.value = 2
        mBinding?.voiceWaveView0?.apply {
            if (bodyWaveList.size < 1) {
                duration = 150
                addHeader(2)
                addHeader(2)
                addHeader(4)
                addHeader(14)
                addBody(27)
                addBody(17)
                addBody(38)
                addBody(81)
                addBody(38)
                addBody(24)
                addBody(8)
                addBody(60)
                addBody(38)
                addBody(14)
                addBody(8)
                addFooter(8)
                addFooter(4)
                addFooter(2)
                addFooter(2)
                start()
            }
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding?.voiceWaveView0?.stop()

        //销毁音频资源
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }
}
