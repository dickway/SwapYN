package com.a.fragment

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.openActivity

class AMeFragment : BaseBindingFragment<FragmentAmineBinding, AMineViewModel>(
    R.layout.fragment_amine,
    AMineViewModel::class.java
) {
    val refreshing = MutableLiveData(false)

    private val tabIcons = listOf(
        R.drawable.a_ic_me_works,
        R.drawable.a_ic_me_saved,
        R.drawable.a_ic_me_history
    )
    private val tabTitles = listOf(
        R.string.a_me_works,
        R.string.a_me_saved,
        R.string.a_me_history
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
        GVM.INSTANT.userInfo.observe(viewLifecycleOwner) {
            refreshUI()
        }
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

            for (i in 0 until tabLayout.tabCount) {
                tabLayout.getTabAt(i)?.apply {
                    text = getString(tabTitles[i])
                    contentDescription = text
                    setIcon(tabIcons[i])
                    setCustomView(R.layout.item_a_me_tab)
                    updateTabStyle(this, isSelected)
                }
            }
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) = updateTabStyle(tab, true)
                override fun onTabUnselected(tab: TabLayout.Tab) = updateTabStyle(tab, false)
                override fun onTabReselected(tab: TabLayout.Tab) = Unit
            })
        }
    }

    private fun updateTabStyle(tab: TabLayout.Tab, selected: Boolean) {
        val icon = when {
            selected && tab.position == 0 -> R.drawable.a_ic_me_works_selected
            selected && tab.position == 1 -> R.drawable.a_ic_me_saved_selected
            selected && tab.position == 2 -> R.drawable.a_ic_me_history_selected
            else -> tabIcons[tab.position]
        }
        tab.setIcon(icon)
        tab.customView?.apply {
            isSelected = selected
            // Keep the selected clock's dark hands instead of tinting the entire icon white.
            findViewById<ImageView>(android.R.id.icon)?.imageTintList = if (selected) {
                null
            } else {
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.a_me_tab_inactive))
            }
            findViewById<TextView>(android.R.id.text1)?.setTypeface(
                null, if (selected) Typeface.BOLD else Typeface.NORMAL
            )
        }
    }

    fun refreshUI() {
        mBinding?.apply {
            val user = GVM.INSTANT.userInfo.value
            val isGuest = user.loginType == "guest"
            val isVip = GVM.INSTANT.isVip.value

            imgHead.loadImage(user.avatar, placeholderResId = R.drawable.a_ic_me_avatar)
            imgHead.strokeWidth = if (isVip) 3.dp.toFloat() else 0f

            val accountId = if (isGuest) {
                user.name.ifBlank { getString(R.string.a_me_guest_id, user.userId) }
            } else {
                user.userId.toString()
            }
            tvAccountName.text = when {
                isGuest -> getString(R.string.a_me_link_google)
                user.loginType == "app" -> user.name.ifBlank { user.email }.ifBlank { user.username }
                else -> user.email.ifBlank { user.name }.ifBlank { user.username }
            }.ifBlank { accountId }
            val vipColor = ContextCompat.getColor(requireContext(), R.color.a_me_vip_gold)
            val crown = if (isVip) {
                ContextCompat.getDrawable(requireContext(), R.drawable.a_ic_me_crown)?.apply {
                    setBounds(0, 0, 19.dp, 14.dp)
                }
            } else {
                null
            }
            tvAccountName.setTextColor(
                if (isVip && !isGuest) vipColor else ContextCompat.getColor(requireContext(), R.color.colorAwhite)
            )
            tvAccountName.setCompoundDrawablesRelative(if (isGuest) null else crown, null, null, null)

            tvAccountId.text = getString(R.string.a_me_account_id, accountId)
            tvAccountId.visibility = if (isGuest) View.VISIBLE else View.GONE
            tvAccountId.setTextColor(
                if (isVip) vipColor else ContextCompat.getColor(requireContext(), R.color.a_me_secondary_text)
            )
            val idArrow = if (isVip) null else {
                ContextCompat.getDrawable(requireContext(), R.drawable.a_ic_me_chevron)?.apply {
                    setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                }
            }
            tvAccountId.setCompoundDrawablesRelative(if (isGuest) crown else null, null, idArrow, null)

            val membershipText = when {
                isVip -> getString(
                    R.string.a_me_expire_date,
                    TimeUtil.getFormatDate(user.expireMillis, "MM/dd/yyyy")
                )
                user.expireMillis > 0 || user.expVip != 0L ->
                    getString(R.string.a_me_membership_expired)
                !isGuest -> getString(R.string.a_me_get_more_benefits)
                else -> ""
            }
            tvAccountTime.text = membershipText
            tvAccountTime.visibility = if (membershipText.isEmpty()) View.GONE else View.VISIBLE

            // Use the same first plan that the VIP page selects by default.
            val scheme = SPUtils.vipSchemes.firstOrNull()
            val price = scheme?.formattedPrice?.takeIf { it.isNotBlank() }
                ?: scheme?.takeIf { it.showPrice.isNotBlank() && it.showUnit.isNotBlank() }
                    ?.let { it.showUnit + it.showPrice }
            tvVipPrice.text = if (price != null) {
                getString(R.string.a_me_unlock_price, price)
            } else {
                getString(R.string.a_me_unlock_premium)
            }
        }
    }

    fun onAccountClick() {
        if (GVM.INSTANT.userInfo.value.loginType == "guest") {
            onLoginClick()
        } else {
            onVipClick()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshUI()
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
