package com.face.viewmodel.activity

import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.apkfuns.logutils.LogUtils
import com.face.net.Repository
import com.face.R
import com.face.util.GVM
import com.zzkj.structure.base.BaseApp
import com.zzkj.structure.base.BaseViewModel
import com.zzkj.structure.base.ViewEffect
import com.zzkj.structure.net.launchRequestOnIO
import com.zzkj.structure.net.launchRequestWithLoadingOnIO
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.ktx.isEmail
import com.zzkj.structure.util.toast
import kotlinx.coroutines.Dispatchers
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class FeedbackViewModel : BaseViewModel() {
    val email = MutableLiveData("")
    val content = MutableLiveData("")
    val enableFeedBack = MediatorLiveData<Boolean>()
    val feedBackFinish = MutableLiveData(false)
    val fileUri = MutableLiveData<Uri?>(null)
    var isImage = true
    private var type = ""
    private var subType = ""
    private var size = 0F
    private var fileUrl = ""

    private val enableEmailObserver = Observer<String?> {
        enableFeedBack.value = email.value.isEmail()
                && !content.value.isNullOrBlank()
    }

    init {
        enableFeedBack.addSource(email, enableEmailObserver)
        enableFeedBack.addSource(content, enableEmailObserver)
    }

    companion object {
        const val VIEW_EFFECT_FEEDBACK_SUCCESS = 1
        const val VIEW_EFFECT_UPDATE_FILE_SUCCESS = 2
    }

    fun onClearInputClick() {
        email.value = ""
    }

    fun deleteFile() {
        fileUri.value = null
        fileUrl = ""
    }

    fun saveFeedback() {
        launchRequestWithLoadingOnIO({
            Repository.saveFeedback(
                email = email.value ?: "",
                content = content.value ?: "",
                videoId=GVM.INSTANT.ofLateSwap.value,
                fileUrl = fileUrl,
                fileIsImage = isImage
            )
        }) {
            onSuccess = {
                viewEffectLiveData.postValue(
                    ViewEffect.SimpleCommonEffect(VIEW_EFFECT_FEEDBACK_SUCCESS)
                )
            }
            onFailed = { _, _, m ->
                toast(m)
            }
        }
    }

    //type:image/jpeg, size:3553049
    fun handleUri(uri: Uri) {
        showLoading(cancelable = false)
        launch(Dispatchers.IO) {
            queryFileInfo(uri)
            if (size > 10) {
                toast(R.string.feedback_upload_txt)
                dismissLoading()
                return@launch
            }
            fileUri.postValue(uri)
            val file = copyFile(
                uri,
                "feedback_${System.currentTimeMillis()}.$subType"
            )
            launchRequestOnIO({ Repository.uploadFile(file, type) }) {
                onSuccess = {
                    LogUtils.i(it)
                    fileUrl = it?.src ?: ""
                    if (fileUrl.isBlank()) {
                        toast(getStringX(R.string.fail))
                    } else {
                        viewEffectLiveData.postValue(
                            ViewEffect.SimpleCommonEffect(VIEW_EFFECT_UPDATE_FILE_SUCCESS)
                        )
                    }
                }

                onFailed = { _, _, m ->
                    toast(m)
                    fileUri.postValue(null)
                }

                onComplete = {
                    dismissLoading()
                }
            }
        }
    }

    //type:image/jpeg, size:3553049
    private fun queryFileInfo(uri: Uri) {
        BaseApp.INSTANCE.contentResolver.query(
            uri,
            arrayOf(MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.SIZE),
            null, null, null
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
}