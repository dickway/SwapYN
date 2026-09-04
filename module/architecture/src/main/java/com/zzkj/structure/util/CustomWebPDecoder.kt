package com.zzkj.structure.util

import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.decode.DecodeResult
import coil.decode.Decoder
import coil.fetch.SourceResult
import coil.request.Options
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomWebPDecoder(
    private val byteArray: ByteArray,
    private val options: Options
) : Decoder {

    override suspend fun decode(): DecodeResult {
        val context = options.context
        val bitmap = withContext(Dispatchers.IO) {
            Glide.with(context)
                .asBitmap()
                .load(byteArray)
                .diskCacheStrategy(DiskCacheStrategy.ALL)  // 保存到磁盘缓存
                .skipMemoryCache(true)  // 禁用内存缓存
                .submit()
                .get()
        }
        return DecodeResult(
            drawable = BitmapDrawable(context.resources, bitmap),
            isSampled = true
        )
    }

    class Factory : Decoder.Factory {
        override fun create(
            result: SourceResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? {
            // 将 ImageSource 的数据转为 byte[]
//            if (!DecodeUtils.isGif(result.source.source())) return null
            val byteArray = result.source.source().readByteArray()
            return CustomWebPDecoder(byteArray, options)
        }
    }
}


