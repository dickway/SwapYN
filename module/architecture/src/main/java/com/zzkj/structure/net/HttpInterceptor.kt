package com.zzkj.structure.net

import com.zzkj.structure.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * @author lmk
 * @date 2022/2/18
 * @description
 */
class HttpInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
            .newBuilder()
            .build()
        val response: Response = chain.proceed(request)
        return response
    }

}