package com.face.adapter.share

import android.view.View
import coil.load
import com.base.BlurTransformation
import com.face.R
import com.face.bean.AiFaceBean
import com.face.bean.CollectBean
import com.face.bean.ShareZoneBean
import com.face.databinding.ItemShareMeBinding
import com.face.databinding.ItemShareZoneBinding
import com.face.ui.SwapFaceNewActivity
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ImgLoader.loadImage
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.singleClick


open class ShareMeAdapter(private val onSonClick: (view: View, position: Int, data: AiFaceBean) -> Unit): BaseAdapter<AiFaceBean, ItemShareMeBinding>(
    R.layout.item_share_me, AiFaceBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemShareMeBinding>,
        binding: ItemShareMeBinding,
        data: AiFaceBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                shareBean=data
                if (width >= height) {
                    imgCover.layoutParams.height = (getScreenWidth() - 30.dp) / 2
                } else {
                    imgCover.layoutParams.height = (getScreenWidth() - 30.dp) / 2 * 4 / 3
                }

                imgCover.load(data.imageUrl)
                ivM.singleClick{
                    onSonClick.invoke(ivM,position,data)
                }

                imgCover.singleClick {
                    SwapFaceNewActivity.jump(imgCover.context, id)
                }
            }
        }
    }

}
