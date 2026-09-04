package com.face.ui.tvoice

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.face.net.Repository
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.engine.UriToFileTransformEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.utils.SandboxTransformUtils
import com.face.BR
import com.face.R
import com.face.adapter.other.VoiceMaterialAdapter
import com.face.bean.ToolTaskBean
import com.face.databinding.ActivitySoundMaterialBinding
import com.face.util.GVM
import com.face.util.GlideEngine
import com.face.util.SPUtils
import com.face.view.BaseContentDialog
import com.face.view.BaseDeleteDialog
import com.face.view.BaseUpdateDialog
import com.face.view.ClearMoreDialog
import com.face.view.VoiceMaterialDialog
import com.face.viewmodel.activity.VoiceMaterialModel
import com.face.ui.BaseBindingActivity
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast


class VoiceMaterialActivity : BaseBindingActivity<ActivitySoundMaterialBinding, VoiceMaterialModel>(
    R.layout.activity_sound_material,
    VoiceMaterialModel::class.java
) {
    val soundMaterialAdapter: VoiceMaterialAdapter = VoiceMaterialAdapter()
    var mMediaPlayer = MediaPlayer()

    override fun init(savedInstanceState: Bundle?) {

        mModel.getUserRecordPage()

        soundMaterialAdapter.onSingOperateClick = this@VoiceMaterialActivity.onSingOClick

        mModel.voiceList.observe(this) {//刷新后重置状态
            soundMaterialAdapter.selectIndex = it.indexOf(mModel.toolTaskBean)
        }

        soundMaterialAdapter.onItemLongClick = { _, bean, _ ->
            if (bean?.state == 1 || bean?.state == 0) {//生产中状态
                BaseContentDialog(
                    getString(R.string.are_you),
                    getString(R.string.pro_hint),
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

        soundMaterialAdapter.onSingSelectClick = { bean, position ->
            if (bean?.state == 1 || bean?.state == 0) {
                openActivity<VoiceGenerateActivity> {
                    putParcelable("dataBean", bean)
                }
            } else {
                if (bean?.id != "") {//不为空就表示是完成的音色
                    soundMaterialAdapter.selectIndex = position
                    mModel.showSave.postValue(true)
                    mModel.toolTaskBean = bean ?: ToolTaskBean()
                    onPlayClick()
                } else {//为空表示是失败音色
                    openActivity<VoiceFailActivity> {
                        putParcelable("dataBean", bean)
                    }

                }
            }
        }


        mModel.GeneratingId.observe(this) {
            if (it == "") {//没有生成中任务可以走广告流程
                startVoice()
            } else if (it == "-1") {//初始值不执行

            } else {//有生成中任务
                BaseContentDialog(
                    getString(R.string.dialog_paper_only),
                    getString(R.string.dialog_paper_only_txt),
                    getString(R.string.cancel),
                    getString(R.string.btn_record),
                    false,
                    onRightData = { toTask(it) })
                    .showIgnoreState(this)
            }
        }
    }

    fun toTask(taskId: String) {
        openActivity<VoiceDubActivity> {
            putParcelable("dataBean", ToolTaskBean(taskId = taskId))
        }
    }

    fun onOkData() {
        openActivity<VoiceGenerateActivity> {
            putParcelable("dataBean", mModel.Generating)
        }
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

    fun onAdd() {
        if (mModel.Generating.taskId != "") {
            BaseContentDialog(
                getString(R.string.please_wait_patiently),
                getString(R.string.only_one),
                getString(R.string.view_detail),
                getString(R.string.cancel),
                onLeftData = { onOkData() })
                .showIgnoreState(this)
        } else {
            VoiceMaterialDialog(
                onRecordAct = { onRecourd() },
                onVideoidAct = { onVideo() })
                .showIgnoreState(this)
        }
    }

    fun startVoice() {
        if (mModel.toolTaskBean.id != "") {
            openActivity<VoiceTextInputActivity> {
                putParcelable("dataBean", mModel.toolTaskBean)
            }
        }
    }

    fun onRecourd() {
        SPUtils.isMaterial = false
        val language = when (SPbaseUtils.spLanguage) {
            "zh" -> LanguageConfig.CHINESE
            "zh-tw" -> LanguageConfig.TRADITIONAL_CHINESE
            "es" -> LanguageConfig.SPANISH
            "de" -> LanguageConfig.GERMANY
            "fr" -> LanguageConfig.FRANCE
            "sv" -> LanguageConfig.SV
            "ar" -> LanguageConfig.AR
            "ja" -> LanguageConfig.JAPAN
            "ko" -> LanguageConfig.KOREA
            "ku" -> LanguageConfig.KU
            "iw" -> LanguageConfig.IW
            "fa" -> LanguageConfig.FA
            "tr" -> LanguageConfig.TR
            "pt-br" -> LanguageConfig.PT_BR
            "pt-pt" -> LanguageConfig.PORTUGAL
            "it" -> LanguageConfig.IT
            else -> LanguageConfig.ENGLISH
        }
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofAudio())
            .setLanguage(language)
            .isInfo(false)
            .isDisplayCamera(false)
            .setMaxSelectNum(1)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setImageEngine(GlideEngine.createGlideEngine())
            .setSelectMaxDurationSecond(60)
            .setSelectMinDurationSecond(7)
            .setFilterVideoMaxSecond(60)
            .setFilterVideoMinSecond(7)
            .setFilterMaxFileSize(1024 * 10)
            .setSelectMaxFileSize(1024 * 10)
            .setSandboxFileEngine(MeSandboxFileEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>,frament: Fragment) {
                    if (result.size > 0) {
                        result[0]?.apply {
                            if (availablePath.isNotEmpty()) {
                                frament.openActivity<VoiceCutActivity> {
                                    putString("audio_url", sandboxPath ?: availablePath)
                                    putBoolean("audio_voide", true)
                                }
                            } else {
                                toast("Audio loading failed, please choose another image")
                            }
                        }
                    } else {
                        toast("Audio loading failed, please choose another image")
                    }
                }

                override fun onCancel() {

                }

                override fun onCamera() {

                }

                override fun onHint(hoACT: FragmentActivity) {

                }
            })
    }

    fun onVideo() {
        SPUtils.isMaterial = false
        val language = when (SPbaseUtils.spLanguage) {
            "zh" -> LanguageConfig.CHINESE
            "zh-tw" -> LanguageConfig.TRADITIONAL_CHINESE
            "es" -> LanguageConfig.SPANISH
            "de" -> LanguageConfig.GERMANY
            "fr" -> LanguageConfig.FRANCE
            "sv" -> LanguageConfig.SV
            "ar" -> LanguageConfig.AR
            "ja" -> LanguageConfig.JAPAN
            "ko" -> LanguageConfig.KOREA
            "ku" -> LanguageConfig.KU
            "iw" -> LanguageConfig.IW
            "fa" -> LanguageConfig.FA
            "tr" -> LanguageConfig.TR
            "pt-br" -> LanguageConfig.PT_BR
            "pt-pt" -> LanguageConfig.PORTUGAL
            "it" -> LanguageConfig.IT
            else -> LanguageConfig.ENGLISH
        }
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofVideo())
            .setLanguage(language)
            .isInfo(false)
            .isDisplayCamera(false)
            .setMaxSelectNum(1)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setImageEngine(GlideEngine.createGlideEngine())
            .setSelectMaxDurationSecond(60)
            .setSelectMinDurationSecond(7)
            .setFilterVideoMaxSecond(60)
            .setFilterVideoMinSecond(7)
            .setFilterMaxFileSize(1024 * 50)
            .setSelectMaxFileSize(1024 * 50)
            .setSandboxFileEngine(MeSandboxFileEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>,frament: Fragment) {
                    if (result.size > 0) {
                        result[0]?.apply {
                            if (availablePath.isNotEmpty()) {
                                frament.openActivity<VoiceCutActivity> {
                                    putString("audio_url", sandboxPath ?: availablePath)
                                    putBoolean("audio_voide", false)
                                }
                            } else {
                                toast("loading failed, please choose another video")
                            }
                        }
                    } else {
                        toast("loading failed, please choose another video")
                    }
                }

                override fun onCancel() {

                }

                override fun onCamera() {

                }

                override fun onHint(hoACT: FragmentActivity) {

                }
            })
    }

    fun onPlayClick() {
        showLoading()
        val voiceUrl = mModel.toolTaskBean.getUrl() ?: ""
        if (voiceUrl != "") {
            try {
                mMediaPlayer.reset()
                mMediaPlayer.setDataSource(voiceUrl)
                mMediaPlayer.prepareAsync()
                mMediaPlayer.setOnCompletionListener {
                    soundMaterialAdapter.playIndex=-1
                }

                // 设置监听器
                mMediaPlayer.setOnPreparedListener {
                    soundMaterialAdapter.playIndex=1
                    mMediaPlayer.start()
                    dismissLoading()
                }

                mMediaPlayer.setOnErrorListener { mp, what, extra ->
                    // 错误处理
                    toast(getString(R.string.fail))
                    dismissLoading()
                    false
                }
            } catch (e: Exception) {
                toast(R.string.fail)
//                e.printStackTrace();
            }
        } else {
            toast(R.string.retry)
            dismissLoading()
        }

    }

    val onSingOClick: (ToolTaskBean?) -> Unit = {
        if (it?.state == 1 || it?.state == 3 || it?.state == 0) {
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
        ) { onUpdateData(bean, it) }.showIgnoreState(this)
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

    private fun onUpdateData(bean: ToolTaskBean?, name: String) {
        mModel.updataUserGenerateRecords(bean?.id, name)
    }

    override fun onStop() {
        super.onStop()
        soundMaterialAdapter.playIndex=-1
        mMediaPlayer.stop()
    }

    override fun onResume() {
        super.onResume()
        mModel.getUserRecordPage()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer.stop()
        mMediaPlayer.reset()
        mMediaPlayer.release()
    }

    /**
     * 自定义沙盒文件处理
     */
    class MeSandboxFileEngine : UriToFileTransformEngine {
        override fun onUriToFileAsyncTransform(
            context: Context,
            srcPath: String,
            mineType: String,
            call: OnKeyValueResultCallbackListener
        ) {
            call.onCallback(
                srcPath,
                SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType)
            )
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.adapter, soundMaterialAdapter)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}