package com.zzkj.structure.util.ktx

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat

/**
 * @author 再战科技
 * @date 2022/7/20
 * @description
 */

inline fun <reified T : Activity> Context.openActivity(
    noinline block: (Bundle.() -> Unit)? = null
) {
    val intent = Intent(this, T::class.java)
    if (block != null) {
        intent.putExtras(Bundle().apply { this.block() })
    }
    ContextCompat.startActivity(this, intent, null)
}