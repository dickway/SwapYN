package com.a.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.a.activity.ASwapActivity
import com.a.databinding.ItemAbannerItemBinding
import com.face.bean.AiFaceBean
import com.face.ui.App
import com.face.ui.SwapFaceNewActivity
import com.youth.banner.adapter.BannerAdapter
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.singleClick

class AExploreBannerAdapter : BannerAdapter<AiFaceBean, RecyclerView.ViewHolder>(null) {

    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return Holder(
            ItemAbannerItemBinding.inflate(
                LayoutInflater.from(
                    parent?.context ?: App.INSTANCE
                ), parent, false
            )
        )
    }


    override fun onBindView(
        holder: RecyclerView.ViewHolder?,
        data: AiFaceBean,
        position: Int,
        size: Int
    ) {
        if (holder is Holder) {
            holder.binding.apply {
                imgCover1.loadImage(
                    data.imageUrl,
                    placeholderResId = com.key.R.drawable.img_default_m
                )
                imgCover1.singleClick {
                    if (data.mediaType != "video") {
                        ASwapActivity.jump(imgCover1.context, data.id)
                    } else {
                        SwapFaceNewActivity.jump(imgCover1.context, data.id)
                    }
                }

                tvName.text = data.name
                tvDec.text = data.desc
//                btnTry.singleClick {
//                    when (data.src) {
//                        0 -> btnTry.context.openActivity<ACartoonStartActivity>()
//                        1 -> btnTry.context.openActivity<ARemovalStartActivity>()
//                        2 -> btnTry.context.openActivity<AChangeStartActivity>()
//                        3 -> btnTry.context.openActivity<APaperworkStartActivity>()
//                        4 -> btnTry.context.openActivity<AClearStartActivity>()
//                    }
//                }
//                root.singleClick {
//                    when(data.src){
//                        0-> btnTry.context.openActivity<ACartoonStartActivity>()
//                        1-> btnTry.context.openActivity<ARemovalStartActivity>()
//                        2-> btnTry.context.openActivity<AChangeStartActivity>()
//                        3-> btnTry.context.openActivity<APaperworkStartActivity>()
//                        4-> btnTry.context.openActivity<AClearStartActivity>()
//                    }
//                }
            }
//            binding.removal.singleClick {
//                binding.removal.context.openActivity<AClearStartActivity>()
//            }
//            binding.idphoto.singleClick {
//                binding.idphoto.context.openActivity<APaperworkStartActivity>()
//            }
//            binding.cartoon.singleClick {
//                binding.cartoon.context.openActivity<ACartoonStartActivity>()
//            }
//            binding.backgroun.singleClick {
//                binding.backgroun.context.openActivity<ARemovalStartActivity>()
//            }
//            binding.age.singleClick {
//                binding.age.context.openActivity<AChangeStartActivity>()
//            }
        }
    }


    class Holder(val binding: ItemAbannerItemBinding) : RecyclerView.ViewHolder(binding.root)

}
