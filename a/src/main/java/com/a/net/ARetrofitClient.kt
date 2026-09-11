package com.a.net

import com.face.net.HttpInterceptor
import com.zzkj.structure.net.BaseRetrofitClient
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient


object ARetrofitClient : BaseRetrofitClient() {


    fun getService() = getService(AApi::class.java, AApi.BASE_URL)

    fun getService2() = getService2(AApi::class.java, AApi.BASE_URL)//上传文件用


    override fun getHttpInterceptor(): Interceptor {
        return HttpInterceptor
    }

    override fun handleBuilder(builder: OkHttpClient.Builder) {
        builder.cookieJar(object : CookieJar {
            var mCookies: List<Cookie> = listOf()
            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return mCookies
            }

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                if (cookies.isNotEmpty()) {
                    mCookies = cookies
                }
            }
        })
    }
}
