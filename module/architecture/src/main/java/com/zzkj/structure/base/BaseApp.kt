package com.zzkj.structure.base

import android.annotation.SuppressLint
import android.app.Application
import android.view.Gravity
import androidx.annotation.CallSuper
import androidx.lifecycle.*
import com.hjq.toast.Toaster
import com.hjq.toast.style.WhiteToastStyle
import com.zzkj.structure.lifecycle.DefaultActivityLifecycle
import com.zzkj.structure.lifecycle.DefaultAppLifecycle
import com.zzkj.structure.util.ImgLoader
import com.zzkj.structure.util.ktx.getScreenHeight

open class BaseApp : Application(), ViewModelStoreOwner {

    override val viewModelStore: ViewModelStore = ViewModelStore()
    private var mApplicationProvider: ViewModelProvider? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmStatic
        lateinit var INSTANCE: BaseApp
            private set
    }

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(getAppLifecycle())
        registerActivityLifecycleCallbacks(getActivityLifecycle())
        ImgLoader.init()
        Toaster.init(this, WhiteToastStyle())
        Toaster.setGravity(Gravity.BOTTOM, 0, getScreenHeight() / 6)
    }

    /**
     * 获取App作用域的viewmodel
     */
    open fun <T : ViewModel> getApplicationScopeViewModel(modelClass: Class<T>): T {
        if (mApplicationProvider == null) {
            mApplicationProvider = ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this)
            )
        }
        return mApplicationProvider!![modelClass]
    }

    open fun getActivityLifecycle(): DefaultActivityLifecycle = DefaultActivityLifecycle()
    open fun getAppLifecycle(): DefaultAppLifecycle = DefaultAppLifecycle()
}