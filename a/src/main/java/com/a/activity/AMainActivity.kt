package com.a.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import com.a.BR
import com.a.R
import com.a.databinding.ActivityAmainBinding
import com.a.dialog.AExitDialog
import com.a.dialog.ANoticeDialog
import com.a.fragment.AExploreFragment
import com.a.fragment.AMeFragment
import com.a.fragment.APictureFragment
import com.a.viewmodel.AMainViewModel
import com.face.ad.AdUtil
import com.face.bean.TagConfigBean.Companion.toMap
import com.face.bean.TaskBean
import com.face.key.AiTaskType
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.GooglePayUtil
import com.face.ui.MainActivity
import com.face.util.FirebaseMessageUtil
import com.face.util.NotificationUtil
import com.face.util.SPUtils
import com.face.util.UpdateLiveTImeUtil
import com.face.view.VersionDialog
import com.face.ui.BaseBindingActivity
import com.face.ui.FeedbackMsgActivity
import com.face.ui.SigninActivity
import com.face.view.HomeNoticeDialog
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.AppManager
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick
import com.zzkj.structure.util.ktx.toIntOrZero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.text.contains


class AMainActivity : BaseBindingActivity<ActivityAmainBinding, AMainViewModel>(
    R.layout.activity_amain,
    AMainViewModel::class.java
) {

    private var fragments = mutableMapOf<Int, Fragment>()


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putInt("index", mModel.selectIndex.value ?: 0)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val index = savedInstanceState.getInt("index", -1)
        if (index != -1) setSelectIndex(index)
    }

    override fun init(savedInstanceState: Bundle?) {
        if (!GVM.INSTANT.isLogin()) {
            openActivity<ALoginActivity>()
            finish()
            return
        }
        onBackPressedDispatcher.addCallback(this) {
            if (mModel.selectIndex.value == 0) {
                AExitDialog(::onCloseApp).showIgnoreState(this@AMainActivity)
            } else {
                setSelectIndex(0)
            }
        }
        //A固定为英语
        SPbaseUtils.spLanguage = "en"

        SPUtils.openAppNum += 1

        FirebaseMessageUtil.subscribeApp()

        EventUtil.startAB()

        GooglePayUtil.init()
        //创建通知,延迟防止ab同时显示
        GlobalScope.launch(Dispatchers.Main) {
            delay(1000)
            NotificationUtil.createChannel(this@AMainActivity)
        }

        //刷新初始化缓存数据
        mModel.retryRefresh()
        //提前获取订阅价格
        mModel.vipSchemes()
        //记录当前订阅信息
        mModel.saveSetting()

        UpdateLiveTImeUtil.init()
        initView()
        initNotice()
        initFragment()
        initDelay()
        handleIntent(intent)
        mModel.taskBean.observe(this) {
            if (it?.id != "") {
                if (it?.taskType == AiTaskType.FACESWAP) {//自定义风格4张
                    openActivity<ACompletionActivity>() {
                        putString("task_id", it.id)
                    }
                }else if(it?.taskType == AiTaskType.ID_CARD){
                    openActivity<AToolTaskHistoryActivity>()
                }
                mModel.taskBean.postValue(TaskBean())
            }
        }

//        GVM.INSTANT.hasNewFeedbackMsg.observe(this) {
//            if (it) {
//                mBinding?.bottomNavView?.getOrCreateBadge(R.id.me)?.let { badge ->
//                    badge.isVisible = true
//                    badge.backgroundColor = getColorX(R.color.colorBgFF3434)
//                    badge.verticalOffset = 8
//                    badge.horizontalOffset = 6
//                }
//            } else {
//                mBinding?.bottomNavView?.getBadge(R.id.me)?.isVisible = false
//            }
//        }
    }

    private fun initView() {
        GVM.INSTANT.aIsAB.observe(this) {
            SPbaseUtils.loadAB = it
            if (it == 1) {
                openActivity<MainActivity>()
                finish()
            }
        }
        GVM.INSTANT.aSelectIndex.observe(this) {
            if (it == 0) {
                setSelectIndex(0)
                GVM.INSTANT.aSelectIndex.value=-1
            }
        }


        mBinding?.apply {
            exploreView.singleClick {
                setSelectIndex(0)
            }
            pictureView.singleClick {
                setSelectIndex(1)
            }
            meView.singleClick {
                setSelectIndex(2)
            }
//            bottomNavView.apply {
//                itemIconTintList = null
//                setOnItemSelectedListener {
//                    mModel.getDeviceRefresh()
//                    setSelectIndex(
//                        when (it.itemId) {
//                            R.id.picture -> 1
//                            R.id.me -> 2
//                            else -> 0
//                        }
//                    )
//                    true
//                }
//            }
        }
    }

    private fun initFragment() {
        supportFragmentManager.fragments.forEach {
            when (it.tag) {
                "main_0" -> fragments[0] = it
                "main_1" -> fragments[1] = it
                "main_2" -> fragments[2] = it
            }
        }
        if (fragments[0] == null) fragments[0] = AExploreFragment()
        if (fragments[1] == null) fragments[1] = APictureFragment()
        if (fragments[2] == null) fragments[2] = AMeFragment()

        fragments.entries.forEach { entry ->
            val (index, fragment) = entry
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            if (!fragment.isAdded) {
                transaction.add(R.id.frameLayout, fragment, "main_$index")
            }
            if (mModel.selectIndex.value == index) {
                transaction
                    .setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
                    .show(fragment)
            } else {
                transaction
                    .setMaxLifecycle(fragment, Lifecycle.State.STARTED)
                    .hide(fragment)
            }
            transaction.commit()
        }
    }

    fun setSelectIndex(index: Int) {
        mModel.getDeviceRefresh()
        mModel.refreshFeedback()
        if (mModel.selectIndex.value != index) {
            fragments.forEach { entry ->
                val (i, fragment) = entry
                if (index == i) {
                    supportFragmentManager.beginTransaction()
                        .setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
                        .show(fragment)
                        .commit()
                    mModel.selectIndex.value = index
                } else {
                    supportFragmentManager.beginTransaction()
                        .setMaxLifecycle(fragment, Lifecycle.State.STARTED)
                        .hide(fragment)
                        .commit()
                }
            }
//            mBinding?.bottomNavView?.selectedItemId =
//                mBinding?.bottomNavView?.menu?.getItem(index)?.itemId ?: 0
        }
    }

    /**
     * 跳转界面判断
     * */
    private fun handleIntent(intent: Intent?) {
        if (!GVM.INSTANT.isLogin()) {
            openActivity<ALoginActivity>()
            return
        }
        intent?.extras?.apply {//通知点击跳转
            val msgType = getInt("msg_type", 0)
            val taskId = getString("task_id", "")
            val mediaId = getString("media_id", "")
            val feedbackId = getString("feedback_id", "")
            when (msgType) {
                100 -> {//MSG_TYPE_NEW_TASK
                    mModel.sendAiTask(taskId)
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
        mModel.getDeviceRefresh()
        mModel.saveSetting()
    }


    private fun initDelay() {

        if (!SPUtils.noticeShow && SPUtils.noticeContent.trim() != "1") {
            ANoticeDialog(onBtnCancel = {
                onCloseApp()
            }).showIgnoreState(this@AMainActivity)
        }

        launch {
            delay(500)
            GVM.INSTANT.networkLiveData.observe(this@AMainActivity) {
                if (it) {
                    VersionDialog.checkUpdate(this@AMainActivity)
                }
            }
        }
    }

//    override fun onBackPressed() {
//        if (mModel.selectIndex.value == 0) {
//            AExitDialog(::onCloseApp).showIgnoreState(this)
//        } else {
//            setSelectIndex(0)
//        }
//    }

    private fun initNotice() {
        val map = SPUtils.noticeList.takeUnless { it.isEmpty() }?.toMap()
        val showPage = map?.get("show_page") ?: ""
        if (!SPUtils.noticeLook&&showPage.contains("a")) {
            val title = map?.get("title") ?: ""
            val content = map?.get("content") ?: ""
            val confirmText = map?.get("confirm_text") ?: ""
            HomeNoticeDialog(title, content, confirmText).showIgnoreState(this)
        }
    }


    private fun onCloseApp() {
        UpdateLiveTImeUtil.destroy()
        GooglePayUtil.destroy()
        AdUtil.destroyAllAd()
        AppManager.exitApp()
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}