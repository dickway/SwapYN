package com.face.adapter.other

import android.graphics.Bitmap
import com.face.R
import com.face.bean.TargetImgBean
import com.face.databinding.ItemUploadFaceBinding
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder

class UploadFaceAdapter : BaseAdapter<TargetImgBean, ItemUploadFaceBinding>(
    R.layout.item_upload_face, TargetImgBean.differCallback
) {

    var selectIndex = -1
        set(value) {
            if (value != field) {
                val origin = field
                field = value
                notifyItemChanged(origin)
                notifyItemChanged(field)
            }
        }

    fun addBitmaps(bitmaps: List<Bitmap>) {
        val listData = mutableListOf<TargetImgBean>()
        bitmaps.take(4).forEach {
            listData.add(TargetImgBean(mBitmap = it))
        }
        submitList(listData)
    }

    override fun onBindData(
        holder: BaseHolder<ItemUploadFaceBinding>,
        binding: ItemUploadFaceBinding,
        data: TargetImgBean?,
        position: Int
    ) {
        holder.binding.apply {
            isSelected = selectIndex == position
            imgFace.setImageBitmap(data?.mBitmap)
            root.setOnClickListener {
                selectIndex = position
                onItemClick?.invoke(it, data, position)
            }
        }
    }
}
