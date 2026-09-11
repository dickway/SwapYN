package com.face.ui.tvoice

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.blankj.utilcode.util.LogUtils
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFprobeKit
import com.arthenica.ffmpegkit.ReturnCode
import com.face.BR
import com.face.R
import com.face.databinding.ActivityVoiceCutBinding
import com.face.key.Constants.voicePath
import com.face.util.AudioWaveformGenerator
import com.face.util.GVM
import com.face.view.voicewaveview.Wave.Companion.PAINT_W
import com.face.viewmodel.activity.VoiceCutViewModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class VoiceCutActivity : BaseBindingActivity<ActivityVoiceCutBinding, VoiceCutViewModel>(
    R.layout.activity_voice_cut,
    VoiceCutViewModel::class.java
) {


    var mMediaPlayer = MediaPlayer()
    val audioUrl by intentExtras("audio_url", "")
    val isVoideOrVicoe by intentExtras("audio_voide", true)
    val INPUT_FILE = "output.mp3"

    var voiceUrl = ""

    var timerTime = Timer()
    var timerTimeTask = object : TimerTask() {
        override fun run() {
            val endTime = mModel.startTime + (mBinding?.wave?.voiceTime ?: 0) * 1000
            val newTime = mMediaPlayer.currentPosition
            if (endTime < newTime) {
                mMediaPlayer.seekTo(mModel.startTime)
                mMediaPlayer.pause()
            }
            mModel.isPlay.postValue(mMediaPlayer.isPlaying)
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        voiceUrl = audioUrl
        showLoading(false)
        if (isVoideOrVicoe && !voiceUrl.endsWith(".mp4", true)) {
            transcoding()
        } else {
            extractAudio()
        }
        mModel.toolTaskBean.observe(this) {
            if (it.taskId != "") {
                openActivity<VoiceGenerateActivity> {
                    putBoolean("ispost", true)
                    putParcelable("dataBean", it)
                }
                finish()
            }
        }

    }

    fun transcoding(){
        val targetPath = voicePath + "saveVoice.mp3"
        val command =  "-y -i $voiceUrl $targetPath"
        FFmpegKit.executeAsync(command) { session ->
            if (ReturnCode.isSuccess(session.getReturnCode())) {
                // SUCCESS
                voiceUrl = targetPath
                initMedia()
            } else if (ReturnCode.isCancel(session.getReturnCode())) {
                // CANCEL
            } else {
                // FAILURE
                // session.getState(),session.getReturnCode(),session.getFailStackTrace()
                dismissLoading()
                toast("Command failed : ${session.failStackTrace}")
            }
        }
    }

    fun initMedia() {
        try {
            mMediaPlayer.setDataSource(voiceUrl)
            mMediaPlayer.prepareAsync()
            // 设置监听器
            mMediaPlayer.setOnPreparedListener {
                mModel.isPrepareAsync = true
            }
        } catch (e: Exception) {
            toast("error:${e.message}")
        }
        mBinding?.apply {
            seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    tvStartTime.text = TimeUtil.second2Describe(progress)
                    wave.setCurrentProgress(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    if (mMediaPlayer.isPlaying) mMediaPlayer.pause()
                }


                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    var time = seekBar.max - wave.voiceTime
                    if (seekBar.progress < time) {
                        mModel.startTime = seekBar.progress * 1000
                        mModel.playTime = seekBar.progress * 1000
                    } else {
                        mModel.startTime = time * 1000
                        mModel.playTime = time * 1000
                    }
                }
            })
            launch(Dispatchers.IO) {
                try {
                    //创建文件
                    val file = File(cacheDir, INPUT_FILE)
                    val sourcePath = File(voiceUrl)
                    FileInputStream(sourcePath).use { fis ->
                        FileOutputStream(file).use { os ->
                            val buffer = ByteArray(4096)
                            var len: Int
                            while (fis.read(buffer).also { len = it } != -1) {
                                os.write(buffer, 0, len)
                            }
                        }
                    }
                    val exNum = (getScreenWidth() - 72.dp) / (PAINT_W * 3.65)

                    val decoder = AudioWaveformGenerator(
                        file.absolutePath,
                        exNum.toInt()
                    )
                    decoder.startDecode()

                    val samples = decoder.getSampleData()

                    withContext(Dispatchers.Main) {
                        wave.voiceTime =
                            if (decoder.durationS.toInt() < 10) (decoder.durationS.toInt() - 1) + 1 else 10
                        wave.totalTime = decoder.durationS.toInt()
                        seekbar.max = decoder.durationS.toInt()
                        tvTotalTime.text = TimeUtil.second2Describe(decoder.durationS.toInt())
                        wave.setValues(samples)
                        timerTime.schedule(timerTimeTask, Date(), 800)
                        dismissLoading()
                    }
                } catch (e: Exception) {
                    LogUtils.e(e.toString())
                    dismissLoading()
                }
            }
        }
    }

    fun extractAudio() {
        val dir = File(voicePath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val targetPath = voicePath + "saveVoice.mp3"
        val command =  "-y -i $voiceUrl -c:a copy -vn -c:a mp3 $targetPath"
        FFmpegKit.executeAsync(command) { session ->
            if (ReturnCode.isSuccess(session.getReturnCode())) {
                // SUCCESS
                voiceUrl = targetPath
                initMedia()
            } else if (ReturnCode.isCancel(session.getReturnCode())) {
                // CANCEL
            } else {
                // FAILURE
                // session.getState(),session.getReturnCode(),session.getFailStackTrace()
                dismissLoading()
                toast("Command failed : ${session.failStackTrace}")
            }
        }
    }


    fun soundMaterial() {
        if (!mModel.isPrepareAsync) {
            toast("Initial loading")
            return
        }

        if (mMediaPlayer.isPlaying) {
            mModel.isPlay.postValue(false)
            mModel.playTime = mMediaPlayer.currentPosition
            mMediaPlayer.pause()
        } else {
            mModel.isPlay.postValue(true)
            mMediaPlayer.start()
            mMediaPlayer.seekTo(mModel.playTime)

        }
    }

    fun cutVoice() {
        mMediaPlayer.pause()
        val durationTime = mBinding?.wave?.voiceTime ?: 0
        val totalTime = mBinding?.wave?.totalTime ?: 0
        if (totalTime <= 10) {
            showLoading(getString(R.string.picpost_uploading), false)
            mModel.uploadPicture(voiceUrl)
        } else {
            val targetPath = voicePath + "postVoice.mp3"
            showLoading(getString(R.string.picpost_uploading), false)
//            "-y -i %s -vn -acodec copy -ss %d -t %d %s"
            val command =  "-i $voiceUrl -vn -c:a copy -ss ${mModel.startTime / 1000} -t $durationTime $targetPath"
            FFmpegKit.executeAsync(command) { session ->
                if (ReturnCode.isSuccess(session.getReturnCode())) {
                    // SUCCESS
                    mModel.uploadPicture(targetPath)
                } else if (ReturnCode.isCancel(session.getReturnCode())) {
                    // CANCEL
                } else {
                    // FAILURE
                    // session.getState(),session.getReturnCode(),session.getFailStackTrace()
                    dismissLoading()
                    toast("Command failed : ${session.failStackTrace}")
                }
            }
        }
    }


    override fun onStop() {
        super.onStop()
        mMediaPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        timerTimeTask.cancel()
        timerTime.cancel()
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