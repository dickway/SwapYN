package com.face.net

import android.os.Build
import com.apkfuns.logutils.LogUtils
import com.face.BuildConfig
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseApp
import com.zzkj.structure.util.SPbaseUtils
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.device.DeviceUtils
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import retrofit2.Invocation
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * @author 再战科技
 * @date 2023/10/11
 * @description
 */
object HttpInterceptor : Interceptor {

    private val packageName: String = BaseApp.INSTANCE.packageName
    private val androidVersionCode = Build.VERSION.SDK_INT.toString()
    private val versionCode = BuildConfig.VERSION_CODE.toString()
    private val debug = BuildConfig.DEBUG.toString()

    var gaid = SPUtils.gaid
    var deviceId = SPUtils.deviceId

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val t1 = System.currentTimeMillis()
        val token = SPUtils.tokenUser.ifEmpty { GVM.INSTANT.userInfo.value.token }
        val request: Request =
            chain.request().newBuilder().addHeader("token", token)
                .addHeader("userId", GVM.INSTANT.userInfo.value.userId.toString())
                .addHeader("packageName", packageName)
                .addHeader("versionCode", versionCode)
                .addHeader("androidVersionCode", androidVersionCode)
                .addHeader("androidBrand", Build.BRAND)
                .addHeader("androidModel", Build.MODEL)
                .addHeader("gaid", gaid)
                .addHeader("deviceId", deviceId)
                .addHeader("debug", debug)
                .addHeader("timestamp", System.currentTimeMillis().toString())
                .addHeader("devicecampaign", GVM.INSTANT.isShowTool.value.toString())
                .addHeader("languageA", SPbaseUtils.spLanguage)
                .addHeader("appLanguage", EventUtil.SYSTEM_LANGUAGE)
                .addHeader("androidId", DeviceUtils.getAndroidID())
                .addHeader("timeZone", EventUtil.TIME_ZONE)
                .build()

        LogUtils.e("gaid:$gaid   userId:${GVM.INSTANT.userInfo.value.userId}")
        printRequest(request)
        val response: Response = chain.proceed(request)
        printResponse(t1, response)
        return response
    }

    private fun printResponse(t1: Long, response: Response) {
        if (!BuildConfig.DEBUG) {
            return
        }
        try {
            val responseBody = response.peekBody((1024 * 500).toLong())
            val t2 = System.currentTimeMillis()
            val request: Request = response.request
            val params = request.body?.let { body ->
                val type = body.contentType().toString()
                if (type.contains("multipart")) {
                    type
                } else {
                    Buffer().also { body.writeTo(it) }.readString(
                        body.contentType()?.charset(StandardCharsets.UTF_8)
                            ?: StandardCharsets.UTF_8
                    )
                }
            } ?: "null"
            val result = if (response.headers["Content-Type"]?.let {
                    it.contains("text/plain", true) || it.contains("application/json", true)
                } == true) {
                responseBody.string()
            } else {
                response.headers["Content-Type"]
            }
            LogUtils.v(
                "请求地址：${response.request.url}" + "\nAPI：${request.tag(Invocation::class.java)}" + "\n请求方式：${request.method}" + "\n请求参数：${params}" + "\n请求耗时：${(t2 - t1)}ms" + "\n请求结果：$result"
            )
        } catch (e: Exception) {
            LogUtils.w(e)
        }
    }

    private fun printRequest(request: Request) {
        if (!BuildConfig.DEBUG) {
            return
        }
        try {
            val params = request.body?.let { body ->
                val type = body.contentType().toString()
                if (type.contains("multipart")) {
                    type
                } else {
                    Buffer().also { body.writeTo(it) }.readString(
                        body.contentType()?.charset(StandardCharsets.UTF_8)
                            ?: StandardCharsets.UTF_8
                    )
                }
            } ?: "null"
            LogUtils.v(
                "请求地址：${request.url}" + "\nAPI：${request.tag(Invocation::class.java)}" + "\n请求方式：${request.method}" + "\n请求参数：${params}"
            )
        } catch (e: Exception) {
            LogUtils.w(e)
        }
    }
}