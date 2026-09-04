package com.face.ui.tmoves

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.apkfuns.logutils.LogUtils
import com.face.BR
import com.face.R
import com.face.bean.AiFaceBean
import com.face.databinding.ActivityMovesBinding
import com.face.key.AiTaskType
import com.face.ui.BaseBindingActivity
import com.face.ui.FunZoneHistoryActivity
import com.face.ui.ToolGeneratingActivity
import com.face.ui.VipActivity
import com.face.util.GVM
import com.face.util.GlideEngine
import com.face.util.SPUtils
import com.face.video.CustomManager
import com.face.video.EmptyVideo
import com.face.video.VideoHeaderManager
import com.face.view.BaseRedDialog
import com.face.view.NotAIPointsDialog
import com.face.viewmodel.activity.MovesViewModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import kotlin.getValue
import kotlin.text.ifEmpty


class MovesActivity : BaseBindingActivity<ActivityMovesBinding, MovesViewModel>(
    R.layout.activity_moves,
    MovesViewModel::class.java
) {
    private val stype by intentExtras("stype", "")
    private val imgUrl by intentExtras("imgUrl", "")
    private val videoUrl by intentExtras("videoUrl", "")
    var tagVideo = ""

    override fun init(savedInstanceState: Bundle?) {
        bar {
            transparent()
        }
        mBinding?.videoView?.loadBgImage5(imgUrl)
        tagVideo = mBinding?.videoView?.getVideoKey() ?: ""
        GSYVideoOptionBuilder()
            .setIsTouchWiget(false)
            .setVideoTitle("")
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setPlayTag(EmptyVideo.Tag)
            .setLooping(true)
            .setCacheWithPlay(true)
            .setShowFullAnimation(false)
            .setNeedLockFull(false)
            .setPlayPosition(0)
            .setMapHeadData(VideoHeaderManager.headers)
            .setUrl(videoUrl)
            .setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPlayError(url: String?, vararg objects: Any?) {
                    super.onPlayError(url, *objects)
                    mBinding?.videoView?.setUp( url,true, null,VideoHeaderManager.headers,"")
                    mBinding?.videoView?.startPlayLogic()
                }
            })
            .build(mBinding?.videoView)
        mBinding?.videoView?.postDelayed({
            mBinding?.videoView?.startPlayLogic()
        },200)
        mModel.taskId.observe(this) {
            openActivity<ToolGeneratingActivity> {
                putString("taskType", AiTaskType.IMG2VIDEO)
                putString("taskId", it)
            }
        }
        GVM.INSTANT.userInfo.observe(this){
            mBinding?.apply {
                tvBalance.setShadowLayer(1f, 2f, 2f, getColorX(R.color.colorBgBlack32))
                tvBalance.text = String.format(
                    resources.getString(R.string.balance_alpoints),
                    GVM.INSTANT.userInfo.value.tflops.toString()
                )
            }
        }
    }

    fun postSend() {
        if (!mModel.isChoose.value) {
            toast(getString(R.string.hug_hint))
            return
        }
        if (GVM.INSTANT.userInfo.value.tflops < mModel.movesPinot.toInt()) {
            GVM.INSTANT.payPage.value="Buy_AIPoints_Moves"
            NotAIPointsDialog().showIgnoreState(this)
        } else {
            BaseRedDialog(
                getString(R.string.note_dialog),
                getString(R.string.hint_spent),
                getString(R.string.vip_continue),
                getString(R.string.cancel),
                unLine=false,
                onBtnOK = {
                    showLoading(getString(R.string.picpost_uploading), false)
                    mModel.uploadPicture(stype)
                }
            ).showIgnoreState(mActivity)
        }
    }

    fun selectorPhoto() {
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
            .openGallery(SelectMimeType.ofImage())
            .setLanguage(language)
            .isDisplayCamera(false)
            .isInfo(false)
            .isGif(false)
            .isWebp(false)
            .isBack(true)
            .setMaxSelectNum(1)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setImageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>, fragment: Fragment) {
                    if (result.isNotEmpty()) {
                        result[0]?.apply {
                            if (availablePath.isNotEmpty()) {
                                mModel.postImg = sandboxPath ?: availablePath
                                mBinding?.apply {
                                    imgChoose1.loadImage( mModel.postImg)
                                }
                                mModel.isChoose.value=true
                            } else {
                                toast("Image loading failed, please choose another image")
                            }
                        }
                    } else {
                        toast("Image loading failed, please choose another image")
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

    override fun onPause() {
        super.onPause()
        mBinding?.videoView?.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        GVM.INSTANT.refreshUserInfo()
        mBinding?.videoView?.onVideoResume()
    }

    override fun onDestroy() {
        if (tagVideo.isNotEmpty()) CustomManager.clearAllVideo(tagVideo)
        super.onDestroy()
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}