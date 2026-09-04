package com.face.videoclips.trim

import com.face.videoclips.widget.ZVideoView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoProgressCoroutine(
    private val videoView: ZVideoView?,
    private val onProgress: (Int) -> Unit
) {
    private var job: Job? = null

    fun start() {
        if (job?.isActive == true) return

        job = GlobalScope.launch(Dispatchers.Default) {
            while (isActive && videoView?.isPlaying == true) {
                withContext(Dispatchers.Main) {
                    onProgress(videoView.currentPosition)
                }
                delay(400)
            }
        }
    }

    fun pause() {
        job?.cancel()
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
