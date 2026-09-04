package com.face.adapter.other

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import com.apkfuns.logutils.LogUtils
import com.face.R
import com.face.bean.ClipsBean
import com.face.databinding.ItemVideoThumbBinding
import com.face.videoclips.trim.VideoTrimmerUtil
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth

class VideoSingAdapter : BaseAdapter<ClipsBean, ItemVideoThumbBinding>(
    R.layout.item_video_thumb, ClipsBean.differCallback
) {

    private val cachedList = mutableListOf<ClipsBean>()
    private var pendingUpdate = false

    fun addBitmaps(bitmap: Bitmap?,id: Int) {
        cachedList.add(ClipsBean(id=id,mBitmap = bitmap))
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

            lin.apply {
                layoutParams.height = 50.dp
                layoutParams.width = (getScreenWidth() - 32.dp) / VideoTrimmerUtil.MAX_COUNT_RANGE
            }
            thumb.setImageBitmap(data?.mBitmap);
        }
    }
}
