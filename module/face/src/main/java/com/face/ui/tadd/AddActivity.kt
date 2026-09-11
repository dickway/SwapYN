package com.face.ui.tadd

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.LogUtils
import com.face.util.GVM
import com.face.R
import com.face.BR
import com.face.bean.DynamicAddBean
import com.face.databinding.ActivityAddBinding
import com.face.ui.FunZoneHistoryActivity
import com.face.ui.ToolGeneratingActivity
import com.face.util.SPUtils
import com.face.ui.BaseBindingActivity
import com.face.ui.VipActivity
import com.face.util.GlideEngine
import com.face.view.BaseRedDialog
import com.face.view.NotAIPointsDialog
import com.face.viewmodel.activity.AddViewModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlin.getValue

class AddActivity : BaseBindingActivity<ActivityAddBinding, AddViewModel>(
    R.layout.activity_add,
    AddViewModel::class.java
) {
    val toolType by intentExtras("tool_type", "")
    var toolList: List<DynamicAddBean> = emptyList()
    var dynamicAddBean = DynamicAddBean()

    override fun init(savedInstanceState: Bundle?) {
        toolList = safeParseToolList()
        dynamicAddBean = toolList.find { it.taskType == toolType } ?: DynamicAddBean()
        if (dynamicAddBean.id == -1) {
            toast("Configuration abnormality!")
            finish()
            return
        }
        mModel.isSingular.value = dynamicAddBean.isOneChoose
        mModel.taskT = dynamicAddBean.taskType
        mBinding?.apply {
            mModel.usePinot.value = dynamicAddBean.usePoint
            tvName.text = dynamicAddBean.getName()
            //标题和描述
            if (dynamicAddBean.getHintTitel().isEmpty()) {
                txt1.visibility = View.GONE
            }
            if (dynamicAddBean.getHintTitel().isEmpty() && dynamicAddBean.getHintContent()
                    .isEmpty()
            ) {
                lin1.visibility = View.GONE
            }
            txt1.text = dynamicAddBean.getHintTitel()
            txt2.text = dynamicAddBean.getHintContent()

            //选择第一个图的描述
            if (dynamicAddBean.getImgTitle1().isEmpty()) {
                tvName1.visibility = View.GONE
            }
            if (dynamicAddBean.getImgTitle1().isEmpty() && dynamicAddBean.getImgDesc1().isEmpty()) {
                lin2.visibility = View.GONE
            }
            tvName1.text = dynamicAddBean.getImgTitle1()
            tvDes1.text = dynamicAddBean.getImgDesc1()

            //选择第二天图的描述
            if (dynamicAddBean.getImgTitle2().isEmpty()) {
                tvName2.visibility = View.GONE
            }
            if (dynamicAddBean.getImgTitle2().isEmpty() && dynamicAddBean.getImgDesc2().isEmpty()) {
                lin3.visibility = View.GONE
            }
            tvName2.text = dynamicAddBean.getImgTitle2()
            tvDes2.text = dynamicAddBean.getImgDesc2()
        }
        mModel.taskId.observe(this) {
            if (it.isNotEmpty()) {
                initData()
                openActivity<ToolGeneratingActivity> {
                    putString("taskId", it)
                    putString("taskType", toolType)
                }
            }
        }

    }

    private fun safeParseToolList(): List<DynamicAddBean> {
        return try {
            val json = SPUtils.listDynamicTools
            if (json.isBlank()) {
                emptyList()
            } else {
                MoshiHelper.convertJsonToList(json) ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        initData()
    }

    fun initData() {
        //选择图片模式
        mModel.isSingular.value = dynamicAddBean.isOneChoose
        mModel.isChoose.value = false
        mModel.img1.value = ""
        mModel.img2.value = ""
        mModel.taskId.value = ""
        mModel.firstImg = ""
        mBinding?.apply {
            imgChoose1.loadImage(mModel.img1.value)
            imgChoose2.loadImage(mModel.img2.value)
        }
    }


    fun postSend() {
        if (!mModel.isChoose.value) {
            toast(getString(R.string.hug_hint))
            return
        }
        mModel.firstImg = ""
        if (GVM.INSTANT.userInfo.value.tflops < mModel.usePinot.value) {
            GVM.INSTANT.payPage.value = dynamicAddBean.eventName
            NotAIPointsDialog().showIgnoreState(this)
        } else {
            if (!dynamicAddBean.isCanStop) {
                BaseRedDialog(
                    getString(R.string.note_dialog),
                    getString(R.string.hint_spent),
                    getString(R.string.vip_continue),
                    getString(R.string.cancel),
                    unLine = false,
                    onBtnOK = {
                        showLoading(getString(R.string.picpost_uploading), false)
                        mModel.uploadPicture(mModel.img1.value)
                    }
                ).showIgnoreState(mActivity)
            } else {
                showLoading(getString(R.string.picpost_uploading), false)
                mModel.uploadPicture(mModel.img1.value)
            }
        }
    }

    fun selectorPhoto(index: Int) {
        GVM.INSTANT.payPage.value = dynamicAddBean.eventName
        if (!GVM.INSTANT.isVip.value) {
            VipActivity.jump(this@AddActivity)
            return
        }
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
                                when (index) {
                                    1 -> mModel.img1.value = sandboxPath ?: availablePath
                                    2 -> mModel.img2.value = sandboxPath ?: availablePath
                                }
                                mBinding?.apply {
                                    imgChoose1.loadImage(mModel.img1.value)
                                    imgChoose2.loadImage(mModel.img2.value)
                                }
                                if (mModel.isSingular.value) {//单图模式是否选择了图片，刷新按钮状态
                                    if (mModel.img1.value.isNotEmpty())
                                        mModel.isChoose.postValue(true)
                                } else {
                                    if (mModel.img1.value.isNotEmpty() &&
                                        (mModel.img2.value.isNotEmpty() || !dynamicAddBean.needSelect)) {
                                        mModel.isChoose.postValue(true)
                                    }
                                }
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

    fun toRecord() {
        openActivity<FunZoneHistoryActivity> {
            putString("aiTaskType", dynamicAddBean.taskType)
        }
    }

    override fun onResume() {
        super.onResume()
        GVM.INSTANT.refreshUserInfo()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}