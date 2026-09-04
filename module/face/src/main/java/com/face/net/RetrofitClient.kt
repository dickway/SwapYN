package com.face.net

import com.zzkj.structure.net.BaseRetrofitClient
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient

/**
 * @author 再战科技
 * @date 2022/2/18
 * @description
 */
object RetrofitClient : BaseRetrofitClient() {


    fun getService() = getService(Api::class.java, Api.BASE_URL)

    fun getService2() = getService2(Api::class.java, Api.BASE_URL)//上传文件用


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