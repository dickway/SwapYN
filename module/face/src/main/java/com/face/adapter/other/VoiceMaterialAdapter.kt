package com.face.adapter.other

import android.graphics.drawable.AnimationDrawable
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.face.R
import com.face.bean.ToolTaskBean
import com.face.databinding.ItemSoundMaterialBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.TimeUtil
import com.zzkj.structure.util.ktx.singleClick

class VoiceMaterialAdapter : BaseAdapter<ToolTaskBean, ItemSoundMaterialBinding>(
    R.layout.item_sound_material, object : DiffUtil.ItemCallback<ToolTaskBean>() {
        override fun areItemsTheSame(
            oldItem: ToolTaskBean,
            newItem: ToolTaskBean
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: ToolTaskBean,
            newItem: ToolTaskBean
        ): Boolean {
            return false
        }
    }, setLongClickListener = true
) {
    var onSingOperateClick: ((ToolTaskBean?) -> Unit)? = null

    var onSingSelectClick: ((ToolTaskBean?, Int) -> Unit)? = null


    var selectIndex = -1
        set(value) {
            if (value != field) {
                val origin = field
                field = value
                notifyItemChanged(origin)
                notifyItemChanged(field)
            }
        }


    var playIndex = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindData(
        holder: BaseHolder<ItemSoundMaterialBinding>,
        binding: ItemSoundMaterialBinding,
        data: ToolTaskBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                imgView.visibility = View.GONE
                if (selectIndex == position) {
                    imgView.visibility = View.VISIBLE
                }
                isPlay = true
                soundName.text = data.name
                soundImg.visibility = View.VISIBLE
                if (data.state == 1 || data.state == 0) {
                    isTask = true
                    soundImg.visibility = View.GONE
                    imgCover.setImageResource(com.key.R.drawable.img_icon_audiofile4)
                } else if (data.state == 3) {
                    isTask = true
                    val fName = TimeUtil.getFormatDate(
                        data.createTime,
                        TimeUtil.DEFAULT_DATE_TIME_DATA
                    )
                    soundName.text = "Task failed ${fName}"
                    imgCover.setImageResource(com.key.R.drawable.img_icon_audiofile3)
                } else {
                    isTask = false
                    if (playIndex != -1 && selectIndex == position) isPlay = false
                    imgCover.setImageResource(com.key.R.drawable.img_icon_audiofile2)
                }
                val mAnimationDrawable = imgPlayAnim.background as AnimationDrawable?
                mAnimationDrawable?.start()
                imgCover.singleClick {
                    onSingSelectClick?.invoke(data, position)
                }
                imgCover.setOnLongClickListener {
                    onItemLongClick?.invoke(imgCover, data, position)
                    false
                }

                soundImg.singleClick {
                    onSingOperateClick?.invoke(data)
                }
            }
        }
    }
}
