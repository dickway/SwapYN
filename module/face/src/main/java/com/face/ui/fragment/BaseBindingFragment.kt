package com.face.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.apkfuns.logutils.LogUtils
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.base.IUiView
import com.zzkj.structure.base.ViewEffect
import com.zzkj.structure.ui.dialog.StatusDialog

abstract class BaseBindingFragment<B : ViewDataBinding, M : BaseViewModel>(
    @LayoutRes val layoutResId: Int,
    private val viewModelType: Class<M>
) : Fragment(), IUiView {

    val TAG: String = javaClass.simpleName
    var mActivity: com.face.ui.BaseBindingActivity<*, *>? = null
    var mBinding: B? = null
    open val mModel: M by lazy { getViewModel(viewModelType) }

    companion object {
        const val TAG_LIFECYCLE = "FragmentLifecycle"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (enablePrintLifecycle()) {
            LogUtils.tag(TAG_LIFECYCLE).d("$TAG onAttach")
        }
        mActivity = context as? com.face.ui.BaseBindingActivity<*, *>
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (enablePrintLifecycle()) {
            LogUtils.tag(TAG_LIFECYCLE).d("$TAG onCreateView")
        }
        val binding: B = DataBindingUtil.inflate(
            inflater, layoutResId,
            container, false, getDataBindingComponent()
        )
        binding.lifecycleOwner = viewLifecycleOwner
        getDataBindingArguments()?.map?.forEach {
            binding.setVariable(it.key, it.value)
        }
        mBinding = binding
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (enablePrintLifecycle()) {
            LogUtils.tag(TAG_LIFECYCLE).d("$TAG onViewCreated")
        }
        init(savedInstanceState)
    }

    protected abstract fun init(savedInstanceState: Bundle?)

    open fun getDataBindingArguments(): DataBindingArguments? {
        return null
    }

    protected open fun getDataBindingComponent(): DataBindingComponent? {
        return DataBindingUtil.getDefaultComponent()
    }

    fun <T : ViewModel> getViewModel(clazz: Class<T>, owner: ViewModelStoreOwner = this): T {
        val model = ViewModelProvider(owner)[clazz]
        if (model is BaseViewModel) {
            initViewEffect(model)
        }
        return model
    }

    private fun initViewEffect(model: BaseViewModel) {
        lifecycleScope.launchWhenResumed {
            model.viewEffectLiveData.observe(viewLifecycleOwner, Observer {
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
    }

    open fun handleCommonEffect(effect: ViewEffect?) {
    }

    open fun navigateUp() {
        try {
            findNavController().navigateUp()
        } catch (e: Throwable) {
            LogUtils.e(e)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (enablePrintLifecycle()) {
            LogUtils.tag(TAG_LIFECYCLE).d("$TAG onActivityCreated")
        }
    }

    override fun onStart() {
        super.onStart()
        if (enablePrintLifecycle()) {
            LogUtils.tag(TAG_LIFECYCLE).d("$TAG onStart")
        }
    }

    override fun onResume() {
        super.onResume()
        if (enablePrintLifecycle()) {
            LogUtils.tag(TAG_LIFECYCLE).d("$TAG onResume")
        }
    }

    override fun onPause() {
        super.onPause()
        if (enablePrintLifecycle()) {
            LogUtils.tag(TAG_LIFECYCLE).d("$TAG onPause")
        }
    }

    override fun onStop() {
        super.onStop()
        if (enablePrintLifecycle()) {
            LogUtils.tag(TAG_LIFECYCLE).d("$TAG onStop")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (enablePrintLifecycle()) {
            LogUtils.tag(TAG_LIFECYCLE).d("$TAG onSaveInstanceState")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (enablePrintLifecycle()) {
            LogUtils.tag(TAG_LIFECYCLE).d("$TAG onDestroyView")
        }
        mBinding?.unbind()
        mBinding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (enablePrintLifecycle()) {
            LogUtils.tag(TAG_LIFECYCLE).d("$TAG onDestroy")
        }
        mActivity = null
    }

    override fun onDetach() {
        super.onDetach()
        if (enablePrintLifecycle()) {
            LogUtils.tag(TAG_LIFECYCLE).d("$TAG onDetach")
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

    override fun dismissLoading() {
        loadingDialog?.takeIf { it.isShowing }?.dismiss()
    }

    override fun isShowLoading() = loadingDialog?.isShowing ?: false

    open fun enablePrintLifecycle() = false
}