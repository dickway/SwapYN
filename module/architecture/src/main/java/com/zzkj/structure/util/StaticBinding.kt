package com.zzkj.structure.util

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import com.blankj.utilcode.util.LogUtils
import com.zzkj.structure.util.ktx.applyInserts
import com.zzkj.structure.util.ktx.getColorX
import com.zzkj.structure.util.ktx.singleClick
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

/**
 * @author lmk
 * @date 2022/2/16
 * @description
 */
class StaticBinding {

    companion object {

        @JvmStatic
        @BindingAdapter("bindVisible")
        fun bindVisible(view: View, visible: Boolean) {
            if (view.isVisible != visible) {
                view.isVisible = visible
            }
        }

        @JvmStatic
        @BindingAdapter("bindInVisible")
        fun bindInVisible(view: View, invisible: Boolean) {
            if (view.isInvisible != invisible) {
                // IllegalArgumentException: Tmp detached view should be removed from RecyclerView before it can be recycled
                if (view is RecyclerView && view.isAnimating) {
                    view.itemAnimator?.isRunning { view.isInvisible = invisible }
                } else {
                    view.isInvisible = invisible
                }
            }
        }

        @JvmStatic
        @BindingAdapter("bindSelected")
        fun bindSelected(view: View, select: Boolean) {
            if (view.isSelected != select) {
                view.isSelected = select
            }
        }

        @JvmStatic
        @BindingAdapter("bindAlpha")
        fun bindAlpha(view: View, select: Boolean) {
            if (select) {
                view.alpha=0.3f
            }
        }

        @JvmStatic
        @BindingAdapter("bindEnabled")
        fun bindEnabled(view: View, enable: Boolean) {
            if (view.isEnabled != enable) {
                view.isEnabled = enable
            }
        }

        @JvmStatic
        @BindingAdapter("bindBackgroundResId")
        fun bindBackgroundResId(view: View, resId: Int) {
            view.setBackgroundResource(resId)
        }

        @JvmStatic
        @BindingAdapter(value = ["bindClickInterval", "bindSingleClick"], requireAll = false)
        fun bindSingleClick(view: View, clickInterval: Int, listener: View.OnClickListener?) {
            view.singleClick(if (clickInterval == 0) 500 else clickInterval, listener)
        }

        @JvmStatic
        @BindingAdapter("bindLongClick")
        fun bindLongClick(view: View, listener: View.OnClickListener?) {
            view.setOnLongClickListener {
                listener?.onClick(it)
                true
            }
        }

        @JvmStatic
        @BindingAdapter("bindDisallowConsumeInsert")
        fun bindDisallowConsumeInsert(view: View, disallow: Boolean) {
            if (disallow) {
                ViewCompat.setOnApplyWindowInsetsListener(view, null)
            }
        }

        @JvmStatic
        @BindingAdapter(
            value = [
                "bindAddHorizonPadding",
                "bindAddVerticalPadding",
                "bindAddHorizonMargin",
                "bindAddVerticalMargin",
                "bindAddHorizonWidth",
                "bindAddVerticalHeight",
                "bindAddStartPadding",
                "bindAddTopPadding",
                "bindAddEndPadding",
                "bindAddBottomPadding",
                "bindAddStartMargin",
                "bindAddTopMargin",
                "bindAddEndMargin",
                "bindAddBottomMargin",
                "bindAddStartWidth",
                "bindAddTopHeight",
                "bindAddEndWidth",
                "bindAddBottomHeight",
                "bindAutoScrollTop"],
            requireAll = false
        )
        fun bindSystemBarInsert(
            view: View,
            addHorizonPadding: Boolean,
            addVerticalPadding: Boolean,
            addHorizonMargin: Boolean,
            addVerticalMargin: Boolean,
            addHorizonWidth: Boolean,
            addVerticalHeight: Boolean,
            addStartPadding: Boolean?,
            addTopPadding: Boolean?,
            addEndPadding: Boolean?,
            addBottomPadding: Boolean?,
            addStartMargin: Boolean?,
            addTopMargin: Boolean?,
            addEndMargin: Boolean?,
            addBottomMargin: Boolean?,
            addStartWidth: Boolean?,
            addTopHeight: Boolean?,
            addEndWidth: Boolean?,
            addBottomHeight: Boolean?,
            autoScrollTop: Boolean,
        ) {
            view.applyInserts(
                addHorizonPadding = addHorizonPadding,
                addVerticalPadding = addVerticalPadding,
                addHorizonMargin = addHorizonMargin,
                addVerticalMargin = addVerticalMargin,
                addHorizonWidth = addHorizonWidth,
                addVerticalHeight = addVerticalHeight,
                addStartPadding = addStartPadding ?: addHorizonPadding,
                addTopPadding = addTopPadding ?: addVerticalPadding,
                addEndPadding = addEndPadding ?: addHorizonPadding,
                addBottomPadding = addBottomPadding ?: addVerticalPadding,
                addStartMargin = addStartMargin ?: addHorizonMargin,
                addTopMargin = addTopMargin ?: addVerticalMargin,
                addEndMargin = addEndMargin ?: addHorizonMargin,
                addBottomMargin = addBottomMargin ?: addVerticalMargin,
                addStartWidth = addStartWidth ?: addHorizonWidth,
                addTopHeight = addTopHeight ?: addVerticalHeight,
                addEndWidth = addEndWidth ?: addHorizonWidth,
                addBottomHeight = addBottomHeight ?: addVerticalHeight,
                autoScrollTopIfRecyclerViewUpdate = autoScrollTop
            )
        }

        @JvmStatic
        @BindingAdapter("bindStartAnimation")
        fun bindStartAnimation(view: ImageView, start: Boolean?) {
            if (start == null) {
                return
            }
            (view.drawable as? AnimationDrawable)?.apply {
                if (start) {
                    isOneShot = false
                    start()
                } else {
                    stop()
                }
            }
        }

        @JvmStatic
        @BindingAdapter("bindImgResId")
        fun bindImgResId(view: ImageView, resId: Int) {
            view.setImageResource(resId)
        }

        @JvmStatic
        @BindingAdapter("bindImgDrawable")
        fun bindImgDrawable(view: ImageView, value: Drawable) {
            view.setImageDrawable(value)
        }

        @JvmStatic
        @BindingAdapter(
            value = [
                "bindUrl",
                "bindPlaceholderResId",
                "bindPlaceholderDrawable",
                "bindErrorResId",
                "bindErrorDrawable",
                "bindCrossfade"
            ],
            requireAll = false
        )
        fun bindImgUrl(
            view: ImageView,
            url: String?,
            placeholderResId: Int?,
            placeholderDrawable: Drawable?,
            errorResId: Int?,
            errorDrawable: Drawable?,
            bindCrossfade: Boolean?
        ) {
            view.load(url) {
                placeholderDrawable?.also { placeholder(it) } ?: kotlin.run {
                    placeholderResId?.let { placeholder(it) }
                }
                errorDrawable?.also { error(it) } ?: kotlin.run {
                    errorResId?.let { error(it) }
                }
                crossfade(bindCrossfade ?: true)
            }
        }


        @JvmStatic
        @BindingAdapter("bindStringResId")
        fun bindStringResId(view: TextView, resId: Int) {
            if (resId != 0) {
                view.setText(resId)
            }
        }

        @JvmStatic
        @BindingAdapter("bindTextSize")
        fun bindTextSize(view: TextView, spSize: Float) {
            if (spSize != view.textSize) {
                view.textSize = spSize
            }
        }

        @JvmStatic
        @BindingAdapter("bindTextColorInt")
        fun bindTextColorInt(view: TextView, colorInt: Int) {
            view.setTextColor(colorInt)
        }

        @JvmStatic
        @BindingAdapter("bindTextColorResId")
        fun bindTextColorResId(view: TextView, colorResId: Int) {
            if (colorResId != 0) {
                view.setTextColor(getColorX(colorResId))
            }
        }

        @JvmStatic
        @BindingAdapter(
            value = ["bindSpanString", "bindSpanClick", "bindSpanClickStart",
                "bindSpanClickEnd", "bindSpanColor", "bindSpanShowUnderline", "bindSpanFlags"],
            requireAll = false
        )
        fun bindClickSpanned(
            textView: TextView, spanString: String?,
            listener: View.OnClickListener?,
            start: Int, end: Int, colorInt: Int,
            showUnderline: Boolean, flags: Int
        ) {
            val spannableString = SpannableString(spanString)
            if (listener != null) {
                spannableString.setSpan(object : ClickableSpan() {
                    var last = 0L
                    override fun onClick(v: View) {
                        if (System.currentTimeMillis() - last > 500) {
                            listener.onClick(v)
                            last = System.currentTimeMillis()
                        }
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        if (colorInt != 0) {
                            ds.color = colorInt
                        }
                        ds.isUnderlineText = showUnderline
                    }
                }, start, end, if (flags == 0) Spanned.SPAN_INCLUSIVE_INCLUSIVE else flags)
                textView.movementMethod = LinkMovementMethod.getInstance()
            }
            textView.text = spannableString
        }

        @JvmStatic
        @BindingAdapter("bindTextBold")
        fun bindTextBold(view: TextView, value: Boolean) {
            view.typeface = Typeface.defaultFromStyle(if (value) Typeface.BOLD else Typeface.NORMAL)
        }


        //中划线
//        @JvmStatic
//        @BindingAdapter("bindTextCenterLine")
//        fun bindTextCenterLine(view: TextView, b: Boolean) {
//            val has =
//                (view.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG
//            if (b != has) {
//                view.paintFlags = view.paintFlags xor Paint.STRIKE_THRU_TEXT_FLAG
//            }
//        }
        //中划线, 下划线
        @JvmStatic
        @BindingAdapter(value = ["bindTextCenterLine", "bindTextUnderLine"], requireAll = false)
        fun bindTextLine(view: TextView, isCenterLine: Boolean, isUnderLine: Boolean) {
            var b = (view.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG
            if (isCenterLine != b) {
                view.paintFlags = view.paintFlags xor Paint.STRIKE_THRU_TEXT_FLAG
            }
            b = (view.paintFlags and Paint.UNDERLINE_TEXT_FLAG) == Paint.UNDERLINE_TEXT_FLAG
            if (isUnderLine != b) {
                view.paintFlags = view.paintFlags xor Paint.UNDERLINE_TEXT_FLAG
            }
        }


        @JvmStatic
        @BindingAdapter(
            value = ["bindAdapter", "bindData", "bindDisableBindData"],
            requireAll = false
        )
        fun bindAdapter(
            view: RecyclerView,
            adapter: RecyclerView.Adapter<*>?,
            data: List<Nothing>?,
            disableBindData: Boolean
        ) {
            if (view.adapter != adapter) {
                view.adapter = adapter
            }
            if (!disableBindData) {
                view.adapter?.apply {
                    if (this is ListAdapter<*, *>) {
                        submitList(data)
                    }
                }
            }
        }

        @JvmStatic
        @BindingAdapter("bindRefreshing")
        fun bindRefreshing(view: SwipeRefreshLayout, isRefreshing: Boolean) {
            view.isRefreshing = isRefreshing
        }

        @JvmStatic
        @BindingAdapter("bindRefreshListener")
        fun bindRefreshListener(
            view: SwipeRefreshLayout,
            value: SwipeRefreshLayout.OnRefreshListener
        ) {
            view.setOnRefreshListener(value)
        }

        @JvmStatic
        @BindingAdapter("bindEnableLoadMore")
        fun bindEnableLoadMore(view: SmartRefreshLayout, enable: Boolean) {
            view.setEnableLoadMore(enable)
        }

        @JvmStatic
        @BindingAdapter("bindEnableRefresh")
        fun bindEnableRefresh(view: SmartRefreshLayout, enable: Boolean) {
            view.setEnableRefresh(enable)
        }

        @JvmStatic
        @BindingAdapter("bindNoMoreData")
        fun bindNoMoreData(view: SmartRefreshLayout, value: Boolean) {
            view.setNoMoreData(value)
        }

        @JvmStatic
        @BindingAdapter("bindRefreshListener")
        fun bindRefreshListener(view: SmartRefreshLayout, listener: OnRefreshListener) {
            view.setOnRefreshListener(listener)
        }

        @JvmStatic
        @BindingAdapter("bindLoadMoreListener")
        fun bindLoadMoreListener(view: SmartRefreshLayout, listener: OnLoadMoreListener) {
            view.setOnLoadMoreListener(listener)
        }

        @JvmStatic
        @BindingAdapter("bindFinishLoadMore")
        fun bindFinishLoadMore(view: SmartRefreshLayout, value: Boolean) {
            if (value) {
                view.finishLoadMore(value)
            }
        }

        @JvmStatic
        @BindingAdapter("bindFinishRefresh")
        fun bindFinishRefresh(view: SmartRefreshLayout, value: Boolean) {
            if (value) {
                view.finishRefresh(value)
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["bindRefreshing", "bindRefreshAnimationOnly"], requireAll = false)
        fun bindRefreshing(view: SmartRefreshLayout, value: Boolean, animationOnly: Boolean?) {
            if (value) {
                if (animationOnly == true) {
                    view.autoRefreshAnimationOnly()
                } else {
                    view.autoRefresh()
                }
            } else {
                view.finishRefresh()
            }
        }

        @JvmStatic
        @BindingAdapter("bindNavigationClick")
        fun bindRefreshListener(
            view: Toolbar,
            value: View.OnClickListener
        ) {
            view.setNavigationOnClickListener(value)
        }

    }
}