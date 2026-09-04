package com.zzkj.structure.net

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author 再战科技
 * @date 2022/11/4
 * @description
 */
internal class LoadingStateTest {

    @Test
    fun main() {
        val s: LoadingState = LoadingState.Refresh()
        val b = s is LoadingState.Finish
        assertEquals(true, b)
    }
}