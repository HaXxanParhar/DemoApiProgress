package com.project.demoapiprogress.upload

import com.thrubal.app.models.auth.ResponseModel
import retrofit2.Response


interface UploadCallback {

    fun onUploadSuccess(action: String)
    fun onUploadFailed(action: String)

    fun onUploadUnsuccessful(response: Response<ResponseModel?>)

    fun onUploadFailure(t: Throwable)

}