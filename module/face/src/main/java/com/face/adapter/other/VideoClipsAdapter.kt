package com.face.adapter.other

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import com.face.R
import com.face.bean.ClipsBean
import com.face.databinding.ItemVideoThumbBinding
import com.face.videoclips.trim.VideoTrimmerUtil
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder

class VideoClipsAdapter : BaseAdapter<ClipsBean, ItemVideoThumbBinding>(
    R.layout.item_video_thumb, ClipsBean.differCallback
) {

    private val cachedList = mutableListOf<ClipsBean>()
    private var pendingUpdate = false

    fun addBitmaps(bitmap: Bitmap?, clipSize: Float = 0f) {
        val id = cachedList.size + 1
        if (clipSize > 0.01) {
            cachedList.add(ClipsBean(id = id, mBitmap = bitmap, clipSize = clipSize))
        } else {
            cachedList.add(ClipsBean(id = id, mBitmap = bitmap))
        }
        scheduleFlush()
    }

    // 100ms 最佳节流值（可调）
    private fun scheduleFlush() {
        if (pendingUpdate) return
        pendingUpdate = true
        Handler(Looper.getMainLooper()).postDelayed({
            pendingUpdate = false
            submitList(cachedList.toList())
        }, 100)
    }

    override fun onBindData(
        holder: BaseHolder<ItemVideoThumbBinding>,
        binding: ItemVideoThumbBinding,
        data: ClipsBean?,
        position: Int
    ) {
        holder.binding.apply {
            thumb.apply {
                if ((data?.clipSize ?: 0f) > 0) {
                    val w =
                        VideoTrimmerUtil.VIDEO_FRAMES_WIDTH / VideoTrimmerUtil.MAX_COUNT_RANGE * data?.clipSize!!
                    layoutParams.width = w.toInt()
                } else {
                    layoutParams.width =
                        VideoTrimmerUtil.VIDEO_FRAMES_WIDTH / VideoTrimmerUtil.MAX_COUNT_RANGE
                }
//                layoutParams.width =
//                    VideoTrimmerUtil.VIDEO_FRAMES_WIDTH / VideoTrimmerUtil.MAX_COUNT_RANGE
            }
            thumb.setImageBitmap(data?.mBitmap);
        }
    }
}
