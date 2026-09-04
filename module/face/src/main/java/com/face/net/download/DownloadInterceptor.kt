package com.face.net.download

import okhttp3.Interceptor
import okhttp3.Response


/**
 * @author 再战科技
 * @date 2023/10/11
 * @description
 */

class DownloadInterceptor(
    private val listener: DownloadProgressListener?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        val body = originalResponse.body ?: return originalResponse

        return originalResponse.newBuilder()
            .body(DownloadResponseBody(body, listener))
            .build()
    }
}