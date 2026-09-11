package com.a.view

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.a.R
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.singleClick

class ALanguagePopup(
    private val activity: Activity,
    private val selectedLanguage: String,
    private val onSelected: (String) -> Unit
) {
    private var window: PopupWindow? = null

    fun show(anchor: View) {
        val list = LinearLayout(activity).apply { orientation = LinearLayout.VERTICAL }
        val languages = languages(activity)
        languages.forEach { (code, label) ->
            val row = activity.layoutInflater.inflate(R.layout.item_a_language, list, false)
            row.findViewById<TextView>(R.id.tvLanguageName).text = label
            row.isSelected = code == selectedLanguage
            row.findViewById<ImageView>(R.id.imgSelected).visibility =
                if (row.isSelected) View.VISIBLE else View.INVISIBLE
            row.singleClick {
                dismiss()
                onSelected(code)
            }
            list.addView(row)
        }
        val scroll = ScrollView(activity).apply {
            isVerticalScrollBarEnabled = false
            setBackgroundResource(R.drawable.a_bg_settings_popup)
            clipToOutline = true
            addView(list)
        }
        val frame = Rect()
        anchor.getWindowVisibleDisplayFrame(frame)
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)
        val availableHeight = (frame.bottom - location[1] - anchor.height - 8.dp).coerceAtLeast(48.dp)
        window = PopupWindow(
            scroll,
            minOf(222.dp, frame.width() - 16.dp),
            minOf(288.dp, availableHeight),
            true
        ).apply {
            setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.a_bg_settings_popup))
            isOutsideTouchable = true
            elevation = 0f
            inputMethodMode = PopupWindow.INPUT_METHOD_NOT_NEEDED
            showAsDropDown(anchor, 0, 0, Gravity.END)
        }
        val selectedIndex = languages.indexOfFirst { it.first == selectedLanguage }
        if (selectedIndex >= 6) scroll.post { scroll.scrollTo(0, list.getChildAt(selectedIndex).top) }
    }

    fun dismiss() {
        window?.dismiss()
        window = null
    }

    companion object {
        fun languages(context: Context): List<Pair<String, String>> =
            context.resources.getStringArray(R.array.a_settings_language_codes)
                .zip(context.resources.getStringArray(R.array.a_settings_language_names))

        fun labelFor(context: Context, code: String): String =
            languages(context).firstOrNull { it.first == code }?.second
                ?: context.getString(com.face.R.string.language_en)
    }
}
