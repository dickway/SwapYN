package com.key.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.databinding.ViewDataBinding
import coil.decode.VideoFrameDecoder
import coil.load
import coil.request.ImageRequest
import com.face.bean.FeedbackMsgWrapBean
import com.face.R
import com.face.databinding.ItemFeedbackMsgContentBinding
import com.face.databinding.ItemFeedbackMsgMediaBinding
import com.face.databinding.ItemFeedbackMsgTextBinding
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.base.adapter.BaseMultipleAdapter
import com.zzkj.structure.util.ImgLoader
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.dp
import kotlin.math.max

/**
 * @author 再战科技
 * @date 2023/2/1
 * @description
 */
class FeedbackMsgAdapter(
    private val onMediaClick: (url: String?, isImage: Boolean) -> Unit
) : BaseMultipleAdapter<FeedbackMsgWrapBean>(
    diffCallback = FeedbackMsgWrapBean.differCallback
) {

    private val imgMaxWidth = 260.dp.toFloat()

    companion object {
        const val TYPE_CONTENT = 1
        const val TYPE_TEXT = 2
        const val TYPE_MEDIA = 3
    }

    private fun bindMediaMsg(
        binding: ItemFeedbackMsgMediaBinding,
        data: FeedbackMsgWrapBean
    ) {
        binding.tvTime.text = TimeUtil.getFriendlyDate(data.createTime)
        binding.clMedia.updateLayoutParams<LinearLayout.LayoutParams> {
            gravity = if (data.type == 1) Gravity.START else Gravity.END
        }
        when {
            data.content.hasVideo() -> {
                binding.imgMedia.isVisible = true
                binding.imgPlay.isVisible = true
                binding.clMedia.setOnClickListener {
                    onMediaClick(data.content.videoUrls?.getOrNull(0), false)
                }
                binding.imgMedia.load(
                    data.content.videoUrls?.getOrNull(0),
                    ImgLoader.imageLoader
                ) {
                    placeholder(com.key.R.drawable.img_default_m)
                    error(com.key.R.drawable.img_default_m)
                    decoderFactory { result, options, _ ->
                        VideoFrameDecoder(result.source, options)
                    }
                }
            }

            data.content.hasImage() -> {
                binding.imgMedia.isVisible = true
                binding.imgPlay.isVisible = false
                binding.clMedia.setOnClickListener {
                    onMediaClick(data.content.imgUrls?.getOrNull(0), true)
                }
                ImgLoader.imageLoader.enqueue(
                    ImageRequest.Builder(binding.imgMedia.context)
                        .data(data.content.imgUrls?.getOrNull(0))
                        .placeholder(com.key.R.drawable.img_default_m)
                        .error(com.key.R.drawable.img_default_m)
                        .target(object : coil.target.Target {
                            override fun onError(error: Drawable?) {
                                binding.imgMedia.setImageDrawable(error)
                            }

                            override fun onStart(placeholder: Drawable?) {
                                binding.imgMedia.setImageDrawable(placeholder)
                            }

                            override fun onSuccess(result: Drawable) {
                                val width = result.intrinsicWidth
                                val height = result.intrinsicHeight
                                val max = max(width / imgMaxWidth, height / imgMaxWidth)
                                if (max > 1) {
                                    val bitmap = result.toBitmap(
                                        (width / max).toInt(),
                                        (height / max).toInt()
                                    )
                                    binding.imgMedia.setImageBitmap(bitmap)
                                } else {
                                    binding.imgMedia.setImageDrawable(result)
                                }
                            }
                        })
                        .build()
                )
            }

            else -> {
                binding.imgMedia.isVisible = false
                binding.imgPlay.isVisible = false
            }
        }
    }

    private fun bindTextMsg(
        binding: ItemFeedbackMsgTextBinding,
        data: FeedbackMsgWrapBean
    ) {
        binding.tvTime.text = TimeUtil.getFriendlyDate(data.createTime)
        binding.tvContent.text = data.content.content
        binding.tvContent.updateLayoutParams<LinearLayout.LayoutParams> {
            gravity = if (data.type == 1) Gravity.START else Gravity.END
        }
        binding.tvContent.setBackgroundResource(
            if (data.type == 1) R.drawable.bg_feedback_msg_peer
            else R.drawable.bg_feedback_msg_my
        )
        if (data.type == 0)
            binding.tvContent.setTextColor(Color.WHITE)
    }

    private fun bindFeedbackContent(
        binding: ItemFeedbackMsgContentBinding,
        data: FeedbackMsgWrapBean
    ) {
        binding.tvTime.text = TimeUtil.getFriendlyDate(data.createTime)
        binding.tvContent.text = data.content.content
        when {
            data.content.hasVideo() -> {
                binding.imgMedia.isVisible = true
                binding.imgPlay.isVisible = true
                binding.imgMedia.setOnClickListener {
                    onMediaClick(data.content.videoUrls?.getOrNull(0), false)
                }
                binding.imgMedia.load(
                    data.content.videoUrls?.getOrNull(0),
                    ImgLoader.imageLoader
                ) {
                    placeholder(com.key.R.drawable.img_default_m)
                    error(com.key.R.drawable.img_default_m)
                    decoderFactory { result, options, _ ->
                        VideoFrameDecoder(result.source, options)
                    }
                }
            }

            data.content.hasImage() -> {
                binding.imgMedia.isVisible = true
                binding.imgPlay.isVisible = false
                binding.imgMedia.setOnClickListener {
                    onMediaClick(data.content.imgUrls?.getOrNull(0), true)
                }
                binding.imgMedia.load(
                    data.content.imgUrls?.getOrNull(0),
                    ImgLoader.imageLoader
                ) {
                    placeholder(com.key.R.drawable.img_default_m)
                    error(com.key.R.drawable.img_default_m)
                }
            }

            else -> {
                binding.imgMedia.isVisible = false
                binding.imgPlay.isVisible = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<ViewDataBinding> {
        return innerCreateViewHolder(
            parent,
            when (viewType) {
                TYPE_TEXT -> R.layout.item_feedback_msg_text
                TYPE_MEDIA -> R.layout.item_feedback_msg_media
                else -> R.layout.item_feedback_msg_content
            },
            false
        )
    }

    override fun onBindData(
        holder: BaseHolder<ViewDataBinding>,
        binding: ViewDataBinding,
        data: FeedbackMsgWrapBean?,
        position: Int
    ) {
        if (data == null) {
            return
        }
        when (binding) {
            is ItemFeedbackMsgContentBinding -> bindFeedbackContent(binding, data)
            is ItemFeedbackMsgTextBinding -> bindTextMsg(binding, data)
            is ItemFeedbackMsgMediaBinding -> bindMediaMsg(binding, data)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when {
            item == null -> TYPE_TEXT
            item.type == 3 -> TYPE_CONTENT
            item.content.hasMedia() -> TYPE_MEDIA
            else -> TYPE_TEXT
        }
    }
}