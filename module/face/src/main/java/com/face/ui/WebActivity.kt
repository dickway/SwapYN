package com.face.ui

import android.content.Context
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.addCallback
import androidx.core.view.isVisible
import com.face.BR
import com.face.R
import com.face.databinding.ActivityWebBinding
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.openActivity

class WebActivity : BaseBindingActivity<ActivityWebBinding, BaseViewModel>(
    layoutResId = R.layout.activity_web,
    viewModelType = BaseViewModel::class.java
) {

    private val url by intentExtras<String>("url")
    private val title by intentExtras<String>("title")

    companion object {
        fun openPrivacyPolicy(context: Context?, url: String, title: String) {
            context?.openActivity<WebActivity> {
                putString("url", url)
                putString("title", title)
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        onBackPressedDispatcher.addCallback(this) {
            mBinding?.webView?.apply {
                if (canGoBack()) {
                    goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            } ?: kotlin.run {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
        initWebViewConfig()
        mBinding?.apply {
            toolbar.title = title ?: getStringX(com.key.R.string.app_ai_name)
        }
        mModel.getCommonLiveData<Int>("progress").observe(this) {
            mBinding?.apply {
                if (it == 100) {
                    if (progressBar.isVisible) {
                        progressBar.isVisible = false
                    }
                } else {
                    progressBar.progress = it
                    if (!progressBar.isVisible) {
                        progressBar.isVisible = true
                    }
                }
            }
        }
        mModel.getCommonLiveData<String>("title").observe(this) {
            mBinding?.toolbar?.title = it
        }
    }


    private fun initWebViewConfig() {
        CookieManager.getInstance().setAcceptCookie(true)
        mBinding?.apply {
            webView.settings.javaScriptEnabled = true
            webView.settings.javaScriptCanOpenWindowsAutomatically = true
            webView.settings.setGeolocationEnabled(true)
            webView.settings.domStorageEnabled = true
            webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            webView.webChromeClient = this@WebActivity.webChromeClient
//          webViewClient = this@WebActivity.webViewClient
            this@WebActivity.url?.let { webView.loadUrl(it) }
            toolbar.setNavigationOnClickListener { finish() }
        }
    }

    override fun onDestroy() {
        mBinding?.webView?.destroy()
        super.onDestroy()
    }


    private val webChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            if (this@WebActivity.title.isNullOrBlank()) {
                mModel.getCommonLiveData<String>("title").postValue(title)
            }
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            mModel.getCommonLiveData<Int>("progress").postValue(newProgress)
        }

        override fun onGeolocationPermissionsShowPrompt(
            origin: String,
            callback: GeolocationPermissions.Callback
        ) {
            callback.invoke(origin, true, true)
            super.onGeolocationPermissionsShowPrompt(origin, callback)
        }
    }

    override fun getDataBindingArguments(): DataBindingArguments? {
        return DataBindingArguments(BR.handler, this)
    }
}