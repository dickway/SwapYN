package com.face.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.face.util.GVM
import com.face.view.LogoutDialog
import com.face.viewmodel.activity.SettingViewModel
import com.face.BR
import com.face.R
import com.face.databinding.ActivitySettingsBinding
import com.face.ui.accountlogin.UpdateAccountActivity
import com.face.util.EventUtil
import com.face.util.SPUtils
import com.face.view.ExitSettingDialog
import com.face.view.LanguageDialog
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick


class SettingActivity : BaseBindingActivity<ActivitySettingsBinding, SettingViewModel>(
    R.layout.activity_settings,
    SettingViewModel::class.java
) {


    override fun init(savedInstanceState: Bundle?) {
        mBinding?.apply {
            if (GVM.INSTANT.userInfo.value.loginType!="app"){
                mBinding?.linUpdate?.visibility= View.GONE
            }
            val isShare: Boolean = SPUtils.isShare
            switchView.isChecked = !isShare
            switchView.singleClick {
                if (SPUtils.isShare) {
                    SPUtils.openHomeShow = 0
                    SPUtils.isShare = false
                    GVM.INSTANT.isVisibleShare.value = false
                } else {
                    SPUtils.isShare = true
                    GVM.INSTANT.isVisibleShare.value = true
                }
                showTxt()
            }

            switchHdView.isChecked = SPUtils.isHdFace
            switchHdView.singleClick {
                SPUtils.isHdFace = !SPUtils.isHdFace
            }
            showTxt()

            lineOpen.singleClick {
                ExitSettingDialog(
                    getString(R.string.default_home_after_launch),
                    getString(R.string.save),
                    {
                        showTxt()
                    }
                ).showIgnoreState(this@SettingActivity)
            }

            val languageStr: String = SPbaseUtils.spLanguage
            when (languageStr.lowercase()) {
                "en" -> tvLanguage.text = getString(R.string.language_en)
                "zh" -> tvLanguage.text = getString(R.string.language_zh)
                "es" -> tvLanguage.text = getString(R.string.language_es)
                "fr" -> tvLanguage.text = getString(R.string.language_fr)
                "sv" -> tvLanguage.text = getString(R.string.language_sv)
                "de" -> tvLanguage.text = getString(R.string.language_de)
                "ar" -> tvLanguage.text = getString(R.string.language_ar)
                "ko" -> tvLanguage.text = getString(R.string.language_ko)
                "ja" -> tvLanguage.text = getString(R.string.language_ja)
                "ku" -> tvLanguage.text = getString(R.string.language_ku)
                "iw" -> tvLanguage.text = getString(R.string.language_iw)
                "fa" -> tvLanguage.text = getString(R.string.language_fa)
                "tr" -> tvLanguage.text = getString(R.string.language_tr)
                "pt-br" -> tvLanguage.text = getString(R.string.language_ptbr)
                "pt-pt" -> tvLanguage.text = getString(R.string.language_pt)
                "zh-tw" -> tvLanguage.text = getString(R.string.language_zhtw)
                "it" -> tvLanguage.text = getString(R.string.language_it)
                else -> tvLanguage.text = getString(R.string.language_en)
            }
            lineLanguage.singleClick {
                LanguageDialog {
                    languageListener(it)
                }.showIgnoreState(this@SettingActivity)
            }
            linDelete.singleClick {
                openActivity<DeleteUserActivity>()
            }
        }
    }

    private fun languageListener(selectedNum: Int) {
        if (selectedNum == 2) {
            SPbaseUtils.spLanguage = "zh"
            EventUtil.languageChange("zh")
        } else if (selectedNum == 1) {
            SPbaseUtils.spLanguage = "en"
            EventUtil.languageChange("en")
        } else if (selectedNum == 3) {
            EventUtil.languageChange("es")
            SPbaseUtils.spLanguage = "es"
        } else if (selectedNum == 4) {
            EventUtil.languageChange("de")
            SPbaseUtils.spLanguage = "de"
        } else if (selectedNum == 5) {
            EventUtil.languageChange("fr")
            SPbaseUtils.spLanguage = "fr"
        } else if (selectedNum == 6) {
            EventUtil.languageChange("sv")
            SPbaseUtils.spLanguage = "sv"
        } else if (selectedNum == 7) {
            EventUtil.languageChange("ar")
            SPbaseUtils.spLanguage = "ar"
        } else if (selectedNum == 8) {
            EventUtil.languageChange("ko")
            SPbaseUtils.spLanguage = "ko"
        } else if (selectedNum == 9) {
            EventUtil.languageChange("ja")
            SPbaseUtils.spLanguage = "ja"
        } else if (selectedNum == 10) {
            EventUtil.languageChange("ku")
            SPbaseUtils.spLanguage = "ku"
        } else if (selectedNum == 11) {
            EventUtil.languageChange("iw")
            SPbaseUtils.spLanguage = "iw"
        } else if (selectedNum == 12) {
            EventUtil.languageChange("fa")
            SPbaseUtils.spLanguage = "fa"
        } else if (selectedNum == 13) {
            EventUtil.languageChange("tr")
            SPbaseUtils.spLanguage = "tr"
        } else if (selectedNum == 14) {
            EventUtil.languageChange("pt-br")
            SPbaseUtils.spLanguage = "pt-br"
        } else if (selectedNum == 15) {
            EventUtil.languageChange("pt-pt")
            SPbaseUtils.spLanguage = "pt-pt"
        } else if (selectedNum == 16) {
            EventUtil.languageChange("zh-tw")
            SPbaseUtils.spLanguage = "zh-tw"
        } else if (selectedNum == 17) {
            EventUtil.languageChange("it")
            SPbaseUtils.spLanguage = "it"
        }
        SPbaseUtils.spCacheLanguage = SPbaseUtils.spLanguage
        // 重启app
        val intent = Intent().setClassName(
            this@SettingActivity,
            SPUtils.mainAct
        )
        intent.putExtra("isSetting", true)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    fun onLogout() {
        SPUtils.tokenUser = ""
        LogoutDialog().showIgnoreState(this)
    }

    fun showTxt(){
        mBinding?.apply {
            when (SPUtils.openHomeShow) {
                0 -> tvOpen.text = getString(R.string.mian_explore)
                1 -> tvOpen.text = getString(R.string.mian_template)
                2 -> tvOpen.text = getString(R.string.frament_tool_title)
                3 -> tvOpen.text = getString(R.string.fragment_tool_share)
                4 -> tvOpen.text = getString(R.string.frament_me_title)
            }
        }
    }

    fun onUpdateClick() {
        UpdateAccountActivity.jump(this)
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
            .addArgument(BR.vm, mModel)
            .addArgument(BR.gvm, GVM.INSTANT)
    }
}