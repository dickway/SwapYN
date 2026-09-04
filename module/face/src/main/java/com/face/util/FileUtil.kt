package com.face.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.face.ui.App
import com.face.key.Constants
import com.zzkj.structure.util.ktx.getStringX
import com.zzkj.structure.util.toast
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import androidx.core.net.toUri
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.zzkj.structure.util.ktx.getScreenHeight
import com.zzkj.structure.util.ktx.getScreenWidth


/**
 * @modifier
 * @createDate 2023/10/22
 */
object FileUtil {

    fun writeFile(fileName: String, input: InputStream?): File? {
        if (input == null) return null
        val dir = File(Constants.savePath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        //创建文件
        val file = File(Constants.savePath, fileName)
        if (file.exists()) {
            file.delete()
        }


        try {
            FileOutputStream(file).use { fos ->
                input.use { ins ->
                    val b = ByteArray(1024)
                    var len: Int
                    while (ins.read(b).also { len = it } != -1) {
                        fos.write(b, 0, len)
                    }
                    fos.flush()
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun copyFileToDownloadDir(oldPath: String, targetDirName: String): Uri? {
        try {
            val oldFile = File(oldPath)
            //设置目标文件的信息
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DESCRIPTION, targetDirName)
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, targetDirName)
            values.put(MediaStore.Files.FileColumns.TITLE, targetDirName)
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, getMimeType(oldPath))
            val relativePath =
                Environment.DIRECTORY_DOWNLOADS + File.separator + getStringX(com.key.R.string.app_ai_name) + File.separator
            //目录不存在时自动创建
            val newFile = File(relativePath)
            if (!newFile.exists()) {
                newFile.mkdirs()
            }
            values.put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
            val downloadUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI

            val resolver = App.INSTANCE.applicationContext.contentResolver
            val insertUri = resolver.insert(downloadUri, values)
            if (insertUri != null) {
                val fos = resolver.openOutputStream(insertUri)
                if (fos != null) {
                    val fis = FileInputStream(oldFile)
                    fis.copyTo(fos)
                    fis.close()
                    fos.close()
                    toast("Download:$relativePath$targetDirName")
                    return insertUri
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    fun getMimeType(path: String?): String {
        var mime = "*/*"
        path ?: return mime
        val mmr = MediaMetadataRetriever()
        try {
            mmr.setDataSource(path)
            mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
                ?: mime
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mmr.release()
        }
        return mime
    }

    //Android 10以下版本
    fun fileSaveToPublic(oldFilePath: String, fileName: String) {
        val folder = if (fileName.endsWith(".mp4", true)
            || fileName.endsWith(".mp3", true)
            || fileName.endsWith(".wav", true)
        ) {

            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        }
        //判断目录是否存在
        val relativePath = folder.path + File.separator + getStringX(com.key.R.string.app_ai_name) + File.separator
        //目录不存在时自动创建
        val newFile = File(relativePath)
        if (!newFile.exists()) {
            newFile.mkdirs()
        }
        //创建文件
        val file = File(relativePath, fileName)
        val sourcePath = File(oldFilePath)
        FileInputStream(sourcePath).use { fis ->
            FileOutputStream(file).use { os ->
                val buffer = ByteArray(1024)
                var len: Int
                while (fis.read(buffer).also { len = it } != -1) {
                    os.write(buffer, 0, len)
                }
            }
        }
        MediaScannerConnection.scanFile(
            App.INSTANCE.applicationContext,
            arrayOf(file.toString()),
            null,
            null
        )
        toast("Download:${file.path}")
    }


    fun compressImage(image: Bitmap?): File {
        val outFile = File(Constants.savePath + Constants.updateImg)
        outFile.parentFile?.mkdirs()
        if (image?.isRecycled == true) {
            toast("The image has been recycled")
            return outFile
        }
        try {
            val baos = ByteArrayOutputStream()
            var options = 100
            image?.compress(Bitmap.CompressFormat.PNG, options, baos)
            //循环判断如果压缩后图片是否大于2M,大于继续加大压缩值
            while (baos.toByteArray().size / 1024 > 1024 * 2) {
                //每次都减少10
                options -= 20
                //重置baos即清空baos
                baos.reset()
                //这里压缩options%，把压缩后的数据存放到baos中,如果不压缩是100，表示压缩率为0
                image?.compress(Bitmap.CompressFormat.PNG, options, baos)
            }
            //把压缩后的数据baos存放到ByteArrayInputStream中
            val bitmap =
                BitmapFactory.decodeStream(ByteArrayInputStream(baos.toByteArray()), null, null)

            //创建上传文件
            val file = File(Constants.savePath + Constants.updateImg)
            val fos = FileOutputStream(file)
            //保存文件
            bitmap?.compress(Bitmap.CompressFormat.PNG, options, fos)
            fos.flush()
            fos.close()
            return file
        } catch (e: Exception) {
            //创建文件
            val file = File(Constants.savePath + Constants.updateImg)
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(file)
                // 将Bitmap压缩并写入文件
                image?.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
                return File(Constants.savePath, Constants.updateImg)
            } catch (e: IOException) {
                return file
            } finally {
                out?.close()
            }
        }
    }


    /**
     * 压缩图片 (支持 FilePath 或 Uri)
     * @param context 上下文
     * @param pathOrUri 可以是本地路径 String 或 Uri
     * @param targetSize 目标大小（字节），默认 2MB
     * @param maxWidth 最大宽度（像素）
     * @param maxHeight 最大高度（像素）
     * @param outFile 输出文件
     */
    fun compressImage2(
        pathOrUri: String,
        targetSize: Long = 2 * 1024 * 1024, // 默认 <2MB
        maxWidth: Int = 1080,
        maxHeight: Int = 1080): File {
        // 1. 打开输入流
        val inputStream = when {
            pathOrUri.startsWith("content://") || pathOrUri.startsWith("file://") -> {
                App.INSTANCE.applicationContext.contentResolver.openInputStream(pathOrUri.toUri())
            }
            else -> FileInputStream(pathOrUri)
        } ?: return File(Constants.savePath + Constants.updateImg)

        // 2. 仅获取尺寸，不加载到内存
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream.close()

        // 3. 计算缩放比例
        var inSampleSize = 1
        val max = 20 * 1024 * 1024
        while (options.outHeight * options.outWidth * 4 / inSampleSize > max) {
            inSampleSize *= 2
        }

//        if (options.outHeight > maxHeight || options.outWidth > maxWidth) {
//            val halfHeight = options.outHeight / 2
//            val halfWidth = options.outWidth / 2
//            while ((halfHeight / inSampleSize) >= maxHeight &&
//                (halfWidth / inSampleSize) >= maxWidth
//            ) {
//                inSampleSize *= 2
//            }
//        }

        // 4. 按比例解码
        val decodeOptions = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
        val inputStream2: InputStream? = when {
            pathOrUri.startsWith("content://") || pathOrUri.startsWith("file://") -> {
                App.INSTANCE.applicationContext.contentResolver.openInputStream(pathOrUri.toUri())
            }
            else -> FileInputStream(pathOrUri)
        }

        val bitmap = BitmapFactory.decodeStream(inputStream2, null, decodeOptions)
        inputStream2?.close()
        bitmap ?: return File(Constants.savePath + Constants.updateImg)

        // 5. 循环质量压缩
        var quality = 100
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        while (baos.size() > targetSize && quality > 10) {
            baos.reset()
            quality -= 10
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        }

        val outFile = File(Constants.savePath + Constants.updateImg)
        outFile.parentFile?.mkdirs()   // 修复 ENOENT
        // 6. 输出到文件
        FileOutputStream(outFile).use { fos ->
            fos.write(baos.toByteArray())
        }

        bitmap.recycle()
        return outFile
    }
}
