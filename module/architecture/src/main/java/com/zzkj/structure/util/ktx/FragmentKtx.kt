package com.zzkj.structure.util.ktx

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @author lmk
 * @date 2022/4/20
 * @description
 */
fun <T> Fragment.getNavigationResult(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun <T> Fragment.setNavigationResult(result: T, key: String = "result") {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}

fun <T> Fragment.addBackPressCallback(black: () -> Unit) =
    activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
        black.invoke()
    }

inline fun <reified T : Activity> Fragment.openActivity(
    requestCode: Int = -1,
    noinline block: (Bundle.() -> Unit)? = null
) {
    val intent = Intent(context, T::class.java)
    if (block != null) {
        intent.putExtras(Bundle().apply { this.block() })
    }
    if (requestCode == -1) {
        startActivity(intent)
    } else {
        startActivityForResult(intent, requestCode)
    }
}

fun Fragment.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job = lifecycleScope.launch(context, start, block)

fun Fragment.launchWhenCreated(
    block: suspend CoroutineScope.() -> Unit
): Job = lifecycleScope.launchWhenCreated(block)

fun Fragment.launchWhenStarted(
    block: suspend CoroutineScope.() -> Unit
): Job = lifecycleScope.launchWhenStarted(block)

fun Fragment.launchWhenResumed(
    block: suspend CoroutineScope.() -> Unit
): Job = lifecycleScope.launchWhenResumed(block)

fun <T : Fragment> T.withArguments(vararg pairs: Pair<String, *>) = apply {
    arguments = bundleOf(*pairs)
}

fun <T> Fragment.arguments(key: String) = lazy {
    arguments?.get(key) as? T
}

fun <T> Fragment.arguments(key: String, default: T) = lazy {
    (arguments?.get(key) as? T) ?: default
}

