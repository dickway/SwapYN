package com.a.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.lifecycle.MutableLiveData
import com.a.BR
import com.a.R
import com.a.activity.ABrowsingActivity
import com.a.activity.ACollectActivity
import com.a.activity.AGenerateActivity
import com.a.activity.AMyFaceAllActivity
import com.a.activity.ASettingActivity
import com.a.activity.AVipActivity
import com.a.databinding.FragmentAmineBinding
import com.a.dialog.ALoginDialog
import com.a.viewmodel.AMineViewModel
import com.face.ui.FeedbackActivity
import com.face.ui.FeedbackHistoryActivity
import com.face.ui.SigninActivity
import com.face.ui.WebActivity
import com.face.util.GVM
import com.face.util.SPUtils
import com.face.ui.fragment.BaseBindingFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.openActivity

class AMeFragment : BaseBindingFragment<FragmentAmineBinding, AMineViewModel>(
    R.layout.fragment_amine,
    AMineViewModel::class.java
) {
    val refreshing = MutableLiveData(false)

    private val tabIconsPressed = arrayListOf(
        R.drawable.a_tab_me_work,
        R.drawable.a_tab_me_collect,
        R.drawable.a_tab_me_history
    )
    private var meFragments = mutableListOf<Fragment>()
    //tab 更新后自动滚到开始, 因为默认会自动滚到中间
    private val tabAdapter: FragmentStatePagerAdapter by lazy {
        object : FragmentStatePagerAdapter(
            childFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            override fun getCount() = meFragments.size

            override fun getItem(position: Int): Fragment {
                return meFragments[position]
            }

            override fun getPageTitle(position: Int): CharSequence {
                return ""
            }
        }
    }
    override fun init(savedInstanceState: Bundle?) {
//        bar {
//            light(true)
//        }
        GVM.INSTANT.userInfo.observe(this) {
            refreshUI()
        }
        //字体渐变
//        mBinding?.tvVipPro?.apply {
//            // 获取 Paint 对象
//            val shader = LinearGradient(
//                0f, 0f, 125.dp.toFloat(), 0f,
//                Color.parseColor("#FFDA47"),
//                Color.parseColor("#BCFF47"),
//                Shader.TileMode.CLAMP
//            )
//            paint.shader = shader
//            setLayerType(View.LAYER_TYPE_HARDWARE, null)
//        }
        initFragment()
    }


    fun refresh() {
        GVM.INSTANT.aIsMeRefresh.postValue(true)
        GVM.INSTANT.refreshUserInfo {
            refreshing.value = false
        }
    }

    private fun initFragment() {
        if (meFragments.isEmpty()) {
            meFragments.add(AWorkFragment())
            meFragments.add(AColletFragment())
            meFragments.add(AHistoryFragment())
        }

        mBinding?.apply {
            viewPager.adapter = tabAdapter
            viewPager.offscreenPageLimit = 3
            tabLayout.setupWithViewPager(viewPager)

            //将 TabLayout 和 ViewPager 绑定
            for (i in 0 until tabLayout.tabCount) {
                tabLayout.getTabAt(i)?.apply {
                    text = null
                    setIcon(tabIconsPressed[i])
                }
            }

        }
    }


    fun refreshUI() {
        mBinding?.apply {
            imgHead.loadImage(
                GVM.INSTANT.userInfo.value.avatarImg(),
                placeholderResId = R.mipmap.a_icon_avatar
            )
            tvAccountTime.visibility = View.VISIBLE
            tvAccountTime2.visibility = View.VISIBLE
            var vipTime = GVM.INSTANT.userInfo.value.vipTime()
            if (GVM.INSTANT.isVip.value) {//会员
                vipTime = "Expire date:$vipTime"
                imgHead.strokeWidth = 3.dp.toFloat()
            } else if (vipTime != "" && !GVM.INSTANT.isVip.value) {//会员到期
                vipTime = "Membership has expired"
                imgHead.strokeWidth = 0.dp.toFloat()
            } else if (vipTime == "") {//未充值会员
                imgHead.strokeWidth = 0.dp.toFloat()
                if (GVM.INSTANT.userInfo.value.expVip != 0L) {
                    vipTime = "Membership has expired"
                } else {
                    tvAccountTime.visibility = View.GONE
                    tvAccountTime2.visibility = View.GONE
                }
            }
//            tvAccountName.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                if (GVM.INSTANT.isVip.value) R.mipmap.a_icon_vip2 else 0,
//                0,
//                0,
//                0
//            )
//            tvAccountName2.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                0,
//                0,
//                if (GVM.INSTANT.isVip.value) R.mipmap.a_icon_vip2 else 0,
//                0
//            )

            tvAccountTime.text = vipTime
            tvAccountTime2.text = vipTime
            if (GVM.INSTANT.userInfo.value.loginType == "guest") {
                line1.visibility = View.GONE
                line2.visibility = View.VISIBLE
                txtLink.visibility = View.VISIBLE
                tvAccountName2.text = GVM.INSTANT.userInfo.value.guestName()
            } else if (GVM.INSTANT.userInfo.value.loginType == "app") {
                line1.visibility = View.VISIBLE
                line2.visibility = View.GONE
                txtLink.visibility = View.GONE
                tvAccountName.text = GVM.INSTANT.userInfo.value.name
            }else {
                line1.visibility = View.VISIBLE
                line2.visibility = View.GONE
                txtLink.visibility = View.GONE
                tvAccountName.text = GVM.INSTANT.userInfo.value.email
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    fun onVipClick() {
        GVM.INSTANT.payPage.value = "AMe"
        openActivity<AVipActivity>()
    }

    fun onIsVipClick() {
        if (GVM.INSTANT.isVip.value) {
            GVM.INSTANT.payPage.value = "AMe"
            openActivity<AVipActivity>()
        }
    }

    fun onCollectClick() {
        openActivity<ACollectActivity>()
    }

    fun onGenerateClick() {
        openActivity<AGenerateActivity>()
    }

    fun onBrowsing() {
        openActivity<ABrowsingActivity>()
    }

    fun onLoginClick() {
        ALoginDialog().showIgnoreState(mActivity)
    }

    fun onSettingClick() {
        openActivity<ASettingActivity>()
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


    fun onPoint() {
        openActivity<SigninActivity>()
    }

    fun onFaceAll() {
        openActivity<AMyFaceAllActivity>()
    }


    fun onPolicyClick() {
        WebActivity.openPrivacyPolicy(context, SPUtils.privacyPolicy, "Privacy Policy")
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}