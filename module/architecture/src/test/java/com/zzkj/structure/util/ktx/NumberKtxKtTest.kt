package com.zzkj.structure.util.ktx

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author 再战科技
 * @date 2022/9/26
 * @description
 */
internal class NumberKtxKtTest {

    @Test
    fun saveDecimals() {
        assertEquals("0.2", 0.2403.saveDecimals(1))
        assertEquals("0.3", 0.2503.saveDecimals(1))
        assertEquals("0.25", 0.2503.saveDecimals(2))
    }
}