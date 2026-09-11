package com.zzkj.structure.ui.dialog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.LogUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zzkj.structure.R
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.base.IUiView
import com.zzkj.structure.base.ViewEffect
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import java.lang.reflect.ParameterizedType
import kotlin.math.min

/**
 * @author lmk
 * @date 2022/2/25
 * @description
 */
open class BaseBottomSheetBindingDF<B : ViewDataBinding> @JvmOverloads constructor(
    /**
     * Fragment 必须要一个无参构造函数
     */
    private val width: Int = WindowManager.LayoutParams.MATCH_PARENT,
    private val height: Int = WindowManager.LayoutParams.WRAP_CONTENT,
    private val gravity: Int = Gravity.CENTER,
    private val initState: Int = BottomSheetBehavior.STATE_COLLAPSED,
    private val skipCollapsed: Boolean = false,
    private val peekHeight: Int = BottomSheetBehavior.PEEK_HEIGHT_AUTO,
    private val dimAmount: Float = 0.2F,
    /**
     * 设置后 width 参数无效
     */
    private val horizontalPadding: Int = 0,
    /**
     * 设置后 width 参数无效
     */
    private val maxWidth: Int = 0,
    /**
     * 设置后 height 参数无效
     */
    private val verticalPadding: Int = 0,

    /**
     * 设置动画
     */
    private val animationRight: Int = 0,
    /**
     * 设置后 height 参数无效
     */
    private val maxHeight: Int = 0,
    private val cancelable: Boolean = true,
    private val isBottomAnimation: Boolean = false,
    /**
     * 初始化回调,用于无需继承BaseBindingDF的简单的弹窗
     */
    private val initCallBack: (BaseBottomSheetBindingDF<B>.(mBinding: B?) -> Unit)? = null
) : BottomSheetDialogFragment(), IUiView {

    protected val TAG = javaClass.simpleName
    protected var mActivity: AppCompatActivity? = null

    var mBinding: B? = null
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as? AppCompatActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = ((javaClass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
            ?.filterIsInstance<Class<B>>()
            ?.firstOrNull()
            ?.getDeclaredMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java
            )?.invoke(null, inflater, container, false) as? B)?.also { binding ->
            binding.lifecycleOwner = viewLifecycleOwner
            getDataBindingArguments()?.map?.forEach {
                binding.setVariable(it.key, it.value)
            }
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(savedInstanceState)
    }

    open fun getDataBindingArguments(): DataBindingArguments? {
        return null
    }

    open fun init(savedInstanceState: Bundle?) {
        initCallBack?.invoke(this, mBinding)
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
//    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.setCancelable(cancelable)
        (dialog as? BottomSheetDialog)?.let {
            it.behavior.state = initState
            it.behavior.skipCollapsed = skipCollapsed
            it.behavior.peekHeight = peekHeight
        }
    }

    override fun onStart() {
        super.onStart()
        (view?.parent as? ViewGroup)?.setBackgroundColor(Color.TRANSPARENT)
        dialog?.window?.run {
            setBackgroundDrawableResource(android.R.color.transparent)
            attributes.width = if (horizontalPadding > 0 || maxWidth > 0) {
                if (maxWidth > 0) min(getScreenWidth() - horizontalPadding * 2, maxWidth)
                else getScreenWidth() - horizontalPadding * 2
            } else {
                width
            }
            attributes.height = if (verticalPadding > 0 || maxHeight > 0) {
                if (maxHeight > 0) min(getScreenHeight() - verticalPadding * 2, maxHeight)
                else getScreenWidth() - horizontalPadding * 2
            } else {
                height
            }
            attributes.dimAmount = dimAmount
            attributes.gravity = gravity
//            if (getAnimation() != 0) {
//                attributes.windowAnimations = getAnimation()
//            }
            attributes = attributes
        }
    }

    protected open fun getAnimation() = if (isBottomAnimation) {
        if (animationRight==0) R.style.BottomDialogAnimation else  R.style.RightDialogAnimation
    } else 0

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
                    else -> {}
                }
            })
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

    fun getSuccessStatusDialog(text: String = ""): StatusDialog {
        val dialog = StatusDialog(requireContext())
        dialog.setText(text)
        dialog.setImgResId(R.mipmap.img_status_success)
        return dialog
    }

    open fun isShowing(): Boolean = dialog != null && dialog!!.isShowing

    open fun showIgnoreState(manager: FragmentManager?) {
        try {
            super.show(manager!!, "")
        } catch (e: Exception) {
            LogUtils.e(e)
        }
    }

    open fun showIgnoreState(activity: FragmentActivity?) {
        if (activity == null || activity.isFinishing) {
            return
        }
        try {
            super.show(activity.supportFragmentManager, "")
        } catch (e: Exception) {
            LogUtils.e(e)
        }
    }

    open fun showIgnoreState(fragment: Fragment) {
        try {
            super.show(fragment.childFragmentManager, "")
        } catch (e: Exception) {
            LogUtils.e(e)
        }
    }
}