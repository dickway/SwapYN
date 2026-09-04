package com.face.adapter.share

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.face.R
import com.face.bean.DynamicAddBean
import com.face.bean.ShareZoneBean
import com.face.databinding.ItemPointHistBinding
import com.face.util.SPUtils
import com.luck.picture.lib.utils.DateUtils
import com.zzkj.structure.base.adapter.BaseAdapter
import com.zzkj.structure.base.adapter.BaseHolder
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.moshi.MoshiHelper
import com.zzkj.structure.util.toast
import kotlin.getValue


open class PointsHistAdapter: BaseAdapter<ShareZoneBean, ItemPointHistBinding>(
    R.layout.item_point_hist, ShareZoneBean.differCallback
) {

    @SuppressLint("SetTextI18n")
    override fun onBindData(
        holder: BaseHolder<ItemPointHistBinding>,
        binding: ItemPointHistBinding,
        data: ShareZoneBean?,
        position: Int
    ) {
        holder.binding.apply {
            data?.apply {
                tvName.text= when(data.type){
                    "adsReward"-> tvName.context.getString(R.string.watch_ads_to_get_rewards)
                    "signReward"-> tvName.context.getString(R.string.check_in_reward_share)
                    "pay"-> tvName.context.getString(R.string.purchase_aipoints)
                    "Download"-> tvName.context.getString(R.string.download_template)
                    "SwapFaceAD"-> tvName.context.getString(R.string.ad_free_deduction_swapfacead)
                    "PaperworkAD"-> tvName.context.getString(R.string.ad_free_deduction_paperworkad)
                    "SwapFace"-> tvName.context.getString(R.string.use_template)
                    "taskBack"-> tvName.context.getString(R.string.use_task)
                    "AgeAD"-> tvName.context.getString(R.string.ad_free_deduction_agead)
                    "LiveAD"-> tvName.context.getString(R.string.ad_free_deduction_livead)
                    "SysAdd"-> tvName.context.getString(R.string.add_aipoints_via_backend)
                    "ShareMediaReward"-> tvName.context.getString(R.string.shared_template_reward)
                    "cancelTask"-> tvName.context.getString(R.string.cancel_task)
                    "ExchangeGift"-> tvName.context.getString(R.string.gift_points)
                    "lifeTime"-> tvName.context.getString(R.string.lifetime_points)
                    "ExchangeVIP"-> tvName.context.getString(R.string.exchange_points)
                    "img2kiss"-> tvName.context.getString(R.string.txt_img2kiss)
                    "change_clothes"-> tvName.context.getString(R.string.txt_change_clothes)
                    "img2hug"-> tvName.context.getString(R.string.txt_img2hug)
                    "rating_reward"-> tvName.context.getString(R.string.txt_rating)
                    "img2video"-> tvName.context.getString(R.string.spicy_moves)
                    "taskBack"-> tvName.context.getString(R.string.task_failed)
                    "point_for_media"->tvName.context.getString(R.string.buy_quota)
                    else -> {
                        val toolList: List<DynamicAddBean>? = MoshiHelper.convertJsonToList(SPUtils.listDynamicTools)
                        val dynamicAddBean = toolList?.find { it.taskType == data.type } ?: DynamicAddBean()
                        if (dynamicAddBean.id!=-1) {
                            dynamicAddBean.getSheepPointsName()
                        }else{
                            data.type
                        }
                    }
                }
                tvTime.text=DateUtils.getYearDataFormat(data.createTime)
                tvScore.text="${if(state>=0){
                    tvScore.setTextColor(ContextCompat.getColor(tvScore.context,R.color.colorBgFFCB80))
                    "+"
                }else{
                    tvScore.setTextColor(ContextCompat.getColor(tvScore.context,R.color.colorWhite))
                    "-"
                }}$num"

            }
        }
    }

}
