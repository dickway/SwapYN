package com.face.ui.tcustom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
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
import com.face.adapter.other.CustomAdapter
import com.face.bean.AiFaceBean
import com.face.databinding.ActivityPhotoCustomBinding
import com.face.ui.App
import com.face.ui.VipActivity
import com.face.util.GVM
import com.face.util.GlideEngine
import com.face.util.SPUtils
import com.face.view.DeteleMaterialDialog
import com.face.viewmodel.activity.CustomPhotoViewModel
import com.face.ui.BaseBindingActivity
import com.face.view.BaseContentDialog
import com.face.view.BugSlotsDialog
import com.face.view.FaceHDDialog
import com.face.view.WarningDialog
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast


class CustomPhotoActivity : BaseBindingActivity<ActivityPhotoCustomBinding, CustomPhotoViewModel>(
    R.layout.activity_photo_custom,
    CustomPhotoViewModel::class.java
) {

    val customAdapter: CustomAdapter = CustomAdapter()

    override fun init(savedInstanceState: Bundle?) {

        if (!SPUtils.faceWarning) WarningDialog().showIgnoreState(mActivity)

        customAdapter.onDeleteClick = { date ->
            DeteleMaterialDialog { onDeleteData(date) }.showIgnoreState(this)
        }
        customAdapter.onBuyClick = { data ->
            onBuyData()
        }

        mModel.customFaceList.observe(this) {
            if (it.size < 2) {
                customAdapter.isShowDe = false
                customAdapter.setShowDelete(false)
                mBinding?.editTxt?.text = getString(R.string.edit)
            }
        }

    }


    fun onShowDelete() {
        if ((mModel.customFaceList.value?.size ?: 0) > 1) {
            customAdapter.setShowDelete(!customAdapter.isShowDe)
            mBinding?.editTxt?.text =
                if (customAdapter.isShowDe) getString(R.string.cancel) else getString(R.string.edit)
        }
    }

    private fun onBuyData() {
        BugSlotsDialog(
            "image",
            onBuy = { num->
                mModel.payUserMediaNum(num)
            })
            .showIgnoreState(this)
    }

    private fun onDeleteData(bean: AiFaceBean) {
        mModel.deleteUserAiMedia(bean)
    }

    fun onPhotoClick() {
        if (GVM.INSTANT.isVip.value) {
            if (mModel.subNum>0){
                selectorPhoto()
            }else{
                onBuyData()
            }
        } else {
            if (GVM.INSTANT.isShowTool.value == 0) {
                GVM.INSTANT.payPage.value = "Home_tool0_image"
            } else {
                GVM.INSTANT.payPage.value = "Home_tool_image"
            }
            VipActivity.jump(this@CustomPhotoActivity)
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
            .setSelectionMode(SelectModeConfig.SINGLE)
            .isInfo(false)
            .isDefaultPhoto(true)
            .isFilterSizeDuration(true)
            .isWebp(false)
            .isGif(false)
            .setImageEngine(GlideEngine.createGlideEngine())
            .setSandboxFileEngine(MeSandboxFileEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>, frament: Fragment) {
                    if (result.size > 0) {
                        result[0]?.apply {
                            if (availablePath.isNotEmpty()) {
                                frament.openActivity<PostPicActivity> {
                                    putString("pic_url", sandboxPath ?: availablePath)
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

                override fun onHint(activity: FragmentActivity?) {
                }

                override fun onCamera() {
                }
            })
    }


    override fun onResume() {
        super.onResume()
        GVM.INSTANT.refreshUserInfo()
        mModel.getUserAiMedia()
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

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.adapter, customAdapter)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}