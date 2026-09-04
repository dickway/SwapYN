package com.face.net.download

import okhttp3.Interceptor

class HeaderInterceptor(
    private val headers: Map<String, String>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val requestBuilder = chain.request().newBuilder()
        headers.forEach { (key, value) ->
            requestBuilder.addHeader(key, value)
        }
        return chain.proceed(requestBuilder.build())
    }
}
