package com.face.adapter.swap

import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.core.view.postDelayed
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.SeekParameters
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.blankj.utilcode.util.LogUtils
import com.base.BlurTransformation
import com.face.R
import com.face.bean.AiFaceBean
import com.face.databinding.ItemSwapBannerBinding
import com.face.ui.VipActivity
import com.face.util.EventUtil
import com.face.util.GVM
import com.face.video.VideoHeaderManager
import com.face.video.ZoomVideo
import com.face.video.cache.PreloadManager
import com.flyjingfish.openimagelib.photoview.PhotoView
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.base.adapter.BaseMultipleAdapter
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick

class SwapImgVideoAdapter(val activity: FragmentActivity, val type: String? = null) :
    BaseMultipleAdapter<AiFaceBean>(AiFaceBean.differCallback) {

    var onViewClick: (() -> Unit)? = null

    fun getPlayerByViewHolder(holder: RecyclerView.ViewHolder?): ZoomVideo? {
        return ((holder as? BaseHolder<*>)?.binding as? ItemSwapBannerBinding)?.videoFace
    }


    fun getPhotoByViewHolder(holder: RecyclerView.ViewHolder?): PhotoView? {
        return ((holder as? BaseHolder<*>)?.binding as? ItemSwapBannerBinding)?.imgFace
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseHolder<ViewDataBinding> {
        return innerCreateViewHolder(
            parent,
            R.layout.item_swap_banner,
        )
    }

    override fun getItemCount(): Int = if (currentList.size > 1) Int.MAX_VALUE else 1

    override fun getItemData(position: Int): AiFaceBean? {
        val actualPosition = position % currentList.size
        return super.getItemData(actualPosition)
    }

    override fun onBindData(
        holder: BaseHolder<ViewDataBinding>,
        binding: ViewDataBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        if (holder.binding is ItemSwapBannerBinding && data != null) {
            bindVideo(holder.binding as ItemSwapBannerBinding, position, data)
        }
    }

    private fun bindVideo(
        binding: ItemSwapBannerBinding,
        position: Int,
        data: AiFaceBean
    ) {


        val isblur = (data.isBlur && !GVM.INSTANT.isVip.value)
        binding.isBlurView = isblur
        if (isblur) {
            binding.imgVague.load(data.imageUrl) {
                transformations(BlurTransformation(radius = 8, scale = 0.2f))
            }
        }

        binding.btnVip.singleClick {
            GVM.INSTANT.payPage.value = "Share_Popular_Templates"
            VipActivity.jump(binding.btnVip.context)
        }


        val widths = getScreenWidth()
        val heights = getScreenHeight()
        val realValue = data.width / data.notZeroHeight().toDouble() //实际比例
        var imgW: Int
        var imgH: Int
        binding.layoutFace.apply {
            if (realValue < 0.75) {//根据图片大小优先显示宽
                imgW = widths
                imgH = heights
            } else {
                imgW = widths
                imgH = data.height * widths / data.notZeroWidth()
            }
        }

        binding.proShow = data.pro != 0
        binding.imgFace.visibility = View.VISIBLE
        binding.videoFace.visibility = View.INVISIBLE

        if (data.mediaType == "video" && type == "Swap") {
//            if (position != 0) {
//                binding.videoFace.postDelayed({
//                    PreloadManager.getInstance()
//                        .addPreloadTask(data.videoUrl, position, activity)
//                },500)
//            }
            binding.imgFace.visibility = View.INVISIBLE
            binding.videoFace.visibility = View.VISIBLE
            binding.videoFace.loadBgImage(
                data.webpUrl.ifEmpty { data.imageUrl },
                com.key.R.drawable.img_default_m,
                imgW,
                imgH
            )
            GSYVideoOptionBuilder()
                .setIsTouchWiget(false)
                .setVideoTitle("")
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setPlayTag("RecyclerView2List")
                .setLooping(true)
                .setShowFullAnimation(false)
                .setNeedLockFull(false)
                .setMapHeadData(VideoHeaderManager.headers)
                .setPlayPosition(position)
                .setVideoAllCallBack(object : GSYSampleCallBack() {
                    override fun onStartPrepared(url: String?, vararg objects: Any?) {
                        super.onStartPrepared(url, *objects)
                        GVM.INSTANT.videoOpenTime.value = System.currentTimeMillis()
                    }

                    //这里已经重写了要双击才触发
                    override fun onClickBlank(url: String?, vararg objects: Any?) {
                        onViewClick?.invoke()
                        super.onClickBlank(url, *objects)
                    }

                    override fun onPrepared(url: String, vararg objects: Any) {
                        super.onPrepared(url, *objects)
                        EventUtil.inPage(
                            "swap_face",
                            mapOf("media_id" to data.id, "media_type" to data.mediaType)
                        )
                        val videoTimeC =
                            System.currentTimeMillis() - GVM.INSTANT.videoOpenTime.value
                        binding.videoFace.gsyVideoManager?.player?.setNeedMute(GVM.INSTANT.swapNeedMute.value)
                        if (GVM.INSTANT.isForeground.value != false)
                            EventUtil.loadTime("0", videoTimeC.toString(), url)
                    }

                    override fun onPlayError(url: String?, vararg objects: Any?) {
                        super.onPlayError(url, *objects)
                        LogUtils.e("onPlayError:${objects.contentToString()}  $url")
//                        PlayerFactory.setPlayManager(SystemPlayerManager::class.java)
                        binding.videoFace.postDelayed(500L) {
                            binding.videoFace.setUp(
                                data.videoUrl.replace("%20", " "),
                                true,
                                null,
                                VideoHeaderManager.headers,
                                ""
                            )
                            binding.videoFace.gsyVideoManager?.player?.setNeedMute(GVM.INSTANT.swapNeedMute.value)
                            binding.videoFace.startPlayLogic()
                        }
                    }
                }).build(binding.videoFace)
        } else {
            binding.imgFace.setDoubleClickListener {
                onViewClick?.invoke()
            }
            binding.imgFace.loadImage(
                data.imageUrl,
                placeholderResId = com.key.R.drawable.img_default_m
            )
        }
    }

    override fun onViewAttachedToWindow(holder: BaseHolder<ViewDataBinding>) {
        super.onViewAttachedToWindow(holder)
        getPhotoByViewHolder(holder)?.attacher?.registerDisplayListener()
        getPlayerByViewHolder(holder)?.getAttacher()?.registerDisplayListener()
//        LogUtils.e(">>>View 加入屏幕 第${holder.bindingAdapterPosition + 1}页")
    }

    override fun onViewDetachedFromWindow(holder: BaseHolder<ViewDataBinding>) {
        super.onViewDetachedFromWindow(holder)
        getPhotoByViewHolder(holder)?.attacher?.unRegisterDisplayListener()
        getPlayerByViewHolder(holder)?.getAttacher()?.unRegisterDisplayListener()
//        LogUtils.e(">>>View 离屏 第${holder.bindingAdapterPosition + 1}页")
//        currentList.getOrNull(holder.layoutPosition)?.videoUrl.takeIf { !it.isNullOrBlank() }
//            .let {
//                PreloadManager.getInstance()
//                    .removePreloadTask(it, holder.layoutPosition)
//            }
    }
}