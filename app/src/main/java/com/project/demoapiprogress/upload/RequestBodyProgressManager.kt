package com.project.demoapiprogress.upload

import android.util.Log
import com.project.demoapiprogress.upload.ProgressRequestBody.UploadCallbacks
import com.project.demoapiprogress.utils.C

// This class will hold the multiple progress request bodies and provide the combined progress
// of them all.
class RequestBodyProgressManager() {
    var list: ArrayList<ProgressRequestBody> = ArrayList()
    private var totalLength: Long = 0
    private var uploaded: Long = 0
    private var itemId: Long = 0
    private var oldPercentage: Int = 0
    private lateinit var callback: Callback

    fun addCallback(itemId: Long, callback: Callback) {
        Log.d(C.UPLOADING_TEST, "Added Callback inside Manager")
        this.itemId = itemId
        this.callback = callback

    }

    fun addRequestBody(body: ProgressRequestBody) {
        try {
            list.add(body)
            totalLength += body.contentLength()
            Log.d(C.UPLOADING_TEST, "Added body request")
            Log.d(C.UPLOADING_TEST, "Total Length : $totalLength")
            body.addListener(object : UploadCallbacks {
                override fun onProgressUpdate(uploadedBytes: Long) {
                    Log.d(C.UPLOADING_TEST, "on Progress Update inside Manager")
                    uploaded = uploaded + uploadedBytes
                    val percentage = (uploaded.toDouble() / totalLength * 100).toInt()

                    // only update for new percentage
                    if (percentage != oldPercentage) {
                        oldPercentage = percentage
                        callback.onProgressUpdate(itemId, percentage)
                    }

                    // finish when all bytes ar euploaded
                    if (uploaded >= totalLength) callback.onFinish(itemId)
                }

                override fun onException(e: Exception?) {
                    callback.onError(itemId, e!!.message)
                }
            })
        } catch (e: Exception) {
            callback.onError(itemId, e.message)
        }
    }

    fun resetProgress() {
        uploaded = 0
    }

    interface Callback {
        fun onProgressUpdate(id: Long, percentage: Int)
        fun onError(id: Long, message: String?)
        fun onFinish(id: Long)
    }
}
