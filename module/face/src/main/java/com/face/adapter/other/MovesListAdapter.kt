package com.face.adapter.other

import com.face.R
import com.face.bean.MovesBean
import com.face.databinding.ItemFaceItemBinding
import com.face.databinding.ItemMovesItemBinding
import com.face.ui.ToolGeneratingActivity
import com.face.ui.tmoves.MovesActivity
import com.face.ui.tmoves.MovesListActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick

class MovesListAdapter : BaseAdapter<MovesBean, ItemMovesItemBinding>(
    R.layout.item_moves_item, MovesBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemMovesItemBinding>,
        binding: ItemMovesItemBinding,
        data: MovesBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                imgCover.layoutParams.height = (getScreenWidth() - 32.dp) / 2
//                if (width >= height) {
//
//                } else {
//                    imgCover.layoutParams.height = (getScreenWidth() - 30.dp) / 2 * 4 / 3
//                }
                imgCover.loadImage(
                    imgUrl,
                    placeholderResId = com.key.R.drawable.img_default_m
                )

                imgCover.singleClick {
                    imgCover.context.openActivity<MovesActivity> {
                        putString("imgUrl", data.imgUrl)
                        putString("stype", data.stype)
                        putString("videoUrl", data.videoUrl)
                    }
                }
            }
        }
    }
}
