package com.face.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.base.IUiView
import com.zzkj.structure.base.ViewEffect
import com.zzkj.structure.ui.dialog.StatusDialog
import com.zzkj.structure.util.LanguageUtil
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.ktx.BarHelper.bar
import com.face.util.GVM
import com.face.util.SPUtils

abstract class BaseBindingActivity<B : ViewDataBinding, M : BaseViewModel>(
    @LayoutRes val layoutResId: Int,
    private val viewModelType: Class<M>
) : AppCompatActivity(), IUiView {

    val TAG = javaClass.simpleName
    var mActivity: BaseBindingActivity<B, M>? = null
    var mBinding: B? = null
    open val mModel: M by lazy { getViewModel(viewModelType) }
    open var isRestartApp: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (GVM.INSTANT.isShowTool.value == null && isRestartApp) {
            val intent = Intent().setClassName(
                packageName,
                SPUtils.mainAct
            )
            startActivity(intent)
            finish()
            return
        }

        if (SPbaseUtils.screenshot == "1" && SPbaseUtils.versionRestriction && SPbaseUtils.loadAB == 1) {
            //禁止app录屏和截屏
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        bar { transparent() }
        mActivity = this
        initBinding()
        init(savedInstanceState)
    }

    private fun initBinding() {
        val binding: B = DataBindingUtil.setContentView(this, layoutResId)
        binding.lifecycleOwner = this
        getDataBindingArguments()?.map?.forEach {
            binding.setVariable(it.key, it.value)
        }
        mBinding = binding
    }

    abstract fun init(savedInstanceState: Bundle?)

    open fun getDataBindingArguments(): DataBindingArguments? {
        return null
    }

    fun <T : ViewModel> getViewModel(clazz: Class<T>): T {
        val model = ViewModelProvider(this)[clazz]
        if (model is BaseViewModel) {
            initViewEffect(model)
        }
        return model
    }

    private fun initViewEffect(model: BaseViewModel) {
        model.viewEffectLiveData.observe(this, Observer {
            when (it) {
                is ViewEffect.ShowLoading -> showLoading(it.msg, it.cancelable)
                is ViewEffect.HideLoading -> dismissLoading()
                else -> handleCommonEffect(it)
            }
            if (it != null) {
                model.viewEffectLiveData.value = null
            }
        })
    }

    open fun handleCommonEffect(effect: ViewEffect?) {
    }

    override fun onDestroy() {
        super.onDestroy()
        mActivity = null
        mBinding = mBinding?.let {
            it.unbind()
            null
        }
    }

    private var loadingDialog: StatusDialog? = null

    override fun showLoading(msg: String?, cancelable: Boolean) {
        if (mActivity == null) return
        if (loadingDialog == null) {
            loadingDialog = StatusDialog(mActivity!!)
                .setLoading()
        }
        msg?.let {
            loadingDialog?.setText(msg)
        }
        loadingDialog?.setCancelable(cancelable)
        loadingDialog?.show()
    }

    override fun attachBaseContext(newBase: Context) {
        LanguageUtil.checkLanguage(newBase)
        super.attachBaseContext(newBase)
    }

    override fun dismissLoading() {
        loadingDialog?.takeIf { it.isShowing }?.dismiss()
    }

    override fun isShowLoading() = loadingDialog?.isShowing ?: false
}