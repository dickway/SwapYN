package com.zzkj.structure.net

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.blankj.utilcode.util.LogUtils
import com.zzkj.structure.BuildConfig
import com.zzkj.structure.base.BaseApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// https://developer.android.com/training/basics/network-ops/reading-network-state?hl=zh-cn
object NetworkUtils {

    private val connectivityManager by lazy {
        BaseApp.INSTANCE.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val wifiManager by lazy {
        BaseApp.INSTANCE.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    var networkInfo: NetworkInfo = NetworkInfo()
        private set

    val networkInfoFlow = MutableStateFlow(networkInfo)
    val hasNetworkFlow = networkInfoFlow.map { it.hasNetwork }.distinctUntilChanged()

    @MainThread
    fun init() {
        destroy()
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        updateNetworkInfo()
        // 后台WiFi变了没有后台定位权限获取不到名字，切换到前台后重新获取下
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
        if (BuildConfig.DEBUG) {
            @OptIn(DelicateCoroutinesApi::class)
            GlobalScope.launch {
                networkInfoFlow.collect { LogUtils.d(it) }
            }
        }
    }

    fun updateNetworkInfo() {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        update(capabilities)
    }

    fun destroy() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (_: Throwable) {
        }
        try {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(lifecycleObserver)
        } catch (_: Throwable) {
        }
    }

    @SuppressLint("NewApi")
    private fun update(capabilities: NetworkCapabilities?) {
        if (capabilities == null) {
//            XLog.i("capabilities == null")
            networkInfo = NetworkInfo()
            networkInfoFlow.value = networkInfo
//            XLog.e(networkInfo.toJson)
            return
        }
        val hasWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val hasCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        val hasVpn = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        val hasEthernet = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        networkInfo = networkInfo.copy(
            hasWifi = hasWifi,
            hasCellular = hasCellular,
            hasVpn = hasVpn,
            hasEthernet = hasEthernet,
            hasNetwork = hasWifi || hasCellular || hasEthernet,
//            wifiName = try {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    (capabilities.transportInfo as WifiInfo).ssid.validWifiName()
//                        .ifEmpty { getWifiName() }
//                } else {
//                    getWifiName()
//                }
//            } catch (_: Throwable) {
//                getWifiName()
//            },
            wifiIp = getWifiIpAddress()
        )
        networkInfoFlow.value = networkInfo
//        XLog.e(networkInfo.toJson)
    }

//    @Suppress("DEPRECATION")
//    private fun getWifiName() = wifiManager.connectionInfo.ssid.validWifiName()

//    private fun String.validWifiName() = takeUnless {
//        it.equals("<unknown ssid>", true)
//    }?.removePrefix("\"")?.removeSuffix("\"").orEmpty()

    @Suppress("DEPRECATION")
    private fun getWifiIpAddress(): String {
        val ipAddress = wifiManager.connectionInfo.ipAddress
        if (ipAddress == 0) return ""
        return (ipAddress and 0xFF).toString() + "." + (ipAddress shr 8 and 0xFF) + "." +
                (ipAddress shr 16 and 0xFF) + "." + (ipAddress shr 24 and 0xFF)
    }

    private val networkCallback: ConnectivityManager.NetworkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities
            ) {
                update(capabilities)
            }

            override fun onLost(network: Network) {
                update(connectivityManager.getNetworkCapabilities(network))
            }
        }

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            updateNetworkInfo()
        }
    }
}


