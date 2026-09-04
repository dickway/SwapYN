package com.face.videoclips.trim

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.net.Uri
import android.os.Build
import com.apkfuns.logutils.LogUtils
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.face.key.Constants
import com.face.videoclips.interfaces.VideoTrimListener
import com.luck.picture.lib.config.PictureMimeType
import com.zzkj.structure.util.ktx.dp
import com.zzkj.structure.util.ktx.getScreenWidth
import com.zzkj.structure.util.ktx.launch
import iknow.android.utils.callback.SingleCallback
import iknow.android.utils.thread.BackgroundExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer


object VideoTrimmerUtil {


    const val MIN_SHOOT_DURATION = 3000L // 最小剪辑时间3s
    const val VIDEO_MAX_TIME = 20  // 20秒* 3 * 5
    const val MAX_SHOOT_DURATION = VIDEO_MAX_TIME * 1000L //视频最多剪切多长时间20s
    const val MAX_COUNT_RANGE = 9 //seekBar的区域内一共有多少张图片

    val RECYCLER_LONG_PADDING = 10.dp
    val RECYCLER_VIEW_PADDING = 0.dp
    val VIDEO_FRAMES_WIDTH = (getScreenWidth() - 62.dp)

    fun trim2(
        isFailure: Boolean,
        inputFile: String,
        startMs: Long,
        endMs: Long,
        callback: VideoTrimListener?
    ) {
        val dir = File(Constants.saveVideoPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        // 使用 File 类来拼接文件地址
        val transformVideo = File(dir.path, "trimmed_video.mp4")
        val start = startMs / 1000
        val duration = (endMs - startMs) / 1000
        val command = if (isFailure) {//失败了尝试转码剪辑
            "-y -ss ${start.toInt()} -t ${duration.toInt()} " +
                    "-i $inputFile -c:v libx264 -c:a aac ${transformVideo.path}"
        } else {
            "-y -ss ${start.toInt()} -t ${duration.toInt()} -accurate_seek " +
                    "-i $inputFile -codec copy -avoid_negative_ts 1 ${transformVideo.path}"
        }

//        val command =
//            "-y -ss $start -t $duration -i $inputFile -c:v libx264 -preset veryfast -c:a aac ${transformVideo.path}"

        FFmpegKit.executeAsync(command) { session ->
            if (ReturnCode.isSuccess(session.getReturnCode())) {
                // SUCCESS
                callback?.onFinishTrim(transformVideo.path, duration)
            } else if (ReturnCode.isCancel(session.getReturnCode())) {
                // CANCEL
            } else {
                // FAILURE
                // session.getState(),session.getReturnCode(),session.getFailStackTrace()
                if (isFailure) {
                    callback?.onError("Command failed : ${session.getReturnCode()}")
                } else {
                    trim2(
                        true,
                        inputFile,
                        startMs,
                        endMs,
                        callback
                    )
                }
            }
        }
    }


    fun trimVideo(
        context: Context,
        inputFile: Uri,
        startMs: Long,
        endMs: Long,
        callback: VideoTrimListener?
    ) {
        MainScope().launch(Dispatchers.IO) {
//            try {
                callback?.onStartTrim()
//                val retriever = MediaMetadataRetriever()
//                retriever.setDataSource(context, inputFile)
//                val rotation = retriever.extractMetadata(
//                    MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION
//                )?.toInt() ?: 0
//                retriever.release()
//
//                val dir = File(Constants.saveVideoPath)
//                if (!dir.exists()) {
//                    dir.mkdirs()
//                }
//                // 使用 File 类来拼接文件地址
//                val dstFile = File(dir.path, "trimmed_video.mp4")
//
//                // Create MediaMuxer to write the trimmed video
//                var videoTrackIndex = -1
//                var audioTrackIndex = -1
//
//                val extractor = MediaExtractor()
//                extractor.setDataSource(context, inputFile, null)
//
//                var videoFormat: MediaFormat? = null
//                var audioFormat: MediaFormat? = null
//                var videoMaxInputSize = 1024 * 1024
//                for (i in 0 until extractor.trackCount) {
//                    val format = extractor.getTrackFormat(i)
//                    val mime = format.getString(MediaFormat.KEY_MIME) ?: continue
//                    if (mime.startsWith("video/")) {
//                        videoTrackIndex = i
//                        videoFormat = format
//                        videoMaxInputSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE)
//                    } else if (mime.startsWith("audio/")) {
//                        audioTrackIndex = i
//                        audioFormat = format
//                    }
//                }
//                if (videoTrackIndex == -1) {
//                    throw RuntimeException("No video track found in $inputFile")
//                }
//
//                val muxer = MediaMuxer(dstFile.path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
//
//                extractor.selectTrack(videoTrackIndex)
//                val dstVideoTrackIndex = muxer.addTrack(videoFormat!!)
//                var dstAudioTrackIndex = -1
//                if (audioTrackIndex != -1) {
//                    dstAudioTrackIndex = muxer.addTrack(audioFormat!!)
//                }
//
//                // 保留视频方向信息
//                muxer.setOrientationHint(rotation)
//                muxer.start()
//
//                extractor.seekTo((startMs * 1000), MediaExtractor.SEEK_TO_CLOSEST_SYNC)
//
//                val bufferInfo = MediaCodec.BufferInfo()
//                val buffer = ByteBuffer.allocate(videoMaxInputSize)
//
//                while (true) {
//                    bufferInfo.offset = 0
//                    bufferInfo.size = extractor.readSampleData(buffer, 0)
//                    if (bufferInfo.size < 0) break
//                    bufferInfo.presentationTimeUs = extractor.sampleTime
//                    if (bufferInfo.presentationTimeUs > endMs * 1000) break
////                    bufferInfo.flags = extractor.sampleFlags.toInt()
//                    bufferInfo.flags =
//                        if (extractor.sampleFlags and MediaExtractor.SAMPLE_FLAG_SYNC != 0)
//                            MediaCodec.BUFFER_FLAG_KEY_FRAME
//                        else
//                            0
//                    muxer.writeSampleData(dstVideoTrackIndex, buffer, bufferInfo)
//                    extractor.advance()
//                }
//
//
//                // Write audio track if exists
//                if (audioTrackIndex != -1) {
//                    extractor.unselectTrack(videoTrackIndex)
//
//                    extractor.selectTrack(audioTrackIndex)
//                    extractor.seekTo((startMs * 1000).toLong(), MediaExtractor.SEEK_TO_CLOSEST_SYNC)
//                    while (true) {
//                        bufferInfo.offset = 0
//                        bufferInfo.size = extractor.readSampleData(buffer, 0)
//                        if (bufferInfo.size < 0) break
//                        val sampleTime = extractor.sampleTime
//                        if (sampleTime > endMs * 1000L) break
//                        bufferInfo.presentationTimeUs = sampleTime
////                        bufferInfo.flags = extractor.sampleFlags.toInt()
//                        bufferInfo.flags =
//                            if (extractor.sampleFlags and MediaExtractor.SAMPLE_FLAG_SYNC != 0)
//                                MediaCodec.BUFFER_FLAG_KEY_FRAME
//                            else
//                                0
//                        muxer.writeSampleData(dstAudioTrackIndex, buffer, bufferInfo)
//                        extractor.advance()
//                    }
//                }
//
//                muxer.stop()
//                muxer.release()
//                extractor.release()
//
//                val duration = (endMs - startMs) / 1000
//                callback?.onFinishTrim(dstFile.path, duration)
//            } catch (e: Exception) {
                trim2(
                    false,
                    inputFile.path ?: "",
                    startMs,
                    endMs,
                    callback
                )
//            }
        }

    }

    fun shootVideoLongBackground(
        context: Context?, videoUri: Uri?, totalThumbsCount: Float, startPosition: Long,
        endPosition: Long, callback: SingleCallback<Bitmap?, Float>
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            try {
                // 设置数据源
                if (PictureMimeType.isContent(videoUri?.path)) {
                    retriever.setDataSource(context, videoUri)
                } else {
                    retriever.setDataSource(videoUri?.path)
                }

                val interval =
                    ((endPosition - startPosition) / totalThumbsCount).toInt()
                var frameTime = startPosition
                var numss = 0
                while (frameTime < (endPosition - interval)) {
                    numss++
                    val bitmap = getFrameCompat(retriever, frameTime)
                    callback.onSingleCallback(bitmap, 0f)
                    frameTime += interval
                }
                val size = totalThumbsCount - kotlin.math.floor(totalThumbsCount)
                // 补最后一帧,为了动态计算宽度
                if (frameTime < endPosition && size > 0) {
                    val bitmap = getFrameCompat(retriever, (endPosition - interval / 2))
                    callback.onSingleCallback(bitmap, size)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                retriever.release()
            }
        }
    }


    fun shootVideoThumbInBackground(
        context: Context?, videoUri: Uri?, totalThumbsCount: Float, startPosition: Long,
        endPosition: Long, callback: SingleCallback<Bitmap?, Int>
    ) {
        val interval = ((endPosition - startPosition) / totalThumbsCount).toInt()
        GlobalScope.launch(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, videoUri)
            try {
                var frameTime = startPosition
                while (frameTime < (endPosition - interval + 1)) {
                    val bitmap = getFrameCompat(retriever, frameTime)
                    if (bitmap != null) {
                        callback.onSingleCallback(bitmap, frameTime.toInt())
                    }
                    frameTime += interval
                }
            } catch (_: Throwable) {
            } finally {
                retriever.release()
            }
        }
    }


    private fun getFrameCompat(
        retriever: MediaMetadataRetriever,
        timeMs: Long,
        width: Int = 40.dp,
        height: Int = 50.dp
    ): Bitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            retriever.getScaledFrameAtTime(
                timeMs * 1000,
                MediaMetadataRetriever.OPTION_PREVIOUS_SYNC,
                width,
                height
            )
        } else {
            retriever.getFrameAtTime(
                timeMs * 1000,
                MediaMetadataRetriever.OPTION_PREVIOUS_SYNC
            )
        }
    }


    /**
     * 查询视频宽高
     */
    fun getVideoResolutionFast(filePath: String): Pair<Int, Int>? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(filePath)

            var width = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
            )?.toIntOrNull() ?: 0

            var height = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
            )?.toIntOrNull() ?: 0

            val rotation = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION
            )?.toIntOrNull() ?: 0

            // 部分视频宽高可能为空或者为0，使用 MediaExtractor 再尝试
            if (width <= 0 || height <= 0) {
                return getVideoResolutionByExtractor(filePath)
            }

            // 处理手机竖屏视频旋转
            if (rotation == 90 || rotation == 270) {
                val temp = width
                width = height
                height = temp
            }

            Pair(width, height)

        } catch (e: Exception) {
            e.printStackTrace()
            getVideoResolutionByExtractor(filePath)
        } finally {
            try {
                retriever.release()
            } catch (_: Exception) {
            }
        }
    }


    /**
     * MediaExtractor 获取视频宽高
     */
    private fun getVideoResolutionByExtractor(filePath: String): Pair<Int, Int>? {
        val extractor = MediaExtractor()

        return try {
            extractor.setDataSource(filePath)

            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)

                if (mime?.startsWith("video/") == true) {

                    var width = format.getInteger(
                        MediaFormat.KEY_WIDTH
                    )

                    var height = format.getInteger(
                        MediaFormat.KEY_HEIGHT
                    )

                    if (format.containsKey(MediaFormat.KEY_ROTATION)) {
                        val rotation = format.getInteger(
                            MediaFormat.KEY_ROTATION
                        )

                        if (rotation == 90 || rotation == 270) {
                            val temp = width
                            width = height
                            height = temp
                        }
                    }

                    return if (width > 0 && height > 0) {
                        Pair(width, height)
                    } else {
                        null
                    }
                }
            }

            null

        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            extractor.release()
        }
    }

    /**
     * 查询视频编码格式
     **/
    @JvmStatic
    fun getVideoCodec(filePath: String?): String? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(filePath)
            val codec = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
            codec
        } catch (e: Exception) {
            ""
        } finally {
            retriever.release()
        }
    }
}
