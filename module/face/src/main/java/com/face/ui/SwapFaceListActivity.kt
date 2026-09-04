package com.face.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.apkfuns.logutils.LogUtils
import com.face.BR
import com.face.R
import com.face.ad.AdUtil
import com.face.adapter.swap.SwapTabAdapter
import com.face.bean.AiFaceBean
import com.face.bean.MyFaceImgBean
import com.face.bean.UseTypeBean
import com.face.bean.VipUserCountBean.Companion.toMap
import com.face.databinding.ActivitySwapListBinding
import com.face.key.AiTaskType
import com.face.key.Constants
import com.face.net.Repository
import com.face.ui.fragment.SwapFragment
import com.face.util.EventUtil
import com.face.util.FileUtil
import com.face.util.GVM
import com.face.util.GlideEngine
import com.face.util.SPUtils
import com.face.view.AdsShowDialog
import com.face.view.AdsVipShowDialog
import com.face.view.BaseRedDialog
import com.face.view.ExperienceDialog
import com.face.view.FaceHDDialog
import com.face.view.HDModePiontDialog
import com.face.view.NotAIPointsDialog
import com.face.view.SwapHintDialog
import com.face.view.SwapTabDialog
import com.face.viewmodel.activity.SwapListViewModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


class SwapFaceListActivity : BaseBindingActivity<ActivitySwapListBinding, SwapListViewModel>(
    R.layout.activity_swap_list,
    SwapListViewModel::class.java
) {

    val mediaId by intentExtras("mediaId", "")
    private val listData by intentExtras("faceList", mutableListOf<AiFaceBean>())

    private lateinit var launcherPermission: ActivityResultLauncher<String>

    private var tabTitles = mutableListOf("Swap", "Pro swap")
    private var swapFragments =
        mutableListOf(SwapFragment.newInstance("Swap"), SwapFragment.newInstance("Pro swap"))
    private val swapTabAdapter: SwapTabAdapter = SwapTabAdapter()


    private val tabAdapter: FragmentStatePagerAdapter by lazy {
        object : FragmentStatePagerAdapter(
            supportFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            override fun getCount() = swapFragments.size

            override fun getItem(position: Int): Fragment {
                return swapFragments[position]
            }

            override fun getPageTitle(position: Int): CharSequence {
                return tabTitles[position]
            }
        }
    }

    companion object {
        fun jump(
            context: Context,
            mediaId: String,
            position: Int,
            faceList: List<AiFaceBean>? = mutableListOf(),
        ) {
            if (faceList?.isNotEmpty() == true)
                context.openActivity<SwapFaceListActivity> {
                    putString("mediaId", mediaId)
                    putInt("position", position)
                    putParcelableArrayList(
                        "faceList",
                        faceList.filter { it.id.isNotEmpty() } as ArrayList)
                }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        if (listData.isEmpty()) finish()

        mModel.isReport.value = GVM.INSTANT.isShowTool.value == 0

        //权限申请
        launcherPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {//同意
                    val downloadFile = File(Constants.savePath, mModel.downName)
                    FileUtil.fileSaveToPublic(downloadFile.path, mModel.pathName)
                } else {//拒绝
                    val intent = Intent()
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.setData(Uri.parse("package:" + packageName))
                    startActivity(intent)
                    toast(getString(R.string.permissions_storage))
                }
            }

        GVM.INSTANT.swapSelectList.value = mutableListOf()
        GVM.INSTANT.swapTabList.value = mutableListOf()
        GVM.INSTANT.selectBean.value = MyFaceImgBean()

        SPUtils.isMaterial = true
        mModel.showInterstitial = AdUtil.showInterstitialAdIfNeed()
        //如果是分享素材只展示第一条模糊数据
        if (!GVM.INSTANT.isVip.value && listData.any { it.isBlur }) {
            // 获取模糊的第一个下标
            mModel.indexBlur.value = listData.indexOfFirst { it.isBlur }
        }

        mModel.mediaListBean.value = listData

        mModel.mediaByBean.value = listData[0]
        mModel.mediaSize = mModel.mediaListBean.value.size

        mBinding?.apply {
            tvBalance.setShadowLayer(1f, 2f, 2f, getColorX(R.color.colorBgBlack32))
            tvBalance.text = String.format(
                resources.getString(R.string.balance_alpoints),
                GVM.INSTANT.userInfo.value.tflops.toString()
            )
            tvBalance2.setShadowLayer(1f, 2f, 2f, getColorX(R.color.colorBgBlack32))
            tvBalance2.text = String.format(
                resources.getString(R.string.balance_alpoints),
                GVM.INSTANT.userInfo.value.tflops.toString()
            )
            viewPager.adapter = tabAdapter
            viewPager.offscreenPageLimit = tabTitles.size
            tabLayout.setupWithViewPager(viewPager)
            viewPager.addOnPageChangeListener(onPageChangeListener)
        }


        mModel.mediaByBean.observe(this, Observer {
            it?.apply {
                mModel.isScore.value = (getIsShareState())
                //如果时视频
                if (mediaType != "video") EventUtil.inPage(
                    "swap_face",
                    mapOf("media_id" to id, "media_type" to mediaType)
                )
                //布局刷新
                if (faceNum > 1 || userId.isNotEmpty() || desc == "" || mediaType == "video" || GVM.INSTANT.isShowTool.value == 0) {//多头像,无描述,视频,不支持自定义风格换脸,不显示TabLayout
                    mModel.isSingle.postValue(true)
                    mBinding?.viewPager?.setNoScroll(false)//禁止滑动
                } else {
                    mModel.isSingle.postValue(false)
                    mBinding?.viewPager?.setNoScroll(true)//禁止滑动
                }
                mBinding?.viewPager?.currentItem = 0

                var sharePointsNum = 0
                if (userId.isNotEmpty() && GVM.INSTANT.userInfo.value.userId.toString() != userId) {
                    sharePointsNum = if (getIsShareState() && mediaType == "image") {
                        SPUtils.videoPic.toInt()
                    } else if (param.contains("300", true)) {
                        SPUtils.videoPoints5m.toInt()
                    } else if (param.contains("180", true)) {
                        SPUtils.videoPoints3m.toInt()
                    } else {
                        SPUtils.videoPoints20s.toInt()
                    }
                    if (GVM.INSTANT.isVip.value) {
                        mModel.isReport.value = true
                    }
                } else {
                    mModel.isReport.value = false
                }

                //素材收费情况
                if (getIsShareState() && mediaType == "image") {
                    mModel.usePinot.value = mModel.videoAipoints.typePic + sharePointsNum
                }else if (param.contains("300")) {
                    mModel.usePinot.value = mModel.videoAipoints.type5 + sharePointsNum
                } else if (it.param.contains("180")) {
                    mModel.usePinot.value = mModel.videoAipoints.type3 + sharePointsNum
                } else if (it.param.contains("20")) {
                    mModel.usePinot.value = mModel.videoAipoints.type2 + sharePointsNum
                } else {
                    mModel.usePinot.value = 0
                }
                //增强收费
                if (mediaType == "video" && mModel.isHdModel.value && GVM.INSTANT.userInfo.value.vipLv != 5) {
                    mModel.usePinot.value = mModel.usePinot.value + SPUtils.hdPoints
                }

                tabAdapter.notifyDataSetChanged()

                //查询是否收藏
                mModel.getUserCollectUrl()
                //触发历史记录
//                mModel.getData(mediaId)

                //数据刷新
                mModel.isVideo.value = mediaType == "video"
                GVM.INSTANT.hasAD.postValue(false)
                GVM.INSTANT.ofLateSwap.value = id
                mModel.collectStr = collectId
                mModel.isCollect.value = collectId != "0"
                val findNull = faceList?.find { it.isSelectBean != null }
                GVM.INSTANT.swapTabList.value = faceList?.apply {
                    if (GVM.INSTANT.swapSelectList.value?.size!! > 0 && findNull == null) {
                        getOrNull(0)?.isSelectBean = GVM.INSTANT.swapSelectList.value?.getOrNull(0)
                    }
                }

                launch(Dispatchers.Main) {
                    delay(300)
                    mBinding?.rlTab?.scrollToPosition(0)
                }
                GVM.INSTANT.selectNum.value = 0
//                GVM.INSTANT.swapTabList.value = faceList?.onEach { bean ->
//                    if (GVM.INSTANT.swapSelectList.value?.size!! > 0 && bean.isSelectBean == null && findNull == null) {
//                        bean.isSelectBean = GVM.INSTANT.swapSelectList.value?.get(0)
//                    }
//                }
            }
        })

        observeInit()

        swapTabAdapter.onItemClick = { _, bean, position ->
            //选择脸弹窗
            SwapTabDialog(
                mModel.mediaByBean.value?.mediaType,
                onToAct = { onProductionClick(1) },
                onToPhoto = { selectorPhoto() }
            ).showIgnoreState(this)
            GVM.INSTANT.selectNum.value = position
        }
    }

    fun observeInit() {
        mModel.isDownload.observe(this) {//下载图片完成处理图片存储路径
            if (it) {
                val downloadFile = File(Constants.savePath, mModel.downName)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    FileUtil.copyFileToDownloadDir(downloadFile.path, mModel.pathName)
                } else {
                    checkPermission()
                }
            }
        }

        GVM.INSTANT.userInfo.observe(this, Observer {
            if (it.isVip() && mModel.isInPageLifetimeVip != 5 && mModel.isHdModel.value && it.vipLv == 5) {//如果是会员界面返回就刷新数据
                //增强收费取消
                mModel.isInPageLifetimeVip = 5
                mModel.usePinot.value = mModel.usePinot.value - SPUtils.hdPoints
            }
            launch(Dispatchers.Main) {
                mBinding?.apply {
                    tvBalance.text = String.format(
                        resources.getString(R.string.balance_alpoints),
                        it.tflops.toString()
                    )
                    tvBalance2.text = String.format(
                        resources.getString(R.string.balance_alpoints),
                        it.tflops.toString()
                    )
                }
            }
        })

        GVM.INSTANT.isVip.observe(this, Observer {
            if (it && !mModel.isInPageVip) {//如果是会员界面返回就刷新数据
                mModel.indexBlur.value = -1
                mModel.isBlurView.postValue(false)
                if (mModel.mediaByBean.value?.userId?.isNotEmpty() == true && GVM.INSTANT.userInfo.value.userId.toString() != mModel.mediaByBean.value?.userId) {
                    mModel.isReport.value = true
                }
            }
        })

        mModel.taskBean.observe(this, Observer {
            if (it?.id != null) {
                val imgurl = mModel.mediaByBean.value?.webpUrl?.ifEmpty {
                    mModel.mediaByBean.value?.imageUrl
                }
                ProductionActivity.jump(
                    this,
                    0,
                    it.id,
                    urlImg = imgurl
                )
                finish()
            }
        })


        GVM.INSTANT.swapTabList.observe(this, Observer {
            swapTabAdapter.notifyDataSetChanged()
        })

        GVM.INSTANT.hasAD.observe(this, Observer {//广告拿到奖励，取消弹窗
            dismissLoading()
        })

        GVM.INSTANT.selectNum.observe(this, Observer {
            if (it < (GVM.INSTANT.swapTabList.value?.size ?: 0)) {
                swapTabAdapter.selectIndex = it
                mBinding?.rlTab?.scrollToPosition(it)
            }
        })

        GVM.INSTANT.swapSelectList.observe(this, Observer {
            if (it.isNotEmpty()) {
                var isAllSelect = false
                GVM.INSTANT.swapTabList.value?.forEach {
                    it.apply {
                        if (isSelectBean != null) {
                            isAllSelect = true
                        }
                    }
                }
                if (!isAllSelect) {
                    GVM.INSTANT.swapTabList.value =
                        GVM.INSTANT.swapTabList.value?.toMutableList()?.apply {
                            getOrNull(0)?.isSelectBean = it[0]
                        }
                    GVM.INSTANT.selectBean.value = it[0]
                }
            }
        })
    }

    fun onNotAds() {
        showLoading()
        SPUtils.useType = UseTypeBean(type = "SwapFaceAD")
        launchRequestOnIO({
            Repository.subUserTFLOPS(
                SPUtils.useAd,
                type = MoshiHelper.convertObjectToJson(SPUtils.useType)
            )
        }) {
            onSuccess = {
                GVM.INSTANT.refreshUserInfo {
                    dismissLoading()
                    GVM.INSTANT.hasAD.value = true
                    onProductionClick(0)
                }
            }
            onFailed = { _, _, errorMsg ->
                dismissLoading()
                toast(errorMsg)
            }
        }
    }

    //反馈和免责弹窗
    fun onMore() {
        if (GVM.INSTANT.hasNewFeedbackMsg.value) {
            openActivity<FeedbackHistoryActivity> {
                putBoolean("isList", true)
            }
        } else {
            openActivity<FeedbackActivity>()
        }
    }

    //选择是否高清
    fun onHdSelect() {
        HDModePiontDialog(mModel.usePinot.value) { isHd, usePoints ->
            mModel.isHdModel.value = isHd
            mModel.usePinot.value = usePoints
        }.showIgnoreState(mActivity)
    }

    //换脸任务
    fun onProductionClick(isDialog: Int? = 0) {

        EventUtil.appClick("onProductionClick")


        //非会员，付费素材
        if (!GVM.INSTANT.isVip.value && (mModel.mediaByBean.value?.pro != 0 || mModel.isSelectPro != 0)) {
            GVM.INSTANT.payPage.value = "Swap_Pro"
            VipActivity.jump(this@SwapFaceListActivity)
            return
        }


        val proStr = if (mModel.isSelectPro == 0) "swap" else "pro"
        GVM.INSTANT.payPage.value = "SwapFace_${mModel.mediaByBean.value?.mediaType}_${proStr}"
        if (mModel.mediaByBean.value?.id == "") {
            return
        }
        var sources = ""//多人脸图片拼接
        var isNull = false

        //长视频需要积分换脸
        if (mModel.usePinot.value > 0 && GVM.INSTANT.userInfo.value.tflops < mModel.usePinot.value) {
            val cs =
                if (GVM.INSTANT.userInfo.value.userId.toString() != mModel.mediaByBean.value?.userId) "Share" else "Custom"
            GVM.INSTANT.payPage.value = "Buy_AIPoints_VideoFace_$cs"
            NotAIPointsDialog().showIgnoreState(mActivity)
            return
        }

        //多人脸拼接逻辑
        GVM.INSTANT.swapTabList.value?.forEach {
            if (it.isSelectBean != null) {
                isNull = true
                sources = sources + "," + it.isSelectBean?.pic
            } else {
                sources = sources + ","
            }
        }

        //如果初始没有人脸需要弹窗，不执行生成任务
        if (!isNull) {
            if (isDialog == 0) {
                //选择脸弹窗
                SwapTabDialog(
                    mModel.mediaByBean.value?.mediaType,
                    onToAct = { onProductionClick(1) },
                    onToPhoto = { selectorPhoto() }
                ).showIgnoreState(this)
                GVM.INSTANT.selectNum.value = 0
            } else {
                toast(R.string.get_select)
            }
            return
        }

        //非vip用户看广告逻辑
        if (mModel.isSelectPro == 0 && !GVM.INSTANT.userInfo.value.isVip() && !GVM.INSTANT.hasAD.value && mModel.usePinot.value == 0) {
            val num1 = GVM.INSTANT.userInfo.value.mediaNum//用户生成次数
            if (num1 < SPUtils.freeNum.toInt()) {//用户免费生成一次，不处理
                EventUtil.appClick("free1")
            } else if (num1 < SPUtils.freeNum.toInt() && GVM.INSTANT.isShowTool.value == 0) {//a用户免费生成，不处理
                EventUtil.appClick("free2")
            } else if (SPUtils.nowNum < SPUtils.nowAdsNum.toInt()) {//每日广告生成判断
                EventUtil.appClick("AdsShowDialog")
                AdsShowDialog(
                    onToAct = { toShowAD() },
                    onNotAds = { onNotAds() }).showIgnoreState(mActivity)
                return
            } else {//无每日广告次数
                val conStr = if (SPUtils.nowAdsNum == "0") {
                    "Your free attempts have been used up. Upgrade now to unlock more exciting features!"
                } else {
                    String.format(
                        resources.getString(R.string.dialog_experience_txt1),
                        SPUtils.nowAdsNum
                    )
                }
                EventUtil.appClick("ExperienceDialog")
                ExperienceDialog(conStr).showIgnoreState(mActivity)
                return
            }
        }

        //vip用户看广告逻辑
        val useCount = SPUtils.vipUserCount.toMap()[GVM.INSTANT.userInfo.value.userId] ?: 0
        if (mModel.usePinot.value == 0 && SPUtils.vipUsageCount.toInt() > 0 && SPUtils.vipUsageCount.toInt() <= useCount && GVM.INSTANT.userInfo.value.isVip() && !GVM.INSTANT.hasAD.value) {

            if (SPUtils.nowAdsNum == "0") {
                BaseRedDialog(
                    getString(R.string.dialog_ads_vip_title),
                    String.format(
                        resources.getString(R.string.dialog_ads_vip_context2),
                        SPUtils.vipUsageCount
                    ),
                    "${
                        String.format(
                            resources.getString(R.string.consume_point),
                            SPUtils.useAd.toString()
                        )
                    } (${GVM.INSTANT.userInfo.value.tflops})",
                    getString(R.string.cancel),
                    unClick = "1",
                    onBtnOK = {
                        onNotAds()
                    }
                ).showIgnoreState(mActivity)
            } else {
                AdsVipShowDialog(
                    String.format(
                        resources.getString(R.string.dialog_ads_vip_context),
                        SPUtils.vipUsageCount
                    ),
                    onToAct = { toShowAD() },
                    onNotAds = { onNotAds() }).showIgnoreState(mActivity)
            }
            return
        }

        var taskType = ""
        if (mModel.mediaByBean.value?.mediaType == "video") {//是不是视频
            taskType = AiTaskType.VIDEO_FACESWAP
            if (mModel.usePinot.value > 0) {
                taskType = AiTaskType.LONG_VIDEOSWAP
            }
            if (mModel.isHdModel.value) {
                taskType = AiTaskType.ENHANCE_VIDEOSWAP
            }
        } else {
            if (mModel.isSelectPro == 0) {//是不是图片自定义风格 1表示是
                taskType = AiTaskType.FACESWAP
            } else {
                taskType = AiTaskType.GEN_PERSONPIC
            }
        }
        //是否需要执行素材生成
        GVM.INSTANT.userInfo.value.apply {
            mediaNum++
        }

        LogUtils.e(">>>>usePinot:${mModel.usePinot.value}  taskType:$taskType   faceHD:${SPUtils.faceHD}")
        //hd弹窗确认
        if (taskType == AiTaskType.ENHANCE_VIDEOSWAP && SPUtils.faceHD) {
            FaceHDDialog {
                mModel.sendAiTask(taskType, sources.substring(1, sources.length))
            }.showIgnoreState(mActivity)
        } else {
            mModel.sendAiTask(taskType, sources.substring(1, sources.length))
        }
    }

    override fun onResume() {
        super.onResume()
        mModel.getUserPics()
        GVM.INSTANT.refreshUserInfo()
    }

//    fun Activity.restartIfCorrupted() {
//        if (mBinding == null ) {
//            LogUtils.e("界面被回收，重启 Activity")
//            val intent = Intent(this, this::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//            finish()
//        }
//    }


    override fun onPause() {
        super.onPause()
        GVM.INSTANT.hasAD.postValue(false)
    }

    private fun toShowAD() {
        EventUtil.appClick("toShowAD")
        if (!AdUtil.showRewardedAdForRewarded()) {
            showLoading(false)
            GlobalScope.launch(Dispatchers.Main) {
                delay(6 * 1000) // 延迟6秒
                AdUtil.cancelAds = true
                dismissLoading()
                if (!AdUtil.showAds) toast(getString(R.string.ad_load_fail))
            }
        }
    }

    private val onPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            GVM.INSTANT.isSwapR.value = false
            mModel.isSelectPro = position
        }

        override fun onPageScrollStateChanged(state: Int) {
        }
    }


    fun selectorPhoto() {
        EventUtil.appClick("selectorPhoto")
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
            .isGif(false)
            .isWebp(false)
            .setMaxSelectNum(1)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setImageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>, fragment: Fragment) {
                    if (result.size > 0) {
                        result[0]?.apply {
                            if (availablePath.isNotEmpty()) {
                                fragment.openActivity<UploadFaceActivity> {
                                    putString("img_url", sandboxPath ?: availablePath)
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
                    openActivity<CameraActivity>()
                }

                override fun onHint(hoACT: FragmentActivity) {
                    SwapHintDialog().showIgnoreState(hoACT)
                }
            })
    }


    fun download() {
        if (!mModel.isDownloadSate) {
            var downloadUrl = mModel.mediaByBean.value?.videoUrl?.ifEmpty {
                mModel.mediaByBean.value?.imageUrl
            } ?: ""
            mModel.download(downloadUrl)
        }
    }

    /**
     * 检查是否拥有权限
     */
    private fun checkPermission() {
        val writePermission: Boolean = (PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ))
        if (writePermission) {
            val downloadFile = File(Constants.savePath, mModel.downName)
            FileUtil.fileSaveToPublic(downloadFile.path, mModel.pathName)
        } else {
            launcherPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.adapterTab, swapTabAdapter)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}