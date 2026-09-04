package com.zzkj.structure.net

/**
 * @date 2022/11/4
 * @description
 */
sealed class LoadingState {
    object None : LoadingState()

    sealed class Loading(open val time: Long = System.currentTimeMillis()) : LoadingState()
    sealed class Finish(open val time: Long = System.currentTimeMillis()) : LoadingState()

    data class Refresh(override val time: Long = System.currentTimeMillis()) : Loading(time)
    data class LoadMore(override val time: Long = System.currentTimeMillis()) : Loading(time)

    data class Success(override val time: Long = System.currentTimeMillis()) : Finish(time)
    data class Fail(
        override val time: Long = System.currentTimeMillis(),
        val msg: String? = null
    ) : Finish(time)

    /**
     * @param refreshInterval Long 刷新间隔(ms), 负数为成功后就不刷新了
     * @return Boolean 是否应该刷新了
     */
    fun needRefresh(refreshInterval: Long = -1) = this is Fail || this is None
            || (refreshInterval > 0 && this is Success
            && System.currentTimeMillis() - this.time > refreshInterval)
}
