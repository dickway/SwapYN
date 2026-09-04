package com.face.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.postDelayed
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.repeatOnLifecycle
import com.key.adapter.FeedbackMsgAdapter
import com.face.bean.FeedbackMsgWrapBean
import com.apkfuns.logutils.LogUtils
import com.face.net.Repository
import com.face.BR
import com.face.R
import com.face.databinding.ActivityFeedbackMsgBinding
import com.face.util.EventUtil
import com.face.util.NotificationUtil
import com.face.util.SPUtils
import com.zzkj.structure.base.BaseApp
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.base.DataBindingArguments
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.util.ktx.intentExtras
import com.zzkj.structure.util.ktx.launch
import com.zzkj.structure.util.ktx.openActivity
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class FeedbackMsgActivity : BaseBindingActivity<ActivityFeedbackMsgBinding, BaseViewModel>(
    R.layout.activity_feedback_msg,
    BaseViewModel::class.java
) {

    val adapter = FeedbackMsgAdapter { url, _ ->
        if (url.isNullOrEmpty()) {
            toast(R.string.fail)
        }
//        else if (isImage) {
////            PreviewPictureActivity.jump(this, url)
//        } else {
////            ARouter.getInstance().build(Router.ROUTE_LOCAL_PLAYER).withString("uri", url)
////                .navigation()
//        }
    }
    val data = MutableLiveData<List<FeedbackMsgWrapBean>>()
    val refreshing = MutableLiveData(true)
    val input = MutableLiveData("")
    val enablePost = input.map { !it.isNullOrBlank() }
    val feedbackState by intentExtras("feedbackState", 0)
    private val feedbackId by intentExtras("feedbackId", 0)

    var isImage = true
    private var type = ""
    private var subType = ""
    private var size = 0F
    private lateinit var pickPictureLauncher: ActivityResultLauncher<Array<String>>

    companion object {
        fun jump(
            context: Context, feedbackId: Int, feedbackState: Int
        ) {
            context.openActivity<FeedbackMsgActivity> {
                putInt("feedbackId", feedbackId)
                putInt("feedbackState", feedbackState)
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        NotificationUtil.deleteNotification(feedbackId)
        EventUtil.inPage("feedback_msg")
        pickPictureLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it != null) {
                handleUri(it)
            }
        }
        launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (isActive) {
                    refresh(false)
                    delay(30000)
                }
            }
        }
    }

    fun refresh(autoScrollToBottom: Boolean = true) {
        launchRequestOnIO({ Repository.getFeedbackMsg(feedbackId) }) {
            onSuccess = {
                LogUtils.i(it)
                data.value = it
                if (autoScrollToBottom) {
                    mBinding?.apply {
                        rv.postDelayed(500L) {
                            rv.smoothScrollToPosition(0)
                        }
                    }
                }
            }
            onComplete = { refreshing.value = false }
        }
    }

    fun onFileClick() {
        pickPictureLauncher.launch(arrayOf("image/*", "video/*"))
    }

    fun onPostClick() {
        sendMsg(input.value, null)
    }

    private fun handleUri(uri: Uri) {
        showLoading()
        launch(Dispatchers.IO) {
            queryFileInfo(uri)
            if (size > 10) {
                toast(R.string.feedback_upload_txt)
                dismissLoading()
                return@launch
            }
            val file = copyFile(
                uri, "feedback_${System.currentTimeMillis()}.$subType"
            )
            launchRequestOnIO({ Repository.uploadFile(file, type) }) {
                onSuccess = {
                    LogUtils.i(it)
                    if (it?.src.isNullOrBlank()) {
                        toast(R.string.fail)
                    } else {
                        sendMsg(null, it?.src, isImage)
                    }
                }

                onFailed = { _, _, m ->
                    toast(m)
                }

                onComplete = {
                    dismissLoading()
                }
            }
        }
    }

    private fun sendMsg(content: String?, url: String?, isImage: Boolean = false) {
        val imgUrl = if (isImage) url else null
        val videoUrl = if (!isImage) url else null
        launchRequestOnIO({ Repository.sendFeedbackMsg(feedbackId, content, imgUrl, videoUrl) }) {
            onSuccess = {
                refresh()
                if (!content.isNullOrBlank()) input.value = ""
            }
            onComplete = {
                dismissLoading()
            }
        }
    }

    //type:image/jpeg, size:3553049
    private fun queryFileInfo(uri: Uri) {
        BaseApp.INSTANCE.contentResolver.query(
            uri,
            arrayOf(MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.SIZE),
            null,
            null,
            null
        )?.use {
            it.moveToFirst()
            type = it.getString(0)
            isImage = type.contains("image", true)
            subType = type.substring(type.indexOf('/') + 1)
            size = it.getString(1).toFloat() / 1024F / 1024F
            LogUtils.i("isImage:$isImage,type:$type, size:$size")
        }
    }

    private fun copyFile(it: Uri, fileName: String): File {
        val path = (BaseApp.INSTANCE.externalCacheDir
            ?: BaseApp.INSTANCE.cacheDir).path + File.separatorChar + fileName
        val file = File(path)
        BaseApp.INSTANCE.contentResolver.openInputStream(it)?.use { input ->
            BufferedOutputStream(FileOutputStream(file)).use { bos ->
                val bytes = ByteArray(1024 * 10)
                var length = input.read(bytes)
                while (length > 0) {
                    bos.write(bytes, 0, length)
                    length = input.read(bytes)
                }
                bos.flush()
            }
        }
        return file
    }

    override fun getDataBindingArguments(): DataBindingArguments {
        return DataBindingArguments(BR.handler, this)
    }
}