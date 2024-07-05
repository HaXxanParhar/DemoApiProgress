package com.project.demoapiprogress.upload

import android.util.Log
import com.project.demoapiprogress.network.ApiClient
import com.thrubal.app.models.auth.ResponseModel

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

// This class extends the Progress Item and overrides it's method
class PostProgressItem(
    val userId: String,
    val mediaType: String,
    val caption: String,
    inputMedia: ArrayList<String?>,
    val callback: UploadCallback,
) : ProgressItem() {

    private var call: Call<ResponseModel?>? = null
    private var isCancelling = false
    val media: ArrayList<String?> = ArrayList()
    private val tag = "UPLOADING_TEST"

    init {
        if (inputMedia.isNotEmpty())
            media.addAll(inputMedia)
    }

    override fun startUploading() {
        Log.d(tag, "Started Uploading...")

        val parts: Array<MultipartBody.Part?> = Array(media.size) { null }
        for ((i, m) in media.withIndex()) {
            if (m.isNullOrEmpty()) continue
            val file = File(m)
            val requestBody =
                ProgressRequestBody(file)
            progressManager.addRequestBody(requestBody)
            val part = MultipartBody.Part.createFormData("media[]", file.name, requestBody)
            parts[i] = part
        }
        val rbUserId = RequestBody.create(MediaType.parse("text/plain"), userId)
        val rbType = RequestBody.create(MediaType.parse("text/plain"), mediaType)
        val rbCaption = RequestBody.create(MediaType.parse("text/plain"), caption)


        call = ApiClient.instance.createPost(rbUserId, rbType, rbCaption, parts)
        call!!.enqueue(object : Callback<ResponseModel?> {
            override fun onResponse(
                call: Call<ResponseModel?>,
                response: Response<ResponseModel?>
            ) {
                if (response.isSuccessful) {
                    Log.d(tag, "Successful Response")
                    val body = response.body() ?: return
                    if (body.status) {
                        Log.d(tag, "Success : ${body.action}")
                    } else {
                        Log.d(tag, "Failed : ${body.action}")
                        if (!isCancelling)
                            callback.onUploadFailed(body.action ?: "")
                    }
                } else {
                    Log.d(tag, "Unsuccessful response")
                    if (!isCancelling)
                        callback.onUploadUnsuccessful(response)
                }
                isCancelling = false
            }

            override fun onFailure(call: Call<ResponseModel?>, t: Throwable) {
                Log.d(tag, "on Failure : ${t.message}")
                if (!isCancelling)
                    callback.onUploadFailure(t)
                isCancelling = false
            }
        })
    }

    override fun cancelUploading() {
        isCancelling = true
        call?.cancel()
    }
}