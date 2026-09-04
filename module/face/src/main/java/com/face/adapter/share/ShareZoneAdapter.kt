package com.face.adapter.share

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.apkfuns.logutils.LogUtils
import com.base.BlurTransformation
import com.face.R
import com.face.bean.AiFaceBean
import com.face.bean.ShareZoneBean
import com.face.databinding.ItemShareZoneBinding
import com.face.ui.SwapFaceListActivity
import com.face.ui.SwapFaceNewActivity
import com.face.ui.VipActivity
import com.face.util.GVM
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick


open class ShareZoneAdapter : BaseAdapter<AiFaceBean, ItemShareZoneBinding>(
    R.layout.item_share_zone, AiFaceBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemShareZoneBinding>,
        binding: ItemShareZoneBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                tvScore.text=getScoreStr()
                if (width >= height) {
                    imgCover.layoutParams.height = (getScreenWidth() - 36.dp) / 2
                } else {
                    imgCover.layoutParams.height = (getScreenWidth() - 36.dp) / 2 * 4 / 3
                }
                if (data.isVip) {
                    if (data.mediaType == "video") {
                        imgPro.visibility = View.GONE
                        imgVideo.visibility = View.VISIBLE
//                        imgPro.setImageResource(com.key.R.drawable.img_share_video_icon)
                    }else{
                        imgVideo.visibility = View.GONE
                    }
                    imgCover.loadImage(
                        data.imageUrl,
                        placeholderResId = com.key.R.drawable.img_default_m
                    )
                } else {
                    imgCover.load(data.imageUrl) {
                        if (!data.isBlur) {
                            if (data.mediaType == "video") {
                                imgPro.visibility = View.GONE
                                imgVideo.visibility = View.VISIBLE
//                                imgPro.setImageResource(com.key.R.drawable.img_share_video_icon)
                            }else{
                                imgVideo.visibility = View.GONE
                            }
                        } else {
                            imgPro.visibility = View.VISIBLE
                            imgVideo.visibility = View.GONE
                            transformations(BlurTransformation(radius = 8, scale = 0.2f))
//                            imgPro.setImageResource(com.key.R.drawable.img_swap_pro_m)
                        }
                    }
                }
                imgCover.singleClick {
                    val pos = holder.bindingAdapterPosition
                    if (pos !in currentList.indices) return@singleClick
                    if (!data.isVip && data.isBlur) {
                        GVM.INSTANT.payPage.value = "Share_Popular_List"
                        VipActivity.jump(imgCover.context)
                    } else {
                        val list =
                            (currentList.drop(pos) + currentList.take(pos))
                                .take(50).toMutableList()
                        SwapFaceListActivity.jump(imgCover.context, id, 0, list)
//                        SwapFaceNewActivity.jump(imgCover.context, id)
                    }

                }
            }
        }
    }

}
