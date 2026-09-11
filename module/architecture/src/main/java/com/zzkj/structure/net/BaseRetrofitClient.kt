package com.zzkj.structure.net

import com.zzkj.structure.BuildConfig
import com.zzkj.structure.util.moshi.MoshiHelper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


abstract class BaseRetrofitClient {

    companion object CLIENT {
        private const val Call_TIME_OUT = 240
        private const val Connect_TIME_OUT = 240
    }

    val httpClient2: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .callTimeout(Call_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(Call_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(Call_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(Connect_TIME_OUT.toLong(), TimeUnit.SECONDS)
        handleBuilder(builder)
        builder.build()
    }

    val httpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .callTimeout(Call_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(Call_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(Call_TIME_OUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(Connect_TIME_OUT.toLong(), TimeUnit.SECONDS)
        getHttpInterceptor()?.let { builder.addInterceptor(it) }
        val logInterceptor = HttpLoggingInterceptor().apply {
            setLevel(
                if (BuildConfig.DEBUG)
                    HttpLoggingInterceptor.Level.BODY
                else
                    HttpLoggingInterceptor.Level.NONE
            )
        }
        builder.addInterceptor(logInterceptor)
        handleBuilder(builder)
        builder.build()
    }

    open fun getHttpInterceptor(): Interceptor? {
        return HttpInterceptor()
    }

    open fun getConverterFactory(): Converter.Factory {
        return MoshiConverterFactory.create(MoshiHelper.moshi)
    }

    open fun handleBuilder(builder: OkHttpClient.Builder) {

    }

    fun <Service> getService(serviceClass: Class<Service>, baseUrl: String): Service {
        return Retrofit.Builder()
            .client(httpClient)
            .addConverterFactory(getConverterFactory())
//            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .create(serviceClass)
    }

    fun <Service> getService2(serviceClass: Class<Service>, baseUrl: String): Service {
        return Retrofit.Builder()
            .client(httpClient2)
            .addConverterFactory(getConverterFactory())
            .baseUrl(baseUrl)
            .build()
            .create(serviceClass)
    }

//    fun <Service> getService2(serviceClass: Class<Service>, baseUrl: String): Service {
//        return Retrofit.Builder()
//            .client(
//                OkHttpClient.Builder()
//                    .readTimeout(Call_TIME_OUT.toLong(), TimeUnit.SECONDS)
//                    .connectTimeout(Connect_TIME_OUT.toLong(), TimeUnit.SECONDS)
//                    .build()
//            )
//            .addConverterFactory(getConverterFactory())
//            .baseUrl(baseUrl)
//            .build()
//            .create(serviceClass)
//    }
}
