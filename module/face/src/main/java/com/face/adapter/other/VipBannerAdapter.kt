package com.face.adapter.other

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.face.ui.App
import com.face.bean.VipBannerBean
import com.face.databinding.ItemBannerVipBinding
import com.youth.banner.adapter.BannerAdapter

class VipBannerAdapter : BannerAdapter<VipBannerBean, RecyclerView.ViewHolder>(null) {


    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return Holder(
            ItemBannerVipBinding.inflate(
                LayoutInflater.from(
                    parent?.context ?: App.INSTANCE
                ), parent, false
            )
        )
    }


    override fun onBindView(
        holder: RecyclerView.ViewHolder?,
        data: VipBannerBean?,
        position: Int,
        size: Int
    ) {
        if (data == null) {
            return
        }
        if (holder is Holder)
            holder.binding.apply {
                imgCover.setImageResource(data.src)
            }
    }

    class Holder(val binding: ItemBannerVipBinding) : RecyclerView.ViewHolder(binding.root)


}