package com.project.demoapiprogress.upload

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException

// This class extends the Request Body and provides the progress of a Single Request Body
// We're getting the progress by overriding the 'writeTo' method and
// calculating how much data is written/sent/uploaded from the total file size.
class ProgressRequestBody(var file: File) : RequestBody() {
    var listener: UploadCallbacks? = null
    var content_type: String? = null

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 4096
    }


    fun addListener(listener: UploadCallbacks?) {
        this.listener = listener
    }

    override fun contentType(): MediaType? {
        return MediaType.parse("$content_type/*")
    }

    override fun contentLength(): Long {
        return file.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        try {
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            FileInputStream(file).use { `in` ->
                var read: Int
                val handler = Handler(Looper.getMainLooper())
                while (`in`.read(buffer).also { read = it } != -1) {
                    // provide the updated Progress to ProgressUpdater class
                    handler.post(ProgressUpdater(read.toLong()))
                    sink.write(buffer, 0, read)
                }
            }
        } catch (e: Exception) {
            if (listener != null) listener!!.onException(e)
        }
    }

    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Long)
        fun onException(e: Exception?)
    }

    // Created this class to call the listener!!.onProgressUpdate(readBytes)
    // inside the Handler().post method. We can use the Anonymous class but
    // it will not accept any arguments, that's why created this inner class
    private inner class ProgressUpdater(var readBytes: Long) : Runnable {
        override fun run() {
            if (listener != null) listener!!.onProgressUpdate(readBytes)
        }
    }
}