package com.zzkj.structure.net

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.blankj.utilcode.util.LogUtils
import com.zzkj.structure.util.singleToast
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.CancellationException

open class BaseRepository {

    suspend fun <T> executeHttp(block: suspend () -> ApiResponse<T>): ApiResponse<T> {
        runCatching {
            block.invoke()
        }.onSuccess { data: ApiResponse<T> ->
            return handleHttpResponse(data)
        }.onFailure { e ->
            return handleError(e)
        }
        return handleError(ImpossibleException())
    }

    /**
     * D 接口返回数据
     * T paging3 返回 list 数据
     */
    fun <D : Any, T : Any> executePagingHttp(
        pageSize: Int = getDefaultPageSize(),
        initialKey: Int = 1,
        transform: (D?) -> List<T> = {
            if (it is List<*>) (it as? List<T>) ?: listOf() else listOf()
        },
        prevKey: (currPage: Int) -> Int? = {
            if (it <= 1) null else it - 1
        },
        nextKey: (currPage: Int, loadSize: Int, data: D?) -> Int? = { currPage, loadSize, data ->
            if (data is List<*> && data.size == loadSize) currPage + 1 else null
        },
        repository: suspend (page: Int, pageSize: Int) -> ApiResponse<D>
    ) = Pager(
        PagingConfig(pageSize, initialLoadSize = pageSize, prefetchDistance = pageSize / 2),
        initialKey,
    ) {
        object : PagingSource<Int, T>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
                val currPage = params.key ?: 1
                return try {
                    val response = repository.invoke(currPage, params.loadSize)
                    if (response._mState == API_STATE_FAIL) {
                        LoadResult.Error(Exception(response.mMsg))
                    } else {
                        LoadResult.Page(
                            transform.invoke(response.mData),
                            prevKey.invoke(currPage),
                            nextKey.invoke(currPage, params.loadSize, response.mData)
                        )
                    }
                } catch (e: Throwable) {
                    val throwable = convertThrowable(e)
                    if (throwable !is CancellationException) {
                        singleToast(throwable.localizedMessage)
                        LogUtils.e("${throwable.javaClass.name}:${throwable.localizedMessage}")
                    }
                    LoadResult.Error(throwable)
                }
            }

            override fun getRefreshKey(state: PagingState<Int, T>): Int? {
                return null
            }
        }
    }.flow

    protected fun convertThrowable(e: Throwable): Throwable {
        val throwable: Throwable = when (e) {
            is UnknownHostException -> UnknownHostException("Failed to resolve host").apply { initCause(e) }
            is SocketTimeoutException -> SocketTimeoutException("Timeout").apply { initCause(e) }
            is ConnectException -> ConnectException("Connection fail").apply { initCause(e) }
            is IOException -> IOException("Connection fail, IOException", e)
            else -> e
        }
//        val throwable: Throwable = when (e) {
//            is UnknownHostException -> {
//                UnknownHostException("Failed to resolve host")
//            }
//
//            is SocketTimeoutException -> {
//                SocketTimeoutException("Timeout")
//            }
//
//            is ConnectException -> {
//                ConnectException("Connection fail")
//            }
//
//            is IOException -> {
//                IOException("Connection fail, IOException")
//            }
//
//            else -> {
//                e
//            }
//        }
        return throwable
    }

    private fun <T> handleHttpResponse(data: ApiResponse<T>): ApiResponse<T> {
        return if (data.mIsSuccess) {
            data.apply { _mState = API_STATE_SUCCESS }
        } else {
            handleCodeError(data.mCode, data.mMsg)
            data.apply { _mState = API_STATE_FAIL }
        }
    }

    private fun <T> handleError(e: Throwable): ApiResponse<T> {
        LogUtils.e("${e.javaClass.name}:${e.localizedMessage}")
        return ApiResponse<T>(API_STATE_FAIL).apply {
            val exception=convertThrowable(e)
            mMsg = exception.localizedMessage
            mError = exception
        }
    }

    open fun handleCodeError(code: Int, msg: String?) {

    }

    open fun getDefaultPageSize() = 40

    inner class ImpossibleException : Exception()
}