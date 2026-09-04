package com.face.net.download

import com.face.net.Api
import com.face.util.FileUtil
import com.face.video.VideoHeaderManager
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


object DownloadUtil {
    private val mExecutorService = Executors.newSingleThreadExecutor()

    /**
     * @param listener
     */
    fun downloadFile(
        url: String,
        desFilePath: String,
        listener: DownloadProgressListener
    ) {

        val builder = OkHttpClient.Builder()
            .readTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(HeaderInterceptor(VideoHeaderManager.headers))
            .addInterceptor(DownloadInterceptor(listener))
            .build()


        val api = Retrofit.Builder()
            .baseUrl("https://img.ink/")//这里的base无用
            .client(builder)
            .build()
            .create(Api::class.java)

        mExecutorService.execute {
            try {
                val result: Response<ResponseBody> =
                    api.downloadWithUrl(url).execute()
                val file = FileUtil.writeFile(desFilePath, result.body()?.byteStream())
                listener.onFinish(file)
            } catch (e: Exception) {
                listener.onFailed(e.message)
            }
        }
    }

}
