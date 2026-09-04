package com.zzkj.structure.util

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import androidx.lifecycle.LiveData

/**
 * @author lmk
 * @date 2022/3/29
 * @description
 */
class NetworkLiveData(private val context: Context) : LiveData<Boolean>() {

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            postValue(checkConnected(capabilities))
        }

        override fun onLost(network: Network) {
            postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()
        updateConnection()
        cm.registerDefaultNetworkCallback(callback)
    }

    override fun onInactive() {
        super.onInactive()
        cm.unregisterNetworkCallback(callback)
    }

    private fun updateConnection() {
        val network = cm.activeNetwork
        val capabilities = cm.getNetworkCapabilities(network)
        postValue(checkConnected(capabilities))
    }

    /** 🔥 核心修复：统一判断，避免一直 true 或错误 true */
    private fun checkConnected(cap: NetworkCapabilities?): Boolean {
        if (cap == null) return false

        val hasInternet = cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val validated = cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        // 如果需要避免“假联网”，可以加上 validated
        return hasInternet && validated
    }
}
