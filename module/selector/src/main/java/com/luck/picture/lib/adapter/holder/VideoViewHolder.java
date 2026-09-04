package com.luck.picture.lib.adapter.holder;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.style.SelectMainStyle;
import com.luck.picture.lib.utils.DateUtils;
import com.luck.picture.lib.utils.StyleUtils;

import java.util.Locale;

/**
 * @author：luck
 * @date：2021/11/20 3:59 下午
 * @describe：VideoViewHolder
 */
public class VideoViewHolder extends BaseRecyclerMediaHolder {
    private final TextView tvDuration;
    private final TextView tvSize;
    private final View hintView;

    SharedPreferences spf = mContext.getSharedPreferences("AI_FACE", MODE_PRIVATE);
    float videoScale = spf.getFloat("videoScale", 0.35f);
    int videoSize = spf.getInt("voideSize", 200);
    public VideoViewHolder(@NonNull View itemView, SelectorConfig config) {
        super(itemView, config);
        tvDuration = itemView.findViewById(R.id.tv_duration);
        tvSize = itemView.findViewById(R.id.tv_size);
        hintView = itemView.findViewById(R.id.hintView);
        SelectMainStyle adapterStyle = selectorConfig.selectorStyle.getSelectMainStyle();
        int drawableLeft = adapterStyle.getAdapterDurationDrawableLeft();
        if (StyleUtils.checkStyleValidity(drawableLeft)) {
            tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableLeft, 0, 0, 0);
        }
        int textSize = adapterStyle.getAdapterDurationTextSize();
        if (StyleUtils.checkSizeValidity(textSize)) {
            tvDuration.setTextSize(textSize);
        }
        int textColor = adapterStyle.getAdapterDurationTextColor();
        if (StyleUtils.checkStyleValidity(textColor)) {
            tvDuration.setTextColor(textColor);
        }

        int shadowBackground = adapterStyle.getAdapterDurationBackgroundResources();
        if (StyleUtils.checkStyleValidity(shadowBackground)) {
            tvDuration.setBackgroundResource(shadowBackground);
        }

        int[] durationGravity = adapterStyle.getAdapterDurationGravity();
        if (StyleUtils.checkArrayValidity(durationGravity)) {
            if (tvDuration.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) tvDuration.getLayoutParams()).removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                for (int i : durationGravity) {
                    ((RelativeLayout.LayoutParams) tvDuration.getLayoutParams()).addRule(i);
                }
            }
        }
    }

    @Override
    public void bindData(LocalMedia media, int position) {
        super.bindData(media, position);
        tvDuration.setText(DateUtils.formatDurationTime(media.getDuration()));
        tvSize.setText(formatFileSize(media.getSize()));
        int timeS = (int) (media.getDuration() / 1000);
        float sizeS = media.getSize() / (1024.00f * 1024);
        media.setEligible(sizeS / timeS);
        if (videoScale < media.getEligible() && media.getSize() > (videoSize * 1024 * 1024)) {
            hintView.setVisibility(View.VISIBLE);
        } else {
            hintView.setVisibility(View.GONE);
        }

    }

    /**
     * 格式化文件大小，将字节转换为 KB/MB/GB
     *
     * @param sizeInBytes 文件大小（字节）
     * @return 格式化后的字符串
     */
    public static String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 1024 * 1024) {
            return String.format(Locale.CHINESE, "%dKB", sizeInBytes / 1024);
        } else if (sizeInBytes < 1024 * 1024 * 1024) {
            return String.format(Locale.CHINESE, "%dMB", sizeInBytes / (1024 * 1024));
        } else {
            return String.format(Locale.CHINESE, "%.1fGB", sizeInBytes / (1024.0 * 1024 * 1024));
        }
    }
}
