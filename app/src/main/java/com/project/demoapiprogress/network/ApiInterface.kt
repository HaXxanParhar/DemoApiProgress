package com.project.demoapiprogress.network

import com.thrubal.app.models.auth.ResponseModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {

//    // -------------------------------------- P  O  S  T  S ----------------------------------------

    @Multipart
    @POST("create/post")
    fun createPost(
        @Part("user_id") id: RequestBody?,
        @Part("media_type") media_type: RequestBody?,// image,video
        @Part("caption") caption: RequestBody?,//
        @Part media: Array<MultipartBody.Part?>
    ): Call<ResponseModel?>

}