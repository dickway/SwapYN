package com.face.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.face.ui.App
import com.face.util.GVM
import com.face.BR
import com.face.R
import com.face.databinding.FragmentMineBinding
import com.face.ui.BrowsingActivity
import com.face.ui.CollectActivity
import com.face.ui.FeedbackActivity
import com.face.ui.FeedbackHistoryActivity
import com.face.ui.HistoryActivity
import com.face.ui.MyFaceAllActivity
import com.face.ui.SettingActivity
import com.face.ui.ShareActivity
import com.face.ui.SigninActivity
import com.face.ui.VipActivity
import com.face.ui.WebActivity
import com.face.util.SPUtils
import com.face.view.GroupDialog
import com.face.view.LoginDialog
import com.face.viewmodel.fragment.MineViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.longToast

class MeFragment : BaseBindingFragment<FragmentMineBinding, MineViewModel>(
    R.layout.fragment_mine,
    MineViewModel::class.java
) {
    val refreshing = MutableLiveData(false)


    override fun init(savedInstanceState: Bundle?) {
        GVM.INSTANT.userInfo.observe(this) {
            if (it.isLogin()) refreshUI()
        }

        mModel.historyList.observe(this) {
            if (it.isNotEmpty()) {
                mBinding?.recyclerView?.scrollToPosition(0)
            }
        }
    }


    fun refresh() {
        if (GVM.INSTANT.isLogin()) {
            mModel.sendAiTask()
            GVM.INSTANT.refreshUserInfo {
                refreshing.value = false
            }
        } else {
            refreshing.value = false
            mBinding?.apply {
                tvAccountName.text = getString(R.string.log_ac)
                imgHead.setImageResource(com.key.R.drawable.img_icon_login)
                imgHead.strokeWidth = 0.dp.toFloat()
                tvAccountTime.visibility = View.GONE
                meVipPro.visibility = View.GONE
                mModel.historyList.value = listOf()
            }
        }
    }

    fun refreshUI() {
        mBinding?.apply {
            mModel.sendAiTask()
            imgHead.loadImage(
                GVM.INSTANT.userInfo.value.avatarImg(),
                placeholderResId = com.key.R.drawable.img_icon_login
            )
            imgHead.strokeWidth = 2.dp.toFloat()
            tvAccountTime.visibility = View.VISIBLE
            meVipPro.visibility = View.VISIBLE
            var vipTime = GVM.INSTANT.userInfo.value.vipTime()
            if (vipTime != "" && vipTime.split("/")[0].toInt() > 2039) {//永久会员
                vipTime = getString(R.string.vip_time)
            } else if (GVM.INSTANT.isVip.value) {//会员
                vipTime = getString(R.string.vip_time2) + vipTime
            } else if (vipTime != "" && !GVM.INSTANT.isVip.value) {//会员到期
                vipTime = getString(R.string.vip_time1)
                imgHead.strokeWidth = 0.dp.toFloat()
            } else if (vipTime == "") {//未充值会员
                if (GVM.INSTANT.userInfo.value.expVip != 0L) {
                    vipTime = getString(R.string.vip_time1)
                    imgHead.strokeWidth = 0.dp.toFloat()
                } else {
                    tvAccountTime.visibility = View.GONE
                    meVipPro.visibility = View.GONE
                    imgHead.strokeWidth = 0.dp.toFloat()
                }
            }
            tvAccountTime.text = vipTime
            if (GVM.INSTANT.userInfo.value.loginType == "guest") {
                txtLink.visibility = View.VISIBLE
                tvAccountName.text = GVM.INSTANT.userInfo.value.guestName()
            }else if (GVM.INSTANT.userInfo.value.loginType == "app") {
                txtLink.visibility = View.GONE
                tvAccountName.text = GVM.INSTANT.userInfo.value.name
            } else {
                txtLink.visibility = View.GONE
                tvAccountName.text = GVM.INSTANT.userInfo.value.email
            }
            tvFlops.text = GVM.INSTANT.userInfo.value.tflops.toString()

            mBinding?.tvShare?.text = if (GVM.INSTANT.isShowTool.value != 0) {
                getString(R.string.mine_my_share)
            } else {
                getString(R.string.mine_my_share2)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
        mModel.onUserAds()
    }

    fun onVipClick() {
        GVM.INSTANT.payPage.value = "Home_me"
        VipActivity.jump(requireActivity())
    }

    fun onPoint() {
        openActivity<SigninActivity>()
    }

    fun onBrowsing() {
        openActivity<BrowsingActivity>()
    }


    fun onIsVipClick() {
        if (GVM.INSTANT.isVip.value) {
            GVM.INSTANT.payPage.value = "Home_me"
            VipActivity.jump(requireActivity())
        }
    }

    fun onLoginClick() {
        LoginDialog().showIgnoreState(mActivity)
    }

    fun onCollectClick() {
        if (GVM.INSTANT.checkingLogin(requireActivity())) openActivity<CollectActivity>()
    }

    fun onMyFaceClick() {
        if (GVM.INSTANT.checkingLogin(requireActivity())) openActivity<MyFaceAllActivity>()
    }

    fun onMyShareClick() {
        if (GVM.INSTANT.checkingLogin(requireActivity())) openActivity<ShareActivity>()
    }

    fun onHistoryClick() {
        if (GVM.INSTANT.checkingLogin(requireActivity())) openActivity<HistoryActivity>()
    }

    fun onSettingClick() {
        openActivity<SettingActivity>()
    }

    fun onFeedbackClick() {
        if (GVM.INSTANT.hasNewFeedbackMsg.value) {
            openActivity<FeedbackHistoryActivity> {
                putBoolean("isList", true)
            }
        } else {
            openActivity<FeedbackActivity>()
        }
    }

    fun onGroup() {
        GVM.INSTANT.payPage.value = "Home_me_group"
        if (!GVM.INSTANT.isVip.value) {
            VipActivity.jump(requireActivity())
            return
        }
        GroupDialog(
            onWhatapp = {
                openThree(SPUtils.txtWhatapp, "com.whatsapp")
            },
            onTelegram = {
                openThree(SPUtils.txtTelegram, "org.telegram.messenger")
            },
            onDiscord = {
                openThree(SPUtils.txtDiscord, "com.discord")
            }
        ).showIgnoreState(activity)
    }

    fun onTelegram() {
        openThree(SPUtils.telegramGroup, "org.telegram.messenger")
    }

    fun onPolicyClick() {
        WebActivity.openPrivacyPolicy(context, SPUtils.privacyPolicy, "Privacy Policy")
    }

    fun openThree(txtLine: String, txtPackage: String) {
        txtLine.takeIf { it.isNotBlank() }?.let {
            var intent = Intent(Intent.ACTION_VIEW, it.toUri()).apply {
                setPackage(txtPackage)
            }
            if (intent.resolveActivity(App.INSTANCE.packageManager) != null) {
                startActivity(intent)
            } else {
                intent = Intent(Intent.ACTION_VIEW, it.toUri())
                try {
                    startActivity(intent)
                } catch (t: Throwable) {
                    longToast(getString(R.string.telegram_group_info))
                }
            }
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}