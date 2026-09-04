package com.a.adapter

import com.a.R
import com.a.activity.AToolCompletionActivity
import com.a.databinding.ItemAhotlistItemBinding
import com.bumptech.glide.Glide
import com.face.bean.ToolTaskBean
import com.face.key.AiTaskType
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.ktx.singleClick


open class AToolHistoryAdapter : BaseAdapter<ToolTaskBean, ItemAhotlistItemBinding>(
    R.layout.item_ahotlist_item, ToolTaskBean.differCallback
) {

    override fun onBindData(
        holder: BaseHolder<ItemAhotlistItemBinding>,
        binding: ItemAhotlistItemBinding,
        data: ToolTaskBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                imgCover.layoutParams.width = (getScreenWidth() - 40.dp) / 2
                imgCover.layoutParams.height = (getScreenWidth() - 40.dp) / 2 * 4 / 3

                if (data.type.equals(AiTaskType.RMBG, false) ||
                    data.type.equals(AiTaskType.IMG2CARTOON, false) ||
                    data.type.equals(AiTaskType.OLD_PHONE, false) ||
                    data.type.equals(AiTaskType.ID_CARD, false) ||
                    data.type.equals(AiTaskType.CHANGE_AGE, false) ||
                    data.type.equals(AiTaskType.DYNAMIC, false)
                ) {
                    data.recordUrl = data.getCutUrl()
                }

                Glide.with(imgCover.context)
                    .load(data.recordUrl.split(",").lastOrNull())
                    .placeholder(com.key.R.drawable.img_default_m)
                    .skipMemoryCache(true)
                    .into(imgCover)

                imgCover.singleClick {
//                    var strTilte = ""
//                    when (data.type) {
//                        AiTaskType.RMBG -> strTilte =
//                            getStringX(com.face.R.string.one_click_background_removal)
//
//                        AiTaskType.IMG2CARTOON -> strTilte =
//                            getStringX(com.face.R.string.image_to_cartoon)
//
//                        AiTaskType.OLD_PHONE -> strTilte =
//                            getStringX(com.face.R.string.old_photo_restoration)
//
//                        AiTaskType.ID_CARD -> strTilte =
//                            getStringX(com.face.R.string.id_photo_generation)
//
//                        AiTaskType.CHANGE_AGE -> strTilte =
//                            getStringX(com.face.R.string.age_change)
//
//                        AiTaskType.DYNAMIC -> strTilte =
//                            getStringX(com.face.R.string.pic_live)
//                    }
                    imgCover.context.openActivity<AToolCompletionActivity>() {
                        putParcelable("task", data)
//                        putString("title", strTilte)
                        putBoolean("ist", data.type == AiTaskType.RMBG)
                    }

                }

            }
        }
    }
}
