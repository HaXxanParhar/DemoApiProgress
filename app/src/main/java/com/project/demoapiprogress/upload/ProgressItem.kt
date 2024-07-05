package com.project.demoapiprogress.upload

import android.util.Log

// This is abstract class for any API where you want to get the progress during the API call
// extend and implement this class and call the API inside the extended class
// See 'PostProgressItem' for reference which a ProgressItem for the Post API
// You can such extended classes for others APIs too i.e. StoryProgressItem
// where the Upload Story API could be used.
abstract class ProgressItem() {

    var progressManager: RequestBodyProgressManager = RequestBodyProgressManager()
    var progress: Int = 0
    var itemId: Long = System.currentTimeMillis()
    var error: String = ""

    fun addCallback(callback: RequestBodyProgressManager.Callback) {
        Log.d("UPLOADING_TEST", "Added Callback inside Progress Item")
        progressManager.addCallback(itemId, callback)
    }

    abstract fun startUploading()
    abstract fun cancelUploading()
}