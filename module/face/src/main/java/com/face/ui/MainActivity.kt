package com.face.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.apkfuns.logutils.LogUtils
import com.face.R
import com.face.ad.AdUtil
import com.face.bean.TagConfigBean.Companion.toMap
import com.face.bean.TaskBean
import com.face.bean.ToolTaskBean
import com.face.databinding.ActivityMainBinding
import com.face.key.AiTaskType
import com.face.ui.fragment.ExploreFragment
import com.face.ui.fragment.MeFragment
import com.face.ui.fragment.TemplateFragment
import com.face.ui.fragment.ToolFragment
import com.face.ui.share.ShareZoneFragment
import com.face.ui.tvoice.VoiceCompletionActivity
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.GooglePayUtil
import com.face.util.NotificationUtil
import com.face.util.SPUtils
import com.face.util.UpdateLiveTImeUtil
import com.face.view.ExitDialog
import com.face.view.ExitSettingDialog
import com.face.view.HomeNoticeDialog
import com.face.view.VersionDialog
import com.face.viewmodel.InitViewModel
import com.zzkj.structure.net.NetworkUtils
import com.zzkj.structure.util.AppManager
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.toIntOrZero
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.let
import kotlin.text.isNullOrBlank


class MainActivity : BaseBindingActivity<ActivityMainBinding, InitViewModel>(
    R.layout.activity_main,
    InitViewModel::class.java
) {

    private var fragments = mutableMapOf<Int, Fragment>()
    private var selectIndex = SPUtils.openHomeShow
    private lateinit var notificationLauncher: ActivityResultLauncher<String>

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun init(savedInstanceState: Bundle?) {
        SPUtils.openAppNum += 1

        GooglePayUtil.init()

        if (!GVM.INSTANT.isLogin()) {
            openActivity<LoginActivity>()
            finish()
            return
        }

        onBackPressedDispatcher.addCallback(this) {
            val activity = this@MainActivity

            if (selectIndex != SPUtils.openHomeShow && !mModel.isShowDownload.value) {
                mBinding?.bottomNavView?.selectedItemId =
                    mBinding?.bottomNavView?.menu
                        ?.getItem(SPUtils.openHomeShow)
                        ?.itemId ?: 0
                return@addCallback
            }

            if (SPUtils.exitApp && SPUtils.isShare && !mModel.isShowDownload.value) {
                ExitSettingDialog(
                    getString(R.string.homepage_content),
                    getString(R.string.save_exit),
                    onCloseApp = ::onCloseApp
                ).showIgnoreState(activity)
            } else {
                ExitDialog(::onCloseApp).showIgnoreState(activity)
            }
        }

        notificationLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                // 系统权限弹窗消失后才会回调到这里
                NotificationUtil.createChannel()
                mModel.isPermissionEnd.value = true
            }


        EventUtil.inPage("main_activity")
        //提前获取订阅价格
        mModel.vipSchemes()
        //提前获取商品价格
        mModel.getTFLOPConfigs()
        //记录当前订阅信息
        mModel.saveSetting()

        UpdateLiveTImeUtil.init()
        initView()
        restoreFragment()

        val restoredIndex = supportFragmentManager.primaryNavigationFragment
            ?.tag
            ?.removePrefix("main_")
            ?.toIntOrNull()

        selectIndex = when {
            // ② FragmentManager 恢复出来的可见 Fragment
            restoredIndex != null -> restoredIndex
            // ① 业务强制：首页
            !SPUtils.isShare || mModel.isShowDownload.value -> 0
            // ③ 兜底默认
            else -> selectIndex
        }

        setSelectIndex(selectIndex, true)
        initNotice()
        initDelay()

        //通知权限，防止同时弹系统和自定义弹窗
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            NotificationUtil.createChannel(this@MainActivity)
            mModel.isPermissionEnd.value = true
        } else {
            launch(Dispatchers.Main) {
                delay(500)
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        handleIntent(intent)
        mModel.taskBean.observe(this) {
            if (it?.id != "") {
                if (it?.taskType == AiTaskType.GEN_PERSONPIC) {//自定义风格4张
                    openActivity<CompletionFourActivity>() {
                        putParcelable("task_data", it)
                    }
                } else if (it?.taskType == AiTaskType.RMBG ||
                    it?.taskType == AiTaskType.IMG2CARTOON ||
                    it?.taskType == AiTaskType.OLD_PHONE ||
                    it?.taskType == AiTaskType.ID_CARD ||
                    it?.taskType == AiTaskType.CHANGE_AGE ||
                    it?.taskType == AiTaskType.DYNAMIC ||
                    it?.taskType == AiTaskType.TEXT2IMG ||
                    it?.taskType == AiTaskType.IMG2HUG ||
                    it?.taskType == AiTaskType.IMG2KISS ||
                    it?.taskType == AiTaskType.CHANGE_CLOTHES
                ) {
                    val toolTaskBean = ToolTaskBean()
                    toolTaskBean.type = it.taskType
                    toolTaskBean.recordUrl =
                        "${it.result.mData.firstOrNull()},${it.result.dataWater.firstOrNull()}"
                    openActivity<ToolCompletionActivity> {
                        putParcelable("task", toolTaskBean)
                    }
                } else if (it?.taskType == AiTaskType.VOICE_CLONING) {
                    val toolTaskBean = ToolTaskBean()
                    toolTaskBean.taskId = it.id
                    toolTaskBean.state = it.state
                    toolTaskBean.id = it.params.recordid
                    toolTaskBean.createTime = it.createTime
                    toolTaskBean.errorMessage = it.ee
                    toolTaskBean.name = getString(R.string.ai_sound)
                    openActivity<VoiceCompletionActivity> {
                        putParcelable("dataBean", toolTaskBean)
                    }
                } else {
                    openActivity<CompletionActivity>() {
                        putString("task_id", it?.id)
                        putInt("task_num", 0)
                    }
                }
                mModel.taskBean.postValue(TaskBean())
            }
        }

        GVM.INSTANT.isVisibleShare.observe(this, Observer {
            mBinding?.bottomNavView?.menu?.findItem(R.id.shareZone)?.isVisible = it
            if (mModel.isShowDownload.value) {//如果事渠道固定不显示
                mBinding?.bottomNavView?.menu?.findItem(R.id.shareZone)?.isVisible = false
            }
        })

        GVM.INSTANT.showMineRedDot.observe(this) {
            if (it) {
                mBinding?.bottomNavView?.getOrCreateBadge(R.id.me)?.let { badge ->
                    badge.isVisible = true
                    badge.backgroundColor = getColorX(R.color.colorBgFF3434)
                    badge.verticalOffset = 8
                    badge.horizontalOffset = 6
                }
            } else {
                mBinding?.bottomNavView?.getBadge(R.id.me)?.isVisible = false
            }
        }
    }

    private fun initView() {
        GVM.INSTANT.isShowTool.observe(this) {
            if (GVM.INSTANT.isPostAB.value == -1) {//进入上报一次同时刷新通知订阅
                GVM.INSTANT.isPostAB.value = 1
                EventUtil.startAB()
            }
            if (GVM.INSTANT.isEventA.value == -1) {//刷新渠道值如果和进入app时值不一样，重新打开app
                GVM.INSTANT.isEventA.value = it//刷新当前值
                val intent = Intent().setClassName(
                    App.INSTANCE.packageName,
                    SPUtils.mainAct
                )
                startActivity(intent)
                finish()
            }
        }
        mBinding?.apply {
            bottomNavView.apply {
                itemIconTintList = null
                setOnItemSelectedListener {
                    mModel.getDeviceRefresh()
                    mModel.refreshFeedback()
                    setSelectIndex(
                        when (it.itemId) {
                            R.id.template -> 1
                            R.id.tool -> 2
                            R.id.shareZone -> 3
                            R.id.me -> 4
                            else -> 0
                        }
                    )
                    true
                }
            }
        }
    }

    private fun restoreFragment() {
        supportFragmentManager.fragments.forEach {
            when (it.tag) {
                "main_0" -> fragments[0] = it
                "main_1" -> fragments[1] = it
                "main_2" -> fragments[2] = it
                "main_3" -> fragments[3] = it
                "main_4" -> fragments[4] = it
            }
        }
    }

    fun setShowIndex(index: Int) {
        mBinding?.bottomNavView?.selectedItemId =
            mBinding?.bottomNavView?.menu?.getItem(index)?.itemId ?: 0
    }

    private fun setSelectIndex(index: Int, force: Boolean = false) {
        if (selectIndex != index || force) {
            val needHide = supportFragmentManager.fragments.find {
                it.tag == "main_${selectIndex}"
            }
            var needShow = supportFragmentManager.fragments.find { it.tag == "main_$index" }
            val transaction = supportFragmentManager.beginTransaction()
            if (needShow == null) {
                transaction.add(
                    R.id.frameLayout,
                    getFragment(index).also { needShow = it },
                    "main_$index"
                )
            } else if (needShow != fragments[index]) {
                needShow.let { fragments[index] = it }
            }
            if (needHide !== needShow) {
                needHide?.let {
                    transaction
                        .setMaxLifecycle(it, Lifecycle.State.STARTED)
                        .hide(it)
                }
            }
            needShow?.let {
                transaction
                    .show(it)
                    .setMaxLifecycle(it, Lifecycle.State.RESUMED)
                    .setPrimaryNavigationFragment(it)
            }
            transaction.commit()
            selectIndex = index

            mBinding?.bottomNavView?.menu?.getItem(index)?.let {
                if (!it.isChecked) {
                    mBinding?.bottomNavView?.selectedItemId = it.itemId
                }
            }
        }
    }


    private fun getFragment(index: Int): Fragment {
        return fragments[index] ?: when (index) {
            1 -> TemplateFragment()
            2 -> ToolFragment()
            3 -> ShareZoneFragment()
            4 -> MeFragment()
            else -> ExploreFragment()
        }.also {
            fragments[index] = it
        }
    }

    /**
     * 跳转界面判断
     * */
    private fun handleIntent(intent: Intent?) {
        if (!GVM.INSTANT.isLogin()) {
            openActivity<LoginActivity>()
            finish()
            return
        }
        //刷新初始化缓存数据
        mModel.retryMian()
        intent?.extras?.apply {//通知点击跳转
            val msgType = getInt("msg_type", 0)
            val taskId = getString("task_id", "")
            val mediaId = getString("media_id", "")
            val feedbackId = getString("feedback_id", "")
            when (msgType) {
                100 -> {//MSG_TYPE_NEW_TASK
                    mModel.sendAiTask(taskId)
                }

                101 -> {//MSG_TYPE_HOT
                    SwapFaceNewActivity.jump(this@MainActivity, mediaId)
                }

                201 -> { //MSG_TYPE_POINT
                    openActivity<SigninActivity>()
                }

                301 -> { //MSG_TYPE_FEEDBACK
                    openActivity<FeedbackMsgActivity> {
                        putInt("feedbackId", feedbackId.toIntOrZero())
                    }
                }
            }

        }
        intent?.extras?.clear()
    }

    override fun onResume() {
        super.onResume()
        mModel.refreshFeedback()
        mModel.saveSetting()
    }


    private fun initDelay() {
        launch {
            delay(500)
            GVM.INSTANT.networkLiveData.observe(this@MainActivity) {
                if (it) {
                    VersionDialog.checkUpdate(this@MainActivity)
                }
            }
        }
    }

    private fun initNotice() {
        val map = SPUtils.noticeList.takeUnless { it.isEmpty() }?.toMap()
        val showPage = map?.get("show_page") ?: ""
        if (!SPUtils.noticeLook&&showPage.contains("b")) {
            val title = map?.get("title") ?: ""
            val content = map?.get("content") ?: ""
            val confirmText = map?.get("confirm_text") ?: ""
            HomeNoticeDialog(title, content, confirmText).showIgnoreState(this)
        }
    }


//    override fun onBackPressed() {
//        if (selectIndex == 0) {
//            ExitDialog(::onCloseApp).showIgnoreState(this)
//        } else {
//            mBinding?.bottomNavView?.selectedItemId =
//                mBinding?.bottomNavView?.menu?.getItem(0)?.itemId ?: 0
//        }
//    }

    private fun onCloseApp() {
        UpdateLiveTImeUtil.destroy()
        GooglePayUtil.destroy()
        AdUtil.destroyAllAd()
        launch {
            delay(500)
            AppManager.exitApp()
        }
    }
}