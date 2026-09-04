package com.face.ui.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.request.videoFrameMillis
import com.apkfuns.logutils.LogUtils
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.FileSizeUnit
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.engine.UriToFileTransformEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.utils.SandboxTransformUtils
import com.face.util.GVM
import com.face.BR
import com.face.R
import com.face.adapter.explore.ExploreBannerAdapter
import com.face.adapter.other.CustomAdapter
import com.face.bean.AiFaceBean
import com.face.bean.VideoAipointBean
import com.face.bean.VideoLongBean
import com.face.databinding.FragmentCustomVideotypeBinding
import com.face.net.Repository
import com.face.ui.App
import com.face.ui.VipActivity
import com.face.ui.share.ShareMeActivity
import com.face.ui.tcustom.PostPicActivity
import com.face.ui.tcustom.VideoClipsLongActivity
import com.face.util.FileUtil
import com.face.util.GlideEngine
import com.face.util.SPUtils
import com.face.view.BugSlotsDialog
import com.face.view.DeteleMaterialDialog
import com.face.view.NotAIPointsDialog
import com.face.view.sharez.SharingDialog
import com.face.viewmodel.activity.CustomVideoViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.arguments
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import java.io.File

class CustomVideoTypeFragment() :
    BaseBindingFragment<FragmentCustomVideotypeBinding, CustomVideoViewModel>(
        R.layout.fragment_custom_videotype,
        CustomVideoViewModel::class.java
    ) {
    private val isShare: Boolean by arguments("isShare", false)

    private val customAdapter: CustomAdapter by lazy {
        CustomAdapter(isShare)
    }

    // 在 Fragment 中获取数据
    var key = ""

    private var videoAipoints =
        MoshiHelper.adapter(VideoAipointBean::class.java).fromJson(SPUtils.videoAipoints)
            ?: VideoAipointBean()

    private var expendAiPoint = 0

    override fun init(savedInstanceState: Bundle?) {

        key = arguments?.getString("param").toString()

        GVM.INSTANT.userInfo.observe(this, Observer {
            launch(Dispatchers.Main) {
                mBinding?.tvBalance?.text = String.format(
                    resources.getString(R.string.balance_alpoints),
                    GVM.INSTANT.userInfo.value.tflops.toString()
                )
            }
        })
        mBinding?.apply {
            tvBalance.text = String.format(
                resources.getString(R.string.balance_alpoints),
                GVM.INSTANT.userInfo.value.tflops.toString()
            )
            when (key) {
                "Picture" -> {
                    expendAiPoint = videoAipoints.typePic
                    if (videoAipoints.typePic == 0) {
                        tvAiPoint.visibility = View.GONE
                        tvOriginal.visibility = View.GONE
                        tvBalance.visibility = View.GONE
                        tvHint.visibility = View.GONE
                    } else {
                        tvAiPoint.text = String.format(
                            resources.getString(R.string.video_aipoint),
                            videoAipoints.typePic.toString()
                        )
                        tvOriginal.text = String.format(
                            resources.getString(R.string.video_o),
                            videoAipoints.typePicOriginal.toString()
                        )
                    }
                    mModel.subType="image"
                    mModel.type = "image"
                    mModel.param = ""
                }

                "3mins" -> {
                    expendAiPoint = videoAipoints.type3
                    tvAiPoint.text = String.format(
                        resources.getString(R.string.video_aipoint),
                        videoAipoints.type3.toString()
                    )
                    tvOriginal.text = String.format(
                        resources.getString(R.string.video_o),
                        videoAipoints.type3Original.toString()
                    )
                    mModel.subType="180"
                    mModel.param =
                        MoshiHelper.convertObjectToJson(VideoLongBean(video_type = "180"))
                }

                "5mins" -> {
                    expendAiPoint = videoAipoints.type5
                    tvAiPoint.text = String.format(
                        resources.getString(R.string.video_aipoint),
                        videoAipoints.type5.toString()
                    )
                    tvOriginal.text = String.format(
                        resources.getString(R.string.video_o),
                        videoAipoints.type5Original.toString()
                    )
                    mModel.subType="300"
                    mModel.param =
                        MoshiHelper.convertObjectToJson(VideoLongBean(video_type = "300"))
                }

                else -> {
                    expendAiPoint = videoAipoints.type2
                    if (videoAipoints.type2 == 0) {
                        tvAiPoint.visibility = View.GONE
                        tvOriginal.visibility = View.GONE
                        tvBalance.visibility = View.GONE
                        tvHint.visibility = View.GONE
                    } else {
                        tvAiPoint.text = String.format(
                            resources.getString(R.string.video_aipoint),
                            videoAipoints.type2.toString()
                        )
                        tvOriginal.text = String.format(
                            resources.getString(R.string.video_o),
                            videoAipoints.type2Original.toString()
                        )
                    }
                    mModel.subType="20"
                    mModel.param = MoshiHelper.convertObjectToJson(VideoLongBean(video_type = "20"))
                }
            }
        }
        customAdapter.onBuyClick = { data ->
            onBuyData()
        }
        customAdapter.onDeleteClick = { date ->
            DeteleMaterialDialog { onDeleteData(date) }.showIgnoreState(this)
        }
        customAdapter.onSonClick = { v, po, data ->
            if (!data.getIsShareState()) {
                //分享
                SharingDialog(onSharing = {
                    mModel.saveMediaShare(data.id) {
                        (requireActivity() as ShareMeActivity).mModel.isShareRefresh.value = 0
                    }
                }).showIgnoreState(this)
            }
        }

        mModel.customFaceList.observe(this) {
            GVM.INSTANT.showListSize.value = it.size
            if (it.size < 2) {
                customAdapter.setShowDelete(false)
            }
        }

        GVM.INSTANT.showDelete.observe(this) {
            if ((mModel.customFaceList.value?.size ?: 0) > 1) {
                customAdapter.setShowDelete(it)
            }
        }
    }

//  短视频可以，长视频卡主线程
//    fun loadVideoFirstFrame(videoUri: Uri, onResult: (Bitmap?) -> Unit) {
//        val imageLoader = ImageLoader.Builder(requireActivity())
//            .components {
//                add(VideoFrameDecoder.Factory())
//            }
//            .build()
//
//        val request = ImageRequest.Builder(requireActivity())
//            .data(videoUri)
//            .videoFrameMillis(0)
//            .allowHardware(false) // 必须关闭硬件位图才能获取 Bitmap
//            .target { drawable ->
//                showLoading()
//                val bitmap = (drawable as? BitmapDrawable)?.bitmap
//                onResult(bitmap)
//            }
//            .listener(
//                onError = { _, _ -> onResult(null) }
//            )
//            .build()
//        imageLoader.enqueue(request)
//    }

    private fun onDeleteData(bean: AiFaceBean) {
        mModel.deleteUserAiMedia(bean)
    }

    fun onPhotoClick() {
        if (GVM.INSTANT.isVip.value) {
            if (GVM.INSTANT.userInfo.value.tflops < expendAiPoint) {
                GVM.INSTANT.payPage.value = "Buy_AIPoints_AddCustom"
                NotAIPointsDialog().showIgnoreState(activity)
            } else {
                if (mModel.subNum>0){
                    selectorVideo()
                }else{
                    onBuyData()
                }
            }
        } else {
            if (GVM.INSTANT.isShowTool.value == 0) {
                GVM.INSTANT.payPage.value = "Home_tool0_video"
            } else {
                GVM.INSTANT.payPage.value = "Home_tool_video"
            }
            VipActivity.jump(requireActivity())
        }
    }
    private fun onBuyData() {
        BugSlotsDialog(
            key.lowercase(),
            onBuy = { num->
                mModel.payUserMediaNum(num)
            })
            .showIgnoreState(this)
    }
    fun selectorVideo() {
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
        val timeMin: Int
        val timeMax: Int
        var sizeFile: Long
        var pictureSelectionModel =SelectMimeType.ofVideo()
        when (key) {
            "Picture" -> {
                timeMin = 0
                timeMax = 60 * 30
                sizeFile = FileSizeUnit.GB * 1
                pictureSelectionModel= SelectMimeType.ofImage()
            }

            "1min" -> {
                timeMin = 2 * 1
                timeMax = 60 * 30
                sizeFile = FileSizeUnit.GB * 1
            }

            "3mins" -> {
                timeMin = 20 * 1
                timeMax = 60 * 30 * 2
                sizeFile = FileSizeUnit.GB * 2
            }

            "5mins" -> {
                timeMin = 20 * 1
                timeMax = 60 * 30 * 3
                sizeFile = FileSizeUnit.GB * 2
            }

            else -> {
                timeMin = 30 * 1
                timeMax = 60 * 30
                sizeFile = FileSizeUnit.GB * 1
            }
        }

        PictureSelector.create(this)
            .openGallery(pictureSelectionModel)
            .setLanguage(language)
            .isDisplayCamera(false)
            .isInfo(false)
            .isFilterSizeDuration(true)
            .setFilterVideoMaxSecond(timeMax)
            .setFilterVideoMinSecond(timeMin)
            .setFilterMaxFileSize(sizeFile)
            .setSelectMaxFileSize(sizeFile)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setImageEngine(GlideEngine.createGlideEngine())
            .setSandboxFileEngine(MeSandboxFileEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>, frament: Fragment) {
                    if (result.isNotEmpty()) {
                        result[0]?.apply {
                            val file = File(availablePath ?: "")  // 替换为实际的文件路径
                            if (file.exists()) {
                                when (key) {

                                    "Picture" -> {
                                        frament.openActivity<PostPicActivity> {
                                            putString("pic_url", sandboxPath ?: availablePath)
                                        }
                                    }
                                    else -> {
                                        frament.openActivity<VideoClipsLongActivity> {
                                            putString("path", sandboxPath ?: availablePath)
                                            putString("paramType", mModel.param)
                                            putBoolean("isShare", isShare)
                                        }
                                    }
                                }
                            } else {
                                toast(getString(R.string.path_error))
                            }
                        }
                    } else {
                        toast(getString(R.string.path_error))
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