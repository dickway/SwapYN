package com.zzkj.structure.util.ktx

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

inline fun <reified T : Activity> Activity.openActivity(
    requestCode: Int = -1,
    noinline block: (Bundle.() -> Unit)? = null
) {
    val intent = Intent(this, T::class.java)
    if (block != null) {
        intent.putExtras(Bundle().apply { this.block() })
    }
    if (requestCode == -1) {
        startActivity(intent)
    } else {
        startActivityForResult(intent, requestCode)
    }
}

fun ComponentActivity.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = lifecycleScope.launch(context, start, block)

fun ComponentActivity.launchWhenCreated(
    block: suspend CoroutineScope.() -> Unit
): Job = lifecycleScope.launchWhenCreated(block)

fun ComponentActivity.launchWhenStarted(
    block: suspend CoroutineScope.() -> Unit
): Job = lifecycleScope.launchWhenStarted(block)

fun ComponentActivity.launchWhenResumed(
    block: suspend CoroutineScope.() -> Unit
): Job = lifecycleScope.launchWhenResumed(block)

fun <T> Activity.intentExtras(name: String) = lazy<T?> {
    intent?.extras?.get(name) as? T
}

fun <T> Activity.intentExtras(name: String, default: T) = lazy {
    (intent?.extras?.get(name) as? T) ?: default
}