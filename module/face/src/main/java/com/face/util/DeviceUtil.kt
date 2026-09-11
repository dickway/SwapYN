package com.face.util

import com.face.ui.App
import com.blankj.utilcode.util.LogUtils
import com.face.net.HttpInterceptor
import com.zzkj.structure.util.device.AdvertisingIdHelper
import com.zzkj.structure.util.device.DeviceUtils
import com.zzkj.structure.util.ktx.md5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * @author 再战科技
 * @date 2023/10/11
 * @description
 */
object DeviceUtil {

    suspend fun getDeviceId(): String {
        return withContext(Dispatchers.IO) {
            LogUtils.i("getGaid")
            var devicesId: String? = AdvertisingIdHelper.getAdvertisingId(App.INSTANCE)
            if (!isValidGaid(devicesId)) {
                LogUtils.i("getMacAddress")
                devicesId = DeviceUtils.getMacAddress()
            } else {
                SPUtils.gaid = devicesId!!
                HttpInterceptor.gaid = devicesId
            }

            if (devicesId.isNullOrBlank()) {
                devicesId = DeviceUtils.getAndroidID()
            }

            if (devicesId.isNullOrBlank()) {
                devicesId = UUID.randomUUID().toString()
            }

            devicesId = devicesId.md5()
            HttpInterceptor.deviceId = devicesId
            LogUtils.i("deviceId:$devicesId")
            devicesId
        }
    }

    private fun createDeviceId(): String {
        val buildField = DeviceUtils.getBuildField()
        return DeviceUtils.getModel() + DeviceUtils.getBrand() +
                buildField["DISPLAY"] + buildField["SERIAL"] +
                buildField["ID"] + buildField["PRODUCT"]
    }

    private fun isValidGaid(gaid: String?) = !gaid.isNullOrBlank()
            && gaid != "0000-0000"
            && gaid != "00000000-0000-0000-0000-000000000000"
}