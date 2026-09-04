package com.zzkj.structure.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.widget.ImageView
import coil.Coil
import coil.ImageLoader
import coil.decode.BitmapFactoryDecoder
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.load
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.size.Size
import coil.size.ViewSizeResolver
import coil.util.DebugLogger
import com.zzkj.structure.base.BaseApp

/**
 * @author lmk
 * @date 2022/3/24
 * @description
 */
object ImgLoader {

    val imageLoader = ImageLoader.Builder(BaseApp.INSTANCE)
        .logger(DebugLogger())
//        .respectCacheHeaders(false)
        .memoryCache(
            MemoryCache.Builder(BaseApp.INSTANCE)
                .maxSizePercent(0.15)
                .build()
        )
        .diskCache(
            DiskCache.Builder()
                .directory(
                    (BaseApp.INSTANCE.externalCacheDir ?: BaseApp.INSTANCE.cacheDir).resolve(
                        "imageCache"
                    )
                )
                .maxSizeBytes(512L * 1024 * 1024) // 比如设置磁盘缓存最大 512MB
                .build()
        )
        .components {//gif
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(CustomWebPDecoder.Factory())
            }
            add(VideoFrameDecoder.Factory())
        }
        .allowRgb565(true)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .allowHardware(false)//默认关闭解码
        .build()

    fun init() {
        Coil.setImageLoader(imageLoader)
    }

    fun ImageView.loadImage(
        url: String?,
        placeholderDrawable: Drawable? = null,
        placeholderResId: Int? = null,
        crossfade: Boolean = false,
        hardware: Boolean = false,
        size: Size? = null
    ) {
        load(url, imageLoader) {
            allowHardware(hardware)
            if (placeholderDrawable != null) {
                placeholder(placeholderDrawable)
                error(placeholderDrawable)
            } else if (placeholderResId != null && placeholderResId != 0) {
                placeholder(placeholderResId)
                error(placeholderResId)
            }
            if (size != null) size(size) else size(ViewSizeResolver(this@loadImage))
            crossfade(crossfade)
        }
    }
}