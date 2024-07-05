package com.project.demoapiprogress.upload

import android.util.Log
import com.project.demoapiprogress.utils.C

// This singleton class will hold the list of all the progress items.
// Remember that a Progress Item can have multiple images/videos
// When an item is added, it will immediately start uploading all the media (images & videos) inside
// each progress item and the combined progress will be provided
// to the callback
object UploadQueue : RequestBodyProgressManager.Callback {

    private var list: ArrayList<ProgressItem> = ArrayList()
    private var callback: RequestBodyProgressManager.Callback? = null
    private var cancelling = false

    // Add a Progress item
    fun addItem(item: ProgressItem) {
        Log.d(C.UPLOADING_TEST, "Add Item in Upload Queue")
        list.add(item)
        item.addCallback(this)
        item.startUploading()
    }

    // Get the List of all the items, useful if you want to show them progress items in the UI
    fun getList(): ArrayList<ProgressItem> {
        Log.d(C.UPLOADING_TEST, "Get List of Size : ${list.size}")
        return list
    }

    // to add the callback
    fun addCallback(callback: RequestBodyProgressManager.Callback) {
        Log.d(C.UPLOADING_TEST, "Add Callback inside Upload Queue")
        UploadQueue.callback = callback
    }

    // This will be called when progress is updated
    override fun onProgressUpdate(id: Long, percentage: Int) {
        Log.d(C.UPLOADING_TEST, "On progress update in Queue --> $id : $percentage")
        val index = findItem(id)
        if (index == -1 || list.isEmpty() || index >= list.size) return

        Log.d(C.UPLOADING_TEST, "Update item at $index")
        list[index].progress = percentage
        list[index].error = ""
        if (callback != null)
            callback!!.onProgressUpdate(index.toLong(), percentage)
    }

    // This will be called when api faces some error
    override fun onError(id: Long, message: String?) {
        Log.d(C.UPLOADING_TEST, "Error in Queue --> $id : $message")

        val index = findItem(id)
        if (index == -1 || list.isEmpty() || index >= list.size) return
        list[index].error = message ?: "Something went wrong!"

        if (callback != null && !cancelling)
            callback!!.onError(index.toLong(), message)
        cancelling = false
    }

    // This will be called when all the media inside a progress item is uploaed
    override fun onFinish(id: Long) {
        Log.d(C.UPLOADING_TEST, "on Finish in Queue --> $id")
        val index = findItem(id)
        if (index == -1 || list.isEmpty() || index >= list.size) return
        if (callback != null)
            callback!!.onFinish(index.toLong())
    }

    // If an upload is failed/cancelled, call this method to retry the upload process
    fun retry(position: Int, model: ProgressItem) {
        model.progress = 0
        model.progressManager.resetProgress()
        model.startUploading()
    }

    // to cancel the upload process
    fun cancelUpload(position: Int, model: ProgressItem) {
        cancelling = true
        model.cancelUploading()
        callback?.onFinish(position.toLong())
    }

    // private method to find them progress item from the list
    private fun findItem(id: Long): Int {
        val index = -1
        for ((i, p) in list.withIndex()) {
            if (p.itemId == id) return i
        }
        return index
    }

}