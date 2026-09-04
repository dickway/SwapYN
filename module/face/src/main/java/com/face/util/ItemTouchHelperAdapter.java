package com.face.util;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperAdapter {

    //数据交换
    void onItemMove(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target);

    //drag或者swipe选中
    void onItemSelect(RecyclerView.ViewHolder source);

    //状态清除
    void onItemClear(RecyclerView.ViewHolder source);

}
